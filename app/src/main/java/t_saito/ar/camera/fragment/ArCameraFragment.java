package t_saito.ar.camera.fragment;


import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.google.ar.core.Anchor;
import com.google.ar.core.Camera;
import com.google.ar.core.Frame;
import com.google.ar.core.Pose;
import com.google.ar.core.Session;
import com.google.ar.core.TrackingState;
import com.google.ar.core.exceptions.CameraNotAvailableException;
import com.google.ar.core.exceptions.UnavailableApkTooOldException;
import com.google.ar.core.exceptions.UnavailableArcoreNotInstalledException;
import com.google.ar.core.exceptions.UnavailableSdkTooOldException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import t_saito.ar.camera.ArConstant;
import t_saito.ar.camera.CommonConstant;
import t_saito.ar.camera.MyApplication;
import t_saito.ar.camera.databinding.FragmentArCameraBinding;
import t_saito.ar.camera.model.ObjectTransform;
import t_saito.ar.camera.util.GlideUtil;
import t_saito.ar.camera.R;
import t_saito.ar.camera.activity.BaseActivity;
import t_saito.ar.camera.dao.ImageDao;
import t_saito.ar.camera.dialogs.BaseDialogFragment;
import t_saito.ar.camera.dialogs.CommonDialogFragment;
import t_saito.ar.camera.dialogs.DialogConst;
import t_saito.ar.camera.helpers.CameraPermissionHelper;
import t_saito.ar.camera.helpers.DisplayRotationHelper;
import t_saito.ar.camera.helpers.SurfaceTapHelper;
import t_saito.ar.camera.model.Image;
import t_saito.ar.camera.model.MatrixData;
import t_saito.ar.camera.model.ObjectData;
import t_saito.ar.camera.rendering.BackgroundRenderer;
import t_saito.ar.camera.rendering.ObjectRenderer;
import t_saito.ar.camera.ui.ItemOffsetDecoration;
import t_saito.ar.camera.ui.adapter.ObjectGridAdapter;
import t_saito.ar.camera.viewmodel.ArCameraViewModel;
import timber.log.Timber;

import static t_saito.ar.camera.ArConstant.ARCoreStatus;

public class ArCameraFragment extends BaseFragment {

    /** ARカメラ関連 */
    private FragmentArCameraBinding binding;
    // Rendering. The Renderers are created here, and initialized when the GL surface is created.
    private DisplayRotationHelper displayRotationHelper;
    private SurfaceTapHelper surfaceTapHelper;
    private Session session;
    private Display display;
    private boolean isObjectVisible = false;
    private ObjectRenderer virtualObject = new ObjectRenderer();
    private BackgroundRenderer backgroundRenderer = new BackgroundRenderer();
    // Temporary matrix allocated here to reduce number of allocations for each frame.
    private final float[] anchorMatrix = new float[16];
    private final ArrayList<Anchor> anchors = new ArrayList<>();
    private final int MAX_OBJ_CNT = 1;
    private MatrixData modelMatrixData = new MatrixData();
    private MatrixData viewMatrixData = new MatrixData();
    private MatrixData projMatrixData = new MatrixData();
    private ObjectTransform objectTransform = new ObjectTransform();

    // obj replace
    private boolean isObjReplaced = false;
    private ObjectData currentObjectData;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    // session supported
    private ARCoreStatus arCoreStatus = ARCoreStatus.SUPPORTED_NOT_INSTALLED;

    /** サウンド関連 */
    private AudioManager am;
    private SoundPool soundPool;
    private int soundShutter;

    private AnimatorSet animatorSet = new AnimatorSet();
    private boolean pinned = false;
    public ArCameraFragment() {

    }

    public static ArCameraFragment newInstance() {
        Bundle args = new Bundle();
        ArCameraFragment fragment = new ArCameraFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_ar_camera, container, false);
        binding.setViewModel(new ArCameraViewModel());
        initView(binding.getRoot());
        createSoundPool();
        getMyApplication();
        Disposable deleteImageDisposable = ((MyApplication) getContext()).getImageRxBus().toObservable().subscribe(imageSet -> loadRecentImage());
        compositeDisposable.add(deleteImageDisposable);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        display = getActivity().getSystemService(WindowManager.class).getDefaultDisplay();

        try {
            if (session == null) {
                session = new Session(getContext());
            }
            arCoreStatus = ARCoreStatus.SUPPORTED_INSTALLED;
        } catch (UnavailableArcoreNotInstalledException e) {
            arCoreStatus = ARCoreStatus.SUPPORTED_NOT_INSTALLED;
            Timber.e(e);
        } catch (UnavailableApkTooOldException e) {
            arCoreStatus = ARCoreStatus.SUPPORTED_APK_TOO_OLD;
            Timber.e(e);
        } catch (UnavailableSdkTooOldException e) {
            arCoreStatus = ARCoreStatus.SUPPORTED_SDK_TOO_OLD;
            Timber.e(e);
        } catch (Exception e) {
            arCoreStatus = ARCoreStatus.UNKNOWN_ERROR;
            Timber.e(e);
        }
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!checkARCore(arCoreStatus)) return;
        resumeARCamera();
    }

    /**
     * ARカメラの処理を開始
     */
    private void resumeARCamera() {
        // ARCore requires camera permissions to operate. If we did not yet obtain runtime
        // permission on Android M and above, now is a good time to ask the user for it.
        if (CameraPermissionHelper.hasCameraPermission(getActivity())) {

            // Note that order matters - see the note in onPause(), the reverse applies here.
            try {
                session.resume();
                displayRotationHelper.onResume();
            } catch (CameraNotAvailableException e) {
                e.printStackTrace();
            }
            if (binding.surfaceView != null) binding.surfaceView.onResume();
        } else {
            CameraPermissionHelper.requestCameraPermission(getActivity());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (arCoreStatus != ARCoreStatus.SUPPORTED_INSTALLED) return;
        pauseARCamera();
    }

    /**
     * カメラの処理を停止
     */
    private void pauseARCamera() {
        // Note that the order matters - GLSurfaceView is paused first so that it does not try
        // to query the session. If Session is paused before GLSurfaceView, GLSurfaceView may
        // still call session.update() and get a SessionPausedException.
        displayRotationHelper.onPause();
        if (binding.surfaceView != null) binding.surfaceView.onPause();
        if (session != null) session.pause();
    }

    @Override
    public void onDestroyView() {
        releaseSoundPool();
        if (compositeDisposable != null && !compositeDisposable.isDisposed()) compositeDisposable.dispose();
        session = null;
        super.onDestroyView();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] results) {
        if (!CameraPermissionHelper.hasCameraPermission(getActivity())) {
            Toast.makeText(getActivity(),
                    "Camera permission is needed to run this application", Toast.LENGTH_LONG).show();
            finishApplication();
        }
    }

    /**
     * View初期処理
     *
     * @param root
     */
    private void initView(View root) {
        displayRotationHelper = new DisplayRotationHelper(/*context=*/ getContext());

        surfaceTapHelper = new SurfaceTapHelper(getContext());

        // Set up tap listener.
        binding.surfaceView.setOnTouchListener(surfaceTapHelper);

        // Set up renderer.
        binding.surfaceView.setPreserveEGLContextOnPause(true);
        binding.surfaceView.setEGLContextClientVersion(2);
        binding.surfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0); // Alpha used for plane blending.
        binding.surfaceView.setRenderer(binding.getViewModel());
        binding.surfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

        compositeDisposable.add(binding.getViewModel().surfaceCreatedObservable.subscribe(eglConfig -> {
            if (arCoreStatus != ARCoreStatus.SUPPORTED_INSTALLED) return;
            initObjectRender();
        }));
        compositeDisposable.add(binding.getViewModel().surfaceChangedObservable.subscribe(screenSize -> surfaceChanged(screenSize.getWidth(), screenSize.getHeight())));
        compositeDisposable.add(binding.getViewModel().drawFrameObservable.subscribe(this::drawFrame));
        compositeDisposable.add(binding.getViewModel().captureObservable
//                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(bitmap -> Image.save(getContext(), bitmap))
                .subscribe(image -> {
                    animatorSet.start();
                    playSoundPool();
                    if (image == null) {
                        Timber.w("Failed to save image");
                        return;
                    }
                    if (binding.buttonPicture.getVisibility() == View.GONE) binding.buttonPicture.setVisibility(View.VISIBLE);
                    GlideUtil.loadCircleImageButton(getContext(), binding.buttonPicture, image.filePath);
                    // イベント送信
//                    Shutter shutter = new Shutter.Builder(currentObjectData.getObjectName()).build();
//                    AnalyticsHelper.send(getContext(), shutter);
//                    AnswersEventClient.send(new ShutterAnswers.Builder(currentObjectData.getObjectName()).build());
                    ((BaseActivity) getActivity()).toPreview();
                }, Timber::e));
        compositeDisposable.add(binding.getViewModel().pinObservable.subscribe(pinned -> {
            this.pinned = pinned;
            if (pinned) {
                objectTransform.setAngleTmpZ(viewMatrixData.getAngleZ());
            } else {
                objectTransform.setLockX(binding.surfaceView.getWidth() / 2);
                objectTransform.setLockY(binding.surfaceView.getHeight() / 2);
                objectTransform.setAngleTmpZ(viewMatrixData.getAngleZ());
                resetObject();
            }
        }));
        compositeDisposable.add(binding.getViewModel().flashLightObservable.subscribe(this::flashLight));
        compositeDisposable.add(binding.getViewModel().otherObservable.subscribe(resId -> {
            switch (resId) {
                case R.id.button_settings:
                    ((BaseActivity) getActivity()).toSettings();
                    break;
                case R.id.button_picture:
                    ((BaseActivity) getActivity()).toPictures();
                    break;
                case R.id.button_object:
                    showObjects();
                    break;
                case R.id.layout_object:
                    hideObjects();
                    break;
                case R.id.button_reset:
                    objectTransform.setLockX(binding.surfaceView.getWidth() / 2);
                    objectTransform.setLockY(binding.surfaceView.getHeight() / 2);
                    resetObject();
                    break;
            }
        }));

        View viewWhiteOut = root.findViewById(R.id.view_white_out);
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(viewWhiteOut, "alpha", 0f, 0.5f);
        fadeIn.setDuration(150);
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(viewWhiteOut, "alpha", 0.5f, 0f);
        fadeOut.setDuration(150);
        animatorSet.playSequentially(fadeIn, fadeOut);

        loadRecentImage();

        List<ObjectData> objectDataList = createData();
        currentObjectData = objectDataList.get(0);
        ObjectGridAdapter objectGridAdapter = new ObjectGridAdapter(getContext(), objectDataList);
        compositeDisposable.add(objectGridAdapter.objectObservable.subscribe(objectData -> {
            // オブジェクトを入れ替える
            hideObjects();
            Timber.d("change obj name %s", objectData.getObjectName());
            currentObjectData = objectData;
            isObjReplaced = true;
            isObjectVisible = true;
        }));

        final int spacing = getResources().getDimensionPixelOffset(R.dimen.default_spacing_none);
        RecyclerView recyclerView = root.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), CommonConstant.GRID_COLUMN_COUNT_OBJECT));
        recyclerView.addItemDecoration(new ItemOffsetDecoration(spacing)); // スペース用の独自ItemDecoration
        recyclerView.setAdapter(objectGridAdapter);
    }

    /**
     * オブジェクトグリッドを非表示
     */
    private void hideObjects() {
        if (binding.layoutObject.getVisibility() != View.VISIBLE) {
            return;
        }
        binding.layoutObject.setVisibility(View.GONE);
        binding.layoutBottom.setVisibility(View.VISIBLE);
        binding.layoutObject.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.layout_object_slide_down));
        binding.layoutBottom.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.layout_bottom_feed_in));
    }

    /**
     * オブジェクトグリッドを表示
     */
    private void showObjects() {
        if (binding.layoutObject.getVisibility() == View.VISIBLE) {
            return;
        }
        binding.layoutObject.setVisibility(View.VISIBLE);
        binding.layoutBottom.setVisibility(View.GONE);
        binding.layoutObject.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.layout_object_slide_up));
        binding.layoutBottom.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.layout_bottom_feed_out));
    }

    /**
     * オブジェクトデータリストを生成
     *
     * @return
     */
    private List<ObjectData> createData() {
        List<ObjectData> objectDataList = new ArrayList<>();
        objectDataList.add(new ObjectData(ArConstant.Object.ASSET_LAB.getObjectName(), ArConstant.Object.ASSET_LAB.getTextureName(), R.drawable.labo));
        objectDataList.add(new ObjectData(ArConstant.Object.ASSET_CUBE.getObjectName(), ArConstant.Object.ASSET_CUBE.getTextureName(), R.drawable.cube));
        return objectDataList;
    }

    /**
     * 最新の画像をviewに反映
     */
    private void loadRecentImage() {
        @Nullable Single<Image> single = ImageDao.newInstance(getContext()).recentGet();
        if (single == null) {
            binding.buttonPicture.setVisibility(View.GONE);
            return;
        }
        @NonNull Image recentImage = single.blockingGet();
        if (!TextUtils.isEmpty(recentImage.filePath)) {
            binding.buttonPicture.setVisibility(View.VISIBLE);
            GlideUtil.loadCircleImageButton(getContext(), binding.buttonPicture, recentImage.filePath);
        }
    }

    private void flashLight(boolean flashLightOn) {
        CameraManager cameraManager = (CameraManager) getActivity().getSystemService(Context.CAMERA_SERVICE);
        try {
            String cameraId = cameraManager.getCameraIdList()[0];
            cameraManager.setTorchMode(cameraId, flashLightOn);
        } catch (CameraAccessException e) {
            Timber.e(e);
        }
    }

    /**
     * レンダー初期処理
     */
    private void initObjectRender() {
        GLES20.glClearColor(0.1f, 0.1f, 0.1f, 1.0f);

        // Create the texture and pass it to ARCore session to be filled during update().
        backgroundRenderer.createOnGlThread(/*context=*/getContext());
        session.setCameraTextureName(backgroundRenderer.getTextureId());

        // Prepare the other rendering objects.
        try {
            virtualObject.createOnGlThread(/*context=*/getContext(), currentObjectData.getObjectName(), currentObjectData.getTextureName());
            virtualObject.setMaterialProperties(ArConstant.MODEL_SURFACE_AMBIENT, ArConstant.MODEL_SURFACE_DIFFUSE, ArConstant.MODEL_SURFACE_SPECULAR, ArConstant.MODEL_SURFACE_SPECULAR_POWER);
            virtualObject.updateModelMatrix(modelMatrixData.getMatrix(), 0.0f, 0.0f, 0.0f, 0.0f);

        } catch (IOException e) {
            Timber.e("Failed to read obj file");
        }
    }

    public void surfaceChanged(int width, int height) {
        if (arCoreStatus != ARCoreStatus.SUPPORTED_INSTALLED) return;
        displayRotationHelper.onSurfaceChanged(width, height);
        GLES20.glViewport(0, 0, width, height);
        // Notify ARCore session that the view size changed so that the perspective matrix and
        // the video background can be properly adjusted.
        int displayRotation = display.getRotation();
        session.setDisplayGeometry(displayRotation, width, height);
    }

    public void drawFrame(GL10 gl) {
        if (arCoreStatus != ARCoreStatus.SUPPORTED_INSTALLED) return;
        if (session == null) return;
        rendering();
    }

    /**
     * SurfaceViewレンダリング処理
     */
    private void rendering() {
        // Clear screen to notify driver it should not load any pixels from previous frame.
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        // Notify ARCore session that the view size changed so that the perspective matrix and
        // the video background can be properly adjusted.
        // ビューのサイズが変更されたことをARCoreセッションに通知して、パースペクティブマトリックスとビデオの背景を適切に調整することができます。
        displayRotationHelper.updateSessionIfNeeded(session);

        try {
            // Obtain the current frame from ARSession. When the configuration is set to
            // UpdateMode.BLOCKING (it is by default), this will throttle the rendering to the
            // camera framerate.
            Frame frame = session.update();
            Camera camera = frame.getCamera();

            // Draw background.
            backgroundRenderer.draw(frame);
            // If not tracking, don't draw 3d objects.
            if (camera.getTrackingState() != TrackingState.TRACKING){
                Timber.w("don't tracking camera");
                return;
            }
            // Get projection matrix.
            camera.getProjectionMatrix(projMatrixData.getMatrix(), 0, 0.1f, 100.0f);
            // Get camera matrix and draw.
            camera.getViewMatrix(viewMatrixData.getMatrix(), 0);

            // Handle taps. Handling only one tap per frame, as taps are usually low frequency
            // compared to frame rate.
            MotionEvent tap = surfaceTapHelper.poll();

            if(tap != null){
                isObjectVisible = true;
                objectTransform.setLockX(tap.getX());
                objectTransform.setLockY(tap.getY());
                createAnchor(camera, tap.getX(), tap.getY(), ArConstant.DEF_TRANSITION_Z);
            } else if (!pinned) {
                createAnchor(camera, objectTransform.getLockX(), objectTransform.getLockY(), ArConstant.DEF_TRANSITION_Z);
            }

            // オブジェクトが切り替わったかチェック
            if (isObjReplaced) {
                isObjReplaced = false;
                try {
                    // オブジェクト入れ替え
                    virtualObject.createOnGlThread(getContext(), currentObjectData.getObjectName(), currentObjectData.getTextureName());
                    virtualObject.setMaterialProperties(ArConstant.MODEL_SURFACE_AMBIENT, ArConstant.MODEL_SURFACE_DIFFUSE, ArConstant.MODEL_SURFACE_SPECULAR, ArConstant.MODEL_SURFACE_SPECULAR_POWER);
                    // 角度をリセット
                    resetObject();
                    if (pinned) {
                        objectTransform.setAngleTmpZ(viewMatrixData.getAngleZ());
                    }
                } catch (IOException e) {
                    Timber.e(e);
                    return;
                }
            }

            if (pinned) {
                // 配置
                objectTransform.setAngleZ(objectTransform.getAngleTmpZ() + surfaceTapHelper.getRotateFactor());
            } else {
                // 画面固定
                lockObject();
            }

            // Compute lighting from average intensity of the image.
            final float lightIntensity = frame.getLightEstimate().getPixelIntensity() + ArConstant.DEF_LIGHT_INTENSITY;

            // Visualize anchors created by touch.
            // 作成したAnchorに描画する
            session.getAllAnchors().forEach(anchor -> {
                // Anchorの座標を生成する
                anchor.getPose().toMatrix(modelMatrixData.getMatrix(), 0);
                // Update and draw the model and its shadow.
                // 設定したデフォルトのスケールサイズを足す
                float scale = currentObjectData.getDefScale() + surfaceTapHelper.getScaleFactor();
                // 座標に描画する
                virtualObject.updateModelMatrix(modelMatrixData.getMatrix(), scale, objectTransform.getAngleX(), objectTransform.getAngleY(), pinned ? objectTransform.getAngleZ() : objectTransform.getAngleLockZ());
                if (isObjectVisible) virtualObject.draw(viewMatrixData, projMatrixData, lightIntensity);
            });
        } catch (Throwable t) {
            // Avoid crashing the application due to unhandled exceptions.
            Timber.e(t, "Exception on the OpenGL thread");
        }
    }

    /**
     * アンカー作成
     *
     * @param camera
     * @param x
     * @param y
     * @param z
     */
    private void createAnchor(final Camera camera, float x, float y, float z) {
        float[] touchArray = new float[]{x, y};
        float[] normalizedMetersArr = getNormalizedScreenCoordinates(touchArray, binding.surfaceView.getWidth(), binding.surfaceView.getHeight(), z);

        // 最初に配置したオブジェクトを削除
        if (session.getAllAnchors().size() >= MAX_OBJ_CNT) {
            anchors.get(0).detach();
            anchors.remove(0);
        }
        // 移動
        Pose translation =  Pose.makeTranslation(normalizedMetersArr[0], normalizedMetersArr[1], z);
        Pose extractTranslation = camera.getPose().compose(translation).extractTranslation();
        Anchor anchor = session.createAnchor(extractTranslation);
        anchors.add(anchor);
        Timber.d("extractTranslation X:%f Y:%f Z:%f:", extractTranslation.tx(), extractTranslation.ty(), extractTranslation.tz());
    }

    /**
     * オブジェクトの位置とスケールをリセット
     */
    private void resetObject() {
        objectTransform.setAngleX(viewMatrixData.getAngleX());
        objectTransform.setAngleY(viewMatrixData.getAngleY());
        surfaceTapHelper.resetScaleFactor();
        surfaceTapHelper.resetRotateFactor();
        objectTransform.setAngleZ(viewMatrixData.getAngleZ());
        objectTransform.setAngleLockZ(viewMatrixData.getAngleZ());
    }

    private void lockObject() {
        objectTransform.setAngleX(viewMatrixData.getAngleX());
        objectTransform.setAngleY(viewMatrixData.getAngleY());
        objectTransform.setAngleLockZ(surfaceTapHelper.getRotateFactor() + viewMatrixData.getAngleZ());
    }

    private float [] getNormalizedScreenCoordinates(float[] vec2, int screenWidth, int screenHeight, float metersAway){
        if(vec2 == null || vec2.length != 2) return null;

        float [] normalizedTouch = new float[]{vec2[0]/screenWidth, vec2[1]/screenHeight};
        float screenWidthInTranslationMeters = 0.37f; //range is [-0.37,0.37]
        float screenHeightInTranslationMeters = 0.675f; //range is [-0.615, 0.615]

        float normalizedAwayFromCenterX;
        float normalizedMetersAwayFromCenterX;
        if(normalizedTouch[0] >= 0.5f){
            normalizedAwayFromCenterX = (float) ((1.0 - 0.0) / (1.0 - 0.5f) * (normalizedTouch[0] - 1.0f) + 1.0f);
        }else{
            normalizedAwayFromCenterX = (float) ((0.0 + 1.0) / (0.5f - 0f) * (normalizedTouch[0] - 1.0f) + 1.0f);
        }
        normalizedMetersAwayFromCenterX = normalizedAwayFromCenterX * screenWidthInTranslationMeters * Math.abs(metersAway);


        float normalizedAwayFromCenterY ;
        float normalizedMetersAwayFromCenterY;
        if((1-normalizedTouch[1]) < 0.5f){
            normalizedAwayFromCenterY = (float) ((1.0 - 0.0) / (1.0 - 0.5f) * ((1 - normalizedTouch[1]) - 1.0f) + 1.0f);
        }else{
            normalizedAwayFromCenterY = (float) ((0.0 + 1.0) / (0.5f - 0f) * ((1 - normalizedTouch[1]) - 1.0f) + 1.0f);
        }
        normalizedMetersAwayFromCenterY = normalizedAwayFromCenterY * screenHeightInTranslationMeters * Math.abs(metersAway);

        return new float[]{normalizedMetersAwayFromCenterX, normalizedMetersAwayFromCenterY};
    }

    /**
     * ARCoreが対応しているかチェック
     *
     * @param arCoreStatus
     */
    private boolean checkARCore(ArConstant.ARCoreStatus arCoreStatus) {
        boolean isSupported = false;
        switch (arCoreStatus) {
            case SUPPORTED_INSTALLED:
                isSupported = true;
                break;
            case SUPPORTED_NOT_INSTALLED:
                showDialogARCoreNotInstalled(this);
                break;
            case SUPPORTED_APK_TOO_OLD:
                showDialogARCoreOldApk(this);
                break;
            case SUPPORTED_SDK_TOO_OLD:
                showDialogARCoreOldSdk(this);
                break;
            case UNKNOWN_ERROR:
            default:
                showARCoreIncompatibleDialog(this);
                break;
        }
        return isSupported;
    }

    /**
     * ARCore非対応ダイアログ表示 (SDKバージョンが古い)
     *
     * @param dialogResultListener
     */
    private void showDialogARCoreOldSdk(BaseDialogFragment.OnDialogResultListener dialogResultListener) {
        CommonDialogFragment dialog = CommonDialogFragment.getInstance(
                null,
                getString(R.string.dialog_title_arcore_sdk_old),
                getString(R.string.dialog_message_arcore_sdk_old),
                new String[] {
                        getString(R.string.dialog_label_yes)
                });
        dialog.setOnDialogResultListener(dialogResultListener, DialogConst.DIALOG_ARCORE_SDK_OLD);
        showDialog(dialog);
    }

    /**
     * ARCore非対応ダイアログ表示 (ARCoreバージョンが古い)
     *
     * @param dialogResultListener
     */
    private void showDialogARCoreOldApk(BaseDialogFragment.OnDialogResultListener dialogResultListener) {
        CommonDialogFragment dialog = CommonDialogFragment.getInstance(
                null,
                getString(R.string.dialog_title_arcore_apk_old),
                getString(R.string.dialog_message_arcore_apk_old),
                new String[] {
                        getString(R.string.dialog_label_yes),
                        getString(R.string.dialog_label_no),
                });
        dialog.setOnDialogResultListener(dialogResultListener, DialogConst.DIALOG_ARCORE_APK_OLD);
        showDialog(dialog);
    }

    /**
     * ARCore非対応ダイアログ表示 (非対応端末)
     *
     * @param dialogResultListener
     */
    private void showARCoreIncompatibleDialog(BaseDialogFragment.OnDialogResultListener dialogResultListener) {
        CommonDialogFragment dialog = CommonDialogFragment.getInstance(
                null,
                getString(R.string.dialog_title_arcore_incompatible),
                getString(R.string.dialog_message_arcore_incompatible),
                new String[] {
                        getString(R.string.dialog_label_yes)
                });
        dialog.setOnDialogResultListener(dialogResultListener, DialogConst.DIALOG_ARCORE_INCOMPATIBLE);
        showDialog(dialog);
    }

    /**
     * ARCoreインストールダイアログ表示
     *
     * @param dialogResultListener
     */
    private void showDialogARCoreNotInstalled(BaseDialogFragment.OnDialogResultListener dialogResultListener) {
        CommonDialogFragment dialog = CommonDialogFragment.getInstance(
                null,
                getString(R.string.dialog_title_arcore_not_installed),
                getString(R.string.dialog_message_arcore_not_installed),
                new String[] {
                        getString(R.string.dialog_label_yes),
                        getString(R.string.dialog_label_no)
                });
        dialog.setOnDialogResultListener(dialogResultListener, DialogConst.DIALOG_ARCORE_NOT_INSTALLED);
        showDialog(dialog);
    }

    @Override
    public void onDialogPositiveButtonClicked(DialogFragment dialogFragment, Bundle args, int dialogId) {
        switch (dialogId) {
            case DIALOG_ARCORE_NOT_INSTALLED:
            case DIALOG_ARCORE_APK_OLD:
                if (getActivity() == null) {
                    return;
                }
                ((BaseActivity) getActivity()).openPlayStore(getContext(), CommonConstant.PACKAGE_NAME_ARCORE);
                finishApplication();
                break;
            case DIALOG_ARCORE_SDK_OLD:
            case DIALOG_ARCORE_INCOMPATIBLE:
                finishApplication();
                break;
            default:
                super.onDialogPositiveButtonClicked(dialogFragment, args, dialogId);
                break;
        }
    }

    @Override
    public void onDialogNegativeButtonClicked(@NonNull DialogFragment dialogFragment, Bundle args, int dialogId) {
        switch (dialogId) {
            case DIALOG_ARCORE_NOT_INSTALLED:
            case DIALOG_ARCORE_APK_OLD:
                finishApplication();
                break;
            default:
                super.onDialogNegativeButtonClicked(dialogFragment, args, dialogId);
                break;
        }
    }

    /**
     * SoundPool初期処理
     * 予め音声データを読み込むこと
     */
    private void createSoundPool() {
        AudioAttributes attr = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();
        soundPool = new SoundPool.Builder()
                .setAudioAttributes(attr)
                .setMaxStreams(1) // ストリーム数に応じて
                .build();
        soundShutter = soundPool.load(getContext(), R.raw.camera_shutter02_2, 1);
        am = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
    }

    /**
     * サウンドを鳴動
     */
    private void playSoundPool() {
        int streamVolume = am.getStreamVolume(AudioManager.STREAM_RING);
        int maxVolume = am.getStreamMaxVolume(AudioManager.STREAM_RING);
        float soundVolume = (float) streamVolume / (float) maxVolume;
        if (soundPool != null) {
            soundPool.play(soundShutter, soundVolume, soundVolume, 0, 0, 1);
        }
    }

    /**
     * SoundPoolの解放
     */
    private void releaseSoundPool() {
        if (soundPool != null) soundPool.release();
    }

    public void onBackPressed() {
        releaseArCamera();
    }

    /**
     * ARカメラ画面解放
     */
    private void releaseArCamera() {
        if (binding.surfaceView != null) {
            binding.surfaceView.onPause();
            binding.surfaceView.surfaceDestroyed(binding.surfaceView.getHolder());
        }
        if (session != null) {
            session.pause();
            session = null;
        }
    }
}

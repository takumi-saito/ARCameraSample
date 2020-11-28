package t_saito.ar.camera.fragment;

import android.Manifest;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import t_saito.ar.camera.CommonConstant;
import t_saito.ar.camera.R;
import t_saito.ar.camera.activity.BaseActivity;
import t_saito.ar.camera.databinding.FragmentLaunchBinding;
import t_saito.ar.camera.dialogs.BaseDialogFragment;
import t_saito.ar.camera.dialogs.CommonDialogFragment;
import t_saito.ar.camera.dialogs.HowToPagerDialogFragment;
import t_saito.ar.camera.util.PermissionUtils;
import t_saito.ar.camera.util.PreferenceUtil;
import timber.log.Timber;

/**
 * スプラッシュfragment
 *
 * @author t-saito
 */
public class LaunchFragment extends BaseFragment {

    private RxPermissions rxPermissions;
    private Handler handler;
    private Runnable r;

    /**
     * 処理フロー
     */
    enum FLOW {
        /** 利用規約ダイアログ表示 */
        TERM_DESCRIPTION,
        /** 権限取得説明ダイアログ表示 */
        PERMISSION_DESCRIPTION,
        /** KPIログ送信 */
        KPI_SEND_ACTIVE_USER,
        /** 使い方画面表示 */
        TO_HOW_TO,
        /** メイン画面表示 */
        TO_MAIN,
    }

    public static LaunchFragment newInstance() {
        Bundle args = new Bundle();
        LaunchFragment fragment = new LaunchFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentLaunchBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_launch, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.handler = new Handler();
        this.r = () -> {
            if (getActivity().isFinishing()) {
                return;
            }
            ((BaseActivity) getActivity()).toArCamera();
        };
        this.rxPermissions = new RxPermissions(getActivity());
        init();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TermDialogFragment.REQUEST_CODE_TERM:
                if (CommonConstant.RESULT_OK == resultCode) {
                    PreferenceUtil.setTermDialogConfirmed(getContext(), true);
                    startUp(FLOW.PERMISSION_DESCRIPTION);
                }
                break;
            case CommonConstant.REQUEST_CODE_HOW_TO:
                if (CommonConstant.RESULT_OK == resultCode) {
                    PreferenceUtil.setHowToConfirmed(getContext(), true);
                    startUp(FLOW.TO_MAIN);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void init() {
        startUp(FLOW.TERM_DESCRIPTION);
    }

    private void startUp(FLOW flow) {
        switch (flow) {
            case TERM_DESCRIPTION:
                if (!PreferenceUtil.isTermDialogConfirmed(getContext())) {
                    TermDialogFragment termDialogFragment = TermDialogFragment.newInstance(this);
                    FragmentManager fragmentManager = getFragmentManager();
                    termDialogFragment.setTargetFragment(this, TermDialogFragment.REQUEST_CODE_TERM);
                    termDialogFragment.show(fragmentManager, TermDialogFragment.class.getSimpleName());
                    break;
                }
            case PERMISSION_DESCRIPTION:
                Timber.d(new Throwable().getStackTrace()[0].getMethodName() + " FLOW:" + FLOW.PERMISSION_DESCRIPTION.toString());
                checkPermission();
                break;
            case KPI_SEND_ACTIVE_USER:
                Timber.d(new Throwable().getStackTrace()[0].getMethodName() + " FLOW:" + FLOW.KPI_SEND_ACTIVE_USER.toString());
                // KPI登録
//                KpiUtils.sendActiveUser(getApplicationContext(), ActiveUserClient.STATUS_APP_START, ActiveUserClient.LOGTYPE_APP, null);
                break;
            case TO_HOW_TO:
                Timber.d(new Throwable().getStackTrace()[0].getMethodName() + " FLOW:" + FLOW.TO_HOW_TO.toString());
                // fragmentだとスワイプが重い
                if (!PreferenceUtil.isHowToConfirmed(getContext())) {
                    HowToPagerDialogFragment fragment = HowToPagerDialogFragment.newInstance(LaunchFragment.this);
                    showDialog(fragment, fragment.getClass().getSimpleName());
                    break;
                }
            case TO_MAIN:
                Timber.d(new Throwable().getStackTrace()[0].getMethodName() + " FLOW:" + FLOW.TO_MAIN.toString());
                handler.postDelayed(r, 500);
        }
    }

    Disposable disposablePermission;

    private void checkPermission() {
        disposablePermission = createPermission();
    }

    private Disposable createPermission() {
        return this.rxPermissions
                .requestEach(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                .subscribe(new Consumer<Permission>() {
                    boolean cameraGranted = false;
                    boolean storageGranted = false;
                    @Override
                    public void accept(Permission permission) throws Exception {
                        Timber.d(cameraGranted ? "camera granted" : "camera denid");
                        Timber.d(storageGranted ? "storage granted" : "storage denid");
                        this.changePermissionFlg(permission);
                        if (cameraGranted && storageGranted) {
                            //  両方許可されたら使い方画面へ
                            startUp(FLOW.TO_HOW_TO);
                            return;
                        }

                        if (permission.granted) {
                            // 許可された場合

                        } else if(permission.shouldShowRequestPermissionRationale) {
                            // 拒否された場合
                            if (!disposablePermission.isDisposed()) disposablePermission.dispose();
                            showPermissionDeniedDialog(permission.name);
                        } else {
                            // 次回から表示しない場合
                            if (!disposablePermission.isDisposed()) disposablePermission.dispose();
                            CommonDialogFragment dialogFragment = PermissionUtils.showPermissionDeniedDialog(getContext(), R.string.permission_title, R.string.permission_description, false, listener);
                            showDialog(dialogFragment);
                        }
                    }

                    private void changePermissionFlg(Permission permission) {
                        switch (permission.name) {
                            case Manifest.permission.CAMERA:
                                cameraGranted = permission.granted;
                                break;
                            case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                                storageGranted = permission.granted;
                                break;
                        }
                    }
                });
    }

    /**
     * パーミッションが拒否された時のダイアログを表示
     * @param permissionName
     */
    private void showPermissionDeniedDialog(String permissionName) {
        switch (permissionName) {
            case Manifest.permission.CAMERA:
            {
                CommonDialogFragment dialogFragment = PermissionUtils.showPermissionDeniedDialog(getContext(), R.string.permission_name_camera, R.string.permission_name_camera_description, false, listener);
                showDialog(dialogFragment);
            }
            break;
            case Manifest.permission.WRITE_EXTERNAL_STORAGE:
            {
                CommonDialogFragment dialogFragment = PermissionUtils.showPermissionDeniedDialog(getContext(), R.string.permission_name_storage, R.string.permission_name_storage_description, false, listener);
                showDialog(dialogFragment);
            }
            break;
        }
    }

    BaseDialogFragment.OnDialogResultListener listener = new BaseDialogFragment.OnDialogResultListener() {
        @Override
        public void onDialogPositiveButtonClicked(DialogFragment dialogFragment, Bundle args, int dialogId) {
            switch (dialogId) {
                case DIALOG_CAMERA_PERMISSION_DESCRIPTION:
                case DIALOG_EXTERNAL_STORAGE_PERMISSION_DESCRIPTION:
                    checkPermission();
                    break;
                case DIALOG_PERMISSION_DENEID_DESCRIPTION:
                    openSettings();
                    break;
            }
        }

        @Override
        public void onDialogNegativeButtonClicked(DialogFragment dialogFragment, Bundle args, int dialogId) {
            switch (dialogId) {
                case DIALOG_CAMERA_PERMISSION_DESCRIPTION:
                case DIALOG_EXTERNAL_STORAGE_PERMISSION_DESCRIPTION:
                    finishApplication();
                    break;
                case DIALOG_PERMISSION_DENEID_DESCRIPTION:
                    finishApplication();
                    break;
            }

        }

        @Override
        public void onDialogNeutralButtonClicked(DialogFragment dialogFragment, Bundle args, int dialogId) {

        }

        @Override
        public void onDialogDismiss(DialogFragment dialogFragmen, Bundle args, int dialogId) {

        }
    };

    /**
     * 端末設定のアプリ情報画面を開く
     */
    public void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        //Fragmentの場合はgetContext().getPackageName()
        Uri uri = Uri.fromParts("package", getContext().getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }
}

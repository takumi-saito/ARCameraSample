package t_saito.ar.camera.viewmodel;

import android.databinding.ObservableField;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.view.View;

import java.io.IOException;
import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import t_saito.ar.camera.model.ScreenSize;

public class ArCameraViewModel implements ViewModel ,GLSurfaceView.Renderer{

    private final PublishSubject <Integer> otherPublishSubject;
    public final Observable<Integer> otherObservable;

    public final ObservableField<Boolean> pinObservableField;
    private final PublishSubject <Boolean> pinPublishSubject;
    public final Observable<Boolean> pinObservable;

    public final ObservableField<Boolean> flashLightObservableField;
    private final PublishSubject <Boolean> flashLightPublishSubject;
    public final Observable<Boolean> flashLightObservable;

    private ScreenSize screenSize = new ScreenSize(0, 0);
    private boolean isCapture = false;
    public ArCameraViewModel() {
        this.otherPublishSubject = PublishSubject.create();
        this.otherObservable = otherPublishSubject.hide();

        this.flashLightObservableField = new ObservableField<>();
        this.flashLightObservableField.set(false);
        this.flashLightPublishSubject = PublishSubject.create();
        this.flashLightObservable = flashLightPublishSubject.hide();

        this.surfaceCreatedPublishSubject = PublishSubject.create();
        this.surfaceCreatedObservable = surfaceCreatedPublishSubject.hide();
        this.surfaceChangedPublishSubject = PublishSubject.create();
        this.surfaceChangedObservable = surfaceChangedPublishSubject.hide();
        this.drawFramePublishSubject = PublishSubject.create();
        this.drawFrameObservable = drawFramePublishSubject.hide();

        this.capturePublishSubject = PublishSubject.create();
        this.captureObservable = capturePublishSubject.hide();
        this.pinObservableField = new ObservableField<>();
        this.pinObservableField.set(false);
        this.pinPublishSubject = PublishSubject.create();
        this.pinObservable = pinPublishSubject.hide();
    }

    public void onSettingsClicked(View view) {
        doNextOtherPublishSubject(view);
    }

    public void onFlushLightClicked(View view, Boolean flashLightOn) {
        flashLightPublishSubject.onNext(flashLightOn);
        flashLightObservableField.set(!flashLightOn);
    }

    public void onResetClicked(View view) {
        otherPublishSubject.onNext(view.getId());
    }

    public void onPinClicked(View view, Boolean pinned) {
        pinObservableField.set(!pinned);
        pinPublishSubject.onNext(!pinned);
    }

    public void onObjectClicked(View view) {
        doNextOtherPublishSubject(view);
    }

    public void onCaptureClicked(View view) {
        isCapture = true;
    }

    public void onPictureClicked(View view) {
        otherPublishSubject.onNext(view.getId());
    }

    public void onLayoutObjectClicked(View view) {
        this.otherPublishSubject.onNext(view.getId());
    }


    private void doNextOtherPublishSubject(View view) {
        this.otherPublishSubject.onNext(view.getId());
    }

    private final PublishSubject <EGLConfig> surfaceCreatedPublishSubject;
    public final Observable<EGLConfig> surfaceCreatedObservable;
    private final PublishSubject <ScreenSize> surfaceChangedPublishSubject;
    public final Observable<ScreenSize> surfaceChangedObservable;
    private final PublishSubject <GL10> drawFramePublishSubject;
    public final Observable<GL10> drawFrameObservable;

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        surfaceCreatedPublishSubject.onNext(config);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        screenSize = new ScreenSize(width, height);
        surfaceChangedPublishSubject.onNext(screenSize);

    }

    @Override
    public void onDrawFrame(GL10 gl) {
        drawFramePublishSubject.onNext(gl);
        if (isCapture) capture();
    }

    @Override
    public void destroy() {

    }

    private final PublishSubject <Bitmap> capturePublishSubject;
    public final Observable<Bitmap> captureObservable;

    private void capture() {
        isCapture = false;
        try {
            capturePublishSubject.onNext(createFrameBitmap(screenSize.getWidth(), screenSize.getHeight()));
        } catch (IOException e) {
            capturePublishSubject.onError(e);
        }
    }

    /**
     * 画面に描画されている内容を保存
     *
     * @return
     * @throws IOException
     */
    public Bitmap createFrameBitmap(int width, int height) throws IOException {
        int pixelData[] = new int[width * height];

        // 現在のGLFrameからピクセルデータを読み込む
        IntBuffer buf = IntBuffer.wrap(pixelData);
        buf.position(0);
        GLES20.glReadPixels(0, 0, width, height, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, buf);

        // Convert the pixel data from RGBA to what Android wants, ARGB.
        int bitmapData[] = new int[pixelData.length];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int p = pixelData[i * width + j];
                int b = (p & 0x00ff0000) >> 16;
                int r = (p & 0x000000ff) << 16;
                int ga = p & 0xff00ff00;
                bitmapData[(height - i - 1) * width + j] = ga | r | b;
            }
        }
        // Create a bitmap.
        return Bitmap.createBitmap(bitmapData, width, height, Bitmap.Config.RGB_565);
    }


}

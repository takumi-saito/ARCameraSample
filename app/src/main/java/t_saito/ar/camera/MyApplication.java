package t_saito.ar.camera;

import android.app.Application;

import com.facebook.stetho.Stetho;
import com.squareup.leakcanary.LeakCanary;

import t_saito.ar.camera.ui.ImageRxBus;
import timber.log.Timber;

public class MyApplication extends Application {

    private ImageRxBus imageRxBus;
    @Override
    public void onCreate() {
        super.onCreate();
        Timber.plant(new Timber.DebugTree());
        if (BuildConfig.DEBUG) {
            LeakCanary.install(this);
            Stetho.initializeWithDefaults(this);
        }
//        FirebaseAnalytics.getInstance(this);
        // Answers Crashlytics設定
//        Fabric.with(this, new Answers(), new Crashlytics());
        this.imageRxBus = new ImageRxBus();
    }

    public ImageRxBus getImageRxBus() {
        return imageRxBus;
    }
}

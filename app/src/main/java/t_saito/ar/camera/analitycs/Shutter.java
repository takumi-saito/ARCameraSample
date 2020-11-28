package t_saito.ar.camera.analitycs;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;

/**
 * シャッターイベントクラス
 */
public class Shutter implements Event {

    private final static String EVENT_NAME = "shutter";
    private final static String PARAM_OBJECT_NAME = "object_name";
    private final @NonNull Bundle bundle;

    @NonNull
    @Override
    public String getEventName() {
        return EVENT_NAME;
    }

    @NonNull
    @Override
    public Bundle getBundle() {
        return bundle;
    }

    private Shutter(@NonNull Bundle bundle) {
        this.bundle = bundle;
    }

    public static class Builder {
        private final String objectName;

        public Builder(String objectName) {
            this.objectName = objectName;
        }

        public Shutter build() {
            Bundle bundle = new Bundle();
            if (!TextUtils.isEmpty(objectName)) bundle.putString(PARAM_OBJECT_NAME, objectName);
            return new Shutter(bundle);
        }
    }
}

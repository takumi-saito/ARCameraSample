package t_saito.ar.camera.analitycs;

import android.os.Bundle;
import android.support.annotation.NonNull;

/**
 * イベントのテンプレートメソッド
 */
public interface Event {
    @NonNull Bundle getBundle();
    @NonNull String getEventName();
}

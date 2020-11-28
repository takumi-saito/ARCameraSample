package t_saito.ar.camera.analitycs;

import android.content.Context;
import android.support.annotation.NonNull;

/**
 * FirebaseAnalyticsヘルパークラス
 */
public class AnalyticsHelper {

    /**
     * イベント送信
     *
     * @param context コンテキスト
     * @param event イベント
     */
    public static void send(@NonNull Context context, @NonNull Event event){
        // Get instance.
//        FirebaseAnalytics fba = FirebaseAnalytics.getInstance(context);
        // Send event log.
//        fba.logEvent(event.getEventName(), event.getBundle());
//        Timber.d("FirebaseAnalytics send event name:%s", event.getEventName());
    }
}

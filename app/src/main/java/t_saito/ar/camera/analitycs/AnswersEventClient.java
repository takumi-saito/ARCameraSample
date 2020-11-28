package t_saito.ar.camera.analitycs;


import android.support.annotation.NonNull;

import timber.log.Timber;


/**
 * Answers ヘルパークラス
 * {@link CustomEvent#putCustomAttribute}は100文字制限があるため、先頭100文字で切り捨て
 *
 * @author t-saito
 */
public class AnswersEventClient {
    public static void send(@NonNull AnswersEvent answersEvent) {
        try {
//            Answers.getInstance().logCustom(answersEvent.getCustomEvent());
        } catch (Exception e) {
            Timber.e(e);
        }
    }
}

package t_saito.ar.camera;

import android.app.Activity;

public interface CommonConstant {
    int GRID_COLUMN_COUNT_PICTURES = 4;
    int GRID_COLUMN_COUNT_OBJECT = 3;
    String HASH_TAG_WIBUN = "#";
    String HASH_TAG_WBFEST = "#";
    String HASH_TAG_WIBUN_PHOTO = "#";

    String PACKAGE_NAME_ARCORE = "com.google.ar.core";
    String PACKAGE_NAME_INSTAGRAM = "com.instagram.android";
    String PACKAGE_NAME_TWITTER = "com.twitter.android";
    String PACKAGE_NAME_FACEBOOK = "com.facebook.katana";

    // Request Code
    int REQUEST_CODE_TERM = 10;
    int REQUEST_CODE_HOW_TO = 11;
    int RESULT_OK = Activity.RESULT_OK;
    int RESULT_CANCELED = Activity.RESULT_CANCELED;

    /**
     * Preference関連
     */
    interface Preference {
        /** 利用規約ダイアログ表示確認 */
        String KEY_TERM_DIALOG_CONFIRMED = "key_term_dialog_confirmed";
        /** 使い方確認 */
        String KEY_HOW_TO_CONFIRMED = "key_how_to_confirmed";
    }

    /**
     * url一覧
     */
    interface Url {
        String SURVEY = "https://forms.office.com/Pages/ResponsePage.aspx?id=9RxsD_L4g0yke0bUWnL5x3szvlauZcREkMLJictQhDdUOUJDVVlQU0lTQ1Q0TFRUSFgzTjY3WkRZMS4u";
    }
}

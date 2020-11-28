package t_saito.ar.camera.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import t_saito.ar.camera.CommonConstant.Preference;
import t_saito.ar.camera.R;


/**
 * Preferenceユーティリティ
 *
 * @author t-saito
 */

public class PreferenceUtil {

    private static String TAG = PreferenceUtil.class.getSimpleName();

    private static class PreferenceHelper {

        private static final String ON = "1";
        private static final String OFF = "0";

        private Context mContext;
        private String mPreferenceName;

        private String mSecretkey;
        private String mIv;

        public PreferenceHelper(Context context) {
            this(context, "preference");
        }

        public PreferenceHelper(Context context, String preferenceName) {
            this(context, preferenceName, context.getString(R.string.secret_key), context.getString(R.string.iv));
        }

        public PreferenceHelper(Context context, String preferenceName, String secretKey, String iv) {
            mContext = context;
            mPreferenceName = preferenceName;
            mSecretkey = secretKey;
            mIv = iv;
        }

        public boolean save(String key, String string) {
            SharedPreferences.Editor editor = mContext.getSharedPreferences(mPreferenceName, Context.MODE_PRIVATE).edit();
            key = CipherUtil.encryptStringToHexString(mSecretkey, key, mIv);
            string = CipherUtil.encryptStringToHexString(mSecretkey, string, mIv);
            editor.putString(key, string);
            return editor.commit();
        }

        public void save(String key, int value) {
            save(key, Integer.toString(value));
        }

        public void save(String key, long value) {
            save(key, Long.toString(value));
        }

        public void save(String key, boolean flag) {
            save(key, flag ? ON : OFF);
        }

        public String loadString(String key) {
            return loadString(key, null);
        }

        public String loadString(String key, @Nullable String defaultValue) {
            SharedPreferences preferences = mContext.getSharedPreferences(mPreferenceName, Context.MODE_PRIVATE);
            key = CipherUtil.encryptStringToHexString(mSecretkey, key, mIv);
            String string = preferences.getString(key, null);
            return string != null ? CipherUtil.decryptHexStringToString(mSecretkey, string, mIv) : defaultValue;
        }

        public int loadInt(String key) {
            return loadInt(key, -1);
        }

        public int loadInt(String key, int defaultValue) {
            String value = loadString(key);
            return value != null ? Integer.parseInt(value) : defaultValue;
        }

        public long loadLong(String key, long defaultValue) {
            String value = loadString(key);
            return value != null ? Long.parseLong(value) : defaultValue;
        }

        public boolean loadBoolean(String key) {
            return loadBoolean(key, false);
        }

        public boolean loadBoolean(String key, boolean defaultValue) {
            String value = loadString(key);
            return value != null ? TextUtils.equals(loadString(key), ON) : defaultValue;
        }

        public boolean remove(String key) {
            SharedPreferences.Editor editor = mContext.getSharedPreferences(mPreferenceName, Context.MODE_PRIVATE).edit();
            key = CipherUtil.encryptStringToHexString(mSecretkey, key, mIv);
            editor.remove(key);
            return editor.commit();
        }
    }

    /**
     * 利用規約ダイアログ確認フラグ設定
     *
     * @param context アプリケーションコンテキスト
     * @param isConfirmed 確認済みフラグ
     */
    public static void setTermDialogConfirmed(Context context, boolean isConfirmed) {
        PreferenceHelper helper = new PreferenceHelper(context.getApplicationContext());
        helper.save(Preference.KEY_TERM_DIALOG_CONFIRMED, isConfirmed);
    }

    /**
     * 利用規約ダイアログ確認フラグ取得
     *
     * @param context アプリケーションコンテキスト
     * @return true 設定済み：false 未確認
     */
    public static boolean isTermDialogConfirmed(Context context) {
        PreferenceHelper helper = new PreferenceHelper(context.getApplicationContext());
        return helper.loadBoolean(Preference.KEY_TERM_DIALOG_CONFIRMED);
    }

    /**
     * 使い方確認フラグ設定
     *
     * @param context アプリケーションコンテキスト
     * @param isConfirmed 確認済みフラグ
     */
    public static void setHowToConfirmed(Context context, boolean isConfirmed) {
        PreferenceHelper helper = new PreferenceHelper(context.getApplicationContext());
        helper.save(Preference.KEY_HOW_TO_CONFIRMED, isConfirmed);
    }

    /**
     * 使い方確認フラグ取得
     *
     * @param context アプリケーションコンテキスト
     * @return true 設定済み：false 未確認
     */
    public static boolean isHowToConfirmed(Context context) {
        PreferenceHelper helper = new PreferenceHelper(context.getApplicationContext());
        return helper.loadBoolean(Preference.KEY_HOW_TO_CONFIRMED);
    }
}


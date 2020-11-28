package t_saito.ar.camera.activity;

import android.app.Application;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.LayoutRes;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Toast;

import t_saito.ar.camera.BuildConfig;
import t_saito.ar.camera.MyApplication;
import t_saito.ar.camera.R;
import timber.log.Timber;

/**
 * Created by t-saito on 2017/01/20.
 */

public class BaseActivity extends AppCompatActivity {

    /**
     * ハンドラ
     */
    private Handler handler;
    /** 表示中ダイアログ */
    protected DialogFragment currentDialog;

    /** アプリケーション終了用ブロードキャストレシーバ */
    private BroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    @Override
    protected void onDestroy() {
        handler = null;
        super.onDestroy();
    }

    /**
     * ActivityのレイアウトリソースIDを取得する
     *
     * @return レイアウトリソースID
     */
    protected @LayoutRes int getLayoutResourceId() {
        return R.layout.activity_base;
    }

    /**
     * 画面に関する共通の初期処理
     */
    private void initView () {
        setContentView(getLayoutResourceId());
    }

    /**
     * フラグメントのスタックが1つなら、アクティビティを閉じる。
     */
    @Override
    public void onBackPressed() {
        Timber.d("fragment count:%d", getSupportFragmentManager().getBackStackEntryCount());
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            // スタックしている Fragmentがない場合、Activityを終了
            finish();
        } else {
            super.onBackPressed();
        }
        // アニメーションしない
        overridePendingTransition(0, 0);
    }

    /**
     * 階層が最上の画面で戻るキーが押された場合の処理
     */
    private void rootBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() <= 1) {
            Toast.makeText(this, "app finish", Toast.LENGTH_SHORT).show();
            super.onBackPressed();
        } else {
            super.onBackPressed();
        }
    }

    /**
     * カメラ画面に遷移
     */
    public void toArCamera() {
        Intent intent = new Intent(getApplicationContext(), ArCameraActivity.class);
        startActivity(intent);
    }

    /**
     * 設定画面に遷移
     */
    public void toSettings() {
        Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
        startActivity(intent);
    }

    /**
     * 設定画面に遷移
     */
    public void toPictures() {
        Intent intent = new Intent(getApplicationContext(), PicturesActivity.class);
        startActivity(intent);
    }

    /**
     * プレビュー画面に遷移
     */
    public void toPreview() {
        Intent intent = new Intent(getApplicationContext(), PreviewActivity.class);
        super.startActivity(intent);
        overridePendingTransition(R.anim.slide_open_enter, R.anim.slide_open_exit);
    }

    /**
     * 端末設定のアプリ情報画面を開く
     */
    public void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        //Fragmentの場合はgetContext().getPackageName()
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    public void openPlayStore(Context context, String targetPackage) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + targetPackage));
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        // アニメーションしない
        overridePendingTransition(0, 0);
    }

    @Override
    public void startActivity(Intent intent, Bundle options) {
        super.startActivity(intent, options);
        // アニメーションしない
        overridePendingTransition(0, 0);
    }

    /**
     * 外部ブラウザを起動する
     *
     * @param url URL
     */
    public void startBrowser(String url) {
        Timber.d("start browser url:%s", url);
        if (TextUtils.isEmpty(url)) return;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Timber.e(e);
        }
    }
    /**
     * ハンドラ取得
     */
    protected Handler getHandler() {
        if (handler == null) {
            handler = new Handler();
        }
        return handler;
    }

    /**
     * トーストを表示する
     *
     * @param text 表示内容
     */
    public void showToast(final String text) {
        runOnUiThread(() -> Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show());
    }

    /**
     * 現在表示されているFragmentを取得する
     *
     * @return Fragmentオブジェクト
     */
    public Fragment getActiveFragment() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            return null;
        }
        String tag = getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName();
        return getSupportFragmentManager().findFragmentByTag(tag);
    }

    /**
     * アプリケーションを終了させる
     */
    public void finishApplication() {
        finishAndRemoveTask();
    }

    /**
     *
     * @param dialog
     */
    public void showDialog(final DialogFragment dialog) {
        dismissDialog();
        getHandler().post(() -> {
            try {
                currentDialog = dialog;
                dialog.show(getSupportFragmentManager(), "dialog");

            } catch (IllegalStateException e) {
                // ignore.
                Timber.e(e);
            }
        });
    }

    /**
     * ダイアログを閉じる
     */
    protected void dismissDialog() {
        if (currentDialog != null) {
            getHandler().post(() -> currentDialog.dismissAllowingStateLoss());
        }
    }

    /**
     * アプリケーション取得
     * @return
     */
    public MyApplication getMyApplication() {
        Application application = getApplication();
        if (application == null) {
            return null;
        } else if (!(application instanceof MyApplication)) {
            return null;
        }
        return (MyApplication) getApplication();
    }
}

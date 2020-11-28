package t_saito.ar.camera.fragment;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.Spanned;
import android.view.View;

import t_saito.ar.camera.MyApplication;
import t_saito.ar.camera.R;
import t_saito.ar.camera.activity.BaseActivity;
import t_saito.ar.camera.dialogs.BaseDialogFragment;
import t_saito.ar.camera.dialogs.DialogConst;
import timber.log.Timber;

/**
 * 基底フラグメント
 */
public abstract class BaseFragment extends Fragment implements DialogConst, BaseDialogFragment.OnDialogResultListener{

    /** タグ */
    public static final String TAG = BaseFragment.class.getSimpleName();
    /** カレント表示ダイアログ */
    private DialogFragment currentDialog;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        currentDialog = null;
        super.onDestroy();
    }

    /**
     * コンテキストを取得する。
     *
     * @return コンテキスト
     */
    public Context getContext() {
        return getActivity().getApplicationContext();
    }

    /**
     * フラグメントを追加する
     *
     * @param fragment 追加するフラグメント
     * @param addFragmentTag 追加するフラグメントのタグ名
     * @param hideFragmentTag 非表示にするフラグメントのタグ名
     */
    protected void addFragment(Fragment fragment, String addFragmentTag, String hideFragmentTag) {
        Fragment activeFragment = ((BaseActivity) getActivity()).getActiveFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        if (activeFragment != null) {
            transaction.hide(activeFragment.getFragmentManager().findFragmentByTag(hideFragmentTag));
        }
        transaction.add(R.id.fragment_container, fragment, addFragmentTag);
        transaction.addToBackStack(addFragmentTag);
        transaction.commit();
        Timber.d("fragment size : " + getFragmentManager().getBackStackEntryCount());
    }

    @Override
    public void startActivity(Intent intent) {
        ((BaseActivity) getActivity()).startActivity(intent);
    }

    @Override
    public void startActivity(Intent intent, @Nullable Bundle options) {
        ((BaseActivity) getActivity()).startActivity(intent, options);
        super.startActivity(intent, options);
    }

    /**
     * 外部ブラウザを起動する。
     *
     * @param url URL
     */
    protected void startWebBrowser(String url) {
        Timber.d(TAG, "startWebBrowser url:" + url);
        Uri uri = Uri.parse(url);
        Intent i = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(i);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        // アニメーションしない
        getActivity().overridePendingTransition(0, 0);
    }

    /**
     * クリップボードにコピー
     */
    protected void copyClipboard(String label, String text) {
        //システムのクリップボードを取得
        ClipboardManager cm = (ClipboardManager)getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        // クリップボードへ値をコピー
        ClipData clipData = ClipData.newPlainText(label, text);
        cm.setPrimaryClip(clipData);
    }

    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String html){
        Spanned result;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(html);
        }
        return result;
    }

    /**
     * ダイアログを表示する。
     *
     * @param dialog ダイアログ
     */
    protected void showDialog(DialogFragment dialog) {
        showDialog(dialog, null);
    }
    protected void showDialog(DialogFragment dialog, String tag) {
        // バグトラッキング対応：FragmentがAddされているときだけ処理する
        if (isAdded()) {
            dismissDialog();
            this.currentDialog = dialog;
            dialog.show(getFragmentManager(), tag);
        }
    }

    /**
     * ダイアログを非表示する。
     */
    protected void dismissDialog() {
        if (currentDialog != null) {
            try {
                currentDialog.dismiss();
            } catch (Exception e) {
                Timber.e(e);
            }
        }
    }

    protected void finishApplication() {
        if (getActivity() == null) {
            return;
        }
        ((BaseActivity) getActivity()).finishApplication();
    }

    /**
     * エラー用スナックバー表示する。
     */
    protected void showSnackBarFromError(View root, String message) {
        showSnackBarFromError(root, message, null, null);
    }
    protected void showSnackBarFromError(View root, String message, String buttonName, View.OnClickListener listener) {
        int duration = 5000;
        Snackbar snackbar = Snackbar.make(root, message, duration);
        snackbar.getView().setBackgroundColor(ContextCompat.getColor(root.getContext(), R.color.colorPrimaryDark));
        snackbar.setActionTextColor(ContextCompat.getColor(root.getContext(), R.color.md_white_1000));
        if (buttonName != null && listener != null) {
            snackbar.setAction(buttonName, listener);
        }
        snackbar.show();
    }

    /**
     *
     * スナックバーを表示する。
     *
     * @param root View
     */
    protected void showSnackBar(@NonNull View root, String message) {
        Snackbar snackbar = Snackbar.make(root, message, Snackbar.LENGTH_SHORT);
        snackbar.getView().setBackgroundColor(ContextCompat.getColor(root.getContext(), R.color.colorPrimaryDark));
        snackbar.setActionTextColor(ContextCompat.getColor(root.getContext(), R.color.md_white_1000));
        snackbar.show();
    }

    @Override
    public void onDialogPositiveButtonClicked(DialogFragment dialogFragment, Bundle args, int dialogId) {
        // 実装が必要な場合は、継承先で行う
        dismissDialog();
    }

    @Override
    public void onDialogNegativeButtonClicked(DialogFragment dialogFragment, Bundle args, int dialogId) {
        // 実装が必要な場合は、継承先で行う
        dismissDialog();
    }

    @Override
    public void onDialogNeutralButtonClicked(DialogFragment dialogFragment, Bundle args, int dialogId) {
        // 実装が必要な場合は、継承先で行う
        dismissDialog();
    }

    @Override
    public void onDialogDismiss(DialogFragment dialogFragment, Bundle args, int dialogId) {
        // 実装が必要な場合は、継承先で行う
        dismissDialog();
    }

    /**
     * アプリケーション取得
     *
     * @return
     */
    protected MyApplication getMyApplication() {
        Activity activity = getActivity();
        if (activity == null) {
            return null;
        } else if (!(activity instanceof BaseActivity)) {
            return null;
        }
        return ((BaseActivity) activity).getMyApplication();
    }

    /**
     * 指定したApkをインストール
     *
     * @param uri
     */
    private void installApk(Uri uri) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        // 呼び出し先で読めるようにフラグを追加
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);
    }
}

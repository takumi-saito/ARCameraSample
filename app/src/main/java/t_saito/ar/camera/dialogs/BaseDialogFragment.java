package t_saito.ar.camera.dialogs;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

/**
 * 基底のDialogFragment。
 */
public class BaseDialogFragment extends DialogFragment {

    /** タグ */
    private static final String TAG = BaseDialogFragment.class.getSimpleName();

    /** ダイアログ処理結果取得リスナ */
    private OnDialogResultListener mDialogResultListener;
    /** ダイアログID */
    private int mDialogId = -1;
    /** キャンセルリスナ */
    private DialogInterface.OnCancelListener mCancelListener;

    /**
     * ダイアログ処理結果取得リスナ。
     */
    public interface OnDialogResultListener {

        /**
         * ダイアログの処理がPositiveで完了した後に、呼び出し元で行う処理を実装する。
         *
         * @param dialogFragment 呼び出したDialogFragment
         * @param args 処理結果を格納したバンドル
         * @param dialogId ダイアログID
         */
        public void onDialogPositiveButtonClicked(DialogFragment dialogFragment, Bundle args, int dialogId);

        /**
         * ダイアログの処理がNegativeで完了した後に、呼び出し元で行う処理を実装する。
         *
         * @param dialogFragment 呼び出したDialogFragment
         * @param args 処理結果を格納したバンドル
         * @param dialogId ダイアログID
         */
        public void onDialogNegativeButtonClicked(DialogFragment dialogFragment, Bundle args, int dialogId);

        /**
         * ダイアログの処理がNeutralで完了した後に、呼び出し元で行う処理を実装する。
         *
         * @param dialogFragment 呼び出したDialogFragment
         * @param args 処理結果を格納したバンドル
         * @param dialogId ダイアログID
         */
        public void onDialogNeutralButtonClicked(DialogFragment dialogFragment, Bundle args, int dialogId);

        /**
         * ダイアログを破棄する。
         *
         * @param dialogFragmen 呼び出したDialogFragment
         * @param args 処理結果を格納したバンドル
         * @param dialogId ダイアログID
         */
        public void onDialogDismiss(DialogFragment dialogFragmen, Bundle args, int dialogId);
    }

    /*
     * (非 Javadoc)
     * @see android.support.v4.app.DialogFragment#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /*
     * (非 Javadoc)
     * @see android.support.v4.app.DialogFragment#onStart()
     */
    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog() instanceof AlertDialog) {
            // フォーカスを除去する
            // DialogUtility.removeFocus((AlertDialog) getDialog());
        }
    }

    /*
     * (非 Javadoc)
     * @see android.support.v4.app.Fragment#onDestroy()
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /*
     * (非 Javadoc)
     * @see android.support.v4.app.DialogFragment#onCancel(android.content.DialogInterface)
     */
    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        if (mCancelListener != null) {
            mCancelListener.onCancel(dialog);
        }
    }

    /**
     * ダイアログ処理結果取得リスナを取得する。
     *
     * @return ダイアログ処理結果取得リスナ
     */
    public OnDialogResultListener getOnDialogResultListener() {
        return this.mDialogResultListener;
    }

    /**
     * ダイアログID取得リスナを取得する。
     *
     * @return ダイアログID取得リスナ
     */
    public int getOnDialogIdListener() {
        return this.mDialogId;
    }

    /**
     * ダイアログ処理結果取得リスナをセットする。
     *
     * @param listener ダイアログ処理結果取得リスナ
     * @param dialogId ダイアログID
     */
    public void setOnDialogResultListener(OnDialogResultListener listener, int dialogId) {
        this.mDialogResultListener = listener;
        this.mDialogId = dialogId;
    }

    /**
     * Positiveな処理結果をセットする。<br>
     * セットした内容は、呼び出し元に返す。
     *
     * @param args 処理結果を格納したバンドル
     */
    protected void setPositiveResult(Bundle args) {
        if (mDialogResultListener != null) {
            if (args == null) {
                args = new Bundle();
            }
            mDialogResultListener.onDialogPositiveButtonClicked(this, args, mDialogId);
        }
    }

    /**
     * Negativeな処理結果をセットする。<br>
     * セットした内容は、呼び出し元に返す。
     *
     * @param args 処理結果を格納したバンドル
     */
    protected void setNegativeResult(Bundle args) {
        if (mDialogResultListener != null) {
            if (args == null) {
                args = new Bundle();
            }
            mDialogResultListener.onDialogNegativeButtonClicked(this, args, mDialogId);
        }
    }

    /**
     * Neutralな処理結果をセットする。<br>
     * セットした内容は、呼び出し元に返す。
     *
     * @param args 処理結果を格納したバンドル
     */
    protected void setNeutralResult(Bundle args) {
        if (mDialogResultListener != null) {
            if (args == null) {
                args = new Bundle();
            }
            mDialogResultListener.onDialogNeutralButtonClicked(this, args, mDialogId);
        }
    }

    /**
     * ダイアログの処理を破棄した結果をセットする。<br>
     * セットした内容は、呼び出し元に返す。
     *
     * @param args 処理結果を格納したバンドル
     */
    protected void setDismissResult(Bundle args) {
        if (mDialogResultListener != null) {
            if (args == null) {
                args = new Bundle();
            }
            mDialogResultListener.onDialogDismiss(this, args, mDialogId);
        }
    }

    /**
     * キャンセルリスナーを設定します
     *
     * @param l
     */
    public void setOnCancelListener(DialogInterface.OnCancelListener l) {
        mCancelListener = l;
    }

    /**
     * コンテキストを取得する。
     *
     * @return コンテキスト
     */
    protected Context getApplicationContext() {
        return getActivity().getApplicationContext();
    }

    /*
     * (非 Javadoc)
     * @see android.support.v4.app.DialogFragment#dismiss()
     */
    @Override
    public void dismiss() {
        try {
            super.dismiss();

        } catch (Exception e) {
            // ignore.
        }
    }

    /**
     * トーストを表示する。
     *
     * @param text 表示テキスト
     */
    protected void showToast(final String text) {
        final Activity act = getActivity();
        if (act != null) {
            act.runOnUiThread(() -> Toast.makeText(act, text, Toast.LENGTH_SHORT).show());
        }
    }
}

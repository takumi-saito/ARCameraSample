package t_saito.ar.camera.dialogs;

import java.util.ArrayList;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;

import t_saito.ar.camera.R;


/**
 * 共通ダイアログ
 */
public class CommonDialogFragment extends BaseDialogFragment {

    /** タグ */
    @SuppressWarnings("unused")
    private static final String TAG = CommonDialogFragment.class.getSimpleName();

    /** アイコンID */
    private static final String ICON_ID = "icon_id";
    /** タイトル */
    private static final String TITLE = "title";
    /** テキスト */
    private static final String TEXT = "text";
    /** データ */
    private static final String DATA = "data";
    /** 複数データ */
    private static final String DATAS = "datas";
    /** Stringデータ */
    private static final String STR_DATA = "str_data";
    /** Positiveボタン */
    private static final String POSITIVE_BUTTON = "positive_button";
    /** Negativeボタン */
    private static final String NEGATIVE_BUTTON = "negative_button";
    /** Neutralボタン */
    private static final String NEUTRAL_BUTTON = "neutral_button";

    /**
     * インスタンスを生成する。
     * @param iconId アイコンID
     * @param title タイトル
     * @param text テキスト
     * @param buttonName 要素1:Positiveボタン名／要素2:Negativeボタン名／要素3:Neutralボタン名<br>
     *            [positive][neutral][negative]の順
     * @return インスタンス
     */
    public static CommonDialogFragment getInstance(Integer iconId, String title, String text, String[] buttonName) {
        Bundle args = new Bundle();
        if (iconId != null) {
            args.putInt(ICON_ID, iconId);
        }
        args.putString(TITLE, title);
        args.putString(TEXT, text);
        if (buttonName != null) {
            switch (buttonName.length) {
            case 3:
                if (!TextUtils.isEmpty(buttonName[2])) {
                    args.putString(NEUTRAL_BUTTON, buttonName[2]);
                }
            case 2:
                if (!TextUtils.isEmpty(buttonName[1])) {
                    args.putString(NEGATIVE_BUTTON, buttonName[1]);
                }
            case 1:
                if (!TextUtils.isEmpty(buttonName[0])) {
                    args.putString(POSITIVE_BUTTON, buttonName[0]);
                }
            }
        }

        CommonDialogFragment dialog = new CommonDialogFragment();
        dialog.setArguments(args);
        dialog.setCancelable(false);
        return dialog;
    }

    /**
     * インスタンスを生成する。
     *
     * @param iconId アイコンID
     * @param title タイトル
     * @param text テキスト
     * @param buttonName 要素1:Positiveボタン名／要素2:Negativeボタン名／要素3:Neutralボタン名<br>
     *            [positive][neutral][negative]の順
     * @param bundle
     * @return インスタンス
     */
    public static CommonDialogFragment getInstance(Integer iconId, String title, String text, String[] buttonName, Bundle bundle) {
        Bundle args = new Bundle();
        if (iconId != null) {
            args.putInt(ICON_ID, iconId);
        }
        args.putString(TITLE, title);
        args.putString(TEXT, text);
        if (buttonName != null) {
            switch (buttonName.length) {
            case 3:
                if (!TextUtils.isEmpty(buttonName[2])) {
                    args.putString(NEUTRAL_BUTTON, buttonName[2]);
                }
            case 2:
                if (!TextUtils.isEmpty(buttonName[1])) {
                    args.putString(NEGATIVE_BUTTON, buttonName[1]);
                }
            case 1:
                if (!TextUtils.isEmpty(buttonName[0])) {
                    args.putString(POSITIVE_BUTTON, buttonName[0]);
                }
            }
        }
        if (bundle != null) {
            args.putParcelable(DATA, bundle.getParcelable(DialogConst.DIALOG_KEY_DATA));
            args.putParcelableArrayList(DATAS, bundle.getParcelableArrayList(DialogConst.DIALOG_KEY_DATAS));
        }
        CommonDialogFragment dialog = new CommonDialogFragment();
        dialog.setArguments(args);
        dialog.setCancelable(false);
        return dialog;
    }

    public static CommonDialogFragment getInstance(Integer iconId, String title, String text, String[] buttonName, String value) {
        Bundle args = new Bundle();
        if (iconId != null) {
            args.putInt(ICON_ID, iconId);
        }
        args.putString(TITLE, title);
        args.putString(TEXT, text);
        if (buttonName != null) {
            switch (buttonName.length) {
            case 3:
                if (!TextUtils.isEmpty(buttonName[2])) {
                    args.putString(NEUTRAL_BUTTON, buttonName[2]);
                }
            case 2:
                if (!TextUtils.isEmpty(buttonName[1])) {
                    args.putString(NEGATIVE_BUTTON, buttonName[1]);
                }
            case 1:
                if (!TextUtils.isEmpty(buttonName[0])) {
                    args.putString(POSITIVE_BUTTON, buttonName[0]);
                }
            }
        }
        args.putString(STR_DATA, value);
        CommonDialogFragment dialog = new CommonDialogFragment();
        dialog.setArguments(args);
        dialog.setCancelable(false);
        return dialog;
    }

    /*
     * (非 Javadoc)
     * @see android.support.v4.app.DialogFragment#onCreateDialog(android.os.Bundle)
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = this.getArguments();
        final Parcelable data = args.getParcelable(DATA);
        final ArrayList<Parcelable> datas = args.getParcelableArrayList(DATAS);
        final String strData = args.getString(STR_DATA);
        if (!args.containsKey(POSITIVE_BUTTON)) {
            args.putString(POSITIVE_BUTTON, getApplicationContext().getString(R.string.close));
        }

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());


        if (args.containsKey(ICON_ID)) {
            alertDialogBuilder.setIcon(args.getInt(ICON_ID));
        }
        alertDialogBuilder.setTitle(args.getString(TITLE));
        alertDialogBuilder.setMessage(args.getString(TEXT));

        // ボタン1個の場合はそのまま
        if (!args.containsKey(NEGATIVE_BUTTON)) {
            alertDialogBuilder.setPositiveButton(
                    args.getString(POSITIVE_BUTTON), (dialog, which) -> {
                        // setPositiveResultで処理を返したい場合は、onCreateDialogを継承して利用。
                        Bundle bundleData = new Bundle();
                        if (data != null) {
                            bundleData.putParcelable(DialogConst.DIALOG_KEY_DATA, data);
                        }
                        if (datas != null) {
                            bundleData.putParcelableArrayList(DialogConst.DIALOG_KEY_DATAS, datas);
                        }
                        if (!TextUtils.isEmpty(strData)) {
                            bundleData.putString(DialogConst.DIALOG_KEY_DATA, strData);
                        }
                        CommonDialogFragment.this.setPositiveResult(bundleData);
                    });
        } else {
            alertDialogBuilder.setPositiveButton(
                    args.getString(POSITIVE_BUTTON), (dialog, which) -> {
                        // setPositiveResultで処理を返したい場合は、onCreateDialogを継承して利用。
                        Bundle bundleData = new Bundle();
                        if (data != null) {
                            bundleData.putParcelable(DialogConst.DIALOG_KEY_DATA, data);
                        }
                        if (datas != null) {
                            bundleData.putParcelableArrayList(DialogConst.DIALOG_KEY_DATAS, datas);
                        }
                        if (!TextUtils.isEmpty(strData)) {
                            bundleData.putString(DialogConst.DIALOG_KEY_DATA, strData);
                        }
                        CommonDialogFragment.this.setPositiveResult(bundleData);
                    });
            alertDialogBuilder.setNegativeButton(
                    args.getString(NEGATIVE_BUTTON), (dialog, which) -> {
                        // setPositiveResultで処理を返したい場合は、onCreateDialogを継承して利用。
                        Bundle bundleData = new Bundle();
                        if (data != null) {
                            bundleData.putParcelable(DialogConst.DIALOG_KEY_DATA, data);
                        }
                        if (datas != null) {
                            bundleData.putParcelableArrayList(DialogConst.DIALOG_KEY_DATAS, datas);
                        }
                        if (!TextUtils.isEmpty(strData)) {
                            bundleData.putString(DialogConst.DIALOG_KEY_DATA, strData);
                        }
                        CommonDialogFragment.this.setNegativeResult(bundleData);
                    });
        }
        // Neutralボタンは真ん中なので変わらず
        if (args.containsKey(NEUTRAL_BUTTON)) {
            alertDialogBuilder.setNeutralButton(
                    args.getString(NEUTRAL_BUTTON), (dialog, which) -> {
                        // setPositiveResultで処理を返したい場合は、onCreateDialogを継承して利用。
                        CommonDialogFragment.this.setNeutralResult(null);
                    });
        }

        return alertDialogBuilder.create();
    }

    /*
     * (非 Javadoc)
     * @see android.support.v4.app.DialogFragment#onDismiss(android.content.DialogInterface)
     */
    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        setDismissResult(null);
    }
}

package t_saito.ar.camera.util;

import android.content.Context;
import android.support.annotation.StringRes;

import t_saito.ar.camera.R;
import t_saito.ar.camera.dialogs.BaseDialogFragment;
import t_saito.ar.camera.dialogs.CommonDialogFragment;
import t_saito.ar.camera.dialogs.DialogConst;


/**
 * ユーザ認証関連ユーティリティ
 */
public class PermissionUtils {
    @SuppressWarnings ("unused")
    private static final String TAG = PermissionUtils.class.getSimpleName();
    /**
     * 電話権限パーミッション設定ダイアログ表示
     * @param context
     * @param permissionName
     * @param permissionDescription
     * @param isFinish
     * @param listener
     * @return
     */
    public static CommonDialogFragment showPermissionDialog(final Context context, @StringRes int permissionName, @StringRes int permissionDescription, boolean isFinish, final BaseDialogFragment.OnDialogResultListener listener) {
        int cancel = isFinish ? R.string.button_app_finish : R.string.button_close;
        CommonDialogFragment dialog = null;
        switch (permissionName) {
        case R.string.permission_name_camera:
            dialog = CommonDialogFragment.getInstance(
                    null,
                    context.getString(R.string.dialog_title_confirm),
                    context.getString(R.string.dialog_message_warning_external_storage_read_permission_denied),
                    new String[] {
                            context.getString(R.string.button_next),
                    });
            dialog.setOnDialogResultListener(listener, DialogConst.DIALOG_EXTERNAL_STORAGE_PERMISSION_DESCRIPTION);
            break;
            case R.string.permission_name_storage:
                dialog = CommonDialogFragment.getInstance(
                        null,
                        context.getString(R.string.dialog_title_confirm),
                        context.getString(R.string.dialog_message_warning_external_storage_read_permission_denied),
                        new String[] {
                                context.getString(R.string.button_next),
                        });
                dialog.setOnDialogResultListener(listener, DialogConst.DIALOG_EXTERNAL_STORAGE_PERMISSION_DESCRIPTION);
                break;
        case R.string.permission_title:
            dialog = CommonDialogFragment.getInstance(
                    null,
                    context.getString(R.string.permission_title),
                    context.getString(permissionDescription),
                    new String[] {
                            context.getString(R.string.button_next),
                            context.getString(R.string.button_finish),
                    });
            dialog.setOnDialogResultListener(listener, DialogConst.DIALOG_PERMISSION_DESCRIPTION);
            break;
        }
        return dialog;
    }

    /**
     * 権限取得失敗ダイアログ表示
     *
     * @param context アクティビティコンテキスト
     * @param permissionName パーミッション名
     * @param permissionDescription パーミッション詳細
     * @param isFinish キャンセルボタンの文言フラグ（true:アプリ終了,false:閉じる）
     * @param listener ダイアログクローズコールバックリスナー
     */
    public static CommonDialogFragment showPermissionDeniedDialog(final Context context, @StringRes int permissionName, @StringRes int permissionDescription, boolean isFinish, final BaseDialogFragment.OnDialogResultListener listener) {
        CommonDialogFragment dialog = null;
        int cancel = isFinish ? R.string.button_app_finish : R.string.button_close;
        switch (permissionName) {
            case R.string.permission_name_camera:
                dialog = CommonDialogFragment.getInstance(
                        null,
                        context.getString(R.string.dialog_title_confirm),
                        context.getString(R.string.dialog_message_permission_denied, context.getString(permissionName), context.getString(permissionDescription)),
                        new String[] {
                                context.getString(R.string.button_check_again),
                                context.getString(R.string.dialog_label_no)
                        });
                dialog.setOnDialogResultListener(listener, DialogConst.DIALOG_CAMERA_PERMISSION_DESCRIPTION);
                break;
            case R.string.permission_name_storage:
                dialog = CommonDialogFragment.getInstance(
                        null,
                        context.getString(R.string.dialog_title_confirm),
                        context.getString(R.string.dialog_message_permission_denied, context.getString(permissionName), context.getString(permissionDescription)),
                        new String[] {
                                context.getString(R.string.button_check_again),
                                context.getString(R.string.dialog_label_no)
                        });
                dialog.setOnDialogResultListener(listener, DialogConst.DIALOG_EXTERNAL_STORAGE_PERMISSION_DESCRIPTION);
                break;
        case R.string.permission_title:
            dialog = CommonDialogFragment.getInstance(
                    null,
                    context.getString(R.string.permission_title),
                    context.getString(permissionDescription),
                    new String[] {
                            context.getString(R.string.button_open_setting),
                            context.getString(R.string.button_finish),
                    });
            dialog.setOnDialogResultListener(listener, DialogConst.DIALOG_PERMISSION_DENEID_DESCRIPTION);
            break;
        }
        return dialog;
    }
}

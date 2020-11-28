package t_saito.ar.camera.dialogs;

/**
 * ダイアログIDを管理する。
 */
public interface DialogConst {

    // *****************************************************************
    // * 共通
    // *****************************************************************
    /** 閉じるダイアログ */
    int DIALOG_CLOSE = 1;
    /** はい／いいえ */
    int DIALOG_YES_NO = 2;
    /** 終了確認 */
    int DIALOG_FINISH_CONFIRM = 21;
    /** アプリ終了ダイアログ */
    int DIALOG_FINISH = 22;

    // *****************************************************************
    // * 権限
    // *****************************************************************
    /** カメラ説明ダイアログ */
    int DIALOG_CAMERA_PERMISSION_DESCRIPTION = 100;
    /** ストレージ権限説明ダイアログ */
    int DIALOG_EXTERNAL_STORAGE_PERMISSION_DESCRIPTION = 101;
    /** 権限説明ダイアログ */
    int DIALOG_PERMISSION_DESCRIPTION = 105;
    /** 権限説明ダイアログ（権限拒否） */
    int DIALOG_PERMISSION_DENEID_DESCRIPTION = 106;
    /** ARCoreインストール遷移ダイアログ */
    int DIALOG_ARCORE_NOT_INSTALLED = 107;
    /** ARCore非対応ダイアログ (SDKバージョンが古い) */
    int DIALOG_ARCORE_SDK_OLD = 108;
    /** ARCore非対応ダイアログ (ARCoreのバージョンが古い) */
    int DIALOG_ARCORE_APK_OLD = 109;
    /** ARCore非対応ダイアログ */
    int DIALOG_ARCORE_INCOMPATIBLE = 110;
    /** 画像削除確認ダイアログ */
    int DIALOG_DELETE_IMAGE = 111;
    /** アンケート確認ダイアログ */
    int DIALOG_SURVEY = 112;

    /** キー */
    String DIALOG_KEY_DATA = "dialog_key_data";
    /** データ */
    String DIALOG_KEY_DATAS = "dialog_key_datas";
}

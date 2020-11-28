package t_saito.ar.camera.model;

import android.support.annotation.StringRes;

/**
 * 設定クラス
 *
 * @author t-saito
 */
public class SettingsTitle {

    @StringRes
    private int resourceId;
    private String title ="";

    public SettingsTitle(int resourceId, String title) {
        this.resourceId = resourceId;
        this.title = title;
    }

    /**
     * stringのリソースID取得
     *
     * @return リソースID
     */
    @StringRes
    public int getResourceId() {
        return resourceId;
    }

    /**
     * タイトル取得
     *
     * @return タイトル名
     */
    public String getTitle() {
        return title;
    }
}

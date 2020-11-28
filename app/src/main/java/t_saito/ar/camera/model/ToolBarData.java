package t_saito.ar.camera.model;

import android.support.annotation.StringRes;

public class ToolBarData {

    @StringRes
    private int resourceId;
    private boolean enableClose;

    public ToolBarData(int resourceId, boolean enableClose) {
        this.resourceId = resourceId;
        this.enableClose = enableClose;
    }

    public int getResourceId() {
        return resourceId;
    }

    public boolean isEnableClose() {
        return enableClose;
    }
}

package t_saito.ar.camera.model;

import android.support.annotation.AnimRes;

/**
 * Created by takumi-saito on 2018/04/20.
 */

public class ScreenSize {
    private final int width;
    private final int height;

    public ScreenSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}

package t_saito.ar.camera.model;

import android.support.annotation.AnimRes;

/**
 * Created by takumi-saito on 2018/04/20.
 */

public class AnimationItem {
    private final String name;
    @AnimRes
    private final int resourceId;

    public AnimationItem(String name, @AnimRes int resourceId) {
        this.name = name;
        this.resourceId = resourceId;
    }

    public String getName() {
        return name;
    }

    @AnimRes
    public int getResourceId() {
        return resourceId;
    }
}

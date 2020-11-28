package t_saito.ar.camera.model;

import android.support.annotation.DrawableRes;

public class ObjectData {

    private final String objectName;
    private final String textureName;
    private final @DrawableRes int thumResId;
    private final float defScale;

    public String getObjectName() {
        return objectName;
    }

    public String getTextureName() {
        return textureName;
    }

    public int getThumResId() {
        return thumResId;
    }

    public float getDefScale() {
        return defScale;
    }

    public ObjectData(String objectName, String textureName, int thumResId) {
        this(objectName, textureName, thumResId, 0.0f);
    }

    public ObjectData(String objectName, String textureName, int thumResId, float defScale) {
        this.objectName = objectName;
        this.textureName = textureName;
        this.thumResId = thumResId;
        this.defScale = defScale;
    }
}

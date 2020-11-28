package t_saito.ar.camera.model;

import t_saito.ar.camera.ArConstant;

public class ObjectTransform {
    private float lockX = ArConstant.DEF_TRANSITION_X;
    private float lockY = ArConstant.DEF_TRANSITION_Y;
    private float angleX = 0.0f;
    private float angleY = 0.0f;
    private float angleZ = 0.0f;
    private float angleTmpZ = 0.0f;
    private float angleLockZ = 0.0f;

    public float getLockX() {
        return lockX;
    }

    public void setLockX(float lockX) {
        this.lockX = lockX;
    }

    public float getLockY() {
        return lockY;
    }

    public void setLockY(float lockY) {
        this.lockY = lockY;
    }

    public float getAngleX() {
        return angleX;
    }

    public void setAngleX(float angleX) {
        this.angleX = angleX;
    }

    public float getAngleY() {
        return angleY;
    }

    public void setAngleY(float angleY) {
        this.angleY = angleY;
    }

    public float getAngleZ() {
        return angleZ;
    }

    public void setAngleZ(float angleZ) {
        this.angleZ = angleZ;
    }

    public float getAngleTmpZ() {
        return angleTmpZ;
    }

    public void setAngleTmpZ(float angleTmpZ) {
        this.angleTmpZ = angleTmpZ;
    }

    public float getAngleLockZ() {
        return angleLockZ;
    }

    public void setAngleLockZ(float angleLockZ) {
        this.angleLockZ = angleLockZ;
    }
}

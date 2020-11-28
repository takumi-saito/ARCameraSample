package t_saito.ar.camera;

import com.google.ar.core.ArCoreApk;

/**
 * AR関連 constant
 *
 * @author t-saito
 */
public interface ArConstant {

    /**
     * オブジェクト関連
     */
    enum Object {
        ASSET_LAB(Const.OBJECT_ASSET_NAME_LAB, Const.TEXTURE_ASSET_NAME_LAB),
        ASSET_CUBE(Const.OBJECT_ASSET_NAME_CUBE, Const.TEXTURE_ASSET_NAME_CUBE),
        ASSET_0(Const.OBJECT_ASSET_NAME_0, Const.TEXTURE_ASSET_NAME_0),
        ASSET_2(Const.OBJECT_ASSET_NAME_2, Const.TEXTURE_ASSET_NAME_2),
        ASSET_3(Const.OBJECT_ASSET_NAME_3, Const.TEXTURE_ASSET_NAME_3),
        ASSET_5(Const.OBJECT_ASSET_NAME_5, Const.TEXTURE_ASSET_NAME_5),
        ASSET_6(Const.OBJECT_ASSET_NAME_6, Const.TEXTURE_ASSET_NAME_6),
        ASSET_7(Const.OBJECT_ASSET_NAME_7, Const.TEXTURE_ASSET_NAME_7);
        private final String objectName;
        private final String textureName;
        Object(String objectName, String textureName) {
            this.objectName = objectName;
            this.textureName = textureName;
        }
        public String getObjectName() {
            return objectName;
        }
        public String getTextureName() {
            return textureName;
        }
        private interface Const {
            String OBJECT_ASSET_NAME_LAB = "lab.obj";
            String OBJECT_ASSET_NAME_CUBE = "cube.obj";
            String OBJECT_ASSET_NAME_0 = "asset_0.obj";
            String OBJECT_ASSET_NAME_2 = "asset_2.obj";
            String OBJECT_ASSET_NAME_3 = "asset_3.obj";
            String OBJECT_ASSET_NAME_5 = "asset_5.obj";
            String OBJECT_ASSET_NAME_6 = "asset_6.obj";
            String OBJECT_ASSET_NAME_7 = "asset_7.obj";

            String TEXTURE_ASSET_NAME_LAB = "texture_black.png";
            String TEXTURE_ASSET_NAME_CUBE = "texture_black.png";
            String TEXTURE_ASSET_NAME_0 = "asset_0_tex.png";
            String TEXTURE_ASSET_NAME_2 = "asset_2_tex.png";
            String TEXTURE_ASSET_NAME_3 = "asset_3_tex.png";
            String TEXTURE_ASSET_NAME_5 = "asset_5_tex.png";
            String TEXTURE_ASSET_NAME_6 = "asset_6_tex.png";
            String TEXTURE_ASSET_NAME_7 = "asset_7_tex.png";
        }
    }

    /**
     * ARCore Status
     */
    enum ARCoreStatus {
        SUPPORTED_INSTALLED(ArCoreApk.Availability.SUPPORTED_INSTALLED.ordinal()),
        SUPPORTED_NOT_INSTALLED(ArCoreApk.Availability.SUPPORTED_NOT_INSTALLED.ordinal()),
        SUPPORTED_APK_TOO_OLD(ArCoreApk.Availability.SUPPORTED_APK_TOO_OLD.ordinal()),
        SUPPORTED_SDK_TOO_OLD(301),
        UNKNOWN_ERROR(ArCoreApk.Availability.UNKNOWN_ERROR.ordinal());

        private final int code;
        ARCoreStatus(int code) {
            this.code = code;
        }
    }

    float DEF_TRANSITION_X = 0.0f;
    float DEF_TRANSITION_Y = 0.0f;
    float DEF_TRANSITION_Z = -0.5f;
    float DEF_SCALE = 1.0f;
    float DEF_ROTATE = 0.0f;
    float DEF_LIGHT_INTENSITY = 0.75f;

    float MODEL_SURFACE_AMBIENT = 0.0f;
    float MODEL_SURFACE_DIFFUSE = 2.0f;
    float MODEL_SURFACE_SPECULAR = 0.5f;
    float MODEL_SURFACE_SPECULAR_POWER = 6.0f;
}

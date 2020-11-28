package t_saito.ar.camera.dao;

import android.support.annotation.NonNull;

import com.github.gfx.android.orma.annotation.StaticTypeAdapter;

import t_saito.ar.camera.model.Image;

/**
 * Created by t-saito on 2018/04/18.
 */

@StaticTypeAdapter(
        targetType = Image.Type.class,
        serializedType = String.class
)
public class TypeStaticTypeAdapter {
    @NonNull
    public static String serialize(@NonNull Image.Type type) {
        return type.name();
    }

    @NonNull
    public static Image.Type deserialize(@NonNull String serialized) {
        return Image.Type.valueOf(serialized);
    }
}
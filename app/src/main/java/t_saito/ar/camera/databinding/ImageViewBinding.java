package t_saito.ar.camera.databinding;

import android.databinding.BindingAdapter;
import android.support.annotation.DrawableRes;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.IOException;

import timber.log.Timber;

/**
 * ImageViewカスタムセッター
 * @author t-saito
 */
public class ImageViewBinding {
    @BindingAdapter("loadImageFromPath")
    public static void loadImage(ImageView view, String source) {
        Glide.with(view.getContext())
                .load(source)
                .into(view);
    }

    @BindingAdapter("loadImageNoAnimFromPath")
    public static void loadImageNoAnim(ImageView view, String source) {
        Glide.with(view.getContext())
                .load(source)
                .dontAnimate()
                .into(view);
    }

    @BindingAdapter("loadImageNoAnimFromDrawableId")
    public static void loadImageFromDrawableId(ImageView view, @DrawableRes int drawableResId) {
        Glide.with(view.getContext())
                .load(drawableResId)
                .dontAnimate()
                .into(view);
    }
}

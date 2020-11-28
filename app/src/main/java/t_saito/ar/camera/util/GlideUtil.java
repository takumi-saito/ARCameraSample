package t_saito.ar.camera.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.text.TextUtils;
import android.widget.ImageButton;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;

import t_saito.ar.camera.dao.ImageDao;
import t_saito.ar.camera.model.Image;
import timber.log.Timber;

public class GlideUtil {

    public static void loadCircleImageButton(final Context context, final ImageButton imageButton, String filePath) {
        Glide.with(context)
                .load(filePath)
                .asBitmap()
                .centerCrop()
                .into(new BitmapImageViewTarget(imageButton) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        imageButton.setImageDrawable(circularBitmapDrawable);
                    }
                });
    }

}

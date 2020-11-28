package t_saito.ar.camera.viewmodel;

import android.databinding.ObservableField;
import android.view.View;
import android.widget.Toast;

import t_saito.ar.camera.model.Image;
import timber.log.Timber;

/**
 * Created by takumi-saito on 2018/04/19.
 */

public class PictureDetailViewModel implements ViewModel {
    public final ObservableField<Image> image;

    public PictureDetailViewModel(Image image) {
        this.image = new ObservableField<>();
        this.image.set(image);
    }

    public void onImageClicked(View view, Image image) {
        Timber.d("onImageClicked filepath:%s", image.filePath);
    }

    @Override
    public void destroy() {

    }
}

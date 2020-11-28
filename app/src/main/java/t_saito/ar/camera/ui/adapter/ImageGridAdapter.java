package t_saito.ar.camera.ui.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import t_saito.ar.camera.CommonConstant;
import t_saito.ar.camera.R;
import t_saito.ar.camera.databinding.ItemImageBinding;
import t_saito.ar.camera.model.Image;
import t_saito.ar.camera.viewmodel.ItemImageClickListener;
import timber.log.Timber;

/**
 * グリッドadapter
 *
 * @author t-saito
 */
public class ImageGridAdapter extends RecyclerView.Adapter<ImageGridAdapter.ImageGridViewHolder> implements ItemImageClickListener {

    private List<Image> images;
    private Context context;

    private final PublishSubject<Image> imageSubject = PublishSubject.create();
    public final Observable<Image> imageObservable = imageSubject.hide();

    public ImageGridAdapter(Context context, List<Image> images) {
        this.images = images;
        this.context = context;
    }

    @Override
    public ImageGridViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemImageBinding itemImageBinding =  DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_image, parent, false);
        int width = parent.getMeasuredWidth() / CommonConstant.GRID_COLUMN_COUNT_PICTURES;
        itemImageBinding.itemImage.setLayoutParams(new ConstraintLayout.LayoutParams(width, width));
        return new ImageGridViewHolder(itemImageBinding);
    }

    @Override
    public void onBindViewHolder(ImageGridViewHolder holder, int position) {
        Image image = images.get(position);
        holder.itemImageBinding.setImage(image);
        holder.itemImageBinding.setListener(this);
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public List<Image> getItems() {
        return images;
    }

    @Override
    public void onClickImage(View view, Image image) {
        Timber.d("grid onClickImage image %s", image.fileName);
        imageSubject.onNext(image);
    }

    static class ImageGridViewHolder extends RecyclerView.ViewHolder {
        private ItemImageBinding itemImageBinding;
        ImageGridViewHolder(ItemImageBinding itemImageBinding) {
            super(itemImageBinding.getRoot());
            this.itemImageBinding = itemImageBinding;
        }
    }
}

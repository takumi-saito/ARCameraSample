package t_saito.ar.camera.ui.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import t_saito.ar.camera.CommonConstant;
import t_saito.ar.camera.R;
import t_saito.ar.camera.databinding.ItemObjectBinding;
import t_saito.ar.camera.model.ObjectData;

/**
 * グリッドadapter
 *
 * @author t-saito
 */
public class ObjectGridAdapter extends RecyclerView.Adapter<ObjectGridAdapter.ObjectDataGridViewHolder> {

    private List<ObjectData> objects;
    private Context context;

    private final PublishSubject<ObjectData> objectSubject = PublishSubject.create();
    public final Observable<ObjectData> objectObservable = objectSubject.hide();

    public ObjectGridAdapter(Context context, List<ObjectData> objects) {
        this.objects = objects;
        this.context = context;
    }

    @Override
    public ObjectDataGridViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemObjectBinding itemObjectBinding =  DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_object, parent, false);
        int width = parent.getMeasuredWidth() / CommonConstant.GRID_COLUMN_COUNT_OBJECT;
        itemObjectBinding.itemLayout.setLayoutParams(new ConstraintLayout.LayoutParams(width, width));
        return new ObjectDataGridViewHolder(itemObjectBinding);
    }

    @Override
    public void onBindViewHolder(ObjectDataGridViewHolder holder, int position) {
        holder.itemObjectBinding.setObjectData(objects.get(position));
        holder.itemObjectBinding.setListener((view, objectData) -> objectSubject.onNext(objectData));
    }

    @Override
    public int getItemCount() {
        return objects.size();
    }


    static class ObjectDataGridViewHolder extends RecyclerView.ViewHolder {
        private ItemObjectBinding itemObjectBinding;
        ObjectDataGridViewHolder(ItemObjectBinding itemObjectBinding) {
            super(itemObjectBinding.getRoot());
            this.itemObjectBinding = itemObjectBinding;
        }
    }
}

package t_saito.ar.camera.ui;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by takumi-saito on 2018/04/20.
 */

public class ItemOffsetDecoration extends RecyclerView.ItemDecoration {

    private final int space;

    public ItemOffsetDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.set(space, space, space, space);
    }
}

package t_saito.ar.camera.ui;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.GridLayoutAnimationController;

import timber.log.Timber;

/**
 * Created by takumi-saito on 2018/04/20.
 */

public class GridRecyclerView extends RecyclerView {

    /** @see View#View(Context) */
    public GridRecyclerView(Context context) { super(context); }

    /** @see View#View(Context, AttributeSet) */
    public GridRecyclerView(Context context, AttributeSet attrs) { super(context, attrs); }

    /** @see View#View(Context, AttributeSet, int) */
    public GridRecyclerView(Context context, AttributeSet attrs, int defStyle) { super(context, attrs, defStyle); }

    /**
     * TODO アイテムの数によってアニメーション開始位置がおかしくなるので修正すること
     * @param child
     * @param params
     * @param index
     * @param count
     */
    @Override
    protected void attachLayoutAnimationParameters(View child, ViewGroup.LayoutParams params,
                                                   int index, int count) {
        final LayoutManager layoutManager = getLayoutManager();
        if (getAdapter() != null && layoutManager instanceof GridLayoutManager){

            GridLayoutAnimationController.AnimationParameters animationParams =
                    (GridLayoutAnimationController.AnimationParameters) params.layoutAnimationParameters;

            if (animationParams == null) {
                animationParams = new GridLayoutAnimationController.AnimationParameters();
                params.layoutAnimationParameters = animationParams;
            }

            final int columns = ((GridLayoutManager) layoutManager).getSpanCount();

            animationParams.count = count;
            animationParams.index = index;
            animationParams.columnsCount = columns;
            animationParams.rowsCount = count / columns;

            final int invertedIndex = count - 1 - index;
            animationParams.column = columns - 1 - (invertedIndex % columns);
            animationParams.row = animationParams.rowsCount - 1 - invertedIndex / columns;
            Timber.d("animation column:%d row:%d", animationParams.column, animationParams.row);

        } else {
            super.attachLayoutAnimationParameters(child, params, index, count);
        }
    }
}

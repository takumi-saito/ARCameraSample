package t_saito.ar.camera.fragment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import io.reactivex.disposables.Disposable;
import t_saito.ar.camera.CommonConstant;
import t_saito.ar.camera.MyApplication;
import t_saito.ar.camera.R;
import t_saito.ar.camera.dao.ImageDao;
import t_saito.ar.camera.databinding.FragmentImageGridBinding;
import t_saito.ar.camera.model.AnimationItem;
import t_saito.ar.camera.model.Image;
import t_saito.ar.camera.model.ToolBarData;
import t_saito.ar.camera.ui.ItemOffsetDecoration;
import t_saito.ar.camera.ui.adapter.ImageGridAdapter;
import t_saito.ar.camera.viewmodel.ToolBarViewModel;
import timber.log.Timber;

/**
 * 画像一覧fragment
 */
public class PicturesFragment extends BaseFragment {

    private FragmentImageGridBinding binding;
    private boolean isFirst = true;

    /** 画像詳細画面に遷移 */
    private Disposable showDetailDisposable;
    /** 画像削除 */
    private Disposable deleteImageDisposable;
    /** 閉じる */
    private Disposable closeDisposable;

    private AnimationItem[] animationItems = new AnimationItem[] {
            new AnimationItem("Slide from bottom", R.anim.grid_layout_from_bottom) // 下からスライドするアニメーション
//            new AnimationItem("Scale", R.anim.grid_layout_animation_scale),
//            new AnimationItem("Scale random", R.anim.grid_layout_animation_scale_random)
    };

    public static PicturesFragment newInstance() {
        Bundle args = new Bundle();
        PicturesFragment fragment = new PicturesFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_image_grid, container, false);
        initView();
        return binding.getRoot();
    }

    /**
     * View初期処理
     */
    private void initView() {
        ToolBarViewModel toolBarViewModel = new ToolBarViewModel(getContext(), new ToolBarData(R.string.toolbar_title_pictures, true));
        closeDisposable = toolBarViewModel.closeObservable.subscribe(view -> getActivity().onBackPressed());

        final int spacing = getResources().getDimensionPixelOffset(R.dimen.default_spacing_none);
        List<Image> images = createModelData();
        ImageGridAdapter imageGridAdapter = new ImageGridAdapter(getContext(), images);
        showDetailDisposable = imageGridAdapter.imageObservable.subscribe(image -> {
            Timber.d("grid imageObservable.subscribe image %s", image.fileName);
            addFragment(PictureDetailPagerFragment.newInstance(image), PictureDetailPagerFragment.class.getSimpleName(), getTag());
        });

        deleteImageDisposable = ((MyApplication) getContext()).getImageRxBus().toObservable().subscribe(removeImageSet -> {
            Set<Image> removeAdapterImagesSet = removeImageSet.stream()
                    .map(removeImage -> images.stream().filter(adapterImage -> adapterImage.key.equals(removeImage.key)).findFirst())
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toSet());

            boolean isRemove = images.removeAll(removeAdapterImagesSet);
            if (isRemove) {
                Timber.d("Image delete of Album");
                imageGridAdapter.notifyDataSetChanged();
            }
//            int position = getSelectedPosition(images, image);
//            Timber.d("image delete hot observable position:%d filename:%s", position, image.fileName);
//            if (imageGridAdapter.getItemCount() < 1) {
//                Timber.w("delete image not find in images use position:%d", position);
//                return;
//            }
//            images.remove(position);
//            imageGridAdapter.notifyItemRemoved(position);
//            imageGridAdapter.notifyItemRangeChanged(position, images.size());
        });

        binding.toolbarLayout.setToolBarViewModel(toolBarViewModel);
        binding.gridRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), CommonConstant.GRID_COLUMN_COUNT_PICTURES));
        binding.gridRecyclerView.addItemDecoration(new ItemOffsetDecoration(spacing)); // スペース用の独自ItemDecoration
        binding.gridRecyclerView.setAdapter(imageGridAdapter);
        runLayoutAnimation(binding.gridRecyclerView, animationItems[0]);
    }

    @Override
    public void onResume() {
        if (!isFirst) {
            ImageGridAdapter imageGridAdapter = (ImageGridAdapter) binding.gridRecyclerView.getAdapter();
            deleteNotExistsImage(imageGridAdapter.getItems());
            imageGridAdapter.notifyDataSetChanged();
        }
        isFirst = false;
        super.onResume();
    }

    @Override
    public void onDestroy() {
        if (deleteImageDisposable != null && !deleteImageDisposable.isDisposed())  deleteImageDisposable.dispose();
        if (showDetailDisposable != null && !showDetailDisposable.isDisposed())  showDetailDisposable.dispose();
        if (closeDisposable != null && !closeDisposable.isDisposed())  closeDisposable.dispose();
        super.onDestroy();
    }

    /**
     * ファイルが存在しない画像をDBとアダプターのリストデータから削除
     *
     * @param images
     * @return
     */
    private List<Image> deleteNotExistsImage(@NonNull final List<Image> images) {
        Set<Image> removed = images.stream()
                .filter(image -> {
                    File file = new File(image.filePath);
                    if (!file.exists()) {
                        Timber.d("Image file not found. Target image deleted from db:%s", image.filePath);
                        ImageDao.newInstance(getContext()).delete(image);
                    }
                    return !file.exists();
                })
                .collect(Collectors.toSet());
        images.removeAll(removed);
        return images;
    }

    /**
     * イメージリスト作成
     * @return イメージリスト
     */
    private List<Image> createModelData() {
        List<Image> images = ImageDao.newInstance(getContext()).all().blockingGet();
        return deleteNotExistsImage(images);
    }

    private void runLayoutAnimation(final RecyclerView recyclerView, final AnimationItem item) {
        final Context context = recyclerView.getContext();

        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, item.getResourceId());

        recyclerView.setLayoutAnimation(controller);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }

    /**
     * イメージリストからポジション取得
     *
     * @param images
     * @param image
     * @return
     */
    private int getSelectedPosition(List<Image> images, Image image) {
        return IntStream.range(0, images.size())
                .filter(i -> images.get(i).key.equals(image.key))
                .findFirst()
                .orElse(-1);
    }
}

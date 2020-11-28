package t_saito.ar.camera.fragment;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;
import java.util.stream.IntStream;

import io.reactivex.disposables.Disposable;
import t_saito.ar.camera.analitycs.AnswersEventClient;
import t_saito.ar.camera.analitycs.ImageDeleteAnswers;
import t_saito.ar.camera.MyApplication;
import t_saito.ar.camera.R;
import t_saito.ar.camera.dao.ImageDao;
import t_saito.ar.camera.databinding.FragmentPictureDetailPagerBinding;
import t_saito.ar.camera.dialogs.BaseDialogFragment;
import t_saito.ar.camera.dialogs.CommonDialogFragment;
import t_saito.ar.camera.dialogs.DialogConst;
import t_saito.ar.camera.model.Image;
import t_saito.ar.camera.ui.adapter.ImagePagerAdapter;
import t_saito.ar.camera.viewmodel.PictureDetailPagerViewModel;
import timber.log.Timber;

/**
 * 画像詳細fragment
 *
 * @author t-saito
 */
public class PictureDetailPagerFragment extends BaseFragment {

    private static final String KEY_IMAGE = "image";
    private FragmentPictureDetailPagerBinding binding;
    private Disposable disposableDeleteImage;
    private Disposable disposableDeleteImageNotify;
    private Disposable disposableSnackbar;

    public static PictureDetailPagerFragment newInstance(Image image) {
        Bundle args = new Bundle();
        args.putSerializable(KEY_IMAGE, image);
        PictureDetailPagerFragment fragment = new PictureDetailPagerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public PictureDetailPagerFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        List<Image> images = createModelData();
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_picture_detail_pager, container, false);
        binding.setViewModel(new PictureDetailPagerViewModel());
        ImagePagerAdapter adapter = new ImagePagerAdapter(getChildFragmentManager(), images);
        binding.viewPager.setAdapter(adapter);
        binding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Image image = adapter.getItem(position);
                Timber.d("view pager slide position[%d] adapter.getItem data:%s", position, image.fileName);
                binding.getViewModel().imageSubject.onNext(image);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        int position = 0;
        Image selectedImage = null;
        if (getArguments() != null && getArguments().containsKey(KEY_IMAGE)) {
            selectedImage = (Image) getArguments().getSerializable(KEY_IMAGE);
        } else {
            // 受け渡しのimageがない場合
            Timber.w("not find selected image in arguments");
            selectedImage = adapter.getItem(position);
        }

        if (selectedImage != null) {
            Timber.d("image:%s", selectedImage);
            position = getSelectedPosition(images, selectedImage);
            binding.getViewModel().imageSubject.onNext(selectedImage);
            binding.viewPager.setCurrentItem(position);
        }

        disposableSnackbar = binding.getViewModel().snackberObservable.subscribe(strResId -> {
            if (getView() == null) return;
            showSnackBar(binding.coordinatorLayoutBottom, getString(strResId));
        });

        disposableDeleteImageNotify = binding.getViewModel().deleteObservable.subscribe(image -> {
            Timber.d("before binding.viewPager.getCurrentItem():%d", binding.viewPager.getCurrentItem());
            showDialogDeleteImage(image);
        });

        disposableDeleteImage = ((MyApplication) getContext()).getImageRxBus().toObservable().subscribe(imageSet -> {
            imageSet.stream()
                    .map(adapter::getPosition)
                    .filter(removePosition -> removePosition > -1)
                    .forEach(removePosition -> {
                        adapter.remove(removePosition);
                        Timber.d("success to remove image position:%d", removePosition);
                    });

            if (adapter.getCount() < 1) {
                // 表示できるイメージが一件もない場合
                getActivity().onBackPressed();
            }

            // 現在の位置から表示イメージを反映させる
            int currentPosition = binding.viewPager.getCurrentItem();
            Image currentImage = adapter.getItem(currentPosition);
            binding.getViewModel().imageSubject.onNext(currentImage);
            Timber.d("after binding.viewPager.getCurrentItem():%d", binding.viewPager.getCurrentItem());
        });
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        if (disposableSnackbar != null && !disposableSnackbar.isDisposed())  disposableSnackbar.dispose();
        if (disposableDeleteImage != null && !disposableDeleteImage.isDisposed())  disposableDeleteImage.dispose();
        if (disposableDeleteImageNotify != null && !disposableDeleteImageNotify.isDisposed())  disposableDeleteImageNotify.dispose();
        super.onDestroy();
    }

    /**
     * イメージリスト作成
     * @return イメージリスト
     */
    private List<Image> createModelData() {
        return ImageDao.newInstance(getContext()).all().blockingGet();
    }

    private int getSelectedPosition(List<Image> images, Image image) {
        return IntStream.range(0, images.size())
                .filter(i -> images.get(i).key.equals(image.key))
                .findFirst()
                .orElse(-1);
    }

    /**
     * 画像削除
     *
     * @param image
     */
    private void deleteImage(Image image) {
        boolean result = Image.delete(getContext(), image);
        if (result) {
            // イメージの削除イベントをView側に通知
            ((MyApplication) getActivity().getApplicationContext())
                    .getImageRxBus()
                    .send(image);
//            AnswersEventClient.send(new ImageDeleteAnswers.Builder().build());
        }
    }

    /**
     * 画像削除確認ダイアログを表示
     *
     * @param image
     */
    private void showDialogDeleteImage(Image image) {
        CommonDialogFragment dialog = CommonDialogFragment.getInstance(
                null,
                    getString(R.string.dialog_title_delete_image),
                getString(R.string.dialog_message_delete_image),
                new String[] {
                        getString(R.string.dialog_delete),
                        getString(R.string.dialog_cancel),
                });
        dialog.setOnDialogResultListener(new BaseDialogFragment.OnDialogResultListener() {
            @Override
            public void onDialogPositiveButtonClicked(DialogFragment dialogFragment, Bundle args, int dialogId) { deleteImage(image); }

            @Override
            public void onDialogNegativeButtonClicked(DialogFragment dialogFragment, Bundle args, int dialogId) { }

            @Override
            public void onDialogNeutralButtonClicked(DialogFragment dialogFragment, Bundle args, int dialogId) { }

            @Override
            public void onDialogDismiss(DialogFragment dialogFragmen, Bundle args, int dialogId) { }
        }, DialogConst.DIALOG_DELETE_IMAGE);
        showDialog(dialog);
    }
}
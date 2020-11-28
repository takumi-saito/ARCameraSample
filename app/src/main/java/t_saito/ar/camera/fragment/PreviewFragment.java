package t_saito.ar.camera.fragment;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import io.reactivex.disposables.Disposable;
import t_saito.ar.camera.CommonConstant;
import t_saito.ar.camera.analitycs.AnswersEventClient;
import t_saito.ar.camera.analitycs.ImageDeleteAnswers;
import t_saito.ar.camera.MyApplication;
import t_saito.ar.camera.R;
import t_saito.ar.camera.databinding.FragmentPreviewBinding;
import t_saito.ar.camera.dialogs.BaseDialogFragment;
import t_saito.ar.camera.dialogs.CommonDialogFragment;
import t_saito.ar.camera.dialogs.DialogConst;
import t_saito.ar.camera.model.Image;
import t_saito.ar.camera.viewmodel.PreviewViewModel;

/**
 * 画像詳細fragment
 *
 * @author t-saito
 */
public class PreviewFragment extends BaseFragment {

    private static final String KEY_IMAGE = "image";
    private FragmentPreviewBinding binding;
    private Disposable disposableDeleteImage;
    private Disposable disposableDeleteImageNotify;
    private Disposable disposableSnackbar;

    public static PreviewFragment newInstance(Image image) {
        Bundle args = new Bundle();
        args.putSerializable(KEY_IMAGE, image);
        PreviewFragment fragment = new PreviewFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public PreviewFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Image recentImage = (Image) getArguments().getSerializable(KEY_IMAGE);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_preview, container, false);
        binding.setViewModel(new PreviewViewModel(recentImage));
        disposableSnackbar = binding.getViewModel().snackberObservable.subscribe(strResId -> {
            if (getView() == null) return;
            showSnackBar(binding.coordinatorLayoutBottom, getString(strResId));
        });
        disposableDeleteImageNotify = binding.getViewModel().deleteObservable.subscribe(this::showDialogDeleteImage);
        disposableDeleteImage = ((MyApplication) getContext()).getImageRxBus().toObservable().subscribe(imageSet -> {
            // 表示できるイメージが一件もない場合
            getActivity().onBackPressed();
        });
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Image image = binding.getViewModel().imageObservableField.get();
        if (image != null && image.id == 10) {
            showDialogSurvey(this);
        }
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

    private void showDialogSurvey(BaseDialogFragment.OnDialogResultListener dialogResultListener) {
        CommonDialogFragment dialog = CommonDialogFragment.getInstance(
                null,
                getString(R.string.dialog_title_survey),
                getString(R.string.dialog_message_survey),
                new String[] {
                        getString(R.string.dialog_label_yes),
                        getString(R.string.dialog_label_no)
                });
        dialog.setOnDialogResultListener(dialogResultListener, DialogConst.DIALOG_SURVEY);
        showDialog(dialog);
    }

    @Override
    public void onDialogPositiveButtonClicked(DialogFragment dialogFragment, Bundle args, int dialogId) {
        switch (dialogId) {
            case DialogConst.DIALOG_SURVEY:
                startWebBrowser(CommonConstant.Url.SURVEY);
                break;
        }
    }
}
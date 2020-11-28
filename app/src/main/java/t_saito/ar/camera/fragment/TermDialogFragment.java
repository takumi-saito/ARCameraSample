package t_saito.ar.camera.fragment;

import android.app.Dialog;
import android.databinding.DataBindingUtil;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import io.reactivex.disposables.Disposable;
import t_saito.ar.camera.CommonConstant;
import t_saito.ar.camera.R;
import t_saito.ar.camera.activity.BaseActivity;
import t_saito.ar.camera.databinding.FragmentTermDialogBinding;
import t_saito.ar.camera.viewmodel.TermDialogViewModel;

public class TermDialogFragment extends DialogFragment {

    public static final int REQUEST_CODE_TERM = CommonConstant.REQUEST_CODE_TERM;
    private Disposable resultDisposable;

    public static TermDialogFragment newInstance(Fragment target) {
        Bundle args = new Bundle();
        TermDialogFragment fragment = new TermDialogFragment();
        fragment.setTargetFragment(target, REQUEST_CODE_TERM);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);   // タイトルなし
        if (dialog.getWindow() != null) {
            View v = dialog.getWindow().getDecorView();
            v.setBackgroundResource(R.color.transparent_background);
        }
        this.setCancelable(false);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentTermDialogBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_term_dialog, container, false);
        binding.setViewModel(new TermDialogViewModel());
        resultDisposable = binding.getViewModel().dismissObservable.subscribe(result -> {
            switch (result) {
                case APPROVE:
                    getTargetFragment().onActivityResult(getTargetRequestCode(), result.getResultCode(), null);
                    dismiss();
                    break;
                case DISAPPROVE:
                    ((BaseActivity) getActivity()).finishApplication();
                    break;
            }
        });
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Rect displayRectangle = new Rect();
        Window window = getActivity().getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        Dialog dialog = getDialog();

        WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
        layoutParams.height = getResources().getDimensionPixelOffset(R.dimen.term_height);
        layoutParams.width = displayRectangle.width();
        dialog.getWindow().setAttributes(layoutParams);
    }

    @Override
    public void onDestroyView() {
        if (resultDisposable != null && !resultDisposable.isDisposed()) resultDisposable.dispose();
        super.onDestroyView();
    }
}

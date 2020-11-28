package t_saito.ar.camera.dialogs;


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
import t_saito.ar.camera.databinding.FragmentPagerHowToBinding;
import t_saito.ar.camera.ui.adapter.HowToPagerAdapter;
import t_saito.ar.camera.viewmodel.HowToPagerViewModel;


/**
 * 使い方ダイアログフラグメント
 *
 * @author t-saito
 */
public class HowToPagerDialogFragment extends DialogFragment {

    public static final int REQUEST_CODE_HOW_TO = CommonConstant.REQUEST_CODE_HOW_TO;

    private FragmentPagerHowToBinding binding;
    private Disposable buttonDisposable;
    private Disposable positionDisposable;

    public static HowToPagerDialogFragment newInstance() {
        return newInstance(null);
    }

    public static HowToPagerDialogFragment newInstance(@Nullable Fragment target) {
        Bundle args = new Bundle();
        HowToPagerDialogFragment fragment = new HowToPagerDialogFragment();
        fragment.setTargetFragment(target, REQUEST_CODE_HOW_TO);
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
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_pager_how_to, container, false);
        binding.setViewModel(new HowToPagerViewModel());
        binding.setAdapter(new HowToPagerAdapter(getChildFragmentManager(), getContext(),  binding.getViewModel().makeFragments()));
        binding.howToViewPager.setAdapter(binding.getAdapter());

        positionDisposable = binding.getViewModel().positionObservable.subscribe(position -> {
            if (binding.getAdapter().getCount() == (position + 1)) {
                binding.getViewModel().nextVisibility.set(View.GONE);
                binding.getViewModel().closeVisibility.set(View.VISIBLE);
            } else {
                binding.getViewModel().nextVisibility.set(View.VISIBLE);
                binding.getViewModel().closeVisibility.set(View.GONE);
            }
        });

        buttonDisposable = binding.getViewModel().buttonObservable.subscribe(resId -> {
            switch (resId) {
                case R.id.text_next:
                    next();
                    break;
                case R.id.text_close:
                    close();
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
        layoutParams.height = displayRectangle.height();
        layoutParams.width = displayRectangle.width();
        dialog.getWindow().setAttributes(layoutParams);
    }

    @Override
    public void onDestroyView() {
        if (positionDisposable != null && !positionDisposable.isDisposed())  positionDisposable.dispose();
        if (buttonDisposable != null && !buttonDisposable.isDisposed())  buttonDisposable.dispose();
        super.onDestroyView();
    }

    public void next() {
        if (binding.howToViewPager.getCurrentItem() != binding.getAdapter().getCount()) {
            binding.howToViewPager.setCurrentItem(binding.howToViewPager.getCurrentItem() + 1, true);
        }
    }

    private void back() {
        if (binding.howToViewPager.getCurrentItem() == 0) {
            return;
        }
        binding.howToViewPager.setCurrentItem(binding.howToViewPager.getCurrentItem() - 1, true);
    }

    private void close() {
        getTargetFragment().onActivityResult(getTargetRequestCode(), CommonConstant.RESULT_OK, null);
        dismiss();
    }
}
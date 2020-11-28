package t_saito.ar.camera.fragment;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.reactivex.disposables.Disposable;
import t_saito.ar.camera.CommonConstant;
import t_saito.ar.camera.R;
import t_saito.ar.camera.databinding.FragmentPagerHowToBinding;
import t_saito.ar.camera.ui.adapter.HowToPagerAdapter;
import t_saito.ar.camera.viewmodel.HowToPagerViewModel;


/**
 * 使い方 pager fragment
 *
 * @author t-saito
 */
public class HowToPagerFragment extends BaseFragment {

    private FragmentPagerHowToBinding binding;
    private Disposable buttonDisposable;
    private Disposable positionDisposable;

    public static HowToPagerFragment newInstance() {
        return newInstance(null);
    }

    public static HowToPagerFragment newInstance(@Nullable Fragment target) {
        HowToPagerFragment fragment = new HowToPagerFragment();
        fragment.setTargetFragment(target, CommonConstant.REQUEST_CODE_HOW_TO);
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
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
        @Nullable Fragment target = getTargetFragment();
        if (target != null) {
            target.onActivityResult(getTargetRequestCode(), CommonConstant.RESULT_OK, null);
        }
        if (getFragmentManager() == null) return;
        getFragmentManager().popBackStack();
    }
}
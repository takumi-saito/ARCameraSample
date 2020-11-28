package t_saito.ar.camera.fragment;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.reactivex.disposables.Disposable;
import t_saito.ar.camera.R;
import t_saito.ar.camera.databinding.FragmentTermsBinding;
import t_saito.ar.camera.model.ToolBarData;
import t_saito.ar.camera.viewmodel.ToolBarViewModel;

/**
 * 利用規約fragment
 *
 * @author t-saito
 */
public class TermsFragment extends BaseFragment {

    /** 閉じる */
    private Disposable closeDisposable;

    public static TermsFragment newInstance() {
        Bundle args = new Bundle();
        TermsFragment fragment = new TermsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public TermsFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentTermsBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_terms, container, false);
        ToolBarViewModel toolBarViewModel = new ToolBarViewModel(getContext(), new ToolBarData(R.string.toolbar_title_terms, true));
        closeDisposable = toolBarViewModel.closeObservable.subscribe(view -> getActivity().onBackPressed());
        binding.toolbarLayout.setToolBarViewModel(toolBarViewModel);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        if (closeDisposable != null && !closeDisposable.isDisposed())  closeDisposable.dispose();
        super.onDestroyView();
    }
}

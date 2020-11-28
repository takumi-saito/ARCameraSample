package t_saito.ar.camera.fragment;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.AnyRes;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;
import t_saito.ar.camera.CommonConstant;
import t_saito.ar.camera.R;
import t_saito.ar.camera.databinding.FragmentSettingsBinding;
import t_saito.ar.camera.model.SettingsTitle;
import t_saito.ar.camera.model.ToolBarData;
import t_saito.ar.camera.ui.adapter.SettingsAdapter;
import t_saito.ar.camera.viewmodel.ToolBarViewModel;
import timber.log.Timber;


/**
 * 設定フラグメント
 *
 * @author t-saito
 */
public class SettingsFragment extends BaseFragment {

    /** 画面を閉じる */
    private Disposable closeDisposable;
    /** 画面遷移 */
    private Disposable fragmentDisposable;
    /** その他処理 */
    private Disposable otherDisposable;

    public static SettingsFragment newInstance() {
        Bundle args = new Bundle();
        SettingsFragment fragment = new SettingsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * 外部からの使用不可<br>
     * ※privateにすると復帰時に落ちることがあるのでpublicに設定
     */
    public SettingsFragment() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentSettingsBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false);
        initView(binding);
        return binding.getRoot();
    }

    /**
     * View初期処理
     */
    private void initView(FragmentSettingsBinding binding) {
        ToolBarViewModel toolBarViewModel = new ToolBarViewModel(getContext(), new ToolBarData(R.string.toolbar_title_settings, true));
        closeDisposable = toolBarViewModel.closeObservable.subscribe(view -> getActivity().onBackPressed());

        binding.toolbarLayout.setToolBarViewModel(toolBarViewModel);
        binding.recyclerView.setHasFixedSize(true); // データにより大きさが変わらないため
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.addItemDecoration(new DividerItemDecoration(binding.recyclerView.getContext(), new LinearLayoutManager(getActivity()).getOrientation()));
        Pair<List<SettingsTitle>, List<SettingsAdapter.ViewType>> pair = createModelData();
        SettingsAdapter settingsAdapter = new SettingsAdapter(pair.first, pair.second);
        fragmentDisposable = settingsAdapter.fragmentObservable.subscribe(baseFragment -> {
            Timber.d(baseFragment.getClass().getSimpleName());
            addFragment(baseFragment, baseFragment.getClass().getSimpleName(), getTag());
        });
        otherDisposable = settingsAdapter.otherObservable.subscribe(resourceId -> {
            switch (resourceId) {
                case R.string.settings_item_title_survey:
                    startWebBrowser(CommonConstant.Url.SURVEY);
                    break;
            }
        });

        binding.recyclerView.setAdapter(settingsAdapter);
        settingsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        if (closeDisposable != null && !closeDisposable.isDisposed())  closeDisposable.dispose();
        if (fragmentDisposable != null && !fragmentDisposable.isDisposed())  fragmentDisposable.dispose();
        if (otherDisposable != null && !otherDisposable.isDisposed())  otherDisposable.dispose();
        super.onDestroyView();
    }

    /**
     * イメージリスト作成
     * @return イメージリスト
     */
    private Pair<List<SettingsTitle>, List<SettingsAdapter.ViewType>> createModelData() {
        List<SettingsTitle> titleList = new ArrayList<>();
        List<SettingsAdapter.ViewType> viewTypeArrayList = new ArrayList<>();
        Resources res = getResources();
        TypedArray titles = res.obtainTypedArray(R.array.settings_item_titles);
        for (int i = 0; i < titles.length(); i++) {
            @AnyRes int resId = titles.getResourceId(i, -1);
            SettingsTitle settings = new SettingsTitle(resId, titles.getString(i));
            titleList.add(settings);
            viewTypeArrayList.add(SettingsAdapter.ViewType.DEFAULT);
        }
        return Pair.create(titleList, viewTypeArrayList);
    }
}

package t_saito.ar.camera.fragment;


import android.content.res.Resources;
import android.content.res.TypedArray;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.ArrayRes;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import t_saito.ar.camera.R;
import t_saito.ar.camera.databinding.FragmentLicenseBinding;
import t_saito.ar.camera.model.License;
import t_saito.ar.camera.model.ToolBarData;
import t_saito.ar.camera.ui.adapter.LicenseAdapter;
import t_saito.ar.camera.viewmodel.ToolBarViewModel;

/**
 * ライセンスfragment
 * @author t-saito
 */
public class LicenseFragment extends BaseFragment {

    public static LicenseFragment newInstance() {
        Bundle args = new Bundle();
        LicenseFragment fragment = new LicenseFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public LicenseFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentLicenseBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_license, container, false);
        initView(binding);
        return binding.getRoot();
    }

    /**
     * View初期処理
     */
    private void initView(FragmentLicenseBinding binding) {
        ToolBarViewModel toolBarViewModel = new ToolBarViewModel(getContext(), new ToolBarData(R.string.toolbar_title_license, true));
        toolBarViewModel.closeObservable.subscribe(view -> getActivity().onBackPressed());
        LicenseAdapter openLicenseAdapter = new LicenseAdapter(createModelData());

        binding.toolbarLayout.setToolBarViewModel(toolBarViewModel);
        binding.recyclerView.setAdapter(openLicenseAdapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(binding.recyclerView.getContext(), new LinearLayoutManager(getActivity()).getOrientation());
        binding.recyclerView.addItemDecoration(dividerItemDecoration);
        openLicenseAdapter.notifyDataSetChanged();
    }

    /**
     * ライセンスリスト作成
     * @return ライセンスリスト
     */
    private List<License> createModelData() {
        List<License> licenseArrayList = new ArrayList<>();
        Resources res = getResources();
        TypedArray licenses = res.obtainTypedArray(R.array.licenses);

        for (int licensesCnt = 0; licensesCnt < licenses.length(); licensesCnt++) {
            @ArrayRes
            int licenseArrayResId = licenses.getResourceId(licensesCnt, 0);
            String[] licenseStringArray = res.getStringArray(licenseArrayResId);
            String title = "", url = "", message = "";
            for (int i = 0; i < licenseStringArray.length; i++) {
                switch (i) {
                    case 0:
                        title = licenseStringArray[i];
                        break;
                    case 1:
                        url = licenseStringArray[i];
                        break;
                    case 2:
                        message = licenseStringArray[i];
                        break;
                }
            }
            License license1 = new License.Builder(title, message)
                    .setUrl(url)
                    .build();
            licenseArrayList.add(license1);
        }

        // GCさせるため使用後はrecycleする
        licenses.recycle();
        return licenseArrayList;
    }
}

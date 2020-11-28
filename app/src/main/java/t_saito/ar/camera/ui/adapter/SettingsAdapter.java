package t_saito.ar.camera.ui.adapter;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import t_saito.ar.camera.R;
import t_saito.ar.camera.databinding.ItemSettingsDefaultBinding;
import t_saito.ar.camera.fragment.HowToPagerFragment;
import t_saito.ar.camera.fragment.PrivacyPolicyFragment;
import t_saito.ar.camera.fragment.TermsFragment;
import t_saito.ar.camera.model.SettingsTitle;
import t_saito.ar.camera.fragment.BaseFragment;
import t_saito.ar.camera.fragment.LicenseFragment;

/**
 * 設定adapter
 *
 * @author t-saito
 */
public class SettingsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    /**
     * Viewタイプの種類
     */
    public enum ViewType {
        DEFAULT,
    }
    /** 設定タイトルリスト */
    private final List<SettingsTitle> settingsTitleList;
    /** 設定Viewタイプリスト */
    private final List<ViewType> viewTypeList;

    /** 画面遷移 PublishSubject */
    private final PublishSubject<BaseFragment> fragmentSubject = PublishSubject.create();
    public final Observable<BaseFragment> fragmentObservable = fragmentSubject.hide();
    /** その他の操作 OtherSubject */
    private final PublishSubject<Integer> otherSubject = PublishSubject.create();
    public final Observable<Integer> otherObservable = otherSubject.hide();

    public SettingsAdapter(List<SettingsTitle> settingsTitleList, List<ViewType> viewTypeList) {
        this.settingsTitleList = settingsTitleList;
        this.viewTypeList = viewTypeList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (ViewType.values()[viewType]) {
        case DEFAULT:
            ItemSettingsDefaultBinding itemSettingsDefaultBinding =  DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_settings_default, parent, false);
            return new SettingsDefaultViewHolder(itemSettingsDefaultBinding);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof SettingsDefaultViewHolder) {
            SettingsDefaultViewHolder settingsDefaultViewHolder = (SettingsDefaultViewHolder) holder;
            settingsDefaultViewHolder.itemSettingsDefaultBinding.setSettingsTitle(settingsTitleList.get(position));
            settingsDefaultViewHolder.itemSettingsDefaultBinding.setListener((view, settingsTitle) -> {
                switch (settingsTitle.getResourceId()) {
                    case R.string.settings_item_title_how_to:
                        fragmentSubject.onNext(HowToPagerFragment.newInstance());
                        break;
                    case R.string.settings_item_title_survey:
                        otherSubject.onNext(R.string.settings_item_title_survey);
                        break;
                    case R.string.settings_item_title_terms:
                        fragmentSubject.onNext(TermsFragment.newInstance());
                        break;
                    case R.string.settings_item_title_privacy_policy:
                        fragmentSubject.onNext(PrivacyPolicyFragment.newInstance());
                        break;
                    case R.string.settings_item_title_app_license:
                        fragmentSubject.onNext(LicenseFragment.newInstance());
                        break;
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position < viewTypeList.size()) {
            ViewType viewType = viewTypeList.get(position);
            return viewType.ordinal();
        }
        return 0;
    }

    @Override
    public int getItemCount() {
        return settingsTitleList.size();
    }

    /**
     * デフォルトのViewHolder
     */
    private static class SettingsDefaultViewHolder extends RecyclerView.ViewHolder {
        private final ItemSettingsDefaultBinding itemSettingsDefaultBinding;
        SettingsDefaultViewHolder(ItemSettingsDefaultBinding itemSettingsDefaultBinding) {
            super(itemSettingsDefaultBinding.getRoot());
            this.itemSettingsDefaultBinding = itemSettingsDefaultBinding;
        }
    }
}

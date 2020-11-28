package t_saito.ar.camera.ui.adapter;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import java.util.List;

import t_saito.ar.camera.R;
import t_saito.ar.camera.activity.BaseActivity;
import t_saito.ar.camera.databinding.ItemLicenseBinding;
import t_saito.ar.camera.model.License;
import t_saito.ar.camera.viewmodel.ItemLicenseClickListener;


/**
 * オープンライセンスadapter
 *
 * @author t-saito
 */
public class LicenseAdapter extends RecyclerView.Adapter<LicenseAdapter.LicenseViewHolder> {

    @NonNull private final List<License> licenseArrayList;

    public LicenseAdapter(@NonNull List<License> licenseArrayList) {
        this.licenseArrayList = licenseArrayList;
    }

    @Override
    public LicenseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemLicenseBinding itemLicenseBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_license, parent, false);
        return new LicenseViewHolder(itemLicenseBinding);
    }

    @Override
    public void onBindViewHolder(LicenseViewHolder holder, int position) {
        holder.itemLicenseBinding.setLicense(licenseArrayList.get(position));
        holder.itemLicenseBinding.setListener(new ItemLicenseClickListener() {
            @Override
            public void onArrowClicked(View view) {
                Animation inAnimation = AnimationUtils.loadAnimation(view.getContext(), R.anim.in_animation);
                Animation outAnimation = AnimationUtils.loadAnimation(view.getContext(), R.anim.out_animation);
                outAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        holder.itemLicenseBinding.textExpand.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });

                if (holder.itemLicenseBinding.textExpand.getVisibility() == View.GONE) {
                    // アニメーションしながらViewを表示
                    holder.itemLicenseBinding.textExpand.startAnimation(inAnimation);
                    holder.itemLicenseBinding.textExpand.setVisibility(View.VISIBLE);
                    holder.itemLicenseBinding.buttonDown.setVisibility(View.GONE);
                    holder.itemLicenseBinding.buttonUp.setVisibility(View.VISIBLE);
                } else {
                    // アニメーションしながらViewを隠す
                    holder.itemLicenseBinding.textExpand.startAnimation(outAnimation);
                    holder.itemLicenseBinding.buttonDown.setVisibility(View.VISIBLE);
                    holder.itemLicenseBinding.buttonUp.setVisibility(View.GONE);
                }
            }

            @Override
            public void onLinkClicked(View view, String url) {
                ((BaseActivity) view.getContext()).startBrowser(url);
            }
        });
    }

    @Override
    public int getItemCount() {
        return licenseArrayList.size();
    }

    public class LicenseViewHolder extends RecyclerView.ViewHolder {
        private final ItemLicenseBinding itemLicenseBinding;
        LicenseViewHolder(ItemLicenseBinding itemLicenseBinding) {
            super(itemLicenseBinding.getRoot());
            this.itemLicenseBinding = itemLicenseBinding;
        }
    }
}

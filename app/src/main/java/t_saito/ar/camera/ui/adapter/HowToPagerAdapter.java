package t_saito.ar.camera.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.util.SparseArrayCompat;

/**
 * 使い方 pager adapter
 */
public class HowToPagerAdapter extends FragmentPagerAdapter {

    private final SparseArrayCompat<Fragment> fragments;

    public HowToPagerAdapter(@NonNull FragmentManager fm, @NonNull Context context, @NonNull SparseArrayCompat<Fragment> fragments) {
        super(fm);
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}

package t_saito.ar.camera.viewmodel;

import android.databinding.ObservableField;
import android.support.v4.app.Fragment;
import android.support.v4.util.SparseArrayCompat;
import android.support.v4.view.ViewPager;
import android.view.View;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import t_saito.ar.camera.fragment.HowToFirstFragment;
import t_saito.ar.camera.fragment.HowToFourthFragment;
import t_saito.ar.camera.fragment.HowToSecondFragment;
import t_saito.ar.camera.fragment.HowToThirdFragment;

/**
 * 使い方 view model
 */
public class HowToPagerViewModel implements ViewModel, ViewPager.OnPageChangeListener {

    enum Pager {
        FIRST,
        SECOND,
        THIRD,
        FOURTH;
    }

    public final ObservableField<String> filePath;
    public final ObservableField<Integer> nextVisibility;
    public final ObservableField<Integer> closeVisibility;
    private final PublishSubject<Integer> buttonSubject;
    public final Observable<Integer> buttonObservable;
    private final PublishSubject<Integer> positionSubject;
    public final Observable<Integer> positionObservable;

    public HowToPagerViewModel() {
        this.filePath = new ObservableField<>();
        this.nextVisibility = new ObservableField<>();
        this.nextVisibility.set(View.VISIBLE);
        this.closeVisibility = new ObservableField<>();
        this.closeVisibility.set(View.GONE);

        this.buttonSubject = PublishSubject.create();
        this.buttonObservable = buttonSubject.hide();
        this.positionSubject = PublishSubject.create();
        this.positionObservable = positionSubject.hide();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

    @Override
    public void onPageSelected(int position) {
        positionSubject.onNext(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) { }

    public void onClickNext(View view) {
        buttonSubject.onNext(view.getId());
    }

    public void onClickClose(View view) {
        buttonSubject.onNext(view.getId());
    }

    @Override
    public void destroy() {

    }

    public SparseArrayCompat<Fragment> makeFragments() {
        SparseArrayCompat<Fragment> fragments = new SparseArrayCompat<>();
        fragments.put(Pager.FIRST.ordinal(), HowToFirstFragment.newInstance(Pager.FIRST.ordinal()));
        fragments.put(Pager.SECOND.ordinal(), HowToSecondFragment.newInstance(Pager.SECOND.ordinal()));
        fragments.put(Pager.THIRD.ordinal(), HowToThirdFragment.newInstance(Pager.THIRD.ordinal()));
        fragments.put(Pager.FOURTH.ordinal(), HowToFourthFragment.newInstance(Pager.FOURTH.ordinal()));
        return fragments;
    }
}

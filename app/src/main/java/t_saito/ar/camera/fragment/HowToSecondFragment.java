package t_saito.ar.camera.fragment;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import t_saito.ar.camera.R;

/**
 * 使い方 pager fragment（2ページ目）
 *
 * @author t-saito
 */
public class HowToSecondFragment extends BaseFragment {

    private static final String ARG_NUMBER = "number";

    private int number;

    public static HowToSecondFragment newInstance(int number) {
        Bundle args = new Bundle();
        HowToSecondFragment fragment = new HowToSecondFragment();
        args.putInt(ARG_NUMBER, number);
        fragment.setArguments(args);
        return fragment;
    }

    public HowToSecondFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            number = getArguments().getInt(ARG_NUMBER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = DataBindingUtil.inflate(inflater, R.layout.fragment_how_to_second, container, false).getRoot();
        initView(root);
        return root;
    }

    private void initView(View root) {
        root.setTag(number);
    }
}
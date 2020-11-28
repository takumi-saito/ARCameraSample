package t_saito.ar.camera.fragment;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import t_saito.ar.camera.R;
import t_saito.ar.camera.databinding.FragmentImageGridBinding;
import t_saito.ar.camera.databinding.FragmentPictureDetailBinding;
import t_saito.ar.camera.model.Image;
import t_saito.ar.camera.viewmodel.PictureDetailViewModel;

/**
 * 画像詳細fragment
 *
 * @author t-saito
 */
public class PictureDetailFragment extends BaseFragment {

    private static final String KEY_IMAGE = "image";

    public static PictureDetailFragment newInstance(Image image) {
        Bundle args = new Bundle();
        args.putSerializable(KEY_IMAGE, image);
        PictureDetailFragment fragment = new PictureDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public PictureDetailFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Image image = (Image) getArguments().getSerializable(KEY_IMAGE);
        FragmentPictureDetailBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_picture_detail, container, false);
        binding.setViewModel(new PictureDetailViewModel(image));
        return binding.getRoot();
    }
}
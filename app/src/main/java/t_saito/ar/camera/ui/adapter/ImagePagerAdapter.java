package t_saito.ar.camera.ui.adapter;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import java.util.ArrayList;
import java.util.List;

import t_saito.ar.camera.fragment.PictureDetailFragment;
import t_saito.ar.camera.model.Image;

public class ImagePagerAdapter extends ArrayFragmentStatePagerAdapter<Image> {

    private List<Image> images;

    public ImagePagerAdapter(FragmentManager fm, List<Image> images) {
        super(fm, images);
        this.images = images;
    }

    @Override
    public Fragment getFragment(Image image, int position) {
        return PictureDetailFragment.newInstance(image);
    }

    @Override
    public int getPosition(Image image) {
        ArrayList<Image> imageArrayList = getItems();
        for (int i = 0; i < imageArrayList.size(); i++) {
            if (imageArrayList.get(i).key.equals(image.key)) {
                return i;
            }
        }
        return -1;
    }
}

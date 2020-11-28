package t_saito.ar.camera.viewmodel;

import android.view.View;

import t_saito.ar.camera.model.Image;


/**
 * Created by takumi-saito on 2018/04/19.
 */

public interface ItemLicenseClickListener {
    void onArrowClicked(View view);
    void onLinkClicked(View view, String url);
}

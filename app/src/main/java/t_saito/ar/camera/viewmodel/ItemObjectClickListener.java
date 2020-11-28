package t_saito.ar.camera.viewmodel;

import android.view.View;

import t_saito.ar.camera.model.Image;
import t_saito.ar.camera.model.ObjectData;


/**
 * Created by takumi-saito on 2018/04/19.
 */
public interface ItemObjectClickListener {
    void onClickObject(View view, ObjectData objectData);
}

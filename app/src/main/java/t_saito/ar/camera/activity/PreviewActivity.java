package t_saito.ar.camera.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import io.reactivex.Single;
import t_saito.ar.camera.R;
import t_saito.ar.camera.dao.ImageDao;
import t_saito.ar.camera.fragment.PreviewFragment;
import t_saito.ar.camera.model.Image;

public class PreviewActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        @Nullable Single<Image> single = ImageDao.newInstance(getApplicationContext()).recentGet();
        Image recentImage = null;
        if (single != null) {
            recentImage = single.blockingGet();
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, PreviewFragment.newInstance(recentImage), PreviewFragment.class.getSimpleName());
        transaction.commit();
    }
}

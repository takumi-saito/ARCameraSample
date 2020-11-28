package t_saito.ar.camera.activity;

import android.databinding.DataBindingUtil;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;

import t_saito.ar.camera.R;
import t_saito.ar.camera.databinding.ActivityPicturesBinding;
import t_saito.ar.camera.fragment.PicturesFragment;
import t_saito.ar.camera.viewmodel.PicturesViewModel;


public class PicturesActivity extends BaseActivity {

    public PicturesViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityPicturesBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_pictures);
        viewModel = new PicturesViewModel();
        binding.setViewModel(viewModel);
        initView();
    }

    @Override
    protected void onDestroy() {
        viewModel.destroy();
        super.onDestroy();
    }

    private void initView() {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.fragment_container, PicturesFragment.newInstance(), PicturesFragment.class.getSimpleName());
        transaction.commit();
    }
}

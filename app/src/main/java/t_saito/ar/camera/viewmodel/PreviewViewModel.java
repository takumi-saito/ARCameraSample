package t_saito.ar.camera.viewmodel;

import android.databinding.ObservableField;
import android.net.Uri;
import android.view.View;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import t_saito.ar.camera.util.FileUtil;
import t_saito.ar.camera.R;
import t_saito.ar.camera.model.Image;
import t_saito.ar.camera.util.ShareUtil;

/**
 * プレビュー view model
 *
 * @author t-saito
 */
public class PreviewViewModel implements ViewModel {

    public final ObservableField<Image> imageObservableField;

    private final PublishSubject<Integer> snackberSubject;
    public final Observable<Integer> snackberObservable;

    private final PublishSubject<Image> deleteSubject;
    public final Observable<Image> deleteObservable;

    public PreviewViewModel(Image image) {
        this.imageObservableField = new ObservableField<>();
        this.imageObservableField.set(image);

        this.snackberSubject = PublishSubject.create();
        this.snackberObservable = snackberSubject.hide();

        this.deleteSubject = PublishSubject.create();
        this.deleteObservable = deleteSubject.hide();
    }

    public void onDeleteClicked(View view, Image image) {
        deleteSubject.onNext(image);
    }

    public void onShareClicked(View view, Image image) {
        Uri uri = FileUtil.shareImage(view.getContext(), image);
        switch (ShareUtil.shareImage(view.getContext(), uri)) {
            case SUCCESS:
                break;
            case IMAGE_NOT_FOUND:
                snackberSubject.onNext(R.string.share_image_not_found);
                break;
            case APP_NOT_FOUND:
                snackberSubject.onNext(R.string.share_app_not_hound);
                break;
        }
    }


    @Override
    public void destroy() { }
}

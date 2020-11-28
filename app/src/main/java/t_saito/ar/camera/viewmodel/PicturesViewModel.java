package t_saito.ar.camera.viewmodel;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
import t_saito.ar.camera.model.Image;

public class PicturesViewModel implements ViewModel {

    public final BehaviorSubject<Image> imageDeleteSubject;
    public final Observable<Image> deleteImageObservable;

    public PicturesViewModel() {
        this.imageDeleteSubject = BehaviorSubject.create();
        this.deleteImageObservable = imageDeleteSubject.publish().refCount();
    }

    @Override
    public void destroy() {

    }
}

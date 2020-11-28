package t_saito.ar.camera.ui;

import java.util.HashSet;
import java.util.Set;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import t_saito.ar.camera.model.Image;

public class ImageRxBus extends RxBus<Set<Image>> {

    public ImageRxBus() {
    }

    private PublishSubject<Set<Image>> bus = PublishSubject.create();

    public void send(Image image) {
        Set<Image> set = new HashSet<>();
        set.add(image);
        bus.onNext(set);
    }

    public void send(Set<Image> imageSet) {
        bus.onNext(imageSet);
    }

//    public Observable<Image> toObservable() {
////        return bus;
////    }

    public Observable<Set<Image>> toObservable() {
        return bus;
    }

    public boolean hasObservers() {
        return bus.hasObservers();
    }
}
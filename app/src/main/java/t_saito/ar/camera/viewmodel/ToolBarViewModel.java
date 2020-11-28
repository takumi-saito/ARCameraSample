package t_saito.ar.camera.viewmodel;

import android.content.Context;
import android.databinding.ObservableField;
import android.view.View;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import t_saito.ar.camera.model.ToolBarData;

public class ToolBarViewModel implements ViewModel {

    public final ObservableField<String> title;
    public final ObservableField<Boolean> enableClose;
    private final Context context;

    public ToolBarViewModel(Context context, ToolBarData toolBarData) {
        this.context = context;
        this.title = new ObservableField<>();
        this.enableClose = new ObservableField<>();

        title.set(context.getString(toolBarData.getResourceId()));
        enableClose.set(toolBarData.isEnableClose());
    }

    private final PublishSubject<View> closeSubject = PublishSubject.create();
    public final Observable<View> closeObservable = closeSubject.hide();
    public void onCloseClicked(View view) {
        closeSubject.onNext(view);
    }

    @Override
    public void destroy() {

    }
}

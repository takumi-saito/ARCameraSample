package t_saito.ar.camera.viewmodel;

import android.view.View;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import t_saito.ar.camera.CommonConstant;

public class TermDialogViewModel implements ViewModel {

    public enum Result {
        APPROVE(CommonConstant.RESULT_OK),
        DISAPPROVE(CommonConstant.RESULT_CANCELED);

        private final int resultCode;
        public int getResultCode() {
            return resultCode;
        }
        Result(int resultCode) {
            this.resultCode = resultCode;
        }
    }

    private final PublishSubject<Result> dismissSubject;
    public final Observable<Result> dismissObservable;

    public TermDialogViewModel() {
        this.dismissSubject = PublishSubject.create();
        this.dismissObservable = dismissSubject.hide();
    }

    public void onClickApprove(View view) {
        dismissSubject.onNext(Result.APPROVE);
    }

    public void onClickDisapprove(View view) {
        dismissSubject.onNext(Result.DISAPPROVE);
    }

    @Override
    public void destroy() {
    }
}

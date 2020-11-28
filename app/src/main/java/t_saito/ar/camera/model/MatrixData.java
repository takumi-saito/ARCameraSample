package t_saito.ar.camera.model;

import org.reactivestreams.Subscription;

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.FlowableSubscriber;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.processors.PublishProcessor;
import timber.log.Timber;

public class MatrixData {

    private PublishProcessor<MatrixData> processor = PublishProcessor.create();
    private Disposable disposable;
    private float[] matrix;

    public float[] getMatrix() {
        return matrix;
    }

    public void setMatrix(float[] matrix) {
        this.matrix = matrix;
    }

    public MatrixData() {
        this.matrix = new float[16];
        this.logger();
    }

    public float getAngleX() {
        double ragX = Math.atan2(matrix[6], matrix[10]);
        return (float)(ragX * 180 / Math.PI);
    }

    public float getAngleY() {
        double ragY = Math.asin(-matrix[2]);
        return (float)(ragY * 180 / Math.PI);
    }

    public float getAngleZ() {
        double ragZ = Math.atan2(matrix[1], matrix[0]);
        return (float)(ragZ * 180 / Math.PI);
    }

    private void logger() {
        try {
            processor
                    .onBackpressureLatest()
                    .subscribe(new FlowableSubscriber<MatrixData>() {
                        Subscription subscription;
                        @Override
                        public void onSubscribe(Subscription s) {
                            this.subscription = s;
                            // 最初に受け取れる値は一つ
                            s.request(1);
                        }

                        @Override
                        public void onNext(MatrixData matrixData) {
                            Observable.just(matrixData)
                                    .subscribeOn(AndroidSchedulers.mainThread())
                                    .delay(1000, TimeUnit.MILLISECONDS)
                                    .subscribe(matrixData1 -> {
                                        Timber.d("viewMatrix translate X:%f Y:%f Z:%f ", matrixData1.matrix[12], matrixData1.matrix[13], matrixData1.matrix[14]);
                                        Timber.tag("getAngle log").d("angle x:%f y:%f z:%f", matrixData1.getAngleX(), matrixData1.getAngleY(), matrixData1.getAngleZ());
                                        subscription.request(1);
                                    });
                        }

                        @Override
                        public void onError(Throwable t) {
                            Timber.e(t);
                        }

                        @Override
                        public void onComplete() { }
                    });

            disposable = Flowable.interval(1000, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(aLong -> {
                        processor.onNext(this);
                    });
        } catch (Exception e) {
            Timber.e(e);
        }
    }
}

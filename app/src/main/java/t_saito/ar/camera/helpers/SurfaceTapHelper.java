/*
 * Copyright 2017 Google Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package t_saito.ar.camera.helpers;

import android.content.Context;
import android.os.SystemClock;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.View.OnTouchListener;

import java.util.concurrent.ArrayBlockingQueue;

import t_saito.ar.camera.ArConstant;
import t_saito.ar.camera.CommonConstant;
import t_saito.ar.camera.ui.gesture.RotateGestureDetector;
import timber.log.Timber;

/**
 * Helper to detect taps using Android GestureDetector, and pass the taps between UI thread and
 * render thread.
 */
public final class SurfaceTapHelper implements OnTouchListener {

    private final static float DEF_SCALE = ArConstant.DEF_SCALE;
    private final static float DEF_ROTATE = ArConstant.DEF_ROTATE;

    private final GestureDetector gestureDetector;
    private final ScaleGestureDetector scaleGestureDetector;
    private final RotateGestureDetector rotateGestureDetector;
    private int ptlCount = 0;
    private MotionEvent motionEvent;
    private final ArrayBlockingQueue<MotionEvent> quesedSingleTaps = new ArrayBlockingQueue<>(16);
    private float scaleFactor = DEF_SCALE;
    private float rotateFactor = 0.0f;

    /**
    * Creates the tap helper.
    *
    * @param context the application's context.
    */
    public SurfaceTapHelper(Context context) {
        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                /**
                 * 指が画面に押下されて，画面から離れると呼び出される．
                 * UPのイベントを拾って呼び出してるイメージ．
                 * ただし，指が少しでも移動してしまう，もしくは長押したときは呼び出されない．
                 * タップされた場所は引数のMotionEventからgetX()とgetY()で取得できる．
                 * @param e
                 * @return
                 */
                @Override
                public boolean onSingleTapConfirmed(MotionEvent e) {
                    quesedSingleTaps.offer(motionEvent);
                    return true;
                }

                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    return super.onDoubleTap(e);
                }

                @Override
                public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                    if (ptlCount < 2) {
                        quesedSingleTaps.offer(motionEvent);
                        return true;
                    } else {
                        return false;
                    }
                }
            });
        scaleGestureDetector = new ScaleGestureDetector(context, new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                scaleFactor *= detector.getScaleFactor();
                Timber.d("scaleFactor %f", scaleFactor);
//                scaleFactor = (scaleFactor < 1 ? 1 : scaleFactor);
//                scaleFactor = ((float) ((int) (scaleFactor + 100))) / 100;
                return true;
            }

            @Override
            public boolean onScaleBegin(ScaleGestureDetector detector) {
                return super.onScaleBegin(detector);
            }

            @Override
            public void onScaleEnd(ScaleGestureDetector detector) {
                super.onScaleEnd(detector);
            }
        });

        rotateGestureDetector = new RotateGestureDetector(new RotateGestureDetector.SimpleOnRotateGestureDetector() {
            @Override
            public boolean onRotate(float degrees, float focusX, float focusY) {
                rotateFactor += degrees * 2;
                Timber.d("rotateFactor:%f degrees:%f focusX:%f focusY:%f", rotateFactor, degrees, focusX, focusY);
                return super.onRotate(degrees, focusX, focusY);
            }
        });
    }

    /**
    * Polls for a tap.
    *
    * @return if a tap was queued, a MotionEvent for the tap. Otherwise null if no taps are queued.
    */
    public MotionEvent poll() {
        return quesedSingleTaps.poll();
    }

    public float getScaleFactor() {
        return scaleFactor;
    }

    public void resetScaleFactor() {
        scaleFactor = DEF_SCALE;
    }

    public float getRotateFactor() {
        return rotateFactor;
    }

    public void resetRotateFactor() {
        rotateFactor = DEF_ROTATE;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
      int action = (motionEvent.getAction() & MotionEvent.ACTION_MASK);
      switch (action) {
          case MotionEvent.ACTION_POINTER_DOWN:
              ptlCount++;
              break;
          case MotionEvent.ACTION_POINTER_UP:
              ptlCount--;
              break;
          case MotionEvent.ACTION_DOWN:
              ptlCount++;
              break;
          case MotionEvent.ACTION_UP:
              ptlCount--;
              break;
      }
      this.motionEvent = motionEvent;
      if (!gestureDetector.onTouchEvent(motionEvent)) {
          scaleGestureDetector.onTouchEvent(motionEvent);
          rotateGestureDetector.onTouchEvent(motionEvent);
      }
      return true;
    }
}

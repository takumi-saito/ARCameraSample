package t_saito.ar.camera.ui.gesture;

import android.view.MotionEvent;

import timber.log.Timber;

public class RotateGestureDetector {

    private interface OnRotateListener {
        boolean onRotate(float degrees, float focusX, float focusY);
    }

    public static class SimpleOnRotateGestureDetector implements OnRotateListener {
        @Override
        public boolean onRotate(float degrees, float focusX, float focusY) {
            return false;
        }
    }

    private static float RADIAN_TO_DEGREES = (float) (180.0 / Math.PI);
    private OnRotateListener listener;
    private float prevX = 0.0f;
    private float prevY = 0.0f;
    private float prevTan;

    public RotateGestureDetector(OnRotateListener listener) {
        this.listener = listener;
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (motionEvent.getPointerCount() == 2 && motionEvent.getActionMasked() == MotionEvent.ACTION_MOVE) {
            boolean result = true;
            float x = motionEvent.getX(1) - motionEvent.getX(0);
            float y = motionEvent.getY(1) - motionEvent.getY(0);
            float focusX = (motionEvent.getX(1) + motionEvent.getX(0)) * 0.5f;
            float focusY = (motionEvent.getY(1) + motionEvent.getY(0)) * 0.5f;
            float tan = (float) Math.atan2(y, x);

            if (prevX != 0.0f && prevY != 0.0f) {
                result = listener.onRotate((tan - prevTan) * RADIAN_TO_DEGREES, focusX, focusY);
            }

            prevX = x;
            prevY = y;
            prevTan = tan;
            return result;
        } else {
            prevX = prevY = prevTan = 0.0f;
            return true;
        }
    }
}

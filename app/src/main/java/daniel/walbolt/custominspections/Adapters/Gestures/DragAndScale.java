package daniel.walbolt.custominspections.Adapters.Gestures;

import android.content.Context;
import android.util.Log;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import daniel.walbolt.custominspections.Activities.CameraActivity;

public class DragAndScale implements View.OnTouchListener, GestureDetector.OnGestureListener
{

    /*
    This is a custom touch listener to enable dragging of certain views around the screen.
    This class communicates with the specific CameraActivity that uses this class in order to enable scaling features.
     */

    private View view;
    private GestureDetector gestureDetector;
    private CameraActivity cameraPage; //Store the activity using this listener on some of its views. This is to provide scaling functionality using a SeekBar
    private boolean dragging = true;

    public DragAndScale(CameraActivity cameraPage)
    {

        this.cameraPage = cameraPage;
        gestureDetector = new GestureDetector(cameraPage, this);

    }

    float dX, dY;
    @Override
    public boolean onTouch(View view, MotionEvent event)
    {

        this.view = view;

        gestureDetector.onTouchEvent(event);

        if (dragging)
        {

            switch (event.getAction()) {

                case MotionEvent.ACTION_DOWN:

                    dX = view.getX() - event.getRawX();
                    dY = view.getY() - event.getRawY();
                    break;

                case MotionEvent.ACTION_MOVE:

                    view.setX(event.getRawX() + dX);
                    view.setY(event.getRawY() + dY);
                    break;
                default:
                    return false;
            }

        }

        return true;
    }


    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent)
    {

        cameraPage.activateScaling(view);

    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1)
    {
        return false;
    }
}

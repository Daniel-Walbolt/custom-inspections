package daniel.walbolt.custominspections.PDF.CustomViews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.animation.ScaleAnimation;
import android.widget.ScrollView;

import androidx.annotation.IdRes;

import daniel.walbolt.custominspections.R;

public class ZoomScrollView extends ScrollView
{

    /*
    This class is a controller for a traditional scroll view.
     */

    private float mScale = 1.0f;
    private ScaleGestureDetector scaleDetector;
    GestureDetector gestureDetector;

    private int ID;

    public ZoomScrollView(Context context, @IdRes int id)
    {

        super(context);

        this.ID = id;

    }

    public ZoomScrollView(Context context, AttributeSet attrs)
    {

        //If the other constructor is called,  this one is called with default attributes.
        super(context, attrs);

        //Create the gesture detector
        gestureDetector = new GestureDetector(context, new GestureListener());

        //Create the scale detector
        scaleDetector = new ScaleGestureDetector(context, new ScaleGestureDetector.SimpleOnScaleGestureListener() {

            @Override
            public boolean onScale(ScaleGestureDetector detector)
            {

                //Determine the scale desired by the user
                float scale = 1 - detector.getScaleFactor();

                //Adjust the previous scale to the new scale
                float prevScale = mScale;
                mScale += scale;

                if (mScale < 0.1f) // Minimum scale factor
                    mScale = 0.1f;

                if (mScale > 10f) // Maximum scale factor
                    mScale = 10f;

                //Create the scale animation
                ScaleAnimation scaleAnimation = new ScaleAnimation(1f / prevScale, 1f / mScale, 1f / prevScale, 1f / mScale, detector.getFocusX(), detector.getFocusY());
                    scaleAnimation.setDuration(0);
                    scaleAnimation.setFillAfter(true);

                //Play the scale animation on this scroll view
                getRootView().findViewById(ID).startAnimation(scaleAnimation);

                        return true;

            }

        });

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event)
    {

        super.dispatchTouchEvent(event);
        scaleDetector.onTouchEvent(event);
        gestureDetector.onTouchEvent(event);
        return gestureDetector.onTouchEvent(event);

    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            return true;
        }
    }



}

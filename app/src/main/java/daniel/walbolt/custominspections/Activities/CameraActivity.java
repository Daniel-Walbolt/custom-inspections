package daniel.walbolt.custominspections.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.concurrent.ExecutionException;

import daniel.walbolt.custominspections.Inspector.Dialogs.Alerts.ConfirmAlert;
import daniel.walbolt.custominspections.R;

public class CameraActivity extends AppCompatActivity
{

    /*

    This activity opens a custom camera feature using CameraX API

     */

    private File imageLocation;

    private boolean isEditing = false;
    private ImageButton addSquare;
    private ImageButton addCircle;
    private Button retake;
    private ImageButton confirmImage;
    private ImageButton editImage;

    private ImageButton takePicture;

    private PreviewView previewView;
    private ImageView captureView;
    private RelativeLayout shapeContainer;

    private ImageCapture imageCapture;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        this.getSupportActionBar().hide();

        //Get the image file location out of the intent
        Intent intent = getIntent();
        imageLocation = (File) intent.getSerializableExtra("File");

        //Initialize ALL views for ALL states of the activity.
        initViews();

        //Initialize the image capture of the camera
        imageCapture = new ImageCapture.Builder()
                .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                .setTargetRotation(getDisplay().getRotation())
                .build();

        //Gets access to the camera hardware
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        //Verify the camera acquisition by adding a listener
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                Toast.makeText(this, "Error while accessing Camera", Toast.LENGTH_SHORT).show();
            }
        }, ContextCompat.getMainExecutor(this));



    }

    private void setEditMode(boolean isEditing)
    {

        previewView.setVisibility(isEditing ? View.GONE : View.VISIBLE);
        takePicture.setVisibility(isEditing ? View.GONE : View.VISIBLE);

        captureView.setVisibility(isEditing ? View.VISIBLE : View.GONE);
        shapeContainer.setVisibility(isEditing ? View.VISIBLE : View.GONE);

        editImage.setVisibility(isEditing ? View.VISIBLE : View.GONE);
            editImage.setImageDrawable(AppCompatResources.getDrawable(editImage.getContext(), R.drawable.image_edit_icon_inactive));
        confirmImage.setVisibility(isEditing ? View.VISIBLE : View.GONE);
        retake.setVisibility(isEditing ? View.VISIBLE : View.GONE);
        addSquare.setVisibility(isEditing ? View.GONE : View.GONE); // Shapes are only visible after clicking edit button
        addCircle.setVisibility(isEditing ? View.GONE : View.GONE); // Shapes are only visible after clicking edit button

    }

    private void bindPreview(ProcessCameraProvider cameraProvider)
    {

        Preview preview = new Preview.Builder().build();

        // Configure Camera Settings using this builder
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        //Link the PreviewView to the Preview controller.
        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        //Bind the camera instance to the lifecycle of this activity.
        Camera camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);

    }

    private void initViews()
    {

        //Initialize views to their default values

        setContentView(R.layout.camera_page);

        takePicture = findViewById(R.id.camera_picture);
        previewView = findViewById(R.id.camera_view);
        captureView = findViewById(R.id.capture_view);
        shapeContainer = findViewById(R.id.shape_container);

        takePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture();
            }
        });

        confirmImage = findViewById(R.id.image_confirm);
        confirmImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new ConfirmAlert(confirmImage.getContext(), "Are you done editing?",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                shapeContainer.setDrawingCacheEnabled(true);
                                Bitmap b = shapeContainer.getDrawingCache();
                                try {
                                    b.compress(Bitmap.CompressFormat.JPEG, 90, new FileOutputStream(imageLocation));
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }
                                finish();
                            }
                        });
            }
        });

        editImage = findViewById(R.id.image_edit);
        editImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Make the edit buttons visible
                isEditing = !isEditing;

                editImage.setImageDrawable(AppCompatResources.getDrawable(editImage.getContext(),
                        isEditing ? R.drawable.image_edit_icon_active
                              : R.drawable.image_edit_icon_inactive));

                showEditViews(isEditing);

            }
        });
        editImage.setActivated(false);

        addSquare = findViewById(R.id.image_edit_square);
        addSquare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isEditing)
                    addSquare();
            }
        });
        addCircle = findViewById(R.id.image_edit_circle);
        addCircle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isEditing)
                    addCircle();
            }
        });

        retake = findViewById(R.id.image_retake);
        retake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                new ConfirmAlert(v.getContext(), "Erase your masterpiece?",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v)
                            {

                                //What to do when confirmed      
                                setEditMode(false);
                                shapeContainer.removeAllViews(); // Erase all the added shapes
                                shapeContainer.addView(captureView); // Add the ImageView back to the container

                            }
                        });
            }
        });

        setEditMode(false);
        showEditViews(false);


    }

    private void showEditViews(boolean show)
    {

        Animation animation;
        if(show)
            animation= AnimationUtils.loadAnimation(this, R.anim.scale_up);
        else
            animation = AnimationUtils.loadAnimation(this, R.anim.scale_down);
        addSquare.setAnimation(animation);
        addCircle.setAnimation(animation);

        addSquare.setVisibility(show ? View.VISIBLE : View.GONE);
        addCircle.setVisibility(show ? View.VISIBLE : View.GONE);

    }

    private void takePicture()
    {

        //Set the handler of the image to save to the file location provided by the Intent
        ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions
                .Builder(imageLocation).build();

        imageCapture.takePicture(outputFileOptions, ContextCompat.getMainExecutor(this), new ImageCapture.OnImageSavedCallback() {

            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(previewView.getContext(), "Saved Image", Toast.LENGTH_SHORT).show();
                        setEditMode(true);
                        captureView.setImageURI(Uri.fromFile(imageLocation));
                    }
                });

            }

            @Override
            public void onError(@NonNull ImageCaptureException exception)
            {
                System.out.println(exception.toString());
                System.out.println("ERROR!");
            }
        });

    }

    private void addSquare()
    {

        //Create view to display drawable
        ImageView drawableImage = new ImageView(this);
        drawableImage.setImageDrawable(AppCompatResources.getDrawable(this,R.drawable.image_square));
        drawableImage.setScaleType(ImageView.ScaleType.MATRIX); // Essential scaling type for PINCH to ZOOM feature

        //Define listeners for touch events
        ScaleGestureDetector GSD = new ScaleGestureDetector(this, new ScaleListener(drawableImage));
        drawableImage.setOnTouchListener(getShapeTouchListener(drawableImage, GSD));

        // Center the drawable by placing layout parameters on it
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(300, 300);
        params.leftMargin = shapeContainer.getWidth()/2 - 150;
        params.topMargin = shapeContainer.getHeight()/2 - 150;

        shapeContainer.addView(drawableImage, params);

    }

    private void addCircle()
    {

        //Create view to display drawable
        ImageView drawableImage = new ImageView(this);
        drawableImage.setImageDrawable(AppCompatResources.getDrawable(this,R.drawable.image_circle));
        drawableImage.setScaleType(ImageView.ScaleType.MATRIX); // Essential scaling type for PINCH to ZOOM feature

        //Define listeners for touch events.
        ScaleGestureDetector GSD = new ScaleGestureDetector(this, new ScaleListener(drawableImage));
        drawableImage.setOnTouchListener(getShapeTouchListener(drawableImage, GSD));

        // Center the drawable by placing layout parameters on it
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(300, 300);
        params.leftMargin = shapeContainer.getWidth()/2 - 150;
        params.topMargin = shapeContainer.getHeight()/2 - 150;

        shapeContainer.addView(drawableImage, params);

    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        private float scale = 1f;   // Scale variable for scaling intensity
        private ImageView imageDrawable; // The image drawable this listener is scaling
        private Matrix matrix;

        public ScaleListener(ImageView imageDrawable)
        {

            this.imageDrawable = imageDrawable;
            matrix = new Matrix();

        }

        @Override
        public boolean onScale(ScaleGestureDetector detector)
        {

            System.out.println("Scaling : " + detector.getScaleFactor());

            scale *= detector.getScaleFactor();
            scale = Math.max(0.5f, Math.min(scale, 10f));
            matrix.setScale(scale, scale); // Scale the x and y the same amount
            imageDrawable.setScaleX(scale);
            imageDrawable.setScaleY(scale);
            return true;

        }

    }

    private float dX;
    private float dY;
    private int lastEvent;
    private View.OnTouchListener getShapeTouchListener(ImageView drawableImage, ScaleGestureDetector GSD)
    {

        return new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {

                GSD.onTouchEvent(event);

                switch(event.getAction())
                {

                    case MotionEvent.ACTION_DOWN:
                        lastEvent = MotionEvent.ACTION_DOWN;
                        dX = drawableImage.getX() - event.getRawX();
                        dY = drawableImage.getY() - event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        lastEvent = MotionEvent.ACTION_MOVE;
                        drawableImage.setX(event.getRawX() + dX);
                        drawableImage.setY(event.getRawY() + dY);
                        break;
                }

                return true;
            }
        };

    }

    @Override
    public void onBackPressed()
    {

        return;

    }
}

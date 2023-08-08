package sgs.env.ecabsdriver.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import androidx.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;

import sgs.env.ecabsdriver.R;
import sgs.env.ecabsdriver.animations.AntimationHelper;
import sgs.env.ecabsdriver.animations.EnumHolder;

public class FloatingViewService extends Service {

    private WindowManager mWindowManager;
    private View mFloatingView;
    private ImageView imageView;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //Inflate the floating view layout when the user clicks on the start button it will take him to google maps navigation
        mFloatingView = LayoutInflater.from(this).inflate(R.layout.layout_floating_view, null);

        //Add the view to the window.
        int layoutFlag;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutFlag = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutFlag = WindowManager.LayoutParams.TYPE_PHONE;
        }
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                layoutFlag,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        //Specify the view position
        params.gravity = Gravity.CENTER | Gravity.LEFT;        //Initially view will be added to left-center corner
        params.x = 0;
        params.y = 100;

        //Add the view to the window
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        if(mFloatingView != null) {
            mWindowManager.addView(mFloatingView, params);
        }

        imageView = mFloatingView.findViewById( R.id.floatinglogo);

        AntimationHelper.with(EnumHolder.Pulse).duration(1500).
                interpolate(new AccelerateDecelerateInterpolator())
                .repeat(3000).
                setView(mFloatingView.findViewById(R.id.floatinglogo));

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent("com.sgs.NAVIGATION");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

                //close the service and remove view from the view hierarchy
                stopSelf();
            }
        });

        // in order to move the icon on the screen all around the screen even though user is opperating on other apps
        mFloatingView.findViewById( R.id.root_container).setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        //remember the initial position.
                        initialX = params.x;
                        initialY = params.y;

                        //get the touch location
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        v.performClick();
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        //Calculate the X and Y coordinates of the view.
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);

                        //Update the layout with new X & Y coordinate
                        mWindowManager.updateViewLayout(mFloatingView, params);
                        v.performClick();
                        return true;

                    case MotionEvent.ACTION_BUTTON_PRESS:

                    case MotionEvent.ACTION_BUTTON_RELEASE:
                        // go the maps activity when user clicks on the icon
                        Intent intent = new Intent("com.sgs.NAVIGATION");
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);

                        //close the service and remove view from the view hierarchy
                        stopSelf();
                        break;

                    //close the service and remove view from the view hierarchy

                    case MotionEvent.ACTION_UP:
                        int Xdiff = (int) (event.getRawX() - initialTouchX);
                        int Ydiff = (int) (event.getRawY() - initialTouchY);

                        //The check for Xdiff <10 && YDiff< 10 because sometime elements moves a little while clicking.
                        //So that is click event.
                        if (Xdiff < 10 && Ydiff < 10) {
                            mWindowManager.updateViewLayout(mFloatingView, params);
                        }
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFloatingView != null) mWindowManager.removeView(mFloatingView);
    }
}


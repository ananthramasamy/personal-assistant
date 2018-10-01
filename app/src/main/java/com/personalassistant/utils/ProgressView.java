package com.personalassistant.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewManager;
import android.view.ViewParent;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;

import com.personalassistant.BuildConfig;
import com.personalassistant.R;


public class ProgressView extends View {

    private static final String TAG = "SplashView";


    public static interface ISplashListener {
        public void onStart();

        public void onUpdate(float completionFraction);

        public void onEnd();
    }


    public ProgressView(Context context) {
        super(context);
        initialize();
    }


    public ProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
        setupAttributes(attrs);
    }


    public ProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
        setupAttributes(attrs);
    }

    public static final boolean DEFAULT_REMOVE_FROM_PARENT_ON_END = true;
    public static final int DEFAULT_ROTATION_RADIUS = 90; // do not leave this dimension as default since it is in px
    public static final int DEFAULT_CIRCLE_RADIUS = 18; // do not leave this dimension as default since it is in px
    public static final int DEFAULT_SPLASH_BG_COLOR = Color.WHITE;
    public static final int DEFAULT_ROTATION_DURATION = 1200; // ms
    public static final int DEFAULT_SPLASH_DURATION = 1200; // ms
    private boolean mRemoveFromParentOnEnd = true; // a flag for removing the view from its parent once the animation is over
    private float mRotationRadius = DEFAULT_ROTATION_RADIUS; // the radius of the large circle
    private float mCircleRadius = DEFAULT_CIRCLE_RADIUS; // the radius of each individual small circle
    private int[] mCircleColors; // the color list of the circles, no default is provided here
    private long mRotationDuration = DEFAULT_ROTATION_DURATION; // the duration, in ms, for one complete rotation of the circles
    private long mSplashDuration = DEFAULT_SPLASH_DURATION; // the duration, in ms, for the splash animation to go away
    private int mSplashBgColor; // the color of the background, the default is set in initialize()
    private ISplashListener mSplashListener; // reference to the listener for the splash events
    private float mHoleRadius = 0F;
    private float mCurrentRotationAngle = 0F;
    private float mCurrentRotationRadius;
    private float mCurrentSingleCircleRadius;
    private SplashState mState = null;
    private Paint mPaint = new Paint();
    private Paint mPaintBackground = new Paint();
    private float mCenterX;
    private float mCenterY;
    private float mDiagonalDist;


    private void setupAttributes(AttributeSet attrs) {
        Context context = getContext();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ProgressView);
        int numAttrs = a.getIndexCount();
        for (int i = 0; i < numAttrs; ++i) {
            int attr = a.getIndex(i);
            switch (attr) {
                case R.styleable.ProgressView_removeFromParentOnEnd:
                    setRemoveFromParentOnEnd(a.getBoolean(i, DEFAULT_REMOVE_FROM_PARENT_ON_END));
                    break;
                case R.styleable.ProgressView_circleRadius:
                    setCircleRadius(a.getDimensionPixelSize(i, DEFAULT_CIRCLE_RADIUS));
                    break;
                case R.styleable.ProgressView_rotationRadius:
                    setRotationRadius(a.getDimensionPixelSize(i, DEFAULT_ROTATION_RADIUS));
                    break;
                case R.styleable.ProgressView_rotationDuration:
                    setRotationDuration(a.getInteger(i, DEFAULT_ROTATION_DURATION));
                    break;
                case R.styleable.ProgressView_splashBackgroundColor:
                    setSplashBackgroundColor(a.getColor(i, DEFAULT_SPLASH_BG_COLOR));
                    break;
                case R.styleable.ProgressView_splashDuration:
                    setSplashDuration(a.getInteger(i, DEFAULT_SPLASH_DURATION));
                    break;
                case R.styleable.ProgressView_circleColors:
                    int arrayId = a.getResourceId(i, -1);
                    if (arrayId >= 0) {
                        // TypedArray does not provide a method for obtaining integer arrays so using resources instead
                        int[] circleColors = getResources().getIntArray(arrayId);
                        if (circleColors != null) {
                            setCircleColors(circleColors);
                        }
                    }
                    break;
            }
        }
        a.recycle();
    }

    private void initialize() {
        setBackgroundColor(Color.TRANSPARENT);
        mPaint.setAntiAlias(true);
        mPaintBackground.setStyle(Paint.Style.STROKE);
        mPaintBackground.setAntiAlias(true);
        setSplashBackgroundColor(DEFAULT_SPLASH_BG_COLOR);
    }

    public void setCircleRadius(float circleRadius) {
        mCircleRadius = circleRadius;
    }

    public void setRotationRadius(float rotationRadius) {
        mRotationRadius = rotationRadius;
    }

    public void setRotationDuration(long duration) {
        mRotationDuration = duration;
    }

    public void setSplashBackgroundColor(int bgColor) {
        mSplashBgColor = bgColor;
        mPaintBackground.setColor(mSplashBgColor);
    }

    public void setSplashDuration(long duration) {
        mSplashDuration = duration;
    }

    public void setCircleColors(int[] circleColors) {
        mCircleColors = circleColors;
    }

    public void setRemoveFromParentOnEnd(boolean shouldRemove) {
        mRemoveFromParentOnEnd = shouldRemove;
    }

    public void splashAndDisappear(final ISplashListener listener) {
        mSplashListener = listener;

        if (mState != null && mState instanceof RotationState) {
            RotationState rotationState = (RotationState) mState;
            rotationState.cancel();
        }

        post(new Runnable() {
            @Override
            public void run() {
                mState = new MergingState();
            }
        });
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mCenterX = w / 2F;
        mCenterY = h / 2F;
        mDiagonalDist = (float) Math.sqrt(w * w + h * h) / 2;
    }

    private void handleFirstDraw() {
        mState = new RotationState();
        mCurrentRotationAngle = 0F;
        mHoleRadius = 0F;
        mCurrentRotationRadius = mRotationRadius;
        mCurrentSingleCircleRadius = mCircleRadius;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mState == null) {
            handleFirstDraw();
        }
        if (mCircleColors == null) {
            mCircleColors = new int[0];
        }
        mState.drawState(canvas);
    }

    private void drawBackground(Canvas canvas) {
        if (mHoleRadius > 0F) {
            float strokeWidth = mDiagonalDist - mHoleRadius;
            float circleRadius = mHoleRadius + strokeWidth / 2;
            mPaintBackground.setStrokeWidth(strokeWidth);
            canvas.drawCircle(mCenterX, mCenterY, circleRadius, mPaintBackground);
        } else {
            canvas.drawColor(mSplashBgColor);
        }
    }

    private void drawCircles(Canvas canvas) {
        int numCircles = mCircleColors.length;
        float rotationAngle = (float) (2 * Math.PI / numCircles);
        for (int i = 0; i < numCircles; ++i) {
            double angle = mCurrentRotationAngle + (i * rotationAngle);
            double circleX = mCenterX + mCurrentRotationRadius * Math.sin(angle);
            double circleY = mCenterY - mCurrentRotationRadius * Math.cos(angle);
            mPaint.setColor(mCircleColors[i]);
            canvas.drawCircle((float) circleX, (float) circleY, mCircleRadius, mPaint);
        }
    }

    private void drawSingleCircle(Canvas canvas) {
        int singleCircleColor = mSplashBgColor;
        int numColors = mCircleColors.length;
        if (numColors > 0) {
            singleCircleColor = mCircleColors[numColors - 1];
        }
        mPaint.setColor(singleCircleColor);
        canvas.drawCircle(mCenterX, mCenterY, mCurrentSingleCircleRadius, mPaint);
    }

    private void removeFromParentIfNecessary() {
        // check if we need to remove the view on animation end
        if (mRemoveFromParentOnEnd) {
            // get the view parent
            ViewParent parent = getParent();
            // check if a parent exists and that it implements the ViewManager interface
            if (parent != null && parent instanceof ViewManager) {
                ViewManager viewManager = (ViewManager) parent;
                // remove the view from its parent
                viewManager.removeView(ProgressView.this);
            } else if (BuildConfig.DEBUG) {
                // even though we had to remove the view we either don't have a parent, or the parent does not implement the method
                // necessary to remove the view, therefore create a warning log (but only do this if we are in DEBUG mode)
                Log.w(TAG, "splash view not removed after animation ended because no ViewManager parent was found");
            }
        }
    }

    private abstract class SplashState {
        public abstract void drawState(Canvas canvas);
    }

    private class RotationState extends SplashState {
        private ValueAnimator mAnimator;

        private RotationState() {
            mAnimator = ValueAnimator.ofFloat(0, (float) (Math.PI * 2));
            // set the requested duration, if the setRotationDuration method is called after this is done, then it will have no effect
            mAnimator.setDuration(mRotationDuration);
            // use a LinearInterpolator to make the animation smooth
            mAnimator.setInterpolator(new LinearInterpolator());
            // add an update listener for updating the necessary values
            mAnimator.addUpdateListener(new AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animator) {
                    // put the animated value into mCurrentRotationAngle
                    mCurrentRotationAngle = (Float) animator.getAnimatedValue();
                    // invalidate the view so that it draws itself again
                    invalidate();
                }
            });
            // make the animation loop infinitely
            mAnimator.setRepeatCount(ValueAnimator.INFINITE);
            // make the animation restart from 0 when done
            mAnimator.setRepeatMode(ValueAnimator.RESTART);
            // start the animation
            mAnimator.start();
        }

        /**
         * Rotation state needs a background and all circles to be drawn
         */
        @Override
        public void drawState(Canvas canvas) {
            drawBackground(canvas);
            drawCircles(canvas);
        }

        /**
         * The animator needs to be canceled on state change, otherwise the rotation angle will keep changing and the view will leak
         * even after it is destroyed. This method takes care of that, but is not a very nice way of doing so since it requires typecasting
         * in the splashAndDisappear method
         */
        private void cancel() {
            mAnimator.cancel();
            mAnimator = null;
        }
    }

    private class MergingState extends SplashState {

        /**
         * The constructor takes care of creating, setting up, and starting the animator
         */
        private MergingState() {
            // Make an animator from 0 (center) to rotation radius, the animator will be used in reverse
            ValueAnimator animator = ValueAnimator.ofFloat(0, mRotationRadius);
            // set the duration to a third of the total duration
            animator.setDuration(mSplashDuration / 3);
            // use an overshoot interpolator to make the circles bounce out before falling into 0
            animator.setInterpolator(new OvershootInterpolator(6F));
            // add an update listener to update draw
            animator.addUpdateListener(new AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animator) {
                    // get the animation value into current rotation radius for the circles to be drawn at
                    mCurrentRotationRadius = (Float) animator.getAnimatedValue();
                    // invalidate the view to force draw
                    invalidate();

                    // if we have a listener, then update it during the first third of the animation
                    // this is bad practice since adding another animation state will require this code to be changed
                    if (mSplashListener != null) {
                        mSplashListener.onUpdate((float) animator.getCurrentPlayTime() / animator.getDuration() / 3);
                    }
                }
            });
            // we need some animation state listeners
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animator) {
                    // inform the listener of splash start, since this is the first splash state
                    if (mSplashListener != null) {
                        mSplashListener.onStart();
                    }
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    // change the state to the next splash state in line
                    mState = new SingularityState();
                }
            });
            // run the animation in reverse to get the bounce out then back in effect
            animator.reverse();
        }

        /**
         * Merging requires the background and all circles to be drawn
         */
        @Override
        public void drawState(Canvas canvas) {
            drawBackground(canvas);
            drawCircles(canvas);
        }
    }

    /**
     * This state is used to make the only circle visible at this time get a little larger then disappear into a single point
     *
     * @author yildizkabaran
     */
    private class SingularityState extends SplashState {

        /**
         * The constructor takes care of creating, setting up, and starting the animator
         */
        private SingularityState() {
            // get a value animator from 0 to the radius of each circle, the animator will be used in reverse
            ValueAnimator animator = ValueAnimator.ofFloat(0, mCircleRadius);
            // set the duration to a third of the total duration
            animator.setDuration(mSplashDuration / 3);
            // use an overshoot interpolator to make the circles bounce out before falling into 0
            animator.setInterpolator(new OvershootInterpolator(6F));
            // add an update listener to update draw
            animator.addUpdateListener(new AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animator) {
                    // get the animation value into current single circle radius
                    mCurrentSingleCircleRadius = (Float) animator.getAnimatedValue();
                    // invalidate the view to force draw
                    invalidate();

                    // if we have a listener, then update it during the second third of the animation
                    // this is bad practice since adding another animation state will require this code to be changed
                    if (mSplashListener != null) {
                        mSplashListener.onUpdate(1F / 3 + (float) animator.getCurrentPlayTime() / animator.getDuration() / 3);
                    }
                }
            });
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    // change the state to the next splash state in line
                    mState = new ExpandingState();
                }
            });
            // run the animation in reverse to get the enlarge then disappear
            animator.reverse();
        }

        /**
         * SinglularityState requires a background and a single circle to be drawn
         */
        @Override
        public void drawState(Canvas canvas) {
            drawBackground(canvas);
            drawSingleCircle(canvas);
        }
    }

    /**
     * This state uses an animator to draw an increasingly larger transparent hole in the view
     *
     * @author yildizkabaran
     */
    private class ExpandingState extends SplashState {

        /**
         * The constructor takes care of creating, setting up, and starting the animator
         */
        private ExpandingState() {
            // get an animator from 0 to the half diagonal distance of the view
            ValueAnimator animator = ValueAnimator.ofFloat(0, mDiagonalDist);
            // set the duration to a third of the total duration
            animator.setDuration(mSplashDuration / 3);
            // use a decelerate interpolator to give the effect that the transparent hole went into a bang
            animator.setInterpolator(new DecelerateInterpolator());
            // add an update listener to update draw
            animator.addUpdateListener(new AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animator) {
                    // get the animated value into the radius of the transparent hole
                    mHoleRadius = (Float) animator.getAnimatedValue();
                    // invalidate the view to force draw
                    invalidate();

                    // if we have a listener, then update it during the last third of the animation
                    // this is bad practice since adding another animation state will require this code to be changed
                    if (mSplashListener != null) {
                        mSplashListener.onUpdate(2F / 3 + (float) animator.getCurrentPlayTime() / animator.getDuration() / 3);
                    }
                }
            });
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    // the splash is over so remove from parent if needed
                    removeFromParentIfNecessary();

                    // notify the listener that we are done
                    if (mSplashListener != null) {
                        mSplashListener.onEnd();
                    }
                }
            });
            // start the animation in forward direction
            animator.start();
        }

        /**
         * The ExpandingState only needs a background
         */
        @Override
        public void drawState(Canvas canvas) {
            drawBackground(canvas);
        }
    }
}

package io.harry.doodlenow.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.Random;

import io.harry.doodlenow.R;

public class HeartEffectView extends FrameLayout {

    private static final int HEART_SIZE_DP = 40;
    private static final int ANIMATION_DURATION = 1000;
    private static final long DRAG_HEART_INTERVAL_MS = 100;

    private Random random = new Random();
    private int heartSizePx;
    private long lastHeartTime = 0;

    public HeartEffectView(Context context) {
        super(context);
        init();
    }

    public HeartEffectView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HeartEffectView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        heartSizePx = (int) (HEART_SIZE_DP * getResources().getDisplayMetrics().density);
        setClickable(true);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                createHeartAt(x, y);
                createHeartAt(x, y);
                return true;

            case MotionEvent.ACTION_MOVE:
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastHeartTime >= DRAG_HEART_INTERVAL_MS) {
                    createHeartAt(x, y);
                    lastHeartTime = currentTime;
                }
                return true;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                lastHeartTime = 0;
                return true;
        }

        return super.onTouchEvent(event);
    }

    private void createHeartAt(float x, float y) {
        final ImageView heartView = new ImageView(getContext());
        Drawable heartDrawable = ContextCompat.getDrawable(getContext(), R.drawable.heart);
        heartView.setImageDrawable(heartDrawable);

        LayoutParams params = new LayoutParams(heartSizePx, heartSizePx);
        heartView.setLayoutParams(params);

        int offsetX = random.nextInt(40) - 20;
        int offsetY = random.nextInt(40) - 20;

        heartView.setX(x - heartSizePx / 2 + offsetX);
        heartView.setY(y - heartSizePx / 2 + offsetY);

        heartView.setScaleX(0f);
        heartView.setScaleY(0f);
        heartView.setAlpha(1f);

        addView(heartView);

        animateHeart(heartView);
    }

    private void animateHeart(final ImageView heartView) {
        float startY = heartView.getY();
        float endY = startY - 200 - random.nextInt(100);

        float startX = heartView.getX();
        float endX = startX + (random.nextInt(100) - 50);

        float rotation = random.nextInt(60) - 30;

        ObjectAnimator scaleUpX = ObjectAnimator.ofFloat(heartView, View.SCALE_X, 0f, 1.2f);
        ObjectAnimator scaleUpY = ObjectAnimator.ofFloat(heartView, View.SCALE_Y, 0f, 1.2f);
        scaleUpX.setDuration(150);
        scaleUpY.setDuration(150);
        scaleUpX.setInterpolator(new DecelerateInterpolator());
        scaleUpY.setInterpolator(new DecelerateInterpolator());

        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(heartView, View.SCALE_X, 1.2f, 1f);
        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(heartView, View.SCALE_Y, 1.2f, 1f);
        scaleDownX.setDuration(100);
        scaleDownY.setDuration(100);
        scaleDownX.setStartDelay(150);
        scaleDownY.setStartDelay(150);

        ObjectAnimator translateY = ObjectAnimator.ofFloat(heartView, View.TRANSLATION_Y, 0, endY - startY);
        translateY.setDuration(ANIMATION_DURATION);
        translateY.setInterpolator(new DecelerateInterpolator());

        ObjectAnimator translateX = ObjectAnimator.ofFloat(heartView, View.TRANSLATION_X, 0, endX - startX);
        translateX.setDuration(ANIMATION_DURATION);

        ObjectAnimator rotateAnimator = ObjectAnimator.ofFloat(heartView, View.ROTATION, 0, rotation);
        rotateAnimator.setDuration(ANIMATION_DURATION);

        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(heartView, View.ALPHA, 1f, 0f);
        fadeOut.setDuration(300);
        fadeOut.setStartDelay(ANIMATION_DURATION - 300);
        fadeOut.setInterpolator(new AccelerateInterpolator());

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleUpX, scaleUpY, scaleDownX, scaleDownY,
                translateY, translateX, rotateAnimator, fadeOut);

        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                removeView(heartView);
            }
        });

        animatorSet.start();
    }
}

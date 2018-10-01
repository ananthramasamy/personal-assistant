package com.personalassistant.utils;

import android.view.animation.AccelerateDecelerateInterpolator;


public interface BounceViewAnim {

    BounceViewAnim setScaleForPushInAnim(float scaleX, float scaleY);

    BounceViewAnim setScaleForPopOutAnim(float scaleX, float scaleY);

    BounceViewAnim setPushInAnimDuration(int timeInMillis);

    BounceViewAnim setPopOutAnimDuration(int timeInMillis);

    BounceViewAnim setInterpolatorPushIn(AccelerateDecelerateInterpolator interpolatorPushIn);

    BounceViewAnim setInterpolatorPopOut(AccelerateDecelerateInterpolator interpolatorPopOut);

}

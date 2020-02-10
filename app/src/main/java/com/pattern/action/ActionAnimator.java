package com.pattern.action;

import android.view.View;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

/**
 * Action response with animation sample.
 */
public class ActionAnimator {
    public static void press(View touchView) {
        YoYo.with(Techniques.Tada)
                .duration(700)
                .repeat(5)
                .playOn(touchView);
    }

    public static void popup(View touchView) {
    }

    public static void tap(View touchView) {
        YoYo.with(Techniques.BounceIn)
                .duration(700)
                .repeat(5)
                .playOn(touchView);
    }

    public static void scroll(View touchView) {

    }

    public static void longPress(View touchView) {
        YoYo.with(Techniques.Hinge)
                .duration(700)
                .repeat(5)
                .playOn(touchView);
    }

    public static void swipeRight(View touchView) {
        YoYo.with(Techniques.BounceInRight)
                .duration(700)
                .repeat(5)
                .playOn(touchView);
    }

    public static void swipeLeft(View touchView) {
        YoYo.with(Techniques.BounceInLeft)
                .duration(700)
                .repeat(5)
                .playOn(touchView);
    }

    public static void swipeDown(View touchView) {
        YoYo.with(Techniques.BounceInDown)
                .duration(700)
                .repeat(5)
                .playOn(touchView);
    }

    public static void swipeUp(View touchView) {
        YoYo.with(Techniques.BounceInUp)
                .duration(700)
                .repeat(5)
                .playOn(touchView);
    }
}

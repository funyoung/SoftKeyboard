package com.pattern;

import android.inputmethodservice.Keyboard;

import com.pattern.action.ActionAnimator;
import com.pattern.action.invoker.ActionInvoker;

public class ActionInvokerAdapter implements ActionInvoker {
    private final KeyboardDelegate keyboardDelegate;

    public ActionInvokerAdapter(KeyboardDelegate keyboardDelegate) {
        this.keyboardDelegate = keyboardDelegate;
    }

    @Override
    public boolean onPress(Keyboard keyboard, Keyboard.Key key) {
        ActionAnimator.press(keyboardDelegate.getCurrentView());
        if (null != key && null != keyboardDelegate) {
            keyboardDelegate.onPress(key.codes[0]);
            return true;
        }

        return false;
    }

    @Override
    public boolean onKey(Keyboard keyboard, Keyboard.Key key) {
        ActionAnimator.tap(keyboardDelegate.getCurrentView());
        if (null != key && null != keyboardDelegate) {
            keyboardDelegate.onKey(key.codes[0], key.codes);
            return true;
        }
        return false;
    }

    @Override
    public boolean onLongPressed(Keyboard keyboard, Keyboard.Key key) {
        ActionAnimator.longPress(keyboardDelegate.getCurrentView());
        return true;
    }

    @Override
    public boolean swipeLeft(Keyboard keyboard, Keyboard.Key key) {
        ActionAnimator.swipeLeft(keyboardDelegate.getCurrentView());
        return false;
    }

    @Override
    public boolean swipeRight(Keyboard keyboard, Keyboard.Key key) {
        ActionAnimator.swipeRight(keyboardDelegate.getCurrentView());
        return false;
    }

    @Override
    public boolean swipeUp(Keyboard keyboard, Keyboard.Key key) {
        ActionAnimator.swipeUp(keyboardDelegate.getCurrentView());
        return false;
    }

    @Override
    public boolean swipeDown(Keyboard keyboard, Keyboard.Key key) {
        ActionAnimator.swipeDown(keyboardDelegate.getCurrentView());
        return false;
    }
}

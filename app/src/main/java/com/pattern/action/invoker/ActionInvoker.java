package com.pattern.action.invoker;

import android.inputmethodservice.Keyboard;

/**
 * The interface to all response implement item.
 */
public interface ActionInvoker {
    boolean onPress(Keyboard keyboard, Keyboard.Key key);

    boolean onKey(Keyboard keyboard, Keyboard.Key key);

    boolean onLongPressed(Keyboard keyboard, Keyboard.Key key);

    boolean swipeLeft(Keyboard keyboard, Keyboard.Key key);

    boolean swipeRight(Keyboard keyboard, Keyboard.Key key);

    boolean swipeUp(Keyboard keyboard, Keyboard.Key key);

    boolean swipeDown(Keyboard keyboard, Keyboard.Key key);
}

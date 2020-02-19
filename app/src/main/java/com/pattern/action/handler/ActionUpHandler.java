package com.pattern.action.handler;

import android.inputmethodservice.Keyboard;

/**
 * Handler for key press action.
 */
public class ActionUpHandler extends ActionHandler {
    @Override
    public boolean execute(Keyboard keyboard, Keyboard.Key key) {
        return actionInvoker.swipeUp(keyboard, key);
    }
}

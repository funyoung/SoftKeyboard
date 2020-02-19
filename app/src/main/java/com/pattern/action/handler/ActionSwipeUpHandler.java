package com.pattern.action.handler;

import android.inputmethodservice.Keyboard;

public class ActionSwipeUpHandler extends ActionHandler {
    @Override
    public boolean execute(Keyboard keyboard, Keyboard.Key key) {
        return actionInvoker.swipeUp(keyboard, key);
    }
}

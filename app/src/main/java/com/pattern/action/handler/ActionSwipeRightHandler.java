package com.pattern.action.handler;

import android.inputmethodservice.Keyboard;

public class ActionSwipeRightHandler extends ActionHandler {
    @Override
    public boolean execute(Keyboard keyboard, Keyboard.Key key) {
        return actionInvoker.swipeRight(keyboard, key);
    }
}

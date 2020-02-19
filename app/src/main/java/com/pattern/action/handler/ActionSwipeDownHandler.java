package com.pattern.action.handler;

import android.inputmethodservice.Keyboard;

public class ActionSwipeDownHandler extends ActionHandler {
    @Override
    public boolean execute(Keyboard keyboard, Keyboard.Key key) {
        return actionInvoker.swipeDown(keyboard, key);
    }
}

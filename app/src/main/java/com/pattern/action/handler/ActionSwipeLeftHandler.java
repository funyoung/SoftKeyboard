package com.pattern.action.handler;

import android.inputmethodservice.Keyboard;

public class ActionSwipeLeftHandler extends ActionHandler {
    @Override
    public boolean execute(Keyboard keyboard, Keyboard.Key key) {
        return actionInvoker.swipeLeft(keyboard, key);
    }
}

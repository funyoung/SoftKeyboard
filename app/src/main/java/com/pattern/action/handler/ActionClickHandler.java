package com.pattern.action.handler;

import android.inputmethodservice.Keyboard;

public class ActionClickHandler extends ActionHandler {
    @Override
    public boolean execute(Keyboard keyboard, Keyboard.Key key) {
        return actionInvoker.onKey(keyboard, key);
    }
}

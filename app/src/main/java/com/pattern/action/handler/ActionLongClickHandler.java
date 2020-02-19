package com.pattern.action.handler;

import android.inputmethodservice.Keyboard;

/**
 * Handler for key press action.
 */
public class ActionLongClickHandler extends ActionHandler {
    @Override
    public boolean execute(Keyboard keyboard, Keyboard.Key key) {
        return actionInvoker.onLongPressed(keyboard, key);
    }
}

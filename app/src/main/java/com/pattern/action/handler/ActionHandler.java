package com.pattern.action.handler;

import android.inputmethodservice.Keyboard;

import java.security.Key;

public interface ActionHandler {
    ActionHandler EMPTY_HANDLER = new ActionHandler() {
        @Override
        public void execute(Keyboard keyboard, Key key) {
        }
    };

    void execute(Keyboard keyboard, Key key);
}

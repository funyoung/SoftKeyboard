package com.pattern.action.handler;

import android.inputmethodservice.Keyboard;

import com.pattern.action.invoker.ActionInvoker;

/**
 * Abstract class to decouple action response interface (ActionInvoker) from its
 * client (Keyboard view, etc).
 *
 * Important: execute MUST be set before any call to execute method.
 */
public abstract class ActionHandler {
    public static final ActionHandler EMPTY_HANDLER = new ActionHandler() {
        @Override
        public boolean execute(Keyboard keyboard, Keyboard.Key key) {
            return true;
        }
    };

    protected ActionInvoker actionInvoker;

    public abstract boolean execute(Keyboard keyboard, Keyboard.Key key);

    public void setActionInvoker(ActionInvoker invoker) {
        this.actionInvoker = invoker;
    }
}

package com.pattern.action.factory;

import android.inputmethodservice.Keyboard;

import com.pattern.action.handler.ActionClickHandler;
import com.pattern.action.handler.ActionConstant;
import com.pattern.action.handler.ActionDownHandler;
import com.pattern.action.handler.ActionHandler;
import com.pattern.action.handler.ActionLongClickHandler;
import com.pattern.action.handler.ActionSwipeDownHandler;
import com.pattern.action.handler.ActionSwipeLeftHandler;
import com.pattern.action.handler.ActionSwipeRightHandler;
import com.pattern.action.handler.ActionSwipeUpHandler;
import com.pattern.action.handler.ActionUpHandler;
import com.pattern.action.invoker.ActionInvoker;
import com.pattern.action.plugin.ActionPlugin;

import java.util.HashMap;

/**
 * Create handlers instances for each actions, with the specifics of plugin, which
 * disable or enable some of the handlers. It also manages and maintains these instance.
 */
public class ActionHandlerFactory implements ActionConstant {
    private final HashMap<String, ActionHandler> handlerMap = new HashMap<>();

    private ActionInvoker actionInvoker;

    public void setActionInvoker(ActionInvoker invoker) {
        this.actionInvoker = invoker;
        for (String key : handlerMap.keySet()) {
            handlerMap.get(key).setActionInvoker(invoker);
        }
    }

    public boolean onDown(Keyboard keyboard, Keyboard.Key key) {
        return execute(actionDown, keyboard, key);
    }

    public boolean onClick(Keyboard keyboard, Keyboard.Key key) {
        return execute(actionClicked, keyboard, key);
    }

    public void onLongClick(Keyboard keyboard, Keyboard.Key key) {
        execute(actionLongClicked, keyboard, key);
    }

    public boolean swipeLeft(Keyboard keyboard, Keyboard.Key key) {
        return execute(actionSwipeLeft, keyboard, key);
    }

    public boolean swipeRight(Keyboard keyboard, Keyboard.Key key) {
        return execute(actionSwipeRight, keyboard, key);
    }

    public boolean swipeUp(Keyboard keyboard, Keyboard.Key key) {
        return execute(actionSwipeUp, keyboard, key);
    }

    public boolean swipeDown(Keyboard keyboard, Keyboard.Key key) {
        return execute(actionSwipeDown, keyboard, key);
    }

    private boolean execute(String action, Keyboard keyboard, Keyboard.Key key) {
        return handlerMap.get(action).execute(keyboard, key);
    }

    private static class Instance {
        private static final ActionHandlerFactory INSTNCE = new ActionHandlerFactory();
    }

    public static ActionHandlerFactory getInstance() {
        return Instance.INSTNCE;
    }

    private ActionHandlerFactory() {
        reload(ActionPlugin.DEFAULT_PLUGIN);
    }

    public void reload(ActionPlugin plugin) {
        reload(actionDown, plugin.actionDownEnabled() ? new ActionDownHandler() : ActionHandler.EMPTY_HANDLER);
        reload(actionUp, plugin.actionUpEnabled() ? new ActionUpHandler() : ActionHandler.EMPTY_HANDLER);
        reload(actionClicked, plugin.actionClickedEnabled() ? new ActionClickHandler() : ActionHandler.EMPTY_HANDLER);
        reload(actionLongClicked, plugin.actionLongClickedEnabled() ? new ActionLongClickHandler() : ActionHandler.EMPTY_HANDLER);
        reload(actionSwipeLeft, plugin.actionSwipeLeftEnabled() ? new ActionSwipeLeftHandler() : ActionHandler.EMPTY_HANDLER);
        reload(actionSwipeRight, plugin.actionSwipeRightEnabled() ? new ActionSwipeRightHandler() : ActionHandler.EMPTY_HANDLER);
        reload(actionSwipeUp, plugin.actionSwipeUpEnabled() ? new ActionSwipeUpHandler() : ActionHandler.EMPTY_HANDLER);
        reload(actionSwipeDown, plugin.actionSwipeDownEnabled() ? new ActionSwipeDownHandler() : ActionHandler.EMPTY_HANDLER);
    }

    private void reload(String action, ActionHandler actionHandler) {
        actionHandler.setActionInvoker(actionInvoker);
        handlerMap.put(action, actionHandler);
    }
}

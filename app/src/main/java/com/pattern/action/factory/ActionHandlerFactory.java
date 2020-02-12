package com.pattern.action.factory;

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
import com.pattern.action.plugin.ActionPlugin;

import java.util.HashMap;

/**
 * Create handlers instances for each actions, with the specifics of plugin, which
 * disable or enable some of the handlers. It also manages and maintains these instance.
 */
public class ActionHandlerFactory implements ActionConstant {
    private final HashMap<String, ActionHandler> handlerMap = new HashMap<>();

    private static class Instance {
        private static final ActionHandlerFactory INSTNCE = new ActionHandlerFactory();
    }
    public ActionHandlerFactory getInstance() {
        return Instance.INSTNCE;
    }

    private ActionHandlerFactory() {
    }

    public void reload(ActionPlugin plugin) {
        handlerMap.put(actionDown, plugin.actionDownEnabled() ? new ActionDownHandler() : ActionHandler.EMPTY_HANDLER);
        handlerMap.put(actionUp, plugin.actionUpEnabled() ? new ActionUpHandler() : ActionHandler.EMPTY_HANDLER);
        handlerMap.put(actionClicked, plugin.actionClickedEnabled() ? new ActionClickHandler() : ActionHandler.EMPTY_HANDLER);
        handlerMap.put(actionLongClicked, plugin.actionLongClickedEnabled() ? new ActionLongClickHandler() : ActionHandler.EMPTY_HANDLER);
        handlerMap.put(actionSwipeLeft, plugin.actionSwipeLeftEnabled() ? new ActionSwipeLeftHandler() : ActionHandler.EMPTY_HANDLER);
        handlerMap.put(actionSwipeRight, plugin.actionSwipeRightEnabled() ? new ActionSwipeRightHandler() : ActionHandler.EMPTY_HANDLER);
        handlerMap.put(actionSwipeUp, plugin.actionSwipeUpEnabled() ? new ActionSwipeUpHandler() : ActionHandler.EMPTY_HANDLER);
        handlerMap.put(actionSwipeDown, plugin.actionSwipeDownEnabled() ? new ActionSwipeDownHandler() : ActionHandler.EMPTY_HANDLER);
    }
}

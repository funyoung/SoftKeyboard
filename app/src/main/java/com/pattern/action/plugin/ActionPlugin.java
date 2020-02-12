package com.pattern.action.plugin;

/**
 * Action Plugin is used to configure which action is enabled, so
 * it could be dynamically changed within code, local or remote
 * configuration files.
 */
public interface ActionPlugin {
    ActionPlugin DEFAULT_PLUGIN = new ActionPlugin() {
        @Override
        public boolean actionDownEnabled() {
            return true;
        }

        @Override
        public boolean actionUpEnabled() {
            return true;
        }

        @Override
        public boolean actionClickedEnabled() {
            return true;
        }

        @Override
        public boolean actionLongClickedEnabled() {
            return true;
        }

        @Override
        public boolean actionSwipeLeftEnabled() {
            return true;
        }

        @Override
        public boolean actionSwipeRightEnabled() {
            return true;
        }

        @Override
        public boolean actionSwipeUpEnabled() {
            return true;
        }

        @Override
        public boolean actionSwipeDownEnabled() {
            return true;
        }
    };

    boolean actionDownEnabled();
    boolean actionUpEnabled();
    boolean actionClickedEnabled();
    boolean actionLongClickedEnabled();
    boolean actionSwipeLeftEnabled();
    boolean actionSwipeRightEnabled();
    boolean actionSwipeUpEnabled();
    boolean actionSwipeDownEnabled();
}

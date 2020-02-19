/*
 * Copyright (C) 2008-2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.softkeyboard;

import android.content.Context;
import android.graphics.Color;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.Keyboard.Key;
import android.inputmethodservice.KeyboardView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodSubtype;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.pattern.action.ActionAnimator;
import com.pattern.action.ActionConf;
import com.pattern.action.factory.ActionHandlerFactory;

import java.util.List;

public class SimpleKeyboardView extends FrameLayout {
    private static final String TAG = SimpleKeyboardView.class.getSimpleName();

    public SimpleKeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SimpleKeyboardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

//    @Override
//    protected boolean onLongPress(Key key) {
//        if (key.codes[0] == Keyboard.KEYCODE_CANCEL) {
//            getOnKeyboardActionListener().onKey(KEYCODE_OPTIONS, null);
//            return true;
//        } else {
//            return super.onLongPress(key);
//        }
//    }

    public void setSubtypeOnSpaceKey(final InputMethodSubtype subtype) {
        final LatinKeyboard keyboard = (LatinKeyboard)getKeyboard();
        keyboard.setSpaceIcon(getResources().getDrawable(subtype.getIconResId()));
        invalidateAllKeys();
    }

    public void setKeyboard(Keyboard keyboard) {
        if (keyboard != mKeyboard) {
            removeAllViews();
        }

        mKeyboard = keyboard;
        List<Key> keys = mKeyboard.getKeys();

        int maxWidth = getResources().getDisplayMetrics().widthPixels;
        for (Key k : keys) {
            addView(buildKey(k));
            if (maxWidth < 0) {
                break;
            }
        }
        invalidateAllKeys();
    }

    private View buildKey(Key k) {
        FrameLayout view = (FrameLayout) LayoutInflater.from(getContext()).inflate(R.layout.key, null);;
        if (null != k.icon) {
            ImageView keyView = view.findViewById(R.id.keyIcon);
            keyView.setImageDrawable(k.icon);
        } else {
            TextView keyView = view.findViewById(R.id.keyLabel);
            keyView.setText(k.label);
            keyView.setTextColor(Color.BLACK);
            keyView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 40);
        }

//        view.setBackgroundColor(Color.GREEN);
        LayoutParams params = new LayoutParams(k.width, k.height);
        params.leftMargin = k.x;
        params.topMargin = k.y;
        view.setLayoutParams(params);

        attachGesture(view, k);
        return view;
    }

    private Keyboard mKeyboard;
    /**
     * Returns the current keyboard being displayed by this view.
     * @return the currently attached keyboard
     * @see #setKeyboard(Keyboard)
     */
    public Keyboard getKeyboard() {
        return mKeyboard;
    }

    public void closing() {

    }
    public boolean setShifted(boolean shifted) {
        if (mKeyboard != null) {
            if (mKeyboard.setShifted(shifted)) {
                // The whole keyboard probably needs to be redrawn
                invalidateAllKeys();
                return true;
            }
        }
        return false;
    }

    /**
     * Requests a redraw of the entire keyboard. Calling {@link #invalidate} is not sufficient
     * because the keyboard renders the keys to an off-screen buffer and an invalidate() only
     * draws the cached buffer.
     * @see #invalidateKey(int)
     */
    public void invalidateAllKeys() {
//        mDirtyRect.union(0, 0, getWidth(), getHeight());
//        mDrawPending = true;
        invalidate();
    }

    /**
     * Returns the state of the shift key of the keyboard, if any.
     * @return true if the shift is in a pressed state, false otherwise. If there is
     * no shift key on the keyboard or there is no keyboard attached, it returns false.
     * @see KeyboardView#setShifted(boolean)
     */
    public boolean isShifted() {
        if (mKeyboard != null) {
            return mKeyboard.isShifted();
        }
        return false;
    }

    public boolean handleBack() {
//        if (mPopupKeyboard.isShowing()) {
//            dismissPopupKeyboard();
//            return true;
//        }
        return false;
    }

    private final GestureDetector.OnGestureListener gestureListener = new GestureDetector.OnGestureListener() {
        @Override
        public boolean onDown(MotionEvent e) {
            return ActionHandlerFactory.getInstance().onDown(getKeyboard(), getTouchKey());
        }

        @Override
        public void onShowPress(MotionEvent e) {
//            Key key = getTouchKey();
//            if (null != key && null != mKeyboardActionListener) {
//                mKeyboardActionListener.onText(key.label);
//            }
//            ActionAnimator.popup(touchView);
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return ActionHandlerFactory.getInstance().onClick(getKeyboard(), getTouchKey());
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
//            ActionAnimator.scroll(touchView);
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            ActionHandlerFactory.getInstance().onLongClick(getKeyboard(), getTouchKey());
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            Key key = getTouchKey();
            if (null != key) {
                float dx = e2.getX() - e1.getX();
                float dy = e2.getY() - e1.getY();
                if (Math.abs(dx) > Math.abs(dy)) {
                    if (dx > ActionConf.SWIPE_THRESHOLD) {
                        return ActionHandlerFactory.getInstance().swipeLeft(getKeyboard(), getTouchKey());
                    } else if (dx < -ActionConf.SWIPE_THRESHOLD) {
                        return ActionHandlerFactory.getInstance().swipeRight(getKeyboard(), getTouchKey());
                    }
                } else {
                    if (dy > ActionConf.SWIPE_THRESHOLD) {
                        return ActionHandlerFactory.getInstance().swipeDown(getKeyboard(), getTouchKey());
                    } else if (dy < -ActionConf.SWIPE_THRESHOLD) {
                        return ActionHandlerFactory.getInstance().swipeUp(getKeyboard(), getTouchKey());
                    }
                }
                return true;
            }

            return false;
        }
    };

    private GestureDetector gd;
    private GestureDetector getGd() {
        if (null == gd) {
            gd = new GestureDetector(getContext(), gestureListener);
        }
        return gd;
    }

//    @Override
//    public boolean dispatchTouchEvent(MotionEvent ev) {
//        getGd().onTouchEvent(ev); // 让GestureDetector响应触碰事件
//        super.dispatchTouchEvent(ev); // 让Activity响应触碰事件
//        return false;
//    }

    private View touchView;
    public View getCurrentView() {
        return touchView;
    }

    private Key getTouchKey() {
        if (null != touchView) {
            Object tag = touchView.getTag();
            if (tag instanceof Key) {
                return (Key) tag;
            }
        }
        return null;
    }
    private void attachGesture(View view, Key key) {
        view.setTag(key);
        view.setClickable(true);
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                touchView = v;
                getGd().onTouchEvent(event);
                return true; // 注：返回true才能完整接收触摸事件
            }
        });
    }
}

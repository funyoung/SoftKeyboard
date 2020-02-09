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
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodSubtype;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import static com.example.android.softkeyboard.LatinKeyboard.KEYCODE_OPTIONS;

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

    /** Listener for {@link KeyboardView.OnKeyboardActionListener}. */
    private KeyboardView.OnKeyboardActionListener mKeyboardActionListener;
    public void setOnKeyboardActionListener(KeyboardView.OnKeyboardActionListener listener) {
        mKeyboardActionListener = listener;
    }
    public void setKeyboard(Keyboard keyboard) {
        mKeyboard = keyboard;
        List<Key> keys = mKeyboard.getKeys();

        int maxWidth = getResources().getDisplayMetrics().widthPixels;
        for (Key k : keys) {
            addView(buildKey(k));
            if (maxWidth < 0) {
                break;
            }
        }
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

}

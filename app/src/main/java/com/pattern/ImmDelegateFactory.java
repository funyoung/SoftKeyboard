package com.pattern;

import android.os.Build;
import android.os.IBinder;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputMethodSubtype;

import com.example.android.softkeyboard.LatinKeyboardView;

import java.util.Observable;

/**
 * Singleton, java implementation, thread safe
 */
public class ImmDelegateFactory extends Observable {
    public static ImmDelegate getDelegate(InputMethodManager imm) {
        return getInstance().createDelegate(imm);
    }

    /**
     * Abstract Factory pattern, instance ImmDelegate product family,
     * Possible change:
     * Strategy for selection decision rather than if/else hard code.
     * @return
     */
    private ImmDelegate createDelegate(InputMethodManager imm) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return new ImmDelegateNull();
        } else {
            ImmDelegateImpl delegate = new ImmDelegateImpl();
            delegate.onCreate(imm);
            return delegate;
        }
    }

    private static class Instance {
        static final ImmDelegateFactory instance = new ImmDelegateFactory();
    }
    //private static ImmDelegateFactory instance;
    private ImmDelegateFactory(){
    }
    private static ImmDelegateFactory getInstance() {
        return Instance.instance;
    }

}

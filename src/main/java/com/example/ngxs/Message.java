package com.example.ngxs;

import com.intellij.DynamicBundle;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.PropertyKey;

public class Message extends DynamicBundle {
    public static final @NonNls String BUNDLE = "messages.NgxsBundle";
    private static final Message INSTANCE = new Message();

    protected Message() {
        super(BUNDLE);
    }

    public static String message(@PropertyKey(resourceBundle = BUNDLE) String key, Object... params) {
        return INSTANCE.getMessage(key, params);
    }
}

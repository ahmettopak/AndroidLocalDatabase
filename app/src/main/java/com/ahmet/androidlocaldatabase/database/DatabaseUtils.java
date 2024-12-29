package com.ahmet.androidlocaldatabase.database;

import android.text.TextUtils;
import android.widget.EditText;

public class DatabaseUtils {
    public static boolean isEmpty(EditText text) {
        CharSequence str = text.getText().toString();
        return TextUtils.isEmpty(str);
    }
}

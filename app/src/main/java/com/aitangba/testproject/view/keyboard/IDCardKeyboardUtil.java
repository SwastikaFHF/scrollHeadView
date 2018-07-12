package com.aitangba.testproject.view.keyboard;

import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by fhf11991 on 2018/7/12
 */
public class IDCardKeyboardUtil {

    public static void bindEditText(View rootView, EditText editText) {
        InputMethodManager imm = (InputMethodManager) rootView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm != null && imm.isActive()) {
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        }
        int currentVersion = android.os.Build.VERSION.SDK_INT;
        if(currentVersion < 14) {
            editText.setInputType(InputType.TYPE_NULL);
            return;
        }

        String methodName = currentVersion >= 16 ? "setShowSoftInputOnFocus" : "setSoftInputShownOnFocus";
        Class<EditText> cls = EditText.class;
        Method setShowSoftInputOnFocus;
        try {
            setShowSoftInputOnFocus = cls.getMethod(methodName, boolean.class);
            setShowSoftInputOnFocus.setAccessible(true);
            setShowSoftInputOnFocus.invoke(editText, false);
        } catch (NoSuchMethodException e) {
            editText.setInputType(InputType.TYPE_NULL);
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}

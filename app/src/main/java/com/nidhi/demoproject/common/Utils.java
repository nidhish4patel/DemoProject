package com.nidhi.demoproject.common;

import android.text.InputFilter;
import android.text.Spanned;
import android.widget.EditText;

/**
 * Created by nidhi on 8/18/2017.
 */

public class Utils {

    public static InputFilter filter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            String blockCharacterSet = "~#^|$%*!/&+()-'\":;,?{}=!$^';,?×÷<>{}€£¥₩%~`¤♡♥|《》¡¿°•○●□■◇◆♧♣▲▼▶◀↑↓←→☆★▪:-);-):-D:-(:'(:O ";
            if (source != null && blockCharacterSet.contains(("" + source))) {
                return "";
            }
            return null;
        }
    };
}

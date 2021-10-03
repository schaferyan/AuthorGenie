package com.ryanschafer.authorgenie.ui.utils;

import android.view.View;
import android.view.ViewGroup;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ViewUtils {

    public static void handleWindowInsets(View view, int top, int right, int bottom, int left) {
        ViewCompat.setOnApplyWindowInsetsListener(view, (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars() |
                    WindowInsetsCompat.Type.systemGestures());

            ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            mlp.topMargin = insets.top + top;
            mlp.rightMargin = insets.right + right;
            mlp.bottomMargin = insets.bottom + bottom;
            mlp.leftMargin = insets.left + left;

            v.setLayoutParams(mlp);
            return WindowInsetsCompat.CONSUMED;
        });
    }

}

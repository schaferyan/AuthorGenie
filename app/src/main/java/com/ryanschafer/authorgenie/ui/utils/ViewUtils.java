package com.ryanschafer.authorgenie.ui.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;

import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

public class ViewUtils {

    public static void handleWindowInsets(View view, int top, int right, int bottom, int left, boolean consume) {
        ViewCompat.setOnApplyWindowInsetsListener(view, (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars() |
                    WindowInsetsCompat.Type.systemGestures());

            ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            mlp.topMargin = insets.top + top;
            mlp.rightMargin = insets.right + right;
            mlp.bottomMargin = insets.bottom + bottom;
            mlp.leftMargin = insets.left + left;

            v.setLayoutParams(mlp);
            return consume? WindowInsetsCompat.CONSUMED : windowInsets;
        });
    }
    public static void handleSystemBarInsets(View root, boolean consume){
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            mlp.topMargin = insets.top;
            v.setLayoutParams(mlp);
            return consume? WindowInsetsCompat.CONSUMED : windowInsets;
        });
    }

    public static void setupSwitch(SwitchCompat switchCompat) {
        switchCompat.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(switchCompat.isChecked()){
                switchCompat.setText(switchCompat.getTextOn());
            }else{
                switchCompat.setText(switchCompat.getTextOff());
            }
        });
    }

    public static void setupToggleRecViewButtonTabs(Context context, RecyclerView recyclerView, CompoundButton tb1, ListAdapter listAdapter1,
                                                    CompoundButton tb2, ListAdapter listAdapter2, int colorOn, int colorOff) {
        tb1.setOnClickListener(v -> {
            tb1.setChecked(true);
            tb2.setChecked(false);
        });
        tb2.setOnClickListener(v -> {
            tb2.setChecked(true);
            tb1.setChecked(false);
        });

        tb1.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SetCompoundButtonColors(context, buttonView, isChecked, colorOn, colorOff);
            if(isChecked){
                recyclerView.setAdapter(listAdapter1);
            }
        });

        tb2.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SetCompoundButtonColors(context, buttonView, isChecked, colorOn, colorOff);
            if(isChecked){
                recyclerView.setAdapter(listAdapter2);
            }
        });
        tb1.setChecked(true);
    }

    public static void SetCompoundButtonColors(Context context, CompoundButton buttonView, boolean isChecked, int colorOn, int colorOff) {
        if(isChecked){
            buttonView.setBackgroundColor(context.getResources().getColor(colorOn));
        }else{
            buttonView.setBackgroundColor(context.getResources().getColor(colorOff));
        }
    }

    public static void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void setShowView(boolean show, View view){
        if(show) {
            view.setVisibility(View.VISIBLE);
        }else{
            view.setVisibility(View.GONE);
        }

    }

    public static int getIntFromEditText(EditText editText )throws NumberFormatException, NullPointerException{
        String str = editText.getText().toString();
        int num;
        return Integer.parseInt(str);
    }

}

package com.ryanschafer.authorgenie.ui.main;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.appcompat.widget.AppCompatTextView;

import com.ryanschafer.authorgenie.ui.utils.OnSwipeTouchListener;

import java.util.Random;

public class TextViewCarousel extends AppCompatTextView {
    private String[] strings;
    public TextViewCarousel(Context context) {
        super(context);
        setOnTouchListener(new CarouselSwipeListener(context));
    }

    public TextViewCarousel(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnTouchListener(new CarouselSwipeListener(context));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                return true;

            case MotionEvent.ACTION_UP:
                performClick();
                return true;
        }
        return false;
    }

    // Because we call this from onTouchEvent, this code will be executed for both
    // normal touch events and for when the system calls this using Accessibility
    @Override
    public boolean performClick() {
        super.performClick();
        return true;
    }

    public void cycleText() {
        if(strings != null) {
            Random random = new Random();
            int index = random.nextInt(strings.length);
            String text = strings[index];
            setText(text);
        }
    }

    public void setStrings(String[] strings) {
        this.strings = strings;
    }

    public class CarouselSwipeListener extends OnSwipeTouchListener {
        public CarouselSwipeListener(Context context) {
            super(context);
        }

        @Override
        public void onSwipeLeft() {
            super.onSwipeLeft();
            cycleText();
        }
        @Override
        public void onSwipeRight() {
            super.onSwipeLeft();
            cycleText();
        }
    }
}

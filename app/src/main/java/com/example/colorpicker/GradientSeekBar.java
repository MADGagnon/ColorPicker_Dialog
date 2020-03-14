package com.example.customwidget;

        import android.content.Context;
        import android.content.res.ColorStateList;
        import android.graphics.Color;
        import android.graphics.drawable.GradientDrawable;
        import android.support.annotation.ColorInt;
        import android.support.v7.widget.AppCompatSeekBar;
        import android.util.AttributeSet;
        import android.widget.SeekBar;

        import com.example.colorpicker.R;

public class GradientSeekBar extends AppCompatSeekBar {
    GradientDrawable gd;

    public GradientSeekBar(Context context) {
        super(context);
        init();
    }

    public GradientSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GradientSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    void init(){
        gd = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,new int[] {R.color.defaultColor, R.color.defaultColorWhite});
        setProgressDrawable(gd);
    }

    public void updateColor(@ColorInt int[] colorList){
        gd.setColors(colorList);
    }
}
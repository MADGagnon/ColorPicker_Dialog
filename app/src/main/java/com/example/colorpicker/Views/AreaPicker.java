package com.example.colorpicker.Views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.InsetDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.example.colorpicker.R;


public class AreaPicker extends View {
    private OnPickedListener onPickedListener = null;

    private InsetDrawable backgroundDrawable;
    private static int thumbRadius, padding;
    private Paint thumb_paint;

    private float x, y,maxX,maxY;

    public AreaPicker(Context context) {
        super(context);
        init();
    }

    public AreaPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AreaPicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        setFocusable(true);
        setFocusableInTouchMode(true);

        thumb_paint = new Paint();
        thumb_paint.setStyle(Paint.Style.FILL);
        thumb_paint.setColor(getContext().getColor(R.color.thumb_color));

        thumbRadius = getResources().getInteger(R.integer.thumb_radius);
        padding = thumbRadius * 2;

        // On utilise un InsetDrawable comme arrière-plan, pour que, lorsque l'usager fournira son
        // propre Drawable à afficher dans le plan de sélection, celui-ci ne se rendent pas
        // jusqu'au bord du View, mais plutôt laisse une petite marge pour que le le marqueur de
        // sélection puisse déborder du plan de sélection sans déborder du View.
        backgroundDrawable = new InsetDrawable(new GradientDrawable(), padding);
        setBackground(backgroundDrawable);
    }

    public void setInsetDrawable(Drawable dr){
        backgroundDrawable.setDrawable(dr);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return false;
        }

        x = screen_to_frac_coord(event.getX(), getWidth());
        y = 1-screen_to_frac_coord(event.getY(), getHeight());

        onChange(true);
        return true;
    }

    public void setMaxX(int newMaxX){
        /* IMPLÉMENTER CETTE MÉTHODE */
        this.maxX=newMaxX;
    }

    public void setMaxY(int newMaxY){
        /* IMPLÉMENTER CETTE MÉTHODE */
        this.maxY=newMaxY;
    }

    public int getPickedX(){
        /* IMPLÉMENTER CETTE MÉTHODE */
        return (int)(this.x*maxX);
    }

    public int getPickedY(){
        /* IMPLÉMENTER CETTE MÉTHODE */
        return (int)(this.y*maxY);
    }

    public void setPickedX(int newX){
        /* IMPLÉMENTER CETTE MÉTHODE */
        this.x = newX/maxX;
        onChange(true);
    }

    public void setPickedY(int newY){
        /* IMPLÉMENTER CETTE MÉTHODE */
        this.y = 1-(newY/maxY);
        onChange(true);
    }

    // Cette fonction doit être appelée immédiatement après que la coordonnée
    // représentée par cet AreaPicker a été mise à jour. (Bref, dès que this.x et/ou this.y a
    // changé, il faut appeler onChange.)
    private void onChange(boolean fromUser){
        if(onPickedListener != null){
            onPickedListener.onPicked(this, getPickedX(), getPickedY(), fromUser);
        }

        invalidate();
    }

    // S'assure que value est entre 0 et 1.
    private float unitClamp(float value){
        return Math.max(0, Math.min(1, value));
    }

    // Convertit une coordonnée exprimée en espace-écran en une coordonée
    // exprimée en fraction de l'étendue (0 à 1).
    private float screen_to_frac_coord(float screen_coord, float screen_range){
        float frac_coord = (screen_coord - padding) / (screen_range - 2 * padding);
        return unitClamp(frac_coord);
    }

    // Convertit une coordonnée exprimée en fraction de l'étendue (0 à 1) en espace-écran.
    private float frac_to_screen_coord(float frac_coord, float screen_range){
        return frac_coord * (screen_range - 2 * padding) + padding;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float ax = frac_to_screen_coord(x, getWidth());
        float ay = frac_to_screen_coord(1-y, getHeight());

        canvas.drawCircle(ax, ay, thumbRadius, thumb_paint);
    }

    public void setOnPickedListener(OnPickedListener onPickedListener){
        this.onPickedListener = onPickedListener;
    }

    public interface OnPickedListener{
        void onPicked(AreaPicker areaPicker, int x, int y, boolean fromUser);
    }
}

package com.example.colorpicker;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.support.annotation.ColorInt;

public class SaturationValueGradient extends LayerDrawable {
    private GradientDrawable saturationGradient;

    private static GradientDrawable[] prepareLayers(){
        // Le dégradé 2D du AreaPicker est créé en superposant 2 dégradés linéaires. Un à
        // l'horizontal, et l'autre vertical. Le dégradé du dessus est dégrade entre noir et
        // transparent, de telle sorte à ce qu'il laisse progressivement transparaître le dégradé
        // en dessous de lui. Seul le dégradé du dessous, qui va de blanc à la couleur
        // pleinement saturée, a besoin d'être modifié. C'est "hacky", mais ça marche!

        // Color layer
        GradientDrawable saturationGradient = new GradientDrawable();
        saturationGradient.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);

        // Black and white layer
        GradientDrawable valueGradient = new GradientDrawable();
        valueGradient.setOrientation(GradientDrawable.Orientation.TOP_BOTTOM);
        valueGradient.setColors(new int[] {Color.TRANSPARENT, Color.BLACK});
        valueGradient.setStroke(1, Color.BLACK);

        return new GradientDrawable[]{saturationGradient, valueGradient};
    }

    public SaturationValueGradient() {
        super(prepareLayers());
        saturationGradient = (GradientDrawable)this.getDrawable(0);
    }

    public SaturationValueGradient(@ColorInt int color) {
        this();
        setColor(color);
    }

    public void setColor(@ColorInt int color) {
        saturationGradient.setColors(new int[] {Color.WHITE, color});
    }
}

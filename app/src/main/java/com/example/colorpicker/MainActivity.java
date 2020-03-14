package com.example.colorpicker;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    Button pickButton;
    View pickedColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /* CETTE MÉTHODE DEVRA ÊTRE MODIFIÉE */

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ColorPickerDialog dialog = new ColorPickerDialog(this);
        dialog.setOnColorPickedListener(new MainActivityOnColorPickedListener());
        pickButton = findViewById(R.id.button_pick);
        pickButton.setOnClickListener((View v) -> dialog.show());

    }

    public class MainActivityOnColorPickedListener implements ColorPickerDialog.OnColorPickedListener{
        public void onColorPicked(ColorPickerDialog colorPickerDialog, @ColorInt int color){
            // on crée un drawable qu'on applique au foreground pour laisser le damier qui témoigne
            // de la composante alpha en background.
            ColorDrawable foreground = new ColorDrawable(color);
            findViewById(R.id.picked_color).setForeground(foreground);
        }
    }
}

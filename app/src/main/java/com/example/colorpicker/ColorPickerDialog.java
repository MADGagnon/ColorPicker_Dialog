package com.example.colorpicker;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Message;
import android.support.annotation.ColorInt;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.customwidget.GradientSeekBar;

import com.example.colorpicker.Views.AreaPicker;

class ColorPickerDialog extends AlertDialog {
    private final static int MAX_ARGB_VALUE = 255;
    private final static int MAX_SV_VALUE = 100;
    private final static int MAX_H_VALUE = 360;

    private AreaPicker seekSV;
    private SaturationValueGradient saturationValueGradient;
    private SaturationValueGradient saturationValueGradientR;

    private GradientSeekBar seekH;
    private GradientSeekBar seekR;
    private GradientSeekBar seekG;
    private GradientSeekBar seekB;
    private GradientSeekBar seekA;//gradiantSeekBar pour Alpha (Bonus)

    private Button OkButton;
    private Button AnnulerButton;


    // Représentation/stockage interne de la couleur présentement sélectionnée par le Dialog.
    // ajout de toutes les variables internes nécessaire
    private int a, r, g, b, h, s, v;

    private boolean RGBLastChanged;

    ColorPickerDialog(Context context) {
        super(context);
        init(context);
    }

    ColorPickerDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init(context);
    }

    ColorPickerDialog(Context context, int themeResId) {
        super(context, themeResId);
        init(context);
    }

    private void init(Context context){

        /* CETTE MÉTHODE DEVRA ÊTRE MODIFIÉE */

        // Initialize dialog
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_picker, null);
        setView(view);

        // Initialise le titre du ColorPickedDialog
        setTitle(R.string.pick_color);

        // Initialize SV gradient
        seekSV = view.findViewById(R.id.seekSV);
        saturationValueGradient = new SaturationValueGradient();
        seekSV.setInsetDrawable(saturationValueGradient);
        seekSV.setOnPickedListener(SVAreaPickerListener());
        seekSV.setMaxX(MAX_SV_VALUE);
        seekSV.setMaxY(MAX_SV_VALUE);

        // Initialisation des GradientSeekBar.
        seekH = view.findViewById(R.id.seekH);
        seekA = view.findViewById(R.id.seekA);//gradiantSeekBar pour Alpha (Bonus)
        seekR = view.findViewById(R.id.seekR);
        seekG = view.findViewById(R.id.seekG);
        seekB = view.findViewById(R.id.seekB);

        seekH.setOnSeekBarChangeListener(HSVGradientSeekBarListener());
        seekR.setOnSeekBarChangeListener(RGBGradientSeekBarListener());
        seekG.setOnSeekBarChangeListener(RGBGradientSeekBarListener());
        seekB.setOnSeekBarChangeListener(RGBGradientSeekBarListener());
        seekA.setOnSeekBarChangeListener(AGradientSeekBarListener());


        seekH.setMax(MAX_H_VALUE);
        seekA.setMax(MAX_ARGB_VALUE);
        seekR.setMax(MAX_ARGB_VALUE);
        seekG.setMax(MAX_ARGB_VALUE);
        seekB.setMax(MAX_ARGB_VALUE);

        //couleur de la barre degrade de la barre Hue
        seekH.updateColor(new int[] {
                Color.argb(MAX_ARGB_VALUE,MAX_ARGB_VALUE,0,0),
                Color.argb(MAX_ARGB_VALUE,MAX_ARGB_VALUE,MAX_ARGB_VALUE,0),
                Color.argb(MAX_ARGB_VALUE,0,MAX_ARGB_VALUE,0),
                Color.argb(MAX_ARGB_VALUE,0,MAX_ARGB_VALUE,MAX_ARGB_VALUE),
                Color.argb(MAX_ARGB_VALUE,0,0,MAX_ARGB_VALUE),
                Color.argb(MAX_ARGB_VALUE,MAX_ARGB_VALUE,0,MAX_ARGB_VALUE),
                Color.argb(MAX_ARGB_VALUE,MAX_ARGB_VALUE,0,0)});

        setColor(context.getResources().getColor(R.color.defaultColor, null));

        //intialise le alpha a opaque
        seekA.setProgress(MAX_ARGB_VALUE);

        // Initialise le bouton "Cancel"
        setButton(BUTTON_NEGATIVE, context.getResources().getString(R.string.colorPickerDialog_buttonText_Annuler),(DialogInterface dialog, int which)->{
            this.onStop();
        });


    }

    @ColorInt int getColor(){
        /* IMPLÉMENTER CETTE MÉTHODE
         * Elle doit retourner la couleur présentement sélectionnée par le dialog.
         * */
        return Color.argb(a,r,g,b);
    }

    public void setColor(@ColorInt int newColor){
        /* IMPLÉMENTER CETTE MÉTHODE
         * Elle doit mettre à jour l'état du dialog pour que la couleur sélectionnée
         * corresponde à "newColor".
         * */

        if(RGBLastChanged){
            seekH.setProgress(this.h);
            seekSV.setPickedX(this.s);
            seekSV.setPickedY(this.v);
        }else{
            seekR.setProgress(this.r);
            seekG.setProgress(this.g);
            seekB.setProgress(this.b);
        }

        saturationValueGradient.setColor(Color.rgb(HSVtoRGB(h,MAX_SV_VALUE,MAX_SV_VALUE)[0], HSVtoRGB(h,MAX_SV_VALUE,MAX_SV_VALUE)[1], HSVtoRGB(h,MAX_SV_VALUE,MAX_SV_VALUE)[2]));
        seekA.updateColor(new int[] {Color.argb(0, this.r, this.g, this.b), Color.argb(MAX_ARGB_VALUE,this.r,this.g,this.b)});
        seekR.updateColor(new int[] {Color.argb(MAX_ARGB_VALUE,0,this.g,this.b), Color.argb(MAX_ARGB_VALUE,MAX_ARGB_VALUE,this.g,this.b)});
        seekG.updateColor(new int[] {Color.argb(MAX_ARGB_VALUE,this.r,0,this.b), Color.argb(MAX_ARGB_VALUE,this.r,MAX_ARGB_VALUE,this.b)});
        seekB.updateColor(new int[] {Color.argb(MAX_ARGB_VALUE,this.r,this.g,0), Color.argb(MAX_ARGB_VALUE,this.r,this.g,MAX_ARGB_VALUE)});
    }

    public void HSVChange(int H, int S, int V){
        this.h = H;
        this.s = S;
        this.v = V;

        int[] convertArray = HSVtoRGB(this.h,this.s,this.v);

        this.r = convertArray[0];
        this.g = convertArray[1];
        this.b = convertArray[2];

        setColor(Color.argb(this.a,this.r,this.g,this.b));
    }

    public void RGBChange(int R, int G, int B){
        this.r = R;
        this.g = G;
        this.b = B;

        int[] convertArray = RGBtoHSV(this.r,this.g,this.b);
        this.h = convertArray[0];
        this.s = convertArray[1];
        this.v = convertArray[2];

        setColor(Color.argb(this.a,this.r,this.g,this.b));
    }

    public void AChange(int A){
        this.a = A;
    }

    private int[] HSVtoRGB(int H, int S, int V){
        /* IMPLÉMENTER CETTE MÉTHODE
         * Elle doit convertir un trio de valeurs HSL à un trio de valeurs RGB
         * */
        float h = (float)H;
        float s = (float)S;
        float v = (float)V;
        float hPrime = h/60;
        float sPrime = s/MAX_SV_VALUE;
        float vPrime = v/MAX_SV_VALUE;
        float c = sPrime * vPrime;
        float delta = vPrime - c;
        float x = 1 - Math.abs((hPrime % 2) - 1);
        float rPrime;
        float gPrime;
        float bPrime;
        int r;
        int g;
        int b;

        //rPrime
        if(hPrime<=1){
            rPrime = 1;
        }else if(hPrime<=2){
            rPrime = x;
        }else if(hPrime<=4){
            rPrime = 0;
        }else if(hPrime<=5){
            rPrime = x;
        }else {//hPrime<=6
            rPrime = 1;
        }

        //gPrime
        if(hPrime<=1){
            gPrime = x;
        }else if(hPrime<=3){
            gPrime = 1;
        }else if(hPrime<=4){
            gPrime = x;
        }else {//hPrime<=6
            gPrime = 0;
        }

        //bPrime
        if(hPrime<=2){
            bPrime = 0;
        }else if(hPrime<=3){
            bPrime = x;
        }else if(hPrime<=5){
            bPrime = 1;
        }else {//hPrime<=6
            bPrime = x;
        }

        r = Math.round(MAX_ARGB_VALUE * ( c * rPrime + delta));
        g = Math.round(MAX_ARGB_VALUE * ( c * gPrime + delta));
        b = Math.round(MAX_ARGB_VALUE * ( c * bPrime + delta));

        //textView.setText(" h: "+h+" s: "+s+" v: "+v+" hPrime: "+hPrime+" sPrime: "+sPrime+" vPrime: "+vPrime+" c: "+c+" delta: "+delta+" x: "+x+" rPrime: "+rPrime+" gPrime: "+gPrime+" bPrime: "+bPrime+" r: "+r+" g: "+g+" b: "+b);
        //textViewRGB.setText("h: "+h+" s: "+s+" v: "+v+"\n NOUS r: "+r+" g: "+g+" b: "+b+"\n EUX r: "+ Color.red(Color.HSVToColor(new float[]{h,s,v}))+" g: "+ Color.green(Color.HSVToColor(new float[]{h,s,v}))+" b: "+ Color.blue(Color.HSVToColor(new float[]{h,s,v})));

        return new int[] {r,g,b};
    }

    //Nous avons décidé de gérer le cas de la division de 0/0 en retournant toujours 0.
    private int[] RGBtoHSV(int R, int G, int B){
        /* IMPLÉMENTER CETTE MÉTHODE
         * Elle doit convertir un trio de valeurs RGB à un trio de valeurs HSL
         * */
        float r = (float)R;
        float g = (float)G;
        float b = (float)B;
        float cMax = Math.max(Math.max(r,g),b);
        float cMin = Math.min(Math.min(r,g),b);
        float deltaC = cMax-cMin;
        float hPrime;
        int h;
        int s;
        int v;

        if(deltaC != 0){
            if(cMax == r){
                hPrime = (g-b)/deltaC;
            }else if(cMax == g){
                hPrime = 2 + ((b-r)/deltaC);
            }else{//cMax == b
                hPrime = 4 + ((r-g)/deltaC);
            }
        }else{
            hPrime = 0;
        }

        if(hPrime >= 0){
            hPrime = hPrime;
        }else{//hPrime < 0
            hPrime = (hPrime + 6);
        }

        h = Math.round((hPrime * 60));

        if(cMax != 0){
            s = Math.round(MAX_SV_VALUE * (deltaC/cMax));
        }else{
            s = 0;
        }
        v = Math.round((MAX_SV_VALUE * (cMax/MAX_ARGB_VALUE)));

        return new int[] {h,s,v};
    }

    public void setOnColorPickedListener(OnColorPickedListener onColorPickedListener) {
         /* Elle doit enregistrer un "OnColorPickedListener", qui sera appelé, éventuellement,
         * lorsque le bouton "ok" du dialog sera cliqué.
         * */
         ColorPickerDialog that = this;
         setButton(BUTTON_POSITIVE, getContext().getString(R.string.colorPickerDialog_buttonText_OK), (DialogInterface dialog, int which)->{
             onColorPickedListener.onColorPicked(that,getColor());
         });
    }

    public GradientSeekBar.OnSeekBarChangeListener RGBGradientSeekBarListener(){
        return new OnGradientSeekBarChangeListener() {
            @Override
            public void onStartTrackingTouch(SeekBar seekBar){
                RGBLastChanged = true;
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
                seekBar.setProgress(progress);
                RGBChange(seekR.getProgress(),seekG.getProgress(),seekB.getProgress());
            }
        };
    }

    public GradientSeekBar.OnSeekBarChangeListener AGradientSeekBarListener(){
        return new OnGradientSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
                seekBar.setProgress(progress);
                AChange(progress);
            }
        };
    }

    public GradientSeekBar.OnSeekBarChangeListener HSVGradientSeekBarListener(){
        return new OnGradientSeekBarChangeListener() {
            @Override
            public void onStartTrackingTouch(SeekBar seekBar){
                RGBLastChanged = false;
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
                seekBar.setProgress(progress);
                HSVChange(seekH.getProgress(),seekSV.getPickedX(),seekSV.getPickedY());
            }
        };
    }

    public AreaPicker.OnPickedListener SVAreaPickerListener(){
        return new AreaPicker.OnPickedListener() {
            @Override
            public void onPicked(AreaPicker areaPicker, int x, int y, boolean fromUser) {
                RGBLastChanged = false;
                HSVChange(seekH.getProgress(),seekSV.getPickedX(),seekSV.getPickedY());
            }
        };
    }

    public interface OnColorPickedListener{
        void onColorPicked(ColorPickerDialog colorPickerDialog, @ColorInt int color);
    }

    public class OnGradientSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener{
        //adapter
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){}
        public void onStartTrackingTouch(SeekBar seekBar){}
        public void onStopTrackingTouch(SeekBar seekBar){}
    }
}



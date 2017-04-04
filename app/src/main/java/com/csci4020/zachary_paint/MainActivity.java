package com.csci4020.zachary_paint;

import android.content.DialogInterface;
import android.graphics.Color;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.github.veritas1.verticalslidecolorpicker.VerticalSlideColorPicker;

import java.util.UUID;

//TODO: Micro-transactions, idk.

public class MainActivity extends AppCompatActivity {

    private  CanvasView canvasView;
    private ImageButton currColor; //Might have been useful if ever get around to showing what color is active.
    private ImageButton brushThick;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        canvasView = (CanvasView) findViewById(R.id.canvas);
        brushThick = (ImageButton) findViewById(R.id.brush_button);


        //copy-pasta'd from https://github.com/veritas1/vertical-slide-color-picker since that's the library we're using.
        final VerticalSlideColorPicker colorPicker = (VerticalSlideColorPicker) findViewById(R.id.color_picker);
        final View selectedColorView = findViewById(R.id.selected_color);

        colorPicker.setOnColorChangeListener(new VerticalSlideColorPicker.OnColorChangeListener() {
            @Override
            public void onColorChange(int selectedColor) {
                selectedColorView.setBackgroundColor(selectedColor);
            }
        });

    }

    public void clearCanvas(View v) {
        //Warning dialog informing the user that the current drawing will be erased if they hit 'confirm'.
        AlertDialog.Builder warnDialog = new AlertDialog.Builder(this);
        warnDialog.setTitle("Confirmation");
        warnDialog.setMessage("Are you sure you want to start a new drawing? Current drawing will not be saved.");
        warnDialog.setPositiveButton("Confirm", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which){
                canvasView.clearCanvas();
                dialog.dismiss();
            }
        });
        warnDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which){
                dialog.cancel();
            }
        });
        warnDialog.show();

    }

    public void saveCanvas(View v) {
        //Warning dialog informing the user that the current 'art' will be saved for posterity if they hit yes.
        AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
        saveDialog.setTitle("Confirmation");
        saveDialog.setMessage("Are you sure you want to save the current drawing?");
        saveDialog.setPositiveButton("Confirm", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which){
                canvasView.setDrawingCacheEnabled(true);
                String saveImage = MediaStore.Images.Media.insertImage(
                        getContentResolver(), canvasView.getDrawingCache(),
                        UUID.randomUUID().toString()+".png", "modernArt");
                if(saveImage != null) {
                    Toast crunchy = Toast.makeText(getApplicationContext(),
                            "Save successful!", Toast.LENGTH_SHORT);
                    crunchy.show();
                } else {
                    Toast burnt = Toast.makeText(getApplicationContext(),
                            "Save unsuccessful!", Toast.LENGTH_SHORT);
                    burnt.show();
                }
                canvasView.destroyDrawingCache();   //to prevent future drawings from using the same cache.
                dialog.dismiss();
            }
        });
        saveDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which){
                dialog.cancel();
            }
        });
        saveDialog.show();

    }

    public void colorClicked(View v) { //method that goes off when the user clicks on the image buttons.
            ImageButton imgColor = (ImageButton) v;
            String color = v.getTag().toString();   //retrieve the image button's tags that hold their color.
            canvasView.setColor(color);
    }
    public void eraserClicked(View v) {     //There isn't a fill tool, so the canvas is always default white.
        canvasView.setColor("#FFFFFFFF");   //Thus, the eraser seems to be doing it's job when it's actually painting things white.
    }

    public void brushClicked(View v) {      //Increases/Decreases line thickness. Acts on a cycle.
        canvasView.growBigger();

        if(canvasView.getStrokeW() == 8f) {
            brushThick.setImageResource(R.drawable.thin);
        } else if (canvasView.getStrokeW() == 16f) {
            brushThick.setImageResource(R.drawable.thick);
        } else if (canvasView.getStrokeW() == 32f) {
            brushThick.setImageResource(R.drawable.thicc);
        } else {
            brushThick.setImageResource(R.drawable.brush);  //Just in case it breaks or something.
        }
    }
}
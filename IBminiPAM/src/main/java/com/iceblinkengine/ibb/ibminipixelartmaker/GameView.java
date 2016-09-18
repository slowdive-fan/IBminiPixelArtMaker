package com.iceblinkengine.ibb.ibminipixelartmaker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Environment;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Created by Slowdive on 9/10/2016.
 */
public class GameView extends View
{
    public Activity myActivity = null;
    public Context gameContext;
    public float screenDensity;
    public int screenWidth;
    public int screenHeight;
    public int squareSizeInPixels = 22;
    public int squareSize; //in dp (squareSizeInPixels * screenDensity)
    public int squaresInWidth = 48; //large 19, small 15
    public int squaresInHeight = 87; //large 10, small 8
    public int oXshift = 0;
    public int oYshift = 0;
    public float drawFontRegHeight;
    public String versionNum = "v1.00";
    public Paint mUiTextPaint = null;
    public int ibbwidth = 132;
    public int ibbheight = 132;
    public Bitmap myBitmap;
    public Paint bmPaint;
    public Paint bmOpacityPaint;
    public Paint rectPaint;
    public AlertDialog ActionDialog;
    public Bitmap button;
    public Bitmap slot;
    public Bitmap grass;
    public IbButton btnNew = null;
    public IbButton btnOpen = null;
    public IbButton btnSave = null;
    public IbButton btnSaveAs = null;
    public IbButton btnAlphaAdjust = null;
    public IbButton btnEraser = null;
    public IbButton btnCanvasBackground = null;
    public IbButton btnPreviewBackground = null;
    public IbButton btnToggleLayer = null;
    public IbButton btnShowLayers = null;
    public IbButton btnGetColor = null;
    public IbButton btnUndo = null;
    public IbButton btnRedo = null;
    public IbPalette palette = null;
    public int currentColor;
    public boolean isIdleLayerShown = true;
    public boolean showLayers = true;
    public boolean getColorMode = false;
    public List<Integer> colorPaletteList = new ArrayList<Integer>();
    public int previewBackIndex = 0;
    public int canvasBackIndex = 0;
    public String filename = "newdrawing";
    public Stack<Bitmap> undoStack = new Stack<>();
    public Stack<Bitmap> redoStack = new Stack<>();
    public boolean continuousDrawMode = false;

    public GameView(Activity actvty, Context context)
    {
        super(context);
        myActivity = actvty;
        gameContext = context;

        int dim1 = getResources().getDisplayMetrics().widthPixels;
        int dim2 = getResources().getDisplayMetrics().heightPixels;

        //ensures that the proper dimensions are used for portrait orientation
        if (dim1 <= dim2)
        {
            screenWidth = dim1;
            screenHeight = dim2;
        }
        else
        {
            screenWidth = dim2;
            screenHeight = dim1;
        }

        float sqrW = (float) screenWidth / (squaresInWidth);
        float sqrH = (float) screenHeight / (squaresInHeight);

        if (sqrW > sqrH)
        {
            squareSize = (int) (sqrH);
        }
        else
        {
            squareSize = (int) (sqrW);
        }
        screenDensity = (float) squareSize / (float) squareSizeInPixels;
        oXshift = (screenWidth - (squareSize * squaresInWidth)) / 2;
        oYshift = (screenHeight - (squareSize * squaresInHeight)) / 2;

        drawFontRegHeight = (squareSize * 1.5f);

        mUiTextPaint = new Paint();
        mUiTextPaint.setStyle(Paint.Style.FILL);
        mUiTextPaint.setColor(Color.YELLOW);
        mUiTextPaint.setAntiAlias(true);

        File sdCard = Environment.getExternalStorageDirectory();
        File directory = new File(sdCard.getAbsolutePath() + "/IceBlink2mini/override");
        if (!directory.exists())
        {
            directory.mkdirs();
        }

        myBitmap = Bitmap.createBitmap(24, 24, Bitmap.Config.ARGB_8888);

        currentColor = Color.argb(255, 255, 255, 255);

        bmPaint = new Paint();
        bmPaint.setAntiAlias(false);
        bmPaint.setFilterBitmap(false);

        bmOpacityPaint = new Paint();
        bmOpacityPaint.setAntiAlias(false);
        bmOpacityPaint.setFilterBitmap(false);
        bmOpacityPaint.setAlpha(255);

        rectPaint = new Paint();
        rectPaint.setColor(currentColor);
        rectPaint.setStrokeWidth(1);

        button = BitmapFactory.decodeResource(this.getResources(), R.mipmap.btn_small);
        slot = BitmapFactory.decodeResource(this.getResources(), R.mipmap.item_slot);
        grass = BitmapFactory.decodeResource(this.getResources(), R.mipmap.t_f_grass);

        setControlsStart();
        fillPaletteColorList();

        //undoStack.setSize(10);
        //redoStack.setSize(10);
    }

    public void setControlsStart()
    {
        if (btnNew == null)
        {
            btnNew = new IbButton(this.gameContext, this, 1.0f);
            btnNew.Img = BitmapFactory.decodeResource(getResources(), R.mipmap.btn_small);
            btnNew.X = 0 * squareSize;
            btnNew.Y = 0 * squareSize;
            btnNew.Text = "New";
            btnNew.Height = (int)(ibbheight * screenDensity);
            btnNew.Width = (int)(ibbwidth * screenDensity);
        }
        if (btnOpen == null)
        {
            btnOpen = new IbButton(this.gameContext, this, 1.0f);
            btnOpen.Img = BitmapFactory.decodeResource(getResources(), R.mipmap.btn_small);
            btnOpen.X = 8 * squareSize;
            btnOpen.Y = 0 * squareSize;
            btnOpen.Text = "Open";
            btnOpen.Height = (int)(ibbheight * screenDensity);
            btnOpen.Width = (int)(ibbwidth * screenDensity);
        }
        if (btnSave == null)
        {
            btnSave = new IbButton(this.gameContext, this, 1.0f);
            btnSave.Img = BitmapFactory.decodeResource(getResources(), R.mipmap.btn_small);
            btnSave.X = 16 * squareSize;
            btnSave.Y = 0 * squareSize;
            btnSave.Text = "Save";
            btnSave.Height = (int)(ibbheight * screenDensity);
            btnSave.Width = (int)(ibbwidth * screenDensity);
        }
        if (btnSaveAs == null)
        {
            btnSaveAs = new IbButton(this.gameContext, this, 1.0f);
            btnSaveAs.Img = BitmapFactory.decodeResource(getResources(), R.mipmap.btn_small);
            btnSaveAs.X = 24 * squareSize;
            btnSaveAs.Y = 0 * squareSize;
            btnSaveAs.Text = "Save As";
            btnSaveAs.Height = (int)(ibbheight * screenDensity);
            btnSaveAs.Width = (int)(ibbwidth * screenDensity);
        }
        if (btnUndo == null)
        {
            btnUndo = new IbButton(this.gameContext, this, 1.0f);
            btnUndo.Img = BitmapFactory.decodeResource(getResources(), R.mipmap.btn_small);
            btnUndo.X = 32 * squareSize;
            btnUndo.Y = 0 * squareSize;
            btnUndo.Text = "Undo";
            btnUndo.Height = (int)(ibbheight * screenDensity);
            btnUndo.Width = (int)(ibbwidth * screenDensity);
        }
        if (btnRedo == null)
        {
            btnRedo = new IbButton(this.gameContext, this, 1.0f);
            btnRedo.Img = BitmapFactory.decodeResource(getResources(), R.mipmap.btn_small);
            btnRedo.X = 40 * squareSize;
            btnRedo.Y = 0 * squareSize;
            btnRedo.Text = "Redo";
            btnRedo.Height = (int)(ibbheight * screenDensity);
            btnRedo.Width = (int)(ibbwidth * screenDensity);
        }
        if (btnAlphaAdjust == null)
        {
            btnAlphaAdjust = new IbButton(this.gameContext, this, 1.0f);
            btnAlphaAdjust.Img = BitmapFactory.decodeResource(getResources(), R.mipmap.btn_small);
            btnAlphaAdjust.X = 40 * squareSize;
            btnAlphaAdjust.Y = 8 * squareSize;
            btnAlphaAdjust.Text = "AlphaSet";
            btnAlphaAdjust.Height = (int)(ibbheight * screenDensity);
            btnAlphaAdjust.Width = (int)(ibbwidth * screenDensity);
        }
        if (btnPreviewBackground == null)
        {
            btnPreviewBackground = new IbButton(this.gameContext, this, 1.0f);
            btnPreviewBackground.Img = BitmapFactory.decodeResource(getResources(), R.mipmap.btn_small);
            btnPreviewBackground.X = 8 * squareSize;
            btnPreviewBackground.Y = 8 * squareSize;
            btnPreviewBackground.Text = "Preview";
            btnPreviewBackground.Height = (int)(ibbheight * screenDensity);
            btnPreviewBackground.Width = (int)(ibbwidth * screenDensity);
        }
        if (btnCanvasBackground == null)
        {
            btnCanvasBackground = new IbButton(this.gameContext, this, 1.0f);
            btnCanvasBackground.Img = BitmapFactory.decodeResource(getResources(), R.mipmap.btn_small);
            btnCanvasBackground.X = 16 * squareSize;
            btnCanvasBackground.Y = 8 * squareSize;
            btnCanvasBackground.Text = "Canvas";
            btnCanvasBackground.Height = (int)(ibbheight * screenDensity);
            btnCanvasBackground.Width = (int)(ibbwidth * screenDensity);
        }
        if (btnToggleLayer == null)
        {
            btnToggleLayer = new IbButton(this.gameContext, this, 1.0f);
            btnToggleLayer.Img = BitmapFactory.decodeResource(getResources(), R.mipmap.btn_small);
            btnToggleLayer.X = 24 * squareSize;
            btnToggleLayer.Y = 8 * squareSize;
            btnToggleLayer.Text = "Idle";
            btnToggleLayer.Height = (int)(ibbheight * screenDensity);
            btnToggleLayer.Width = (int)(ibbwidth * screenDensity);
        }
        if (btnShowLayers == null)
        {
            btnShowLayers = new IbButton(this.gameContext, this, 1.0f);
            btnShowLayers.Img = BitmapFactory.decodeResource(getResources(), R.mipmap.btn_small);
            btnShowLayers.X = 32 * squareSize;
            btnShowLayers.Y = 8 * squareSize;
            btnShowLayers.Text = "ShowLyrs";
            btnShowLayers.Height = (int)(ibbheight * screenDensity);
            btnShowLayers.Width = (int)(ibbwidth * screenDensity);
        }
        if (btnEraser == null)
        {
            btnEraser = new IbButton(this.gameContext, this, 1.0f);
            btnEraser.Img = BitmapFactory.decodeResource(getResources(), R.mipmap.btn_small);
            btnEraser.X = 42 * squareSize;
            btnEraser.Y = 71 * squareSize;
            btnEraser.Text = "Eraser";
            btnEraser.Height = (int)(ibbheight * screenDensity);
            btnEraser.Width = (int)(ibbwidth * screenDensity);
        }
        if (btnGetColor == null)
        {
            btnGetColor = new IbButton(this.gameContext, this, 1.0f);
            btnGetColor.Img = BitmapFactory.decodeResource(getResources(), R.mipmap.btn_small);
            btnGetColor.X = 42 * squareSize;
            btnGetColor.Y = 78 * squareSize;
            btnGetColor.Text = "GetColor";
            btnGetColor.Height = (int)(ibbheight * screenDensity);
            btnGetColor.Width = (int)(ibbwidth * screenDensity);
        }
        if (palette == null)
        {
            palette = new IbPalette(this);
            palette.Img = BitmapFactory.decodeResource(getResources(), R.mipmap.color_palette2);
            palette.X = 0 * squareSize;
            palette.Y = 68 * squareSize;
            palette.Height = (int)(5 * 3 * squareSize);
            palette.Width = (int)(13 * 3 * squareSize);
        }
    }

    public void doOpenDialog()
    {
        File sdCard = Environment.getExternalStorageDirectory();
        File directory = new File (sdCard.getAbsolutePath() + "/IceBlink2mini/override");
        new FileChooser(this.myActivity, directory).setFileListener(new FileChooser.FileSelectedListener()
        {
            @Override public void fileSelected(final File file)
            {
                filename = file.getName().replaceFirst("[.][^.]+$", "");
                // do something with the file
                Bitmap immutable = BitmapFactory.decodeFile(file.getPath());
                myBitmap = immutable.copy(Bitmap.Config.ARGB_8888, true);
                invalidate();
            }
        }).showDialog();
    }
    public void doSaveAsDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(gameContext);
        builder.setTitle("Save As file name (no extension, '.png' will be added");

        // Set up the input
        final EditText input = new EditText(gameContext);
        // Specify the type of input expected
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(filename);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                if (input.getText().toString().length() > 0)
                {
                    filename = input.getText().toString();
                    FileOutputStream out = null;
                    try
                    {
                        File sdCard = Environment.getExternalStorageDirectory();
                        out = new FileOutputStream(sdCard.getAbsolutePath() + "/IceBlink2mini/override/" + filename + ".png");
                        myBitmap.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
                        // PNG is a lossless format, the compression factor (100) is ignored
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
                else
                {
                    Toast.makeText(gameContext, "You must enter a file name...abort save", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.cancel();
            }
        });

        builder.show();
    }
    public void doSaveDialog()
    {
        try
        {
            FileOutputStream out = null;
            File sdCard = Environment.getExternalStorageDirectory();
            out = new FileOutputStream(sdCard.getAbsolutePath() + "/IceBlink2mini/override/" + filename + ".png");
            myBitmap.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
            Toast.makeText(gameContext, "Saved " + filename + ".png to root/IceBlink2mini/override folder", Toast.LENGTH_SHORT).show();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Toast.makeText(gameContext, "Failed to save file: " + filename + ".png", Toast.LENGTH_SHORT).show();
        }
    }
    public void doAlphaAdjust()
    {
        int [] allpixels = new int [myBitmap.getHeight()*myBitmap.getWidth()];

        myBitmap.getPixels(allpixels, 0, myBitmap.getWidth(), 0, 0, myBitmap.getWidth(), myBitmap.getHeight());

        for(int i = 0; i < allpixels.length; i++)
        {
            if(Color.alpha(allpixels[i]) < 30)
            {
                allpixels[i] = Color.argb(0, Color.red(allpixels[i]), Color.green(allpixels[i]), Color.blue(allpixels[i]));
            }
            else
            {
                allpixels[i] = Color.argb(255, Color.red(allpixels[i]), Color.green(allpixels[i]), Color.blue(allpixels[i]));
            }
        }

        myBitmap.setPixels(allpixels, 0, myBitmap.getWidth(), 0, 0, myBitmap.getWidth(), myBitmap.getHeight());
    }
    public void fillPaletteColorList()
    {
        //red
        colorPaletteList.add(Color.argb(255, 78, 7, 7));
        colorPaletteList.add(Color.argb(255, 156, 14, 14));
        colorPaletteList.add(Color.argb(255, 234, 21, 21));
        colorPaletteList.add(Color.argb(255, 241, 99, 99));
        colorPaletteList.add(Color.argb(255, 248, 177, 177));
        //orange
        colorPaletteList.add(Color.argb(255, 78, 18, 7));
        colorPaletteList.add(Color.argb(255, 156, 35, 14));
        colorPaletteList.add(Color.argb(255, 234, 53, 21));
        colorPaletteList.add(Color.argb(255, 241, 120, 99));
        colorPaletteList.add(Color.argb(255, 248, 188, 177));
        //brown
        colorPaletteList.add(Color.argb(255, 78, 30, 7));
        colorPaletteList.add(Color.argb(255, 156, 61, 14));
        colorPaletteList.add(Color.argb(255, 234, 90, 21));
        colorPaletteList.add(Color.argb(255, 241, 146, 99));
        colorPaletteList.add(Color.argb(255, 248, 200, 177));
        //gold
        colorPaletteList.add(Color.argb(255, 78, 51, 7));
        colorPaletteList.add(Color.argb(255, 156, 103, 14));
        colorPaletteList.add(Color.argb(255, 234, 154, 21));
        colorPaletteList.add(Color.argb(255, 241, 188, 99));
        colorPaletteList.add(Color.argb(255, 248, 221, 177));
        //yellow
        colorPaletteList.add(Color.argb(255, 78, 69, 7));
        colorPaletteList.add(Color.argb(255, 156, 138, 14));
        colorPaletteList.add(Color.argb(255, 234, 207, 21));
        colorPaletteList.add(Color.argb(255, 241, 223, 99));
        colorPaletteList.add(Color.argb(255, 248, 239, 177));
        //green
        colorPaletteList.add(Color.argb(255, 7, 78, 7));
        colorPaletteList.add(Color.argb(255, 14, 156, 14));
        colorPaletteList.add(Color.argb(255, 21, 234, 21));
        colorPaletteList.add(Color.argb(255, 99, 241, 99));
        colorPaletteList.add(Color.argb(255, 177, 248, 177));
        //green-blue
        colorPaletteList.add(Color.argb(255, 7, 78, 61));
        colorPaletteList.add(Color.argb(255, 14, 156, 121));
        colorPaletteList.add(Color.argb(255, 21, 234, 181));
        colorPaletteList.add(Color.argb(255, 99, 241, 206));
        colorPaletteList.add(Color.argb(255, 177, 248, 231));
        //blue
        colorPaletteList.add(Color.argb(255, 7, 43, 78));
        colorPaletteList.add(Color.argb(255, 14, 85, 156));
        colorPaletteList.add(Color.argb(255, 21, 128, 234));
        colorPaletteList.add(Color.argb(255, 99, 170, 241));
        colorPaletteList.add(Color.argb(255, 177, 213, 248));
        //violet
        colorPaletteList.add(Color.argb(255, 34, 7, 78));
        colorPaletteList.add(Color.argb(255, 67, 14, 156));
        colorPaletteList.add(Color.argb(255, 101, 21, 234));
        colorPaletteList.add(Color.argb(255, 152, 99, 241));
        colorPaletteList.add(Color.argb(255, 204, 177, 248));
        //magenta
        colorPaletteList.add(Color.argb(255, 78, 7, 78));
        colorPaletteList.add(Color.argb(255, 156, 14, 156));
        colorPaletteList.add(Color.argb(255, 234, 21, 234));
        colorPaletteList.add(Color.argb(255, 241, 99, 241));
        colorPaletteList.add(Color.argb(255, 248, 177, 248));
        //magenta-red
        colorPaletteList.add(Color.argb(255, 78, 7, 43));
        colorPaletteList.add(Color.argb(255, 156, 14, 85));
        colorPaletteList.add(Color.argb(255, 234, 21, 128));
        colorPaletteList.add(Color.argb(255, 241, 99, 170));
        colorPaletteList.add(Color.argb(255, 248, 177, 213));
        //black to white
        colorPaletteList.add(Color.argb(255, 0, 0, 0));
        colorPaletteList.add(Color.argb(255, 28, 28, 28));
        colorPaletteList.add(Color.argb(255, 56, 56, 56));
        colorPaletteList.add(Color.argb(255, 84, 84, 84));
        colorPaletteList.add(Color.argb(255, 112, 112, 112));
        colorPaletteList.add(Color.argb(255, 140, 140, 140));
        colorPaletteList.add(Color.argb(255, 168, 168, 168));
        colorPaletteList.add(Color.argb(255, 196, 196, 196));
        colorPaletteList.add(Color.argb(255, 224, 224, 224));
        colorPaletteList.add(Color.argb(255, 255, 255, 255));
    }
    public void doUndo()
    {
        Bitmap newBm = myBitmap.copy(Bitmap.Config.ARGB_8888, true);
        redoStack.push(newBm);
        myBitmap = undoStack.pop();
    }
    public void doRedo()
    {
        Bitmap newBm = myBitmap.copy(Bitmap.Config.ARGB_8888, true);
        undoStack.push(newBm);
        myBitmap = redoStack.pop();
    }
    public void pushToUndoStack()
    {
        Bitmap newBm = myBitmap.copy(Bitmap.Config.ARGB_8888, true);
        undoStack.push(newBm);
        redoStack.clear();
    }
    public void doCanvasSizeSelectionDialog()
    {
        final CharSequence[] items = {"24x24 (standard)","48x48 (tiles)","24x48 (regular combat token)","48x96 (large combat token)"};
        // Creating and Building the Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this.gameContext);
        builder.setTitle("Choose a Canvas Size.");
        builder.setItems(items, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int item)
            {
                if (item == 0)
                {
                    //24x24
                    myBitmap = Bitmap.createBitmap(24, 24, Bitmap.Config.ARGB_8888);
                }
                else if (item == 1)
                {
                    //48x48
                    myBitmap = Bitmap.createBitmap(48, 48, Bitmap.Config.ARGB_8888);
                }
                else if (item == 2)
                {
                    //24x48
                    myBitmap = Bitmap.createBitmap(24, 48, Bitmap.Config.ARGB_8888);
                }
                else if (item == 3)
                {
                    //48x96
                    myBitmap = Bitmap.createBitmap(48, 96, Bitmap.Config.ARGB_8888);
                }
                ActionDialog.dismiss();
                invalidate();
            }
        });
        this.ActionDialog = builder.create();
        this.ActionDialog.show();
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        canvas.drawColor(Color.BLACK);

        //CANVAS
        if (canvasBackIndex == 0)
        {
            Rect src = new Rect(0, 0, grass.getWidth(), grass.getHeight());
            Rect dst = new Rect(0, 16 * squareSize, 48 * squareSize, (48 * squareSize) + (16 * squareSize));
            DrawBitmap(canvas, grass, src, dst);
        }
        else if (canvasBackIndex == 1)
        {
            Rect src = new Rect(0, 0, slot.getWidth(), slot.getHeight());
            Rect dst = new Rect(0, 16 * squareSize, 48 * squareSize, (48 * squareSize) + (16 * squareSize));
            DrawBitmap(canvas, slot, src, dst);
        }
        else if (canvasBackIndex == 2)
        {
            Rect src = new Rect(0, 0, button.getWidth(), button.getHeight());
            Rect dst = new Rect(0, 16 * squareSize, 48 * squareSize, (48 * squareSize) + (16 * squareSize));
            DrawBitmap(canvas, button, src, dst);
        }

        //if combat token, show idle or attack
        if (myBitmap.getWidth() != myBitmap.getHeight())
        {
            if (isIdleLayerShown) //idle layer on top
            {
                if (showLayers)
                {
                    //draw attack first with low opacity
                    Rect src = new Rect(0, myBitmap.getWidth(), myBitmap.getWidth(), myBitmap.getWidth() + myBitmap.getWidth());
                    Rect dst = new Rect(0, 16 * squareSize, 48 * squareSize, (48 * squareSize) + (16 * squareSize));
                    DrawBitmap(canvas, myBitmap, src, dst, 50);
                }
                //draw idle at full opacity
                Rect src = new Rect(0, 0, myBitmap.getWidth(), myBitmap.getWidth());
                Rect dst = new Rect(0, 16 * squareSize, 48 * squareSize, (48 * squareSize) + (16 * squareSize));
                DrawBitmap(canvas, myBitmap, src, dst);
            }
            else //attack layer on top
            {
                if (showLayers)
                {
                    //draw idle first with low opacity
                    Rect src = new Rect(0, 0, myBitmap.getWidth(), myBitmap.getWidth());
                    Rect dst = new Rect(0, 16 * squareSize, 48 * squareSize, (48 * squareSize) + (16 * squareSize));
                    DrawBitmap(canvas, myBitmap, src, dst, 50);
                }
                //draw attack at full opacity
                Rect src = new Rect(0, myBitmap.getWidth(), myBitmap.getWidth(), myBitmap.getWidth() + myBitmap.getWidth());
                Rect dst = new Rect(0, 16 * squareSize, 48 * squareSize, (48 * squareSize) + (16 * squareSize));
                DrawBitmap(canvas, myBitmap, src, dst);
            }
        }
        else
        {
            Rect src = new Rect(0, 0, myBitmap.getWidth(), myBitmap.getHeight());
            Rect dst = new Rect(0, 16 * squareSize, 48 * squareSize, (48 * squareSize) + (16 * squareSize));
            DrawBitmap(canvas, myBitmap, src, dst);
        }


        //PREVIEW
        if (previewBackIndex == 0)
        {
            Rect src = new Rect(0, 0, grass.getWidth(), grass.getHeight());
            Rect dst = new Rect(0, 8 * squareSize, 6 * squareSize, (6 * squareSize) + (8 * squareSize));
            DrawBitmap(canvas, grass, src, dst);
        }
        else if (previewBackIndex == 1)
        {
            Rect src = new Rect(0, 0, slot.getWidth(), slot.getHeight());
            Rect dst = new Rect(0, 8 * squareSize, 6 * squareSize, (6 * squareSize) + (8 * squareSize));
            DrawBitmap(canvas, slot, src, dst);
        }
        else if (previewBackIndex == 2)
        {
            Rect src = new Rect(0, 0, button.getWidth(), button.getHeight());
            Rect dst = new Rect(0, 8 * squareSize, 6 * squareSize, (6 * squareSize) + (8 * squareSize));
            DrawBitmap(canvas, button, src, dst);
        }

        //if combat token, show idle or attack
        if (myBitmap.getWidth() != myBitmap.getHeight())
        {
            if (isIdleLayerShown) //idle layer on top
            {
                Rect src = new Rect(0, 0, myBitmap.getWidth(), myBitmap.getWidth());
                Rect dst = new Rect(0, 8 * squareSize, 6 * squareSize, (6 * squareSize) + (8 * squareSize));
                DrawBitmap(canvas, myBitmap, src, dst);
            }
            else //attack layer on top
            {
                Rect src = new Rect(0, myBitmap.getWidth(), myBitmap.getWidth(), myBitmap.getWidth() + myBitmap.getWidth());
                Rect dst = new Rect(0, 8 * squareSize, 6 * squareSize, (6 * squareSize) + (8 * squareSize));
                DrawBitmap(canvas, myBitmap, src, dst);
            }
        }
        else
        {
            Rect src = new Rect(0, 0, myBitmap.getWidth(), myBitmap.getHeight());
            Rect dst = new Rect(0, 8 * squareSize, 6 * squareSize, (6 * squareSize) + (8 * squareSize));
            DrawBitmap(canvas, myBitmap, src, dst);
        }

        DrawColoredRectangle(canvas, currentColor, 43 * squareSize, 66 * squareSize, 4 * squareSize, 4 * squareSize);

        //CONTROLS
        palette.Draw(canvas);
        btnNew.Draw(canvas);
        btnOpen.Draw(canvas);
        btnSave.Draw(canvas);
        btnSaveAs.Draw(canvas);
        btnToggleLayer.Draw(canvas);
        btnShowLayers.Draw(canvas);
        btnAlphaAdjust.Draw(canvas);
        btnEraser.Draw(canvas);
        btnGetColor.Draw(canvas);
        btnCanvasBackground.Draw(canvas);
        btnPreviewBackground.Draw(canvas);
        btnUndo.Draw(canvas);
        btnRedo.Draw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        try
        {
            int eventAction = event.getAction();
            switch (eventAction)
            {
                case MotionEvent.ACTION_UP:
                    int x = (int) event.getX() - oXshift;
                    int y = (int) event.getY() - oYshift;

                    if (continuousDrawMode)
                    {
                        continuousDrawMode = false;
                    }

                    if (btnSaveAs.getImpact(x, y))
                    {
                        performHapticFeedback(android.view.HapticFeedbackConstants.VIRTUAL_KEY);
                        doSaveAsDialog();
                    }
                    else if (btnSave.getImpact(x, y))
                    {
                        performHapticFeedback(android.view.HapticFeedbackConstants.VIRTUAL_KEY);
                        doSaveDialog();
                    }
                    else if (btnOpen.getImpact(x, y))
                    {
                        performHapticFeedback(android.view.HapticFeedbackConstants.VIRTUAL_KEY);
                        doOpenDialog();
                        undoStack.clear();
                        redoStack.clear();
                    }
                    else if (btnUndo.getImpact(x, y))
                    {
                        if (!undoStack.isEmpty())
                        {
                            doUndo();
                        }
                    }
                    else if (btnRedo.getImpact(x, y))
                    {
                        if (!redoStack.isEmpty())
                        {
                            doRedo();
                        }
                    }
                    else if (btnNew.getImpact(x, y))
                    {
                        performHapticFeedback(android.view.HapticFeedbackConstants.VIRTUAL_KEY);
                        new AlertDialog.Builder(this.gameContext)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setTitle("New Canvas")
                                .setMessage("Are you sure you want start a new canvas (existing drawing will be erased)?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        doCanvasSizeSelectionDialog();
                                        //myBitmap = Bitmap.createBitmap(24, 24, Bitmap.Config.ARGB_8888);
                                        undoStack.clear();
                                        redoStack.clear();
                                        invalidate();
                                    }
                                })
                                .setNegativeButton("No", null)
                                .show();
                    }
                    else if (btnAlphaAdjust.getImpact(x, y))
                    {
                        pushToUndoStack();
                        doAlphaAdjust();
                    }
                    else if (btnToggleLayer.getImpact(x, y))
                    {
                        if (isIdleLayerShown)
                        {
                            isIdleLayerShown = false;
                            btnToggleLayer.Text = "Attack";
                        }
                        else
                        {
                            isIdleLayerShown = true;
                            btnToggleLayer.Text = "Idle";
                        }
                    }
                    else if (btnShowLayers.getImpact(x, y))
                    {
                        if (showLayers)
                        {
                            showLayers = false;
                            btnShowLayers.Text = "HideLyrs";
                        }
                        else
                        {
                            showLayers = true;
                            btnShowLayers.Text = "ShowLyrs";
                        }
                    }
                    else if (btnGetColor.getImpact(x, y))
                    {
                        if (getColorMode)
                        {
                            getColorMode = false;
                            btnGetColor.Img = BitmapFactory.decodeResource(getResources(), R.mipmap.btn_small);
                        }
                        else
                        {
                            getColorMode = true;
                            btnGetColor.Img = BitmapFactory.decodeResource(getResources(), R.mipmap.item_slot);
                        }
                    }
                    else if (palette.getImpact(x, y))
                    {
                        int clrClicked = palette.getColorIndexClicked(x,y);
                        if (clrClicked != -1)
                        {
                            //set current color to color index returned
                            currentColor = colorPaletteList.get(clrClicked);
                        }
                    }
                    else if (btnEraser.getImpact(x, y))
                    {
                        currentColor = Color.argb(0, 0, 0, 0);
                    }
                    else if (btnCanvasBackground.getImpact(x, y))
                    {
                        canvasBackIndex++;
                        if (canvasBackIndex > 2) { canvasBackIndex = 0; }
                    }
                    else if (btnPreviewBackground.getImpact(x, y))
                    {
                        previewBackIndex++;
                        if (previewBackIndex > 2) { previewBackIndex = 0; }
                    }

                    break;

                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_MOVE:
                    x = (int) event.getX() - oXshift;
                    y = (int) event.getY() - oYshift;

                    if (getColorMode)
                    {
                        //check to see if in drawing area first
                        if (isInDrawingArea(x, y))
                        {
                            //check to see if combat token
                            if (myBitmap.getWidth() != myBitmap.getHeight()) //combat token so get color from active layer
                            {
                                if (isIdleLayerShown) //get color from idle layer
                                {
                                    int pixelSize = 48 / myBitmap.getWidth() * squareSize;
                                    currentColor = myBitmap.getPixel(x / pixelSize, (y - (16 * squareSize)) / pixelSize);
                                }
                                else //get color from attack layer
                                {
                                    int pixelSize = 48 / myBitmap.getWidth() * squareSize;
                                    currentColor = myBitmap.getPixel(x / pixelSize, ((y - (16 * squareSize)) / pixelSize) + myBitmap.getWidth());
                                }
                            }
                            else //not a combat token
                            {
                                int pixelSize = 48 / myBitmap.getWidth() * squareSize;
                                currentColor = myBitmap.getPixel(x / pixelSize, (y - (16 * squareSize)) / pixelSize);
                            }
                        }
                    }
                    else //drawing mode
                    {
                        //check to see if in drawing area first
                        if (isInDrawingArea(x, y))
                        {
                            if (!continuousDrawMode)
                            {
                                pushToUndoStack();
                                continuousDrawMode = true;
                            }
                        }
                        if (isInDrawingArea(x, y))
                        {
                            if (myBitmap.getWidth() != myBitmap.getHeight()) //combat token so draw on active layer
                            {
                                if (isIdleLayerShown) //draw on idle layer
                                {
                                    int pixelSize = 48 / myBitmap.getWidth() * squareSize;
                                    myBitmap.setPixel(x / pixelSize, (y - (16 * squareSize)) / pixelSize, currentColor);
                                }
                                else //draw on attack layer
                                {
                                    int pixelSize = 48 / myBitmap.getWidth() * squareSize;
                                    myBitmap.setPixel(x / pixelSize, ((y - (16 * squareSize)) / pixelSize) + myBitmap.getWidth(), currentColor);
                                }
                            }
                            else //not combat token
                            {
                                int pixelSize = 48 / myBitmap.getWidth() * squareSize;
                                myBitmap.setPixel(x / pixelSize, (y - (16 * squareSize)) / pixelSize, currentColor);
                            }
                        }
                    }
                    break;
            }
            invalidate();

        }
        catch (Exception ex)
        {
            //print exception
        }
        return true;
    }

    public boolean isInDrawingArea(int x, int y)
    {
        if ((x > 0) && (x < 48 * squareSize) && (y > 16 * squareSize) && (y < (48 * squareSize) + (16 * squareSize)))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public void DrawText(Canvas canvas, String text, float x, float y, Paint pnt)
    {
        canvas.drawText(text, x + oXshift, y + oYshift, pnt);
    }
    public void DrawBitmap(Canvas canvas, Bitmap bitmap, Rect source, Rect target)
    {
        Rect dst = new Rect(target.left + oXshift, target.top + oYshift, target.width() + target.left + oXshift, target.height() + target.top + oYshift);
        Rect src = new Rect(source.left, source.top, source.width() + source.left, source.height() + source.top);
        canvas.drawBitmap(bitmap, src, dst, bmPaint);
    }
    public void DrawBitmap(Canvas canvas, Bitmap bitmap, Rect source, Rect target, int opacity)
    {
        bmOpacityPaint.setAlpha(opacity);
        Rect dst = new Rect(target.left + oXshift, target.top + oYshift, target.width() + target.left + oXshift, target.height() + target.top + oYshift);
        Rect src = new Rect(source.left, source.top, source.width() + source.left, source.height() + source.top);
        canvas.drawBitmap(bitmap, src, dst, bmOpacityPaint);
    }
    public void DrawColoredRectangle(Canvas canvas, int color, int xLoc, int yLoc, int width, int height)
    {
        rectPaint.setColor(color);
        canvas.drawRect(xLoc + oXshift, yLoc + oYshift, width + xLoc + oXshift, height + yLoc + oYshift, rectPaint);
    }
}


package com.iceblinkengine.ibb.ibminipixelartmaker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;

/**
 * Created by Slowdive on 9/11/2016.
 */
public class IbButton
{
    public Bitmap Img = null;    //this is the normal button and color intensity
    public String Text = "";
    public int X = 0;
    public int Y = 0;
    public int Width = 0;
    public int Height = 0;
    public float scaler = 1.0f;
    private Paint mUiTextPaint = null;
    private Context gameContext;
    public GameView gv;

    public IbButton(Context gmContext, GameView g, float sc)
    {
        gv = g;
        gameContext = gmContext;
        scaler = sc;
        mUiTextPaint = new Paint();
        mUiTextPaint.setStyle(Paint.Style.FILL);
        mUiTextPaint.setColor(Color.WHITE);
        mUiTextPaint.setAntiAlias(true);
        mUiTextPaint.setShadowLayer(10.0f, 0, 0, Color.BLACK);
        Typeface uiTypeface = Typeface.SANS_SERIF;
        mUiTextPaint.setTypeface(uiTypeface);
        mUiTextPaint.setTextSize(gv.drawFontRegHeight * scaler);
    }

    public boolean getImpact(int x, int y)
    {
        if ((x >= X) && (x <= (X + this.Width)))
        {
            if ((y >= Y) && (y <= (Y + this.Height)))
            {
                return true;
            }
        }
        return false;
    }

    public void Draw(Canvas canvas)
    {
        int pH = (int)((float)gv.screenHeight / 200.0f);
        int pW = (int)((float)gv.screenHeight / 200.0f);
        float fSize = (float)(gv.squareSize / 4) * scaler;

        Rect src = new Rect(0, 0, this.Img.getWidth(), this.Img.getHeight());
        Rect dst = new Rect(this.X, this.Y, this.Width + this.X, this.Height + this.Y);

        gv.DrawBitmap(canvas, this.Img, src, dst);

        //DRAW button text
        Rect bounds = new Rect();
        mUiTextPaint.getTextBounds(Text, 0, Text.length(), bounds);
        //place in the center
        float ulX = (this.Width - bounds.width()) / 2;
        float ulY = (this.Height + bounds.height()) / 2;
        gv.DrawText(canvas, Text, this.X + ulX, this.Y + ulY, mUiTextPaint);
    }
}

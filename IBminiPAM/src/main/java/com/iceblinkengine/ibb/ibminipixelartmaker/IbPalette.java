package com.iceblinkengine.ibb.ibminipixelartmaker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;

/**
 * Created by Slowdive on 9/12/2016.
 */
public class IbPalette
{
    public Bitmap Img = null;    //this is the normal button and color intensity
    public int X = 0;
    public int Y = 0;
    public int Width = 0;
    public int Height = 0;
    public GameView gv;

    public IbPalette(GameView g)
    {
        gv = g;
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

    public int getColorIndexClicked(int x, int y)
    {
        int squareSize = this.Width / 13;
        int col = (x - X) / squareSize;
        int row = (y - Y) / squareSize;
        int clickedIndex = col * 5 + row;
        if ((clickedIndex >= 0) && (clickedIndex <= 65))
        {
            return clickedIndex;
        }
        return -1;
    }

    public void Draw(Canvas canvas)
    {
        Rect src = new Rect(0, 0, this.Img.getWidth(), this.Img.getHeight());
        Rect dst = new Rect(this.X, this.Y, this.Width + this.X, this.Height + this.Y);
        gv.DrawBitmap(canvas, this.Img, src, dst);
    }
}

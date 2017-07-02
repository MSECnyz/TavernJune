package com.msecnyz.tavernjune.customview;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ImageView;

public class ArcImageView extends ImageView {

    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Bitmap mRawBitmap;
    private BitmapShader mShader;
    private Matrix mMatrix = new Matrix();

    public ArcImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        //画笔颜色
        paint.setColor(Color.BLACK);
        //画笔粗细
        paint.setStrokeWidth(6);
        //抗锯齿
        paint.setAntiAlias(true);
    }
    /*
        *
      *躺会，躺会
        *
    */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}

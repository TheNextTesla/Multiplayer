package independent_study.multiplayer.draw;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

import independent_study.multiplayer.gui.GameActivity;

public class PaintView extends View
{
    private Path drawPath;
    private Path tempPath;
    private ArrayList<PointF> points;
    private Paint drawPaint;
    private Paint canvasPaint;
    private int color;
    private Canvas drawCanvas;
    private Bitmap canvasBitmap;

    private GameActivity gameActivity;

    public PaintView(Context context, AttributeSet attributeSet, GameActivity gameActivity)
    {
        super(context, attributeSet);
        drawPath = new Path();
        drawPaint = new Paint();
        canvasPaint = new Paint(Paint.DITHER_FLAG);
        color = Color.argb(255, getRandomIntColor(), getRandomIntColor(), getRandomIntColor());
        drawPaint.setColor(color);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(20);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);

        this.gameActivity = gameActivity;
        points = new ArrayList<>();
    }

    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
        points.clear();
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        if(tempPath != null)
        {
            canvas.drawPath(tempPath, drawPaint);
            tempPath = null;
        }
        else
        {
            canvas.drawPath(drawPath, drawPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        float touchX = event.getX();
        float touchY = event.getY();

        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                drawPath.moveTo(touchX, touchY);
                points.clear();
                points.add(new PointF(touchX, touchY));
                break;
            case MotionEvent.ACTION_MOVE:
                drawPath.lineTo(touchX, touchY);
                points.add(new PointF(touchX, touchY));
                break;
            case MotionEvent.ACTION_UP:
                drawCanvas.drawPath(drawPath, drawPaint);
                points.add(new PointF(touchX, touchY));
                gameActivity.onDrawPathCreated(points);
                points.clear();
                drawPath.reset();
                break;
            default:
                return false;
        }
        invalidate();
        return true;
    }

    public void drawPointsPath(ArrayList<PointF> points)
    {
        tempPath = new Path();
        for(int i = 0; i < points.size(); i++)
        {
            if(i == 0)
                tempPath.moveTo(points.get(i).x, points.get(i).y);
            else
                tempPath.lineTo(points.get(i).x, points.get(i).y);
        }
        invalidate();
    }

    public Path getDrawPath()
    {
        return drawPath;
    }

    private int getRandomIntColor()
    {
        return (int)(Math.random() * 256);
    }
}

package com.example.canvastraining;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.net.CookieHandler;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new MyView(this));
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

    }
}

class MyView extends View {

    public MyView(Context context) {
        super(context);
    }

    protected void onDraw(@NonNull Canvas canvas) {
// в будущем для солнца
//        for (int yi = y; yi >= y - h; yi -= span) {
//            int stopY = Math.min(yi - yDiff, y);
//            canvas.drawLine(x, yi, x + w, stopY, paint);
//        }
        canvas.drawColor(Color.WHITE);
        int y = getBottom();
        int x = getRight();
        int x0 = getLeft();

        // трава
        drawRectWithStroke(x0, y, x, 200, Color.parseColor("#088404"), canvas);

        // дом
        drawRectWithStroke(x / 7, y - 50, 250, 250, Color.parseColor("#804000"), canvas);
        drawFrameWithGrid(x / 7 + 125, y - 50, 100, 190, -35, 15, 1, Color.WHITE, canvas);
        // окно
        drawWindow(x / 7 + 10, y - 125, 90, 115, 18, 2, Color.BLUE, canvas);
        // крыша
        drawTriangle(x / 7, y - 300, x / 7 + 125, y - 450, x / 7 + 250, y - 300, Color.parseColor("#804000"), canvas);
        // окошко на крыше
        drawCircleWithGrid(x / 7 + 125, y - 355, 40, -45, 12, 1, Color.BLUE, canvas);
        // дерево
        drawRect(x / 2 - 12, y - 50, 24, 50, Color.parseColor("#7b3f00"), canvas);
        drawOvalWithStroke(x / 2 - 125, y - 550, x / 2 + 125, y - 80, 2, Color.GREEN, canvas);
        // солнышко
        drawSun(x0 + 30, 30, 50, 3, 200, canvas);

        // лавочка
        drawRectWithStroke(x - x / 7, y - 125, -250, 20, Color.GRAY, canvas);
        drawRectWithStroke(x - x / 7 - 125 + 40, y - 125, 30, -50, Color.GRAY, canvas);
        drawRectWithStroke(x - x / 7 - 125 - 40, y - 125, -30, -50, Color.GRAY, canvas);

    }

    static Rect drawRect(int x, int y, int w, int h, int color, Canvas canvas) {
        Rect rect = new Rect(x, y - h, x + w, y);
        Paint paint = new Paint();
        paint.setColor(color);
        canvas.drawRect(rect, paint);
        paint.reset();

        return rect;
    }

    static void drawRectWithStroke(int x, int y, int w, int h, int color, Canvas canvas) {
        Rect rect = drawRect(x, y, w, h, color, canvas);
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
        canvas.drawRect(rect, paint);
    }


    static void drawFrameWithGrid(int x, int y, int w, int h, int angleGridDegrees, int span, int strokeWidth, int color, Canvas canvas) {
        Paint paint = new Paint();

        paint.reset();
        paint.setStrokeWidth(strokeWidth);
        paint.setColor(color);
        double angle = Math.toRadians(180 - angleGridDegrees);
        int yDiff = (int) (Math.tan(angle) * w);
        for (int startY = y + Math.abs(yDiff), i = 0; startY >= y - h - Math.abs(yDiff); i++, startY = y + Math.abs(yDiff) - i * span) { //наносим штрихофку с запасом
            int stopY = startY + yDiff;
            int stopX = x + w;
            int startX = x;

            //конечная точка вышла за границы
            if (stopY < (y - h)) {
                stopY = y - h;
                stopX = x + (int) (-1 / Math.tan(angle) * (startY - (y - h)));
            }
            else if (stopY > y) {
                stopY = y;
                stopX = x + (int) (1 / Math.tan(angle) * (y - startY));
            }

            // начальная точка вышла за границы при положительном угле
            if (startY > y && angleGridDegrees >= 0) {
                startY = y;
                startX = x + w - (int) (-1 / Math.tan(angle) * (startY - stopY));
            }
            // начальная точка вышла за границы при отрицательном угле
            if (startY < y - h && angleGridDegrees < 0) {
                startY = y - h;
                startX = x + w - (int) (1 / Math.tan(angle) * (stopY - startY));
            }

            // фильтруем оставшиеся артефакты
            if (stopY >= y - h && stopY <= y && startY >= y - h && startY <= y ) canvas.drawLine(startX, startY, stopX, stopY, paint);
            //canvas.drawLine(startX, startY, stopX, stopY, paint);
        }

        // рисуем рамку
        paint.reset();
        Rect rect = new Rect(x, y - h, x + w, y);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
        canvas.drawRect(rect, paint);
    }

    static void drawWindow(int x, int y, int w, int h, int span, int strokeWidth, int color, Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setStrokeWidth(strokeWidth);

        // вертикальные линии
        for (int ix = x; ix <= x + w; ix += span) {
            canvas.drawLine(ix, y, ix, y - h, paint);
        }
        paint.reset();
        // горизонтальные линии и рамка
        drawFrameWithGrid(x, y, w, h, 0, span, strokeWidth, color, canvas);
    }

    static void drawTriangle(int x1, int y1, int x2, int y2, int x3, int y3, int color, Canvas canvas) {
        Paint paint = new Paint();
        Path path = new Path();

        // рамка
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(2);
        path.moveTo(x1,y1);
        path.lineTo(x2,y2);
        path.lineTo(x3,y3);
        canvas.drawPath(path, paint);

        // треугольник
        paint.reset();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(color);
        path.reset();
        path.moveTo(x1,y1);
        path.lineTo(x2,y2);
        path.lineTo(x3,y3);
        canvas.drawPath(path, paint);
    }

    static void drawCircleWithGrid(int x, int y, int r, int angleGridDegrees, int span, int strokeWidth, int color, Canvas canvas) {
        Paint paint = new Paint();

        paint.setStrokeWidth(strokeWidth);
        paint.setColor(color);
        double angle = Math.toRadians(angleGridDegrees);
        // наносим штрихофку
        for (int startY = 0, startX = -r; startX <= r; startX += span) {
            int h = (int) Math.sqrt(r * r - startX * startX);
            int relativeX2 = (int) (startX * Math.cos(angle) - (startY + h) * Math.sin(angle)); // матрица
            int relativeY2 = (int) (startX * Math.sin(angle) + (startY + h) * Math.cos(angle)); // | cosA -sinA | |x|
            int relativeX1 = (int) (startX * Math.cos(angle) - (startY - h) * Math.sin(angle)); // | sinA cosA  | |y|
            int relativeY1 = (int) (startX * Math.sin(angle) + (startY - h) * Math.cos(angle));
            canvas.drawLine(x + relativeX1, y + relativeY1, x + relativeX2, y + relativeY2, paint);
        }

        // рамка
        paint.reset();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
        paint.setColor(Color.BLACK);
        canvas.drawCircle(x, y, r, paint);
    }

    static void drawOvalWithStroke(int left, int top, int right,  int bottom, int strokeWidth, int color, Canvas canvas) {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(color);
        canvas.drawOval(left, top, right, bottom, paint);

        paint.reset();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(strokeWidth);
        paint.setColor(Color.BLACK);
        canvas.drawOval(left, top, right, bottom, paint);

    }

    static void drawSun(int x, int y, int r, int spanInDegrees, int linesLength, Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.YELLOW);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(2);
        canvas.drawCircle(x, y, r, paint);

        for (double degree = Math.toRadians(15); degree <= Math.toRadians(75); degree += Math.toRadians(spanInDegrees)) {
            int relativeX = (int) (linesLength * Math.cos(degree));
            int relativeY = (int) (linesLength * Math.sin(degree));
            canvas.drawLine(x, y, x + relativeX, y + relativeY, paint);
        }
    }
}
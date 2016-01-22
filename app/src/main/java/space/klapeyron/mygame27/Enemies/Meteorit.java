package space.klapeyron.mygame27.Enemies;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.Random;

import space.klapeyron.mygame27.MainActivity;

public class Meteorit {
    private static Bitmap bmp;
    public static int bmpHeight = 0;
    public static int bmpWidth = 0;

    private int X;
    private int Y;

    private int speed = 4;

    public Meteorit() {
        Y = - bmp.getHeight();

        Random random = new Random();
        X = random.nextInt(MainActivity.screenWidth-bmpWidth);
    }

    public void onDraw(Canvas canvas) {
        updateY();
        canvas.drawBitmap(bmp, X, Y, null);
    }

    public void updateY() {
        Y += speed;
    }

    public static void setBmp(Bitmap bmpp) {

        bmp = bmpp;
        bmpHeight = bmp.getHeight();
        bmpWidth = bmp.getWidth();
    }

    public int getX() {
        return X;
    }

    public int getY() {
        return Y;
    }
}

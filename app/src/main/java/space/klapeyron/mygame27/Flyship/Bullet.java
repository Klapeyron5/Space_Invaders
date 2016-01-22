package space.klapeyron.mygame27.Flyship;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Bullet {
    private static Bitmap bmp;
    public static int bmpHeight = 0;
    public static int bmpWidth = 0;

    private int X;
    private int Y;

    private int speed = 8;

    public Bullet(FlyShip flyShip, int g) {
        X = flyShip.getAbsX() + 21;
        if(g == 1)
            X += 8;
        Y = flyShip.getAbsY();
    }

    public void onDraw(Canvas canvas) {
        canvas.drawBitmap(bmp, X, Y, null);
    }

    public void updateY() {
        Y -= speed;
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
package space.klapeyron.mygame27.Flyship;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.ArrayList;

import space.klapeyron.mygame27.GameView;
import space.klapeyron.mygame27.MainActivity;


public class FlyShip {

    public Bitmap bmp;
    public static int bmpHeight = 0;
    public static int bmpWidth = 0;

    private int X;
    private int Y;

    private int taskX;
    private int taskY;

    public static final int offsetY = 50;

    private final int speed = 1000;
    private int speedX = 0;
    private int speedY = 0;
    private int distX_per_dT = (int) Math.round(speedX / GameView.FPS);
    private int distY_per_dT = (int) Math.round(speedY / GameView.FPS);
    private final int DISTANCE_PER_dT = (int) Math.round(speed / GameView.FPS);

    private static int rate = 20;
    private int shootCounter = 0;
    private int pairedCounter = 0;

    public ArrayList<Bullet> bullets = new ArrayList<Bullet>();
    
    public FlyShip(Bitmap bmp) {
        this.bmp = bmp;
        bmpHeight = bmp.getHeight();
        bmpWidth = bmp.getWidth();

        X = MainActivity.screenWidth / 2 - this.bmp.getWidth() / 2;
        Y = MainActivity.screenHeight - this.bmp.getHeight() - 210;
        taskX = X;
        taskY = Y;

        int sizeX = bmp.getWidth() / 3;
        int sizeY = bmp.getHeight() / 3;
        this.bmp.createScaledBitmap(bmp, sizeX, sizeY, false);
    }

    public void onDraw(Canvas canvas) {//}, int X, int Y) {

        update();

   //     int rWidth = bmp.getWidth();//*2;
   //     int rHeight = bmp.getHeight();//*2;


   //     Bitmap bmp1 = bmp.createScaledBitmap(bmp,rWidth,rHeight,false);
        canvas.drawBitmap(bmp, X, Y, null);


        for(int i = 0; i < bullets.size(); i++)
            bullets.get(i).onDraw(canvas);

    }

    private void update() {

        double zz = Math.pow(Math.pow(taskX - X, 2) + Math.pow(taskY - Y, 2), 0.5);

        speedX = (int) Math.round(speed * (taskX - X) / zz);
        speedY = (int) Math.round(speed * (taskY - Y) / zz);
        distX_per_dT = (int) Math.round(speedX / GameView.FPS);
        distY_per_dT = (int) Math.round(speedY / GameView.FPS);

        if(Math.pow(Math.pow(distX_per_dT, 2) + Math.pow(distY_per_dT, 2), 0.5) < zz) {
            X += distX_per_dT;
            Y += distY_per_dT;
        } else {
            X = taskX;
            Y = taskY;
        }

        if(shootCounter == rate) {
            onShoot();
            shootCounter = 0;
        }
        shootCounter += 1;

    /*    if(this.X != taskX)
            if(DISTANCE_PER_dT < Math.abs(taskX - this.X))
                this.X += directionX * DISTANCE_PER_dT;
            else
                this.X = taskX;

        if(this.Y != taskY)
            if(DISTANCE_PER_dT < Math.abs(taskY - this.Y))
                this.Y += directionY * DISTANCE_PER_dT;
            else
                this.Y = taskY;*/

        //update bullets not from Bullet, because must to removed from bullets ArrayList and number of Bullet don't knew
     /*   for(int i = 0; i < bullets.size(); i++) {
            if(bullets.get(i).getY() > - Bullet.bmpHeight)
                bullets.get(i).updateY();
            else
                bullets.remove(i);
        }*/

        for(int i = bullets.size()-1; i >= 0; i--) {
            if(bullets.get(i).getY() > - Bullet.bmpHeight)
                bullets.get(i).updateY();
            else
                bullets.remove(i);
        }
    }

    public void setTask(int X, int Y) {
        taskX = X - bmpHeight/2;
        taskY = Y - bmpWidth/2 - offsetY;
    }

    /*
    *TODO
     */
    public void onShoot() {
        Bullet bullet = new Bullet(this,pairedCounter);
        if(pairedCounter == 0)
            pairedCounter = 1;
        else
            pairedCounter = 0;
        bullets.add(bullet);
    }

    public void setX(int X) {
        this.X = X;
    }

    public void setY(int Y) {
        this.Y = Y;
    }

    public int getX() {
        return this.X + bmpHeight/2;
    }

    public int getY() {
        return this.Y + bmpWidth/2;
    }

    public int getAbsX() {
        return this.X;
    }

    public int getAbsY() {
        return this.Y;
    }
}

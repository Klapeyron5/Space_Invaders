package space.klapeyron.mygame27;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;

import space.klapeyron.mygame27.Enemies.Meteorit;
import space.klapeyron.mygame27.Flyship.Bullet;
import space.klapeyron.mygame27.Flyship.FlyShip;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {


    GameView gameViewLink = this;
    MainActivity mainActivityLink;
    PlayThread playThread;
    Bitmap background;
    FlyShip flyship;
    ArrayList<Meteorit> meteorits = new ArrayList<>();

    private static int scores = 0;

    private int enemiesInWave = 5;
    private int[] enemiesInWaveCollection = {5,8,11,15,20,50,300,400};
    private int countEnemyInWave = 0;
    private int meteoritsInSecond = 1;
    private int[] meteoritsInSecondCollection = {1,2,4,5,8,10,20,40};
    private int numberWave = 0;

    public static final String bgTag = "bgTag";

    /*
     *frames per second
     */
    public final static int FPS = 80;
    /*
     *the interval of integration in milliseconds
     */
    public final static long dT = 1000 / FPS;


    public GameView(Context context, MainActivity mainActivity) {
        super(context);
        getHolder().addCallback(this);
        mainActivityLink = mainActivity;

        background = BitmapFactory.decodeResource(getResources(), R.drawable.bg);
        background = resizeImage(background, MainActivity.screenWidth, MainActivity.screenHeight);
        flyship = new FlyShip(BitmapFactory.decodeResource(getResources(), R.drawable.qw));
        Bullet.setBmp(BitmapFactory.decodeResource(getResources(), R.drawable.bullet));
        Meteorit.setBmp(BitmapFactory.decodeResource(getResources(), R.drawable.meteorite1));
        scores = 0;
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        playThread = new PlayThread();
        playThread.setRunning(true);
        playThread.start();
    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }


    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        while (retry) {
            try {
                playThread.setRunning(false);
                playThread.join();
                retry = false;
            } catch (InterruptedException e) {
            }
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if(MainActivity.CURRENT_STATE != MainActivity.STATE_DEFEAT) {
            switch (e.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    flyship.setTask((int) e.getX(), (int) e.getY());
                    break;
                case MotionEvent.ACTION_MOVE:
                    flyship.setTask((int) e.getX(), (int) e.getY());
                    break;
                case MotionEvent.ACTION_UP:
                    flyship.setTask(flyship.getX(), flyship.getY() + FlyShip.offsetY);
                    break;
            }
        }
        return true;
    }


    class PlayThread extends Thread {
        private boolean running = false;
        Canvas canvas;
        private int countdT = 0;
        public PlayThread(){}

        @Override
        public void run() {
            while(true) {
                if(running) {
                    canvas = gameViewLink.getHolder().lockCanvas();
                    //TODO
                    if(MainActivity.CURRENT_STATE != MainActivity.STATE_DEFEAT)
                        onDrawPlay();
                    else
                        onDrawDefeat();
                    gameViewLink.getHolder().unlockCanvasAndPost(canvas);
                    try {
                        Thread.sleep(dT, 0);
                    } catch (InterruptedException e) {
                    }
                } else {
                    return;
                }
            }
        }


        private void onDrawPlay() {
            canvas.drawBitmap(background,0,0,null);
            //    canvas.drawColor(Color.BLACK);
            flyship.onDraw(canvas);

            //TODO
            for(int i=0;i< meteorits.size();i++) {
                meteorits.get(i).onDraw(canvas);
            }
            for(int i=0;i< meteorits.size();i++) {
                Meteorit m = meteorits.get(i);
                for(int j=0;j<flyship.bullets.size();j++) {
                    Bullet b = flyship.bullets.get(j);
                    if(((m.getX() < b.getX())&&(b.getX() - m.getX() < Meteorit.bmpWidth - 2)) ||
                            ((b.getX() < m.getX())&&(m.getX() - b.getX() < Bullet.bmpWidth - 2)))
                        if(((m.getY() < b.getY())&&(b.getY() - m.getY() < Meteorit.bmpHeight - 2)) ||
                                ((b.getY() < m.getY())&&(m.getY() - b.getY() < Bullet.bmpHeight - 2))) {
                            meteorits.remove(i);
                            flyship.bullets.remove(j);
                            i--;
                            j--;
                            scores++;
                            break;
                        }
                }
            }

            for(int i=0;i< meteorits.size();i++) {
                Meteorit m = meteorits.get(i);
                if(((m.getX() < flyship.getAbsX())&&(flyship.getAbsX() - m.getX() < Meteorit.bmpWidth - 8)) ||
                        ((flyship.getAbsX() < m.getX())&&(m.getX() - flyship.getAbsX() < FlyShip.bmpWidth - 8)))
                    if(((m.getY() < flyship.getAbsY())&&(flyship.getAbsY() - m.getY() < Meteorit.bmpHeight - 8)) ||
                            ((flyship.getAbsY() < m.getY())&&(m.getY() - flyship.getAbsY() < FlyShip.bmpHeight - 8))) {
              //          setRunning(false);
                        mainActivityLink.setState(MainActivity.STATE_DEFEAT);
                    }
                if(meteorits.get(i).getY() > MainActivity.screenHeight) {
                    meteorits.remove(i);
                    i--;
                }
            }

            countdT++;
            if(countdT == ((int) FPS / meteoritsInSecond)) {
                countdT = 0;
                Meteorit meteorit = new Meteorit();
                meteorits.add(meteorit);
                countEnemyInWave++;
                if(countEnemyInWave == enemiesInWave) {
                    countEnemyInWave = 0;
                    if(numberWave < 7)
                        numberWave++;
                    enemiesInWave = enemiesInWaveCollection[numberWave];
                    meteoritsInSecond = meteoritsInSecondCollection[numberWave];
                }
            }
        }


        private void onDrawDefeat() {
            canvas.drawBitmap(background, 0, 0, null);
            Paint paint = new Paint();
            paint.setColor(Color.WHITE);
            paint.setTextSize(25);
            canvas.drawText("GAME OVER",200,200,paint);
            canvas.drawText("scores: " + scores, 220, 240, paint);
            for(int i=0;i< meteorits.size();i++) {
                meteorits.get(i).onDraw(canvas);
                if (meteorits.get(i).getY() > MainActivity.screenHeight) {
                    meteorits.remove(i);
                    i--;
                }
            }
        }


        public void setRunning(boolean b) {
            running = b;
        }
    }


    public int getScores() {
        return scores;
    }


    public Bitmap resizeImage(Bitmap image, int maxWidth, int maxHeight) {
        Bitmap resizedImage = null;
        try {
            resizedImage = Bitmap.createScaledBitmap(image, MainActivity.screenWidth, MainActivity.screenHeight, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resizedImage;
    }
}

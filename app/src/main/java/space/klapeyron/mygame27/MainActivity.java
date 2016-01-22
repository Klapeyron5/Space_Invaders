package space.klapeyron.mygame27;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

    public static int screenWidth;
    public static int screenHeight;

    public static int CURRENT_STATE = 0;
    public final static int STATE_MENU = 0;
    public final static int STATE_PLAY = 1;
    public final static int STATE_PAUSE = 2;
    public final static int STATE_DEFEAT = 3;
    public final static int STATE_RECORDS = 4;
    public final static int STATE_CONTACTS = 5;

    /*
     *SurfaceView for often rendering playing game
     */
    GameView gameView;
    /*
     *Absolute record (save for current device)
     */
    private int absRecord = 0;
    private final String keyAbsRecord = "keyAbsRecord";

    MainActivity link = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        screenHeight = metrics.heightPixels;
        screenWidth = metrics.widthPixels;

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        if(savedInstanceState != null) {
            absRecord = savedInstanceState.getInt(keyAbsRecord);
        }
        else {
            absRecord = 0;
        }

        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        absRecord = sharedPref.getInt(keyAbsRecord, 0);

        setState(STATE_MENU);
    }


    @Override
    protected void onPause() {
        super.onPause();
        switch(CURRENT_STATE) {
            case STATE_MENU:
                break;
            case STATE_PLAY:
                setState(STATE_PAUSE);
                break;
            case STATE_PAUSE:
                break;
            case STATE_DEFEAT:
                setState(STATE_PAUSE);
                setState(STATE_MENU);
                break;
            case STATE_RECORDS:
                break;
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //replaces the default 'Back' button action
        if(keyCode == KeyEvent.KEYCODE_BACK)
        {
            switch(CURRENT_STATE) {
                case STATE_MENU:
                    openQuitDialog();
                    break;
                case STATE_PLAY:
                    setState(STATE_PAUSE);
                    break;
                case STATE_PAUSE:
                    setState(STATE_PLAY);
                    break;
                case STATE_DEFEAT:
                    setState(STATE_PAUSE);
                    setState(STATE_MENU);
                    break;
                case STATE_RECORDS:
                    setState(STATE_MENU);
                    break;
                case STATE_CONTACTS:
                    setState(STATE_MENU);
                    break;
            }
        }
        return false;
    }


    public void setState(int STATE) {
        switch(STATE) {
            case STATE_MENU:
                CURRENT_STATE = STATE_MENU;
                setMenu();
                break;
            case STATE_PLAY:
                if(CURRENT_STATE == STATE_MENU)
                    gameView = new GameView(link, this);
                CURRENT_STATE = STATE_PLAY;
                setPlay();
                break;
            case STATE_PAUSE:
                CURRENT_STATE = STATE_PAUSE;
                setPause();
                break;
            case STATE_DEFEAT:
                CURRENT_STATE = STATE_DEFEAT;
                break;
            case STATE_RECORDS:
                CURRENT_STATE = STATE_RECORDS;
                setRecords();
                break;
            case STATE_CONTACTS:
                CURRENT_STATE = STATE_CONTACTS;
                setContacts();
                break;
        }
    }


    private void setMenu() {
        setContentView(R.layout.menu);
        Button button1 = (Button) findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setState(STATE_PLAY);
            }
        });
        Button button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setState(STATE_RECORDS);
            }
        });
        Button button3 = (Button) findViewById(R.id.button3);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setState(STATE_CONTACTS);
            }
        });
        Button button4 = (Button) findViewById(R.id.button4);
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setState(STATE_DEFEAT);
            }
        });
   //     gameView = new GameView(link,this);
    }


    private void setPlay() {
        //   gameView = new GameView(link,this);
        setContentView(gameView);
    }


    private void setPause() {
        gameView.surfaceDestroyed(gameView.getHolder());
        setContentView(R.layout.pause);
        Button button1 = (Button) findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setState(STATE_PLAY);
            }
        });
        Button button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setState(STATE_MENU);
            }
        });

        if(gameView.getScores() > absRecord)
            absRecord = gameView.getScores();
    }


    private void setRecords() {
        setContentView(R.layout.records);
        TextView textView = (TextView) findViewById(R.id.textView2);
        textView.setText(Integer.toString(absRecord));
    }


    private void setContacts() {
        setContentView(R.layout.contacts);
    }


    private void openQuitDialog() {
        AlertDialog.Builder quitDialog = new AlertDialog.Builder(this);
        quitDialog.setTitle("Quit: Are you sure?");

        quitDialog.setPositiveButton("Yep", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        quitDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
            }
        });

        quitDialog.show();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(keyAbsRecord,absRecord);
        super.onSaveInstanceState(outState);

        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(keyAbsRecord, absRecord);
        editor.commit();
    }
}

package com.andrstudy.pushtest;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.widget.Toast;

import org.apache.commons.net.ftp.FTPFile;

public class SubActivity extends AppCompatActivity implements View.OnClickListener {

    TabHost mTab;

    static final String RECORDED_FILE = "";
    static final String FTP_FILE = "";

    MediaRecorder recorder;
    MediaPlayer player;

    private int playBackPosition = 0;
    private String fileSize;
    private ConnectFTP ConnectFTP;

    ImageButton startPlay, stopPlay,startPlay2, stopPlay2;
    ProgressBar progressBar;

    private Button head, rightArm, leftArm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);

        rightArm = (Button)findViewById(R.id.rightArm);
        rightArm.setOnClickListener(this);
        leftArm = (Button)findViewById(R.id.leftArm);
        leftArm.setOnClickListener(this);
        head = (Button)findViewById(R.id.head);
        head.setOnClickListener(this);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("");
        toolbar.setBackgroundColor(Color.parseColor("#FFCD12"));
//        setSupportActionBar(toolbar);

        TabHost tabHost1 = (TabHost) findViewById(R.id.tabHost1);
        tabHost1.setup() ; // 첫 번째 Tab. (탭 표시 텍스트:"TAB 1"), (페이지 뷰:"content1")
        TabHost.TabSpec ts1 = tabHost1.newTabSpec("Tab Spec 1");
        ts1.setContent(R.id.content1);
        ts1.setIndicator("play audio");
        tabHost1.addTab(ts1); // 두 번째 Tab. (탭 표시 텍스트:"TAB 2"), (페이지 뷰:"content2")
        TabHost.TabSpec ts2 = tabHost1.newTabSpec("Tab Spec 2");
        ts2.setContent(R.id.content2);
        ts2.setIndicator("parts info");
        tabHost1.addTab(ts2) ; // 세 번째 Tab. (탭 표시 텍스트:"TAB 3"), (페이지 뷰:"content3")
        TabHost.TabSpec ts3 = tabHost1.newTabSpec("Tab Spec 3");
        ts3.setContent(R.id.content3);
        ts3.setIndicator("set audio");
        tabHost1.addTab(ts3) ;

        ConnectFTP = new ConnectFTP();

        startPlay= (ImageButton) findViewById(R.id.playStart);
        stopPlay = (ImageButton) findViewById(R.id.playStop);
        startPlay2 = (ImageButton) findViewById(R.id.playStart2);
        stopPlay2 = (ImageButton) findViewById(R.id.playStop2);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);



        startPlay.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try{
//                            Toast.makeText(getApplicationContext(), "음악파일 재생 시작됨1", Toast.LENGTH_SHORT).show();
                            String recode_file = "";
                            boolean status = false;
                            status = ConnectFTP.ftpConnect("", "", "", );
                            if(status == true) {
                                Log.d(ConnectFTP.TAG, "Connection Success");
                            }
                            else {
                                Log.d(ConnectFTP.TAG, "Connection failed");
                            }

                            ConnectFTP.ftpChangeDirectory("");

                            String currentPath = ConnectFTP.ftpGetDirectory();

                            Log.d(ConnectFTP.TAG, currentPath);
                            ConnectFTP.ftpDownloadFile(currentPath + "", recode_file);

                            playAudio(recode_file);

                        } catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });

        stopPlay.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                if(player != null){
                    playBackPosition = player.getCurrentPosition();
                    player.pause();
                    Toast.makeText(getApplicationContext(), "음악 파일 재생 중지됨.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        startPlay2.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try{
//                            Toast.makeText(getApplicationContext(), "음악파일 재생 시작됨1", Toast.LENGTH_SHORT).show();
                            String recode_file = "";
                            boolean status = false;
                            status = ConnectFTP.ftpConnect("", "", "", );
                            if(status == true) {
                                Log.d(ConnectFTP.TAG, "Connection Success");
                            }
                            else {
                                Log.d(ConnectFTP.TAG, "Connection failed");
                            }

                            ConnectFTP.ftpChangeDirectory("sound/app/head");

                            String currentPath = ConnectFTP.ftpGetDirectory();

                            Log.d(ConnectFTP.TAG, currentPath);
                            ConnectFTP.ftpDownloadFile(currentPath + "", recode_file);

                            playAudio(recode_file);

                        } catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });

        stopPlay2.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                if(player != null){
                    playBackPosition = player.getCurrentPosition();
                    player.pause();
                    Toast.makeText(getApplicationContext(), "음악 파일 재생 중지됨.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void playAudio(String url) throws Exception {
        killMediaPlayer();

        player = new MediaPlayer();
        player.setDataSource(url);
        player.prepare();
        player.start();
    }
    protected void onDestory(){
        super.onDestroy();
        killMediaPlayer();
    }

    private void killMediaPlayer(){
        if(player != null){
            try{
                player.release();
            } catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    protected void onPause(){
        if(recorder != null){
            recorder.release();
            recorder = null;
        }
        if(player != null){
            player.release();
            player = null;
        }
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == head.getId()) {
            Handler handler = new Handler(){
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    startActivity(new Intent(SubActivity.this, recodingTest.class));
                }
            };
            handler.sendEmptyMessageDelayed(0, 0);
        }
    }
}
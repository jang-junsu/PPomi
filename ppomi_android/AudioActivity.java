
package com.andrstudy.pushtest;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.apache.commons.net.ftp.FTPFile;


public class AudioActivity extends AppCompatActivity{
    static final String RECORDED_FILE = "";
    static final String FTP_FILE = "";

    MediaRecorder recorder;
    MediaPlayer player;

    private int playBackPosition = 0;
    private String fileSize;
    private ConnectFTP ConnectFTP;

    Button startRecord, stopRecord, startPlay, stopPlay;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);

        ConnectFTP = new ConnectFTP();

        startRecord = (Button) findViewById(R.id.redStart);
        stopRecord = (Button) findViewById(R.id.redStop);
        startPlay= (Button) findViewById(R.id.playStart);
        stopPlay = (Button) findViewById(R.id.playStop);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        startRecord.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                if (recorder != null) {
                    recorder.stop();
                    recorder.release();
                    recorder = null;
                }
                recorder = new MediaRecorder();
                recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
                recorder.setOutputFile(RECORDED_FILE);
                try{
                    Toast.makeText(getApplicationContext(), "������ �����մϴ�.", Toast.LENGTH_SHORT).show();
                    recorder.prepare();
                    recorder.start();
                } catch (Exception e){
                    Log.e("SampleAudioRecorder", "Exception : ", e);
                }
            }
        });

        stopRecord.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                if(recorder == null)
                    return;

                recorder.stop();
                recorder.release();
                recorder = null;

                Toast.makeText(getApplicationContext(), "������ �����Ǿ����ϴ�.", Toast.LENGTH_SHORT).show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        boolean status = false;
                        status = ConnectFTP.ftpConnect("", "", "", );
                        if(status == true) {
                            Log.d(ConnectFTP.TAG, "Connection Success");
                        }
                        else {
                            Log.d(ConnectFTP.TAG, "Connection failed");
                        }

                        ConnectFTP.ftpUploadFile(RECORDED_FILE, FTP_FILE);
                    }
                }).start();
            }
        });

        startPlay.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try{
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

                            FTPFile[] fileList = ConnectFTP.mFTPClient.listFiles(currentPath);
                            Log.d("chock", "hello");
                            for(FTPFile file : fileList){
                                String fileName = file.getName();
                                if(fileName.equals("")){
                                    fileSize = String.valueOf(file.getSize());
                                }
                            }
                            Log.d(ConnectFTP.TAG, currentPath);
                            ConnectFTP.ftpDownloadFile(currentPath + "/hajae.wav", RECORDED_FILE);

                            playAudio(RECORDED_FILE);

                            Toast.makeText(getApplicationContext(), "�������� ��� ���۵�", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getApplicationContext(), "���� ���� ��� ������.", Toast.LENGTH_SHORT).show();
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
}
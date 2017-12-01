package com.andrstudy.pushtest;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.naman14.androidlame.AndroidLame;
import com.naman14.androidlame.LameBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;

public class recodingTest extends AppCompatActivity {

    static final String RECORDED_FILE = "";
    static final String FTP_FILE = "";

    MediaRecorder recorder;
    MediaPlayer player;

    Thread recordingThread = null;

    private int playBackPosition = 0;
    private String fileSize;

    Button stopRecord;
    ImageButton startRecord;

    int minBuffer;
    int inSamplerate = 8000;

    String filePath = "";

    boolean isRecording = false;

    AudioRecord audioRecord;
    AndroidLame androidLame = new AndroidLame();
    FileOutputStream outputStream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recoding_test);

        startRecord = (ImageButton) findViewById(R.id.startRecode);
        stopRecord = (Button) findViewById(R.id.stopRecode);

        startRecord.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (!isRecording) {
                    new Thread() {
                        @Override
                        public void run() {
                            isRecording = true;
                            startRecording();
                        }
                    }.start();
                }

            }

        });


        stopRecord.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                isRecording = false;

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ConnectFTP ConnectFTP = new ConnectFTP();
                        boolean status = false;
                        status = ConnectFTP.ftpConnect("", "", "", );
                        if (status == true) {
                            Log.d(ConnectFTP.TAG, "Connection Success");
                        } else {
                            Log.d(ConnectFTP.TAG, "Connection failed");
                        }
                        ConnectFTP.ftpUploadFile(RECORDED_FILE, FTP_FILE);

                        try {
                            long now = System.currentTimeMillis();
                            // 현재시간을 date 변수에 저장한다.
                            Date date = new Date(now);
                            // 시간을 나타냇 포맷을 정한다 ( yyyy/MM/dd 같은 형태로 변형 가능 )
                            SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                            String formatDate = sdfNow.format(date);
                            String link = "";
                            Log.d(ConnectFTP.TAG, formatDate);
                            URL url = new URL(link);
                            URLConnection conn = url.openConnection();
                            url.openStream();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }).start();
            }
        });


    }

//    private byte[] short2byte(short[] sData) {
//        int shortArrsize = sData.length;
//        byte[] bytes = new byte[shortArrsize * 2];
//        for (int i = 0; i < shortArrsize; i++) {
//            bytes[i * 2] = (byte) (sData[i] & 0x00FF);
//            bytes[(i * 2) + 1] = (byte) (sData[i] >> 8);
//            sData[i] = 0;
//        }
//        return bytes;
//    }

    private void startRecording() {

        minBuffer = AudioRecord.getMinBufferSize(inSamplerate, AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT);

        audioRecord = new AudioRecord(
                MediaRecorder.AudioSource.MIC, inSamplerate,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT, minBuffer * 2);

        //5 seconds data
        short[] buffer = new short[inSamplerate * 2 * 5];

        // 'mp3buf' should be at least 7200 bytes long
        // to hold all possible emitted data.
        byte[] mp3buffer = new byte[(int) (7200 + buffer.length * 2 * 1.25)];

        try {
            outputStream = new FileOutputStream(new File(filePath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        androidLame = new LameBuilder()
                .setInSampleRate(inSamplerate)
                .setOutChannels(1)
                .setOutBitrate(32)
                .setOutSampleRate(inSamplerate)
                .build();

        audioRecord.startRecording();

        int bytesRead = 0;

        while (isRecording) {

            bytesRead = audioRecord.read(buffer, 0, minBuffer);

            if (bytesRead > 0) {

                int bytesEncoded = androidLame.encode(buffer, buffer, bytesRead, mp3buffer);

                if (bytesEncoded > 0) {
                    try {
                        outputStream.write(mp3buffer, 0, bytesEncoded);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }


        int outputMp3buf = androidLame.flush(mp3buffer);

        if (outputMp3buf > 0) {
            try {
                outputStream.write(mp3buffer, 0, outputMp3buf);
                outputStream.close();


            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        audioRecord.stop();
        audioRecord.release();


        androidLame.close();


        isRecording = false;

    }
}

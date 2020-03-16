package com.example.bluetooth_1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;


public class ReadPageActivity extends AppCompatActivity {
    Button btnvup, btnvdown, btnsup, btnsdown, btnhome;
    SeekBar s;
    static int v = 10;
    SharedPreferences v_msg;
    SharedPreferences.Editor Volume_editor;
    String volume_change;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_page);

        //Bundle extras = getIntent().getExtras(); // starting the readpageactivity with passing a data for volume up or down from mainactivity by creating an intent. inefficient approach cause activity is getting creating everytime v ups or downs
        v_msg =getSharedPreferences("Volume_change",MODE_PRIVATE);

        btnsup = (Button)findViewById(R.id.btnsup);
        btnsdown = (Button)findViewById(R.id.btnsdown);
        btnhome = (Button)findViewById(R.id.btnhome);
        s = (SeekBar)findViewById(R.id.seekBar1);
        volume_change = v_msg.getString("Volume_change", "v up");
        final AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        int a = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int c = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        s.setMax(a);
        s.setProgress(c);
        /*

                this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
                this.setVolumeControlStream(AudioManager.STREAM_RING);
                this.setVolumeControlStream(AudioManager.STREAM_ALARM);
                this.setVolumeControlStream(AudioManager.STREAM_NOTIFICATION);
                this.setVolumeControlStream(AudioManager.STREAM_SYSTEM);
                this.setVolumeControlStream(AudioManager.STREAM_VOICECALL);

         */

        s.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar arg0) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar arg0) {
            }

            @Override
            public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, arg1, 0);

            }
        });
        btnhome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReadPageActivity.this,  MainActivity.class);
                startActivity(intent);
            }
        });


        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, v, 0);
        s.setProgress(v);

        if(volume_change.equals("v up")){
            v++;
        }
        else if(volume_change.equals("v down")){
            v--;
        }

    }


}

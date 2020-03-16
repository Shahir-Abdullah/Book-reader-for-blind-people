package com.example.bluetooth_1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import java.io.IOException;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    //public Button connect;
    //public Button disconnect;
    //public Button hellowbtn;
    public int REQUEST_ENABLE_BT = 1;
    public static BluetoothAdapter bluetoothAdapter;
    public MyBluetoothService send = null;
    Set<BluetoothDevice> pairedDevices; //set of paired devices






        public final static String MODULE_MAC = "98:D3:11:FC:44:DD";

        private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

        BluetoothAdapter bta;
        BluetoothSocket mmSocket;
        BluetoothDevice mmDevice;
        ConnectedThread btt = null;
        Button btnread, btnpht;
        TextView response;
        boolean lightflag = false;
        boolean relayFlag = true;
        public Handler mHandler;
        SharedPreferences v_msg;
        SharedPreferences.Editor Volume_editor;
    @Override
        protected void onCreate(Bundle savedInstanceState) {

            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            //setSupportActionBar(toolbar);

            //using sharepreferences to send volume change commands to another activity readpageactivity
            v_msg =getSharedPreferences("Volume_change",MODE_PRIVATE);
            Volume_editor= v_msg.edit();






        /*

                this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
                this.setVolumeControlStream(AudioManager.STREAM_RING);
                this.setVolumeControlStream(AudioManager.STREAM_ALARM);
                this.setVolumeControlStream(AudioManager.STREAM_NOTIFICATION);
                this.setVolumeControlStream(AudioManager.STREAM_SYSTEM);
                this.setVolumeControlStream(AudioManager.STREAM_VOICECALL);

         */



            Log.i("[BLUETOOTH]", "Creating listeners");
            response = (TextView) findViewById(R.id.response);




            btnread = (Button) findViewById(R.id.read);
            btnpht = (Button) findViewById(R.id.photo);
            btnpht.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("[BLUETOOTH]", "Attempting to send data");
                    if (mmSocket.isConnected() && btt != null) { //if we have connection to the bluetoothmodule

                            String sendtxt = "take photo";
                            btt.write(sendtxt.getBytes());
                            lightflag = true;

                    } else {
                        Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                    }
                    Intent intent = new Intent(MainActivity.this,  HomeActivity.class);
                    startActivity(intent);
                }
            });
            btnread.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("[BLUETOOTH]", "Attempting to send data");
                    if (mmSocket.isConnected() && btt != null) { //if we have connection to the bluetoothmodule

                            String sendtxt = "Read page";
                            btt.write(sendtxt.getBytes());
                            //relayFlag = false;

                        //disable the button and wait for 4 seconds to enable it again
                        btnread.setEnabled(false);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try{
                                    Thread.sleep(4000);
                                }catch(InterruptedException e){
                                    return;
                                }

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        btnread.setEnabled(true);
                                    }
                                });

                            }
                        }).start();
                    } else {
                        Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                    }
                }
            });

            bta = BluetoothAdapter.getDefaultAdapter();

            //if bluetooth is not enabled then create Intent for user to turn it on
            if(!bta.isEnabled()){
                Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBTIntent, REQUEST_ENABLE_BT);
            }else{
                initiateBluetoothProcess();
            }
        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            if(resultCode == RESULT_OK && requestCode == REQUEST_ENABLE_BT){
                initiateBluetoothProcess();
            }
        }

        public void initiateBluetoothProcess(){

            if(bta.isEnabled()){

                //attempt to connect to bluetooth module
                BluetoothSocket tmp = null;
                mmDevice = bta.getRemoteDevice(MODULE_MAC);

                //create socket
                try {
                    tmp = mmDevice.createRfcommSocketToServiceRecord(MY_UUID);
                    mmSocket = tmp;
                    mmSocket.connect();
                    Log.i("[BLUETOOTH]","Connected to: "+mmDevice.getName());
                }catch(IOException e){
                    try{mmSocket.close();}catch(IOException c){return;}
                }

                Log.i("[BLUETOOTH]", "Creating handler");
                mHandler = new Handler(Looper.getMainLooper()){
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                        if(msg.what == ConnectedThread.RESPONSE_MESSAGE){
                            String txt = (String)msg.obj;

                            if(txt.equals("start")){
                                //gets ready to take photo
                                Intent intent = new Intent(MainActivity.this,  HomeActivity.class);
                                startActivity(intent);
                            }
                            else if(txt.equals("read")){
                                //reads the captured image out loud and goes to volume/speed control section
                                Intent intent = new Intent(MainActivity.this,  ReadPageActivity.class);
                                startActivity(intent);
                            }
                            else if(txt.equals("scan")){
                                Intent intent = new Intent(MainActivity.this,   ScanActivity.class);
                                startActivity(intent);
                            }
                            else if(txt.equals("v up")){
                                //pass the command to another activity
                                //Intent i = new Intent(MainActivity.this, ReadPageActivity.class);
                                //i.putExtra("volume", "v up");
                                //startActivity(i);

                                Volume_editor.putString("Volume_msg", "v up");
                                Volume_editor.commit();



                                //Toast.makeText(MainActivity.this, "vol increased", Toast.LENGTH_SHORT).show();
                            }
                            else if(txt.equals("v down")){
                                //Intent i = new Intent(MainActivity.this, ReadPageActivity.class);
                                //i.putExtra("volume", "v down");
                                //startActivity(i);

                                Volume_editor.putString("Volume_msg", "v up");
                                Volume_editor.commit();


                              //  Toast.makeText(MainActivity.this, "vol decreased", Toast.LENGTH_SHORT).show();
                            }
                            else if(txt.equals("take photo")){

                                dispatchTakePictureIntent();

                                    try {
                                        TimeUnit.SECONDS.sleep(10);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }

                                Intent intent = new Intent(MainActivity.this,  take_photo.class);
                                startActivity(intent);


                            }

                            else if(txt.equals("read faster")){

                            }
                            else if(txt.equals("read slower")){

                            }
                            else if(txt.equals("home")){
                                Intent intent = new Intent(MainActivity.this,  MainActivity.class);
                                startActivity(intent);
                            }
                            if(response.getText().toString().length() >= 30){
                                response.setText("");
                                response.append(txt);

                            }else{
                                response.append("\n" + txt);
                            }
                        }
                    }
                };

                Log.i("[BLUETOOTH]", "Creating and running Thread");
                btt = new ConnectedThread(mmSocket,mHandler);
                btt.start();


            }
        }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);

        }




    }

        /*
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        disconnect = (Button)findViewById(R.id.btndcnt);
        connect = (Button)findViewById(R.id.btncnt);
        hellowbtn = (Button)findViewById(R.id.hellobtn);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        pairedDevices = bluetoothAdapter.getBondedDevices();
        if (bluetoothAdapter == null) {
            // Device doesn't support Bluetooth
        }

        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!bluetoothAdapter.isEnabled()) {
                   Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                   startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                }

                // Query all paired devices

                if (pairedDevices.size() > 0) {
                    // There are paired devices. Get the name and address of each paired device.
                    for (BluetoothDevice device : pairedDevices) {
                        String deviceName = device.getName();
                        String deviceHardwareAddress = device.getAddress(); // MAC address
                    }
                }


            }
        });

        //send data to other bluetooth device
        hellowbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!bluetoothAdapter.isEnabled()) {

                   byte[] message = {1};


                }
            }
        });

        disconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bluetoothAdapter.isEnabled()) {
                    bluetoothAdapter.disable();
                }
            }
        });
        */



}





























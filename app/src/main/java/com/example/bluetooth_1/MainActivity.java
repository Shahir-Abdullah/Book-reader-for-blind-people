package com.example.bluetooth_1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
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
    static int v = 7;
    int a, c;

    Button capture, upload;
    File photoFile;
    String encodedString;
    AudioManager audioManager;



        public MediaPlayer welcomemsg= MediaPlayer.create(MainActivity.this,R.raw.homeaudio); // welcome voice message
        public MediaPlayer opencamera= MediaPlayer.create(MainActivity.this,R.raw.opencamera); // welcome voice message
        public MediaPlayer takepic = MediaPlayer.create(MainActivity.this,R.raw.takepicture); //take picture voice command
        public MediaPlayer scanpage = MediaPlayer.create(MainActivity.this,R.raw.scanpage);
        public MediaPlayer downloadaudio = MediaPlayer.create(MainActivity.this,R.raw.downloadaudio);


        public final static String MODULE_MAC = "98:D3:11:FC:44:DD";

        private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

        BluetoothAdapter bta;
        BluetoothSocket mmSocket;
        BluetoothDevice mmDevice;
        ConnectedThread btt = null;
        //Button btnread, btnpht;
        TextView response;
        boolean lightflag = false;
        boolean relayFlag = true;
        public Handler mHandler;
        SharedPreferences v_msg;
        SharedPreferences.Editor Volume_editor;
    @Override
        protected void onCreate(Bundle savedInstanceState) {

            super.onCreate(savedInstanceState);
            setContentView(R.layout.home);
            //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            //setSupportActionBar(toolbar);

            //using sharepreferences to send volume change commands to another activity readpageactivity
            v_msg =getSharedPreferences("Volume_change",MODE_PRIVATE);
            Volume_editor= v_msg.edit();
            audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
            a = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            c = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);


            capture = (Button)findViewById(R.id.btnphoto);
            upload = (Button)findViewById(R.id.btnupload);


            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
             e.printStackTrace();
            }
            welcomemsg.start();

            try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
            opencamera.start(); // camera opening voice command

        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // Ensure that there's a camera activity to handle the intent
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    // Create the File where the photo should go
                    photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                        // Error occurred while creating the File
                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        Uri photoURI = FileProvider.getUriForFile(CameraActivity.this,
                                "com.example.bluetooth_1.fileprovider",
                                photoFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                    }
                }*/
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                encodedString = getFileToByte(currentPhotoPath);
                Log.d("Error" , encodedString);
                String url = "https://29923fd65015.ngrok.io";

                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

                StringRequest jsonObjRequest = new StringRequest(Request.Method.POST,
                        url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                            }
                        }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }) {

                    @Override
                    public String getBodyContentType() {
                        return "application/x-www-form-urlencoded; charset=UTF-8";
                    }

                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {


                        Map<String, String> postParam = new HashMap<String, String>();

                        postParam.put("basestring", encodedString);

                        return postParam;
                    }

                };

                requestQueue.add(jsonObjRequest);*/
            }
        });

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

            /*
            btnpht = (Button) findViewById(R.id.photo);
            btnpht.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("[BLUETOOTH]", "Attempting to send data");
                    if (mmSocket.isConnected() && btt != null) { //if we have connection to the bluetoothmodule

                            //String sendtxt = "take photo";
                            //btt.write(sendtxt.getBytes());
                            lightflag = true;

                    } else {
                        Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                    }
                    Intent intent = new Intent(MainActivity.this,  MainActivity.class);
                    startActivity(intent);
                }
            });
            btnread.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("[BLUETOOTH]", "Attempting to send data");
                    if (mmSocket.isConnected() && btt != null) { //if we have connection to the bluetoothmodule

                            //String sendtxt = "Read page";
                            //btt.write(sendtxt.getBytes());
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
*/
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
                            if(txt.equals("home")){
                                welcomemsg.start();
                                Toast.makeText(MainActivity.this, "Welcome! Press 2nd button", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(MainActivity.this,  MainActivity.class);
                                startActivity(intent);
                            }
                            else if(txt.equals("take photo")){

                                dispatchTakePictureIntent();

                                try {
                                    TimeUnit.SECONDS.sleep(8);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                                scanpage.start();

                                //Intent intent = new Intent(MainActivity.this,   MainActivity.class);
                                //startActivity(intent);
                            }
                            else if(txt.equals("upload")){ //send photo to server
                               // Intent intent = new Intent(MainActivity.this,   Upload.class);
                                //startActivity(intent);
                                Toast.makeText(MainActivity.this, "Scanning the photo, please wait", Toast.LENGTH_LONG).show();
                                downloadaudio.start();
                                try {
                                    TimeUnit.SECONDS.sleep(6);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                uploadPhoto();
                            }
                            else if(txt.equals("read")){

                                //reads the captured image out loud and goes to volume/speed control section


                                Toast.makeText(MainActivity.this, "Touch the screen to listen", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(MainActivity.this,  Read.class);
                                startActivity(intent);
                            }

                            else if(txt.equals("v up")){
                                //pass the command to another activity
                                //Intent i = new Intent(MainActivity.this, Read.class);
                                //i.putExtra("volume", "v up");
                                //startActivity(i);
                                //Volume_editor.putString("Volume_msg", "v up");
                                //Volume_editor.commit();
                               ShowDialog(++v);
                                Toast.makeText(MainActivity.this, "vol increased", Toast.LENGTH_SHORT).show();
                            }
                            else if(txt.equals("v down")){
                                //Intent i = new Intent(MainActivity.this, Read.class);
                                //i.putExtra("volume", "v down");
                                //startActivity(i);

                                //Volume_editor.putInt("Volume_msg", v++);
                                //Volume_editor.commit();


                                ShowDialog(++v);

                               Toast.makeText(MainActivity.this, "vol decreased", Toast.LENGTH_SHORT).show();
                            }
                            /*

                            */

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
    private String currentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String imageFileName = "JPEG_" + "_" + "temp";
        File storageDir = getExternalFilesDir("Pictures");
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    // Put the image file path into this method
    public static String getFileToByte(String filePath){
        Bitmap bmp = null;
        ByteArrayOutputStream bos = null;
        byte[] bt = null;
        String encodeString = null;
        try{
            bmp = BitmapFactory.decodeFile(filePath);
            bos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bt = bos.toByteArray();
            encodeString = Base64.encodeToString(bt, Base64.DEFAULT);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return encodeString;
    }
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takepic.start();
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(MainActivity.this,
                        "com.example.bluetooth_1.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }

    }
    private void uploadPhoto() {
        encodedString = getFileToByte(currentPhotoPath);
        Log.d("Error" , encodedString);
        String url = "https://9b93e08798f8.ngrok.io";

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        StringRequest jsonObjRequest = new StringRequest(Request.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(MainActivity.this, "ami riday", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this,  Read.class);
                        startActivity(intent);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {


                Map<String, String> postParam = new HashMap<String, String>();

                postParam.put("basestring", encodedString);

                return postParam;
            }

        };

        requestQueue.add(jsonObjRequest);
    }

    public void ShowDialog(int v)

    {

        final AlertDialog.Builder popDialog = new AlertDialog.Builder(this);

        final SeekBar seek = new SeekBar(this);

        seek.setMax(a);
        seek.setProgress(v);


        //seek.setProgress(v);
        //popDialog.setIcon(android.R.drawable.btn_star_big_on);

        //popDialog.setTitle("Please Select Rank 1-100 ");

        popDialog.setView(seek);


/*
        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){

//Do something here with new value

                //txtView.setText("Value of : " + progress);



            }



            public void onStartTrackingTouch(SeekBar arg0) {

// TODO Auto-generated method stub



            }
            public void onStopTrackingTouch(SeekBar seekBar) {

// TODO Auto-generated method stub



            }

        });

// Button OK

        popDialog.setPositiveButton("OK",

                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                    }


                });

        popDialog.create();



        popDialog.show();

        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, v, 0);



 */

        popDialog.create();
        popDialog.show();
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

            }


        };


    }


        /*
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
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





























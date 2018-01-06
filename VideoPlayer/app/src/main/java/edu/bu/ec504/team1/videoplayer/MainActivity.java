package edu.bu.ec504.team1.videoplayer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

/**
 * Created by heqifan on 10/28/17.
 */

public class MainActivity extends AppCompatActivity {

    EditText inputText;
    TextView response;
    public static String fileName;
    private VideoView myVideoView;
    Button saveButton;
    Button buttonOpenDialog;
    Button buttonUp;
    TextView textFolder;
    String KEY_TEXTPSS = "TEXTPSS";
    static final int CUSTOM_DIALOG_ID = 0;
    ListView dialogListView;



    File root;
    File curFolder;

    private List<String> fileList = new ArrayList<String>();

    File myExternalFile;
    String myData = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myVideoView = (VideoView)findViewById(R.id.videoView5);

        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.bkg);
        myVideoView.setVideoURI(uri);
        myVideoView.start();

        myVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.setLooping(true);
            }
        });

        requestPermission();


        response = (TextView) findViewById(R.id.response);


        buttonOpenDialog = (Button) findViewById(R.id.OPEN);
        buttonOpenDialog.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(CUSTOM_DIALOG_ID);
            }
        });

        saveButton =
                (Button) findViewById(R.id.PLAY);
        saveButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fileName != null)
                    displayImg();
                else
                    Toast.makeText(MainActivity.this, "Please Select File!", Toast.LENGTH_LONG).show();


            }
        });


        root = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
        curFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
    }


    @Override
    protected void onResume() {
        super.onResume();
        myVideoView.start();
    }



    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Log.i("permission","Permission passed!!!!!!");


                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Log.i("permission","Permission didn't pass!!!!!!");
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public void displayImg(){
        Intent intent = new Intent(this, VideoViewerActivity.class);
        startActivity(intent);
    }

    private static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    private static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    private void requestPermission() {
        Log.i("Request Permission", "Checking permission.");
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            Log.i("Request Permission", "permission has NOT been granted. Requesting permission.");

            ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

        }

    }

    @Override
    protected Dialog onCreateDialog(int id){
        Dialog dialog = null;

        switch(id){
            case CUSTOM_DIALOG_ID:
                dialog = new Dialog(MainActivity.this);
                dialog.setContentView(R.layout.dialoglayout);
                dialog.setTitle("Custom Dialog");
                dialog.setCancelable(true);
                dialog.setCanceledOnTouchOutside(true);

                textFolder = (TextView)dialog.findViewById(R.id.folder);
                buttonUp = (Button) dialog.findViewById(R.id.up);
                buttonUp.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        ListDir(curFolder.getParentFile());
                    }
                });

                dialogListView = (ListView) dialog.findViewById(R.id.dialogList);
                dialogListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        File selected = new File(fileList.get(i));
                        if(selected.isDirectory()) {
                            ListDir(selected);
                        } else {
                            Toast.makeText(MainActivity.this, selected.toString() + " selected", Toast.LENGTH_LONG).show();
                            response.setText("Selected File: " + selected.getName().toString());
                            fileName = selected.toString();
                            dismissDialog(CUSTOM_DIALOG_ID);
                        }
                    }
                });
                break;
        }
        return dialog;
    }



    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        super.onPrepareDialog(id, dialog);
        switch (id) {
            case CUSTOM_DIALOG_ID:
                ListDir(curFolder);
                break;
        }
    }

    void ListDir(File f){
        if(f.equals(root)){
            buttonUp.setEnabled(false);
        } else {
            buttonUp.setEnabled(true);
        }

        curFolder = f;
        textFolder.setText(f.getPath());

        File[] files = f.listFiles();
        fileList.clear();

        ArrayList<String> fileListToShow = new ArrayList<String>();

        for(File file : files){
            fileList.add(file.getPath());
            fileListToShow.add(file.getName() + (file.isDirectory()? "/" : ""));
        }

        ArrayAdapter<String> directoryList = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, fileListToShow);
        dialogListView.setAdapter(directoryList);
    }



}




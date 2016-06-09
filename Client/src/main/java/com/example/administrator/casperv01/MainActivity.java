package com.example.administrator.casperv01;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import com.szugyi.circlemenu.view.CircleImageView;
import com.szugyi.circlemenu.view.CircleLayout;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private CircleLayout circleLayout;
    private ImageView centerCircle, twinkleImage;

    private SeekBar volumeBar;
    private AudioManager audioManager;
    private Button exitButton;
    private CircleImageView recordImage, playImage, pauseImage, volumeImage, exitImage;
    private TextView centerText, explainMsg;
    private DrawerLayout mainLayout;

    private AlertDialog.Builder alt_bld;
    private String Mode[] = { "Exit", "PLAY", "Paused" , "RECORD", "Volume"};


    private UdpNetwork udpNetwork;
    private AudioHandler audioHandler;
    private AudioRecord audioRecoder;
    private Main main;


    private boolean [] positionFlag = {true, false, false, false, false};


    public boolean setPositionFlag(boolean [] positionFlag, int position){
        for(int i = 0; i < positionFlag.length; i++){
            if(position == i)
                positionFlag[i] = true;
            else
                positionFlag[i] = false;
        }
        return positionFlag[position];
    }

    public void setNonActionImage(){
        exitImage.setBackgroundResource(R.mipmap.ic_global_exit_normal);
        playImage.setBackgroundResource(R.mipmap.ic_global_play_normal);
        pauseImage.setBackgroundResource(R.mipmap.ic_global_pause_normal);
        recordImage.setBackgroundResource(R.mipmap.ic_global_record_normal);
        volumeImage.setBackgroundResource(R.mipmap.ic_global_volume_normal);
    }

    public void initClass(){
        udpNetwork = new UdpNetwork();
        audioHandler = new AudioHandler();
        audioRecoder = new AudioRecord();

        main = new Main(udpNetwork, audioHandler, audioRecoder);
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.getBackground().setAlpha(0);


        circleLayout = (CircleLayout) findViewById(R.id.CircleLayout);
        centerCircle = (ImageView) findViewById(R.id.centerCircle);
        pauseImage = (CircleImageView) findViewById(R.id.pauseImage);
        playImage = (CircleImageView) findViewById(R.id.playImage);
        recordImage = (CircleImageView)  findViewById(R.id.recordImage);
        volumeImage = (CircleImageView) findViewById(R.id.speakerImage);
        exitImage = (CircleImageView) findViewById(R.id.exitImage);
        twinkleImage = (ImageView) findViewById(R.id.twinkleImage);
        centerText = (TextView) findViewById(R.id.centerText);
        explainMsg = (TextView) findViewById(R.id.explainMsg);
        mainLayout = (DrawerLayout) findViewById(R.id.drawer_layout);




        volumeBar = (SeekBar)findViewById(R.id.volumeBar);
        exitButton = (Button) findViewById(R.id.exitButton);


        volumeBarInit();


        initClass();
        main.start();





        circleLayout.setFirstChildPosition(CircleLayout.FirstChildPosition.NORTH);
        circleLayout.setOnRotationFinishedListener(new CircleLayout.OnRotationFinishedListener() {
            @Override
            public void onRotationFinished(View view) {
                twinkleImage.setVisibility(View.VISIBLE);

                switch (view.getId()) {
                    case R.id.playImage:
                        exitButton.setVisibility(View.INVISIBLE);
                        setNonActionImage();
                        playImage.setBackgroundResource(R.mipmap.ic_global_play_active);
                        mainLayout.setBackgroundResource(R.mipmap.bg_photo_play2);
                        if(audioHandler.getAudioState() == AudioTrack.PLAYSTATE_PAUSED) {

                            audioHandler.play();
                            break;
                        }

                        else if(main.getState() == Thread.State.TERMINATED){
                            initClass();
                            main.start();
                            audioHandler.play();

                            break;
                        }

                        else
                            break;

                    case R.id.pauseImage:


                        volumeBar.setVisibility(View.INVISIBLE);
                        exitButton.setVisibility(View.INVISIBLE);
                        setNonActionImage();
                        pauseImage.setBackgroundResource(R.mipmap.ic_global_pause_active);
                        mainLayout.setBackgroundResource(R.mipmap.bg_photo_pause2);

                        if(audioHandler.getAudioState() == AudioTrack.PLAYSTATE_PLAYING) {
                            audioHandler.pause();
                            break;
                        }else
                            break;

                    case R.id.speakerImage:
                        volumeBar.setVisibility(View.VISIBLE);
                        exitButton.setVisibility(View.INVISIBLE);
                        setNonActionImage();
                        mainLayout.setBackgroundResource(R.mipmap.bg_photo_volume2);
                        volumeImage.setBackgroundResource(R.mipmap.ic_global_volume_active);
                        break;

                    case R.id.recordImage:
                        volumeBar.setVisibility(View.INVISIBLE);
                        exitButton.setVisibility(View.INVISIBLE);
                        setNonActionImage();
                        mainLayout.setBackgroundResource(R.mipmap.bg_photo_record2);

                        explainMsg.setText("녹음을 하시려면 아이콘을 눌러주세요");
                        recordImage.setBackgroundResource(R.mipmap.ic_global_record_active);

                        break;

                    case R.id.exitImage:
                        //Toast.makeText(MainActivity.this,"확인 버튼을 누르면 종료됩니다", Toast.LENGTH_LONG).show();
                        exitButton.setVisibility(View.VISIBLE);
                        volumeBar.setVisibility(View.INVISIBLE);
                        setNonActionImage();
                        mainLayout.setBackgroundResource(R.mipmap.bg_photo_exit2);
                        exitImage.setBackgroundResource(R.mipmap.ic_global_exit_active);

                        exitButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                exitDialog();

                            }
                        });

                        break;

                }
            }
        });


        circleLayout.setOnItemClickListener(new CircleLayout.OnItemClickListener() {
            @Override
            public void onItemClick(View view) {
                switch (view.getId()) {
                    case R.id.playImage:
                       break;


                    case R.id.pauseImage:

                        break;


                    case R.id.speakerImage:
                        break;


                    case R.id.recordImage:
                        centerCircle.setBackgroundResource(R.mipmap.btn_global_center_inactive);
                        explainMsg.setText("녹음을 중지하시려면 가운데 버튼을 눌러주세요");
                        setPositionFlag(positionFlag, 3);
                        circleLayout.setRotating(false);
                        circleLayout.setEnabled(false);
                        audioRecoder.recordStart();
                        main.setRecordFlag(true);
                        Log.d("Log", "RECORD FLAG TRUE");



                        break;

                }
            }
        });


        //Center Click
        centerCircle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (positionFlag[0]) {

                } else if (positionFlag[1]) {

                } else if (positionFlag[2]) {

                } else if (positionFlag[3]) {
                    Log.d("MYLOG", "CENTER CLICK");
                    circleLayout.setRotating(true);
                    centerCircle.setBackgroundResource(R.mipmap.btn_global_center_normal);
                    circleLayout.setEnabled(true);
                    main.setRecordFlag(false);
                    Toast.makeText(MainActivity.this, "파일을 저장 중", Toast.LENGTH_LONG).show();
                    audioRecoder.recordStop();
                    Toast.makeText(MainActivity.this, "파일 저장 완료", Toast.LENGTH_LONG).show();
                    explainMsg.setText("녹음을 하시려면 아이콘을 눌러주세요");

                    Toast.makeText(MainActivity.this, "Recording Success", Toast.LENGTH_LONG).show();
                } else {

                }
            }
        });



        circleLayout.setOnItemSelectedListener(new CircleLayout.OnItemSelectedListener() {
            @Override
            public void onItemSelected(View view) {

                twinkleImage.setVisibility(View.INVISIBLE);
                explainMsg.setText("");

                switch (view.getId()) {
                    case R.id.playImage:

                        centerText.setText(Mode[1]);
                        volumeBar.setVisibility(View.INVISIBLE);
                        exitButton.setVisibility(View.INVISIBLE);
                        break;


                    case R.id.pauseImage:
                        centerText.setText(Mode[2]);
                        volumeBar.setVisibility(View.INVISIBLE);
                        volumeBar.setVisibility(View.INVISIBLE);
                        break;


                    case R.id.speakerImage:
                        centerText.setText(Mode[4]);
                        volumeBar.setVisibility(View.VISIBLE);
                        break;


                    case R.id.recordImage:
                        centerText.setText(Mode[3]);
                        volumeBar.setVisibility(View.INVISIBLE);
                        volumeBar.setVisibility(View.INVISIBLE);

                        //Dialog out


                        break;


                    case R.id.exitImage:
                        centerText.setText(Mode[0]);
                        volumeBar.setVisibility(View.INVISIBLE);
                        break;
                }
            }
        });



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }



    private void exitDialog()
    {
        alt_bld = new AlertDialog.Builder(this);
        alt_bld.setMessage("앱을 종료하시겠습니까?").setCancelable(
                false).setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (main.getState() == Thread.State.RUNNABLE) {
                            main.setAliveFlag(false);
                        }
                        MainActivity.this.finish();
                        // Action for 'Yes' Button
                    }
                }).setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Action for 'NO' Button
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alt_bld.create();
        // Title for AlertDialog
        alert.setTitle("Sound Catcher");
        // Icon for AlertDialog
        alert.setIcon(R.mipmap.ic_launcher);
        alert.show();
    }




    // Hardware Volume Key Click connect Seek Bar
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP)
        {
            volumeBar = (SeekBar) findViewById(R.id.volumeBar);
            int index = volumeBar.getProgress();
            volumeBar.setProgress(index + 1);
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)
        {
            int index = volumeBar.getProgress();
            volumeBar.setProgress(index - 1);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    public void volumeBarInit()
    {
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        volumeBar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        volumeBar.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));

        volumeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    @Override
    public void onBackPressed()
    {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else if(main.getState() == Thread.State.RUNNABLE) {
            main.setAliveFlag(false);
        }
        else {
            super.onBackPressed();
        }
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    //navigation Button Click Event <- Dialog Introduce
    public boolean onNavigationItemSelected(MenuItem item)
    {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch(item.getItemId()){
            case R.id.choi:
                break;
            case R.id.shim:
                break;
            case R.id.kwon:
                break;
            case R.id.yoo:
                break;
        }

        if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }




    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        finish();

    }
}

package com.example.isangeetplayer;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class playsong extends AppCompatActivity {
    TextView textView,finish,start;
    ImageView previous,next,play;
    ArrayList<File> songs ;
    MediaPlayer mediaPlayer;
    String textcontent ;
    int position;
    SeekBar seekBar;
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        mediaPlayer.release();
        updateseek.interrupt();
    }
    Thread updateseek;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playsong);
        textView=findViewById(R.id.textView);
        play=findViewById(R.id.play);
        next=findViewById(R.id.next);
        previous=findViewById(R.id.previous);
        seekBar=findViewById(R.id.seekBar);
        start=findViewById(R.id.start);
        finish=findViewById(R.id.finish);
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#2da694"));
        actionBar.setBackgroundDrawable(colorDrawable);

        Intent intent =getIntent();
        Bundle bundle = intent.getExtras();
        songs=(ArrayList) bundle.getParcelableArrayList("songlist");
        textcontent=intent.getStringExtra("currentsong");
        textView.setText(textcontent);
        textView.setSelected(true);
        position = intent.getIntExtra("position",0);
        Uri uri=Uri.parse(songs.get(position).toString());
        mediaPlayer=MediaPlayer.create(this,uri);
        String setfinish=convertDurationMillis(mediaPlayer.getDuration());
        finish.setText(setfinish);
        String setstart=convertDurationMillis(0);
        start.setText(setstart);
        seekBar.setMax((mediaPlayer.getDuration()));
        mediaPlayer.start();


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String setstart = convertDurationMillis(mediaPlayer.getCurrentPosition());
                start.setText(setstart);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                String setstart = convertDurationMillis(mediaPlayer.getCurrentPosition());
                start.setText(setstart);
                mediaPlayer.seekTo(seekBar.getProgress());
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());

            }
        });
        updateseek= new Thread(){
            @Override
            public void run() {
                int currentposition=0;
                try {
                    while (currentposition<mediaPlayer.getDuration()){
                        currentposition=mediaPlayer.getCurrentPosition();
                        seekBar.setProgress(currentposition);
                        sleep(800);

                    }
                    if(mediaPlayer.getDuration()==mediaPlayer.getDuration()){
                        play.setImageResource(R.drawable.play);
                        String setfinish=convertDurationMillis(mediaPlayer.getDuration());
                        finish.setText(setfinish);
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        updateseek.start();

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer.isPlaying()){
                    play.setImageResource(R.drawable.play);
                    mediaPlayer.pause();
                }
                else {
                    play.setImageResource(R.drawable.pause);
                    mediaPlayer.start();
                }
            }
        });
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer.release();
                if (position!=0){
                    position=position-1;
                }
                else {
                    position=songs.size()-1;
                }
                Uri uri=Uri.parse(songs.get(position).toString());
                mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
                mediaPlayer.start();
                play.setImageResource(R.drawable.pause);
                seekBar.setMax(mediaPlayer.getDuration());
                textcontent=songs.get(position).getName().toString();
                textView.setText(textcontent);
                String setfinish=convertDurationMillis(mediaPlayer.getDuration());
                finish.setText(setfinish);
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer.release();
                if (position!=songs.size()-1){
                    position=position+1;

                }
                else {
                    position=0;
                }
                Uri uri=Uri.parse(songs.get(position).toString());
                mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
                mediaPlayer.start();
                play.setImageResource(R.drawable.pause);
                seekBar.setMax(mediaPlayer.getDuration());
                textcontent=songs.get(position).getName().toString();
                textView.setText(textcontent);
                String setfinish=convertDurationMillis(mediaPlayer.getDuration());
                finish.setText(setfinish);
            }
        });
    }
    public String convertDurationMillis(int getDurationInMillis){

        int getDurationMillis = getDurationInMillis ;

        String convertHours = String.format("%02d", TimeUnit.MILLISECONDS.toHours(getDurationMillis));
        String convertMinutes = String.format("%02d", TimeUnit.MILLISECONDS.toMinutes(getDurationMillis));
        String convertSeconds = String.format("%02d", TimeUnit.MILLISECONDS.toSeconds(getDurationMillis) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(getDurationMillis)));
        if(convertHours.equals("00")){
            return convertMinutes + ":" + convertSeconds;
        }
        else{
            return convertHours + ":" + convertMinutes + ":" + convertSeconds;
        }



    }
}
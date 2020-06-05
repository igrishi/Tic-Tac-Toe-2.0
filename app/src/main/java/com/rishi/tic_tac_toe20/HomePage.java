package com.rishi.tic_tac_toe20;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class HomePage extends AppCompatActivity {

    private Button play,multiplayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        play=findViewById(R.id.play_game);
        multiplayer=findViewById(R.id.multiplayer);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //offline game
            }
        });

        multiplayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //online game
                Intent intent=new Intent(HomePage.this, Multiplayer.class);
                startActivity(intent);
            }
        });
    }
}

package com.euromedcompany.orderfood;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
public class MainActivityCard extends AppCompatActivity {
        CardView videoCard, shortsCard, liveCard, exploreCard;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main_card);
            videoCard = findViewById(R.id.videoCard);
            shortsCard = findViewById(R.id.shortsCard);
            liveCard = findViewById(R.id.liveCard);
            exploreCard = findViewById(R.id.exploreCard);
            videoCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivityCard.this, VideoCardActivity.class);
                    startActivity(intent);
                }
            });
            shortsCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivityCard.this, ShortsCardActivity.class);
                    startActivity(intent);
                }
            });
            liveCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivityCard.this, LiveCardActivity.class);
                    startActivity(intent);
                }
            });
            exploreCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivityCard.this, ExploreCardActivity.class);
                    startActivity(intent);
                }
            });
        }
    }
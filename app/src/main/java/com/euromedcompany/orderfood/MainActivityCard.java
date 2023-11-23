package com.euromedcompany.orderfood;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import androidx.cardview.widget.CardView;
import android.content.Intent;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.euromedcompany.orderfood.databinding.ActivityMainCardBinding;

public class MainActivityCard extends AppCompatActivity {
        CardView videoCard, shortsCard, liveCard, exploreCard;
        ActivityMainCardBinding binding;

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

        binding = ActivityMainCardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new HomeFragment());
        binding.bottomNavigationView.setBackground(null);
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.home) {
                replaceFragment(new HomeFragment());
            } else if (itemId == R.id.video) {
                replaceFragment(new VideoFragment());
            } else if (itemId == R.id.live) {
                replaceFragment(new LiveFragment());
            } else if (itemId == R.id.explore) {
                replaceFragment(new ExploreFragment());
            }else if (itemId == R.id.shorts) {
                replaceFragment(new ShortsFragment());
            }
            return true;
        });

    }
       private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }
}



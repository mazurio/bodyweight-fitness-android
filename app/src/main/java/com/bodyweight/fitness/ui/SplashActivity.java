package com.bodyweight.fitness.ui;

import android.os.Bundle;

import com.bodyweight.fitness.R;
import com.bodyweight.fitness.view.fragment.SlideFragment;
import com.github.paolorotolo.appintro.AppIntro2;

public class SplashActivity extends AppIntro2 {
    @Override
    public void init(Bundle savedInstanceState) {
        addSlide(SlideFragment.newInstance(R.layout.activity_splash_1));
        addSlide(SlideFragment.newInstance(R.layout.activity_splash_2));
        addSlide(SlideFragment.newInstance(R.layout.activity_splash_3));
    }

    @Override
    public void onNextPressed() {

    }

    @Override
    public void onDonePressed() {
        this.onBackPressed();
    }

    @Override
    public void onSlideChanged() {

    }
}

package com.aib.circle;

import androidx.appcompat.app.AppCompatActivity;

import listener.AnimationListener;

import android.os.Bundle;

import com.aib.adapter.BaseAdapter;
import com.aib.view.PanView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private List<String> texts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        for (int i = 0; i < 10; i++) {
            texts.add(i + "");
        }

        PanView pan = findViewById(R.id.pan);
        BaseAdapter adapter = new BaseAdapter(texts);
        pan.setAdapter(adapter);
        pan.startPosition(5, new AnimationListener() {
            @Override
            public void onAnimationStart() {

            }

            @Override
            public void onAnimationEnd() {

            }
        });

    }
}

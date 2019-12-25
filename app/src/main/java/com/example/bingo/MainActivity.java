package com.example.bingo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.example.bingo.fragments.Home;
import com.example.bingo.fragments.Multiplayer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private FrameLayout frameLayout;
    private Fragment fragment;
    public static Context mainContext;
    public static Activity mainActivity;
    public static Set<String> ipSet = new HashSet<>();
    public static HashMap<String,String> playersListMap = new HashMap<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        frameLayout = (FrameLayout) findViewById(R.id.frame);
        mainContext = getApplicationContext();
        mainActivity = this;

        getSupportActionBar().hide();

        fragment = new Home();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frame, fragment).commit();

    }

    @Override
    public void onBackPressed()
    {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.frame);
        if (!(fragment instanceof IOnBackPressed) || !((IOnBackPressed) fragment).onBackPressed())
        {
            super.onBackPressed();
        }
    }

}

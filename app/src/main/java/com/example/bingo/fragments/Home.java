package com.example.bingo.fragments;


import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.example.bingo.R;

public class Home extends Fragment implements View.OnClickListener {

    private Button set_button;
    private Button single_player;
    private Button multi_player;
    private Fragment fragment;
    private LinearLayout home_layout;




    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        set_button = (Button) view.findViewById(R.id.set_button);
        single_player = (Button) view.findViewById(R.id.single_player);
        multi_player = (Button) view.findViewById(R.id.multi_player);
        home_layout = (LinearLayout)view.findViewById(R.id.home_layout);
        Animation aniSlide = AnimationUtils.loadAnimation(getContext(), R.anim.zoom);
        home_layout.setAnimation(aniSlide);
        set_button.setOnClickListener(this);
        single_player.setOnClickListener(this);
        multi_player.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.single_player:
                singelPlayer();
                break;
            case R.id.multi_player:
                multiPlayer();
                break;
            case R.id.set_button:
                openSettingsPanel();
                break;

        }
    }


    private void openSettingsPanel()
    {

        fragment = new Settings();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frame, fragment).commit();
    }

    private void singelPlayer()
    {
        fragment = new SinglePlayer();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frame, fragment).commit();

    }

    private void multiPlayer()
    {
        fragment = new Multiplayer();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frame, fragment).commit();

    }

}


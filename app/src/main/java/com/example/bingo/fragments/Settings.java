package com.example.bingo.fragments;


import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.bingo.Change_Name_Dialog;
import com.example.bingo.IOnBackPressed;
import com.example.bingo.R;


public class Settings extends Fragment implements View.OnClickListener, IOnBackPressed
{


    private LinearLayout set_layout;
    private ImageButton backToHome;
    private Button changename_button;
    private Fragment fragment;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        backToHome = (ImageButton) view.findViewById(R.id.backHome_button);
        changename_button = (Button)view.findViewById(R.id.change_button);
        backToHome.setOnClickListener(this);
        changename_button.setOnClickListener(this);
        set_layout = (LinearLayout)view.findViewById(R.id.set_layout);
        Animation aniSlide = AnimationUtils.loadAnimation(getContext(), R.anim.zoom);
        set_layout.setAnimation(aniSlide);



        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backHome_button:
                openHomePanel();
                break;
            case R.id.change_button:
                changename();
                break;
        }

    }

    private void openHomePanel() {

        fragment = new Home();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frame, fragment).commit();
    }

    private void changename() {

        Change_Name_Dialog cdd = new Change_Name_Dialog(getActivity());

        cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        cdd.show();

        cdd.getWindow().setLayout(800,750);

    }

    @Override
    public boolean onBackPressed()
    {
        Fragment fragment = new Home();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frame, fragment).commit();
        return true;
    }

}


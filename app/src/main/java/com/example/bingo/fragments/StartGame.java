package com.example.bingo.fragments;


import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.bingo.MainActivity;
import com.example.bingo.PlayerName_Adaptor;
import com.example.bingo.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


public class StartGame extends Fragment implements View.OnClickListener {

    private static ListView playersList;
    public  static PlayerName_Adaptor playerName_adaptor;
    public static Context context;
    private Button exit_button;
    private Fragment fragment;
    public static Activity activity;
    private MainActivity ma;
    private int isHost;
    private Button start_button;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_start_game, container, false);

        playersList = (ListView) view.findViewById(R.id.playersList);
        exit_button = (Button) view.findViewById(R.id.exit_button);
        start_button = (Button) view.findViewById(R.id.start_button);
        isHost = getArguments().getInt("isHost");
        exit_button.setOnClickListener(this);
        start_button.setOnClickListener(this);
        ma = new MainActivity();
        context = getContext();
        activity = getActivity();
        if(isHost == 0)
        {
            start_button.setBackgroundResource(R.drawable.disable_button);
            start_button.setTextColor(Color.parseColor("#5E5E5E"));
        }
        playerName_adaptor = new PlayerName_Adaptor(activity,ma.playersListMap);
        playersList.setAdapter(playerName_adaptor);



        return view;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.exit_button:
                exitHost();
                break;
            case R.id.start_button:
                if(isHost == 0)
                {
                    Toast.makeText(activity,"Only Host Can Start The Game",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    int size = new MainActivity().ipSet.size();
                   // if(size > 0)
                        startgame();
                    //else
                        Toast.makeText(activity,"No Players Joined The Game",Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }


    private void exitHost()
    {

        new Multiplayer().androidWebServer.stop();
        new MainActivity().playersListMap.clear();
        new MainActivity().ipSet.clear();
        if(isHost == 0)
        {
            Toast.makeText(activity,"Joined",Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(activity,"Hosted",Toast.LENGTH_SHORT).show();
        }
        fragment = new Multiplayer();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frame, fragment).commit();

    }

    public void updateList() {

        activity.runOnUiThread(new Runnable() {

            @Override
            public void run() {

                playerName_adaptor.updateAdaptor();
            }
        });


    }

    private void startgame()
    {
        fragment = new Matrix();
        Bundle bundle = new Bundle();
        bundle.putInt("isHost", 1);
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frame, fragment).commit();
        StartGameThread startGameThread = new StartGameThread();
        startGameThread.execute();
    }

    public class StartGameThread extends AsyncTask<Void, Void, Integer>
    {

        @Override
        protected Integer doInBackground(Void... params) {

            try
            {
                Set<String>  IPSet = new HashSet<>();
                IPSet.addAll(ma.ipSet);
                String removeOwn = new Multiplayer().ownIP;
                IPSet.remove(removeOwn);
                Iterator itr = IPSet.iterator();
                String url_str = "";
                for(int i=0;i<IPSet.size();i++)
                {
                    String ip = itr.next().toString();
                    url_str = "http://" + ip + "/StartGame";
                    Log.d("!!!!!", Integer.toString(IPSet.size()) + "----" + url_str);
                    URL urlObj = new URL(url_str);
                    HttpURLConnection urlConnection = (HttpURLConnection) urlObj.openConnection();
                    InputStream is = urlConnection.getInputStream();
                    urlConnection.disconnect();
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();

            } catch (IOException e) {

                e.printStackTrace();
            }


            return 0;
        }
        @Override
        protected void onPostExecute(Integer result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);

        }
    }


}


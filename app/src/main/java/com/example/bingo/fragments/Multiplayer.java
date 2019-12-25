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

import com.example.bingo.AndroidWebServer;
import com.example.bingo.MainActivity;
import com.example.bingo.R;
import com.example.bingo.StartActivity;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Iterator;


public class Multiplayer extends Fragment implements View.OnClickListener {

    private Button host_button;
    private JoinGame joinGame = new JoinGame();
    private Button join_button;
    private Button back_button;
    private MainActivity ma;
    private Fragment fragment;
    private String pl_name;
    private String url_str;
    private static Activity activity;
    public static AndroidWebServer androidWebServer;
    public static String ownIP;
    private LinearLayout multi_layout;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_multiplayer, container, false);

        ma = new MainActivity();
        host_button = (Button) view.findViewById(R.id.host_button);
        join_button = (Button) view.findViewById(R.id.join_button);
        back_button = (Button) view.findViewById(R.id.backHome_button);
        pl_name = new StartActivity().PlayerName;
        host_button.setOnClickListener(this);
        join_button.setOnClickListener(this);
        back_button.setOnClickListener(this);
        activity = getActivity();
        multi_layout = (LinearLayout)view.findViewById(R.id.multi_player_layout);
        Animation aniSlide = AnimationUtils.loadAnimation(getContext(), R.anim.zoom);
        multi_layout.setAnimation(aniSlide);


        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.host_button:
                hostGame();
                break;
            case R.id.join_button:
                joinGame();
                break;
            case R.id.backHome_button:
                backToHome();
                break;

        }
    }


    private void hostGame() {

        ownIP = getIpAddress() + ":8080" ;
        ma.ipSet.add(ownIP);
        ma.playersListMap.put(ownIP,new StartActivity().PlayerName);
        try {
            androidWebServer = new AndroidWebServer((MainActivity) ma.mainActivity, 8080);
            androidWebServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //server = new Server1((MainActivity) ma.mainActivity,8080);
        fragment = new StartGame();
        Bundle bundle = new Bundle();
        bundle.putInt("isHost", 1);
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frame, fragment).commit();

    }


    private void joinGame() {

        ownIP = getIpAddress()+":8081";
        ma.ipSet.add(ownIP);
        ma.playersListMap.put(ownIP,new StartActivity().PlayerName);
        //server = new Server1((MainActivity)ma.mainActivity,8081);
        try {
            androidWebServer = new AndroidWebServer((MainActivity) ma.mainActivity, 8081);
            androidWebServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        joinGame.execute();

    }

    private void backToHome() {

        fragment = new Home();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frame, fragment).commit();
    }

    public class JoinGame extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... params) {

            URL urlObj;
            HttpURLConnection urlConnection = null;
            try {
                url_str = "http://192.168.43.1:8080/joinGame?ip=" + ownIP + "&name=" + pl_name;
                urlObj = new URL(url_str);
                urlConnection = (HttpURLConnection) urlObj.openConnection();
                InputStream is = urlConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(is);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String line = bufferedReader.readLine();
                JSONObject jsonObject = new JSONObject(line);
                Iterator<String> k = jsonObject.keys();
                while (k.hasNext()) {
                    String key = k.next();
                    String val = jsonObject.get(key).toString();
                    ma.playersListMap.put(key,val);
                    ma.ipSet.add(key);

                }
            } catch (UnknownHostException e)
            {
                Log.d("!!!!!!!!!!hosr->", e.toString());
                doToast("No Host Available");
                urlConnection.disconnect();
                cancelTask();

            } catch (MalformedURLException e) {
                Log.d("!!!!!!!!!!mal->", e.toString());
                e.printStackTrace();

            } catch (IOException e)
            {
                Log.d("!!!!!!!!!!IO->", e.toString());
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }


            return 0;
        }

        @Override
        protected void onPostExecute(Integer result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            fragment = new StartGame();
            Bundle bundle = new Bundle();
            bundle.putInt("isHost", 0);
            fragment.setArguments(bundle);
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.frame, fragment).commit();

        }
    }

    private void doToast(String text) {
        Toast.makeText(activity, text, Toast.LENGTH_SHORT).show();
    }

    private void cancelTask()
    {
        joinGame.cancel(true);
    }

    public String getIpAddress() {
        String ip = "";
        try {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface
                    .getNetworkInterfaces();
            while (enumNetworkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = enumNetworkInterfaces
                        .nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface
                        .getInetAddresses();
                while (enumInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumInetAddress
                            .nextElement();

                    if (inetAddress.isSiteLocalAddress()) {
                        ip = inetAddress.getHostAddress();
                    }
                }
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ip += "Something Wrong! " + e.toString() + "\n";
        }
        return ip;
    }
}


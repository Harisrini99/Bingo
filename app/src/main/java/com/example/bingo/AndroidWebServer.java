package com.example.bingo;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import org.json.JSONObject;

import com.example.bingo.fragments.Multiplayer;
import com.example.bingo.fragments.Matrix;
import com.example.bingo.fragments.StartGame;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import fi.iki.elonen.NanoHTTPD;

public class AndroidWebServer extends NanoHTTPD {

    MainActivity activity;
    String mess = "";
    private int port;
    private String newIP;
    private String newName;
    private Set<String> oldIPSet = new HashSet<>();
    private String url_str = "";
    private Matrix matrix = new Matrix();
    private Multiplayer home = new Multiplayer();
    private MainActivity mainActivity = new MainActivity();



    public AndroidWebServer(MainActivity activity,int port) {
        super(port);
        this.port = port;
        this.activity = activity;
    }

    public AndroidWebServer(MainActivity activity,String hostname, int port) {
        super(hostname, port);
        this.activity = activity;
    }



    @Override
    public Response serve(String uri, Method method,
                          Map<String, String> header,
                          Map<String, String> parameters,
                          Map<String, String> files) {


        if(uri.contains("joinGame"))
        {
            Set<String> temp = activity.ipSet;
            Iterator it = temp.iterator();
            while (it.hasNext())
            {
                oldIPSet.add(it.next().toString());
            }
            newIP = parameters.get("ip");
            newName = parameters.get("name");
            int size = activity.ipSet.size();
            if(size >= 7)
            {
                activity.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(activity,"Maximum 8 Player Can be Connected",Toast.LENGTH_SHORT).show();

                    }
                });
            }
            else
            {
                updateIPListToAll updateIPListToAll = new updateIPListToAll();
                updateIPListToAll.execute();
                HashMap<String,String> map = new HashMap<>();
                Object[] keys = activity.playersListMap.keySet().toArray();

                for(Object k : keys)
                {
                    map.put(k.toString(),activity.playersListMap.get(k));
                }
                Log.d("@@@@@@@@@@@@@","--------"+map.toString());
                activity.ipSet.add(newIP);
                activity.playersListMap.put(newIP, newName);

                new StartGame().updateList();
                JSONObject j = new JSONObject(map);
                return newFixedLengthResponse(j.toString());
            }


        }

        if(uri.contains("updateIPList"))
        {
            newIP = parameters.get("ip");
            newName = parameters.get("name");
            activity.ipSet.add(newIP);
            activity.playersListMap.put(newIP, newName);
            new StartGame().updateList();
            return newFixedLengthResponse("IP List Updated");

        }

        if(uri.contains("StartGame"))
        {
            activity.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    Fragment fragment = new Matrix();
                    Bundle bundle = new Bundle();
                    bundle.putInt("isHost", 0);
                    fragment.setArguments(bundle);
                    FragmentManager fragmentManager = activity.getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.frame, fragment).commit();
                }
            });
        }

        if(uri.contains("FilledMatrix"))
        {
            newIP = parameters.get("ip");
            matrix.filledMatrix.put(newIP,1);
            Map<String,Integer> t = matrix.filledMatrix;
            boolean flag = true;
            Object[] keys = t.keySet().toArray();
            int isHost = matrix.isHost;
            for(Object o : keys)
            {
                if(t.get(o) == 0)
                {
                    flag = false;
                    break;
                }
            }
            if(flag)
            {
                if(isHost == 1)
                {
                    matrix.isAllFilled = true;
                    if(matrix.isStartGame) {
                        activity.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                Toast.makeText(activity, "Your Turn", Toast.LENGTH_LONG).show();

                            }
                        });
                    }

                }
            }
        }

        if( uri.contains("SendData"))
        {
            final String boxNumber = parameters.get("boxNumber");
            activity.runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    Button b[][] = matrix.b;
                    for(int i=1;i<=5;i++)
                    {
                        for (int j=1;j<=5;j++)
                        {
                            Animation aniSlide = AnimationUtils.loadAnimation(activity, R.anim.zoom);
                            String id = "b" + i + j;
                            int idint =activity.getResources().getIdentifier(id, "id", activity.getPackageName());
                            b[i][j] = (Button)activity.findViewById(idint);
                            if(b[i][j].getText().toString() == boxNumber || b[i][j].getText().toString().equals(boxNumber))
                            {
                                int oldStrike =  matrix.isBingo();
                                matrix.game[i-1][j-1] = 0;
                                int newStrike =  matrix.isBingo();
                                Log.d("sssssssssssssssssssstr",oldStrike+"-----"+newStrike);
                                if(newStrike > oldStrike && newStrike <= 5)
                                {
                                    String id1 = "b" + newStrike;
                                    int idint1 =activity.getResources().getIdentifier(id1, "id", activity.getPackageName());
                                    Button but = (Button)activity.findViewById(idint1);
                                    but.setTextColor(Color.parseColor("#FFD180"));
                                    Animation aniSlide1 = AnimationUtils.loadAnimation(activity, R.anim.bingo_zoom);
                                    but.setAnimation(aniSlide1);
                                }
                                b[i][j].setBackgroundColor(Color.TRANSPARENT);
                                final Drawable drawable = ContextCompat.getDrawable(activity, R.drawable.snow_silver);
                                b[i][j].setForeground(drawable);
                                b[i][j].setText("  ");
                                b[i][j].startAnimation(aniSlide);
                                break;
                            }
                        }
                    }


                }
            });
        }

        if(uri.contains("Turn")) {
            activity.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                        matrix.enableButton(true);
                        Toast.makeText(activity,"Yours Turn",Toast.LENGTH_SHORT).show();
                }
            });
        }


            return newFixedLengthResponse( "Request Received");
    }

    public class updateIPListToAll extends AsyncTask<Void, Void, Integer>
    {

        @Override
        protected Integer doInBackground(Void... params) {

            try {
                if(oldIPSet != null)
                {
                    Set<String> old_ipset = new HashSet<>();
                    old_ipset.addAll(oldIPSet);
                    old_ipset.remove(home.ownIP);
                    Iterator itr = old_ipset.iterator();
                    for(int i=0;i<old_ipset.size();i++)
                    {
                        String ip = itr.next().toString();
                        url_str = "http://" + ip + "/updateIPList?ip=" + newIP + "&name=" + newName;
                        Log.d("!!!!!", Integer.toString(oldIPSet.size()) + "----" + url_str);
                        URL urlObj = new URL(url_str);
                        HttpURLConnection urlConnection = (HttpURLConnection) urlObj.openConnection();
                        InputStream is = urlConnection.getInputStream();
                        urlConnection.disconnect();
                    }

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
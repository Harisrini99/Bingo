package com.example.bingo.fragments;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.bingo.IOnBackPressed;
import com.example.bingo.MainActivity;
import com.example.bingo.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;


public class SinglePlayer extends Fragment implements View.OnClickListener, IOnBackPressed
{

    private static Button[][] b;
    private static int game[][];
    private static int computerGame[][];
    private Button Start_button;
    private Button RandomButton;
    private Button UndoButton;
    private ImageButton back_button;
    private ArrayList<View> selectedViews;
    private TextView Title;
    private MainActivity mainActivity = new MainActivity();
    private Multiplayer multiplayer;
    private boolean isStartGame = false;
    private int count = 1;
    private boolean checked[];
    private int diagonal=0;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_single_player, container, false);
        checked= new boolean[12];
        for(int i=0;i<12;i++)
            checked[i] = false;
        Title = (TextView)view.findViewById(R.id.title);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar); //here toolbar is your id in xml
        Animation aniSlide = AnimationUtils.loadAnimation(getContext(), R.anim.blink);
        Title.setAnimation(aniSlide);
        b = new Button[6][6];
        game = new int[5][5];
        computerGame = new int[5][5];
        RandomButton = (Button)view.findViewById(R.id.random);
        RandomButton.setOnClickListener(this);
        back_button = (ImageButton) view.findViewById(R.id.back_button_toolbar);
        back_button.setOnClickListener(this);
        UndoButton = (Button)view.findViewById(R.id.undo);
        UndoButton.setOnClickListener(this);
        selectedViews = new ArrayList<>();
        for(int i=1;i<=5;i++)
        {
            for (int j=1;j<=5;j++)
            {
                String id = "b" + i + j;
                int idint = getContext().getResources().getIdentifier(id, "id", getContext().getPackageName());
                b[i][j] = (Button)view.findViewById(idint);
                final Animation ani = AnimationUtils.loadAnimation(getContext(), R.anim.matrix_zoom);
                b[i][j].setAnimation(ani);
                game[i-1][j-1]=1;
                b[i][j].setText(null);
                b[i][j].setTextSize(22);
                b[i][j].setOnClickListener(this);
            }
        }

        Start_button = (Button)view.findViewById(R.id.start_button_matrix);
        Start_button.setOnClickListener(this);
        mainActivity = new MainActivity();
        multiplayer = new Multiplayer();
        return view;
    }


    @Override
    public void onClick(View v)
    {
        if( v == back_button)
        {
            onBackPressed();
        }
        else if(v == Start_button)
        {
            enableButton(true);
            Title.clearAnimation();
            Title.setText("BINGO");
            Typeface typeface = ResourcesCompat.getFont(getContext(), R.font.maple_snow);
            Title.setTypeface(typeface);
            Title.setTextSize(TypedValue.COMPLEX_UNIT_SP,30);
            Title.setPadding(0,5,0,0);
            Start_button.setVisibility(View.GONE);
            RandomButton.setVisibility(View.GONE);
            UndoButton.setVisibility(View.GONE);
            isStartGame=true;
            ArrayList<String> bingoNumbers = new ArrayList<>();
            for(int i=0;i<25;i++)
                bingoNumbers.add((i+1)+"");
            for(int i=1;i<=5;i++)
            {
                for(int j=1;j<=5;j++)
                {
                    Random r = new Random();
                    int size = bingoNumbers.size();
                    int index = r.nextInt(size);
                    computerGame[i-1][j-1]=Integer.parseInt(bingoNumbers.get(index));
                    bingoNumbers.remove(index);
                }
            }
            Log.d("bbbbbbbbbbbbbbbbbbbbbbb",computerGame[0][0]+" "+computerGame[0][1]+" "+computerGame[0][2]+" "+ +computerGame[0][3]+" "+computerGame[0][4]+" ");
        }
        else if(v == RandomButton)
        {
            UndoButton.setEnabled(false);
            UndoButton.setBackgroundResource(R.drawable.disable_button);
            UndoButton.setTextColor(Color.parseColor("#5E5E5E"));
            ArrayList<String> bingoNumbers = new ArrayList<>();
            for(int i=0;i<25;i++)
                bingoNumbers.add((i+1)+"");
            for(int i=1;i<=5;i++)
            {
                for(int j=1;j<=5;j++)
                {
                    Random r = new Random();
                    int size = bingoNumbers.size();
                    int index = r.nextInt(size);
                    final Animation ani = AnimationUtils.loadAnimation(getContext(), R.anim.matrix_zoom);
                    b[i][j].setAnimation(ani);
                    b[i][j].setText(bingoNumbers.get(index));
                    bingoNumbers.remove(index);
                }
            }
            enableButton(false);
        }
        else if(v == UndoButton)
        {
            int size = selectedViews.size()-1;
            if(size > -1)
            {
                View vw = selectedViews.get(size);
                Button b = (Button) vw;
                b.setText("");
                b.setEnabled(true);
                count--;
                selectedViews.remove(size);
            }
            else {
                Toast.makeText(getActivity(),"Insert Numbers to Undo",Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            if(isStartGame)
            {
                String id[] = v.toString().split("/");
                int i = Integer.parseInt(String.valueOf(id[1].charAt(1)));
                int j = Integer.parseInt(String.valueOf(id[1].charAt(2)));
                Button b = (Button) v;
                b.setBackgroundColor(Color.TRANSPARENT);
                final Drawable drawable1 = ContextCompat.getDrawable(getContext(), R.drawable.snow_silver);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    b.setForeground(drawable1);
                }
                b.setText("");
                b.setEnabled(false);
                Animation aniSlide = AnimationUtils.loadAnimation(getContext(), R.anim.zoom);
                b.startAnimation(aniSlide);
                ArrayList<HashMap<String,Integer>> mapArr;
                mapArr = isBingo(game);
                int oldStrike =0,newStrike=0;
                oldStrike = mapArr.get(mapArr.size() - 1).get("strike");
                game[i - 1][j - 1] = 0;
                mapArr = isBingo(game);
                newStrike = mapArr.get(mapArr.size() - 1).get("strike");
                mapArr.remove(mapArr.size()-1);
                if(newStrike > oldStrike && newStrike <=6)
                {
                    for(int temp = oldStrike;temp<newStrike && temp < 5;temp++) {
                        int t = temp +1;
                        String id1 = "b" + (temp+1);
                        int idint1 = getActivity().getResources().getIdentifier(id1, "id", getActivity().getPackageName());
                        Button but = (Button) getActivity().findViewById(idint1);
                        if( t ==1 )
                            but.setTextColor(Color.parseColor("#75ff72"));
                        else if( t==2 )
                            but.setTextColor(Color.parseColor("#ffb4b4"));
                        else if( t==3 )
                            but.setTextColor(Color.parseColor("#f8fe52"));
                        else if( t==4 )
                            but.setTextColor(Color.parseColor("#a9ffec"));
                        else
                            but.setTextColor(Color.parseColor("#fe9f81"));
                        Animation aniSlide1 = AnimationUtils.loadAnimation(getActivity(), R.anim.bingo_zoom);
                        but.setAnimation(aniSlide1);
                    }
                }
                if(newStrike > 4)
                {
                    enableButton(false);
                }
            }
            else
            {
                Button b = (Button) v;
                selectedViews.add(v);
                b.setText(count+"");
                b.setEnabled(false);
                count++;
            }
        }

    }

    private void enableButton(boolean isEnabled)
    {
        for(int i=1;i<=5;i++)
        {
            for (int j=1;j<=5;j++)
            {
                b[i][j].setEnabled(isEnabled);
            }
        }

    }


    @Override
    public boolean onBackPressed()
    {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
        builder.setMessage("Do You Want To Exit The Game")
                // .setIcon(R.drawable.alert)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {

                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        Fragment fragment = new Home();
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.frame, fragment).commit();
                    }
                });
        android.app.AlertDialog alert = builder.create();
        alert.show();
        return true;
    }

    private ArrayList<HashMap<String,Integer>> isBingo(int[][] game)
    {
        int diagonal =0;
        ArrayList<HashMap<String,Integer>> mapArr = new ArrayList<>();
        for(int i=0;i<5;i++)
        {
            int flag = 0;
            HashMap<String,Integer> map = new HashMap<>();
            for(int j=0;j<5;j++)
            {
                int key = j+1;
                int data = (i+1)*10 + j +1;
                map.put(key+"",data);
                if(game[i][j] == 1)
                {
                    flag = 1;
                    break;
                }
            }
            if(flag == 0)
            {
                diagonal=diagonal+1;
                map.put("type",1);
                mapArr.add(map);
                Log.d("8888888888888888888888888888",mapArr.toString());
            }
            HashMap<String,Integer> map1 = new HashMap<>();
            flag = 0;
            for(int j=0;j<5;j++)
            {
                int key = j+1;
                int data = (j+1)*10 + i +1;
                map1.put(key+"",data);
                if(game[j][i] == 1)
                {
                    flag = 1;
                    break;
                }
            }
            if(flag == 0)
            {
                diagonal=diagonal+1;
                map1.put("type",2);
                mapArr.add(map1);
                Log.d("8888888888888888888888888823232323----",mapArr.toString());
            }
        }
        if((game[0][0]==0)&&(game[1][1]==0)&&(game[2][2]==0)&&(game[3][3]==0)&&(game[4][4]==0))
        {
            HashMap<String,Integer> tmap = new HashMap<>();
            diagonal=diagonal+1;
            tmap.put("type",3);
            tmap.put("1",11);
            tmap.put("2",22);
            tmap.put("3",33);
            tmap.put("4",44);
            tmap.put("5",55);
            mapArr.add(tmap);
        }
        if((game[0][4]==0)&&(game[1][3]==0)&&(game[2][2]==0)&&(game[3][1]==0)&&(game[4][0]==0))
        {
            HashMap<String,Integer> tmap = new HashMap<>();
            diagonal=diagonal+1;
            tmap.put("type",4);
            tmap.put("1",15);
            tmap.put("2",24);
            tmap.put("3",33);
            tmap.put("4",42);
            tmap.put("5",51);
            mapArr.add(tmap);
        }
        HashMap<String,Integer> t = new HashMap<>();
        t.put("strike",diagonal);
        mapArr.add(t);
        Log.d("8888888888888888888888888854545445455454----",mapArr.toString());
        return mapArr;
    }
}


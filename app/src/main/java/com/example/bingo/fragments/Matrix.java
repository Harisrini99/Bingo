package com.example.bingo.fragments;

import android.graphics.Color;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
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


public class Matrix extends Fragment implements View.OnClickListener
{

    public static Button[][] b;
    public static int game[][];
    private Button Start_button;
    public static boolean isStartGame = false;
    private int count = 1;
    private Button RandomButton;
    private Button UndoButton;
    private ArrayList<View> selectedViews;
    public static int isHost;
    public static Map<String,Integer> filledMatrix = new HashMap<>();
    public static boolean isAllFilled = false;
    private TextView Title;
    Thread thread;
    private String data;
    private FilledMatrixThread filledMatrixThread;
    private MainActivity mainActivity = new MainActivity();
    private Multiplayer multiplayer;
    public static String nextIP;
    public ArrayList<String> turnList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_matrix, container, false);

        Title = (TextView)view.findViewById(R.id.title);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar); //here toolbar is your id in xml

        Animation aniSlide = AnimationUtils.loadAnimation(getContext(), R.anim.blink);
        Title.setAnimation(aniSlide);
        b = new Button[6][6];
        game = new int[5][5];
        RandomButton = (Button)view.findViewById(R.id.random);
        RandomButton.setOnClickListener(this);
        UndoButton = (Button)view.findViewById(R.id.undo);
        UndoButton.setOnClickListener(this);
        selectedViews = new ArrayList<>();
        isHost = getArguments().getInt("isHost");
        Set<String> ipl = new MainActivity().ipSet;
        Iterator it = ipl.iterator();
        while(it.hasNext())
        {
            filledMatrix.put(it.next().toString(),0);
        }
        filledMatrix.remove(new Multiplayer().ownIP);
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
        List<String> numbersList = new ArrayList<String>(mainActivity.ipSet);
        Collections.sort(numbersList);
        turnList.addAll(numbersList);

        return view;
    }


    @Override
    public void onClick(View v)
    {
        if(v == Start_button)
        {
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
            if(isHost == 1)
            {
                enableButton(true);
                if(isAllFilled)
                {
                    Toast.makeText(getActivity(),"Your Turn",Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(getActivity(),"Wait For The Other Player To Fill Box \n See Further Details in Players Menu",Toast.LENGTH_SHORT).show();

            }
            else
            {
                filledMatrixThread = new FilledMatrixThread();
                filledMatrixThread.execute();
            }
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
            if(isStartGame && !isAllFilled && isHost == 1 )
            {
                Log.d("}}}}}}}}}}}}}}}}}}}}}}",filledMatrix.toString());
                Toast.makeText(getActivity(),"Wait For The Other Player To Fill Box \n See Further Details in Players Menu",Toast.LENGTH_LONG).show();
            }
            else if(isStartGame && isHost == 1 && isAllFilled || isStartGame && isHost == 0)
            {
                String id[] = v.toString().split("/");
                int i = Integer.parseInt(String.valueOf(id[1].charAt(1)));
                int j = Integer.parseInt(String.valueOf(id[1].charAt(2)));
                int oldStrike =  isBingo();
                game[i-1][j-1] = 0;
                int newStrike =  isBingo();
                Log.d("sssssssssssssssssssstr",oldStrike+"-----"+newStrike);
                if(newStrike >= 5)
                {
                    enableButton(false);
                    String id1 = "b" + 5;
                    int idint1 =getActivity().getResources().getIdentifier(id1, "id", getActivity().getPackageName());
                    Button but = (Button)getActivity().findViewById(idint1);
                    but.setTextColor(Color.parseColor("#FFD180"));
                    Animation aniSlide1 = AnimationUtils.loadAnimation(getActivity(), R.anim.bingo_zoom);
                    but.setAnimation(aniSlide1);
                }
                if(newStrike > oldStrike && newStrike < 6)
                {
                    String id1 = "b" + newStrike;
                    int idint1 =getActivity().getResources().getIdentifier(id1, "id", getActivity().getPackageName());
                    Button but = (Button)getActivity().findViewById(idint1);
                    but.setTextColor(Color.parseColor("#FFD180"));
                    Animation aniSlide = AnimationUtils.loadAnimation(getActivity(), R.anim.bingo_zoom);
                    but.setAnimation(aniSlide);
                }
                Button b = (Button) v;
                data = b.getText().toString();
                b.setBackgroundColor(Color.TRANSPARENT);
                final Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.snow_silver);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    b.setForeground(drawable);
                }
                b.setText("  ");
                Animation aniSlide = AnimationUtils.loadAnimation(getContext(), R.anim.zoom);
                b.startAnimation(aniSlide);
                enableButton(false);
                SendData sendData = new SendData();
                sendData.execute();
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

    public void enableButton(boolean isEnabled)
    {
        for(int i=1;i<=5;i++)
        {
            for (int j=1;j<=5;j++)
            {
                b[i][j].setEnabled(isEnabled);
            }
        }

    }

    public class FilledMatrixThread extends AsyncTask<Void, Void, Integer> {

            @Override
            protected Integer doInBackground(Void... params) {

                URL urlObj;
                HttpURLConnection urlConnection = null;
                try {
                    String url_str = "http://192.168.43.1:8080/FilledMatrix?ip=" + new Multiplayer().ownIP;
                    Log.d("~~~~~~~~~~~~",url_str);
                    urlObj = new URL(url_str);
                    urlConnection = (HttpURLConnection) urlObj.openConnection();
                    InputStream is = urlConnection.getInputStream();
                    InputStreamReader inputStreamReader = new InputStreamReader(is);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String line = bufferedReader.readLine();
                    } catch (UnknownHostException e)
                {

                } catch (MalformedURLException e) {
                    e.printStackTrace();

                } catch (IOException e)
                {
                    e.printStackTrace();
                }


                return 0;
            }

            @Override
            protected void onPostExecute(Integer result) {
                // TODO Auto-generated method stub
                super.onPostExecute(result);
                filledMatrixThread.cancel(true);
            }
        }

    public class SendData extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... params) {

            try {
                Set<String>  IPSet = new HashSet<>();
                IPSet.addAll(mainActivity.ipSet);
                String removeOwn = multiplayer.ownIP;
                IPSet.remove(removeOwn);
                Iterator itr = IPSet.iterator();
                String url_str = "";
                for(int i=0;i<IPSet.size();i++)
                {
                    String ip = itr.next().toString();
                    url_str = "http://"+ip+"/SendData?boxNumber="+data;
                    Log.d("!!!!!stargame---", Integer.toString(IPSet.size()) + "----" + url_str);
                    URL urlObj = new URL(url_str);
                    HttpURLConnection urlConnection = (HttpURLConnection) urlObj.openConnection();
                    InputStream is = urlConnection.getInputStream();
                    urlConnection.disconnect();
                }
            } catch (UnknownHostException e)
            {

            } catch (MalformedURLException e) {
                e.printStackTrace();

            } catch (IOException e)
            {
                e.printStackTrace();
            }


            return 0;
        }

        @Override
        protected void onPostExecute(Integer result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
                Turn turn = new Turn();
                turn.execute();

        }
    }

    public class Turn extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... params) {

            try {

                String url_str = "";
                int n =turnList.indexOf(multiplayer.ownIP);
                int size = turnList.size();
                if( (n+1) >= size )
                    n=-1;
                String ip = turnList.get(n+1);
                url_str = "http://" + ip + "/Turn?ip="+ multiplayer.ownIP;
                Log.d("!!!!!", "----" +url_str+"-----"+turnList);
                URL urlObj = new URL(url_str);
                HttpURLConnection urlConnection = (HttpURLConnection) urlObj.openConnection();
                InputStream is = urlConnection.getInputStream();
                urlConnection.disconnect();
            } catch (UnknownHostException e)
            {

            } catch (MalformedURLException e) {
                e.printStackTrace();

            } catch (IOException e)
            {
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

    public int isBingo()
    {
        int diagonal=0;
        if((game[0][0]==0)&&(game[0][1]==0)&&(game[0][2]==0)&&(game[0][3]==0)&&(game[0][4]==0))
        {
            diagonal=diagonal+1;
        }
        if((game[1][0]==0)&&(game[1][1]==0)&&(game[1][2]==0)&&(game[1][3]==0)&&(game[1][4]==0))
        {
            diagonal=diagonal+1;
        }
        if((game[2][0]==0)&&(game[2][1]==0)&&(game[2][2]==0)&&(game[2][3]==0)&&(game[2][4]==0))
        {
            diagonal=diagonal+1;
        }
        if((game[3][0]==0)&&(game[3][1]==0)&&(game[3][2]==0)&&(game[3][3]==0)&&(game[3][4]==0))
        {
            diagonal=diagonal+1;
        }
        if((game[4][0]==0)&&(game[4][1]==0)&&(game[4][2]==0)&&(game[4][3]==0)&&(game[4][4]==0))
        {
            diagonal=diagonal+1;
        }
        if((game[0][0]==0)&&(game[1][0]==0)&&(game[2][0]==0)&&(game[3][0]==0)&&(game[4][0]==0))
        {
            diagonal=diagonal+1;
        }
        if((game[0][1]==0)&&(game[1][1]==0)&&(game[2][1]==0)&&(game[3][1]==0)&&(game[4][1]==0))
        {
            diagonal=diagonal+1;
        }
        if((game[0][2]==0)&&(game[1][2]==0)&&(game[2][2]==0)&&(game[3][2]==0)&&(game[4][2]==0))
        {
            diagonal=diagonal+1;
        }
        if((game[0][3]==0)&&(game[1][3]==0)&&(game[2][3]==0)&&(game[3][3]==0)&&(game[4][3]==0))
        {
            diagonal=diagonal+1;
        }
        if((game[0][4]==0)&&(game[1][4]==0)&&(game[2][4]==0)&&(game[3][4]==0)&&(game[4][4]==0))
        {
            diagonal=diagonal+1;
        }
        if((game[0][0]==0)&&(game[1][1]==0)&&(game[2][2]==0)&&(game[3][3]==0)&&(game[4][4]==0))
        {
            diagonal=diagonal+1;
        }
        if((game[0][4]==0)&&(game[1][3]==0)&&(game[2][2]==0)&&(game[3][1]==0)&&(game[4][0]==0))
        {
            diagonal=diagonal+1;
        }
        return diagonal;
    }
}


package com.example.bingo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class StartActivity extends AppCompatActivity {

    private EditText player_name;
    public static String PlayerName;
    public static SharedPreferences.Editor editor;
    public static SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        player_name = (EditText)findViewById(R.id.player_name_button);

        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        int isName = sharedPref.getInt("isName",0);
        PlayerName = sharedPref.getString("Name","Player");

        sharedPref = this.getPreferences(Context.MODE_MULTI_PROCESS);
        editor = sharedPref.edit();



        if(isName == 1)
        {
            Intent intent = new Intent(StartActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public void Next(View view)
    {
        editor.putInt("isName",1);
        String t = player_name.getText().toString();
        editor.putString("Name", player_name.getText().toString());
        PlayerName = player_name.getText().toString();

        editor.commit();
      Intent intent = new Intent(StartActivity.this,MainActivity.class);
       startActivity(intent);
        finish();
    }
}

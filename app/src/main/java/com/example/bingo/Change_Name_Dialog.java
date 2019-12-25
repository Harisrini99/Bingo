package com.example.bingo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class Change_Name_Dialog extends Dialog implements View.OnClickListener
{

    public Change_Name_Dialog(@NonNull Context context) {
        super(context);
    }

    private ImageButton close;
    private StartActivity startActivity;
    private EditText pl_name;
    private Button update;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change__name__dialog);

        close = (ImageButton) findViewById(R.id.close);
        update = (Button)findViewById(R.id.update_button);
        pl_name = (EditText)findViewById(R.id.pl_name);


        close.setOnClickListener(this);
        update.setOnClickListener(this);

        startActivity = new StartActivity();
        pl_name.setText(startActivity.PlayerName);
    }

    @Override
    public void onClick(View v)
    {
        if(v == close)
        {
            dismiss();
        }
        if(v == update)
        {
            startActivity.editor.putInt("isName",1);
            startActivity.editor.putString("Name", pl_name.getText().toString());
            startActivity.PlayerName = pl_name.getText().toString();
            startActivity.editor.commit();
            Toast.makeText(getContext(),"Player Name Changed",Toast.LENGTH_SHORT).show();
            dismiss();

        }
    }
}

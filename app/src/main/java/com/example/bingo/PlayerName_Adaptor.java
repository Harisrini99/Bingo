package com.example.bingo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class PlayerName_Adaptor  extends BaseAdapter {

    private Activity activity;
    private MainActivity ma = new MainActivity();
    private Map<String,String> playerName;
    private String color;


    public  void updateAdaptor()
    {
        HashMap<String, String> map = ma.playersListMap;
        Object[] keys = map.keySet().toArray();
        for(Object k :keys)
        {
            playerName.put(k.toString(),map.get(k));
        }
        Log.d("{{{{{{{{{{------",playerName.toString());
        Log.d("{{{{{{{{{{------size",playerName.size()+"-------"+getCount());
        notifyDataSetChanged();

    }

    public PlayerName_Adaptor(Activity activity,Map<String,String> playerName)
    {
        this.activity = activity;
        this.playerName = playerName;
    }

    @Override
    public int getViewTypeCount() {
        return getCount();
    }
    @Override
    public int getItemViewType(int position) {

        return position;
    }

    @Override
    public int getCount() {
        return playerName.size();
    }

    @Override
    public Object getItem(int position) {
        return playerName.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder(); LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.player_list, null, true);
            holder.playerName = (TextView)convertView.findViewById(R.id.playerName_txt);
            holder.IPAdd = (TextView)convertView.findViewById(R.id.ip_txt);


            convertView.setTag(holder);
        }else {
            // the getTag returns the viewHolder object set as a tag to the view
            holder = (ViewHolder)convertView.getTag();
        }

        Object[] keys = playerName.keySet().toArray();
        Log.d("{{{{{{{{{{------count",playerName.size()+"-------"+getCount());
        holder.playerName.setText(playerName.get(keys[position]));
        holder.IPAdd.setText(keys[position].toString());

        return convertView;
    }

    private class ViewHolder {

        TextView playerName;
        TextView IPAdd;

    }

}
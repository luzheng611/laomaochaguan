package com.laomachaguan.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Administrator on 2016/8/1.
 */
public class mArrayAdapter extends ArrayAdapter {
    private Context context;
    private int textviewId;
    private int resource;
    private static final String TAG = "mArrayAdapter";
    private String type;

    public mArrayAdapter(Context context, int resource, int textViewResourceId, ArrayList<HashMap<String, String>> list, String type) {
        super(context, resource, textViewResourceId, list);
        this.context = context;
        this.textviewId = textViewResourceId;
        this.resource = resource;
        this.type = type;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        if (view == null) {
            view = LayoutInflater.from(context).inflate(resource, parent, false);

        }
        TextView textView = (TextView) view.findViewById(textviewId);
        HashMap<String, String> map = (HashMap<String, String>) getItem(position);
        if (type.equals("念佛")) {
            textView.setText(map.get("ba_name"));
            textView.setTag(map.get("id"));
        } else if (type.equals("诵经")) {
            textView.setText(map.get("rg_name"));
            textView.setTag(map.get("id"));
        } else if (type.equals("持咒")) {
            textView.setText(map.get("ja_name"));
            textView.setTag(map.get("id"));
        }


        return view;
    }

}

package com.projects.assignment;

/**
 * Created by adwait on 21-07-2017.
 */

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class LazyAdapter extends BaseAdapter{
    HashMap<String,String> persons = new HashMap<String,String>();
    private Activity activity;
    // ImageView image;
    int cho;
    private static LayoutInflater inflater=null;
    public ImageLoader imageLoader;
    SharedPreferences sharep;
    SharedPreferences.Editor edit;
    JSONObject jo,jo1;
    JSONArray jar;
    ArrayList<HashMap<String, String>> personList,personListt,personListtt,personListttt;
    double hc;
    CountDownTimer tim;

    public LazyAdapter(Activity a, ArrayList<HashMap<String, String>> personList1,int ch) {
        activity = a;
        cho=ch;
        if(ch==2) {
            personListt = personList1;
        }
        else if(ch==3){
            personListtt = personList1;
        }
        else if(ch==4){
            personListttt = personList1;
        }
        else{
            personList = personList1;
        }

        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader=new ImageLoader(activity.getApplicationContext());
        sharep = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
        edit=sharep.edit();

    }
    public void refresh(ArrayList<HashMap<String, String>> personList1){

        personList.addAll(0,personList1);
    }
    public void append(ArrayList<HashMap<String, String>> personList1)
    {
        if(cho==2) {
            personListt.addAll(personList1);
        }
        else if(cho==3){
            personListtt.addAll(personList1);
        }else if(cho==4){
            personListttt.addAll(personList1);
        }
        else{
            personList.addAll(personList1);
        }
    }

    public int getCount() {

        if(cho==2) {
            return personListt.size();
        }
        else if(cho==3){
            return personListtt.size();
        }
        else if(cho==4){
            return personListttt.size();
        }
        else{

            mHighlightedPositions = new boolean[personList.size()];
            return personList.size();
        }
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }
    @Override
    public int getViewTypeCount() {
        System.out.println("viewtypecount  = "+getCount());
        // return getCount()+1;
        return getCount();
    }

    @Override
    public int getItemViewType(int position) {
        System.out.println("viewtype  = "+position);
        return IGNORE_ITEM_VIEW_TYPE;
    }
    boolean[] mHighlightedPositions ;
    ViewHolder holder = null;
    String postid,uid;
    //todo edit getView like in 9sec
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        if(convertView==null&&cho==1) {
            vi = inflater.inflate(R.layout.list_item, null);
            holder=new ViewHolder();

            holder.t1=(TextView) vi.findViewById(R.id.textView9);
            holder.t2=(TextView) vi.findViewById(R.id.textView10);
            holder.iv = (ImageView) vi.findViewById(R.id.iv);
            vi.setTag(holder);
        }
        else {
            System.out.println("error in lazy 1");
            holder = (ViewHolder) vi.getTag();
            //TODO:implement something to recover from error or give msg to user
        }
        if (cho == 1) {
            persons = personList.get(position);
            holder.t1.setText(persons.get("title"));
            holder.t2.setText(persons.get("des"));
            holder.t1.setTag(persons);
            imageLoader.DisplayImage(persons.get("urlToImage"),holder.iv);
        }
        else {
            System.out.println("error in lazy");
            //TODO:implement something to recover from error or give msg to user
        }

        return vi;
    }
    static class ViewHolder{
        TextView t1,t2;
        ImageView iv;
    }

}


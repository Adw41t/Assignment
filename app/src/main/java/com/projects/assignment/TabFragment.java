package com.projects.assignment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;

public class TabFragment extends Fragment {

    int position;
    String title;
    ArrayList<HashMap<String, String>> personList;
    ListView list;
    LazyAdapter adapter;
    JSONArray users;
    ImageButton ref;
    public static Fragment getInstance(int position,String title) {
        Bundle bundle = new Bundle();
        bundle.putInt("pos", position);
        bundle.putString("title",title);
        TabFragment tabFragment = new TabFragment();
        tabFragment.setArguments(bundle);
        return tabFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments().getInt("pos");
        title=getArguments().getString("title");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_tab, container, false);
        list = (ListView) view.findViewById(R.id.listView);
        ref=(ImageButton)view.findViewById(R.id.refresh);
        GetJSON gj=new GetJSON();
        gj.execute("https://newsapi.org/v2/top-headlines?country=in&category="+title+"&apiKey=61c2f4d0cc364098afa1aecde3090ee7");
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RelativeLayout parentrow = (RelativeLayout) view;
                CardView c=(CardView)parentrow.getChildAt(0);
                parentrow=(RelativeLayout)c.getChildAt(0);
                TextView im = (TextView) parentrow.getChildAt(1);
                HashMap<String,String> persons = new HashMap<String,String>();
                persons= (HashMap<String, String>) im.getTag();
                Intent in=new Intent(getContext(),NewsActivity.class);
                in.putExtra("url",persons.get("url"));
                in.putExtra("urlToImage",persons.get("urlToImage"));
                in.putExtra("title",persons.get("title"));
                in.putExtra("des",persons.get("description"));
                in.putExtra("publishedAt",persons.get("publishedAt"));
                startActivity(in);
            }
        });

        ref.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetJSON gj=new GetJSON();
                gj.execute("https://newsapi.org/v2/top-headlines?country=in&category="+title+"&apiKey=61c2f4d0cc364098afa1aecde3090ee7");

            }
        });
    }
    class GetJSON extends AsyncTask<String, Void, String> {
        ProgressDialog loading;
        StringBuilder sb;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(getContext(), "Please Wait...", null, true, true);
        }

        @Override
        protected String doInBackground(String... params) {

            String uri = params[0];

            BufferedReader bufferedReader = null;
            sb = new StringBuilder();
            try {
                HashMap<String, String> params1 = new HashMap<>();
                //    params1.put("amount", "10");

                URL url = new URL(uri);
                //Creating an httmlurl connection
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                //Configuring connection properties
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                //conn.setDoOutput(true);
                int responseCode = conn.getResponseCode();
                if (responseCode == HttpsURLConnection.HTTP_OK) {

                    // HttpURLConnection con = (HttpURLConnection) url.openConnection();


                    bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                    String json;
                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json + "\n");
                    }


                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return sb.toString().trim();
        }

            @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            loading.dismiss();
            personList = new ArrayList<HashMap<String, String>>();
            if (s == null ) {
                Toast.makeText(getContext(),"Can't connect to server",Toast.LENGTH_SHORT).show();
            } else {
                    try {
                        JSONObject jsonObject = new JSONObject(s);
                        users = jsonObject.getJSONArray("articles");
                        for (int i = 0; i < users.length(); i++) {
                            JSONObject c = users.getJSONObject(i);
                            HashMap<String, String> persons = new HashMap<String, String>();
                            persons.put("title", c.getString("title"));
                            persons.put("des", c.getString("description"));
                            persons.put("url", c.getString("url"));
                            persons.put("urlToImage", c.getString("urlToImage"));
                            persons.put("publishedAt", c.getString("publishedAt"));
                            personList.add(persons);
                        }

                        if (personList.size() > 0) {
                            adapter = new LazyAdapter(getActivity(), personList, 1);
                            list.setAdapter(adapter);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
            }
        }
    }

}

package com.projects.assignment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class UserActivity extends AppCompatActivity {
        FirebaseUser u;
        DatabaseReference d;
    ArrayList<HashMap<String, String>> personList;
    ListView list;
    LazyAdapter adapter;
    String uid,name;
    SharedPreferences sharep;
    SharedPreferences.Editor edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        sharep = PreferenceManager.getDefaultSharedPreferences(this);
        edit = sharep.edit();

        personList = new ArrayList<HashMap<String, String>>();
        list=(ListView)findViewById(R.id.listView1);
        if(isNetworkAvailable()) {
            u = FirebaseAuth.getInstance().getCurrentUser();
            if (u != null) {
                uid = u.getUid();
                name = u.getDisplayName();
            } else {
                uid = sharep.getString(getString(R.string.id), "123");
                name = sharep.getString(getString(R.string.name), "User");
            }
        }
        else {
            uid = sharep.getString(getString(R.string.id), "123");
            name = sharep.getString(getString(R.string.name), "User");
        }
        TextView t=(TextView)findViewById(R.id.welcome);
        t.setText("Welcome "+name+" !!");
            d=FirebaseDatabase.getInstance().getReference();

            d.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        System.out.println(postSnapshot.getValue());
                        news n=postSnapshot.getValue(news.class);
                        if(n!=null){
                            HashMap<String, String> persons = new HashMap<String, String>();
                            persons.put("title", n.getTitle());
                            persons.put("des", n.getDes());
                            persons.put("url", n.getUrl());
                            persons.put("urlToImage", n.getUrlToImg());
                            persons.put("publishedAt", n.getPublishedAt());
                            personList.add(persons);
                        }
                    }

                    if (personList.size() > 0) {
                        adapter = new LazyAdapter(UserActivity.this, personList, 1);
                        list.setAdapter(adapter);
                    }
                    else{
                        Toast.makeText(UserActivity.this,"No Bookmarks",Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            d.child(uid).keepSynced(true);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                System.out.println("Item clicked "+position);
                RelativeLayout parentrow = (RelativeLayout) view;
                CardView c=(CardView)parentrow.getChildAt(0);
                parentrow=(RelativeLayout)c.getChildAt(0);
                TextView im = (TextView) parentrow.getChildAt(1);
                HashMap<String,String> persons = new HashMap<String,String>();
                persons= (HashMap<String, String>) im.getTag();
                System.out.println("url= "+persons.get("url"));
                Intent in=new Intent(UserActivity.this,NewsActivity.class);
                in.putExtra("url",persons.get("url"));
                in.putExtra("urlToImage",persons.get("urlToImage"));
                in.putExtra("title",persons.get("title"));
                in.putExtra("des",persons.get("description"));
                in.putExtra("publishedAt",persons.get("publishedAt"));
                startActivity(in);
            }
        });
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService( CONNECTIVITY_SERVICE );
        NetworkInfo activeNetworkInfo = null;
        if (connectivityManager != null) {
            activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        }
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // adds item to action bar
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem item = menu.findItem(R.id.action_login);
        MenuItem item1 = menu.findItem(R.id.action_my_prof);
        if(u==null) {
            item.setVisible(true);
            item.setEnabled(true);
            item1.setVisible(false);
            item1.setEnabled(false);
        }
        else{
            item1.setVisible(true);
            item1.setEnabled(true);
            item.setVisible(false);
            item.setEnabled(false);
        }
        return super.onCreateOptionsMenu(menu);
        //return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent in;
        switch(id) {
            case R.id.action_my_prof:
                in = new Intent(UserActivity.this, UserActivity.class);
            /*in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            in.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            in.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);*/
                startActivity(in);
                break;

            case R.id.action_signo:
                AuthUI.getInstance()
                        .signOut(this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                // ...
                                Toast.makeText(UserActivity.this, "Signed out", Toast.LENGTH_SHORT).show();
                                /*Intent in = new Intent(UserActivity.this, MainActivity.class);
                                startActivity(in);*/
                                u = FirebaseAuth.getInstance().getCurrentUser();
                                invalidateOptionsMenu();
                            }
                        });
                //return true;
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}

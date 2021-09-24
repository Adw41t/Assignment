package com.projects.assignment.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import com.projects.assignment.R;
import com.projects.assignment.adapters.NewsRecyclerAdapter;
import com.projects.assignment.databinding.ActivityUserBinding;
import com.projects.assignment.models.Article;
import com.projects.assignment.models.news;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class UserActivity extends AppCompatActivity {
    FirebaseUser u;
    DatabaseReference d;
    List<Article> list;
    RecyclerView recyclerView;
    NewsRecyclerAdapter adapter;
    String uid,name;
    SharedPreferences sharep;
    SharedPreferences.Editor edit;
    private ActivityUserBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        sharep = PreferenceManager.getDefaultSharedPreferences(this);
        edit = sharep.edit();

        recyclerView=(RecyclerView) binding.userRecyclerNews;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
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
        TextView t=(TextView)binding.welcome;
        t.setText("Welcome "+name+" !!");
        list=new ArrayList<Article>();
        d=FirebaseDatabase.getInstance().getReference();
        d.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    news n=postSnapshot.getValue(news.class);
                    if(n!=null) {
                        Article article=new Article(n.getArticleId(),"","",n.getDes(),n.getPublishedAt(),null,n.getTitle(),n.getUrl(),n.getUrlToImg(),"");
                        list.add(article);
                    }
                }

                if (list.size() > 0) {
                    adapter=new NewsRecyclerAdapter(UserActivity.this,list);
                    recyclerView.setAdapter(adapter);
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
        MenuItem item2 = menu.findItem(R.id.action_signo);
        if(u==null) {
            item.setVisible(true);
            item.setEnabled(true);
            item1.setVisible(false);
            item1.setEnabled(false);
            item2.setVisible(false);
            item2.setEnabled(false);
        }
        else{
            item1.setVisible(true);
            item1.setEnabled(true);
            item2.setVisible(true);
            item2.setEnabled(true);
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
                                u = FirebaseAuth.getInstance().getCurrentUser();
                                invalidateOptionsMenu();
                            }
                        });
                //return true;
                break;
            case R.id.action_day_night:
                boolean dn=sharep.getBoolean(getString(R.string.dayNightTheme),true);
                if(dn) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    edit.putBoolean(getString(R.string.dayNightTheme),false).apply();
                    item.getIcon().setTint(Color.WHITE);
                }
                else{
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    edit.putBoolean(getString(R.string.dayNightTheme),true).apply();
                    item.getIcon().setTint(Color.BLACK);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}

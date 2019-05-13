package com.projects.assignment;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    int SIGN_IN_REQUEST_CODE = 123;
    private static final String TAG = MainActivity.class.getSimpleName();
    ProgressDialog pd;
    FirebaseUser u;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    SharedPreferences sharep;
    SharedPreferences.Editor edit;
    static boolean calledAlready = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!calledAlready)
        {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            calledAlready = true;
        }
        pd = new ProgressDialog(MainActivity.this);
        pd.setMessage("Loading....");
        sharep = PreferenceManager.getDefaultSharedPreferences(this);
        edit = sharep.edit();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE},99);
        }

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        u = FirebaseAuth.getInstance().getCurrentUser();
        if(u!=null){
            edit.putString(getString(R.string.name),u.getDisplayName());
            edit.putString(getString(R.string.id),u.getUid());
            edit.apply();
        }

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
            System.out.println("u= "+u);
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
                in = new Intent(MainActivity.this, UserActivity.class);
            /*in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            in.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            in.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);*/
                startActivity(in);
                break;
            case R.id.action_login:

                List<AuthUI.IdpConfig> providers = Arrays.asList(
                         new AuthUI.IdpConfig.EmailBuilder().build());
                        //new AuthUI.IdpConfig.GoogleBuilder().build());

                if (u == null) {
                    // Start sign in/sign up activity
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setAvailableProviders(providers)
                                    .setLogo(R.drawable.news)      // Set logo drawable
                                    .build(),
                            SIGN_IN_REQUEST_CODE
                    );
                }
                break;
            case R.id.action_signo:
                AuthUI.getInstance()
                        .signOut(this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                // ...
                                Toast.makeText(MainActivity.this, "Signed out", Toast.LENGTH_SHORT).show();
                               /* Intent in = new Intent(MainActivity.this, MainActivity.class);
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SIGN_IN_REQUEST_CODE) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                Toast.makeText(this,
                        "Successfully signed in. Welcome!",
                        Toast.LENGTH_LONG)
                        .show();
                u = FirebaseAuth.getInstance().getCurrentUser();
                    invalidateOptionsMenu();
            } else {
                // Sign in failed
                if (response == null) {
                    // User pressed back button
                    Log.e(TAG, "Sign-in cancelled: ");
                }

                if (response != null) {
                    Toast.makeText(this,
                            "No Internet Connection.",
                            Toast.LENGTH_LONG)
                            .show();

                }
                Toast.makeText(this,
                        "We couldn't sign you in. Please try again later.",
                        Toast.LENGTH_LONG)
                        .show();
                if (response != null) {
                    Log.e(TAG, "Sign-in error: " + response.getError());
                }


                // Close the app
               // finish();
            }
        }
    }

}

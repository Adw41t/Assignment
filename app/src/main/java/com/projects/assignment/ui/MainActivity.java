package com.projects.assignment.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.projects.assignment.R;
import com.projects.assignment.adapters.ViewPagerAdapter;

import java.util.Arrays;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {
    int SIGN_IN_REQUEST_CODE = 123;
    private static final String TAG = MainActivity.class.getSimpleName();
    FirebaseUser u;
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
        sharep = PreferenceManager.getDefaultSharedPreferences(this);
        edit = sharep.edit();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ViewPager2 viewPager = (ViewPager2) findViewById(R.id.viewpager);
        viewPager.setPageTransformer(new DepthPageTransformer());
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(),getLifecycle());
        viewPager.setAdapter(adapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(adapter.title[position])
        ).attach();
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
                in = new Intent(MainActivity.this, UserActivity.class);
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
                                    .setLogo(R.mipmap.ic_launcher_round)      // Set logo drawable
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
                }
                else{
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    edit.putBoolean(getString(R.string.dayNightTheme),true).apply();
                }
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

package com.projects.assignment;

import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.List;

public class NewsActivity extends AppCompatActivity {
    ImageButton fab;
    WebView webView;
    FirebaseUser u;
    int SIGN_IN_REQUEST_CODE = 123;
    private static final String TAG = MainActivity.class.getSimpleName();
    Intent in;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        fab=(ImageButton) findViewById(R.id.fab);
        fab.setBackgroundResource(android.R.drawable.btn_star_big_off);
         in=getIntent();
        String url=in.getStringExtra("url");
        u = FirebaseAuth.getInstance().getCurrentUser();

        if(u==null){
            fab.setVisibility(View.INVISIBLE);
            fab.setEnabled(false);
        }

        webView = (WebView) findViewById(R.id.webview);
        webView.getSettings().setAppCacheMaxSize( 5 * 1024 * 1024 ); // 5MB
        webView.getSettings().setAppCachePath( getApplicationContext().getCacheDir().getAbsolutePath() );
        webView.getSettings().setAllowFileAccess( true );
        webView.getSettings().setAppCacheEnabled( true );
        webView.getSettings().setJavaScriptEnabled( true );
        webView.setWebViewClient(new MyWebViewClient());
        webView.getSettings().setCacheMode( WebSettings.LOAD_DEFAULT ); // load online by default

        if ( !isNetworkAvailable() ) { // loading offline
            webView.getSettings().setCacheMode( WebSettings.LOAD_CACHE_ELSE_NETWORK );
        }
        if(url!=null)
        webView.loadUrl(url);
    }
    public void bookmark(View view){
        fab.setBackgroundResource(android.R.drawable.btn_star_big_on);
        if(u!=null) {
            news n = new news(in.getStringExtra("title"), in.getStringExtra("des"), in.getStringExtra("url"), in.getStringExtra("urlToImage"), in.getStringExtra("publishedAt"));
            String key = FirebaseDatabase.getInstance().getReference().child(u.getUid()).push().getKey();
            FirebaseDatabase.getInstance().getReference().child(u.getUid()).child(key).setValue(n).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    System.out.println("Bookmarked");
                }
            });
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService( CONNECTIVITY_SERVICE );
        NetworkInfo activeNetworkInfo = null;
        if (connectivityManager != null) {
            activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        }
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if(url!=null)
                view.loadUrl(url);
            return true;
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Check if the key event was the Back button and if there's history
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        // If it wasn't the Back key or there's no web page history, bubble up to the default
        // system behavior (probably exit the activity)
        return super.onKeyDown(keyCode, event);
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
                in = new Intent(NewsActivity.this, UserActivity.class);
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
                                    .setLogo(R.drawable.ic_launcher_background)      // Set logo drawable
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
                                Toast.makeText(NewsActivity.this, "Signed out", Toast.LENGTH_SHORT).show();
                                /*Intent in = new Intent(NewsActivity.this, MainActivity.class);
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
                    Log.e(TAG, "Sign-in cancelled ");
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

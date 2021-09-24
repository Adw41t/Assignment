package com.projects.assignment.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.projects.assignment.R;
import com.projects.assignment.databinding.ActivityNewsBinding;
import com.projects.assignment.models.Article;
import com.projects.assignment.models.news;

import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.webkit.WebSettingsCompat;
import androidx.webkit.WebViewFeature;

public class NewsActivity extends AppCompatActivity {
    ImageButton fab;
    WebView webView;
    FirebaseUser u;
    int SIGN_IN_REQUEST_CODE = 123;
    ProgressBar progressBar_newsWebView;
    private static final String TAG = MainActivity.class.getSimpleName();
    Intent in;
    Article article;
    SharedPreferences sharep;
    boolean isDayMode;
    private ActivityNewsBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNewsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        fab=(ImageButton) binding.ibBookmark;
        fab.setBackgroundResource(android.R.drawable.btn_star_big_off);
        progressBar_newsWebView = binding.progressNewsWebview;
        sharep = PreferenceManager.getDefaultSharedPreferences(this);
        isDayMode = sharep.getBoolean(getString(R.string.dayNightTheme),true);
        in=getIntent();
        Bundle bundle=in.getBundleExtra(getString(R.string.articleBundle));
        if(bundle!=null)
            article= (Article) bundle.getSerializable(getString(R.string.articleObject));
        String url = null;
        if(article!=null){
            url = article.getUrl();
        }
        u = FirebaseAuth.getInstance().getCurrentUser();

        if(u==null){
            fab.setVisibility(View.INVISIBLE);
            fab.setEnabled(false);
        }

        webView = (WebView) binding.webview;
        webView.getSettings().setAppCacheMaxSize( 5 * 1024 * 1024 ); // 5MB
        webView.getSettings().setAppCachePath( getApplicationContext().getCacheDir().getAbsolutePath() );
        webView.getSettings().setAppCacheEnabled( true );
        WebView.setWebContentsDebuggingEnabled(false);
        webView.setWebViewClient(new MyWebViewClient());
        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                if(progressBar_newsWebView!=null) {
                    progressBar_newsWebView.setProgress(progress);
                }
            }
        });
        webView.getSettings().setCacheMode( WebSettings.LOAD_DEFAULT ); // load online by default

        if ( !isNetworkAvailable() ) { // loading offline
            webView.getSettings().setCacheMode( WebSettings.LOAD_CACHE_ELSE_NETWORK );
        }

        if (isDayMode && WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
            WebSettingsCompat.setForceDark(webView.getSettings(),WebSettingsCompat.FORCE_DARK_OFF);
        }
        else if(!isDayMode && WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)){
            WebSettingsCompat.setForceDark(webView.getSettings(),WebSettingsCompat.FORCE_DARK_ON);
        }

        if(url!=null) {
            webView.loadUrl(url);
        }
    }
    public void bookmark(View view){
        fab.setBackgroundResource(android.R.drawable.btn_star_big_on);
        if(u!=null) {
            if(article!=null) {
                String key = FirebaseDatabase.getInstance().getReference().child(u.getUid()).push().getKey();
                if(key==null)
                    key=u.getUid();
                news news=new news(article.getTitle(),article.getDescription(),article.getUrl(),article.getUrlToImage(),article.getPublishedAt(),article.getArticleId());
                FirebaseDatabase.getInstance().getReference().child(u.getUid()).child(key).setValue(news).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(NewsActivity.this,
                                "Bookmarked!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(progressBar_newsWebView!=null){
            progressBar_newsWebView.setVisibility(View.GONE);
        }
    }

    @SuppressLint("MissingPermission")
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
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            progressBar_newsWebView.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            progressBar_newsWebView.setVisibility(View.GONE);
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
        menu.findItem(R.id.action_day_night).setVisible(false);
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
                                Toast.makeText(NewsActivity.this, "Signed out", Toast.LENGTH_SHORT).show();
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

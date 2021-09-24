package com.projects.assignment.ui;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.projects.assignment.adapters.NewsRecyclerAdapter;
import com.projects.assignment.databinding.FragmentTabBinding;
import com.projects.assignment.models.Article;
import com.projects.assignment.viewmodels.NewsViewModel;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class TabFragment extends Fragment {

    int position;
    String title;
    SwipeRefreshLayout ref;
    RecyclerView recyclerView;
    NewsRecyclerAdapter adapter;
    NewsViewModel viewModel;
    ProgressDialog loading;
    private FragmentTabBinding binding;
    SharedPreferences sharep;
    SharedPreferences.Editor edit;
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
        if (getArguments() != null) {
            position = getArguments().getInt("pos");
            title=getArguments().getString("title");
        }
        sharep = PreferenceManager.getDefaultSharedPreferences(getContext());
        edit = sharep.edit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTabBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        ref=(SwipeRefreshLayout) binding.tabFragmentSwipeRefresh;
        recyclerView = binding.recyclerNews;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        viewModel= new ViewModelProvider(this).get(NewsViewModel.class);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getHeadlinesForCategory();
        ref.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getHeadlinesForCategory();
            }
        });
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(loading!=null){
            loading.dismiss();
        }
        if(ref!=null){
            ref.setRefreshing(false);
        }
    }

    public void getHeadlinesForCategory(){

        boolean getNews = true;
        if (sharep!=null) {
            getNews = checkIfTimeLessThanTenMinutes(sharep.getLong(title, System.currentTimeMillis()));
        }
        if(getNews && (sharep!=null && edit!=null)){
            edit.putLong(title,System.currentTimeMillis()).commit();
        }
        if(ref!=null){
            ref.setRefreshing(true);
        }
        loading = ProgressDialog.show(getContext(), "Please Wait...", null, true, true);
        viewModel.getArticlesByCategory("in",title, getNews).observe(getViewLifecycleOwner(), new Observer<List<Article>>() {
            @Override
            public void onChanged(List<Article> articles) {
                if(loading!=null) {
                    loading.dismiss();
                }
                if(ref!=null){
                    ref.setRefreshing(false);
                }
                if(articles!=null){
                    adapter=new NewsRecyclerAdapter(getContext(),articles);
                    recyclerView.setAdapter(adapter);
                }
            }
        });
    }

    private boolean checkIfTimeLessThanTenMinutes(Long time){

        long now = System.currentTimeMillis();
        if((now - time)<1000){
            //first time
            return true;
        }
        else if(((now/60000) - (time/60000)) > 10 ){
            return true;
        }
        return false;
    }
}

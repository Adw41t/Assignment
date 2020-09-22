package com.projects.assignment.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.projects.assignment.R;
import com.projects.assignment.adapters.NewsRecyclerAdapter;
import com.projects.assignment.models.Article;
import com.projects.assignment.viewmodels.NewsViewModel;

import java.util.List;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_tab, container, false);
        ref=(SwipeRefreshLayout) view.findViewById(R.id.tab_fragment_swipeRefresh);

        recyclerView=view.findViewById(R.id.recyclerNews);

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

        if(ref!=null){
            ref.setRefreshing(true);
        }
        loading = ProgressDialog.show(getContext(), "Please Wait...", null, true, true);
        viewModel.getArticlesByCategory("in",title).observe(getViewLifecycleOwner(), new Observer<List<Article>>() {
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
}

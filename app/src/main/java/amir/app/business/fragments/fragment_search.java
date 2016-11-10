package amir.app.business.fragments;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.strongloop.android.loopback.callbacks.ListCallback;

import java.util.List;

import amir.app.business.GuideApplication;
import amir.app.business.R;
import amir.app.business.adapter.BusinessHorizontalListAdapter;
import amir.app.business.adapter.BusinessVerticalListAdapter;
import amir.app.business.models.Businesse;
import amir.app.business.models.Location;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by amin on 08/20/2016.
 */

public class fragment_search extends baseFragment {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.progress)
    ProgressBar progress;

    List<Businesse> businesses;
    BusinessHorizontalListAdapter adapter;
    SearchView searchView = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, null);
        ButterKnife.bind(this, view);

        setHasOptionsMenu(true);

        //config toolbar
        getactivity().setSupportActionBar(toolbar);
        getactivity().getSupportActionBar().setTitle("");
        getactivity().getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getactivity().getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //setup back button on toolbar
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getactivity().onBackPressed();
            }
        });

        if (businesses != null)
            setup_adapter_and_views();

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);

        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);

        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setIconifiedByDefault(false);
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    search(query);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }
            });
        }

    }

    private void search(String query) {
        progress.setVisibility(View.VISIBLE);

        Businesse.Repository repository = GuideApplication.getLoopBackAdapter().createRepository(Businesse.Repository.class);
        repository.searchBusiness(0, query, new ListCallback<Businesse>() {
            public void onSuccess(List<Businesse> items) {
                businesses = items;

//                Dummy for fill UI
//                for (int i = 0; i < 10; i++) {
//                    Businesse b = new Businesse();
//                    b.setName("business " + i);
//                    b.setDescription("description " + i);
//
//                    Location location = new Location();
//                    location.lat = (int) (32 + i * Math.random());
//                    location.lng = (int) (52 + i * Math.random());
//
//                    b.setLocation(location);
//                    businesses.add(b);
//                }
//
                progress.setVisibility(View.GONE);
                setup_adapter_and_views();
            }

            @Override
            public void onError(Throwable t) {

            }
        });
    }

    private void setup_adapter_and_views() {
        //setup init views
        init_layout();

        //setup top businesses view
        adapter = new BusinessHorizontalListAdapter(getActivity(), businesses);
        adapter.setOnItemClickListener(new BusinessHorizontalListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Businesse businesse) {
                switch_to_business_page(businesse);
            }
        });
        recyclerview.setAdapter(adapter);
        recyclerview.setNestedScrollingEnabled(false);
    }

    //setup recyclerview lists
    private void init_layout() {
        recyclerview.setLayoutManager(new GridLayoutManager(getActivity(), 3, LinearLayoutManager.VERTICAL, false));
    }

    private void switch_to_business_page(Businesse businesse) {
        switchFragment(new fragment_business().newInstance(businesse), true);
    }
}

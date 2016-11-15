package amir.app.business.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.android.gms.maps.GoogleMap;
import com.strongloop.android.loopback.callbacks.ListCallback;

import java.util.List;

import amir.app.business.GuideApplication;
import amir.app.business.R;
import amir.app.business.adapter.BusinessHorizontalListAdapter;
import amir.app.business.fragments.business.fragment_business;
import amir.app.business.models.Businesse;
import amir.app.business.models.Category;
import amir.app.business.util;
import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by amin on 08/09/2016.
 */

public class fragment_category_page extends baseFragment {
    @BindView(R.id.businessRecyclerview)
    RecyclerView businessRecyclerview;
    @BindView(R.id.topRecyclerview)
    RecyclerView topRecyclerview;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.progress)
    ProgressBar progress;

    List<Businesse> businesse;
    GoogleMap map;
    Category category;

    public static fragment_category_page newInstance(Category category) {
        fragment_category_page fragment = new fragment_category_page();
        fragment.category = category;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category_page, null);

        ButterKnife.bind(this, view);

        //config toolbar
        getactivity().setSupportActionBar(toolbar);
        getactivity().getSupportActionBar().setTitle(category.getName());
        getactivity().getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getactivity().getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //setup back button on toolbar
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getactivity().onBackPressed();
            }
        });

        //load category business list via api
        load_business_list();

        return view;
    }

    private void load_business_list() {
        businessRecyclerview.setLayoutManager(new GridLayoutManager(getActivity(), 3, LinearLayoutManager.VERTICAL, false));

        Businesse.Repository repository = GuideApplication.getLoopBackAdapter().createRepository(Businesse.Repository.class);

        repository.findAll(new ListCallback<Businesse>() {
            @Override
            public void onSuccess(List<Businesse> items) {
                businesse = items;

                for (int i = 0; i < 10; i++) {
                    Businesse b = new Businesse();
                    b.setName("business " + i);
                    b.setDescription("description " + i);
                    businesse.add(b);
                }

                progress.setVisibility(View.GONE);
                setup_adapter_and_views();
            }

            @Override
            public void onError(Throwable t) {
                progress.setVisibility(View.GONE);

                util.alertDialog(getActivity(), "بستن", "خطا در ارتباط با شبکه", "خطا", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        getactivity().onBackPressed();
                    }
                }, SweetAlertDialog.ERROR_TYPE);
            }
        });
    }

    //setup recyclerview lists
    private void init_layout() {
        topRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        businessRecyclerview.setLayoutManager(new GridLayoutManager(getActivity(), 3, LinearLayoutManager.VERTICAL, false));
    }

    private void setup_adapter_and_views() {
        init_layout();

        //setup top businesses view
        BusinessHorizontalListAdapter topadapter = new BusinessHorizontalListAdapter(getActivity(), businesse);
        topadapter.setOnItemClickListener(new BusinessHorizontalListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Businesse businesse) {
                switch_to_business_page(businesse);
            }
        });
        topRecyclerview.setAdapter(topadapter);
        topRecyclerview.setNestedScrollingEnabled(false);

        //setup main businesses view
        BusinessHorizontalListAdapter adapter = new BusinessHorizontalListAdapter(getActivity(), businesse);
        adapter.setOnItemClickListener(new BusinessHorizontalListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Businesse businesse) {
                switch_to_business_page(businesse);
            }
        });
        businessRecyclerview.setAdapter(adapter);
        businessRecyclerview.setNestedScrollingEnabled(false);
    }

    public void switch_to_business_page(Businesse businesse) {
        switchFragment(new fragment_business().newInstance(businesse), true);
    }
}

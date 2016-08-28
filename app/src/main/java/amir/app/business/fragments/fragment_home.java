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
import android.widget.ImageView;

import com.strongloop.android.loopback.callbacks.ListCallback;

import java.util.List;

import amir.app.business.GuideApplication;
import amir.app.business.R;
import amir.app.business.adapter.BusinessHorizontalListAdapter;
import amir.app.business.adapter.BusinessVerticalListAdapter;
import amir.app.business.models.Businesse;
import amir.app.business.widget.FarsiTextView;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by amin on 08/20/2016.
 */

public class fragment_home extends baseFragment {
    @BindView(R.id.adverPager)
    ImageView adverPager;
    @BindView(R.id.txttoptitle)
    FarsiTextView txttoptitle;
    @BindView(R.id.topRecyclerview)
    RecyclerView topRecyclerview;
    @BindView(R.id.businessRecyclerview)
    RecyclerView businessRecyclerview;

    List<Businesse> businesses;

    BusinessHorizontalListAdapter topadapter;
    BusinessVerticalListAdapter mainadapter;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, null);
        ButterKnife.bind(this, view);

        //setup init views
        init_layout();

        //load business list via api
        load_business_list();

        return view;
    }

    //setup recyclerview lists
    private void init_layout() {
        topRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        businessRecyclerview.setLayoutManager(new GridLayoutManager(getActivity(), 2, LinearLayoutManager.VERTICAL, false));
    }


    //read all business and fill list
    private void load_business_list() {
        if (businesses != null) {
            setup_adapter_and_views();
            return;
        }

        Businesse.Repository repository = GuideApplication.getLoopBackAdapter().createRepository(Businesse.Repository.class);

        repository.findAll(new ListCallback<Businesse>() {
            @Override
            public void onSuccess(List<Businesse> items) {
                businesses = items;

                for (int i = 0; i < 10; i++) {
                    Businesse b = new Businesse();
                    b.setName("business " + i);
                    b.setDescription("description " + i);
                    businesses.add(b);
                }

                setup_adapter_and_views();

            }

            @Override
            public void onError(Throwable t) {

            }
        });
    }

    private void setup_adapter_and_views() {
        //setup top businesses view
        topadapter = new BusinessHorizontalListAdapter(getActivity(), businesses);
        topadapter.setOnItemClickListener(new BusinessHorizontalListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Businesse businesse) {
                switch_to_business_page(businesse);
            }
        });
        topRecyclerview.setAdapter(topadapter);
        topRecyclerview.setNestedScrollingEnabled(false);


        //setup main businesses view
        mainadapter = new BusinessVerticalListAdapter(getActivity(), businesses);
        mainadapter.setOnItemClickListener(new BusinessVerticalListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Businesse businesse) {
                switch_to_business_page(businesse);
            }
        });
        businessRecyclerview.setAdapter(mainadapter);
        businessRecyclerview.setNestedScrollingEnabled(false);
    }

    public void switch_to_business_page(Businesse businesse) {
        switchfragment(new fragment_business().newInstance(businesse), true);
    }
}

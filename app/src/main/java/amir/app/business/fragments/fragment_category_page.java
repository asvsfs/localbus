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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import amir.app.business.EndlessRecyclerOnScrollListener;
import amir.app.business.GuideApplication;
import amir.app.business.R;
import amir.app.business.adapter.ProductHorizontalListAdapter;
import amir.app.business.fragments.product.fragment_product;
import amir.app.business.models.Category;
import amir.app.business.models.Product;
import amir.app.business.util;
import amir.app.business.widget.FarsiTextView;
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
    @BindView(R.id.txtcount)
    FarsiTextView txtcount;

    List<Product> products;
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

        products = new ArrayList<>();

        init_layout();

        //load category business list via api
        load_business_list(0);

        return view;
    }

    private void load_business_list(int page) {
        Product.Repository repository = GuideApplication.getLoopBackAdapter().createRepository(Product.Repository.class);

        repository.productByCategory(page, category.getId(), new ListCallback<Product>() {
            @Override
            public void onSuccess(List<Product> items) {
                products.addAll(items);

                txtcount.setText(String.format(Locale.ENGLISH, "%d", products.size()));
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

        businessRecyclerview.setOnScrollListener(new EndlessRecyclerOnScrollListener((LinearLayoutManager) businessRecyclerview.getLayoutManager()) {
            @Override
            public void onLoadMore(int current_page) {
                load_business_list(current_page - 1);
            }
        });
    }

    private void setup_adapter_and_views() {


        //setup top businesses view
        ProductHorizontalListAdapter topadapter = new ProductHorizontalListAdapter(getActivity(), products);
        topadapter.setOnItemClickListener(new ProductHorizontalListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Product product) {
                switch_to_product_page(product);
            }
        });
        topRecyclerview.setAdapter(topadapter);
        topRecyclerview.setNestedScrollingEnabled(false);


        //setup main businesses view
        ProductHorizontalListAdapter adapter = new ProductHorizontalListAdapter(getActivity(), products);
        adapter.setOnItemClickListener(new ProductHorizontalListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Product product) {
                switch_to_product_page(product);
            }
        });
        businessRecyclerview.setAdapter(adapter);
        businessRecyclerview.setNestedScrollingEnabled(false);
    }

    public void switch_to_product_page(Product product) {
        switchFragment(new fragment_product().newInstance(product), true);
    }
}

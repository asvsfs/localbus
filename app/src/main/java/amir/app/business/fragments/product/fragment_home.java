package amir.app.business.fragments.product;

import android.content.Context;
import android.location.Criteria;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.strongloop.android.loopback.callbacks.ListCallback;

import java.util.ArrayList;
import java.util.List;

import amir.app.business.GuideApplication;
import amir.app.business.R;
import amir.app.business.adapter.AdverListAdapter;
import amir.app.business.adapter.ProductHorizontalListAdapter;
import amir.app.business.fragments.baseFragment;
import amir.app.business.models.Product;
import amir.app.business.widget.CircleIndicator;
import amir.app.business.widget.FarsiTextView;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by amin on 08/20/2016.
 */

public class fragment_home extends baseFragment implements OnMapReadyCallback {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.adverPager)
    ViewPager adverPager;
    @BindView(R.id.txttoptitle)
    FarsiTextView txttoptitle;
    @BindView(R.id.topRecyclerview)
    RecyclerView topRecyclerview;
    @BindView(R.id.productRecyclerview)
    RecyclerView productRecyclerview;
    @BindView(R.id.indicator)
    CircleIndicator indicator;

    List<Product> products;
    GoogleMap map;
    ProductHorizontalListAdapter topadapter;
    ProductHorizontalListAdapter mainadapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_home, null);
        ButterKnife.bind(this, view);

        //config toolbar
        getactivity().setSupportActionBar(toolbar);
        getactivity().getSupportActionBar().setTitle(R.string.app_name);
        getactivity().getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(false);
        getactivity().getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        load_advers();

        //load product list via api
        load_product_list();
        return view;
    }

    private void load_advers() {
        List<String> adver = new ArrayList<>();

        //template
        adver.add("");
        adver.add("");
        adver.add("");
        //template

        adverPager.setAdapter(new AdverListAdapter(getactivity(), adver));
        indicator.setViewPager(adverPager);
    }

    //setup recyclerview lists
    private void init_layout() {
        topRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        productRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
    }


    //read all product and fill list
    private void load_product_list() {
        if (products != null) {
            setup_adapter_and_views();
            return;
        }

        Product.Repository repository = GuideApplication.getLoopBackAdapter().createRepository(Product.Repository.class);
        repository.findAll(new ListCallback<Product>() {
            @Override
            public void onSuccess(List<Product> items) {
                products = items;

                //fake product
                for (int i = 0; i < 10; i++) {
                    Product b = new Product();
                    b.setId("121221");
                    b.setName("product " + i);
                    b.setDescription("description " + i);

                    products.add(b);
                }
                //fake product

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

        //setup top products view
        topadapter = new ProductHorizontalListAdapter(getActivity(), products);
        topadapter.setOnItemClickListener(new ProductHorizontalListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Product product) {
                switch_to_product_page(product);
            }
        });
        topRecyclerview.setAdapter(topadapter);
        topRecyclerview.setNestedScrollingEnabled(false);


        //setup main products view
        mainadapter = new ProductHorizontalListAdapter(getActivity(), products);
        mainadapter.setOnItemClickListener(new ProductHorizontalListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Product product) {
                switch_to_product_page(product);
            }
        });
        productRecyclerview.setAdapter(mainadapter);
        productRecyclerview.setNestedScrollingEnabled(false);

    }

    public void switch_to_product_page(Product product) {
        switchFragment(new fragment_product().newInstance(product), true);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        map.getUiSettings().setMyLocationButtonEnabled(false);
        map.getUiSettings().setAllGesturesEnabled(false);
        map.setMyLocationEnabled(true);

        LocationManager locationManager = (LocationManager)
                getactivity().getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();

        android.location.Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        map.moveCamera( CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude) , 10) );


    }
}

package amir.app.business.fragments.business;

import android.content.Context;
import android.location.Criteria;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.strongloop.android.loopback.callbacks.ListCallback;

import java.util.ArrayList;
import java.util.List;

import amir.app.business.GuideApplication;
import amir.app.business.R;
import amir.app.business.adapter.AdverListAdapter;
import amir.app.business.adapter.BusinessHorizontalListAdapter;
import amir.app.business.fragments.baseFragment;
import amir.app.business.fragments.fragment_map;
import amir.app.business.models.Businesse;
import amir.app.business.models.Location;
import amir.app.business.widget.CircleIndicator;
import amir.app.business.widget.FarsiTextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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
    @BindView(R.id.businessRecyclerview)
    RecyclerView businessRecyclerview;
    @BindView(R.id.mapview)
    MapView mapview;
    @BindView(R.id.scrollview)
    NestedScrollView scrollview;
    @BindView(R.id.imgfull)
    ImageView imgfull;
    @BindView(R.id.indicator)
    CircleIndicator indicator;

    List<Businesse> businesses;
    GoogleMap map;
    BusinessHorizontalListAdapter topadapter;
    BusinessHorizontalListAdapter mainadapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_business_home, null);
        ButterKnife.bind(this, view);

        //config toolbar
        getactivity().setSupportActionBar(toolbar);
        getactivity().getSupportActionBar().setTitle(R.string.app_name);
        getactivity().getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(false);
        getactivity().getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        setup_map_view(savedInstanceState);

        load_advers();
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

    private void setup_map_view(@Nullable Bundle savedInstanceState) {
        mapview.onCreate(savedInstanceState);

//        mapview.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_MOVE:
//                        scrollview.requestDisallowInterceptTouchEvent(true);
//                        break;
//                    case MotionEvent.ACTION_UP:
//                    case MotionEvent.ACTION_CANCEL:
//                        scrollview.requestDisallowInterceptTouchEvent(true);
//                        break;
//                }
//                return mapview.onTouchEvent(event);
//            }
//        });

        // Gets to GoogleMap from the MapView and does initialization stuff
        mapview.getMapAsync(this);

        // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
        MapsInitializer.initialize(this.getActivity());

    }

    //setup recyclerview lists
    private void init_layout() {
        topRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        businessRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
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

                    Location location = new Location();
                    location.lat = (int) (32 + i * Math.random());
                    location.lng = (int) (52 + i * Math.random());

                    b.setLocation(location);
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
        //setup init views
        init_layout();

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
        mainadapter = new BusinessHorizontalListAdapter(getActivity(), businesses);
        mainadapter.setOnItemClickListener(new BusinessHorizontalListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Businesse businesse) {
                switch_to_business_page(businesse);
            }
        });
        businessRecyclerview.setAdapter(mainadapter);
        businessRecyclerview.setNestedScrollingEnabled(false);

        setup_marker_list();
    }

    public void switch_to_business_page(Businesse businesse) {
        switchFragment(new fragment_business().newInstance(businesse), true);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapview.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();

        mapview.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapview.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapview.onLowMemory();
    }

    @OnClick(R.id.imgfull)
    public void fullscreen() {
        switchFragment(new fragment_map().newInstance(businesses), true);
    }

    private void setup_marker_list() {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        for (Businesse business : businesses) {
            if (business.getLocation() != null) {
                Marker marker = map.addMarker(new MarkerOptions()
                        .position(new LatLng(business.getLocation().lat, business.getLocation().lng))
                        .title(business.getName())
                        .snippet(business.getDescription()));

                builder.include(marker.getPosition());
            }
        }

//
//        LatLngBounds bounds = builder.build();
//        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 0);
//        try {
////            map.moveCamera(cu);
//
//            // Updates the location and zoom of the MapView
//            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(bounds.getCenter(), 8);
//            map.animateCamera(cameraUpdate);
//
//        } catch (Exception ignored) {
//        }

        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                for (Businesse business : businesses) {
                    if (business.getLocation() != null && business.getLocation().lat == marker.getPosition().latitude && business.getLocation().lng == marker.getPosition().longitude) {
                        switchFragment(new fragment_business().newInstance(business), true);
                        break;
                    }
                }
            }
        });
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

        //load business list via api
        load_business_list();
    }
}

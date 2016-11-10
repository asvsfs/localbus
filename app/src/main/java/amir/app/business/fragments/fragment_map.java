package amir.app.business.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import amir.app.business.R;
import amir.app.business.models.Businesse;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by amin on 08/20/2016.
 */

public class fragment_map extends baseFragment implements OnMapReadyCallback {
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.mapview)
    MapView mapview;

    List<Businesse> businesses;
    GoogleMap map;


    public fragment_map newInstance(List<Businesse> businesses) {
        fragment_map f = new fragment_map();
        f.businesses = businesses;

        return f;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, null);
        ButterKnife.bind(this, view);

        //config toolbar
        getactivity().setSupportActionBar(toolbar);
        getactivity().getSupportActionBar().setTitle("نقشه کسب و کار");
        getactivity().getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getactivity().getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //setup back button on toolbar
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getactivity().onBackPressed();
            }
        });

        setup_map_view(savedInstanceState);

        return view;
    }

    private void setup_map_view(@Nullable Bundle savedInstanceState) {
        mapview.onCreate(savedInstanceState);

        // Gets to GoogleMap from the MapView and does initialization stuff
        mapview.getMapAsync(this);

        // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
        MapsInitializer.initialize(this.getActivity());

        // Updates the location and zoom of the MapView
//        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(43.1, -87.9), 10);
//        map.animateCamera(cameraUpdate);
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

        LatLngBounds bounds = builder.build();
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 0);
        try {
            map.moveCamera(cu);
        } catch (Exception ignored) {
        }

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
        map.setMyLocationEnabled(true);

        //load business list via api
        setup_marker_list();
    }
}

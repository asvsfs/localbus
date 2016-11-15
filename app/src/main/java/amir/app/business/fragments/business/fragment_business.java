package amir.app.business.fragments.business;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
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
import com.strongloop.android.loopback.callbacks.ListCallback;
import com.strongloop.android.loopback.callbacks.VoidCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import amir.app.business.GuideApplication;
import amir.app.business.R;
import amir.app.business.adapter.BusinessHorizontalListAdapter;
import amir.app.business.adapter.GalleryListAdapter;
import amir.app.business.config;
import amir.app.business.fragments.baseFragment;
import amir.app.business.fragments.fragment_comment;
import amir.app.business.models.Businesse;
import amir.app.business.models.Comment;
import amir.app.business.models.Location;
import amir.app.business.util;
import amir.app.business.widget.CircleIndicator;
import amir.app.business.widget.FarsiTextView;
import amir.app.business.widget.widgettools;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by amin on 08/09/2016.
 */

public class fragment_business extends baseFragment implements OnMapReadyCallback {
    @BindView(R.id.imagePager)
    ViewPager imagePager;
    @BindView(R.id.indicator)
    CircleIndicator indicator;
    @BindView(R.id.txtdistance)
    TextView txtdistance;
    @BindView(R.id.txtname)
    FarsiTextView txtname;
    @BindView(R.id.txtdesc)
    FarsiTextView txtdesc;
    @BindView(R.id.similarRecyclerview)
    RecyclerView similarRecyclerview;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.txtverification)
    FarsiTextView txtverification;
    @BindView(R.id.btncomments)
    Button btncomments;
    @BindView(R.id.commentProgress)
    ProgressBar commentProgress;
    @BindView(R.id.commentInnerLayout)
    View commentInnerLayout;
    @BindView(R.id.btnSendComment)
    ImageView btnSendComment;
    @BindView(R.id.txtlastcomment)
    TextView txtlastcomment;
    @BindView(R.id.lastRatingBar)
    RatingBar lastRatingBar;
    @BindView(R.id.ratingbar)
    RatingBar ratingbar;
    @BindView(R.id.btnlike)
    Button btnlike;
    @BindView(R.id.btnshare)
    ImageView btnshare;
    @BindView(R.id.mapview)
    MapView mapview;
    @BindView(R.id.btnroute)
    Button btnroute;

    Businesse businesse;
    GoogleMap map;

    public static fragment_business newInstance(Businesse business) {
        fragment_business fragment = new fragment_business();
        fragment.businesse = business;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_business, null);

        ButterKnife.bind(this, view);


        setHasOptionsMenu(true);

        //config toolbar
        getactivity().setSupportActionBar(toolbar);
        getactivity().getSupportActionBar().setTitle(businesse.getName());
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

        //load business list via api
        load_similar_business_list();

        //load three lastest comment about this business
        load_latest_comments_list();

        load_business_images();

        return view;
    }

    private void load_business_images() {
        List<String> images = businesse.getImages();

        //template
        if (images == null || images.size() == 0) {
            images = new ArrayList<>();

            images.add("");
            images.add("");
            images.add("");
        }
        //template

        imagePager.setAdapter(new GalleryListAdapter(getactivity(), images));
        indicator.setViewPager(imagePager);

        Display display = getactivity().getWindowManager().getDefaultDisplay();
        imagePager.getLayoutParams().height = display.getWidth();
    }

    //Create Markers from Business Info on the map.
    private void setup_marker_list() {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        if (businesse.getLocation() != null) {
            Marker marker = map.addMarker(new MarkerOptions()
                    .position(new LatLng(businesse.getLocation().lat, businesse.getLocation().lng))
                    .title(businesse.getName())
                    .snippet(businesse.getDescription()));

            CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(new LatLng(businesse.getLocation().lat, businesse.getLocation().lng), 10);
            try {
                map.moveCamera(cu);

            } catch (Exception ignored) {
            }
        }

    }

    private void setup_map_view(@Nullable Bundle savedInstanceState) {
        mapview.onCreate(savedInstanceState);

        // Gets to GoogleMap from the MapView and does initialization stuff
        mapview.getMapAsync(this);

//       mapview.setClickable(false);
        mapview.getMap().getUiSettings().setScrollGesturesEnabled(false);
    }

    //load comments and select last comment to show on UI
    private void load_latest_comments_list() {
        Comment.Repository repository = GuideApplication.getLoopBackAdapter().createRepository(Comment.Repository.class);
        repository.getByBusinessId(businesse.getId(), new ListCallback<Comment>() {
            @Override
            public void onSuccess(List<Comment> comments) {
                commentProgress.setVisibility(View.GONE);
                commentInnerLayout.setVisibility(View.VISIBLE);

                if (comments.size() > 0)
                    txtlastcomment.setText(comments.get(0).getText());
            }

            @Override
            public void onError(Throwable t) {

            }
        });

    }

    private void load_similar_business_list() {
        similarRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        Businesse.Repository repository = GuideApplication.getLoopBackAdapter().createRepository(Businesse.Repository.class);

        repository.findAll(new ListCallback<Businesse>() {
            @Override
            public void onSuccess(List<Businesse> items) {
                for (int i = 0; i < 10; i++) {
                    Businesse b = new Businesse();
                    b.setName("business " + i);
                    b.setDescription("description " + i);
                    items.add(b);
                }

                //setup top businesses view
                BusinessHorizontalListAdapter topadapter = new BusinessHorizontalListAdapter(getActivity(), items);
                similarRecyclerview.setAdapter(topadapter);
                similarRecyclerview.setNestedScrollingEnabled(false);
            }

            @Override
            public void onError(Throwable t) {
            }
        });
    }

    @OnClick(R.id.btncomments)
    public void btnComment() {
        switchFragment(new fragment_comment(), true);
    }

    @OnClick(R.id.btnSendComment)
    public void btnSendComment() {
        View content = LayoutInflater.from(getActivity()).inflate(R.layout.view_business_question, null);
        final EditText editText = (EditText) content.findViewById(R.id.editText);
        editText.setHint("نظر خود را بنویسید");

        util.contentDialog(getActivity(), content, "نظر شما درباره این کسب و کار", "ثبت نظر", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Comment.Repository repository = GuideApplication.getLoopBackAdapter().createRepository(Comment.Repository.class);

                Map<String, Object> param = new HashMap<String, Object>();
                param.put("customerId", config.customer.getId());
                param.put("text", editText.getText().toString());
                param.put("businessId", businesse.getId());

                Comment comment = repository.createObject(param);
                comment.save(new VoidCallback() {
                    @Override
                    public void onSuccess() {
                        util.alertDialog(getActivity(), "بستن", "", "ارسال شد", null, SweetAlertDialog.SUCCESS_TYPE);
                    }

                    @Override
                    public void onError(Throwable t) {
                        util.alertDialog(getActivity(), "بستن", "خطا در ارسال نظر", "خطا", new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                getactivity().onBackPressed();
                            }
                        }, SweetAlertDialog.ERROR_TYPE);
                    }
                });
            }
        });
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_business, menu);

        MenuItem quetion = menu.findItem(R.id.action_question);
        quetion.getActionView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View content = LayoutInflater.from(getActivity()).inflate(R.layout.view_business_question, null);
                EditText editText = (EditText) content.findViewById(R.id.editText);
                editText.setHint("پرسش خود را بنویسید");

                util.contentDialog(getActivity(), content, "پرسش از کسب و کار", "ارسال", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
            }
        });

        boolean tooltipshown = config.getValueAsBool(getActivity(), "businessQuestionShowCase");
        if (!tooltipshown) {
            config.setValue(getActivity(), "businessQuestionShowCase", true);

            TapTargetView.showFor(getactivity(),
                    TapTarget.forView(quetion.getActionView(), "پرسش از کسب و کار", "برای طرح پرسش از اینجا اقدام کنید")
                            // All options below are optional
                            .textTypeface(widgettools.typeface(getActivity(), 4))
                            .outerCircleColor(R.color.white_gray_color)
                            .targetCircleColor(R.color.colorAccent)
                            .textColor(android.R.color.black)
                            .drawShadow(true)
                            .cancelable(true)
                            .tintTarget(true),
                    new TapTargetView.Listener() {
                        @Override
                        public void onTargetClick(TapTargetView view) {
                            super.onTargetClick(view);      // This call is optional
                        }
                    });

//            util.showTooltip(getactivity(), (ViewGroup) getView(), quetion.getActionView(), "برای طرح پرسش از اینجا اقدام کنید");
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.getUiSettings().setMyLocationButtonEnabled(false);
        map.setMyLocationEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);

        // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
        MapsInitializer.initialize(this.getActivity());

        //Check if business location is available
        if (businesse.getLocation() != null) {
            // Updates the location and zoom of the MapView
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(businesse.getLocation().lat, businesse.getLocation().lng), 10);
            map.animateCamera(cameraUpdate);

            setup_marker_list();
        } else {
            //Hide Route feature from the map
            btnroute.setVisibility(View.GONE);
        }
    }


    @OnClick(R.id.btnroute)
    public void route() {

        //Temporary for show correct location and route feature
        //======================================================
        Location location = new Location();
        location.lat = 32.6440555f;
        location.lng = 51.6249668f;

        businesse.setLocation(location);
        //======================================================


        String uri = String.format(Locale.ENGLISH,
                "http://maps.google.com/maps?daddr=%f,%f",
                businesse.getLocation().lat,
                businesse.getLocation().lng,
                businesse.getName());

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");

        try {
            startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            try {
                Intent unrestrictedIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                startActivity(unrestrictedIntent);
            } catch (ActivityNotFoundException innerEx) {
                Toast.makeText(getActivity(), "لطفا ابتدا اپلیکیشن گوگل-مپ را نصب کنید", Toast.LENGTH_LONG).show();
            }
        }
    }
}

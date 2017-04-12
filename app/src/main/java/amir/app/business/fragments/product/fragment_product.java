package amir.app.business.fragments.product;

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

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.common.collect.ImmutableMap;
import com.strongloop.android.loopback.callbacks.ListCallback;
import com.strongloop.android.loopback.callbacks.ObjectCallback;
import com.strongloop.android.loopback.callbacks.VoidCallback;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import amir.app.business.GuideApplication;
import amir.app.business.R;
import amir.app.business.adapter.GalleryListAdapter;
import amir.app.business.adapter.ProductHorizontalListAdapter;
import amir.app.business.config;
import amir.app.business.fragments.baseFragment;
import amir.app.business.models.Businesse;
import amir.app.business.models.Comment;
import amir.app.business.models.Customer;
import amir.app.business.models.Followed;
import amir.app.business.models.Following;
import amir.app.business.models.Product;
import amir.app.business.models.StringCallback;
import amir.app.business.models.db.Basket;
import amir.app.business.util;
import amir.app.business.widget.CircleIndicator;
import amir.app.business.widget.FarsiTextView;
import amir.app.business.widget.RatingBarView;
import amir.app.business.widget.widgettools;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by amin on 08/09/2016.
 */

public class fragment_product extends baseFragment implements OnMapReadyCallback {
    @BindView(R.id.imagePager)
    ViewPager imagePager;
    @BindView(R.id.indicator)
    CircleIndicator indicator;
    @BindView(R.id.txtbusiness)
    FarsiTextView txtbusiness;
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
    @BindView(R.id.txtAmount)
    TextView txtAmount;
    @BindView(R.id.txtmorecomments)
    TextView txtmorecomments;
    @BindView(R.id.commentProgress)
    ProgressBar commentProgress;
    @BindView(R.id.btnSendComment)
    ImageView btnSendComment;
    @BindView(R.id.txtlastcomment)
    TextView txtlastcomment;
    @BindView(R.id.txtusername)
    TextView txtusername;
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
    @BindView(R.id.btnfollow)
    Button btnfollow;
    @BindView(R.id.followProgress)
    View followProgress;
    @BindView(R.id.commentLayout)
    View commentLayout;

    Product product;
    GoogleMap map;

    String followingId = "";
    String likeId = "";
    Comment customercomment;

    Product.Repository repository = GuideApplication.getLoopBackAdapter().createRepository(Product.Repository.class);

    public static fragment_product newInstance(Product product) {
        fragment_product fragment = new fragment_product();
        fragment.product = product;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product, null);

        ButterKnife.bind(this, view);

        setHasOptionsMenu(true);

        //config toolbar
        getactivity().setSupportActionBar(toolbar);
        getactivity().getSupportActionBar().setTitle(product.getName());
        getactivity().getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getactivity().getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //setup back button on toolbar
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getactivity().onBackPressed();
            }
        });

        txtname.setText(product.getName());
        txtdesc.setText(product.getDescription());
        btnfollow.setVisibility(config.customer == null ? View.GONE : View.VISIBLE);

        setup_map_view(savedInstanceState);

        load_business();

        checkfollowState();

        //load product list via api
        load_similar_product_list();

        //load likes count for this product
        load_likes_count();

        //load three lastest comment about this product
        load_latest_comments_list();

        load_product_images();

        load_remained();

        return view;
    }

    private void load_business() {
        Businesse.Repository repository = GuideApplication.getLoopBackAdapter().createRepository(Businesse.Repository.class);

        HashMap<String, String> filter = new HashMap<>();
        filter.put("userId", product.getOwner());

        repository.findOne(filter, new ObjectCallback<Businesse>() {
            @Override
            public void onSuccess(Businesse businesse) {
                txtbusiness.setText(businesse.getName());
            }

            @Override
            public void onError(Throwable t) {

            }
        });
    }

    private void load_product_images() {
        List<String> images = product.getImages();

        //template
        if (images == null || images.size() == 0) {
            images = new ArrayList<>();

            images.add("");
            images.add("");
            images.add("");
        }
        //template

        imagePager.setAdapter(new GalleryListAdapter(getactivity(), images, null));
        indicator.setViewPager(imagePager);

        Display display = getactivity().getWindowManager().getDefaultDisplay();
        imagePager.getLayoutParams().height = display.getWidth();
    }

    //Create Markers from product Info on the map.
//    private void setup_marker_list() {
//        LatLngBounds.Builder builder = new LatLngBounds.Builder();
//
//        if (product.getLocation() != null) {
//            Marker marker = map.addMarker(new MarkerOptions()
//                    .position(new LatLng(product.getLocation().lat, product.getLocation().lng))
//                    .title(product.getName())
//                    .snippet(product.getDescription()));
//
//            CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(new LatLng(product.getLocation().lat, product.getLocation().lng), 10);
//            try {
//                map.moveCamera(cu);
//
//            } catch (Exception ignored) {
//            }
//        }
//
//    }

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
        repository.getByProductId(product.getId().toString(), new ListCallback<Comment>() {
            @Override
            public void onSuccess(List<Comment> comments) {
                //calculate average of comments rate
                int rate = 0;
                for (Comment comment : comments) {
                    if (config.customer != null && comment.getCustomerId().equals(config.customer.getId()))
                        customercomment = comment;

                    rate += comment.getRate();
                }
                rate = rate / comments.size();
                ratingbar.setRating(rate);

                commentProgress.setVisibility(View.GONE);
                if (comments.size() > 1)
                    txtmorecomments.setText(String.format(Locale.ENGLISH, "همه %d نظر را ببینید", comments.size()));
                else
                    txtmorecomments.setVisibility(View.GONE);

                if (comments.size() > 0) {
                    Comment comment = comments.get(comments.size() - 1);
                    Customer.Repository customer = GuideApplication.getLoopBackAdapter().createRepository(Customer.Repository.class);
                    customer.findById(comment.getCustomerId(), new ObjectCallback<Customer>() {
                        @Override
                        public void onSuccess(Customer customer) {
                            txtusername.setText(customer.getRealm());
                        }

                        @Override
                        public void onError(Throwable t) {

                        }
                    });

                    txtlastcomment.setText(comment.getText());
                    lastRatingBar.setRating(comment.getRate());
                } else
                    commentLayout.setVisibility(View.GONE);
            }

            @Override
            public void onError(Throwable t) {
                commentProgress.setVisibility(View.GONE);
                commentLayout.setVisibility(View.GONE);
            }
        });

    }

    @OnClick(R.id.btnSendComment)
    public void btnSendComment() {
        if (config.customer == null) {
            util.alertDialog(getActivity(), "بستن", "", "برای ثبت نظر باید وارد شوید", null, SweetAlertDialog.WARNING_TYPE);
            return;
        }

        View content = LayoutInflater.from(getActivity()).inflate(R.layout.view_product_comment, null);
        final EditText editText = (EditText) content.findViewById(R.id.editText);
        final RatingBarView ratingview = (RatingBarView) content.findViewById(R.id.rate);
        editText.setHint("نظر خود را بنویسید");

        //نمایش کامنت کاربر که برای محصول وارد کرده است
        if (customercomment != null) {
            editText.setText(customercomment.getText());
        }

        util.contentDialog(getActivity(), content, "نظر شما درباره محصول", "ثبت نظر", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Comment.Repository repository = GuideApplication.getLoopBackAdapter().createRepository(Comment.Repository.class);

                String text = editText.getText().toString();
                if (text.equals(""))
                    text = " ";

                Comment comment = repository.createObject(ImmutableMap.of("customerId", config.customer.getId()));
                comment.setText(text);
                comment.setProductId(product.getId().toString());
                comment.setBusinessId("0");
                comment.setRate(ratingview.getRatevalue());

                //در صورتی که یوزر قبلا کامنت درج کرده، ابتدا کامنت پاک شود
                if (customercomment != null)
                    customercomment.destroy(new VoidCallback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Throwable t) {

                        }
                    });

                //ذخیره کامنت جدید کاربر
                comment.save(new VoidCallback() {
                    @Override
                    public void onSuccess() {
                        load_latest_comments_list();
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

    private void load_likes_count() {
        Followed.Repository repository = GuideApplication.getLoopBackAdapter().createRepository(Followed.Repository.class);

        repository.findAll(new ListCallback<Followed>() {
            @Override
            public void onSuccess(List<Followed> likes) {
                int likeDrawableId = R.drawable.ic_favorite_grey_500_18dp;
                int likecount = 0;
                for (Followed like : likes) {
                    if (like.getFollowedid().contains(product.getId().toString()))
                        likecount++;

                    if (config.customer != null && like.getUserid().equals(config.customer.getId()) && like.getFollowedid().contains(product.getId().toString())) {
                        likeId = like.getId();
                        likeDrawableId = R.drawable.ic_favorite_red_a700_18dp;
                        break;
                    }
                }

                btnlike.setCompoundDrawablesWithIntrinsicBounds(likeDrawableId, 0, 0, 0);
                btnlike.setText(String.format(Locale.ENGLISH, "%d", likecount));
            }

            @Override
            public void onError(Throwable t) {

            }
        });
    }

    @OnClick(R.id.btnlike)
    public void Like() {
        if (config.customer == null) {
            util.alertDialog(getActivity(), "بستن", "", "برای لایک محصول، ابتدا باید وارد شوید", null, SweetAlertDialog.WARNING_TYPE);
            return;
        }

        Followed.Repository repository = GuideApplication.getLoopBackAdapter().createRepository(Followed.Repository.class);

        //add new follow
        if (likeId.equals("")) {
            Followed newfollowed = repository.createObject(ImmutableMap.of("userid", config.customer.getId()));

            List<String> ids = new ArrayList<>();
            ids.add(product.getId().toString());
            newfollowed.setFollowedid(ids);

            newfollowed.save(new VoidCallback() {
                @Override
                public void onSuccess() {
                    likeId = "";
                    load_likes_count();
                }

                @Override
                public void onError(Throwable t) {

                }
            });
        } else {
            repository.findById(likeId, new ObjectCallback<Followed>() {
                @Override
                public void onSuccess(Followed object) {
                    object.destroy(new VoidCallback() {
                        @Override
                        public void onSuccess() {
                            likeId = "";
                            load_likes_count();
                        }

                        @Override
                        public void onError(Throwable t) {

                        }
                    });
                }

                @Override
                public void onError(Throwable t) {

                }
            });
        }
    }

    private void checkfollowState() {
        if (config.customer == null) {
            btnfollow.setVisibility(View.GONE);
            return;
        }

        Following.Repository repository = GuideApplication.getLoopBackAdapter().createRepository(Following.Repository.class);

        HashMap<String, String> filter = new HashMap<>();
        filter.put("followingid", product.getOwner());

        followProgress.setVisibility(View.VISIBLE);
        btnfollow.setVisibility(View.INVISIBLE);

        repository.findOne(filter, new ObjectCallback<Following>() {
            @Override
            public void onSuccess(Following object) {
                followingId = object.getId();
                btnfollow.setText("دنبال میکنم");

                followProgress.setVisibility(View.GONE);
                btnfollow.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError(Throwable t) {
                followingId = "";
                btnfollow.setText("دنبال کنید");

                followProgress.setVisibility(View.GONE);
                btnfollow.setVisibility(View.VISIBLE);
            }
        });


    }

    @OnClick(R.id.btnfollow)
    public void follow() {
        Following.Repository repository = GuideApplication.getLoopBackAdapter().createRepository(Following.Repository.class);

        followProgress.setVisibility(View.VISIBLE);
        btnfollow.setVisibility(View.INVISIBLE);

        //add new follow
        if (followingId.equals("")) {
            Following newfollow = repository.createObject(ImmutableMap.of("userid", config.customer.getId()));

            List<String> ids = new ArrayList<>();
            ids.add(product.getOwner());
            newfollow.setFollowingid(ids);

            newfollow.save(new VoidCallback() {
                @Override
                public void onSuccess() {
                    checkfollowState();
                }

                @Override
                public void onError(Throwable t) {

                }
            });
        } else {
            repository.findById(followingId, new ObjectCallback<Following>() {
                @Override
                public void onSuccess(Following object) {
                    object.destroy(new VoidCallback() {
                        @Override
                        public void onSuccess() {
                            followingId = "";
                            checkfollowState();
                        }

                        @Override
                        public void onError(Throwable t) {

                        }
                    });
                }

                @Override
                public void onError(Throwable t) {

                }
            });
        }
    }


    //load similar products with product category
    private void load_similar_product_list() {
        similarRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        repository.productByCategory(0, product.getCategory(), new ListCallback<Product>() {
            @Override
            public void onSuccess(List<Product> items) {
                //setup top product view
                int foundindex = -1;
                for (int i = 0; i < items.size(); i++) {
                    if (items.get(i).getId().equals(product.getId()))
                        foundindex = i;
                }

                if (foundindex > -1)
                    items.remove(foundindex);

                ProductHorizontalListAdapter adapter = new ProductHorizontalListAdapter(getActivity(), items);
                adapter.setOnItemClickListener(new ProductHorizontalListAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(Product product) {
                        switchFragment(new fragment_product().newInstance(product), true);
                    }
                });
                similarRecyclerview.setAdapter(adapter);
                similarRecyclerview.setNestedScrollingEnabled(false);
            }

            @Override
            public void onError(Throwable t) {
            }
        });
    }

    //load remained of products
    private void load_remained() {

        repository.getRemained(product.getId().toString(), new StringCallback() {
            @Override
            public void onSuccess(String amount) {
                txtAmount.setText(String.format(Locale.ENGLISH, "موجودی: %s عدد", amount));
            }

            @Override
            public void onError(Throwable t) {

            }
        });
    }

    @OnClick(R.id.txtmorecomments)
    public void moreComment() {
        switchFragment(new fragment_comment().newInstance(product), true);
    }


    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_product, menu);

        MenuItem quetion = menu.findItem(R.id.action_question);
        quetion.getActionView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View content = LayoutInflater.from(getActivity()).inflate(R.layout.view_product_question, null);
                EditText editText = (EditText) content.findViewById(R.id.editText);
                editText.setHint("پرسش خود را بنویسید");

                util.contentDialog(getActivity(), content, "پرسش از محصول", "ارسال", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
            }
        });

        boolean tooltipshown = config.getValueAsBool(getActivity(), "productQuestionShowCase");
        if (!tooltipshown) {
            config.setValue(getActivity(), "productQuestionShowCase", true);

            TapTargetView.showFor(getactivity(),
                    TapTarget.forView(quetion.getActionView(), "پرسش از محصول", "برای طرح پرسش از اینجا اقدام کنید")
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
        switch (item.getItemId()) {
            case R.id.action_basket:
                add_to_basket();
        }
        return super.onOptionsItemSelected(item);
    }

    private void add_to_basket() {
        View content = LayoutInflater.from(getActivity()).inflate(R.layout.view_product_count, null);
        final EditText editText = (EditText) content.findViewById(R.id.editText);
        editText.setText("1");
        editText.setHint("تعداد خرید از محصول");

        util.contentDialog(getActivity(), content, "تعداد خرید از محصول", "تایید", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editText.getText().toString().isEmpty()) {
                    util.alertDialog(getactivity(), "تعداد خرید محصول مشخص نشد.", SweetAlertDialog.WARNING_TYPE);
                    return;
                }

                Basket basket = new Basket();
                basket.name = product.getName();
                basket.image = product.getImages().size() > 0 ? product.getImages().get(0) : "";
                basket.productid = product.getId().toString();
                basket.count = Integer.parseInt(editText.getText().toString());
                basket.comment = "";
                basket.price = product.getPrice();
                basket.date = Calendar.getInstance().getTimeInMillis();
                basket.save();

                util.alertDialog(getactivity(), "بستن", "محصول به سبد خرید اضافه گردید", SweetAlertDialog.SUCCESS_TYPE);
            }
        });
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
//        if (product.getLocation() != null) {
//            // Updates the location and zoom of the MapView
//            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(product.getLocation().lat, product.getLocation().lng), 10);
//            map.animateCamera(cameraUpdate);
//
//            setup_marker_list();
//        } else {
//            //Hide Route feature from the map
//            btnroute.setVisibility(View.GONE);
//        }
    }


//    @OnClick(R.id.btnroute)
//    public void route() {
//
//        //Temporary for show correct location and route feature
//        //======================================================
//        Location location = new Location();
//        location.lat = 32.6440555f;
//        location.lng = 51.6249668f;
//
//        product.setLocation(location);
//        //======================================================
//
//
//        String uri = String.format(Locale.ENGLISH,
//                "http://maps.google.com/maps?daddr=%f,%f",
//                product.getLocation().lat,
//                product.getLocation().lng,
//                product.getName());
//
//        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
//        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
//
//        try {
//            startActivity(intent);
//        } catch (ActivityNotFoundException ex) {
//            try {
//                Intent unrestrictedIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
//                startActivity(unrestrictedIntent);
//            } catch (ActivityNotFoundException innerEx) {
//                Toast.makeText(getActivity(), "لطفا ابتدا اپلیکیشن گوگل-مپ را نصب کنید", Toast.LENGTH_LONG).show();
//            }
//        }
//    }
}

package amir.app.business.management.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
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
import java.util.zip.Inflater;

import amir.app.business.GuideApplication;
import amir.app.business.R;
import amir.app.business.adapter.GalleryListAdapter;
import amir.app.business.adapter.ProductHorizontalListAdapter;
import amir.app.business.config;
import amir.app.business.fragments.baseFragment;
import amir.app.business.fragments.product.fragment_comment;
import amir.app.business.models.Comment;
import amir.app.business.models.Inventory;
import amir.app.business.models.Product;
import amir.app.business.models.StringCallback;
import amir.app.business.models.db.Basket;
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

public class ProductActivity extends AppCompatActivity implements OnMapReadyCallback {
    @BindView(R.id.imagePager)
    ViewPager imagePager;
    @BindView(R.id.indicator)
    CircleIndicator indicator;
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
    View txtmorecomments;
    @BindView(R.id.commentProgress)
    ProgressBar commentProgress;
    //    @BindView(R.id.commentInnerLayout)
//    View commentInnerLayout;
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
//    @BindView(R.id.commentCard)
//    CardView commentCard;

    Product product;
    String productid;
    GoogleMap map;
    Product.Repository repository = GuideApplication.getLoopBackAdapter().createRepository(Product.Repository.class);

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_product);

        ButterKnife.bind(this);

        product = (Product) getIntent().getExtras().getSerializable("product");
        productid = getIntent().getExtras().getString("productid");

        //config toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(product.getName());
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //setup back button on toolbar
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        txtname.setText(product.getName());
        txtdesc.setText(product.getDescription());

        setup_map_view(savedInstanceState);

        //load product list via api
        load_similar_product_list();

        //load three lastest comment about this product
        load_latest_comments_list();

        load_product_images();

        load_remained();
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

        imagePager.setAdapter(new GalleryListAdapter(this, images, null));
        indicator.setViewPager(imagePager);

        Display display = getWindowManager().getDefaultDisplay();
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
        repository.getByProductId(productid, new ListCallback<Comment>() {
            @Override
            public void onSuccess(List<Comment> comments) {
                commentProgress.setVisibility(View.GONE);
//                commentInnerLayout.setVisibility(View.VISIBLE);

                if (comments.size() > 0)
                    txtlastcomment.setText(comments.get(0).getText());
            }

            @Override
            public void onError(Throwable t) {
                commentProgress.setVisibility(View.GONE);
//                commentCard.setVisibility(View.GONE);
            }
        });

    }

    private void load_similar_product_list() {
        similarRecyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        repository.findAll(new ListCallback<Product>() {
            @Override
            public void onSuccess(List<Product> items) {
                for (int i = 0; i < 10; i++) {
                    Product b = new Product();
                    b.setName("product " + i);
                    b.setDescription("description " + i);
                    items.add(b);
                }

                //setup top product view
                ProductHorizontalListAdapter topadapter = new ProductHorizontalListAdapter(ProductActivity.this, items);
                similarRecyclerview.setAdapter(topadapter);
                similarRecyclerview.setNestedScrollingEnabled(false);
            }

            @Override
            public void onError(Throwable t) {
            }
        });
    }

    //load remained of products
    private void load_remained() {

        repository.getRemained(productid, new StringCallback() {
            @Override
            public void onSuccess(String amount) {
                txtAmount.setText(String.format(Locale.ENGLISH, "موجودی: %s عدد", amount));
            }

            @Override
            public void onError(Throwable t) {

            }
        });
    }

//    @OnClick(R.id.txtmorecomments)
//    public void morecomments() {
//        switchFragment(new fragment_comment().newInstance(product), true);
//    }

    @OnClick(R.id.btnSendComment)
    public void btnSendComment() {
        if (config.customer == null) {
            util.alertDialog(this, "بستن", "", "برای ثبت نظر باید وارد شوید", null, SweetAlertDialog.WARNING_TYPE);
            return;
        }

        View content = LayoutInflater.from(this).inflate(R.layout.view_product_question, null);
        final EditText editText = (EditText) content.findViewById(R.id.editText);
        editText.setHint("نظر خود را بنویسید");

        util.contentDialog(this, content, "نظر شما درباره محصول", "ثبت نظر", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Comment.Repository repository = GuideApplication.getLoopBackAdapter().createRepository(Comment.Repository.class);

                Map<String, Object> param = new HashMap<String, Object>();
                param.put("customerId", config.customer.getId());
                param.put("text", editText.getText().toString());
                param.put("productId", productid);

                Comment comment = repository.createObject(param);
                comment.save(new VoidCallback() {
                    @Override
                    public void onSuccess() {
                        util.alertDialog(ProductActivity.this, "بستن", "", "ارسال شد", null, SweetAlertDialog.SUCCESS_TYPE);
                    }

                    @Override
                    public void onError(Throwable t) {
                        util.alertDialog(ProductActivity.this, "بستن", "خطا در ارسال نظر", "خطا", new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                onBackPressed();
                            }
                        }, SweetAlertDialog.ERROR_TYPE);
                    }
                });
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_product_edit, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_inventory:
                add_to_product();
                break;

            case R.id.action_edit:
                edit_product();
                break;

            case R.id.action_delete:
                delete_product();
        }
        return super.onOptionsItemSelected(item);
    }

    private void add_to_product() {
        View content = LayoutInflater.from(this).inflate(R.layout.view_product_count, null);
        final EditText editText = (EditText) content.findViewById(R.id.editText);
        editText.setText("1");
        editText.setHint("افزایش/کاهش تعداد محصول");

        util.contentDialog(this, content, "تعداد محصول", "تایید", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editText.getText().toString().isEmpty()) {
                    util.alertDialog(ProductActivity.this, "تعداد محصول مشخص نشد.", SweetAlertDialog.WARNING_TYPE);
                    return;
                }

                if (Integer.parseInt(editText.getText().toString()) == 0) {
                    util.alertDialog(ProductActivity.this, "حداقل تعداد محصول قابل قبول نیست.", SweetAlertDialog.WARNING_TYPE);
                    return;
                }

                Inventory.Repository repository = GuideApplication.getLoopBackAdapter().createRepository(Inventory.Repository.class);
                final Inventory inventory = repository.createObject(ImmutableMap.of("productId", productid));
                inventory.setAmount(Integer.parseInt(editText.getText().toString()));
                inventory.setDate("2017-02-21");
                inventory.setUserId(config.customer.getId());
                inventory.save(new VoidCallback() {
                    @Override
                    public void onSuccess() {
                        load_remained();
                        util.alertDialog(ProductActivity.this, "بستن", "تعداد محصول اعمال شد.", SweetAlertDialog.SUCCESS_TYPE);
                    }

                    @Override
                    public void onError(Throwable t) {
                        util.alertDialog(ProductActivity.this, "بستن", "خطا در تعیین مقدار محصول", SweetAlertDialog.ERROR_TYPE);
                    }
                });

            }
        });
    }

    private void delete_product() {
        util.confirmDialog(this, "تایید", "انصراف", "حذف محصول", "از حذف محصول مطمئن هستید؟", new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                Product.Repository repository = GuideApplication.getLoopBackAdapter().createRepository(Product.Repository.class);
                product.setRepository(repository);

                repository.getById(productid, new ObjectCallback<Product>() {
                    @Override
                    public void onSuccess(Product object) {
                        object.destroy(new VoidCallback() {
                            @Override
                            public void onSuccess() {
                                util.alertDialog(ProductActivity.this, "بستن", "محصول با موفقیت حذف شد.", "نتیجه", new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        onBackPressed();
                                    }
                                }, SweetAlertDialog.SUCCESS_TYPE);

                                setResult(RESULT_OK);
                                finish();
                            }

                            @Override
                            public void onError(Throwable t) {
                                util.alertDialog(ProductActivity.this, "پاسخی از سرور دریافت نشد.", SweetAlertDialog.ERROR_TYPE);
                            }
                        });
                    }

                    @Override
                    public void onError(Throwable t) {
                        util.alertDialog(ProductActivity.this, "پاسخی از سرور دریافت نشد.", SweetAlertDialog.ERROR_TYPE);
                    }
                });
            }
        }, SweetAlertDialog.WARNING_TYPE);
    }

    private void edit_product() {
        Intent intent = new Intent(this, ProductEdit.class);
        intent.putExtra("product", product);
        intent.putExtra("productid", productid);

        startActivity(intent);
    }

    private void add_to_basket() {
        View content = LayoutInflater.from(this).inflate(R.layout.view_product_count, null);
        final EditText editText = (EditText) content.findViewById(R.id.editText);
        editText.setText("1");
        editText.setHint("تعداد خرید از محصول");

        util.contentDialog(this, content, "تعداد خرید از محصول", "تایید", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editText.getText().toString().isEmpty()) {
                    util.alertDialog(ProductActivity.this, "تعداد خرید محصول مشخص نشد.", SweetAlertDialog.WARNING_TYPE);
                    return;
                }

                Basket basket = new Basket();
                basket.name = product.getName();
                basket.productid = productid;
                basket.count = Integer.parseInt(editText.getText().toString());
                basket.comment = "";
                basket.price = product.getPrice();
                basket.date = Calendar.getInstance().getTimeInMillis();
                basket.save();

                util.alertDialog(ProductActivity.this, "بستن", "محصول به سبد خرید اضافه گردید", SweetAlertDialog.SUCCESS_TYPE);
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
        MapsInitializer.initialize(this);

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


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAfterTransition();
        }
    }
}

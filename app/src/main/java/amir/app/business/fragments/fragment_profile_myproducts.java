package amir.app.business.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.common.collect.ImmutableMap;
import com.strongloop.android.loopback.callbacks.ListCallback;
import com.strongloop.android.loopback.callbacks.ObjectCallback;
import com.strongloop.android.loopback.callbacks.VoidCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import amir.app.business.EndlessRecyclerOnScrollListener;
import amir.app.business.GuideApplication;
import amir.app.business.R;
import amir.app.business.config;
import amir.app.business.event.ProfileRefreshEvent;
import amir.app.business.management.activity.ProductActivity;
import amir.app.business.management.activity.ProductDefine;
import amir.app.business.management.adapter.ProductGridListAdapter;
import amir.app.business.models.Inventory;
import amir.app.business.models.Product;
import amir.app.business.models.QrCode;
import amir.app.business.models.StringCallback;
import amir.app.business.models.db.Basket;
import amir.app.business.util;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by amin on 11/02/2016.
 */

public class fragment_profile_myproducts extends baseFragment {
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.progress)
    ProgressBar progress;
    @BindView(R.id.txtempty)
    TextView txtempty;
    @BindView(R.id.floatAdd)
    FloatingActionButton floatAdd;

    ProductGridListAdapter adapter;
    List<Product> products;

    Product.Repository repository = GuideApplication.getLoopBackAdapter().createRepository(Product.Repository.class);
    QrCode.Repository qrcoderepository = GuideApplication.getLoopBackAdapter().createRepository(QrCode.Repository.class);

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void RefreshTabList(ProfileRefreshEvent event) {
        load_product_list(0);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_myproducts, null);
        ButterKnife.bind(this, view);

        init_recycler_view();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        //load product list via api
        load_product_list(0);
    }

    private void setup_adapter_and_views() {
        //setup main products view
        adapter = new ProductGridListAdapter(getActivity(), products);
        adapter.setOnItemClickListener(new ProductGridListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Product product, View view) {
                Intent intent = new Intent(getActivity(), ProductActivity.class);
                intent.putExtra("productid", product.getId().toString());
                intent.putExtra("product", product);
//                startActivity(intent);

                Pair<View, String> pair1 = Pair.create(view.findViewById(R.id.imgproduct), getString(R.string.fragment_image_trans));
//                Pair<View, String> pair2 = Pair.create(view.findViewById(R.id.txtname), getString(R.string.fragment_name_trans));

                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), pair1);
                //Start the Intent
                ActivityCompat.startActivityForResult(getActivity(), intent, 2, options.toBundle());

            }
        });

        recyclerview.setAdapter(adapter);
        recyclerview.setNestedScrollingEnabled(true);
        progress.setVisibility(View.GONE);
        txtempty.setVisibility(products.size() > 0 ? View.GONE : View.VISIBLE);
    }

    private void init_recycler_view() {
        recyclerview.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        recyclerview.setOnScrollListener(new EndlessRecyclerOnScrollListener((LinearLayoutManager) recyclerview.getLayoutManager()) {
            @Override
            public void onLoadMore(int current_page) {
                load_product_list(current_page - 1);
            }
        });

    }

    //read all product and fill list
    private void load_product_list(int page) {
        if (config.customer == null) {
            progress.setVisibility(View.GONE);
            txtempty.setVisibility(View.VISIBLE);
            floatAdd.setVisibility(View.GONE);
            return;
        }

        if (products == null || page == 0) {
            products = new ArrayList<>();
        }

        repository.getByOwner(page, config.customer.getId(), new ListCallback<Product>() {
            @Override
            public void onSuccess(List<Product> items) {
                //add new product in page to products collection
                products.addAll(items);

                if (products.size() > 0)
                    floatAdd.setVisibility(View.VISIBLE);

                setup_adapter_and_views();
            }

            @Override
            public void onError(Throwable t) {

            }
        });
    }

    @OnClick(R.id.floatAdd)
    public void floatAdd() {


//        getactivity().startActivityForResult(new Intent(getActivity(), BarCodeScannerActivity.class), 1);
    }

    private void add_to_product(final String productid) {
        View content = LayoutInflater.from(getActivity()).inflate(R.layout.view_product_count, null);
        final EditText editText = (EditText) content.findViewById(R.id.editText);
        editText.setText("1");
        editText.setHint("افزایش/کاهش تعداد محصول");

        util.contentDialog(getActivity(), content, "تعداد محصول", "تایید", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editText.getText().toString().isEmpty()) {
                    util.alertDialog(getactivity(), "تعداد محصول مشخص نشد.", SweetAlertDialog.WARNING_TYPE);
                    return;
                }

                if (Integer.parseInt(editText.getText().toString())==0) {
                    util.alertDialog(getactivity(), "حداقل تعداد محصول قابل قبول نیست.", SweetAlertDialog.WARNING_TYPE);
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
                        util.alertDialog(getactivity(), "بستن", "تعداد محصول اعمال شد.", SweetAlertDialog.SUCCESS_TYPE);
                    }

                    @Override
                    public void onError(Throwable t) {
                        util.alertDialog(getactivity(), "بستن", "خطا در تعیین مقدار محصول", SweetAlertDialog.ERROR_TYPE);
                    }
                });

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == 1) {
            final String qrcode = data.getExtras().getString("content");
            final MaterialDialog progress = util.progressDialog(getActivity(), "جستجوی محصول", "منتظر باشید...");

            qrcoderepository.findById(qrcode, new ObjectCallback<QrCode>() {
                @Override
                public void onSuccess(final QrCode qrCode) {
                    progress.dismiss();

                    util.confirmDialog(getActivity(), "تایید", "بستن", "اضافه کردن مدل", "محصولی با این کد موجود است. به این مدل اضافه میکنید؟", new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            add_to_product(qrCode.getProductId());
                        }
                    }, SweetAlertDialog.WARNING_TYPE);
                }

                @Override
                public void onError(Throwable t) {
                    progress.dismiss();
                    util.confirmDialog(getActivity(), "تایید", "بستن", "ثبت محصول جدید", "محصولی با کد شناسایی موردنظر موجود نمیباشد.\nآیا میخواهید محصول جدید ثبت کنید؟", new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            Intent intent = new Intent(getActivity(), ProductDefine.class);
                            intent.putExtra("qrcode", qrcode);
                            startActivity(intent);
                        }
                    }, SweetAlertDialog.WARNING_TYPE);
                }
            });

        } else if (resultCode == Activity.RESULT_OK && requestCode == 2) {
            products.clear();
            load_product_list(0);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}

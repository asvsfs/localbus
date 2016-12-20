package amir.app.business.management.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.afollestad.materialdialogs.MaterialDialog;
import com.strongloop.android.loopback.callbacks.ListCallback;
import com.strongloop.android.loopback.callbacks.ObjectCallback;

import java.util.List;

import amir.app.business.GuideApplication;
import amir.app.business.R;
import amir.app.business.management.adapter.ProductGridListAdapter;
import amir.app.business.models.Product;
import amir.app.business.util;
import amir.app.business.widget.FarsiEditText;
import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by amin on 12/05/2016.
 */

public class ProductManagerActivity extends AppCompatActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.progress)
    ProgressBar progress;
    @BindView(R.id.editname)
    FarsiEditText editname;

    ProductGridListAdapter adapter;
    List<Product> products;

    Product.Repository repository = GuideApplication.getLoopBackAdapter().createRepository(Product.Repository.class);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.management_products);

        ButterKnife.bind(this);

        //config toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("مدیریت محصولات");
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //setup back button on toolbar
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //load product list via api
        load_product_list();
    }

    //read all product and fill list
    private void load_product_list() {
        recyclerview.setLayoutManager(new GridLayoutManager(this, 3));

        if (products != null) {
            setup_adapter_and_views();
            return;
        }

        repository.findAll(new ListCallback<Product>() {
            @Override
            public void onSuccess(List<Product> items) {
                products = items;

                setup_adapter_and_views();
            }

            @Override
            public void onError(Throwable t) {

            }
        });
    }

    private void setup_adapter_and_views() {
        //setup main products view
        adapter = new ProductGridListAdapter(this, products);
        adapter.setOnItemClickListener(new ProductGridListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Product product) {
            }
        });

        recyclerview.setAdapter(adapter);
        recyclerview.setNestedScrollingEnabled(true);
        progress.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_product_manager, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_camera:
                startActivityForResult(new Intent(this, BarCodeScannerActivity.class), 1);
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if (resultCode == RESULT_OK && requestCode == 1) {
            String qrcode = data.getExtras().getString("content");
            final MaterialDialog progress = util.progressDialog(this, "جستجوی محصول", "منتظر باشید...");

            repository.getByQRCode(qrcode, new ObjectCallback<Product>() {
                @Override
                public void onSuccess(Product object) {
                    progress.dismiss();

                    util.confirmDialog(ProductManagerActivity.this, "تایید", "بستن", "اضافه کردن مدل", "محصولی با این کد موجود است. به این مدل اضافه میکنید؟", new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                        }
                    }, SweetAlertDialog.WARNING_TYPE);
                }

                @Override
                public void onError(Throwable t) {
                    progress.dismiss();

                    util.confirmDialog(ProductManagerActivity.this, "تایید", "بستن", "ثبت محصول جدید", "محصولی با کد شناسایی موردنظر موجود نمیباشد.\nآیا میخواهید محصول جدید ثبت کنید؟", new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            Intent intent = new Intent(ProductManagerActivity.this, ProductDefine.class);
                            intent.putExtra("qrcode", data.getExtras().getString("content"));
                            startActivity(intent);
                        }
                    }, SweetAlertDialog.WARNING_TYPE);
                }
            });
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}

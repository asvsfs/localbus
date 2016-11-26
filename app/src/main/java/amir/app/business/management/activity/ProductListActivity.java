package amir.app.business.management.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;

import com.strongloop.android.loopback.callbacks.ListCallback;

import java.util.List;

import amir.app.business.GuideApplication;
import amir.app.business.R;
import amir.app.business.management.adapter.ProductGridListAdapter;
import amir.app.business.models.Product;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by amin on 11/22/2016.
 */

public class ProductListActivity extends AppCompatActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.progress)
    ProgressBar progress;

    ProductGridListAdapter adapter;
    List<Product> products;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.management_products);
        ButterKnife.bind(this);

        //config toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.app_name);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        //setup back button on toolbar
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        load_product_list();
    }

    //setup recyclerview lists
    private void init_layout() {
        recyclerview.setLayoutManager(new GridLayoutManager(this, 2));
    }

    private void setup_adapter_and_views() {
        //setup init views
        init_layout();

        adapter = new ProductGridListAdapter(this, products);
        adapter.setOnItemClickListener(new ProductGridListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Product product) {
            }
        });

        recyclerview.setAdapter(adapter);
        recyclerview.setNestedScrollingEnabled(false);

        progress.setVisibility(View.GONE);
    }

    public void switch_to_product_page(Product product) {

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

                for (int i = 0; i < 10; i++) {
                    Product b = new Product();
                    b.setName("product " + i);
                    b.setDescription("description " + i);

                    products.add(b);
                }

                setup_adapter_and_views();

            }

            @Override
            public void onError(Throwable t) {

            }
        });
    }

}

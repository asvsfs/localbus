package amir.app.business.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import amir.app.business.R;
import amir.app.business.adapter.BasketListAdapter;
import amir.app.business.models.db.Basket;
import amir.app.business.util;
import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by amin on 12/04/2016.
 */

public class fragment_basket extends baseFragment {
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.progress)
    ProgressBar progress;
    @BindView(R.id.txtempty)
    TextView txtempty;

    List<Basket> basket;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_basket, null);
        ButterKnife.bind(this, view);

        //config toolbar
        getactivity().setSupportActionBar(toolbar);
        getactivity().getSupportActionBar().setTitle("سبد خرید");
        getactivity().getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getactivity().getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //setup back button on toolbar
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getactivity().onBackPressed();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        load_basket();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_basket, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_order:
                show_order_kind();
        }

        return super.onOptionsItemSelected(item);
    }

    private void show_order_kind() {
        if (basket.size() == 0) {
            util.alertDialog(getactivity(), "بستن", "سبد خرید خالی است", SweetAlertDialog.WARNING_TYPE);
            return;
        }

        List<String> items = new ArrayList<>();

        items.add("تحویل با پیک");
        items.add("دریافت در محل فروشگاه");
        util.listDialog(getactivity(), items, "انتخاب نوع تحویل کالا", new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void load_basket() {
        basket = Basket.getAll();

        //setup Basket view
        final BasketListAdapter adapter = new BasketListAdapter(getactivity(), basket);
        adapter.setOnItemListener(new BasketListAdapter.OnItemListener() {
            @Override
            public void onItemClick(Basket basket) {

            }

            @Override
            public void onDeleteClick(final Basket basket) {
                util.confirmDialog(getactivity(), "حذف", "انصراف", "حذف محصول", "محصول از سبد خرید حذف شود؟", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        basket.delete();
                        load_basket();
                    }
                }, SweetAlertDialog.WARNING_TYPE);
            }
        });

        recyclerview.setLayoutManager(new LinearLayoutManager(getactivity(), LinearLayoutManager.VERTICAL, false));
        recyclerview.setAdapter(adapter);
        recyclerview.setNestedScrollingEnabled(false);

        progress.setVisibility(View.GONE);
        txtempty.setVisibility(basket.size() == 0 ? View.VISIBLE : View.GONE);

    }
}

package amir.app.business.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.strongloop.android.loopback.callbacks.ListCallback;

import java.util.List;

import amir.app.business.GuideApplication;
import amir.app.business.R;
import amir.app.business.adapter.CategoryListAdapter;
import amir.app.business.models.Category;
import amir.app.business.util;
import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by amin on 08/20/2016.
 */

public class fragment_category extends baseFragment {
    @BindView(R.id.categoryresyclerview)
    RecyclerView categoryresyclerview;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.progress)
    ProgressBar progress;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, null);

        ButterKnife.bind(this, view);

        //config toolbar
        getactivity().setSupportActionBar(toolbar);
        getactivity().getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getactivity().getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //setup back button on toolbar
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getactivity().onBackPressed();
            }
        });

        //load category list via api
        load_category_list();

        return view;
    }

    private void load_category_list() {
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 3, LinearLayoutManager.VERTICAL, false);
        categoryresyclerview.setLayoutManager(layoutManager);

        Category.Repository repository = GuideApplication.getLoopBackAdapter().createRepository(Category.Repository.class);

        repository.findAll(new ListCallback<Category>() {
            @Override
            public void onSuccess(List<Category> items) {
                //setup top category view
                CategoryListAdapter catadapter = new CategoryListAdapter(getActivity(), items);
                catadapter.setOnItemClickListener(new CategoryListAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(Category category) {
                        switchFragment(new fragment_category_page().newInstance(category), true);
                    }
                });
                categoryresyclerview.setAdapter(catadapter);
                categoryresyclerview.setNestedScrollingEnabled(false);

                progress.setVisibility(View.GONE);
            }

            @Override
            public void onError(Throwable t) {
                progress.setVisibility(View.GONE);

                util.alertDialog(getActivity(), "بستن", "خطا در ارتباط با شبکه", "خطا", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        getactivity().onBackPressed();
                    }
                }, SweetAlertDialog.ERROR_TYPE);
            }
        });
    }
}

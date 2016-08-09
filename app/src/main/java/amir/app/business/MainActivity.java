package amir.app.business;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.strongloop.android.loopback.callbacks.ListCallback;

import java.util.List;

import amir.app.business.adapter.BusinessHorizontalListAdapter;
import amir.app.business.adapter.BusinessVerticalListAdapter;
import amir.app.business.models.Businesse;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    //    @BindView(R.id.adverPager)
//    ViewPager adverPager;
    @BindView(R.id.topRecyclerview)
    RecyclerView topRecyclerview;
    @BindView(R.id.bottombar)
    TabLayout bottombar;
    @BindView(R.id.businessRecyclerview)
    RecyclerView businessRecyclerview;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.app_name);

        init_bottombar();
        init_layout();

//        Businesse b = new Businesse();
//        b.name = "android";
//        b.description = "android description";
//        b.location = new Location();
//        b.location.lat = 52;
//        b.location.lng = 32;
//        b.id = "121212";
//        b.images = new ArrayList<>();

//        b.setRepository(repository);
//        b.save(new VoidCallback() {
//            @Override
//            public void onSuccess() {
//                Toast.makeText(MainActivity.this, "saves", Toast.LENGTH_LONG).show();
//            }
//
//            @Override
//            public void onError(Throwable t) {
//
//            }
//        });

//        RestHelper.getRestService().businesseFindAll().enqueue(new Callback<List<Businesse>>() {
//            @Override
//            public void onResponse(Call<List<Businesse>> call, Response<List<Businesse>> response) {
//                Toast.makeText(MainActivity.this, "count:" + response.body().size(), Toast.LENGTH_LONG).show();
//            }
//
//            @Override
//            public void onFailure(Call<List<Businesse>> call, Throwable t) {
//
//            }
//        });

//        repository.getById("57a4ed29713f951ee89f6815", new ObjectCallback<Businesse>() {
//            @Override
//            public void onSuccess(Businesse object) {
//            }
//
//            @Override
//            public void onError(Throwable t) {
//                Toast.makeText(MainActivity.this, "onError", Toast.LENGTH_LONG).show();
//            }
//        });

        load_business_list();
    }

    private void load_business_list() {
        Businesse.Repository repository = GuideApplication.getLoopBackAdapter().createRepository(Businesse.Repository.class);

        repository.findAll(new ListCallback<Businesse>() {
            @Override
            public void onSuccess(List<Businesse> businesses) {
                for (int i = 0; i < 10; i++) {
                    Businesse b = new Businesse();
                    b.setName("business " + i);
                    b.setDescription("description " + i);
                    businesses.add(b);
                }

                //setup top businesses view
                BusinessHorizontalListAdapter topadapter = new BusinessHorizontalListAdapter(MainActivity.this, businesses);
                topadapter.setOnItemClickListener(new BusinessHorizontalListAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(Businesse businesse) {
                        switch_to_business_page(businesse);
                    }
                });
                topRecyclerview.setAdapter(topadapter);
                topRecyclerview.setNestedScrollingEnabled(false);


                //setup main businesses view
                BusinessVerticalListAdapter mainadapter = new BusinessVerticalListAdapter(MainActivity.this, businesses);
                mainadapter.setOnItemClickListener(new BusinessVerticalListAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(Businesse businesse) {
                        switch_to_business_page(businesse);
                    }
                });
                businessRecyclerview.setAdapter(mainadapter);
                businessRecyclerview.setNestedScrollingEnabled(false);
            }

            @Override
            public void onError(Throwable t) {

            }
        });
    }

    private void switch_to_business_page(Businesse businesse) {
        Intent intent = new Intent(this, BusinessActivity.class);
        intent.putExtra("business", businesse);
        startActivity(intent);
    }

    private void init_layout() {
        topRecyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        businessRecyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }

    private void init_bottombar() {
        bottombar.setTabMode(TabLayout.MODE_FIXED);

        bottombar.addTab(bottombar.newTab().setCustomView(getTabView(R.drawable.ic_home_black_24dp)));
        bottombar.addTab(bottombar.newTab().setCustomView(getTabView(R.drawable.ic_list_black_24dp)));
        bottombar.addTab(bottombar.newTab().setCustomView(getTabView(R.drawable.ic_search_black_24dp)));
        bottombar.addTab(bottombar.newTab().setCustomView(getTabView(R.drawable.ic_notifications_black_24dp)));
        bottombar.addTab(bottombar.newTab().setCustomView(getTabView(R.drawable.ic_person_black_24dp)));
    }

    private View getTabView(int drawable) {
        ImageView tab = (ImageView) LayoutInflater.from(this).inflate(R.layout.bottombar_tabview_layout, null);
        tab.setImageResource(drawable);

//        tab.setOnTouchListener(tabTouchListener);

        return tab;
    }
}

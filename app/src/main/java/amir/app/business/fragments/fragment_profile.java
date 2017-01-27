package amir.app.business.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.strongloop.android.loopback.callbacks.ListCallback;
import com.strongloop.android.loopback.callbacks.ObjectCallback;

import java.util.List;

import amir.app.business.GuideApplication;
import amir.app.business.LoginActivity;
import amir.app.business.R;
import amir.app.business.RegisterActivity;
import amir.app.business.callbacks.SimpleCallback;
import amir.app.business.config;
import amir.app.business.management.activity.EventDefine;
import amir.app.business.management.activity.ProductManagerActivity;
import amir.app.business.management.adapter.EventListAdapter;
import amir.app.business.management.adapter.FollowingListAdapter;
import amir.app.business.models.Customer;
import amir.app.business.models.Event;
import amir.app.business.models.Following;
import amir.app.business.widget.CircleImageView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by amin on 11/02/2016.
 */

public class fragment_profile extends baseFragment {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.mainContent)
    View mainContent;
    @BindView(R.id.loginContent)
    View loginContent;
    @BindView(R.id.loginProgress)
    View loginProgress;

    @BindView(R.id.imgavatar)
    CircleImageView imgavatar;
    @BindView(R.id.btnlogin)
    Button btnlogin;
    @BindView(R.id.btnRegister)
    Button btnRegister;
    @BindView(R.id.editName)
    EditText editName;
    @BindView(R.id.editPhone)
    EditText editPhone;
    @BindView(R.id.editMail)
    EditText editMail;
    @BindView(R.id.btnReset)
    Button btnReset;
    @BindView(R.id.btnProductManage)
    Button btnProductManage;
    @BindView(R.id.profileInfo)
    LinearLayout profileInfo;
    @BindView(R.id.tablayout)
    TabLayout tablayout;

    Customer.Repository repository;
    @BindView(R.id.viewpager)
    ViewPager viewpager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, null);
        ButterKnife.bind(this, view);

        //config toolbar
        getactivity().setSupportActionBar(toolbar);
        getactivity().getSupportActionBar().setTitle("پروفایل کاربری");
        getactivity().getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getactivity().getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //setup back button on toolbar
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getactivity().onBackPressed();
            }
        });

        mainContent.setVisibility(View.GONE);
        btnlogin.setVisibility(View.GONE);
        viewpager.setVisibility(View.GONE);
        tablayout.setVisibility(View.GONE);

        repository = GuideApplication.getLoopBackAdapter().createRepository(Customer.Repository.class);

        repository.authCheck(new SimpleCallback() {
            @Override
            public void onSuccess(String response) {
                load_user_info();
            }

            @Override
            public void onError(Throwable t) {
                loginProgress.setVisibility(View.GONE);
                btnlogin.setVisibility(View.VISIBLE);

            }
        });


        return view;
    }

    private void load_tabs_fragments_list() {
        viewpager.setAdapter(new pagerAdapter(getactivity()));
        tablayout.setupWithViewPager(viewpager);

        viewpager.setVisibility(View.VISIBLE);
        tablayout.setVisibility(View.VISIBLE);

        viewpager.setCurrentItem(1);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            load_user_info();
        }
    }

    private void load_user_info() {
        repository.findById(config.token.userId, new ObjectCallback<Customer>() {
            @Override
            public void onSuccess(Customer customer) {
                config.customer = customer;

                loginProgress.setVisibility(View.GONE);

                btnlogin.setVisibility(config.token == null ? View.VISIBLE : View.GONE);
                btnRegister.setVisibility(config.token == null ? View.VISIBLE : View.GONE);
                profileInfo.setVisibility(config.token == null ? View.GONE : View.VISIBLE);
//                tablayout.setVisibility(config.token == null ? View.GONE : View.VISIBLE);

                if (config.customer != null) {
                    editMail.setText(config.customer.getEmail());
                    editName.setText(config.customer.getUsername());
                    editPhone.setText(config.customer.getTelegramNumber());
                }

                mainContent.setVisibility(View.VISIBLE);

                load_tabs_fragments_list();
            }

            @Override
            public void onError(Throwable t) {
                //need to login again
                loginProgress.setVisibility(View.GONE);
                btnlogin.setVisibility(View.VISIBLE);
            }
        });

    }

    @OnClick(R.id.btnlogin)
    public void login() {
        getactivity().startActivityForResult(new Intent(getActivity(), LoginActivity.class), 1);
    }

    @OnClick(R.id.btnRegister)
    public void register() {
        getactivity().startActivityForResult(new Intent(getActivity(), RegisterActivity.class), 1);
    }

    @OnClick(R.id.btnProductManage)
    public void productManage() {
        getactivity().startActivity(new Intent(getActivity(), ProductManagerActivity.class));
    }

    private class pagerAdapter extends PagerAdapter {
        private Context context;

        int[] layouts = new int[]{R.layout.fragment_profile_following, R.layout.fragment_profile_events};
        String[] titles = new String[]{"دنبال شده‌ها", "رویدادها"};

        public pagerAdapter(Context context) {
            this.context = context;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        @Override
        public Object instantiateItem(ViewGroup collection, int position) {
            LayoutInflater inflater = LayoutInflater.from(this.context);
            ViewGroup layout = (ViewGroup) inflater.inflate(layouts[position], collection, false);

            switch (position) {
                case 0: //following
                    load_following((RecyclerView) layout.findViewById(R.id.recyclerview));
                    break;

                case 1: //events
                    load_events((RecyclerView) layout.findViewById(R.id.recyclerview));
                    layout.findViewById(R.id.floatAdd).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(getactivity(), EventDefine.class));
                        }
                    });
                    break;
            }

            collection.addView(layout);
            return layout;
        }

        @Override
        public void destroyItem(ViewGroup collection, int position, Object view) {
            collection.removeView((View) view);
        }

        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }

    private void load_following(final RecyclerView recyclerview) {
        recyclerview.setLayoutManager(new LinearLayoutManager(getactivity(), LinearLayoutManager.VERTICAL, false));

        Following.Repository repository = GuideApplication.getLoopBackAdapter().createRepository(Following.Repository.class);
        repository.findAll(new ListCallback<Following>() {
            @Override
            public void onSuccess(List<Following> following) {
                recyclerview.setAdapter(new FollowingListAdapter(getactivity(), following));
            }

            @Override
            public void onError(Throwable t) {

            }
        });
    }

    private void load_events(final RecyclerView recyclerview) {
        recyclerview.setLayoutManager(new LinearLayoutManager(getactivity(), LinearLayoutManager.VERTICAL, false));

        Event.Repository repository = GuideApplication.getLoopBackAdapter().createRepository(Event.Repository.class);
        repository.findAll(new ListCallback<Event>() {
            @Override
            public void onSuccess(List<Event> events) {
                recyclerview.setAdapter(new EventListAdapter(getactivity(), events));
            }

            @Override
            public void onError(Throwable t) {

            }
        });

    }

}

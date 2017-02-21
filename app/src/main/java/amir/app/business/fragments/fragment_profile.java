package amir.app.business.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.strongloop.android.loopback.callbacks.ObjectCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import amir.app.business.GuideApplication;
import amir.app.business.R;
import amir.app.business.config;
import amir.app.business.event.ProfileRefreshEvent;
import amir.app.business.models.Verification;
import amir.app.business.util;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by amin on 11/02/2016.
 */

public class fragment_profile extends baseFragment {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tablayout)
    TabLayout tablayout;
    @BindView(R.id.viewpager)
    ViewPager viewpager;

    tabPagerAdapter tabAdapter;

    baseFragment fragments[] = new baseFragment[]{new fragment_profile_myfollowing(),
            new fragment_profile_myevent(),
            new fragment_profile_myproducts(),
            new fragment_profile_myinfo()};

    String[] titles = new String[]{"دنبال شده‌ها", "رویدادهای من", "محصولات من", "پروفایل من"};

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
        tabAdapter.notifyDataSetChanged();
        util.set_tab_font(getactivity(), tablayout);
//        checkVerificationId(config.customer.getVerificationId());
    }

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

        tabAdapter = new tabPagerAdapter(getactivity(), getChildFragmentManager());

        viewpager.setAdapter(tabAdapter);
        tablayout.setupWithViewPager(viewpager);

        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                fragments[position].refresh(getactivity());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        tablayout.getTabAt(tablayout.getTabCount() - 1).select();

        util.set_tab_font(getactivity(), tablayout);

        if (config.customer != null)
            checkVerificationId(config.customer.getVerificationId());

        return view;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        tabAdapter.notifyDataSetChanged();
        util.set_tab_font(getactivity(), tablayout);

//        if (resultCode == Activity.RESULT_OK) {
        ((baseFragment) tabAdapter.getItem(tablayout.getSelectedTabPosition())).onActivityResult(requestCode, resultCode, data);

//            util.set_tab_font(getactivity(), tablayout);
//        }
    }

    private void checkVerificationId(String verification) {
        Verification.Repository verif_repo = GuideApplication.getLoopBackAdapter().createRepository(Verification.Repository.class);

        verif_repo.findById(verification, new ObjectCallback<Verification>() {
            @Override
            public void onSuccess(Verification object) {
                tabAdapter.notifyDataSetChanged();
                util.set_tab_font(getactivity(), tablayout);
            }

            @Override
            public void onError(Throwable t) {

            }
        });
    }

    private class tabPagerAdapter extends FragmentStatePagerAdapter {
        private Context context;


        public tabPagerAdapter(Context context, FragmentManager fragmentManager) {
            super(fragmentManager);
            this.context = context;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return super.isViewFromObject(view, object);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        @Override
        public Fragment getItem(int position) {
            return fragments[position];
        }

        @Override
        public int getCount() {
            return titles.length;
        }

    }

}

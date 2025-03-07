package amir.app.business;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.fingerlinks.mobile.android.navigator.Navigator;

import amir.app.business.fragments.baseFragment;
import amir.app.business.fragments.fragment_basket;
import amir.app.business.fragments.fragment_category;
import amir.app.business.fragments.fragment_event;
import amir.app.business.fragments.fragment_notification;
import amir.app.business.fragments.fragment_profile;
import amir.app.business.fragments.fragment_search;
import amir.app.business.fragments.product.fragment_home;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "InstanceID";
    @BindView(R.id.bottombar)
    TabLayout bottombar;

    private baseFragment currentfragment;
    boolean doubleBackToExitPressedOnce = false;

    ImageLoaderConfiguration configuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

//        setSupportActionBar(toolbar);
//        toolbar.setTitle(R.string.app_name);

        //init procedure
        init_bottombar();

        //set default fragment to home page
        switchFragment(new fragment_home(), true);

        init_gps_location();

    }

    private void init_gps_location() {
        SingleShotLocationProvider.requestSingleUpdate(this,
                new SingleShotLocationProvider.LocationCallback() {
                    @Override
                    public void onNewLocationAvailable(SingleShotLocationProvider.GPSCoordinates location) {
                        config.lastlocation = location;
                    }
                });
    }


    //setup bottom icon tab
    private void init_bottombar() {
        bottombar.setTabMode(TabLayout.MODE_FIXED);

        bottombar.addTab(bottombar.newTab().setCustomView(getTabView(R.drawable.ic_home_white_24dp)));
        bottombar.addTab(bottombar.newTab().setCustomView(getTabView(R.drawable.ic_view_module_white_24dp)));
        bottombar.addTab(bottombar.newTab().setCustomView(getTabView(R.drawable.ic_search_white_24dp)));
        bottombar.addTab(bottombar.newTab().setCustomView(getTabView(R.drawable.ic_notifications_white_24dp)));
        bottombar.addTab(bottombar.newTab().setCustomView(getTabView(R.drawable.ic_person_outline_white_24dp)));
        bottombar.addTab(bottombar.newTab().setCustomView(getTabView(R.drawable.ic_shopping_cart_white_24dp)));

        bottombar.setOnTabSelectedListener(bottombarTabListener);

    }

    //create tab child of bottom icon
    private View getTabView(int drawable) {
        ImageView tab = (ImageView) LayoutInflater.from(this).inflate(R.layout.view_bottombar_tab_layout, null);
        tab.setImageResource(drawable);

        tab.setOnTouchListener(tabTouchListener);

        return tab;
    }

    View.OnTouchListener tabTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            return !view.isEnabled();
        }
    };

    TabLayout.OnTabSelectedListener bottombarTabListener = new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            chooseTab(tab.getPosition());
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {
        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {
            chooseTab(tab.getPosition());
        }
    };

    private void chooseTab(int position) {
        switch (position) {
            case 0: //home
                switchFragment(new fragment_home(), true);
                break;

            case 1: //category
                switchFragment(new fragment_category(), true);
                break;

            case 2: //search
                switchFragment(new fragment_search(), true);
                break;

            case 3: //event
                switchFragment(new fragment_event(), true);
                break;

            case 4: //profile
                switchFragment(new fragment_profile(), true);
                break;

            case 5: //basket
                switchFragment(new fragment_basket(), true);
                break;
        }
    }

    public void switchFragment(baseFragment fragment, boolean addtostack) {

        String tag = fragment.getClass().toString();

        if (Navigator.with(this).utils().canGoBackToSpecificPoint(tag, R.id.container, getSupportFragmentManager()))
            Navigator.with(this).utils().goBackToSpecificPoint(tag);
        else
            Navigator.with(this)
                    .build() //Enter in navigation mode
                    .goTo(fragment, R.id.container)
                    .tag(tag)
                    .addToBackStack() //add backstack
                    .replace() //CommitType
                    .commit(); //commit operation

        currentfragment = fragment;
    }

    public void switchFragmentWitTransaction(View element, baseFragment fragment, boolean addtostack) {
        String tag = fragment.getClass().toString();

        if (Navigator.with(this).utils().canGoBackToSpecificPoint(tag, R.id.container, getSupportFragmentManager()))
            Navigator.with(this).utils().goBackToSpecificPoint(tag);
        else {
            FragmentManager fragmentManager = getSupportFragmentManager();

            fragmentManager.beginTransaction()
                    .replace(R.id.container, fragment)
                    .addToBackStack(tag)
                    .addSharedElement(element, getString(R.string.fragment_image_trans))
                    .commit();

//            Navigator.with(this)
//                    .build() //Enter in navigation mode
//                    .goTo(fragment, R.id.container)
//                    .tag(tag)
////                    .animation(R.anim.fadein, R.anim.fadeout, R.anim.fadein, R.anim.fadeout)
//                    .addToBackStack() //add backstack
//                    .replace() //CommitType
//                    .commit(); //commit operation
        }
        currentfragment = fragment;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        currentfragment.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {

        FragmentManager fm = getSupportFragmentManager();
        int count = fm.getBackStackEntryCount();

        if (count == 0) {
            Navigator.with(MainActivity.this)
                    .utils()
                    .confirmExitWithMessage("برای خروج، دوباره کلید برگشت را بزنید");

        } else {
            if (Navigator.with(this).utils().canGoBack(getSupportFragmentManager())) {
                Navigator.with(this)
                        .utils()
                        .goToPreviousBackStack();

                currentfragment = (baseFragment) fm.getFragments().get(fm.getBackStackEntryCount() - 2);

                String tag = currentfragment.getTag().toString();
                if (tag.equals(fragment_home.class.toString()))
                    bottombar.setScrollPosition(0, 0, true);
                else if (tag.equals(fragment_category.class.toString()))
                    bottombar.setScrollPosition(1, 0, true);
                else if (tag.equals(fragment_search.class.toString()))
                    bottombar.setScrollPosition(2, 0, true);

            } else {
                Navigator.with(MainActivity.this)
                        .utils()
                        .confirmExitWithMessage("برای خروج، دوباره کلید برگشت را بزنید");

            }
        }
    }
}

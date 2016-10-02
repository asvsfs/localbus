package amir.app.business;

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
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.fingerlinks.mobile.android.navigator.Navigator;

import amir.app.business.fragments.baseFragment;
import amir.app.business.fragments.fragment_category;
import amir.app.business.fragments.fragment_home;
import amir.app.business.fragments.fragment_search;
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

        Log.d(TAG, "InstanceID token: " + FirebaseInstanceId.getInstance().getToken());
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

    }


    //setup bottom icon tab
    private void init_bottombar() {
        bottombar.setTabMode(TabLayout.MODE_FIXED);

        bottombar.addTab(bottombar.newTab().setCustomView(getTabView(R.drawable.ic_home_black_24dp)));
        bottombar.addTab(bottombar.newTab().setCustomView(getTabView(R.drawable.ic_list_black_24dp)));
        bottombar.addTab(bottombar.newTab().setCustomView(getTabView(R.drawable.ic_search_black_24dp)));
        bottombar.addTab(bottombar.newTab().setCustomView(getTabView(R.drawable.ic_notifications_black_24dp)));
        bottombar.addTab(bottombar.newTab().setCustomView(getTabView(R.drawable.ic_person_black_24dp)));

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
            switch (tab.getPosition()) {
                case 0: //home
                    switchFragment(new fragment_home(), true);
                    break;

                case 1: //category
                    switchFragment(new fragment_category(), true);
                    break;

                case 2: //search
                    switchFragment(new fragment_search(), true);
                    break;
            }
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {
        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {
        }
    };

    public void switchFragment(baseFragment fragment, boolean addtostack) {
        String tag = fragment.getClass().toString();

        if (Navigator.with(this).utils().canGoBackToSpecificPoint(tag, R.id.container, getSupportFragmentManager()))
            Navigator.with(this).utils().goBackToSpecificPoint(tag);
        else
            Navigator.with(this)
                    .build() //Enter in navigation mode
                    .goTo(fragment, R.id.container)
                    .tag(tag)
//                    .animation(R.anim.fadein, R.anim.fadeout, R.anim.fadein, R.anim.fadeout)
                    .addToBackStack() //add backstack
                    .replace() //CommitType
                    .commit(); //commit operation

        currentfragment = fragment;
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        int count = fm.getBackStackEntryCount();

        if (count == 0) {
            if (doubleBackToExitPressedOnce) {
                finish();
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "برای خروج دوباره کلید back را بزنید", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
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
                if (doubleBackToExitPressedOnce)
                    Navigator.with(this).utils().finishWithAnimation();

                this.doubleBackToExitPressedOnce = true;
                Toast.makeText(this, "برای خروج دوباره کلید back را بزنید", Toast.LENGTH_SHORT).show();

                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce = false;
                    }
                }, 2000);

            }
        }
    }
}

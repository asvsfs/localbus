package amir.app.business.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.strongloop.android.loopback.callbacks.ListCallback;
import com.strongloop.android.loopback.callbacks.ObjectCallback;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import amir.app.business.GuideApplication;
import amir.app.business.R;
import amir.app.business.adapter.EventListAdapter;
import amir.app.business.adapter.NotificationListAdapter;
import amir.app.business.config;
import amir.app.business.models.Customer;
import amir.app.business.models.Event;
import amir.app.business.models.db.Notification;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by amin on 11/02/2016.
 */

public class fragment_event extends baseFragment {
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.progress)
    ProgressBar progress;
    @BindView(R.id.txtempty)
    TextView txtempty;

    List<Event> events;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event, null);
        ButterKnife.bind(this, view);

        //config toolbar
        getactivity().setSupportActionBar(toolbar);
        getactivity().getSupportActionBar().setTitle("رویدادها");
        getactivity().getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getactivity().getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //setup back button on toolbar
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getactivity().onBackPressed();
            }
        });

        load_events();

//        EventBus.getDefault().register(this);

        return view;
    }

    private void load_events() {
        if (config.customer == null) {
            progress.setVisibility(View.GONE);
            txtempty.setVisibility(View.VISIBLE);
            return;
        }

        recyclerview.setLayoutManager(new LinearLayoutManager(getactivity(), LinearLayoutManager.VERTICAL, false));

        if (events != null) {
            progress.setVisibility(View.GONE);
            txtempty.setVisibility(View.GONE);

            setup_recyclerview();
            return;
        }

        Event.Repository eventRepo = GuideApplication.getLoopBackAdapter().createRepository(Event.Repository.class);
        eventRepo.getForCustomer(config.customer.getId(), new ListCallback<Event>() {
            @Override
            public void onSuccess(List<Event> events) {
                fragment_event.this.events = events;
                setup_recyclerview();

                progress.setVisibility(View.GONE);
                txtempty.setVisibility(events.size() > 0 ? View.GONE : View.VISIBLE);
            }

            @Override
            public void onError(Throwable t) {
                progress.setVisibility(View.GONE);
                txtempty.setVisibility(View.VISIBLE);
            }
        });
    }

    private void setup_recyclerview() {
        EventListAdapter adapter = new EventListAdapter(getActivity(), events);
        adapter.setOnItemClickListener(new EventListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Event event) {
                switchFragment(new fragment_event_page().newInstance(event), true);
            }
        });

        recyclerview.setAdapter(adapter);
        recyclerview.setNestedScrollingEnabled(false);
    }
}

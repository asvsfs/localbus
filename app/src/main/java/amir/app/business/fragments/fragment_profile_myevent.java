package amir.app.business.fragments;

import android.content.Intent;
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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import amir.app.business.GuideApplication;
import amir.app.business.R;
import amir.app.business.adapter.EventListAdapter;
import amir.app.business.config;
import amir.app.business.event.ProfileRefreshEvent;
import amir.app.business.management.activity.EventPage;
import amir.app.business.models.Event;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by amin on 11/02/2016.
 */

public class fragment_profile_myevent extends baseFragment {
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.progress)
    ProgressBar progress;
    @BindView(R.id.txtempty)
    TextView txtempty;

    List<Event> events;

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
        load_events();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_myevents, null);
        ButterKnife.bind(this, view);

        load_events();

        return view;
    }

    private void load_events() {
        recyclerview.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        if (config.customer == null) {
            progress.setVisibility(View.GONE);
            txtempty.setVisibility(View.VISIBLE);
            return;
        }

        if (events != null) {
            setup_recyclerview(events);
            return;
        }

        Event.Repository repository = GuideApplication.getLoopBackAdapter().createRepository(Event.Repository.class);
        repository.getForCustomer(config.customer.getId(), new ListCallback<Event>() {
            @Override
            public void onSuccess(List<Event> events) {
                fragment_profile_myevent.this.events = events;

                setup_recyclerview(events);
            }

            @Override
            public void onError(Throwable t) {
                progress.setVisibility(View.GONE);
            }
        });

    }

    private void setup_recyclerview(List<Event> events) {
        EventListAdapter adapter = new EventListAdapter(getActivity(), events);
        recyclerview.setAdapter(adapter);
        progress.setVisibility(View.GONE);
        txtempty.setVisibility(events.size() > 0 ? View.GONE : View.VISIBLE);

        adapter.setOnItemClickListener(new EventListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Event event) {
                Intent intent=new Intent(getactivity(), EventPage.class);
                intent.putExtra("event", event);
                getactivity().startActivity(intent);
            }
        });
    }

//    private void setup_recyclerview() {
//        EventListAdapter adapter = new EventListAdapter(getActivity(), events);
//        adapter.setOnItemClickListener(new EventListAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(Event event) {
//                switchFragment(new fragment_event_page().newInstance(event), true);
//            }
//        });
//
//        recyclerview.setAdapter(adapter);
//        recyclerview.setNestedScrollingEnabled(false);
//    }
}

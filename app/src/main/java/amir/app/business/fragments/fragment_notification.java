package amir.app.business.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import amir.app.business.R;
import amir.app.business.adapter.NotificationListAdapter;
import amir.app.business.models.db.Notification;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by amin on 11/02/2016.
 */

public class fragment_notification extends baseFragment {
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.progress)
    ProgressBar progress;
    @BindView(R.id.txtempty)
    TextView txtempty;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, null);
        ButterKnife.bind(this, view);

        //config toolbar
        getactivity().setSupportActionBar(toolbar);
        getactivity().getSupportActionBar().setTitle("اعلانات");
        getactivity().getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getactivity().getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //setup back button on toolbar
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getactivity().onBackPressed();
            }
        });

        load_notification();

        return view;
    }

    private void load_notification() {
        List<Notification> items = Notification.getAll();

        //setup notification view
        NotificationListAdapter adapter = new NotificationListAdapter(getActivity(), items);
        recyclerview.setAdapter(adapter);
        recyclerview.setNestedScrollingEnabled(false);

        progress.setVisibility(View.GONE);
        txtempty.setVisibility(items.size() > 0 ? View.GONE : View.VISIBLE);
    }
}

package amir.app.business.management.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import amir.app.business.R;
import amir.app.business.fragments.baseFragment;
import amir.app.business.models.Event;
import amir.app.business.widget.FarsiTextView;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by amin on 11/02/2016.
 */

public class EventPage extends AppCompatActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.txttitle)
    FarsiTextView txttitle;
    @BindView(R.id.txtdesc)
    FarsiTextView txtdesc;

    Event event;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_page);
        ButterKnife.bind(this);

        event= (Event) getIntent().getSerializableExtra("event");

        //config toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(event.getTitle());
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //setup back button on toolbar
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        txttitle.setText(event.getTitle());
        txtdesc.setText(event.getDescription());
    }
}

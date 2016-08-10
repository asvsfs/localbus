package amir.app.business;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.strongloop.android.loopback.callbacks.ListCallback;

import java.util.List;

import amir.app.business.adapter.BusinessHorizontalListAdapter;
import amir.app.business.models.Businesse;
import amir.app.business.widget.FarsiTextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by amin on 08/09/2016.
 */

public class BusinessActivity extends AppCompatActivity {
    @BindView(R.id.imggallery)
    ImageView imggallery;
    @BindView(R.id.txtdistance)
    TextView txtdistance;
    @BindView(R.id.txtname)
    FarsiTextView txtname;
    @BindView(R.id.txtdesc)
    FarsiTextView txtdesc;
    @BindView(R.id.similarRecyclerview)
    RecyclerView similarRecyclerview;

    Businesse businesse;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.btncomments)
    Button btncomments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business);
        ButterKnife.bind(this);

        businesse = (Businesse) getIntent().getExtras().getSerializable("business");


        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(businesse.getName());
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        load_similar_business_list();
    }

    private void load_similar_business_list() {
        similarRecyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

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
                BusinessHorizontalListAdapter topadapter = new BusinessHorizontalListAdapter(BusinessActivity.this, businesses);
                similarRecyclerview.setAdapter(topadapter);
                similarRecyclerview.setNestedScrollingEnabled(false);
            }

            @Override
            public void onError(Throwable t) {
            }
        });
    }

    @OnClick(R.id.btncomments)
    public void btncomment() {
        Intent intent = new Intent(this, CommentActivity.class);
        startActivity(intent);
    }

}

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
import android.widget.RatingBar;
import android.widget.TextView;

import com.strongloop.android.loopback.callbacks.ListCallback;

import java.util.ArrayList;
import java.util.List;

import amir.app.business.adapter.BusinessHorizontalListAdapter;
import amir.app.business.adapter.CommentMiniListAdapter;
import amir.app.business.models.Businesse;
import amir.app.business.models.Comment;
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
    @BindView(R.id.txtverification)
    FarsiTextView txtverification;
    @BindView(R.id.btncomments)
    Button btncomments;
    @BindView(R.id.btnmap)
    Button btnmap;
    @BindView(R.id.lastcommentRecyclerview)
    RecyclerView lastcommentRecyclerview;
    @BindView(R.id.ratingbar)
    RatingBar ratingbar;
    @BindView(R.id.btnlike)
    ImageView btnlike;
    @BindView(R.id.btnshare)
    ImageView btnshare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business);
        ButterKnife.bind(this);

        businesse = (Businesse) getIntent().getExtras().getSerializable("business");

        //config toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(businesse.getName());
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //setup back button on toolbar
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //load business list via api
        load_similar_business_list();

        //load three lastest comment about this business
        load_latest_comments_list();
    }

    private void load_latest_comments_list() {
        lastcommentRecyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        List<Comment> comments = new ArrayList<>();

        Comment c = new Comment();
        c.setText("خیلی خوب بود");
        comments.add(c);

        c = new Comment();
        c.setText("من این رو دیروز خریدم و خیلی راضی ام");
        comments.add(c);

        c = new Comment();
        c.setText("کاربردی");
        comments.add(c);

        lastcommentRecyclerview.setAdapter(new CommentMiniListAdapter(this, comments));
        lastcommentRecyclerview.setNestedScrollingEnabled(false);
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

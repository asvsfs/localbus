package amir.app.business.fragments;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.strongloop.android.loopback.callbacks.ListCallback;

import java.util.List;

import amir.app.business.GuideApplication;
import amir.app.business.R;
import amir.app.business.adapter.BusinessHorizontalListAdapter;
import amir.app.business.adapter.CommentListAdapter;
import amir.app.business.models.Businesse;
import amir.app.business.models.Comment;
import amir.app.business.widget.FarsiTextView;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by amin on 08/20/2016.
 */


public class fragment_comment extends baseFragment {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.ratingBar)
    RatingBar ratingBar;
    @BindView(R.id.txtrate)
    TextView txtrate;
    @BindView(R.id.txtname)
    FarsiTextView txtname;
    @BindView(R.id.commentresyclerview)
    RecyclerView commentresyclerview;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comments, null);
        ButterKnife.bind(this, view);

        //config toolbar
        getactivity().setSupportActionBar(toolbar);
        getactivity().getSupportActionBar().setTitle("نظرات");
        getactivity().getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getactivity().getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //setup back button on toolbar
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getactivity().onBackPressed();
            }
        });

        load_business_comments();
        return view;
    }

    private void load_business_comments() {
        commentresyclerview.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        Comment.Repository repository = GuideApplication.getLoopBackAdapter().createRepository(Comment.Repository.class);

        repository.findAll(new ListCallback<Comment>() {
            @Override
            public void onSuccess(List<Comment> items) {
                for (int i = 0; i < 10; i++) {
                    Comment comment = new Comment();

                    comment.setText("خیلی خوب");
                    items.add(comment);
                }

                //setup top businesses view
                CommentListAdapter topadapter = new CommentListAdapter(getActivity(), items);
                commentresyclerview.setAdapter(topadapter);
                commentresyclerview.setNestedScrollingEnabled(false);
            }

            @Override
            public void onError(Throwable t) {
            }
        });
    }

}

package amir.app.business;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.strongloop.android.loopback.callbacks.ListCallback;

import java.util.List;

import amir.app.business.adapter.BusinessHorizontalListAdapter;
import amir.app.business.adapter.CommentListAdapter;
import amir.app.business.models.Businesse;
import amir.app.business.models.Comment;
import amir.app.business.widget.FarsiTextView;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by amin on 08/09/2016.
 */

public class CommentActivity extends AppCompatActivity {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        load_comments_list();
    }

    private void load_comments_list() {
        commentresyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        Comment.Repository repository = GuideApplication.getLoopBackAdapter().createRepository(Comment.Repository.class);

        repository.findAll(new ListCallback<Comment>() {
            @Override
            public void onSuccess(List<Comment> comments) {
                for (int i = 0; i < 10; i++) {
                    Comment comment = new Comment();
//                    comment.setName("business " + i);
//                    comment.setDescription("description " + i);
                    comments.add(comment);
                }

                CommentListAdapter adapter = new CommentListAdapter(CommentActivity.this, comments);
                commentresyclerview.setAdapter(adapter);
            }

            @Override
            public void onError(Throwable t) {
            }
        });
    }


}

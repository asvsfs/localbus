package amir.app.business.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.strongloop.android.loopback.callbacks.ListCallback;

import java.util.List;

import amir.app.business.GuideApplication;
import amir.app.business.R;
import amir.app.business.management.adapter.FollowingListAdapter;
import amir.app.business.models.Event;
import amir.app.business.models.Following;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by amin on 11/02/2016.
 */

public class fragment_profile_myfollowing extends baseFragment {
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.progress)
    ProgressBar progress;
    @BindView(R.id.txtempty)
    TextView txtempty;

    List<Following> followings;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_myfollowing, null);
        ButterKnife.bind(this, view);

        load_following();

        return view;
    }

    private void load_following() {
        recyclerview.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        if (followings != null) {
            setup_recyclerview(followings);
            return;
        }

        Following.Repository repository = GuideApplication.getLoopBackAdapter().createRepository(Following.Repository.class);
        repository.findAll(new ListCallback<Following>() {
            @Override
            public void onSuccess(List<Following> following) {
                fragment_profile_myfollowing.this.followings = following;

                setup_recyclerview(following);
            }

            @Override
            public void onError(Throwable t) {

            }
        });
    }

    private void setup_recyclerview(List<Following> following) {
        recyclerview.setAdapter(new FollowingListAdapter(getActivity(), following));
        progress.setVisibility(View.GONE);
        txtempty.setVisibility(following.size() > 0 ? View.GONE : View.VISIBLE);
    }

}

package amir.app.business.management.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.Toast;

import com.strongloop.android.loopback.callbacks.ObjectCallback;
import com.strongloop.android.loopback.callbacks.VoidCallback;

import java.util.HashMap;
import java.util.List;

import amir.app.business.GuideApplication;
import amir.app.business.R;
import amir.app.business.models.Businesse;
import amir.app.business.models.Event;
import amir.app.business.models.Following;
import amir.app.business.widget.FarsiTextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by amin on 08/08/2016.
 */

public class FollowingListAdapter extends RecyclerView.Adapter<FollowingListAdapter.ViewHolder> {
    public interface OnItemClickListener {
        void onItemClick(Following following, View view);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.txttitle)
        FarsiTextView txttitle;
        @BindView(R.id.btnfollow)
        Button btnfollow;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

            view.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(items.get(getLayoutPosition()), v);
            }
        }

        @OnClick(R.id.btnfollow)
        public void btnfollow() {
            Following following = items.get(getLayoutPosition());
            following.destroy(new VoidCallback() {
                @Override
                public void onSuccess() {
                    items.remove(getLayoutPosition());
                    notifyItemRemoved(getLayoutPosition());
                    Toast.makeText(context, "از فهرست دنبال شده‌ها حذف شد", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(Throwable t) {

                }
            });
        }
    }

    Businesse.Repository businessRepo = GuideApplication.getLoopBackAdapter().createRepository(Businesse.Repository.class);
    Following.Repository followingRepo = GuideApplication.getLoopBackAdapter().createRepository(Following.Repository.class);

    OnItemClickListener mItemClickListener;
    Adapter adapter;

    Context context;
    LayoutInflater inflater;
    List<Following> items;

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public FollowingListAdapter(Context context, List<Following> items) {
        this.context = context;
        this.items = items;
        inflater = ((Activity) context).getLayoutInflater();

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.fragment_profile_following_row, viewGroup, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Following following = items.get(position);


        HashMap<String, String> param = new HashMap<>();
        param.put("userid", following.getUserid());

        businessRepo.findOne(param, new ObjectCallback<Businesse>() {
            @Override
            public void onSuccess(Businesse businesse) {
                holder.txttitle.setText(businesse.getName());
            }

            @Override
            public void onError(Throwable t) {

            }
        });
//        holder.txttitle.setText(following.getTitle());
//        holder.txtdesc.setText(following.getDescription());
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

}

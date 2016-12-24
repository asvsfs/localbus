package amir.app.business.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;

import com.nostra13.universalimageloader.core.DisplayImageOptions;

import java.util.List;

import amir.app.business.R;
import amir.app.business.models.Comment;
import amir.app.business.models.db.Notification;
import amir.app.business.widget.FarsiTextView;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by amin on 11/06/2016.
 */

public class NotificationListAdapter extends RecyclerView.Adapter<NotificationListAdapter.ViewHolder> {
    public interface OnItemClickListener {
        void onItemClick(Notification notification);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.txtmessage)
        FarsiTextView txtmessage;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

            view.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(items.get(getPosition()));
            }
        }
    }

    static OnItemClickListener mItemClickListener;
    Adapter adapter;

    Context context;
    LayoutInflater inflater;
    List<Notification> items;

    DisplayImageOptions options;

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public NotificationListAdapter(Context context, List<Notification> items) {
        this.context = context;
        this.items = items;
        inflater = ((Activity) context).getLayoutInflater();

        options = new DisplayImageOptions.Builder()
                .resetViewBeforeLoading(true)
                .cacheInMemory(true) // default
                .cacheOnDisk(true)
//                .showImageOnFail(R.drawable.icon_shop_avatar)
                .build(); // default
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.comments_row, viewGroup, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int i) {
        final Notification n = items.get(i);

        holder.txtmessage.setText(n.message);

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

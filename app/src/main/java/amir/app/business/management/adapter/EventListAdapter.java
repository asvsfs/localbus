package amir.app.business.management.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;

import java.util.List;

import amir.app.business.R;
import amir.app.business.models.Event;
import amir.app.business.widget.FarsiTextView;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by amin on 08/08/2016.
 */

public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.ViewHolder> {
    public interface OnItemClickListener {
        void onItemClick(Event event, View view);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.txttitle)
        FarsiTextView txttitle;
        @BindView(R.id.txtdesc)
        FarsiTextView txtdesc;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

            view.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(items.get(getPosition()), v);
            }
        }
    }

    static OnItemClickListener mItemClickListener;
    Adapter adapter;

    Context context;
    LayoutInflater inflater;
    List<Event> items;

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public EventListAdapter(Context context, List<Event> items) {
        this.context = context;
        this.items = items;
        inflater = ((Activity) context).getLayoutInflater();

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.fragment_profile_myevents_row, viewGroup, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Event event = items.get(position);

        holder.txttitle.setText(event.getTitle());
        holder.txtdesc.setText(event.getDescription());
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

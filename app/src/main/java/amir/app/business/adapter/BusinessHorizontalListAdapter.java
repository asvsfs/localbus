package amir.app.business.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;

import com.bumptech.glide.Glide;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import amir.app.business.R;
import amir.app.business.models.Businesse;
import amir.app.business.widget.FarsiTextView;
import amir.app.business.widget.SquareImageViewHeight_Based;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by amin on 08/08/2016.
 */

public class BusinessHorizontalListAdapter extends RecyclerView.Adapter<BusinessHorizontalListAdapter.ViewHolder> {
    public interface OnItemClickListener {
        void onItemClick(Businesse businesse);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.imgbusiness)
        SquareImageViewHeight_Based imgbusiness;
        @BindView(R.id.txtname)
        FarsiTextView txtname;
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
                mItemClickListener.onItemClick(items.get(getPosition()));
            }
        }
    }

    static OnItemClickListener mItemClickListener;
    Adapter adapter;

    Context context;
    LayoutInflater inflater;
    List<Businesse> items;

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public BusinessHorizontalListAdapter(Context context, List<Businesse> items) {
        this.context = context;
        this.items = items;
        inflater = ((Activity) context).getLayoutInflater();

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.business_horizontal_card_view, viewGroup, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Businesse b = items.get(position);

        holder.txtname.setText(b.getName());
        holder.txtdesc.setText(b.getDescription());

        List<String> images = b.getImages();
        if (images != null && images.size() > 0)
            Glide.with(context.getApplicationContext())
                    .load(context.getString(R.string.server) + images.get(0))
                    .into(holder.imgbusiness);
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

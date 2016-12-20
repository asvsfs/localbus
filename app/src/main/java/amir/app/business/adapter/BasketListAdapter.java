package amir.app.business.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;

import java.util.List;
import java.util.Locale;

import amir.app.business.R;
import amir.app.business.models.db.Basket;
import amir.app.business.widget.FarsiTextView;
import amir.app.business.widget.SquareImageViewHeight_Based;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by amin on 11/06/2016.
 */

public class BasketListAdapter extends RecyclerView.Adapter<BasketListAdapter.ViewHolder> {
    public interface OnItemListener {
        void onItemClick(Basket basket);
        void onDeleteClick(Basket basket);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.imgproduct)
        ImageView imgproduct;
        @BindView(R.id.txtname)
        FarsiTextView txtname;
        @BindView(R.id.txtcount)
        FarsiTextView txtcount;
        @BindView(R.id.imgdelete)
        ImageView imgdelete;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

            view.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            if (mItemListener != null) {
                mItemListener.onItemClick(items.get(getPosition()));
            }
        }

        @OnClick(R.id.imgdelete)
        public void imgdelete() {
            if (mItemListener != null) {
                mItemListener.onDeleteClick(items.get(getPosition()));
            }
        }

    }

    static OnItemListener mItemListener;
    Adapter adapter;

    Context context;
    LayoutInflater inflater;
    List<Basket> items;

    DisplayImageOptions options;

    public void setOnItemListener(final  OnItemListener mItemListener) {
        this.mItemListener = mItemListener;
    }

    public BasketListAdapter(Context context, List<Basket> items) {
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
                inflate(R.layout.fragment_basket_row, viewGroup, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int i) {
        final Basket basket = items.get(i);

        holder.txtname.setText(basket.name);
        holder.txtcount.setText(String.format(Locale.ENGLISH, "%d عدد", basket.count));
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

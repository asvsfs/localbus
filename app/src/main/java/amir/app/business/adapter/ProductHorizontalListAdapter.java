package amir.app.business.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;

import com.bumptech.glide.Glide;

import java.util.List;

import amir.app.business.R;
import amir.app.business.models.Product;
import amir.app.business.widget.FarsiTextView;
import amir.app.business.widget.SquareImageViewHeight_Based;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by amin on 08/08/2016.
 */

public class ProductHorizontalListAdapter extends RecyclerView.Adapter<ProductHorizontalListAdapter.ViewHolder> {
    public interface OnItemClickListener {
        void onItemClick(Product Producte);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.imgproduct)
        SquareImageViewHeight_Based imgProduct;
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

    OnItemClickListener mItemClickListener;
    Adapter adapter;

    Context context;
    LayoutInflater inflater;
    List<Product> items;

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public ProductHorizontalListAdapter(Context context, List<Product> items) {
        this.context = context;
        this.items = items;
        inflater = ((Activity) context).getLayoutInflater();

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.product_horizontal_card_view, viewGroup, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Product product = items.get(position);

        holder.txtname.setText(product.getName());
        holder.txtdesc.setText(product.getDescription());

        List<String> images = product.getImages();
        if (images != null && images.size() > 0)
            Glide.with(context.getApplicationContext())
                    .load(context.getString(R.string.server) + "/images/" + images.get(0))
                    .into(holder.imgProduct);
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

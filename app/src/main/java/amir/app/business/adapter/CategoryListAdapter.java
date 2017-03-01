package amir.app.business.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import java.util.List;

import amir.app.business.R;
import amir.app.business.models.Category;
import amir.app.business.models.Comment;
import amir.app.business.widget.FarsiTextView;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by amin on 08/08/2016.
 */

public class CategoryListAdapter extends RecyclerView.Adapter<CategoryListAdapter.ViewHolder> {
    public interface OnItemClickListener {
        void onItemClick(Category category);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.txtname)
        FarsiTextView txtname;
        @BindView(R.id.imageView)
        ImageView imageView;

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
    List<Category> items;

    DisplayImageOptions options;

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public CategoryListAdapter(Context context, List<Category> items) {
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
                inflate(R.layout.category_row, viewGroup, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int i) {
        final Category b = items.get(i);

        holder.txtname.setText(b.getName());

        Glide.with(context.getApplicationContext())
                .load(b.getIcon())
                .into(holder.imageView);
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

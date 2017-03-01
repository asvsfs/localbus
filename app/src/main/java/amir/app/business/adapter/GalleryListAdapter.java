package amir.app.business.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import amir.app.business.R;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by amin on 08/08/2016.
 */

public class GalleryListAdapter extends PagerAdapter {
    class ViewHolder {
        @BindView(R.id.imgproduct)
        ImageView imgproduct;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    String server;
    List<String> items;
    Context context;

    public GalleryListAdapter(Context context, List<String> items, String server) {
        this.context = context;
        this.items = items;
        this.server = context.getString(R.string.server) + "/images/";

        if (server != null)
            this.server = server;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_product_gallery_page, null);

        String item = items.get(position);
        ViewHolder holder = new ViewHolder(view);
        if (!item.equals(""))
            Glide.with(context.getApplicationContext())
                    .load(item.startsWith("/storage") ? item : server + item)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.imgproduct);

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == object);
    }

}

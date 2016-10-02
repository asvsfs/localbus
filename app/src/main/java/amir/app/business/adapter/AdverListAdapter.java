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

public class AdverListAdapter extends PagerAdapter {
    class ViewHolder {
        @BindView(R.id.imgadver)
        ImageView imgadver;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    List<String> items;
    Context context;

    public AdverListAdapter(Context context, List<String> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        int index = position % items.size();
        View view = LayoutInflater.from(context).inflate(R.layout.view_adver_page, null);

        ViewHolder holder = new ViewHolder(view);
//        Glide.with(context.getApplicationContext())
//                .load(context.getString(R.string.server) + items.get(position))
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .into(holder.imgadver);

        Display display = ((Activity) context).getWindowManager().getDefaultDisplay();

        holder.imgadver.getLayoutParams().height = display.getWidth() / 2;

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

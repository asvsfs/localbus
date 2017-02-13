package amir.app.business.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.strongloop.android.loopback.callbacks.ObjectCallback;
import com.strongloop.android.loopback.callbacks.VoidCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import amir.app.business.GuideApplication;
import amir.app.business.R;
import amir.app.business.config;
import amir.app.business.models.Comment;
import amir.app.business.models.Customer;
import amir.app.business.models.Product;
import amir.app.business.widget.CircleImageView;
import amir.app.business.widget.FarsiTextView;
import amir.app.business.widget.RoundedLetterView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import eu.inmite.android.lib.validations.form.annotations.Custom;

/**
 * Created by amin on 08/08/2016.
 */

public class CommentListAdapter extends RecyclerView.Adapter<CommentListAdapter.ViewHolder> {


    public interface OnItemClickListener {
        void onItemClick(Comment comment);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.txtdislike)
        RoundedLetterView txtdislike;
        @BindView(R.id.txtlike)
        RoundedLetterView txtlike;
        @BindView(R.id.txtlikecount)
        FarsiTextView txtlikecount;
        @BindView(R.id.txtdislikecount)
        FarsiTextView txtdislikecount;
        @BindView(R.id.txtcoment)
        FarsiTextView txtcoment;
        @BindView(R.id.imgavatar)
        CircleImageView imgavatar;
        @BindView(R.id.txtname)
        FarsiTextView txtname;

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

        @OnClick(R.id.txtlike)
        public void like() {
            Comment comment = items.get(getPosition());

            if (!comment.getLike().contains(config.customer.getId())) {
                comment.getLike().add(config.customer.getId());
                comment.getDislike().remove(config.customer.getId());

                comment.save(new VoidCallback() {
                    @Override
                    public void onSuccess() {
                        notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Throwable t) {

                    }
                });
            } else
                Toast.makeText(context, "به این نظر قبلا رای داده اید.", Toast.LENGTH_SHORT).show();
        }

        @OnClick(R.id.txtdislike)
        public void dislike() {
            Comment comment = items.get(getPosition());

            if (!comment.getDislike().contains(config.customer.getId())) {
                comment.getDislike().add(config.customer.getId());
                comment.getLike().remove(config.customer.getId());

                comment.save(new VoidCallback() {
                    @Override
                    public void onSuccess() {

                        notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Throwable t) {

                    }
                });
            } else
                Toast.makeText(context, "به این نظر قبلا رای داده اید.", Toast.LENGTH_SHORT).show();

        }

    }

    OnItemClickListener mItemClickListener;
    Adapter adapter;

    Customer.Repository customerRepo = GuideApplication.getLoopBackAdapter().createRepository(Customer.Repository.class);

    Context context;
    LayoutInflater inflater;
    List<Comment> items;

    DisplayImageOptions options;

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public CommentListAdapter(Context context, List<Comment> items) {
        this.context = context;
        this.items = items;
        inflater = ((Activity) context).getLayoutInflater();

        options = new DisplayImageOptions.Builder()
                .resetViewBeforeLoading(true)
                .cacheInMemory(true) // default
                .cacheOnDisk(true)
//                .showImageOnFail(R.drawable.icon_shop_avatar)
                .build(); // default

        for (Comment item : items) {
            if (item.getLike() == null)
                item.setLike(new ArrayList<String>());

            if (item.getDislike() == null)
                item.setDislike(new ArrayList<String>());
        }

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.comments_row, viewGroup, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int i) {
        final Comment comment = items.get(i);

        customerRepo.findById(comment.getCustomerId(), new ObjectCallback<Customer>() {
            @Override
            public void onSuccess(Customer customer) {
                holder.txtname.setText(customer.getRealm());
            }

            @Override
            public void onError(Throwable t) {

            }
        });

        holder.txtlikecount.setVisibility(config.customer == null ? View.GONE : View.VISIBLE);
        holder.txtlike.setVisibility(config.customer == null ? View.GONE : View.VISIBLE);
        holder.txtdislikecount.setVisibility(config.customer == null ? View.GONE : View.VISIBLE);
        holder.txtdislike.setVisibility(config.customer == null ? View.GONE : View.VISIBLE);

        holder.txtcoment.setText(comment.getText());
        holder.txtlikecount.setText(String.format(Locale.ENGLISH, "%d", comment.getLike().size()));
        holder.txtdislikecount.setText(String.format(Locale.ENGLISH, "%d", comment.getDislike().size()));
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

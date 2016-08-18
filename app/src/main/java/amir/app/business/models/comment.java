package amir.app.business.models;

import com.strongloop.android.loopback.Model;
import com.strongloop.android.loopback.ModelRepository;

/**
 * Created by amin on 08/05/2016.
 */

public class Comment extends Model {

    private String customerId;
    private String text;

    public String getText() {
        return text;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public static class Repository extends ModelRepository<Comment> {
        public Repository() {
            super("comment", "comments", Comment.class);
        }
    }
}

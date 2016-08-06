package amir.app.business.models;

import com.strongloop.android.loopback.Model;
import com.strongloop.android.loopback.ModelRepository;

/**
 * Created by amin on 08/05/2016.
 */

public class comment extends Model {
    public String customerId;
    public String text;

    public static class Repository extends ModelRepository<comment> {
        public Repository() {
            super("comment", "comments", comment.class);
        }
    }
}

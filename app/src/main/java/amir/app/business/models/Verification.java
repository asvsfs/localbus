package amir.app.business.models;

import com.strongloop.android.loopback.Model;
import com.strongloop.android.loopback.ModelRepository;

/**
 * Created by amin on 01/29/2016.
 */

public class Verification extends Model {

    private String customerId;

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String description) {
        this.customerId= description;
    }

    public static class Repository extends ModelRepository<Verification> {
        public Repository() {
            super("verification", "verifications", Verification.class);
        }
    }

}

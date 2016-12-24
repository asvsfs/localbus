package amir.app.business.models;

import com.google.common.collect.ImmutableMap;
import com.strongloop.android.loopback.Model;
import com.strongloop.android.loopback.ModelRepository;
import com.strongloop.android.loopback.callbacks.JsonArrayParser;
import com.strongloop.android.loopback.callbacks.JsonObjectParser;
import com.strongloop.android.loopback.callbacks.ListCallback;
import com.strongloop.android.loopback.callbacks.ObjectCallback;
import com.strongloop.android.remoting.adapters.RestContract;
import com.strongloop.android.remoting.adapters.RestContractItem;

import java.util.HashMap;
import java.util.Map;

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

        @Override
        public RestContract createContract() {
            RestContract contract = super.createContract();


            contract.addItem(new RestContractItem("/" + getNameForRestUrl() + "/:id", "GET"),
                    getClassName() + ".getByBusinessId");

            return contract;
        }

        public void getByBusinessId(String businessId, ListCallback<Comment> callback) {
            invokeStaticMethod("getById", ImmutableMap.of("id", businessId),
                    new JsonArrayParser<>(this, callback));
        }

        public void getByProductId(String productid, ListCallback<Comment> callback) {
            invokeStaticMethod("getByProductId", ImmutableMap.of("id", productid),
                    new JsonArrayParser<>(this, callback));
        }

    }
}

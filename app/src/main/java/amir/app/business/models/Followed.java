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
import java.util.List;
import java.util.Map;

/**
 * Created by amin on 11/01/2016.
 */

public class Followed extends Model  {
    private String userid;
    private List<String> followedid;
    private String id;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public List<String> getFollowedid() {
        return followedid;
    }

    public void setFollowedid(List<String> followedid) {
        this.followedid = followedid;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public static class Repository extends ModelRepository<Businesse> {
        public Repository() {
            super("followed", "followeds", Businesse.class);
        }

        @Override
        public RestContract createContract() {
            RestContract contract = super.createContract();

            contract.addItem(new RestContractItem("/" + getNameForRestUrl() + "/:id", "GET"),
                    getClassName() + ".getById");

            return contract;
        }

        public void getById(String id, ObjectCallback<Businesse> callback) {
            invokeStaticMethod("getById", ImmutableMap.of("id", id),
                    new JsonObjectParser<Businesse>(this, callback));
        }

    }

}

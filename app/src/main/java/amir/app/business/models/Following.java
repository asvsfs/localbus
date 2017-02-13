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

public class Following extends Model  {
    private String userid;
    private List<String> followingid;
    private String id;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public List<String> getFollowingid() {
        return followingid;
    }

    public void setFollowingid(List<String> followingid) {
        this.followingid = followingid;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public static class Repository extends ModelRepository<Following> {
        public Repository() {
            super("following", "followings", Following.class);
        }

        @Override
        public RestContract createContract() {
            RestContract contract = super.createContract();

            contract.addItem(new RestContractItem("/" + getNameForRestUrl() + "/:id", "GET"),
                    getClassName() + ".getById");

            return contract;
        }

        public void getById(String id, ObjectCallback<Following> callback) {
            invokeStaticMethod("getById", ImmutableMap.of("id", id),
                    new JsonObjectParser<Following>(this, callback));
        }

    }

}

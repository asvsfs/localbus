package amir.app.business.models;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
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

public class Event extends Model {

    private String title;
    private String description;
    private Location location;
    private String publishDate;
    private String ownerId;
    private int seen;
    private String id;

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public HashMap<String, Float> getLocation() {
        HashMap<String, Float> param = new HashMap<>();

        param.put("lat", location.getLat());
        param.put("lng", location.getLng());

        return param;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    public int getSeen() {
        return seen;
    }

    public void setSeen(int seen) {
        this.seen = seen;
    }

    public static class Repository extends ModelRepository<Event> {
        public Repository() {
            super("event", "events", Event.class);
        }

        @Override
        public RestContract createContract() {
            RestContract contract = super.createContract();

            contract.addItem(new RestContractItem("/" + getNameForRestUrl() + "/getForCustomer", "GET"),
                    getClassName() + ".getForCustomer");

            return contract;
        }

        public void getForCustomer(String id,  ListCallback<Event> callback) {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("id", id);

            invokeStaticMethod("getForCustomer", params, new JsonArrayParser<Event>(this, callback));
        }

    }

}

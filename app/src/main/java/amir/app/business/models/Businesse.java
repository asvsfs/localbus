package amir.app.business.models;

import com.google.common.collect.ImmutableMap;
import com.strongloop.android.loopback.Model;
import com.strongloop.android.loopback.ModelRepository;
import com.strongloop.android.loopback.callbacks.JsonArrayParser;
import com.strongloop.android.loopback.callbacks.JsonObjectParser;
import com.strongloop.android.loopback.callbacks.ListCallback;
import com.strongloop.android.loopback.callbacks.ObjectCallback;
import com.strongloop.android.loopback.callbacks.VoidCallback;
import com.strongloop.android.remoting.adapters.Adapter;
import com.strongloop.android.remoting.adapters.RestContract;
import com.strongloop.android.remoting.adapters.RestContractItem;

import java.io.Serializable;
import java.net.InterfaceAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

/**
 * Created by amin on 08/05/2016.
 */

public class Businesse extends Model implements Serializable {
    private String name;
    private Location location;
    private String description;
    private List<String> images;
    private String category;
    private String id;
    private String userid;

    public String getName() {
        return name;
    }

    public Location getLocation() {
        return location;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getImages() {
        return images;
    }

    public String getCategory() {
        return category;
    }

    public String getId() {
        return id;
    }

    public String getUserid() {
        return userid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public static class Repository extends ModelRepository<Businesse> {
        public Repository() {
            super("Businesse", "Businesses", Businesse.class);
        }

        @Override
        public RestContract createContract() {
            RestContract contract = super.createContract();

            contract.addItem(new RestContractItem("/" + getNameForRestUrl() + "/:id", "GET"),
                    getClassName() + ".getById");

            contract.addItem(new RestContractItem("/" + getNameForRestUrl() + "/searchBusiness", "GET"),
                    getClassName() + ".search");

            return contract;
        }

        public void getById(String id, ObjectCallback<Businesse> callback) {
            invokeStaticMethod("getById", ImmutableMap.of("id", id),
                    new JsonObjectParser<Businesse>(this, callback));
        }

        public void search(int page, String text, ListCallback<Businesse> callback) {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("page", page);
            params.put("text", text);

            invokeStaticMethod("searchBusiness", params, new JsonArrayParser<Businesse>(this, callback));
        }

    }


}

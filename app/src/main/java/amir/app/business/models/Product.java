package amir.app.business.models;

import com.google.common.collect.ImmutableMap;
import com.strongloop.android.loopback.Model;
import com.strongloop.android.loopback.ModelRepository;
import com.strongloop.android.loopback.callbacks.JsonArrayParser;
import com.strongloop.android.loopback.callbacks.JsonObjectParser;
import com.strongloop.android.loopback.callbacks.ListCallback;
import com.strongloop.android.loopback.callbacks.ObjectCallback;
import com.strongloop.android.remoting.adapters.Adapter;
import com.strongloop.android.remoting.adapters.RestContract;
import com.strongloop.android.remoting.adapters.RestContractItem;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by amin on 08/05/2016.
 */

public class Product extends Model implements Serializable {

//    private String id;
    private String name;
    private String description;
    private int price;
    private String owner;
    private String category;
    private String qrcode;
    private List<String> images;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

//    public String getId() {
//        return id;
//    }
//
//    public void setId(String id) {
//        this.id = id;
//    }

    public String getQrcode() {
        return qrcode;
    }

    public void setQrcode(String qrcode) {
        this.qrcode = qrcode;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public static class Repository extends ModelRepository<Product> {
        public Repository() {
            super("product", "products", Product.class);
        }

        @Override
        public RestContract createContract() {
            RestContract contract = super.createContract();

            contract.addItem(new RestContractItem("/" + getNameForRestUrl() + "/:id", "GET"),
                    getClassName() + ".getById");

            contract.addItem(new RestContractItem("/" + getNameForRestUrl() + "/:id", "GET"),
                    getClassName() + ".getByCustomerId");

            contract.addItem(new RestContractItem("/" + getNameForRestUrl() + "/qrexists", "GET"),
                    getClassName() + ".getByQRCode");

            return contract;
        }

        public void getById(String id, ObjectCallback<Product> callback) {
            invokeStaticMethod("getById", ImmutableMap.of("id", id),
                    new JsonObjectParser<Product>(this, callback));

        }

        public void getByCustomerId(String id, ListCallback<Product> callback) {
            invokeStaticMethod("getByCustomerId", ImmutableMap.of("id", id),
                    new JsonArrayParser<Product>(this, callback));
        }

        public void getByQRCode(String id, ObjectCallback<Product> callback) {
            invokeStaticMethod("qrexists", ImmutableMap.of("code", id),
                    new JsonObjectParser<Product>(this, callback));
        }
    }


}

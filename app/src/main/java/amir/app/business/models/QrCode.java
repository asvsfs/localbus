package amir.app.business.models;

import com.strongloop.android.loopback.Model;
import com.strongloop.android.loopback.ModelRepository;
import com.strongloop.android.loopback.callbacks.JsonArrayParser;
import com.strongloop.android.loopback.callbacks.ListCallback;
import com.strongloop.android.remoting.adapters.RestContract;
import com.strongloop.android.remoting.adapters.RestContractItem;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by amin on 02/22/2017.
 */

public class QrCode extends Model {

    private String productId;
    private String id;

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public static class Repository extends ModelRepository<QrCode> {
        public Repository() {
            super("qrCode", "qrCodes", QrCode.class);
        }

//        @Override
//        public RestContract createContract() {
//            RestContract contract = super.createContract();
//
//            contract.addItem(new RestContractItem("/" + getNameForRestUrl() + "/getForCustomer", "GET"),
//                    getClassName() + ".getForCustomer");
//
//            return contract;
//        }
//
//        public void getForCustomer(String id,  ListCallback<Event> callback) {
//            Map<String, Object> params = new HashMap<String, Object>();
//            params.put("id", id);
//
//            invokeStaticMethod("getForCustomer", params, new JsonArrayParser<Event>(this, callback));
//        }
    }
}

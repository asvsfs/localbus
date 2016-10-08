package amir.app.business.models;

import android.accounts.AccountManager;
import android.provider.Settings;

import com.strongloop.android.loopback.AccessToken;
import com.strongloop.android.loopback.Model;
import com.strongloop.android.loopback.ModelRepository;
import com.strongloop.android.loopback.User;
import com.strongloop.android.loopback.UserRepository;
import com.strongloop.android.loopback.callbacks.EmptyResponseParser;
import com.strongloop.android.loopback.callbacks.JsonArrayParser;
import com.strongloop.android.loopback.callbacks.JsonObjectParser;
import com.strongloop.android.loopback.callbacks.ListCallback;
import com.strongloop.android.loopback.callbacks.ObjectCallback;
import com.strongloop.android.loopback.callbacks.VoidCallback;
import com.strongloop.android.remoting.JsonUtil;
import com.strongloop.android.remoting.adapters.Adapter;
import com.strongloop.android.remoting.adapters.RestContract;
import com.strongloop.android.remoting.adapters.RestContractItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import amir.app.business.callbacks.EmptyCallback;
import amir.app.business.callbacks.SimpleCallback;

/**
 * Created by amin on 08/05/2016.
 */

public class Customer extends User {

    public interface LoginCallback {
        public void onSuccess(Token token);
        public void onError(Throwable t);
    }

    private String realm;
    private String username;
    private String email;
    private Boolean emailVerified;
    private String id;
    private AccountManager mAccountManager;

    public String getRealm() {
        return realm;
    }

    public void setRealm(String realm) {
        this.realm = realm;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(Boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public static class Repository extends UserRepository<Customer> {
        public Repository() {
            super("Customer", "Customers", Customer.class);
        }

        public RestContract createContract() {
            RestContract contract = super.createContract();

            contract.addItem(new RestContractItem("/" + getNameForRestUrl() + "/login", "POST"),
                    getClassName() + ".login");

            contract.addItem(new RestContractItem("/" + getNameForRestUrl() + "/authCheck", "GET"),
                    getClassName() + ".authCheck");

            return contract;
        }
        public void authCheck(SimpleCallback callback){
            invokeStaticMethod("authCheck", null, new EmptyCallback(callback));
        }
        public void loginUserA(String username, String password,Boolean isEmail,
                              final Customer.LoginCallback callback) {

            String android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                    Settings.Secure.ANDROID_ID);

            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put(isEmail ? "email": "username", username);
            params.put("password", password);
            params.put("deviceId", android_id);

            invokeStaticMethod("login", params,
                    new Adapter.JsonObjectCallback() {

                        @Override
                        public void onError(Throwable t) {
                            callback.onError(t);
                        }

                        @Override
                        public void onSuccess(JSONObject response) {
                            Token token = new Token();
                            try {
                                token.id = response.getString("id");
                                token.userId = response.getString("userId");
                                callback.onSuccess(token);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            setCurrentUserId(token.userId);
                        }
                    });
        }
    }

}

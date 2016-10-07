package amir.app.business.models;

import android.accounts.AccountManager;
import android.provider.Settings;

import com.strongloop.android.loopback.Model;
import com.strongloop.android.loopback.ModelRepository;
import com.strongloop.android.loopback.UserRepository;
import com.strongloop.android.remoting.adapters.Adapter;
import com.strongloop.android.remoting.adapters.RestContract;
import com.strongloop.android.remoting.adapters.RestContractItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by amin on 08/05/2016.
 */

public class Customer extends Model implements Serializable {
    private String realm;
    private String username;
    private String email;
    private String emailVerified;
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

    public String getEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(String emailVerified) {
        this.emailVerified = emailVerified;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public static class Repository extends ModelRepository<Admin> {
        public Repository() {
            super("Customer", "Users", Admin.class);
        }

        public RestContract createContract() {
            RestContract contract = super.createContract();

            contract.addItem(new RestContractItem("/" + getNameForRestUrl() + "/login", "POST"),
                    getClassName() + ".login");

            return contract;
        }

        public void loginUser(String username, String password,
                              final Admin.LoginCallback callback) {

            String android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                    Settings.Secure.ANDROID_ID);

            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("username", username);
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
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            callback.onSuccess(token);
                        }
                    });
        }
    }

}

package amir.app.business.models;

import com.strongloop.android.loopback.Model;
import com.strongloop.android.loopback.ModelRepository;
import com.strongloop.android.remoting.adapters.Adapter;
import com.strongloop.android.remoting.adapters.RestContract;
import com.strongloop.android.remoting.adapters.RestContractItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by amin on 08/05/2016.
 */

public class Admin extends Model {

    public interface LoginCallback {
        public void onSuccess(Token token);
        public void onError(Throwable t);
    }

    private String realm;
    private String username;
    private String email;
    private String emailVerified;
    private String id;

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
            super("admin", "admins", Admin.class);
        }

        public RestContract createContract() {
            RestContract contract = super.createContract();

            contract.addItem(new RestContractItem("/" + getNameForRestUrl() + "/login", "POST"),
                    getClassName() + ".login");

            return contract;
        }

        public void loginUser(String username, String password,
                              final LoginCallback callback) {

            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("username", username);
            params.put("password", password);

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

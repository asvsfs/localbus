package amir.app.business.models;

/**
 * Created by amin on 02/21/2017.
 */

public interface StringCallback {
    void onSuccess(String string);
    void onError(Throwable t);
}

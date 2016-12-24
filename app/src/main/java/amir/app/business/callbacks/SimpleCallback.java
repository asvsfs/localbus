package amir.app.business.callbacks;

/**
 * Created by Amir on 10/8/2016.
 */

public interface SimpleCallback {
    public void onSuccess(String response);
    public void onError(Throwable t);
}

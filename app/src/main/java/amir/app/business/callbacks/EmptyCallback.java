package amir.app.business.callbacks;

import com.strongloop.android.loopback.callbacks.VoidCallback;
import com.strongloop.android.remoting.adapters.Adapter;

/**
 * Created by Amir on 10/8/2016.
 */


public class EmptyCallback implements Adapter.Callback {
    private final SimpleCallback callback;

    public EmptyCallback(SimpleCallback callback) {
        this.callback = callback;
    }

    @Override
    public void onSuccess(String response) {
        callback.onSuccess(response);
    }

    @Override
    public void onError(Throwable t) {
        callback.onError(t);
    }
}

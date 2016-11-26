package amir.app.business;

import android.content.Context;

import com.strongloop.android.loopback.RestAdapter;

import amir.app.business.models.Token;

/**
 * Created by amin on 08/05/2016.
 */

public class GuideApplication extends com.activeandroid.app.Application {
    static RestAdapter adapter;
    static Context context;

    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        getLoopBackAdapter();
    }

    public static RestAdapter getLoopBackAdapter() {
        if (adapter == null) {
            adapter = new RestAdapter(context, context.getString(R.string.server) + "/api");

            config.token = new Token();
            config.token.id = config.getValue(context, "token");
            config.token.userId = config.getValue(context, "userid");
            adapter.setAccessToken(config.token.id);
        }
        return adapter;
    }
}

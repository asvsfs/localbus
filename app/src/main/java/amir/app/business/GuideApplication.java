package amir.app.business;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.strongloop.android.loopback.RestAdapter;

import amir.app.business.models.Admin;
import amir.app.business.models.Businesse;
import amir.app.business.models.User;

/**
 * Created by amin on 08/05/2016.
 */

public class GuideApplication extends Application {
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
//            adapter.setAccessToken("");
        }
        return adapter;
    }
}

package amir.app.business;

import android.app.Application;
import android.content.Context;

import com.strongloop.android.loopback.RestAdapter;

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
            adapter = new RestAdapter(context, "http://139.59.153.64:3000/api");
            adapter.setAccessToken("kx9tUi1U9b3kSV2CNL54eUTQC37BBjLiOnZ2MRmd9tLH21XKljf8xd8lh7R9M7d3");
        }

        return adapter;
    }
}

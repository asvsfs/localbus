package org.fingerlinks.mobile.android.navigator;

import android.content.Context;

import org.fingerlinks.mobile.android.navigator.builder.Builders;
import org.fingerlinks.mobile.android.navigator.builder.impl.GoToBuilder;
import org.fingerlinks.mobile.android.navigator.builder.impl.NavigatorUtils;
import org.fingerlinks.mobile.android.navigator.utils.ContextReference;

/**
 * Created by fabio on 07/09/15.
 */
public class Navigator {

    private Navigator mInstance;
    private Context mContext;

    private Navigator(Context context) {
        mContext = context;
    }

    /**
     * Get the default Navigator object instance and begin building a request
     *
     * @param context
     * @return
     */
    public static Navigator with(Context context) throws NavigatorException {
        return getInstance(context);
    }

    public Builders.Any.U utils() {
        return new NavigatorUtils(ContextReference.fromContext(mContext));
    }

    public Builders.Any.G build() throws NavigatorException {
        mNavBean = new NavigatorBean();
        return new GoToBuilder(ContextReference.fromContext(mContext), mNavBean);
    }

    /***********************
     * private method
     ******************************************************/

    private static Navigator getInstance(Context context) {
        if (context == null)
            throw new NullPointerException("Can not pass null context in to retrieve Navigator instance");

        //if(mInstance == null) {
        //    mInstance = new Navigator(context);
        //}
        return new Navigator(context); //mInstance;
    }

    private NavigatorBean mNavBean;

}
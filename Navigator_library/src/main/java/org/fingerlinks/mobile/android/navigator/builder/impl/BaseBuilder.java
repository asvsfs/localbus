package org.fingerlinks.mobile.android.navigator.builder.impl;

import android.util.Log;

import org.fingerlinks.mobile.android.navigator.NavigatorBean;
import org.fingerlinks.mobile.android.navigator.NavigatorException;
import org.fingerlinks.mobile.android.navigator.utils.ContextReference;

/**
 * Created by fabio on 11/09/15.
 */
public class BaseBuilder {

    protected ContextReference mContextReference;
    protected NavigatorBean mNavigatorBean;

    public BaseBuilder(ContextReference context, NavigatorBean navigatorBean) throws NavigatorException {
        mContextReference = context;
        String alive = mContextReference.isAlive();
        if (null != alive) {
            String _msg = "Building request with dead context[" + alive + "]";
            Log.w(getClass().getName(), _msg);
            throw new NavigatorException(_msg);
        }
        mNavigatorBean = navigatorBean;
    }

}
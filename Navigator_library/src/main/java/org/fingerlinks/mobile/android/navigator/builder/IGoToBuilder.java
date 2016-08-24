package org.fingerlinks.mobile.android.navigator.builder;

import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * Created by koush on 5/30/13.
 */
public interface IGoToBuilder<A extends IActivityBuilder, F extends IFragmentBuilder> {

    /**
     * @param activity
     * @return
     */
    A goTo(Class<?> activity);

    /**
     * @param activity
     * @param bundle
     * @return
     */
    A goTo(Class<?> activity, Bundle bundle);

    /**
     * @param fragment
     * @param container
     * @return
     */
    F goTo(Fragment fragment, int container);

    /**
     * @param fragment
     * @param bundle
     * @param container
     * @return
     */
    F goTo(Fragment fragment, Bundle bundle, int container);

}
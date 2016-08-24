package org.fingerlinks.mobile.android.navigator.builder;

/**
 * Created by fabio on 07/09/15.
 */
public interface INavigatorBuilder<N extends INavigatorBuilder> {

    N debug();

    /**
     *
     */
    void commit();

}
package org.fingerlinks.mobile.android.navigator.builder;

import org.fingerlinks.mobile.android.navigator.AnimationEnum;

/**
 * Created by fabio on 07/09/15.
 */
public interface IActivityBuilder<A extends IActivityBuilder> {

    A addRequestCode(int request_code);

    /**
     * replacement method Builders.Any.N.commit () only to avoid duplication of the method call
     */
    void commit();

    /**
     *
     */
    A animation();

    /**
     *
     */
    A animation(AnimationEnum animation);

    /**
     * @param enter
     * @param exit
     */
    A animation(int enter, int exit);

}
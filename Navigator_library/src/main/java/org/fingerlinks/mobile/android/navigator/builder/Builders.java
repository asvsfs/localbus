package org.fingerlinks.mobile.android.navigator.builder;

/**
 * Created by Fabio
 */
public interface Builders {

    interface Any {


        // restrict to fragment builder
        interface F extends IFragmentBuilder<F> {
        }

        // restrict to activity builder
        interface A extends IActivityBuilder<A> {
        }

        // restrict to goto builder
        interface G extends IGoToBuilder<A, F> {
        }

        // generic navigator buider
        interface U extends INavigatorUtils<U> {
        }

        // generic navigator buider
        interface N extends INavigatorBuilder<N> {
        }
    }

}
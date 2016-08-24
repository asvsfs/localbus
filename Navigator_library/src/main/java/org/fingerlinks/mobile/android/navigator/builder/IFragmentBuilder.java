package org.fingerlinks.mobile.android.navigator.builder;

/**
 * Created by fabio on 07/09/15.
 */
public interface IFragmentBuilder<F extends IFragmentBuilder> {

    /**
     * @param tag
     * @return
     */
    F tag(String tag);

    /**
     * @return
     */
    Builders.Any.N replace();

    /**
     * @return
     */
    Builders.Any.N add();

    /**
     *
     */
    F animation();

    /**
     * @param enter
     * @param exit
     */
    F animation(int enter, int exit);

    /**
     * @param enter
     * @param exit
     * @param popEnter
     * @param popExit
     * @return
     */
    F animation(int enter, int exit, int popEnter, int popExit);

    /**
     * @return
     */
    F addToBackStack();

}
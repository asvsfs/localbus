package org.fingerlinks.mobile.android.navigator.builder.impl;

import org.fingerlinks.mobile.android.navigator.CommitTypeEnum;
import org.fingerlinks.mobile.android.navigator.NavigatorBean;
import org.fingerlinks.mobile.android.navigator.NavigatorException;
import org.fingerlinks.mobile.android.navigator.builder.Builders;
import org.fingerlinks.mobile.android.navigator.utils.ContextReference;

/**
 * Created by fabio on 11/09/15.
 */
public class FragmentBuilder extends BaseBuilder implements Builders.Any.F {

    public FragmentBuilder(ContextReference context, NavigatorBean navigatorBean) throws NavigatorException {
        super(context, navigatorBean);
    }

    @Override
    public Builders.Any.F tag(String tag) {
        mNavigatorBean.setTag(tag);
        return this;
    }

    @Override
    public Builders.Any.N replace() {
        setCommitType(CommitTypeEnum.REPLACE);
        return new NavigatorBuilder(mContextReference, mNavigatorBean, true);
    }

    @Override
    public Builders.Any.N add() {
        setCommitType(CommitTypeEnum.ADD);
        return new NavigatorBuilder(mContextReference, mNavigatorBean, true);
    }

    @Override
    public Builders.Any.F animation() {
        if (mNavigatorBean == null) {
            throw new NavigatorException("NavBean not initialized");
        }
        mNavigatorBean.setAnimation(true);
        return this;
    }

    @Override
    public Builders.Any.F animation(int enter, int exit) {
        int[] animations = new int[2];
        animations[0] = enter;
        animations[1] = exit;
        mNavigatorBean.setAnimations(animations);
        return this;
    }

    @Override
    public Builders.Any.F animation(int enter, int exit, int popEnter, int popExit) {
        int[] animations = new int[4];
        animations[0] = enter;
        animations[1] = exit;
        animations[2] = popEnter;
        animations[3] = popExit;
        mNavigatorBean.setAnimations(animations);
        return this;
    }


    @Override
    public Builders.Any.F addToBackStack() {
        mNavigatorBean.setAddToBackStack(true);
        return this;
    }

    private void setCommitType(CommitTypeEnum commitType) {
        mNavigatorBean.setType(commitType);
    }

}
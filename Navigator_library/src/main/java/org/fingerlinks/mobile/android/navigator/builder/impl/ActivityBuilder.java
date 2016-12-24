package org.fingerlinks.mobile.android.navigator.builder.impl;

import org.fingerlinks.mobile.android.navigator.AnimationEnum;
import org.fingerlinks.mobile.android.navigator.NavigatorBean;
import org.fingerlinks.mobile.android.navigator.NavigatorException;
import org.fingerlinks.mobile.android.navigator.builder.Builders;
import org.fingerlinks.mobile.android.navigator.utils.ContextReference;

/**
 * Created by fabio on 11/09/15.
 */
public class ActivityBuilder extends BaseBuilder implements Builders.Any.A {

    public ActivityBuilder(ContextReference context, NavigatorBean navigatorBean) throws NavigatorException {
        super(context, navigatorBean);
    }

    @Override
    public Builders.Any.A addRequestCode(int request_code) {
        mNavigatorBean.setRequestCode(request_code);
        return this;
    }

    public void commit() {
        new NavigatorBuilder(mContextReference, mNavigatorBean, false).commit();
    }

    @Override
    public Builders.Any.A animation() {
        animation(AnimationEnum.HORIZONTAL);
        return this;
    }

    @Override
    public Builders.Any.A animation(AnimationEnum animation) {
        if (mNavigatorBean == null) {
            throw new NavigatorException("NavBean not initialized");
        }
        mNavigatorBean.setAnimationEnum(animation);
        mNavigatorBean.setAnimation(true);
        return this;
    }

    @Override
    public Builders.Any.A animation(int enter, int exit) {
        int[] animations = new int[2];
        animations[0] = enter;
        animations[1] = exit;
        mNavigatorBean.setAnimations(animations);
        return this;
    }

    private final static String TAG = ActivityBuilder.class.getName();
}
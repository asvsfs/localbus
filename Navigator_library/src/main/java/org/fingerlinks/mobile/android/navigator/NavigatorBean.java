package org.fingerlinks.mobile.android.navigator;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import java.io.Serializable;

/**
 * Created by fabio on 06/09/15.
 */
public class NavigatorBean implements Serializable {

    private Intent intent = null;
    private FragmentManager fragmentManager = null;
    private Fragment fragment = null;
    private int container;
    private boolean addToBackStack = false;
    private String tag = null;
    private boolean animation = false;
    private int[] animations = new int[0];
    private CommitTypeEnum type;
    private int requestCode;
    private AnimationEnum animationEnum;

    protected NavigatorBean() {
    }

    public Intent getIntent() {
        return intent;
    }

    public void setIntent(Intent intent) {
        this.intent = intent;
    }

    public FragmentManager getFragmentManager() {
        return fragmentManager;
    }

    public void setFragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    public Fragment getFragment() {
        return fragment;
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }

    public int getContainer() {
        return container;
    }

    public void setContainer(int container) {
        this.container = container;
    }

    public CommitTypeEnum getType() {
        return type;
    }

    public void setType(CommitTypeEnum type) {
        this.type = type;
    }

    public boolean isAddToBackStack() {
        return addToBackStack;
    }

    public void setAddToBackStack(boolean addToBackStack) {
        this.addToBackStack = addToBackStack;
    }

    public String getTag() {
        if (tag == null) {
            return getFragment().getClass().getName();
        }
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public boolean isAnimation() {
        return animation;
    }

    public void setAnimation(boolean animation) {
        this.animation = animation;
    }

    public int[] getAnimations() {
        return animations;
    }

    public void setAnimations(int... animations) {
        this.animations = animations;
        setAnimation(true);
    }

    public int getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(int requestCode) {
        this.requestCode = requestCode;
    }

    public AnimationEnum getAnimationEnum() {
        return animationEnum;
    }

    public void setAnimationEnum(AnimationEnum animationEnum) {
        this.animationEnum = animationEnum;
    }
}
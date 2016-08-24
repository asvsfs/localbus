package org.fingerlinks.mobile.android.navigator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Encapsulates navigation operations between fragments or activity
 *
 * @deprecated Migrate to Builders pattern</br>
 * {will be removed in version 1.1} </br>
 * use {@link Navigator} instead like this:
 * <p>
 * <blockquote>
 * <pre>
 * Navigator
 *   .with(MainActivity.this)
 *   .build() //Enter in navigation mode
 *   .animation() //Add default animation
 *   .goTo(SecondActivity.class, bundle) //set destination and Bundle data
 *   .addRequestCode(9001) //set REQUEST_CODE
 *   .commit(); //Execute startActivity -- startActivityForResult
 * </pre></blockquote>
 */
@Deprecated()
public class NavigatorHelper {

    public final static String BUNDLE = NavigatorHelper.class.getName() + ".bundle";
    private final static String TAG = NavigatorHelper.class.getName();
    private static final long DOUBLE_PRESS_INTERVAL = 5 * 1000;
    private static NavigatorHelper mInstance;
    private List<String> listStep;
    private long lastPressTime;
    private Context mContext;
    private NavigatorBean mNavBean;

    public static NavigatorHelper with(Context context) {
        if (mInstance == null) {
            mInstance = new NavigatorHelper();
            mInstance.initDebug();
        }
        mInstance.initNavHelper(context);
        return mInstance;
    }

    public void printDebug() {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "--------------------------------------------------------");
            Log.d(TAG, "list of step");
            for (String step : listStep) {
                Log.d(TAG, step);
            }
        }
    }

    public NavigatorHelper goTo(Class<?> activity, Bundle bundle) {
        Intent intent = new Intent(mContext, activity);
        if (bundle != null) {
            intent.putExtra(BUNDLE, bundle);
        }
        mNavBean.setIntent(intent);
        listStep.add(activity.getName());
        return this;
    }

    public NavigatorHelper goTo(Class<?> activity) {
        return goTo(activity, null);
    }

    public NavigatorHelper goTo(Fragment fragment, Bundle bundle, int container) {
        FragmentManager fragmentManager = ((FragmentActivity) mContext).getSupportFragmentManager();
        if (bundle != null) {
            fragment.setArguments(bundle);
        }
        mNavBean.setFragment(fragment);
        mNavBean.setFragmentManager(fragmentManager);
        mNavBean.setContainer(container);
        return this;
    }

    public NavigatorHelper goTo(Fragment fragment, int container) {
        goTo(fragment, null, container);
        return this;
    }

    public NavigatorHelper tag(String tag) {
        mNavBean.setTag(tag);
        return this;
    }

    public NavigatorHelper replace() {
        CommitTypeEnum type = CommitTypeEnum.REPLACE;
        mNavBean.setType(type);
        return this;
    }

    public NavigatorHelper add() {
        CommitTypeEnum type = CommitTypeEnum.ADD;
        mNavBean.setType(type);
        return this;
    }

    public NavigatorHelper addToBackStack() {
        mNavBean.setAddToBackStack(true);
        return this;
    }

    public void commit() {
        commit(-1);
    }

    public void commit(int REQUEST_CODE) {
        if (mNavBean.getIntent() != null) {
            commitActivity(REQUEST_CODE);
        }
        if (mNavBean.getFragmentManager() != null) {
            commitFragment();
        }
    }

    public NavigatorHelper animation() {
        mNavBean.setAnimation(true);
        return this;
    }

    public NavigatorHelper customAnimation(int enter, int exit) {
        int[] animations = new int[2];
        animations[0] = enter;
        animations[1] = exit;
        mNavBean.setAnimations(animations);
        return this;
    }

    public NavigatorHelper customAnimation(int enter, int exit, int popEnter, int popExit) {
        int[] animations = new int[4];
        animations[0] = enter;
        animations[1] = exit;
        animations[2] = popEnter;
        animations[3] = popExit;
        mNavBean.setAnimations(animations);
        return this;
    }

    public boolean canGoBack(String tag, int container, FragmentManager fragmentManager) {
        if (TextUtils.isEmpty(tag)) {
            return (fragmentManager.getBackStackEntryCount() > 1);
        } else {
            List<FragmentManager.BackStackEntry> fragmentList = fragmentList();
            Fragment fragment = fragmentManager.findFragmentById(container);
            if (fragment != null && tag.equalsIgnoreCase(fragment.getTag())) {
                return false;
            }
            for (int i = 0; i < fragmentList.size(); i++) {
                if (tag.equalsIgnoreCase(fragmentList.get(i).getName())) {
                    return true;
                }
            }
            return false;
        }
    }

    public boolean canGoBack(FragmentManager fragmentManager) {
        return canGoBack(null, 0, fragmentManager);
    }

    public void goBackTo(String tag) throws NavigatorException {
        FragmentManager fragmentManager = ((FragmentActivity) mContext).getSupportFragmentManager();
        if (fragmentManager.findFragmentByTag(tag) != null) {
            List<FragmentManager.BackStackEntry> fragmentList = fragmentList();
            Collections.reverse(fragmentList);
            for (int i = 0; i < fragmentList.size(); i++) {
                if (!tag.equalsIgnoreCase(fragmentList.get(i).getName())) {
                    fragmentManager.popBackStack();
                } else {
                    listStep.add(tag);
                    return;
                }
            }
        } else {
            Log.e(TAG, "no fragment found");
            String message = "Fragment with TAG[" + tag + "] not found into backstack entry";
            throw new NavigatorException(message);
        }
    }

    public void goBack() throws NavigatorException {
        FragmentManager fragmentManager = ((FragmentActivity) mContext).getSupportFragmentManager();
        if (canGoBack(fragmentManager)) {
            fragmentManager.popBackStack();
        }
    }

    public void backHome(Activity activity, String message) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
        long pressTime = System.currentTimeMillis();
        if ((pressTime - lastPressTime) <= DOUBLE_PRESS_INTERVAL) {
            activity.finish();
        }
        lastPressTime = pressTime;
    }

    /**************************
     * Private class Method
     ***********************************************/

    private void initNavHelper(Context context) {
        this.mContext = context;
        this.mNavBean = new NavigatorBean();
    }

    private void initDebug() {
        listStep = new ArrayList<>();
    }

    private void commitActivity() {
        commitActivity(-1);
    }

    private void commitActivity(int REQUEST_CODE) {
        if (REQUEST_CODE > 0) {
            ((Activity) mContext).startActivityForResult(mNavBean.getIntent(), REQUEST_CODE);
        } else {
            mContext.startActivity(mNavBean.getIntent());
        }
        if (mNavBean.isAnimation()) {
            switch (mNavBean.getAnimations().length) {
                case 2:
                    ((Activity) mContext).overridePendingTransition(mNavBean.getAnimations()[0], mNavBean.getAnimations()[1]);
                    break;
                default:
                    ((Activity) mContext).overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_left);
                    break;
            }
        }
    }

    private void commitFragment() {
        listStep.add(mNavBean.getTag());

        FragmentTransaction fragmentTransaction = mNavBean.getFragmentManager().beginTransaction();
        if (mNavBean.isAnimation()) {
            switch (mNavBean.getAnimations().length) {
                case 2:
                    fragmentTransaction.setCustomAnimations(mNavBean.getAnimations()[0], mNavBean.getAnimations()[1]);
                    break;
                case 4:
                    fragmentTransaction.setCustomAnimations(mNavBean.getAnimations()[0], mNavBean.getAnimations()[1], mNavBean.getAnimations()[2], mNavBean.getAnimations()[3]);
                    break;
                default:
                    fragmentTransaction.setCustomAnimations(R.anim.view_flipper_transition_in_left, R.anim.view_flipper_transition_out_left, R.anim.view_flipper_transition_in_right, R.anim.view_flipper_transition_out_right);
                    break;
            }
        }
        switch (mNavBean.getType()) {
            case REPLACE:
                fragmentTransaction.replace(mNavBean.getContainer(), mNavBean.getFragment(), mNavBean.getTag());
                break;
            case ADD:
                fragmentTransaction.add(mNavBean.getContainer(), mNavBean.getFragment(), mNavBean.getTag());
                break;
        }
        if (mNavBean.isAddToBackStack()) {
            fragmentTransaction.addToBackStack(mNavBean.getTag());
        }
        fragmentTransaction.commit();
    }

    private List<FragmentManager.BackStackEntry> fragmentList() {
        FragmentManager fragmentManager = ((FragmentActivity) mContext).getSupportFragmentManager();
        List<FragmentManager.BackStackEntry> fragmentList = new ArrayList<>();
        int size = fragmentManager.getBackStackEntryCount();
        for (int i = 0; i < size; i++) {
            fragmentList.add(fragmentManager.getBackStackEntryAt(i));
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "position: " + i + " name: " + fragmentManager.getBackStackEntryAt(i).getName());
            }
        }
        return fragmentList;
    }
}
package amir.app.business.fragments;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import amir.app.business.MainActivity;

/**
 * Created by amin on 08/20/2016.
 */

public class baseFragment extends Fragment {

    public AppCompatActivity getactivity(){
        return ((MainActivity) getActivity());
    }

    public void switchFragment(baseFragment fragment, boolean addtostack) {
        ((MainActivity) getActivity()).switchFragment(fragment, addtostack);
    }
}

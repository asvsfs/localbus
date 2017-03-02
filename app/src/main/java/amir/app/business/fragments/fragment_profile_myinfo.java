package amir.app.business.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.strongloop.android.loopback.callbacks.ObjectCallback;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;

import amir.app.business.GuideApplication;
import amir.app.business.LoginActivity;
import amir.app.business.R;
import amir.app.business.RegisterActivity;
import amir.app.business.callbacks.SimpleCallback;
import amir.app.business.config;
import amir.app.business.event.ProfileRefreshEvent;
import amir.app.business.models.Businesse;
import amir.app.business.models.Customer;
import amir.app.business.models.Verification;
import amir.app.business.widget.CircleImageView;
import amir.app.business.widget.FarsiButton;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by amin on 11/02/2016.
 */

public class fragment_profile_myinfo extends baseFragment {
    @BindView(R.id.mainContent)
    View mainContent;
    @BindView(R.id.loginContent)
    View loginContent;
    @BindView(R.id.loginProgress)
    View loginProgress;

    @BindView(R.id.imgavatar)
    CircleImageView imgavatar;
    @BindView(R.id.btnlogin)
    Button btnlogin;
    @BindView(R.id.btnRegister)
    FarsiButton btnRegister;
    @BindView(R.id.txtName)
    TextView txtName;
    @BindView(R.id.editName)
    EditText editName;
    @BindView(R.id.editPhone)
    EditText editPhone;
    @BindView(R.id.editMail)
    EditText editMail;
    @BindView(R.id.btnReset)
    Button btnReset;
    @BindView(R.id.profileInfo)
    LinearLayout profileInfo;

    Customer.Repository repository = GuideApplication.getLoopBackAdapter().createRepository(Customer.Repository.class);
    Context context;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_myinfo, null);
        ButterKnife.bind(this, view);

        mainContent.setVisibility(View.GONE);
        btnlogin.setVisibility(View.GONE);

        repository = GuideApplication.getLoopBackAdapter().createRepository(Customer.Repository.class);

        if (config.customer == null)
            repository.authCheck(new SimpleCallback() {
                @Override
                public void onSuccess(String response) {
                    load_user_info();
                }

                @Override
                public void onError(Throwable t) {
                    loginProgress.setVisibility(View.GONE);
                    btnlogin.setVisibility(View.VISIBLE);
                }
            });
        else
            load_user_info();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        load_user_info();
    }

    private void load_user_info() {
        if (config.customer != null) {
            setup_info_view(config.customer);
            load_Business();
        }

        repository.findById(config.token.userId, new ObjectCallback<Customer>() {
            @Override
            public void onSuccess(Customer customer) {
                config.customer = customer;

                load_Business();

                checkVerificationId(customer.getVerificationId());

                setup_info_view(customer);
            }

            @Override
            public void onError(Throwable t) {
                //need to login again
                loginProgress.setVisibility(View.GONE);
                btnlogin.setVisibility(View.VISIBLE);
            }
        });

    }

    private void load_Business() {
        Businesse.Repository businessrepo = GuideApplication.getLoopBackAdapter().createRepository(Businesse.Repository.class);
        HashMap<String, String> param = new HashMap<>();
        param.put("customerId", config.customer.getId());

        businessrepo.findOne(param, new ObjectCallback<Businesse>() {
            @Override
            public void onSuccess(Businesse business) {
                config.Businesse = business;

                //send refresh to profile page to activel all business tabs
                EventBus.getDefault().post(new ProfileRefreshEvent(null));
            }

            @Override
            public void onError(Throwable t) {

            }
        });
    }

    private void setup_info_view(Customer customer) {
        loginProgress.setVisibility(View.GONE);

        btnlogin.setVisibility(config.token == null ? View.VISIBLE : View.GONE);
        btnRegister.setVisibility(config.token == null ? View.VISIBLE : View.GONE);
        profileInfo.setVisibility(config.token == null ? View.GONE : View.VISIBLE);

        txtName.setText(customer.getRealm());
        editMail.setText(config.customer.getEmail());
        editName.setText(config.customer.getUsername());
        editPhone.setText(config.customer.getTelegramNumber());

        mainContent.setVisibility(View.VISIBLE);
    }

    private void checkVerificationId(String verification) {
        Verification.Repository verif_repo = GuideApplication.getLoopBackAdapter().createRepository(Verification.Repository.class);

//        HashMap<String, String> param=new HashMap<>();
//        param.put("customerId", verification)
        verif_repo.findById(verification, new ObjectCallback<Verification>() {
            @Override
            public void onSuccess(Verification object) {
//                btnProductManage.setVisibility(View.VISIBLE);
//                load_tabs_fragments_list();
            }

            @Override
            public void onError(Throwable t) {

            }
        });
    }

    @OnClick(R.id.btnlogin)
    public void login() {
        getActivity().startActivityForResult(new Intent(getActivity(), LoginActivity.class), 1);
    }

    @OnClick(R.id.btnRegister)
    public void register() {
        getActivity().startActivityForResult(new Intent(getActivity(), RegisterActivity.class), 1);
    }

}

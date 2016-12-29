package amir.app.business;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.common.collect.ImmutableMap;
import com.strongloop.android.loopback.callbacks.VoidCallback;

import amir.app.business.management.activity.ProductDefine;
import amir.app.business.models.Customer;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import eu.inmite.android.lib.validations.form.annotations.NotEmpty;


public class RegisterActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @NotEmpty(messageId = R.string.emptyName)
    @BindView(R.id.editName)
    EditText editName;
    @NotEmpty(messageId = R.string.emptyUserName)
    @BindView(R.id.editusername)
    EditText editusername;
    @NotEmpty(messageId = R.string.emptyPhone)
    @BindView(R.id.editPhone)
    EditText editPhone;
    @NotEmpty(messageId = R.string.emptyMail)
    @BindView(R.id.editMail)
    EditText editMail;
    @NotEmpty(messageId = R.string.emptyPassword)
    @BindView(R.id.editPassword)
    EditText editPassword;
    @BindView(R.id.btnRegister)
    Button btnRegister;

    MaterialDialog dlg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        //config toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //setup back button on toolbar
        toolbar.setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    @OnClick(R.id.btnRegister)
    public void register() {
        if (!util.isValid(this)) {
            return;
        }

        Customer.Repository repository = GuideApplication.getLoopBackAdapter().createRepository(Customer.Repository.class);
        Customer customer = repository.createObject(ImmutableMap.of("deviceIds", "[]"));

        customer.setRealm(editName.getText().toString());
        customer.setUsername(editusername.getText().toString());
        customer.setEmail(editMail.getText().toString());
        customer.setTelegramNumber(editPhone.getText().toString());
        customer.setPassword(editPassword.getText().toString());
        dlg = util.progressDialog(this, "", "در حال ذخیره مشخصات کاربری ");

        customer.save(new VoidCallback() {
            @Override
            public void onSuccess() {
                dlg.dismiss();
                util.alertDialog(RegisterActivity.this, "بستن", "", "مشخصات عضویت شما ذخیره شد.\n لینک فعال سازی به ایمیل شما ارسال گردید\nبرای شروع ابتدا کاربری خود را فعال کنید.", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        finish();
                    }
                }, SweetAlertDialog.SUCCESS_TYPE);
            }

            @Override
            public void onError(Throwable t) {
                dlg.dismiss();
                util.alertDialog(RegisterActivity.this, "بستن", "خطا در ثبت مشخصات", "خطا", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                    }
                }, SweetAlertDialog.ERROR_TYPE);
            }
        });
    }

}


package amir.app.business;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import amir.app.business.models.Admin;
import amir.app.business.models.Token;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by amin on 08/24/2016.
 */

public class splash extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(splash.this, MainActivity.class));
                finish();

//                final Admin.Repository repository = GuideApplication.getLoopBackAdapter().createRepository(Admin.Repository.class);
//                repository.loginUser("test", "test", new Admin.LoginCallback() {
//                    @Override
//                    public void onSuccess(Token token) {
//                        GuideApplication.getLoopBackAdapter().setAccessToken(token.id);
//                        startActivity(new Intent(splash.this, MainActivity.class));
//                        finish();
//                    }
//
//                    @Override
//                    public void onError(Throwable t) {
//                        util.alertDialog(splash.this, "بستن", "خطا در ارتباط با شبکه", "خطا", new SweetAlertDialog.OnSweetClickListener() {
//                            @Override
//                            public void onClick(SweetAlertDialog sweetAlertDialog) {
//                                finish();
//                            }
//                        }, SweetAlertDialog.ERROR_TYPE);
//                    }
//                });
            }
        }, 2000);

    }
}

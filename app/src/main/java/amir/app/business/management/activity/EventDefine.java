package amir.app.business.management.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.common.collect.ImmutableMap;
import com.strongloop.android.loopback.callbacks.VoidCallback;

import amir.app.business.GuideApplication;
import amir.app.business.R;
import amir.app.business.config;
import amir.app.business.models.Event;
import amir.app.business.models.Location;
import amir.app.business.util;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import eu.inmite.android.lib.validations.form.annotations.NotEmpty;

/**
 * Created by amin on 12/06/2016.
 */

public class EventDefine extends AppCompatActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @NotEmpty(order = 1, messageId = R.string.emptyEventName)
    @BindView(R.id.editTitle)
    EditText editTitle;
    @NotEmpty(order = 1, messageId = R.string.emptyEventDesc)
    @BindView(R.id.editDesc)
    EditText editDesc;
    @BindView(R.id.btnSave)
    Button btnSave;

    MaterialDialog dlg;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.management_event_define);
        ButterKnife.bind(this);

        //config toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("ثبت رویداد جدید");
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //setup back button on toolbar
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }


    @OnClick(R.id.btnSave)
    public void save() {
        if (!util.isValid(this))
            return;

        save_event();
    }

    private void save_event() {
        dlg = util.progressDialog(this, "", "در حال ثبت رویداد");

        Event.Repository repository = GuideApplication.getLoopBackAdapter().createRepository(Event.Repository.class);
        final Event event = repository.createObject(ImmutableMap.of("title", ""));

        event.setOwnerId(config.customer.getId());
        event.setTitle(editTitle.getText().toString());
        event.setDescription(editDesc.getText().toString());
        event.setPublishDate("2017-01-27");
        event.setSeen(0);

        Location location = new Location();
        location.setLat(32.5f);
        location.setLng(51.7f);
        event.setLocation(location);

        event.save(new VoidCallback() {
            @Override
            public void onSuccess() {
                dlg.dismiss();
                util.alertDialog(EventDefine.this, "بستن", "رویداد با موفقیت ثبت شد.", "نتیجه", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        onBackPressed();
                    }
                }, SweetAlertDialog.SUCCESS_TYPE);
            }

            @Override
            public void onError(Throwable t) {
                dlg.dismiss();
                util.alertDialog(EventDefine.this, "بستن", "خطا در ثبت رویداد", "خطا", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                    }
                }, SweetAlertDialog.ERROR_TYPE);

            }
        });
    }

}

package amir.app.business.management.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jaredrummler.materialspinner.MaterialSpinner;
import com.kbeanie.imagechooser.api.ChooserType;
import com.kbeanie.imagechooser.api.ChosenImage;
import com.kbeanie.imagechooser.api.ImageChooserListener;
import com.kbeanie.imagechooser.api.ImageChooserManager;
import com.kbeanie.imagechooser.exceptions.ChooserException;
import com.strongloop.android.loopback.callbacks.ListCallback;
import com.strongloop.android.loopback.callbacks.VoidCallback;

import java.util.ArrayList;
import java.util.List;

import amir.app.business.GuideApplication;
import amir.app.business.R;
import amir.app.business.adapter.GalleryListAdapter;
import amir.app.business.models.Category;
import amir.app.business.models.Product;
import amir.app.business.util;
import amir.app.business.widget.CircleIndicator;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import eu.inmite.android.lib.validations.form.annotations.NotEmpty;

/**
 * Created by amin on 12/06/2016.
 */

public class ProductDefine extends AppCompatActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.editCode)
    TextView editCode;
    @NotEmpty(order = 1, messageId = R.string.emptyProductName)
    @BindView(R.id.editName)
    EditText editName;
    @NotEmpty(order = 1, messageId = R.string.emptyProductDesc)
    @BindView(R.id.editDesc)
    EditText editDesc;
    @BindView(R.id.categorySpinner)
    MaterialSpinner categorySpinner;
    @BindView(R.id.editPrice)
    EditText editPrice;
    @BindView(R.id.btnSave)
    Button btnSave;
    @BindView(R.id.progress)
    ProgressBar progress;
    @BindView(R.id.imagePager)
    ViewPager imagePager;
    @BindView(R.id.indicator)
    CircleIndicator indicator;

    String qrcode;

    ImageChooserManager imageChooserManager;
    List<String> category = new ArrayList<>();
    List<Category> categories = new ArrayList<>();
    List<String> images = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.management_product_define);
        ButterKnife.bind(this);

        //config toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("ثبت محصول جدید");
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //setup back button on toolbar
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        qrcode = getIntent().getExtras().getString("qrcode");
        editCode.setText(qrcode);

        initialize_ImageChooser();

        load_category_list();
    }

    private void setup_category_spinner() {
        categorySpinner.setItems(category);
        categorySpinner.setSelectedIndex(0);
    }


    private void load_category_list() {
        Category.Repository repository = GuideApplication.getLoopBackAdapter().createRepository(Category.Repository.class);

        repository.findAll(new ListCallback<Category>() {
            @Override
            public void onSuccess(List<Category> items) {
                categories = items;
                for (Category item : items) {
                    category.add(item.getName());
                }

                setup_category_spinner();
                progress.setVisibility(View.GONE);
            }

            @Override
            public void onError(Throwable t) {
                progress.setVisibility(View.GONE);

                util.alertDialog(ProductDefine.this, "بستن", "خطا در ارتباط با شبکه", "خطا", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        onBackPressed();
                    }
                }, SweetAlertDialog.ERROR_TYPE);
            }
        });
    }

    public void save() {
        if (!util.isValid(this))
            return;

        save_product();
    }

    private void save_product() {
        Product product = new Product();
        product.save(new VoidCallback() {
            @Override
            public void onSuccess() {
                util.alertDialog(ProductDefine.this, "بستن", "محصول با موفقیت ثبت شد.", "نتیجه", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        onBackPressed();
                    }
                }, SweetAlertDialog.SUCCESS_TYPE);
            }

            @Override
            public void onError(Throwable t) {
                util.alertDialog(ProductDefine.this, "بستن", "خطا در ثبت محصول", "خطا", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                    }
                }, SweetAlertDialog.ERROR_TYPE);

            }
        });
    }

    @OnClick(R.id.btnAddImage)
    public void btnAddImage() {
        try {
            imageChooserManager.choose();
        } catch (ChooserException e) {
            e.printStackTrace();
        }
    }

    private void load_product_images() {
        imagePager.setAdapter(new GalleryListAdapter(this, images, ""));
        indicator.setViewPager(imagePager);

        Display display = getWindowManager().getDefaultDisplay();
        imagePager.getLayoutParams().height = display.getWidth();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK)
            if (requestCode == ChooserType.REQUEST_PICK_PICTURE)
                imageChooserManager.submit(requestCode, data);
    }


    private void initialize_ImageChooser() {
        imageChooserManager = new ImageChooserManager(this, ChooserType.REQUEST_PICK_PICTURE, "business", true);
        imageChooserManager.setImageChooserListener(new ImageChooserListener() {
            @Override
            public void onImageChosen(final ChosenImage image) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String selectedfile = image.getFilePathOriginal();
                        images.add("file://" + selectedfile);

                        load_product_images();
                    }
                });
            }

            @Override
            public void onError(String reason) {
            }
        });

        imageChooserManager.reinitialize("");
    }
}

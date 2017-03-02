package amir.app.business.management.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.common.collect.ImmutableMap;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.kbeanie.imagechooser.api.ChooserType;
import com.kbeanie.imagechooser.api.ChosenImage;
import com.kbeanie.imagechooser.api.ImageChooserListener;
import com.kbeanie.imagechooser.api.ImageChooserManager;
import com.kbeanie.imagechooser.exceptions.ChooserException;
import com.strongloop.android.loopback.Container;
import com.strongloop.android.loopback.ContainerRepository;
import com.strongloop.android.loopback.callbacks.ListCallback;
import com.strongloop.android.loopback.callbacks.ObjectCallback;
import com.strongloop.android.loopback.callbacks.VoidCallback;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import amir.app.business.GuideApplication;
import amir.app.business.R;
import amir.app.business.SingleShotLocationProvider;
import amir.app.business.adapter.GalleryListAdapter;
import amir.app.business.config;
import amir.app.business.event.ProductListRefreshEvent;
import amir.app.business.models.Businesse;
import amir.app.business.models.Category;
import amir.app.business.models.Inventory;
import amir.app.business.models.Location;
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
    public interface completeInterface {
        void onComplete();

        void onFail();
    }

    private class image {
        public String filename;
        public String path;
    }

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
    @NotEmpty(order = 1, messageId = R.string.emptyPrice)
    EditText editPrice;
    @NotEmpty(order = 1, messageId = R.string.emptyCount)
    @BindView(R.id.editCount)
    EditText editCount;
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
    ImageChooserManager imageTakerManager;

    List<String> category = new ArrayList<>();
    List<Category> categories = new ArrayList<>();
    List<image> images = new ArrayList<>();
    MaterialDialog dlg;
    BottomSheetDialog bottomsheet;

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
        initialize_ImageTaker();

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

    @OnClick(R.id.btnSave)
    public void save() {
        if (!util.isValid(this))
            return;

        upload_images_and_save_product();
    }

    private void upload_images_and_save_product() {
        dlg = util.progressDialog(this, "ذخیره محصول", "در حال آپلود تصویر ");

        ContainerRepository container = GuideApplication.getLoopBackAdapter().createRepository(ContainerRepository.class);
        container.setAdapter(GuideApplication.getLoopBackAdapter());

        container.get("common", new ObjectCallback<Container>() {
            @Override
            public void onSuccess(Container container) {
                if (images.size() > 0)
                    upload_product_images(container, 0);
                else
                    save_product();
            }

            @Override
            public void onError(Throwable t) {
                util.alertDialog(ProductDefine.this, "بستن", "خطا در ارتباط با شبکه", "خطا", null, SweetAlertDialog.ERROR_TYPE);
            }
        });

    }

    private void save_product() {
        dlg.setContent("در حال ذخیره محصول");

        Product.Repository repository = GuideApplication.getLoopBackAdapter().createRepository(Product.Repository.class);
        final Product product = repository.createObject(ImmutableMap.of("name", ""));

//        product.setId("");
        product.setCategory(categories.get(categorySpinner.getSelectedIndex()).getId());
        product.setName(editName.getText().toString());
        product.setDescription(editDesc.getText().toString());
        product.setOwner(config.Businesse.getId());
        product.setPrice(Integer.parseInt(editPrice.getText().toString()));
        product.setQrcode(qrcode);

        SingleShotLocationProvider.GPSCoordinates gpslocation = config.lastlocation;
        if (gpslocation == null)
            gpslocation = new SingleShotLocationProvider.GPSCoordinates(0, 0);

        Location location = new Location();
        location.setLat(gpslocation.latitude);
        location.setLng(gpslocation.longitude);
        product.setLocation(location);

        Location userlocation = new Location();
        userlocation.setLat(gpslocation.latitude);
        userlocation.setLng(gpslocation.longitude);
        product.setUserlocation(userlocation);

        List<String> _images = new ArrayList<>();
        for (image img : images) {
            _images.add(img.filename);
        }
        product.setImages(_images);

        product.save(new VoidCallback() {
            @Override
            public void onSuccess() {
                EventBus.getDefault().post(new ProductListRefreshEvent(product.getName() + " به لیست محصولات اضافه شد."));

                dlg.setContent("در حال تعیین موجودی اولیه...");
                int count = Integer.parseInt(editCount.getText().toString());
                add_to_product(product.getId().toString(), count, new completeInterface() {
                    @Override
                    public void onComplete() {
                        dlg.dismiss();

                        util.alertDialog(ProductDefine.this, "بستن", "محصول با موفقیت ثبت شد.", "نتیجه", new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                onBackPressed();
                            }
                        }, SweetAlertDialog.SUCCESS_TYPE);
                    }

                    @Override
                    public void onFail() {
                        util.alertDialog(ProductDefine.this, "بستن", "خطا در تعیین مقدار محصول", SweetAlertDialog.ERROR_TYPE);
                    }
                });
            }

            @Override
            public void onError(Throwable t) {
                dlg.dismiss();
                util.alertDialog(ProductDefine.this, "بستن", "خطا در ثبت محصول", "خطا", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                    }
                }, SweetAlertDialog.ERROR_TYPE);

            }
        });
    }

    private void upload_product_images(final Container container, final int indexOfFile) {
        dlg.setContent("در حال آپلود تصویر " + (indexOfFile + 1) + " از " + images.size());

//        if (!images.get(indexOfFile).equals("")) {
//            if (indexOfFile < images.size())
//                upload_product_images(container, indexOfFile);
//            else
//                save_product();
//            return;
//        }

        container.upload(new File(images.get(indexOfFile).path), new ObjectCallback<com.strongloop.android.loopback.File>() {
            @Override
            public void onSuccess(com.strongloop.android.loopback.File object) {
                images.get(indexOfFile).filename = object.getName();
                if (indexOfFile + 1 < images.size())
                    upload_product_images(container, indexOfFile + 1);
                else
                    save_product();
            }

            @Override
            public void onError(Throwable t) {
                dlg.dismiss();
                util.alertDialog(ProductDefine.this, "error in upload", SweetAlertDialog.SUCCESS_TYPE);
            }
        });
    }

    @OnClick(R.id.btnAddImage)
    public void btnAddImage() {
        View view = getLayoutInflater().inflate(R.layout.management_product_define_imagechoose, null);
        view.findViewById(R.id.txtgallery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    bottomsheet.dismiss();
                    imageChooserManager.choose();
                } catch (ChooserException e) {
                    e.printStackTrace();
                }

            }
        });
        view.findViewById(R.id.txtcamera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    bottomsheet.dismiss();
                    imageTakerManager.choose();
                } catch (ChooserException e) {
                    e.printStackTrace();
                }

            }
        });

        bottomsheet = new BottomSheetDialog(this);
        bottomsheet.setContentView(view);
        bottomsheet.show();

    }

    private void load_product_images() {
        List<String> gallery = new ArrayList<>();
        for (image img : images) {
            gallery.add(img.path);
        }

        imagePager.setAdapter(new GalleryListAdapter(this, gallery, ""));
        indicator.setViewPager(imagePager);

        Display display = getWindowManager().getDefaultDisplay();
        imagePager.getLayoutParams().height = display.getWidth();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK)
            if (requestCode == ChooserType.REQUEST_PICK_PICTURE)
                imageChooserManager.submit(requestCode, data);
            else if (requestCode == ChooserType.REQUEST_CAPTURE_PICTURE)
                imageTakerManager.submit(requestCode, data);

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
                        image img = new image();
                        img.path = selectedfile;
                        images.add(img);

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

    private void initialize_ImageTaker() {
        imageTakerManager = new ImageChooserManager(this, ChooserType.REQUEST_CAPTURE_PICTURE, "business", true);
        imageTakerManager.setImageChooserListener(new ImageChooserListener() {
            @Override
            public void onImageChosen(final ChosenImage image) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String selectedfile = image.getFilePathOriginal();
                        image img = new image();
                        img.path = selectedfile;
                        images.add(img);

                        load_product_images();
                    }
                });
            }

            @Override
            public void onError(String reason) {

            }
        });

        imageTakerManager.reinitialize("");
    }

    private void add_to_product(final String productid, int amount, final completeInterface event) {
        Inventory.Repository repository = GuideApplication.getLoopBackAdapter().createRepository(Inventory.Repository.class);
        final Inventory inventory = repository.createObject(ImmutableMap.of("productId", productid));
        inventory.setAmount(amount);
        inventory.setDate("2017-02-21");
        inventory.setUserId(config.customer.getId());
        inventory.save(new VoidCallback() {
            @Override
            public void onSuccess() {
                event.onComplete();
            }

            @Override
            public void onError(Throwable t) {
                event.onFail();

            }
        });

    }
}

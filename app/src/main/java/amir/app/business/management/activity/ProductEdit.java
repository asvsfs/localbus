package amir.app.business.management.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Display;
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
import java.util.Locale;

import amir.app.business.GuideApplication;
import amir.app.business.R;
import amir.app.business.SingleShotLocationProvider;
import amir.app.business.adapter.GalleryListAdapter;
import amir.app.business.config;
import amir.app.business.event.ProductListRefreshEvent;
import amir.app.business.models.Businesse;
import amir.app.business.models.Category;
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

public class ProductEdit extends AppCompatActivity {
    private class image {
        public String filename;
        public String path;
        public boolean added;
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
    EditText editPrice;
    @BindView(R.id.btnSave)
    Button btnSave;
    @BindView(R.id.progress)
    ProgressBar progress;
    @BindView(R.id.imagePager)
    ViewPager imagePager;
    @BindView(R.id.indicator)
    CircleIndicator indicator;
    @BindView(R.id.btnDelImage)
    Button btnDelImage;

    Product product;
    String productid;

    ImageChooserManager imageChooserManager;
    ImageChooserManager imageTakerManager;

    List<String> category = new ArrayList<>();
    List<Category> categories = new ArrayList<>();
    List<image> images = new ArrayList<>();
    MaterialDialog dlg;
    BottomSheetDialog bottomsheet;

    Product.Repository repository = GuideApplication.getLoopBackAdapter().createRepository(Product.Repository.class);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.management_product_define);
        ButterKnife.bind(this);

        //config toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("ویرایش محصول");
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //setup back button on toolbar
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

//        product = (Product) getIntent().getExtras().getSerializable("product");
        productid = getIntent().getExtras().getString("productid");

        load_product();

        initialize_ImageChooser();
        initialize_ImageTaker();
    }

    private void setup_product_info() {
        editName.setText(product.getName());
        editDesc.setText(product.getDescription());
        editCode.setText(product.getQrcode());
        editPrice.setText(String.format(Locale.ENGLISH, "%d", product.getPrice()));

        for (String filename : product.getImages()) {
            image img = new image();
            img.path = filename;

            images.add(img);

        }
    }

    private void load_product() {
        repository.getById(productid, new ObjectCallback<Product>() {
            @Override
            public void onSuccess(Product object) {
                product = object;

                setup_product_info();
                load_product_images();
                load_category_list();
            }

            @Override
            public void onError(Throwable t) {

            }
        });
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
                int category_index = 0;
                categories = items;
                for (int i = 0; i < items.size(); i++) {
                    category.add(items.get(i).getName());
                    if (items.get(i).getId().equals(product.getCategory()))
                        category_index = i;
                }

                setup_category_spinner();
                progress.setVisibility(View.GONE);


                categorySpinner.setSelectedIndex(category_index);
            }

            @Override
            public void onError(Throwable t) {
                progress.setVisibility(View.GONE);

                util.alertDialog(ProductEdit.this, "بستن", "خطا در ارتباط با شبکه", "خطا", new SweetAlertDialog.OnSweetClickListener() {
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
        boolean newexist = false;
        for (image image : images) {
            if (image.added)
                newexist = true;
        }

        if (!newexist) {
            save_product();
            return;
        }


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
                util.alertDialog(ProductEdit.this, "بستن", "خطا در ارتباط با شبکه", "خطا", null, SweetAlertDialog.ERROR_TYPE);
            }
        });

    }

    private void save_product() {
        if (dlg == null)
            dlg = util.progressDialog(this, "ذخیره محصول", "");

        dlg.setContent("در حال ذخیره محصول");

        product.setCategory(categories.get(categorySpinner.getSelectedIndex()).getId());
        product.setName(editName.getText().toString());
        product.setDescription(editDesc.getText().toString());
        product.setOwner(config.Businesse.getId());
        product.setPrice(Integer.parseInt(editPrice.getText().toString()));
        product.setQrcode(product.getQrcode());

        SingleShotLocationProvider.GPSCoordinates gpslocation = config.lastlocation;
        if (gpslocation == null)
            gpslocation = new SingleShotLocationProvider.GPSCoordinates(0, 0);

        Location location = new Location();
        location.setLat(gpslocation.latitude);
        location.setLng(gpslocation.longitude);
        product.setLocation(location);

        Location userlocation = new Location();
        location.setLat(gpslocation.latitude);
        location.setLng(gpslocation.longitude);
        product.setUserlocation(userlocation);

        List<String> _images = new ArrayList<>();
        for (image img : images) {
            if(img.added)
                _images.add(img.filename);
            else
                _images.add(img.path);
        }
        product.setImages(_images);

        product.save(new VoidCallback() {
            @Override
            public void onSuccess() {
                EventBus.getDefault().post(new ProductListRefreshEvent(product.getName() + " به لیست محصولات اضافه شد."));

                dlg.dismiss();
                util.alertDialog(ProductEdit.this, "بستن", "محصول با موفقیت ثبت شد.", "نتیجه", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        onBackPressed();
                    }
                }, SweetAlertDialog.SUCCESS_TYPE);
            }

            @Override
            public void onError(Throwable t) {
                dlg.dismiss();
                util.alertDialog(ProductEdit.this, "بستن", "خطا در ثبت محصول", "خطا", new SweetAlertDialog.OnSweetClickListener() {
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

        //new image added, upload new images only
        if (images.get(indexOfFile).added)
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
                    util.alertDialog(ProductEdit.this, "error in upload", SweetAlertDialog.SUCCESS_TYPE);
                }
            });

        else if (indexOfFile + 1 < images.size())
            upload_product_images(container, indexOfFile + 1);
        else
            save_product();

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

    @OnClick(R.id.btnDelImage)
    public void btnDelImage() {
        images.remove(imagePager.getCurrentItem());
        load_product_images();
    }

    private void load_product_images() {
        List<String> gallery = new ArrayList<>();
        for (image img : images) {
            gallery.add(img.path);
        }

        imagePager.setAdapter(new GalleryListAdapter(this, gallery, null));
        indicator.setViewPager(imagePager);

        Display display = getWindowManager().getDefaultDisplay();
        imagePager.getLayoutParams().height = display.getWidth();

        btnDelImage.setVisibility(images.size() > 0 ? View.VISIBLE : View.GONE);
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
                        img.added = true;

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
                        img.added = true;

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
}

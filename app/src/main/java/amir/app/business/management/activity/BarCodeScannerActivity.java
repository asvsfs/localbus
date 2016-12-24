package amir.app.business.management.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

import amir.app.business.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import me.dm7.barcodescanner.zbar.BarcodeFormat;
import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;

/**
 * Created by amin on 12/19/2016.
 */

public class BarCodeScannerActivity extends AppCompatActivity implements ZBarScannerView.ResultHandler {

    @BindView(R.id.content_frame)
    FrameLayout contentFrame;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    ZBarScannerView mScannerView;
    ArrayList<Integer> mSelectedIndices;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.management_barcode_scanner);
        ButterKnife.bind(this);

        //config toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("اسکن بارکد");
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //setup back button on toolbar
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mScannerView.stopCamera();
                onBackPressed();
            }
        });

        scanBarCode();
    }

    public void setupFormats() {
        List<BarcodeFormat> formats = new ArrayList<BarcodeFormat>();
        if (mSelectedIndices == null || mSelectedIndices.isEmpty()) {
            mSelectedIndices = new ArrayList<Integer>();
            for (int i = 0; i < BarcodeFormat.ALL_FORMATS.size(); i++) {
                mSelectedIndices.add(i);
            }
        }

        for (int index : mSelectedIndices) {
            formats.add(BarcodeFormat.ALL_FORMATS.get(index));
        }
        if (mScannerView != null) {
            mScannerView.setFormats(formats);
        }
    }

    private void scanBarCode() {
        mScannerView = new ZBarScannerView(this);    // Programmatically initialize the scanner view
        mScannerView.setAutoFocus(true);
        setupFormats();

        contentFrame.addView(mScannerView);
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();                  // Start camera on resume
    }

    @Override
    public void handleResult(Result result) {
        Intent data = new Intent();
        data.putExtra("content", result.getContents());
        setResult(RESULT_OK, data);
        finish();
    }
}

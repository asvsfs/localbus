package amir.app.business;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.support.design.widget.TabLayout;
import android.text.Html;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;

import amir.app.business.widget.FarsiTextView;
import amir.app.business.widget.widgettools;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by amin on 17/05/2015.
 */
public class util {

    public static int getbestcols(Context context) {
        float scalefactor = context.getResources().getDisplayMetrics().density * 120;
        int number = ((Activity) context).getWindowManager().getDefaultDisplay().getWidth();
        return (int) ((float) number / (float) scalefactor);
    }

    public static Spanned getHighLightText(String title, String text) {
        String html = "<font color='#ffff00'>" + title + "</font>";
        if (text != null)
            html += text;

        return Html.fromHtml(html);
    }

    public static void hidekeyboard(Context context, TextView v) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    public static boolean isNetworkAvailable(Context context) {
        return ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() != null;
    }

    public static void alerdialog(Context context, String title, int type) {
        alerdialog(context, "بستن", title, "", null, type);
    }

    public static void alerdialog(Context context, String confirmtext, String title, int type) {
        alerdialog(context, confirmtext, title, "", null, type);
    }

    public static void alerdialog(Context context, String confirmtext, String title, String content, final SweetAlertDialog.OnSweetClickListener listener, int type) {
        try {
            new SweetAlertDialog(context, type)
                    .setContentText(content)
                    .setTitleText(title)
                    .setConfirmText(confirmtext)
                    .showCancelButton(false)
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            if (listener != null)
                                listener.onClick(sweetAlertDialog);
                            sweetAlertDialog.dismiss();
                        }
                    })
                    .show();
        } catch (Exception ex) {
        }
    }

    public static void confirmdialog(Context context, String oktext, String canceltext, String title, String content, final SweetAlertDialog.OnSweetClickListener oklistener, int type) {
        new SweetAlertDialog(context, type)
                .setContentText(content)
                .setTitleText(title)
                .setConfirmText(oktext)
                .setCancelText(canceltext)
                .showCancelButton(true)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        if (oklistener != null)
                            oklistener.onClick(sweetAlertDialog);
                        sweetAlertDialog.dismiss();
                    }
                })
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismiss();
                    }
                })
                .show();
    }

    public static int dpToPx(Context context, int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    public static void adjustResize(Activity activity) {
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    public static void adjustPan(Activity activity) {
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = listView.getPaddingTop() + listView.getPaddingBottom();
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            if (listItem instanceof ViewGroup) {
                listItem.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.WRAP_CONTENT, AbsListView.LayoutParams.WRAP_CONTENT));
            }
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }


    public static MaterialDialog progressdialog(Context context, String title, String content) {
        return progressdialog(context, title, content, null);
    }


    public static MaterialDialog progressdialog(Context context, String title, String content, final View.OnClickListener onClickListener) {
        MaterialDialog dlg = new MaterialDialog.Builder(context)
                .title(title)
                .titleGravity(GravityEnum.END)
                .contentGravity(GravityEnum.END)
                .typeface(widgettools.typeface(context, 2), widgettools.typeface(context, 4))
                .cancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        if (onClickListener != null)
                            onClickListener.onClick(null);
                    }
                })
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        if (onClickListener != null)
                            onClickListener.onClick(dialog.getView());

                        super.onPositive(dialog);
                    }
                })
                .content(content)
                .cancelable(false)
                .positiveText("بستن")
                .progress(true, 0)
                .theme(Theme.LIGHT)
                .show();

        return dlg;
    }

    public static MaterialDialog contentdialog(Context context, View contentview, String title, String positiveText, final View.OnClickListener onClickListener) {
        MaterialDialog dlg = new MaterialDialog.Builder(context)
                .title(title)
                .titleGravity(GravityEnum.END)
                .contentGravity(GravityEnum.END)
                .typeface(widgettools.typeface(context, 2), widgettools.typeface(context, 4))
                .customView(contentview, false)
                .cancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        if (onClickListener != null)
                            onClickListener.onClick(null);
                    }
                })
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        if (onClickListener != null)
                            onClickListener.onClick(dialog.getView());

//                        super.onPositive(dialog);
                    }
                })
                .cancelable(true)
                .negativeText("انصراف")
                .positiveText(positiveText)
                .theme(Theme.LIGHT)
                .show();

        return dlg;
    }

    public static void set_tab_font(Context context, TabLayout tabLayout) {
        Typeface tf = widgettools.typeface(context, 0);

        ViewGroup vg = (ViewGroup) tabLayout.getChildAt(0);
        int tabsCount = vg.getChildCount();
        for (int j = 0; j < tabsCount; j++) {
            ViewGroup vgTab = (ViewGroup) vg.getChildAt(j);
            int tabChildsCount = vgTab.getChildCount();
            for (int i = 0; i < tabChildsCount; i++) {
                View tabViewChild = vgTab.getChildAt(i);
                if (tabViewChild instanceof TextView) {
                    ((TextView) tabViewChild).setTypeface(tf);
                }
            }
        }
    }


}

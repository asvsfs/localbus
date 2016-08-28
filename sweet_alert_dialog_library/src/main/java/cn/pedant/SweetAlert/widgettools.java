package cn.pedant.SweetAlert;

import android.content.Context;
import android.graphics.Typeface;

public class widgettools {
    private static Typeface[] _typeface;

    private static String[] fontname = new String[]{"Yekan.ttf", "Yekan.ttf"};

    public static Typeface typeface(Context context, int id, boolean isbold) {
        if (_typeface == null) {
            _typeface = new Typeface[fontname.length];
        }

        if (_typeface[id] == null) {
            _typeface[0] = Typeface.createFromAsset(context.getAssets(), "fonts/" + fontname[0]);
            _typeface[1] = Typeface.createFromAsset(context.getAssets(), "fonts/" + fontname[1]);
        }

//        Log.i("bold", Boolean.toString(isbold));
        return _typeface[1];
    }

    public static int dp2px(Context context, int dp) { // فانکشن تبدیل واحد
        // دنسیته به پیکسل
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

}

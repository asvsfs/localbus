package amir.app.business.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;

public class widgettools {
    private static Typeface[] _typeface;
    private static String[] fontname = new String[]{"IRANSansBold.ttf", "IRANSansLight.ttf",
            "IRANSansMedium.ttf", "IRANSansUltraLight.ttf",
            "IRANSans.ttf"};

    public static Typeface typeface(Context context, int id) {
        if (_typeface == null) {
            _typeface = new Typeface[fontname.length];
        }

        if (_typeface[id] == null) {
            _typeface[0] = Typeface.createFromAsset(context.getAssets(), "fonts/" + fontname[0]);
            _typeface[1] = Typeface.createFromAsset(context.getAssets(), "fonts/" + fontname[1]);
            _typeface[2] = Typeface.createFromAsset(context.getAssets(), "fonts/" + fontname[2]);
            _typeface[3] = Typeface.createFromAsset(context.getAssets(), "fonts/" + fontname[3]);
        }

//        Log.i("bold", Boolean.toString(isbold));
//        return _typeface[id];
        return _typeface[id];
    }


    public static int dp2px(Context context, int dp) { // فانکشن تبدیل واحد
        // دنسیته به پیکسل
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

}

package cn.pedant.SweetAlert;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class FarsiTextView extends TextView {
    private Context _context;
    private int fontid;

    public enum FontName {
        Default(0),
        Yekan(1);

        int _fontid;

        FontName(int i) {
            _fontid = i;
        }

        public int getValue() {
            return _fontid;
        }
    }


    public void setfontid(FontName name) {
        fontid = name.getValue();
//        rebuildfont();
    }

    public FarsiTextView(Context context) {
        super(context);
        fontid = 0;
        _context = context;
//        rebuildfont();
    }

    public FarsiTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        _context = context;
    }

    @Override
    public void setTypeface(Typeface tf, int style) {

        super.setTypeface(widgettools.typeface(getContext(), fontid, tf != null ? tf.isBold() : false));

    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, type);
    }

    public FarsiTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        _context = context;
//        rebuildfont();
    }

//    private void rebuildfont() {
//        if (isInEditMode())
//            return;
//
//        if (fontid != 0)
//            setTypeface(widgettools.typeface(_context, fontid, getTypeface().isBold()));
//    }

}

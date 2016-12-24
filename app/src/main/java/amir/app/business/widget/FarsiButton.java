package amir.app.business.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.TextView;

import amir.app.business.R;

public class FarsiButton extends TextView {
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

    public FarsiButton(Context context) {
        super(context);
        _context = context;
//        rebuildfont();
    }

    public FarsiButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        _context = context;

        TypedArray a = _context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.FarsiTextView, 0, 0);

        try {
            fontid = a.getInt(R.styleable.FarsiTextView_FarsiTypeface, 0);
//            Log.i("font", "font id:" + fontid);
        } finally {
            a.recycle();
        }

        setTypeface(widgettools.typeface(getContext(), fontid));
//                setTypeface(getTypeface(), attrs.getStyleAttribute());
//        rebuildfont();
    }

    @Override
    public void setTypeface(Typeface tf, int style) {
//        Log.i("font", "font face id:" + fontid);
        super.setTypeface(widgettools.typeface(getContext(), fontid));
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, type);
    }

    public FarsiButton(Context context, AttributeSet attrs, int defStyle) {
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

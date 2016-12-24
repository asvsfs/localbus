package cn.pedant.SweetAlert;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

public class FarsiButton extends Button {

    private Context _context;
    private static int _fontid;

    public enum FontName {
        Default(0),
        Yekan(1);

        FontName(int i) {
            _fontid = i;
        }

        public int getValue() {
            return _fontid;
        }
    }


    public void setfontid(int fontid) {
        _fontid = fontid;
//        rebuildfont();
    }

    public FarsiButton(Context context) {
        super(context);
        _context = context;
        _fontid = 0;
//        rebuildfont();

    }

    public FarsiButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        _context = context;
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

    @Override
    public void setTypeface(Typeface tf, int style) {
        super.setTypeface(widgettools.typeface(getContext(), _fontid, tf != null ? tf.isBold() : false));

    }
//    private void rebuildfont() {
//        if (isInEditMode())
//            return;
//        if (_fontid != 0)
//            setTypeface(widgettools.typeface(_context, _fontid, getTypeface().isBold()));
//
//        this.setIncludeFontPadding(false);
//
//    }

    @Override
    protected void onDraw(Canvas canvas) {
//        rebuildfont();
        super.onDraw(canvas);
    }

}

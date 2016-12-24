package amir.app.business.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.CheckBox;

import amir.app.business.R;

public class FarsiCheckBox extends CheckBox {

    private Context _context;
    private static int _fontid;

    public enum FontName {
        Default(0),
        Yekan(1);

        FontName (int i)
        {
            _fontid = i;
        }

        public int getValue()
        {
            return _fontid;
        }
    }


    public void setfontid(int fontid) {
        _fontid = fontid;
        rebuildfont();
    }

    public FarsiCheckBox(Context context) {
        super(context);
        _context=context;
        rebuildfont();

    }

    public FarsiCheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        _context = context;
        TypedArray a = _context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.FarsiButton, 0, 0);

        try {
            _fontid = a.getInt(R.styleable.FarsiButton_FarsiTypeface, 1);
        } finally {
            a.recycle();
        }
        rebuildfont();

    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, type);
    }

    public FarsiCheckBox(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        _context = context;
        rebuildfont();
    }

    private void rebuildfont() {
        if (isInEditMode())
            return;
        if (_fontid != 0)
            setTypeface(widgettools.typeface(_context, _fontid));

        this.setIncludeFontPadding(false);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        rebuildfont();
        super.onDraw(canvas);
    }

}

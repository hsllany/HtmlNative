package com.mozz.htmlnative.common;

import android.support.annotation.IntDef;
import android.util.TypedValue;

import com.mozz.htmlnative.utils.ParametersUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author Yang Tao, 17/3/30.
 */

public class PixelValue {

    public static final PixelValue ZERO = new PixelValue(0, TypedValue.COMPLEX_UNIT_PX);

    public static final int EM = -2;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({TypedValue.COMPLEX_UNIT_PX, TypedValue.COMPLEX_UNIT_DIP, TypedValue.COMPLEX_UNIT_SP,
            EM})
    public @interface PixelUnit {
    }

    private final float value;
    private final int unit;

    public PixelValue(float pxValue) {
        this(pxValue, TypedValue.COMPLEX_UNIT_PX);
    }

    public PixelValue(float value, @PixelUnit int unit) {
        if (unit == EM) {
            this.value = value * 16;
            this.unit = TypedValue.COMPLEX_UNIT_PX;
        } else {
            this.value = value;
            this.unit = unit;
        }
    }

    @PixelUnit
    public int getUnit() {
        return unit;
    }

    @Override
    public String toString() {
        return this.value + " " + this.unit;
    }


    public final float getEmValue() {
        return this.getPxValue() / 16.f;
    }

    public final float getPxValue() {
        switch (unit) {
            case EM:
                return this.value / 16.f;
            case TypedValue.COMPLEX_UNIT_PX:
                return this.value;
            case TypedValue.COMPLEX_UNIT_SP:
                return ParametersUtils.spToPx(this.value);
            case TypedValue.COMPLEX_UNIT_DIP:
                return ParametersUtils.dpToPx(this.value);
            default:
                return value;
        }
    }

    public final float getDpValue() {
        return ParametersUtils.pxToDp(getPxValue());
    }

    public final float getSpValue() {
        return ParametersUtils.pxToSp(getPxValue());
    }
}

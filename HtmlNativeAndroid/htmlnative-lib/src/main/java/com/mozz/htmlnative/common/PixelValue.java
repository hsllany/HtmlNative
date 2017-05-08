package com.mozz.htmlnative.common;

import android.support.annotation.IntDef;
import android.util.TypedValue;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author Yang Tao, 17/3/30.
 */

public class PixelValue {

    public static final int UNSET = -1;

    public static final int EM = -2;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({TypedValue.COMPLEX_UNIT_PX, TypedValue.COMPLEX_UNIT_DIP, TypedValue.COMPLEX_UNIT_SP,
            UNSET, EM})
    public @interface PixelUnit {
    }

    private double value;
    private int unit = UNSET;


    public PixelValue(int value, @PixelUnit int unit) {
        if (unit == EM) {
            this.value = value * 16;
            this.unit = TypedValue.COMPLEX_UNIT_PX;
        } else {
            this.value = value;
            this.unit = unit;
        }
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

    public double getValue() {
        return value;
    }

    @PixelUnit
    public int getUnit() {
        return unit;
    }

    @Override
    public String toString() {
        return this.value + " " + this.unit;
    }


    public final double getEmValue() {
        switch (unit) {
            case EM:
                return this.value;
            case TypedValue.COMPLEX_UNIT_PX:
                return this.value / 16.f;
            case TypedValue.COMPLEX_UNIT_DIP:
                return ParametersUtils.dpToPx((float) this.value);
            default:
                return value;
        }
    }

    public final double getPxValue() {
        switch (unit) {
            case EM:
                return this.value / 16;
            case TypedValue.COMPLEX_UNIT_PX:
                return this.value;
            case TypedValue.COMPLEX_UNIT_DIP:
                return ParametersUtils.pxToDp((float) this.value);
            default:
                return value;
        }
    }
}

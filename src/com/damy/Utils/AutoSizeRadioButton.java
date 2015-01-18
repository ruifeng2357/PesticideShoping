package com.damy.Utils;

import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.RadioButton;

public class AutoSizeRadioButton extends RadioButton{
	// Minimum size of the text in pixels
    private static final int DEFAULT_MIN_TEXT_SIZE = 8; //sp
    // How precise we want to be when reaching the target textWidth size
    
    // Attributes
    private float mMinTextSize;
    private float mMaxTextSize;
    private float mOrgTextSize;
    
    private final float LargeFontSize = 22.0f;
    private final float NormalFontSize = 14.0f;
    private final float SmallFontSize = 12.0f;
    
	public AutoSizeRadioButton(Context context) {
        super(context);
        init();
    }

    public AutoSizeRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mMinTextSize = DEFAULT_MIN_TEXT_SIZE;
        mMaxTextSize = getScaledTextSize();
        refitTextSize();
    }
    
    /**
     * calc scaled text size by screen resolution
     * @return font size
     */
    private float getScaledTextSize()
    {
    	float fFontSize = 0.0f;
    	Context context = getContext();
    	
    	if ((context.getResources().getConfiguration().screenLayout &
    			Configuration.SCREENLAYOUT_SIZE_LARGE) ==
    			Configuration.SCREENLAYOUT_SIZE_LARGE){
    		// Screen layout large
    		fFontSize = LargeFontSize;
    	} else if ((context.getResources().getConfiguration().screenLayout &
        			Configuration.SCREENLAYOUT_SIZE_NORMAL) ==
        			Configuration.SCREENLAYOUT_SIZE_NORMAL){
    		// Screen layout medium
    		fFontSize = NormalFontSize;
    	} else {
    		// Screen layout small
    		fFontSize = SmallFontSize;
    	}
    	
    	return fFontSize;
    }
    
    /**
     * refit text size
     */
    private void refitTextSize() {
        float newTextSize = recalcOrgTextSize();
        
        if (newTextSize < mMinTextSize) {
            newTextSize = mMinTextSize;
        }
        
        setTextSize(TypedValue.COMPLEX_UNIT_SP, newTextSize);
    }
    
    /**
     * recalc original text size : add offset value
     * @return
     */
    private float recalcOrgTextSize()
    {
    	// get original text size
        mOrgTextSize = getTextSize();
        // convert text size
        mOrgTextSize = pixelsToSp(mOrgTextSize);
        
        return mOrgTextSize;
    }
    
    /**
     * convert pixel value to sp value
     * @param px [in], value to be convert
     * @return convert value
     */
    private float pixelsToSp(Float px) {
    	Context context = getContext();
        float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
        return px/scaledDensity;
    }
    
    @Override
    public void setTextSize(int unit, float size)
    {
    	float newTextSize = mMaxTextSize;
        
    	if (unit == TypedValue.COMPLEX_UNIT_SP) {
            // add offset value
    		if (mMaxTextSize >= NormalFontSize) {
                newTextSize += size - NormalFontSize;    			
    		} else {
                newTextSize = size; // don't add offset : small
    		}
        	super.setTextSize(unit, newTextSize);
    	} else {
    		super.setTextSize(unit, size);
    	}
    }
}

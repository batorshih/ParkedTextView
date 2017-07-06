package com.goka.parkedtextview;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;

/**
 * Created by katsuyagoto on 15/07/22.
 */
public class ParkedTextView extends android.support.v7.widget.AppCompatEditText {

    private static final String TAG = ParkedTextView.class.getSimpleName();
    private static final String DEFAULT_TEXT_COLOR = "FFFFFF";

    // Able to set
    private String mParkedText = "";
    private String mHintText = "";
    private boolean mIsBoldParkedText = true;
    private boolean mIsParkedInFront = false;
    private String mParkedTextColor = DEFAULT_TEXT_COLOR;
    private String mParkedHintColor = DEFAULT_TEXT_COLOR;

    // Unable to set
    private String mText = null;

    public ParkedTextView(Context context) {
        super(context);
        init();
    }

    public ParkedTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ParkedTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ParkedTextView, defStyleAttr, 0);

        mIsParkedInFront = a.getBoolean(R.styleable.ParkedTextView_parkedInFront, false);

        mParkedText = a.getString(R.styleable.ParkedTextView_parkedText);
        if (mParkedText == null) {
            mParkedText = "";
        }

        mParkedTextColor = a.getString(R.styleable.ParkedTextView_parkedTextColor);
        if (mParkedTextColor == null) {
            mParkedTextColor = ParkedTextView.DEFAULT_TEXT_COLOR;
        }

        mParkedHintColor = a.getString(R.styleable.ParkedTextView_parkedHintColor);
        if (mParkedHintColor == null) {
            mParkedHintColor = ParkedTextView.DEFAULT_TEXT_COLOR;
        }

        mIsBoldParkedText = a.getBoolean(R.styleable.ParkedTextView_parkedTextBold, true);

        setHintText(a.getString(R.styleable.ParkedTextView_parkedHint));

        init();

        a.recycle();

        setEnabled(true);
        setFocusable(true);
        setFocusableInTouchMode(true);
    }

    private void init() {
        mText = "";
        addTextChangedListener(new ParkedTextViewWatcher(this));
    }

    public void setHintText(String hint){
        mHintText = hint;
        if(mHintText == null)
            mHintText = "";
        setPlaceholderText();
    }

    public String getHintText(){
        return mHintText;
    }

    public String getParkedText() {
        return mParkedText;
    }

    public void setParkedText(String parkedText) {
        String typed;
        if(!TextUtils.isEmpty(mText)){
            if(mIsParkedInFront) {
                typed = mText.substring(getParkedText().length());
                mText = getParkedText() + typed;
            }else{
                typed = mText.substring(0, getBeginningPositionOfParkedText());
                mText = typed + getParkedText();
            }
            mParkedText = parkedText;
        }else{
            mParkedText = parkedText;
        }
    }

    public String getTypedText() {
        if(!mIsParkedInFront && mText.endsWith(mParkedText)){
            return mText.substring(0, getBeginningPositionOfParkedText());
        }else if(mIsParkedInFront && mText.startsWith(mText) && !mText.isEmpty())
            return mText.substring(getParkedText().length());
        else
            return mText;
    }

    public void setTypedText(String typedText) {
        textChanged(typedText);
    }


    private int getBeginningPositionOfParkedText() {
        int position = mText.length() - getParkedText().length();
        if (position < 0) {
            return 0;
        }
        return position;
    }

    private void goToBeginningOfParkedText() {
        setSelection(getBeginningPositionOfParkedText());
    }

    private void goToEndOfParkedText() {
        setSelection(mText.length());
    }

    private void setEmptyText() {
        setTypedText("");
    }

    public void setPlaceholderText() {
        Spanned hint;
        String parkedTextColor = reformatColor(mParkedTextColor);
        String parkedHintColor = reformatColor(mParkedHintColor);
        if(!getHintText().isEmpty()) {
            if (mIsParkedInFront)
                if (mIsBoldParkedText) {
                    hint = fromHtml(String.format("<font color=\"#%s\"><b>%s</b></font><font color=\"#%s\">%s</font>", parkedTextColor, getParkedText(), parkedHintColor, getHintText()));
                } else {
                    hint = fromHtml(String.format("<font color=\"#%s\">%s</font><font color=\"#%s\">%s</font>", parkedTextColor, getParkedText(), parkedHintColor, getHintText()));
                }
            else if (mIsBoldParkedText) {
                hint = fromHtml(String.format("<font color=\"#%s\">%s</font><font color=\"#%s\"><b>%s</b></font>", parkedHintColor, getHintText(), parkedTextColor, getParkedText()));
            } else {
                hint = fromHtml(String.format("<font color=\"#%s\">%s</font><font color=\"#%s\">%s</font>", parkedHintColor, getHintText(), parkedTextColor, getParkedText()));
            }
            super.setHint(hint);
        }
    }

    private String reformatColor(String color) {
        if (color.startsWith("#")) {
            color = color.substring(1);
        }

        if (color.length() > 6) {
            return color.substring(2);
        }
        return color;
    }

    private Spanned getHtmlText() {
        String parkedTextColor = reformatColor(mParkedTextColor);
        if(mIsParkedInFront){
            if (mIsBoldParkedText) {
                return fromHtml(
                        String.format(
                                "<font color=\"#%s\"><b>%s</b></font><font color=\"#%s\">%s</font>",
                                parkedTextColor, getParkedText(), parkedTextColor, getTypedText()));
            }
            return fromHtml(
                    String.format("<font color=\"#%s\">%s</font>", parkedTextColor,
                            getParkedText() + getTypedText()));
        }else{
            if (mIsBoldParkedText) {
                return fromHtml(
                        String.format(
                                "<font color=\"#%s\">%s</font><font color=\"#%s\"><b>%s</b></font>",
                                parkedTextColor, getTypedText(), parkedTextColor, getParkedText()));
            }
            return fromHtml(
                    String.format("<font color=\"#%s\">%s</font>", parkedTextColor,
                            getTypedText() + getParkedText()));
        }
    }

    private void textChanged(String typed) {
        if(typed.length() <= getParkedText().length() && !mText.isEmpty()){
            mText = getParkedText();
        }else{
            if(mIsParkedInFront) {
                if(typed.startsWith(getParkedText()))
                    mText = typed;
                else
                    mText = getParkedText() + typed;
            }else{
                if(typed.endsWith(getParkedText()))
                    mText = typed;
                else
                    mText = typed + getParkedText();
            }
        }
        setText(getHtmlText(), BufferType.SPANNABLE);
        if(mIsParkedInFront)
            goToEndOfParkedText();
        else
            goToBeginningOfParkedText();
    }

    public boolean isBoldParkedText() {
        return mIsBoldParkedText;
    }

    public void setBoldParkedText(boolean boldParkedText) {
        mIsBoldParkedText = boldParkedText;
    }

    public String getParkedTextColor() {
        return mParkedTextColor;
    }

    public void setParkedTextColor(String parkedTextColor) {
        mParkedTextColor = parkedTextColor;
    }

    public String getParkedHintColor() {
        return mParkedHintColor;
    }

    public void setParkedHintColor(String parkedHintColor) {
        mParkedHintColor = parkedHintColor;
    }

    private class ParkedTextViewWatcher implements TextWatcher {

        private ParkedTextView mParkedTextView;

        ParkedTextViewWatcher(ParkedTextView parkedTextView) {
            this.mParkedTextView = parkedTextView;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            mParkedTextView.removeTextChangedListener(this);
            String text = s.toString().replace(" ", "");
            boolean isParkedModified = false;
            if(!mText.isEmpty() && mIsParkedInFront && !text.startsWith(getParkedText())){
                setText(getHtmlText(), BufferType.SPANNABLE);
                goToEndOfParkedText();
                isParkedModified = true;
            }else if(!mText.isEmpty() && !mIsParkedInFront && !text.endsWith(getParkedText())){
                setText(getHtmlText(), BufferType.SPANNABLE);
                goToBeginningOfParkedText();
                isParkedModified = true;
            }
            if(!isParkedModified)
                mParkedTextView.setTypedText(text);
            mParkedTextView.addTextChangedListener(this);
        }
    }

    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String str){
        if(Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N)
            return Html.fromHtml(str, Html.FROM_HTML_MODE_LEGACY);
        return Html.fromHtml(str);
    }
}

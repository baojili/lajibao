package com.ln.base.dialog;

import android.content.Context;
import android.widget.TextView;

import com.ln.base.R;
import com.ln.base.tool.StringUtils;

public class AlertDialog extends Dialog {

    public AlertDialog(Context context, int contentResId) {
        this(context, context.getString(contentResId));
    }

    public AlertDialog(Context context, String content) {
        this(context, null, content);
    }

    public AlertDialog(Context context, int titleResId, int contentResId) {
        this(context, context.getString(titleResId), context.getString(contentResId));
    }

    public AlertDialog(Context context, String title, String content) {
        this(context, title, content, 0f);
    }

    public AlertDialog(Context context, String title, String content, float dialogWidthRatio) {
        super(context, dialogWidthRatio);
        setTitle(title);
        setContentView(R.layout.dialog_alert);
        ((TextView) findViewById(R.id.dialog_alert)).setText(content);
        if (StringUtils.isValid(title)) {
            ((TextView) findViewById(R.id.dialog_alert)).setTextColor(context.getResources().getColor(R.color.text_light));
        }
    }


    public String getContent() {
        return ((TextView) findViewById(R.id.dialog_alert)).getText().toString().trim();
    }

    public TextView getContentTextView() {
        return ((TextView) findViewById(R.id.dialog_alert));
    }

    public void setContextTextViewGravity(int gravity) {
        getContentTextView().setGravity(gravity);
    }
}
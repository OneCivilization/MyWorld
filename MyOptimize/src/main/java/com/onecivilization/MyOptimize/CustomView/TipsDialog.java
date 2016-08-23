package com.onecivilization.MyOptimize.CustomView;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.onecivilization.MyOptimize.R;

/**
 * Created by CGZ on 2016/8/23.
 */
public class TipsDialog extends AlertDialog {
    public TipsDialog(Context context, int textResId) {
        super(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_tips, null);
        ((TextView) view.findViewById(R.id.tips)).setText(textResId);
        setView(view);
        setTitle(R.string.tips);
        setButton(BUTTON_POSITIVE, context.getString(R.string.close), new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        });
    }
}

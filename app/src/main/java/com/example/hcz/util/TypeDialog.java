package com.example.hcz.util;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import com.example.hcz.hechangzhi.R;

/**
 * Created by Administrator on 2018/1/8.
 */
public class TypeDialog extends Dialog {

    Context context;
    public TextView all, xj, ts, cs;

    public TypeDialog(Context context, int theme) {
        super(context, theme);
        this.context = context;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.dialog_type);
        initView();
    }

    private void initView() {
        all = findViewById(R.id.all);
        xj = findViewById(R.id.xj);
        ts = findViewById(R.id.ts);
        cs = findViewById(R.id.cs);
    }

}

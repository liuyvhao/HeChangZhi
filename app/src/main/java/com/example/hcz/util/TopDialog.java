package com.example.hcz.util;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.hcz.adapter.RiverAdapter;
import com.example.hcz.hechangzhi.R;
import com.example.hcz.pojo.River;

import java.util.List;

/**
 * Created by Administrator on 2018/1/23.
 */

public class TopDialog extends Dialog implements AdapterView.OnItemClickListener {
    private Context context;
    private TextView title_name;
    private ListView listView;
    private List<River> riverList;
    private RiverAdapter adapter;
    private int riverId;

    public TopDialog(Context context, TextView title_name, List<River> riverList, int riverId) {
        super(context, R.style.ActionSheetDialogStyle);
        this.context = context;
        this.title_name = title_name;
        this.riverList = riverList;
        this.riverId = riverId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_top);//loading的xml文件
        listView = findViewById(R.id.listView);
        adapter = new RiverAdapter(context, riverList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        getWindow().setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.y = 20;
        getWindow().setAttributes(lp);

    }

    @Override
    public void dismiss() {
        super.dismiss();
        Drawable drawable = context.getResources().getDrawable(R.drawable.gj_down);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight()); //设置边界
        title_name.setCompoundDrawables(null, null, drawable, null);//画在右边
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        title_name.setText(riverList.get(position).getName());
        riverId = (int) riverList.get(position).getId();
        dismiss();
    }
}

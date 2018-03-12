package com.example.hcz.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.hcz.hechangzhi.R;
import com.example.hcz.pojo.Addressbook;
import com.example.hcz.util.QuickAlphabeticBar;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * 通讯录适配器
 * Created by Administrator on 2017/11/24.
 */
public class AddressbookAdapter extends BaseAdapter {
    private List<Addressbook> addressbooks;
    private Context context;
    private LayoutInflater layoutInflater;
    private HashMap<String, Integer> alphaIndexer; // 字母索引
    private String[] sections; // 存储每个章节
    private Context ctx; // 上下文

    public AddressbookAdapter(Context context, List<Addressbook> addressbooks, QuickAlphabeticBar alpha) {
        this.addressbooks = addressbooks;
        this.context = context;
        this.layoutInflater = layoutInflater.from(context);
        this.alphaIndexer = new HashMap<String, Integer>();

        this.sections = new String[addressbooks.size()];

        for (int i = 0; i < addressbooks.size(); i++) {
            // 得到字母
            String name = getAlpha(addressbooks.get(i).getPin());
            if (!alphaIndexer.containsKey(name)) {
                alphaIndexer.put(name, i);
            }
        }

        Set<String> sectionLetters = alphaIndexer.keySet();
        ArrayList<String> sectionList = new ArrayList<String>(sectionLetters);
        Collections.sort(sectionList); // 根据首字母进行排序
        sections = new String[sectionList.size()];
        sectionList.toArray(sections);

        alpha.setAlphaIndexer(alphaIndexer);

    }

    @Override
    public int getCount() {
        return addressbooks.size();
    }

    @Override
    public Object getItem(int position) {
        return addressbooks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AddressbookViewHolder addressbookViewHolder;
        if (convertView == null) {
            addressbookViewHolder = new AddressbookViewHolder();
            convertView = layoutInflater.inflate(R.layout.item_addressbook, null);
            addressbookViewHolder.alpha = convertView.findViewById(R.id.alpha);
            addressbookViewHolder.userImg = convertView.findViewById(R.id.userImg);
            addressbookViewHolder.name = convertView.findViewById(R.id.name);
            addressbookViewHolder.description = convertView.findViewById(R.id.description);
            convertView.setTag(addressbookViewHolder);
        } else {
            addressbookViewHolder = (AddressbookViewHolder) convertView.getTag();
        }

        if (getItem(position) != null) {
            addressbookViewHolder.userImg.setImageURI(addressbooks.get(position).getAvatarUrl());
            addressbookViewHolder.name.setText(addressbooks.get(position).getName());
            addressbookViewHolder.description.setText(addressbooks.get(position).getDescription());
        }

        // 当前字母
        String currentStr = getAlpha(addressbooks.get(position).getPin());
        // 前面的字母
        String previewStr = (position - 1) >= 0 ? getAlpha(addressbooks.get(
                position - 1).getPin()) : " ";

        if (!previewStr.equals(currentStr)) {
            addressbookViewHolder.alpha.setVisibility(View.VISIBLE);
            addressbookViewHolder.alpha.setText(currentStr);
        } else {
            addressbookViewHolder.alpha.setVisibility(View.GONE);
        }

        return convertView;
    }

    class AddressbookViewHolder {
        SimpleDraweeView userImg;   //头像
        TextView alpha;              //姓名
        TextView name;              //姓名
        TextView description;       //职务
    }

    /**
     * 获取首字母
     *
     * @param str
     * @return
     */
    private String getAlpha(String str) {
        if (str == null) {
            return "#";
        }
        if (str.trim().length() == 0) {
            return "#";
        }
        char c = str.trim().substring(0, 1).charAt(0);
        // 正则表达式匹配
        Pattern pattern = Pattern.compile("^[A-Za-z]+$");
        if (pattern.matcher(c + "").matches()) {
            return (c + "").toUpperCase(); // 将小写字母转换为大写
        } else {
            return "#";
        }
    }
}

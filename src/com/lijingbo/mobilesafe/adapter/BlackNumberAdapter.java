package com.lijingbo.mobilesafe.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lijingbo.mobilesafe.R;
import com.lijingbo.mobilesafe.bean.BlackNumberInfo;
import com.lijingbo.mobilesafe.db.dao.BlackNumberDao;
import com.lijingbo.mobilesafe.utils.ToastUtils;

import java.util.List;

/**
 * @FileName: com.lijingbo.mobilesafe.adapter.BlackNumberAdapter.java
 * @Author: Li Jingbo
 * @Date: 2016-01-28 16:40
 * @Version V1.0  黑名单listview显示的adapter
 */
public class BlackNumberAdapter extends MyBaseAdapter< BlackNumberInfo > {

    public BlackNumberAdapter(List< BlackNumberInfo > lists, Context mContext) {
        super(lists, mContext);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final BlackNumberInfo blackNumberInfo = lists.get(position);
        ViewHolder holder;
        if ( convertView == null ) {
            holder = new ViewHolder();
            convertView = View.inflate(mContext, R.layout.blacknumber_item_view, null);
            holder.tv_blacknumber = (TextView) convertView.findViewById(R.id.tv_blacknumber);
            holder.tv_mode = (TextView) convertView.findViewById(R.id.tv_mode);
            holder.iv_delete = (ImageView) convertView.findViewById(R.id.iv_delete);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv_blacknumber.setText(blackNumberInfo.getNumber());
        String mode = blackNumberInfo.getMode();
        if ( mode.equals("1") ) {
            holder.tv_mode.setText("电话拦截");
        } else if ( mode.equals("2") ) {
            holder.tv_mode.setText("短信拦截");
        } else if ( mode.equals("3") ) {
            holder.tv_mode.setText("电话+短信拦截");
        }

        holder.iv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number = blackNumberInfo.getNumber();
                boolean result = new BlackNumberDao(mContext).delete(number);
                if ( result ) {
                    ToastUtils.showShortToast(mContext, "删除成功");
                    lists.remove(blackNumberInfo);  //视图中移除该条数据
                    notifyDataSetChanged();     //更新数据
                } else {
                    ToastUtils.showShortToast(mContext, "删除失败");
                }
            }
        });

        return convertView;
    }

    static class ViewHolder {
        TextView tv_blacknumber;
        TextView tv_mode;
        ImageView iv_delete;
    }
}

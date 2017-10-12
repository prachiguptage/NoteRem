package com.prachigupta.noterem;


import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class CheckListAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private List<CheckListItem> addItemList;
    private Context context;

    public CheckListAdapter(Context context, List<CheckListItem> addItemList) {
        this.context = context;
        this.addItemList = addItemList;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return addItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return addItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return addItemList.get(position).get_id();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

        if (convertView == null) {

            convertView = inflater.inflate(R.layout.checklist_add, parent, false);
            viewHolder = new ViewHolder();

            viewHolder.tvContent = (TextView) convertView.findViewById(R.id.tvCheckItem);
            viewHolder.ivDelete = (ImageView) convertView.findViewById(R.id.ivDelete);

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        TextView tvContent = viewHolder.tvContent;
        ImageView ivDelete = viewHolder.ivDelete;
        ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatabaseHandler.getInstance(context).
                        deleteCheckListItem(addItemList.get(position).get_id());
                addItemList.remove(position);
                notifyDataSetChanged();
            }
        });

        CheckListItem content = (CheckListItem) getItem(position);

        tvContent.setText(content.get_itemName());
        if (content.get_checked().equals(Constant.CHECKED)) {
            tvContent.setPaintFlags(tvContent.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            tvContent.setPaintFlags(0);
        }

        return convertView;
    }

    private static class ViewHolder {
        private TextView tvContent;
        private ImageView ivDelete;
    }
}

package com.prachigupta.noterem;


import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;


public class NoteAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private List<NoteEntry> noteEntryList;
    private Context context;

    public NoteAdapter(Context context, List<NoteEntry> noteEntryList) {
        this.context = context;
        this.noteEntryList = noteEntryList;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return noteEntryList.size();
    }

    @Override
    public Object getItem(int position) {
        return noteEntryList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return noteEntryList.get(position).get_id();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_note, parent, false);

            viewHolder = new ViewHolder();

            viewHolder.tTitle = (TextView) convertView.findViewById(R.id.tTitle);
            viewHolder.ivIcon = (ImageView) convertView.findViewById(R.id.ivIcon);
            viewHolder.llNoteMain = (RelativeLayout) convertView.findViewById(R.id.llNoteMain);
            viewHolder.ivDelete = (ImageView) convertView.findViewById(R.id.ivDelete);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        TextView tTitle = viewHolder.tTitle;
        ImageView ivIcon = viewHolder.ivIcon;
        RelativeLayout layout = viewHolder.llNoteMain;
        ImageView ivDelete = viewHolder.ivDelete;

        final NoteEntry entry = (NoteEntry) getItem(position);

        ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Do you want to delete " + entry.get_type() + " with Title " +
                        (entry.get_title() != null ? entry.get_title() : entry.get_content()) + "?");
                builder.setCancelable(true);

                builder.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                noteEntryList.remove(position);
                                DatabaseHandler.getInstance(context).deleteEntry(entry.get_id());
                                notifyDataSetChanged();
                                dialog.dismiss();
                            }
                        });
                builder.setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();


            }
        });

        switch (entry.get_type()) {
            case Constant.TEXT:
                ivIcon.setImageResource(R.drawable.ic_note_add_black_24dp);
                break;
            case Constant.CHECKLIST:
                ivIcon.setImageResource(R.drawable.ic_receipt_black_24dp);
                break;
            case Constant.REMINDER:
                ivIcon.setImageResource(R.drawable.ic_query_builder_black_24dp);
                break;
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            layout.setBackgroundColor(context.getColor(Utils.colorDecide(entry.get_color())));
        }

        if (entry.get_title() != null && entry.get_title().length() > 0) {
            tTitle.setText(entry.get_title());
        } else {
            tTitle.setText(entry.get_date());
        }


        return convertView;
    }

    private static class ViewHolder {
        private TextView tTitle;
        private ImageView ivIcon;
        private RelativeLayout llNoteMain;
        private ImageView ivDelete;
    }
}

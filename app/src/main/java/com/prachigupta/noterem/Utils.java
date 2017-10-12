package com.prachigupta.noterem;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.Calendar;

public class Utils {

    public static int colorDecide(String color) {

        if (color.equals(Constant.INDIGO)) {
            return R.color.theme_indigo;
        }

        if (color.equals(Constant.BLUE_GREY)) {
            return R.color.theme_blueGrey;
        }

        if (color.equals(Constant.GRAY)) {
            return R.color.theme_grey;
        }

        if (color.equals(Constant.BROWN)) {
            return R.color.theme_brown;
        }

        if (color.equals(Constant.ORANGE)) {
            return R.color.theme_orange;
        }

        if (color.equals(Constant.YELLOW)) {
            return R.color.theme_yellow;
        }

        if (color.equals(Constant.GREEN)) {
            return R.color.theme_green;
        }
        if (color.equals(Constant.BLUE)) {
            return R.color.theme_blue;
        }
        if (color.equals(Constant.LIME)) {
            return R.color.theme_lime;
        }
        if (color.equals(Constant.PURPLE)) {
            return R.color.theme_purple;
        }
        if (color.equals(Constant.PINK)) {
            return R.color.theme_pink;
        }
        if (color.equals(Constant.RED)) {
            return R.color.theme_red;
        }
        return R.color.theme_yellow;
    }

    public static Calendar getDate(String date) {
        String[] dateArray = date.split("/");
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateArray[0]));
        cal.set(Calendar.MONTH, Integer.parseInt(dateArray[1]) - 1);
        cal.set(Calendar.YEAR, Integer.parseInt(dateArray[2]));
        return cal;
    }

    public static Calendar getTime(String time, Calendar cal) {
        String[] timeArray = time.split(":");
        cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeArray[0]));
        cal.set(Calendar.MINUTE, Integer.parseInt(timeArray[1]));

        return cal;
    }

    public static long monthly() {

        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH);
        currentMonth++;

        if (currentMonth > Calendar.DECEMBER) {
            currentMonth = Calendar.JANUARY;
            calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) + 1);
        }
        calendar.set(Calendar.MONTH, currentMonth);

        int maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        calendar.set(Calendar.DAY_OF_MONTH, maxDay);
        return calendar.getTimeInMillis();
    }

    public static void getListViewSize(ListView listview) {
        ListAdapter adapter = listview.getAdapter();
        if (adapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < adapter.getCount(); i++) {
            View item = adapter.getView(i, null, listview);
            item.measure(0, 0);
            totalHeight += item.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listview.getLayoutParams();
        params.height = totalHeight + (listview.getDividerHeight() * (listview.getCount() - 1));
        listview.setLayoutParams(params);
    }
}

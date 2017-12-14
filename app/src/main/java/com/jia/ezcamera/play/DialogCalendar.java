package com.jia.ezcamera.play;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jia.ezcamera.utils.CalendarUtils;
import com.jia.ezcamera.utils.StringUtils;
import com.jia.znjj2.R;

import java.util.ArrayList;
import java.util.Calendar;


import vv.android.params.PlaybackGetDateListItem;

public class DialogCalendar {
    private static final String TAG = DialogCalendar.class.getSimpleName();
    private static DialogCalendar instance = null;
    private static int jumpMonth = 0;
    private static int jumpYear = 0;
    Dialog dlg = null;
    View view = null;
    Button previous;
    Button next;
    LinearLayout main;
    private Context mContext;
    private Calendar mCalendar;
    private CalendarUtils calendarCallback = null;
    private CalendarAdapter calV = null;
    private GridView gridView = null;
    private TextView topText = null;
    private int year_c = 0;
    private int month_c = 0;
    private int day_c = 0;

    private DialogCalendar() {
    }

    public synchronized static DialogCalendar getInstance() {
        if (instance == null) {
            instance = new DialogCalendar();
        }
        return instance;
    }
    // private String currentDate = "";

    public void setCallback(CalendarUtils cu) {
        calendarCallback = cu;
    }

    public void showDialog(Context context, Calendar calendar) {
        mContext = context;
        cancleDialog();
        mCalendar = calendar;
        if (dlg == null) {
            init();
        }
        dlg = new Dialog(context, R.style.style_dlg_groupnodes);
        dlg.setContentView(view);
        dlg.setCanceledOnTouchOutside(false);
        dlg.show();
    }

    public void cancleDialog() {
        if (dlg != null) {
            dlg.cancel();
            dlg = null;
            mCalendar = null;
        }
    }

    // SimpleDateFormat format1 = new SimpleDateFormat("yyyy MM");
    // SimpleDateFormat format2 = new SimpleDateFormat("MMM yyyy", Locale.US);
    private void init() {
        view = View.inflate(mContext, R.layout.dlg_calender, null);
        year_c = mCalendar.get(Calendar.YEAR);
        month_c = mCalendar.get(Calendar.MONTH);
        day_c = mCalendar.get(Calendar.DAY_OF_MONTH);
        // Log.i(TAG, "init    year_c=" + year_c + "    month_c=" + month_c
        // + "    day_c=" + day_c);
        jumpMonth = 0;
        jumpYear = 0;
        calV = new CalendarAdapter(mContext, mContext.getResources(), jumpMonth,
                jumpYear, year_c, month_c + 1, day_c);
        calV.setSelectedDay("" + year_c + (month_c + 1) + day_c);
        gridView = (GridView) view.findViewById(R.id.gridView1);
        gridView.setVerticalSpacing(1);
        gridView.setHorizontalSpacing(1);
        gridView.setBackgroundResource(R.color.white);
        gridView.setPadding(1, 1, 1, 1);
        gridView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {

                String scheduleDay = calV.getDateByClickItem(position).split(
                        "\\.")[0]; // ��һ�������
                String titleYear = calV.getShowYear();
                String titleMonth = calV.getShowMonth();
                int startPosition = calV.getStartPositon();
                int endPosition = calV.getEndPosition();

                if (position >= startPosition && position <= endPosition) {
                    // Toast.makeText(
                    // mContext,
                    // titleYear + "��" + titleMonth + "��" + scheduleDay
                    // + "��", Toast.LENGTH_LONG).show();
                    // calV.notifyDataSetChanged();
                    if (calendarCallback != null) {
                        calendarCallback.seletData(titleYear, titleMonth,
                                scheduleDay);
                    }
                    cancleDialog();
                }
                // else if (position < startPosition) {
                // getPreviousMonth();
                // } else if (position > endPosition) {
                // getNextMonth();
                // }
                // else {
                // Toast.makeText(MainActivity.this, "No", Toast.LENGTH_LONG)
                // .show();
                // }
            }
        });
        gridView.setAdapter(calV);
        previous = (Button) view.findViewById(R.id.previous);
        next = (Button) view.findViewById(R.id.next);
        main = (LinearLayout) view.findViewById(R.id.main);
        topText = (TextView) view.findViewById(R.id.toptext);
        addTextToTopTextView(topText);

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPreviousMonth();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getNextMonth();
            }
        });
    }

    /**
     * �ϸ���
     */
    private void getPreviousMonth() {
//        WaitingDialog.getInstance().showDialog(mContext, mContext.getResources().getString(R.string.doing_search_mouth));
        jumpMonth--;
        calV = new CalendarAdapter(mContext, mContext.getResources(), jumpMonth,
                jumpYear, year_c, month_c + 1, day_c);
        calV.setSelectedDay("" + year_c + (month_c + 1) + day_c);
        gridView.setAdapter(calV);
        addTextToTopTextView(topText);
        calendarCallback.doPrevious("" + year_c, "" + (month_c + jumpMonth), ""
                + day_c);
    }

    /**
     * �¸���
     */
    private void getNextMonth() {
        jumpMonth++;
        calV = new CalendarAdapter(mContext, mContext.getResources(), jumpMonth,
                jumpYear, year_c, month_c + 1, day_c);
        calV.setSelectedDay("" + year_c + (month_c + 1) + day_c);
        gridView.setAdapter(calV);
        addTextToTopTextView(topText);
        calendarCallback.doNext("" + year_c, "" + (month_c + jumpMonth), ""
                + day_c);
    }

    private void addTextToTopTextView(TextView view) {
        String datestr = "" + calV.getShowYear() + "." + calV.getShowMonth()
                ;
        view.setText(datestr);
        view.setTypeface(Typeface.DEFAULT_BOLD);
    }

    /**
     * ���ÿ�ʼ�ͽ������� 1-��31
     */
    public void setStartAndEnd(ArrayList<PlaybackGetDateListItem> mlist) {
        calV.setStartAndEnd(mlist);
    }

    /**
     * �ж��Ƿ��ǵ�ǰ��
     *
     * @param syear
     * @param smonth
     * @return
     */
    public boolean isNowMonth(String syear, String smonth) {
//		Log.i(TAG, "isNowMonth     syear=" + syear.trim() + "     smonth=" + smonth.trim()
//		+ "     calV.getShowMonth().trim()="
//		+ calV.getShowYear().trim()
//		+ "    calV.getShowMonth().trim()="
//		+ calV.getShowMonth().trim());
        if ((StringUtils.format(calV.getShowYear().trim()) + StringUtils.format(calV.getShowMonth().trim()))
                .equals(syear.trim() + smonth.trim())) {
            return true;
        }
        return false;
    }

    public void doRefresh() {
        calV.notifyDataSetChanged();
    }

    public void getNextDay() {
        if (calendarCallback != null) {
            calendarCallback.doGetNextDay("" + year_c, "" + (month_c + jumpMonth), ""
                    + day_c);
        }
    }

}

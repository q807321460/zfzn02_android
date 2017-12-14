package com.jia.ezcamera.play;


import android.content.Context;
import android.content.res.Resources;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jia.ezcamera.utils.ScheduleDateTag;
import com.jia.ezcamera.utils.SpecialCalendar;
import com.jia.znjj2.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


import vv.android.params.PlaybackGetDateListItem;

/**
 * 日历gridview中的每一个item显示的textview
 */
public class CalendarAdapter extends BaseAdapter {
    private static final String TAG = CalendarAdapter.class.getSimpleName();
    SpecialCalendar spe = new SpecialCalendar();
    // private ScheduleDAO dao = null;
    private boolean isLeapyear = false; // 是否为闰年
    private int daysOfMonth = 0; // 某月的天数
    private int dayOfWeek = 0; // 具体某一天是星期几
    private int lastDaysOfMonth = 0; // 上一个月的总天数
    private Context context;
    private String[] dayNumber = new String[42]; // 一个gridview中的日期存入此数组中
    private SpecialCalendar sc = null;
    private String currentYear = "";
    private String currentMonth = "";
    private String currentDay = "";
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
    private int currentFlag = -1; // 用于标记当天
    private String selectedDay = null;// 用于存储选中的日期 yyyymmdd
    private int[] schDateTagFlag = null; // 存储当月所有的日程日期
    private String showYear = ""; // 用于在头部显示的年份
    private String showMonth = ""; // 用于在头部显示的月份
    private String animalsYear = "";
    private String leapMonth = ""; // 闰哪一个月
    private String cyclical = ""; // 天干地支
    // 系统当前时间
    private String sysDate = "";
    private String sys_year = "";
    private String sys_month = "";
    private String sys_day = "";
    // 日程时间(需要标记的日程日期)
    private String sch_year = "";
    private String sch_month = "";
    private String sch_day = "";
    // private int startDay = 0;// 开始时间
    // private int endDay = 0;// 结束时间
    private ArrayList<PlaybackGetDateListItem> myList = null;

    public CalendarAdapter() {
        Date date = new Date();
        sysDate = sdf.format(date); // 当期日期
        sys_year = sysDate.split("-")[0];
        sys_month = sysDate.split("-")[1];
        sys_day = sysDate.split("-")[2];
        // Log.i(TAG, "CalendarView      sys_year=" + sys_year + "  sys_month="
        // + sys_month + "     sys_day=" + sys_day);
    }

    public CalendarAdapter(Context context, Resources rs, int jumpMonth,
                           int jumpYear, int year_c, int month_c, int day_c) {
        this();
        this.context = context;
        sc = new SpecialCalendar();
        int stepYear = year_c + jumpYear;
        int stepMonth = month_c + jumpMonth;
        if (stepMonth > 0) {
            // 往下一个月跳转
            if (stepMonth % 12 == 0) {
                stepYear = year_c + stepMonth / 12 - 1;
                stepMonth = 12;
            } else {
                stepYear = year_c + stepMonth / 12;
                stepMonth = stepMonth % 12;
            }
        } else {
            // 往上一个月跳转
            stepYear = year_c - 1 + stepMonth / 12;
            stepMonth = stepMonth % 12 + 12;
            if (stepMonth % 12 == 0) {

            }
        }

        currentYear = String.valueOf(stepYear);
        ; // 得到当前的年份
        currentMonth = String.valueOf(stepMonth); // 得到本月
        // （jumpMonth为跳动的次数，每滑动一次就增加一月或减一月）
        currentDay = String.valueOf(day_c); // 得到当前日期是哪天

        getCalendar(Integer.parseInt(currentYear),
                Integer.parseInt(currentMonth));

    }

    // public CalendarView(Context context, Resources rs, int year, int month,
    // int day) {
    // this();
    // this.context = context;
    // sc = new SpecialCalendar();
    // currentYear = String.valueOf(year);
    // ; // 得到跳转到的年份
    // currentMonth = String.valueOf(month); // 得到跳转到的月份
    // currentDay = String.valueOf(day); // 得到跳转到的天
    // getCalendar(Integer.parseInt(currentYear),
    // Integer.parseInt(currentMonth));
    // }

    @Override
    public int getCount() {
        return 42;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHoler holder = new ViewHoler();
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.item_calender, null);
            holder.tView = (TextView) convertView.findViewById(R.id.day);
            convertView.setTag(holder);
        } else {
            holder = (ViewHoler) convertView.getTag();
            resetViewHolder(holder);
        }

        String d = dayNumber[position].split("\\.")[0];
        SpannableString sp = new SpannableString(d);
        holder.tView.setText(sp);
        holder.tView.setTextColor(context.getResources().getColor(
                R.color.text_gray));
        if (position < daysOfMonth + dayOfWeek && position >= dayOfWeek) {
            // 当前月信息显示
            holder.tView.setTextColor(context.getResources().getColor(
                    R.color.white));// 当月
        }
        if (schDateTagFlag != null && schDateTagFlag.length > 0) {
            for (int i = 0; i < schDateTagFlag.length; i++) {
                if (schDateTagFlag[i] == position) {
                    // System.out.println("=============schDateTagFlag" + "[" +
                    // i
                    // + "]==" + position);
                    // 设置日程标记背景
                }
            }
        }
        // 设置选择的天的背景
        if (("" + showYear + showMonth + (position + 1 - dayOfWeek))
                .equals(selectedDay)) {
//			Log.i(TAG, "selectedDay=" + selectedDay + "    22="
//					+ ("" + showYear + showMonth + (position + 1)));
            holder.tView.setBackgroundColor(context.getResources().getColor(
                    R.color.blue));
        }
        if ((currentFlag - 1) == position) {
            // 设置当天的背景
            holder.tView.setTextColor(context.getResources().getColor(
                    R.color.blue_light));
        } else {
            holder.tView.setTextColor(context.getResources().getColor(
                    R.color.white));
            if (myList != null) {
                int mSize = myList.size();
                if (mSize > 0) {
                    for (int i = 0; i < mSize; i++) {
                        PlaybackGetDateListItem saei = myList.get(i);
//                        Log.i(TAG,"getView    saei.start_day="+saei.start_day+"     saei.end_day="+saei.end_day+"     position="+position+"     i="+i);
                        if (saei.start_day <= saei.end_day
                                && position >= (saei.start_day - 1 + dayOfWeek)
                                && position < (saei.end_day + dayOfWeek)) {
                            holder.tView.setTextColor(context.getResources()
                                    .getColor(R.color.orange));
                        }
                    }
                }
            }

        }
        if (position < dayOfWeek || position >= daysOfMonth + dayOfWeek) {
            // 设置上一月和下一月的背景
            holder.tView.setTextColor(context.getResources().getColor(
                    R.color.text_gray));
        }
        return convertView;
    }

    // 得到某年的某月的天数且这月的第一天是星期几
    public void getCalendar(int year, int month) {
        isLeapyear = sc.isLeapYear(year); // 是否为闰年
        daysOfMonth = sc.getDaysOfMonth(isLeapyear, month); // 某月的总天数
        dayOfWeek = sc.getWeekdayOfMonth(year, month); // 某月第一天为星期几
        lastDaysOfMonth = sc.getDaysOfMonth(isLeapyear, month - 1); // 上一个月的总天数
        getweek(year, month);
    }

    private ArrayList<ScheduleDateTag> getTagDate(int year, int month) {
        ArrayList<ScheduleDateTag> dateTagList = new ArrayList<ScheduleDateTag>() {
            private static final long serialVersionUID = -5976649074350323408L;
        };
        int i = 0;
        while (i < 10) {
            int tagID = i;
            int year1 = 2012;
            int month1 = 11;
            int day = 2 * (i);
            int scheduleID = i;
            ScheduleDateTag dateTag = new ScheduleDateTag(tagID, year1, month1,
                    day, scheduleID);
            dateTagList.add(dateTag);
            i++;
        }
        if (dateTagList != null && dateTagList.size() > 0) {
            return dateTagList;
        }
        return null;
    }

    // 将一个月中的每一天的值添加入数组dayNuber中
    private void getweek(int year, int month) {
        int j = 1;
        int flag = 0;

        // 得到当前月的所有日程日期(这些日期需要标记)
        // dao = new ScheduleDAO(context);
        ArrayList<ScheduleDateTag> dateTagList = this.getTagDate(year, month);
        if (dateTagList != null && dateTagList.size() > 0) {
            schDateTagFlag = new int[dateTagList.size()];
        }

        for (int i = 0; i < dayNumber.length; i++) {
            // 周一
            if (i < dayOfWeek) { // 前一个月
                int temp = lastDaysOfMonth - dayOfWeek + 1;
                dayNumber[i] = (temp + i) + ".";
            } else if (i < daysOfMonth + dayOfWeek) { // 本月
                String day = String.valueOf(i - dayOfWeek + 1); // 得到的日期
                dayNumber[i] = i - dayOfWeek + 1 + ".";
                // 对于当前月才去标记当前日期
                if (sys_year.equals(String.valueOf(year))
                        && sys_month.equals(String.valueOf(month))
                        && sys_day.equals(day)) {
                    // 笔记当前日期
                    currentFlag = i + 1;
                }
                // 标记日程日期
                if (dateTagList != null && dateTagList.size() > 0) {
                    for (int m = 0; m < dateTagList.size(); m++) {
                        ScheduleDateTag dateTag = dateTagList.get(m);
                        int matchYear = dateTag.getYear();
                        int matchMonth = dateTag.getMonth();
                        int matchDay = dateTag.getDay();
                        if (matchYear == year && matchMonth == month
                                && matchDay == Integer.parseInt(day)) {
                            schDateTagFlag[flag] = i;
                            flag++;
                        }
                    }

                }
                setShowYear(String.valueOf(year));
                setShowMonth(String.valueOf(month));
            } else { // 下一个月
                dayNumber[i] = j + ".";
                j++;
            }
        }

        // dayList = new ArrayList<String>();
        // dayList.clear();
        // String abc = "";
        // for(int i = 0; i < dayNumber.length; i++){
        // abc = abc+dayNumber[i]+":";
        // dayList.add(dayNumber[i]);
        // }

        // Log.d("hef",abc);

    }

    private void resetViewHolder(ViewHoler vh) {
        // vh.icon.setImageDrawable(null);
        // vh.icon.setImageDrawable(null);
    }

    public int getCurrentFlag() {
        return currentFlag;
    }

    // /**
    // * 得到所有的日程信息
    // */
    // public void getScheduleAll(){
    // // schList = dao.getAllSchedule();
    // if(schList != null){
    // for (ScheduleVO vo : schList) {
    // String content = vo.getScheduleContent();
    // int startLine = content.indexOf("\n");
    // if(startLine > 0){
    // content = content.substring(0, startLine)+"...";
    // }else if(content.length() > 30){
    // content = content.substring(0, 30)+"...";
    // }
    // scheduleID = vo.getScheduleID();
    // createInfotext(scheduleInfo, scheduleID);
    // }
    // }else{
    // scheduleInfo = "没有日程";
    // createInfotext(scheduleInfo,-1);
    // }
    // }

    public void setCurrentFlag(int currentFlag) {
        this.currentFlag = currentFlag;
    }

    public String getSelectedDay() {
        return selectedDay;
    }

    public void setSelectedDay(String selected) {
        this.selectedDay = selected;
    }

    public void matchScheduleDate(int year, int month, int day) {

    }

    /**
     * 点击每一个item时返回item中的日期
     *
     * @param position
     * @return
     */
    public String getDateByClickItem(int position) {
        return dayNumber[position];
    }

    /**
     * 在点击gridView时，得到这个月中第一天的位置
     *
     * @return
     */
    public int getStartPositon() {
        return dayOfWeek;
    }

    /**
     * 在点击gridView时，得到这个月中最后一天的位置
     *
     * @return
     */
    public int getEndPosition() {
        return (dayOfWeek + daysOfMonth) - 1;
    }

    public String getShowYear() {
        return showYear;
    }

    public void setShowYear(String showYear) {
        this.showYear = showYear;
    }

    public String getShowMonth() {
        return showMonth;
    }

    public void setShowMonth(String showMonth) {
        this.showMonth = showMonth;
    }

    public String getAnimalsYear() {
        return animalsYear;
    }

    public void setAnimalsYear(String animalsYear) {
        this.animalsYear = animalsYear;
    }

    public String getLeapMonth() {
        return leapMonth;
    }

    public void setLeapMonth(String leapMonth) {
        this.leapMonth = leapMonth;
    }

    public String getCyclical() {
        return cyclical;
    }

    public void setCyclical(String cyclical) {
        this.cyclical = cyclical;
    }

    /**
     * 设置开始和结束日期
     * <p/>
     * 1--31
     */
    public void setStartAndEnd(ArrayList<PlaybackGetDateListItem> mlist) {
        if (myList == null) {
            myList = new ArrayList<PlaybackGetDateListItem>();
        }
        myList.clear();
        myList.addAll(mlist);
//        Log.i(TAG,"setStartAndEnd     listsize="+myList.size());
    }

    class ViewHoler {
        TextView tView;
    }

}
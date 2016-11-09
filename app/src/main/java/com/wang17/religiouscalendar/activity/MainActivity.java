package com.wang17.religiouscalendar.activity;

import android.animation.AnimatorInflater;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.wang17.religiouscalendar.R;
import com.wang17.religiouscalendar.emnu.SolarTerm;
import com.wang17.religiouscalendar.helper.CalendarHelper;
import com.wang17.religiouscalendar.helper.GanZhi;
import com.wang17.religiouscalendar.helper.Lunar;
import com.wang17.religiouscalendar.helper.Religious;
import com.wang17.religiouscalendar.helper._Helper;
import com.wang17.religiouscalendar.helper._Session;
import com.wang17.religiouscalendar.helper._String;
import com.wang17.religiouscalendar.model.CalendarItem;
import com.wang17.religiouscalendar.model.DataContext;
import com.wang17.religiouscalendar.model.DateTime;
import com.wang17.religiouscalendar.model.Setting;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    // 视图变量
    private TextView textView_ganzhi, textView_nongli, textView_fo, button_today;
    private CalenderGridAdapter calendarAdapter;
    private ImageButton imageButton_leftMenu, imageButton_settting;
    private ImageView imageView_banner, imageView_welcome;
    private DrawerLayout drawer;
    private LinearLayout layout_upper_banner, layout_under_banner, layout_leftMenu, layout_setting, layout_religious;
    private View include_banner;
    private GridView userCalender;
    // 类变量
    private ProgressDialog progressDialog;
    private DataContext dataContext;
    private Typeface fontHWZS,fontGF;
    private DateTime selectedDate;
    // 值变量
    private int calendarItemCount, preSelectedPosition, todayPosition, currentYear, currentMonth;
    private long xxxTimeMillis;
    private boolean isFirstTime;
    private Map<Integer, CalendarItem> calendarItemsMap;
    private TreeMap<DateTime, SolarTerm> solarTermMap;
    private Map<DateTime, SolarTerm> currentMonthSolarTerms;
    private HashMap<DateTime, String> religiousDays, remarks;
    private Handler uiHandler;

    private static final int STOP_PROGRESS_DIALOG = 0;

    @Override
    public boolean onSupportNavigateUp() {
        return super.onSupportNavigateUp();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void requestPermission() {
        List<String> pers = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(this, INTERNET) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{INTERNET}, REQUEST_PERMISSION_INTERNET);
            pers.add(INTERNET);
        }
        if (ContextCompat.checkSelfPermission(this, READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{READ_PHONE_STATE}, REQUEST_PERMISSION_READ_PHONE_STATE);
            pers.add(READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION_READ_EXTERNAL_STORAGE);
            pers.add(READ_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE);
            pers.add(WRITE_EXTERNAL_STORAGE);
        }

        String[] permissions = (String[]) pers.toArray(new String[pers.size()]);
        if (permissions.length > 0)
            ActivityCompat.requestPermissions(this, permissions, 0);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            xxxTimeMillis = System.currentTimeMillis();
            uiHandler = new Handler();
            dataContext = new DataContext(MainActivity.this);
            isFirstTime = true;

            // 确认权限
//            requestPermission();

            setContentView(R.layout.activity_main);

            drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, null, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            toggle.syncState();
            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);

            imageView_welcome = (ImageView) findViewById(R.id.imageView_welcome);

            int itemPosition = Integer.parseInt(dataContext.getSetting("welcome", 0).getValue());
            if (itemPosition >= _Session.welcomes.size()) {
                itemPosition = 0;
                dataContext.editSetting(Setting.KEYS.welcome.toString(), itemPosition + "");
            }
            imageView_welcome.setImageResource(_Session.welcomes.get(itemPosition).getResId());

            //
            solarTermMap = loadJavaSolarTerms(R.raw.solar_java_50);

            //
            initializeComponent();

//
//            UpdateManager manager = new UpdateManager(MainActivity.this);
//            manager.checkUpdate();

            Log.i("wangsc", "MainActivity have loaded ... ");
        } catch (Exception ex) {
            _Helper.exceptionSnackbar(MainActivity.this, "onCreate", ex.getMessage());
        }
    }

    View.OnClickListener leftMenuClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                drawer.openDrawer(GravityCompat.START);
            }
        }
    };
    View.OnLongClickListener leftMenuLongClick = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            }
            startActivityForResult(new Intent(MainActivity.this, SettingActivity.class), TO_SETTING);
            return true;
        }
    };
    View.OnClickListener settingClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            }
            startActivityForResult(new Intent(MainActivity.this, SettingActivity.class), TO_SETTING);
        }
    };

    /**
     * 六字名号呼吸效果。
     */
    private void nianfo() throws Exception {
        ObjectAnimator objectAnimator = (ObjectAnimator) AnimatorInflater.loadAnimator(MainActivity.this, R.animator.color_animator);
        objectAnimator.setEvaluator(new ArgbEvaluator());
        objectAnimator.setTarget(textView_fo);
        objectAnimator.start();
    }

    /**
     * 方法 - 初始化所有变量
     */
    private void initializeComponent() {
        try {
            //
            int itemPosition = 0;
            itemPosition = Integer.parseInt(dataContext.getSetting(Setting.KEYS.banner.toString(), itemPosition).getValue());
            if (itemPosition >= _Session.banners.size()) {
                itemPosition = 0;
                dataContext.editSetting(Setting.KEYS.banner.toString(), itemPosition);
            }

            layout_upper_banner = (LinearLayout) findViewById(R.id.layout_upper_banner);
            layout_under_banner = (LinearLayout) findViewById(R.id.layout_under_banner);
            // 加载include_main_banner
            include_banner = getLayoutInflater().inflate(R.layout.include_main_banner, null);
            imageView_banner = (ImageView) include_banner.findViewById(R.id.imageView_banner);
            imageView_banner.setImageResource(_Session.banners.get(itemPosition).getResId());
            imageView_banner.setOnLongClickListener(new View.OnLongClickListener() {//2130903043
                @Override
                public boolean onLongClick(View v) {
                    try {
                        int position = 0;
                        position = Integer.parseInt(dataContext.getSetting(Setting.KEYS.banner.toString(), position).getValue()) + 1;
                        if (position >= _Session.banners.size()) {
                            position = 0;
                        }
                        dataContext.editSetting(Setting.KEYS.banner.toString(), position);
                        imageView_banner.setImageResource(_Session.banners.get(position).getResId());
                    } catch (Exception ex) {
                        _Helper.exceptionSnackbar(MainActivity.this, "imageView_banner.setOnLongClickListener", ex.getMessage());
                    }
                    return true;
                }
            });

            itemPosition = 0;
            itemPosition = Integer.parseInt(dataContext.getSetting(Setting.KEYS.bannerPositoin.toString(), itemPosition).getValue());
            setBannerPosition(itemPosition);

            imageButton_leftMenu = (ImageButton) findViewById(R.id.imageButton_leftMenu);
            imageButton_settting = (ImageButton) findViewById(R.id.imageButton_setting);

            imageButton_leftMenu.setOnClickListener(leftMenuClick);
            imageButton_leftMenu.setOnLongClickListener(leftMenuLongClick);
            imageButton_settting.setOnClickListener(settingClick);


            AssetManager mgr = getAssets();//得到AssetManager
            fontHWZS = Typeface.createFromAsset(mgr, "fonts/STZHONGS.TTF");
            fontGF = Typeface.createFromAsset(mgr, "fonts/GONGFANG.ttf");

            TextView textViewVersion = (TextView) findViewById(R.id.textView_Version);
            textViewVersion.setText("寿康宝鉴日历 " + this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName);
            TextView textViewContent = (TextView) findViewById(R.id.textView_Content);
            textViewContent.setLineSpacing(1f, 1.2f);
            textViewContent.setTypeface(fontHWZS);
            textViewContent.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11);

            textView_fo = (TextView) findViewById(R.id.tvfo);
//            textView_fo.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
//            ((TextView)findViewById(R.id.textView_banner_text)).setTypeface(fontHWZS);
            textView_fo.setTypeface(fontGF);
//            textView_fo.getPaint().setFakeBoldText(true);
            //
            nianfo();

            // selectedDate
            selectedDate = DateTime.getToday();
            currentYear = selectedDate.getYear();
            currentMonth = selectedDate.getMonth();
            currentMonthSolarTerms = new HashMap<DateTime, SolarTerm>();

            // button_today
            button_today = (TextView) findViewById(R.id.btnToday);
            button_today.setTypeface(fontGF);
            button_today.setOnClickListener(btnToday_OnClickListener);

            // 信息栏
//            yearMonth = (TextView) findViewById(R.id.tvYearMonth);
//            yangliBig = (TextView) findViewById(R.id.tvYangLiBig);
            TextView selectMonth = (TextView) findViewById(R.id.textView_select_month);
            selectMonth.setTypeface(fontGF);
            selectMonth.setOnClickListener(btnCurrentMonth_OnClickListener);

            textView_nongli = (TextView) findViewById(R.id.textView_selected_day);
            textView_ganzhi = (TextView) findViewById(R.id.tvGanZhi);
            layout_religious = (LinearLayout) findViewById(R.id.linearReligious);

            // calendarAdapter
            calendarAdapter = new CalenderGridAdapter();
            calendarItemsMap = new HashMap<Integer, CalendarItem>();

            // btnCurrentMonth
//            btnCurrentMonth = (Button) findViewById(R.id.btnChangeMonth);
//            btnCurrentMonth.setOnClickListener(btnCurrentMonth_OnClickListener);

            // userCalender
            userCalender = (GridView) findViewById(R.id.userCalender);
            userCalender.setOnItemClickListener(userCalender_OnItemClickListener);
            userCalender.setOnItemSelectedListener(userCalender_OnItemSelectedListener);
            GridView calendarHeader = (GridView) findViewById(R.id.userCalenderHeader);
            calendarHeader.setAdapter(new CalenderHeaderGridAdapter()); // 添加星期标头

            // 填充日历
            new Thread(new Runnable() {
                @Override
                public void run() {
                    refreshCalendar();
                }
            }).start();
        } catch (Exception ex) {
            _Helper.exceptionSnackbar(MainActivity.this, "initializeComponent", ex.getMessage());
        }
    }

    /**
     * 自定义日历标头适配器
     */
    protected class CalenderHeaderGridAdapter extends BaseAdapter {
        private String[] header = {"日", "一", "二", "三", "四", "五", "六"};

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return 7;
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            TextView mTextView = new TextView(getApplicationContext());
            mTextView.setText(header[position]);
            mTextView.setGravity(Gravity.CENTER_HORIZONTAL);
            mTextView.setTextSize(12);
            mTextView.getPaint().setFakeBoldText(true);
//            mTextView.setTextColor(getResources().getColor(R.color.dim_foreground_material_dark));
            mTextView.setTextColor(Color.parseColor("#000000"));
            mTextView.setWidth(60);
            return mTextView;
        }
    }

    /**
     * 自定义日历适配器
     */
    protected class CalenderGridAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return calendarItemCount;
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
//            convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.inflat_calender_item, null);
            getApplicationContext().getResources();
            convertView = View.inflate(MainActivity.this, R.layout.inflat_calender_item, null);
            TextView ci_tvYangLi = (TextView) convertView.findViewById(R.id.calenderItem_tv_YangLiDay);
            TextView ci_tvNongLi = (TextView) convertView.findViewById(R.id.calendarItem_tv_NongLiDay);
            ImageView ci_cvIsToday = (ImageView) convertView.findViewById(R.id.calendarItem_cvIsToday);
            ImageView ci_cvIsSelected = (ImageView) convertView.findViewById(R.id.calendarItem_cvIsSelected);
            ImageView ci_cvIsUnReligious = (ImageView) convertView.findViewById(R.id.calendarItem_cvIsUnReligious);
            if (calendarItemsMap.containsKey(position)) {
                CalendarItem calendarItem = calendarItemsMap.get(position);
                DateTime today = DateTime.getToday();
                DateTime dafs = calendarItem.getYangLi().getDate();
                ci_tvYangLi.setText(calendarItem.getYangLi().get(DateTime.DAY_OF_MONTH) + "");
                // 农历月初，字体设置。
                if (calendarItem.getNongLi().getDay() == 1) {
                    ci_tvNongLi.setText(calendarItem.getNongLi().getMonthStr());
                    ci_tvNongLi.setTextColor(Color.BLACK);
                } else {
                    ci_tvNongLi.setText(calendarItem.getNongLi().getDayStr());
                }

                // 如果是今天的View，则变换当前View主题
                if (today.compareTo(calendarItem.getYangLi().getDate()) == 0) {
                    ci_cvIsToday.setVisibility(View.VISIBLE);
                    ci_tvYangLi.setTextColor(Color.WHITE);
                    ci_tvNongLi.setTextColor(Color.WHITE);
                    todayPosition = position;
                }
                // 标出选中日期的按钮
                if (CalendarHelper.isSameDate(calendarItem.getYangLi(), selectedDate) && !CalendarHelper.isSameDate(calendarItem.getYangLi(), today)) {
                    ci_cvIsSelected.setVisibility(View.VISIBLE);
                    preSelectedPosition = position;
                }
                // 今天非戒期
                if (calendarItem.getReligious() == null) {
                    ci_cvIsUnReligious.setVisibility(View.VISIBLE);
                }
                // 显示节气
                for (Map.Entry<DateTime, SolarTerm> entry : currentMonthSolarTerms.entrySet()) {
                    today.set(entry.getKey().getYear(), entry.getKey().getMonth(), entry.getKey().get(DateTime.DAY_OF_MONTH), 0, 0, 0);
                    if (CalendarHelper.isSameDate(today, calendarItem.getYangLi())) {
                        ci_tvNongLi.setText(entry.getValue().toString());
                        break;
                    }
                }
            } else {
                ci_tvYangLi.setText("");
                ci_tvNongLi.setText("");
            }
            return convertView;
        }
    }

    /**
     * 以星期日为1，星期一为2，以此类推。
     *
     * @param day
     * @return
     */
    private String Convert2WeekDay(int day) throws Exception {
        switch (day) {
            case 1:
                return "星期日";
            case 2:
                return "星期一";
            case 3:
                return "星期二";
            case 4:
                return "星期三";
            case 5:
                return "星期四";
            case 6:
                return "星期五";
            case 7:
                return "星期六";
        }
        return "";
    }

    /**
     * 更新信息栏（农历，干支，戒期信息），一定要在当前月份的日历界面载入完毕后在引用此方法。因为此方法数据调用calendarItemsMap，而calendarItemsMap是在形成当月数据时形成。
     *
     * @param seletedDateTime 当前选中的日期
     */
    private void refreshInfoLayout(DateTime seletedDateTime) {
        if (calendarItemsMap.size() == 0) return;

        CalendarItem calendarItem = null;
        for (Map.Entry<Integer, CalendarItem> entity : calendarItemsMap.entrySet()) {
            if (CalendarHelper.isSameDate(entity.getValue().getYangLi(), seletedDateTime)) {
                calendarItem = entity.getValue();
            }
        }
        if (calendarItem == null) return;
//        yearMonth.setText(currentYear + "." + format(currentMonth + 1));
//        yangliBig.setText(seletedDateTime.getDay() + "");
        textView_nongli.setText(_String.concat(calendarItem.getYangLi().getYear(), "年", calendarItem.getYangLi().getMonth() + 1, "月", calendarItem.getYangLi().getDay(), "日"));
        try {
            GanZhi gz = new GanZhi(calendarItem.getYangLi(), this.solarTermMap);
            textView_ganzhi.setText(_String.concat(gz.getTianGanYear(), gz.getDiZhiYear(), "年 ",
                    gz.getTianGanMonth(), gz.getDiZhiMonth(), "月 ",
                    gz.getTianGanDay(), gz.getDiZhiDay(), "日"));
        } catch (Exception ex) {
            _Helper.exceptionSnackbar(MainActivity.this, "refreshInfoLayout", ex.getMessage());
        }

        layout_religious.removeAllViews();

        boolean haveReligious = calendarItem.getReligious() != null;
        boolean haveRemarks = calendarItem.getRemarks() != null;
        if (haveReligious) {
            String[] religious = calendarItem.getReligious().split("\n");
            int i = 1;
            for (String str : religious) {
                View view = View.inflate(MainActivity.this, R.layout.inflate_targ_religious, null);


                TextView tv = (TextView) view.findViewById(R.id.textView_religious);
                tv.setText(str);
                tv.getPaint().setFakeBoldText(true);
                tv.setTypeface(fontHWZS);

                layout_religious.addView(view);
            }
        }
        if (haveRemarks) {
            String[] remarks = calendarItem.getRemarks().split("\n");
            int i = 1;

            for (String str : remarks) {
                View view = View.inflate(MainActivity.this, R.layout.inflate_targ_note, null);

                TextView tv = (TextView) view.findViewById(R.id.textView_note);
                tv.setText(str);
                tv.getPaint().setFakeBoldText(true);
                tv.setTypeface(fontHWZS);

                layout_religious.addView(view);
            }
        }
    }

    /**
     * 重载日历控件日期。
     * 使用之前先设定currentYear,currentMonth。
     */
    private void refreshCalendarWithDialog(String dialogMessage) {
        progressDialog = ProgressDialog.show(MainActivity.this, "", dialogMessage, true, false);

        new Thread() {
            @Override
            public void run() {
                // 得到当月天数
                refreshCalendar();
            }
        }.start();
    }

    /**
     * 刷新日历界面，使用此方法必须标明forAsynch变量。
     *
     * @throws Exception
     */
    private void refreshCalendar() {
        int maxDayInMonth = 0;
        DateTime tmpCalendar = new DateTime(currentYear, currentMonth, 1);
        calendarItemCount = maxDayInMonth = tmpCalendar.getActualMaximum(DateTime.DAY_OF_MONTH);
        calendarItemCount += tmpCalendar.get(DateTime.DAY_OF_WEEK) - 1;

        // “今”按钮是否显示
        DateTime today = DateTime.getToday();
        if (selectedDate.compareTo(today) == 0) {
            setTodayEnable(false);
        } else {
            setTodayEnable(true);
        }

        // 得到本月节气
        currentMonthSolarTerms.clear();
        for (Map.Entry<DateTime, SolarTerm> entry : solarTermMap.entrySet()) {
            if (entry.getKey().getYear() == currentYear && entry.getKey().getMonth() == currentMonth) {
                currentMonthSolarTerms.put(entry.getKey(), entry.getValue());
            }
        }

        // 获得当月戒期信息
        try {
            long dt1 = new DateTime().getTimeInMillis();
            Religious religious = new Religious(MainActivity.this, currentYear, currentMonth, solarTermMap);
            religiousDays = religious.getReligiousDays();
            remarks = religious.getRemarks();
            long dt2 = new DateTime().getTimeInMillis();
            Log.i("wangsc-runtime", _String.concat("获取戒期数据，用时：", (double) (dt2 - dt1) / 1000, "秒"));
        } catch (Exception ex) {
            religiousDays = new HashMap<DateTime, String>();
            remarks = new HashMap<DateTime, String>();
            _Helper.exceptionSnackbar(MainActivity.this, "refreshCalendar", ex.getMessage());
        }

        // 得到填充日历控件所需要的数据
        calendarItemsMap.clear();
        for (int i = 1; i <= maxDayInMonth; i++) {
            int week = tmpCalendar.get(DateTime.WEEK_OF_MONTH);
            int day_week = tmpCalendar.get(DateTime.DAY_OF_WEEK);
            int key = (week - 1) * 7 + day_week - 1;
            DateTime newCalendar = new DateTime();
            newCalendar.setTimeInMillis(tmpCalendar.getTimeInMillis());
            CalendarItem item = new CalendarItem(newCalendar);
            //
            DateTime currentDate = newCalendar.getDate();
            if (religiousDays.containsKey(currentDate)) {
                item.setReligious(religiousDays.get(currentDate));
            }
            if (remarks.containsKey(currentDate)) {
                item.setRemarks(remarks.get(currentDate));
            }
            calendarItemsMap.put(key, item);
            tmpCalendar.add(DateTime.DAY_OF_MONTH, 1);
        }
        // 填充日历控件
        todayPosition = -1;
        preSelectedPosition = -1;

        if (isFirstTime) {
            int duration = (Integer.parseInt(dataContext.getSetting(Setting.KEYS.welcome_duration.toString(), 1).getValue())+2) * 1000;
            Log.i("wangsc","duration: "+duration);
            long span = duration - (System.currentTimeMillis() - xxxTimeMillis);
            if (span > 0) {
                try {
                    Thread.sleep(span);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                userCalender.setAdapter(calendarAdapter);
                refreshInfoLayout(selectedDate);
                imageView_welcome.setVisibility(View.INVISIBLE);
                if (progressDialog != null)
                    progressDialog.dismiss();
            }
        });
        isFirstTime = false;
    }

//    private Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case STOP_PROGRESS_DIALOG:
//                    if (progressDialog != null)
//                        progressDialog.dismiss();
//                    break;
//                case 2:
////                    btnCurrentMonth.setText(currentYear + "." + format(currentMonth + 1));
//                    break;
//                case 3:
//                    userCalender.setAdapter(calendarAdapter);
//                    break;
//                case 4:
//                    refreshInfoLayout(selectedDate);
//                    break;
//            }
//        }
//    };

    /**
     * 事件 - 改变月份按钮
     */
    View.OnClickListener btnCurrentMonth_OnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                MonthPickerDialog monthPickerDialog = new MonthPickerDialog(currentYear, currentMonth);
                monthPickerDialog.show();
            } catch (Exception ex) {
                _Helper.exceptionSnackbar(MainActivity.this, "refreshCalendar", ex.getMessage());
            }
        }
    };

    public class MonthPickerDialog {
        Dialog dialog;

        /**
         * @param year  1900 - 2049
         * @param month 0 - 11
         */
        public MonthPickerDialog(int year, int month) {
            dialog = new Dialog(MainActivity.this);
            dialog.setContentView(R.layout.inflate_date_picker_dialog);
            dialog.setTitle("选择月份");

            final NumberPicker npYear = (NumberPicker) dialog.findViewById(R.id.npYear);
            final NumberPicker npMonth = (NumberPicker) dialog.findViewById(R.id.npMonth);
            Button btnOK = (Button) dialog.findViewById(R.id.btnOK);
            Button btnCancle = (Button) dialog.findViewById(R.id.btnCancel);
            npYear.setMinValue(Lunar.MinYear);
            npYear.setMaxValue(Lunar.MaxYear);
            npYear.setValue(year);
            npYear.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS); // 禁止对话框打开后数字选择框被选中
            npMonth.setMinValue(1);
            npMonth.setMaxValue(12);
            npMonth.setValue(month + 1);
            npMonth.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS); // 禁止对话框打开后数字选择框被选中
            btnOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int year = npYear.getValue();
                    int month = npMonth.getValue() - 1;
                    DateTime dateTime = new DateTime();
                    dateTime.set(year, month, 1);
                    int maxDayOfMonth = dateTime.getActualMaximum(Calendar.DAY_OF_MONTH);
                    int selectedDay = MainActivity.this.selectedDate.getDay();
                    setSelectedDate(year, month, maxDayOfMonth < selectedDay ? maxDayOfMonth : selectedDay);
//                    refreshCalendarWithDialog();
                    dialog.cancel();
                }
            });
            btnCancle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.cancel();
                }
            });
        }

        public void show() {
            dialog.show();
        }
    }

    /**
     * 事件 - 点击日历某天
     */
    private AdapterView.OnItemClickListener userCalender_OnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (!calendarItemsMap.containsKey(position)) return;

            CalendarItem calendarItem = calendarItemsMap.get(position);
            CalendarItem preCalendarItem = calendarItemsMap.get(preSelectedPosition);
            if (todayPosition != -1) {
                if (preSelectedPosition != -1 && preSelectedPosition != todayPosition) {
                    parent.getChildAt(preSelectedPosition).findViewById(R.id.calendarItem_cvIsSelected).setVisibility(View.INVISIBLE);
                }
                if (position != todayPosition) {
                    view.findViewById(R.id.calendarItem_cvIsSelected).setVisibility(View.VISIBLE);
                }
            } else {
                if (preSelectedPosition != -1) {
                    parent.getChildAt(preSelectedPosition).findViewById(R.id.calendarItem_cvIsSelected).setVisibility(View.INVISIBLE);
                }
                view.findViewById(R.id.calendarItem_cvIsSelected).setVisibility(View.VISIBLE);
            }
            preSelectedPosition = position;

            //
            setSelectedDate(calendarItem.getYangLi().getYear(), calendarItem.getYangLi().getMonth(), calendarItem.getYangLi().getDay());
        }
    };

    /**
     * 事件 - 返回今天
     */
    private View.OnClickListener btnToday_OnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (preSelectedPosition != -1) {
                userCalender.getChildAt(preSelectedPosition).findViewById(R.id.calendarItem_cvIsSelected).setVisibility(View.INVISIBLE);
            }
            DateTime today = DateTime.getToday();
            setSelectedDate(today.getYear(), today.getMonth(), today.getDay());
        }
    };

    /**
     * 设置SelectedDate，并在修改该属性之后，重载自定义日历区域数据。
     *
     * @param year
     * @param month
     * @param day
     */
    public void setSelectedDate(int year, int month, int day) {
        boolean monthHasChanged = false;

        // 如果新选中日期与当前月份不再同一月份，则刷新日历。
        if (year != currentYear || month != currentMonth) {
            monthHasChanged = true;
        }
        this.selectedDate.set(year, month, day);

        // 判断是否刷新自定义日历区域
        if (monthHasChanged) {
            currentYear = year;
            currentMonth = month;
            refreshCalendarWithDialog(_String.concat("正在加载", currentYear, "年", currentMonth + 1, "月份", "戒期信息。"));
        }

        // “今”按钮是否显示
        DateTime today = DateTime.getToday();
        if (year == today.getYear() && month == today.getMonth() && day == today.getDay()) {
            setTodayEnable(false);
        } else {
            setTodayEnable(true);
        }

        //
        refreshInfoLayout(selectedDate);
    }

    /**
     * 设置“回到今天”按钮是否可用。
     *
     * @param enable
     */
    private void setTodayEnable(Boolean enable) {
        if (enable) {
            button_today.setVisibility(View.VISIBLE);
            imageButton_leftMenu.setVisibility(View.INVISIBLE);
        } else {
            button_today.setVisibility(View.INVISIBLE);
            imageButton_leftMenu.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 事件 - 选中日历某天
     */
    private AdapterView.OnItemSelectedListener userCalender_OnItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    /**
     * 小于10前追加‘0’
     *
     * @param x
     * @return
     */
    private String format(int x) {
        String s = "" + x;
        if (s.length() == 1)
            s = "0" + s;
        return s;
    }

    /**
     * 读取JAVA结构的二进制节气数据文件。
     *
     * @param resId 待读取的JAVA二进制文件。
     * @return
     * @throws IOException
     * @throws Exception
     */
    private TreeMap<DateTime, SolarTerm> loadJavaSolarTerms(int resId) throws IOException, Exception {
        TreeMap<DateTime, SolarTerm> result = new TreeMap<DateTime, SolarTerm>();
        DataInputStream dis = new DataInputStream(getResources().openRawResource(resId));

        long date = dis.readLong();
        int solar = dis.readInt();
        try {
            while (true) {
                DateTime cal = new DateTime();
                cal.setTimeInMillis(date);
                SolarTerm solarTerm = SolarTerm.Int2SolarTerm(solar);
                result.put(cal, solarTerm);
                date = dis.readLong();
                solar = dis.readInt();
            }
        } catch (EOFException ex) {
            dis.close();
        }
        // 按照KEY排序TreeMap
//        TreeMap<DateTime, SolarTerm> result = new TreeMap<DateTime, SolarTerm>(new Comparator<DateTime>() {
//            @Override
//            public int compare(DateTime lhs, DateTime rhs) {
//                return lhs.compareTo(rhs);
//            }
//        });
        return result;
    }

    /**
     * 将C#导出的二进制文件转化为JAVA数据结构存储的二进制文件，并保存为/mnt/sdcard/solar300.dat。
     *
     * @param resId 待转化的C#二进制文件资源ID。
     * @throws IOException
     * @throws Exception
     */
    private void convertToJavafile(int resId) throws IOException, Exception {
        Map<DateTime, SolarTerm> solarTermMap = loadCsharpSolarTerms(resId);
        File file = new File("/mnt/sdcard/solar300.dat");
        if (!file.exists()) {
            file.createNewFile();
        }
        FileOutputStream fos = new FileOutputStream(file);
        DataOutputStream dos = new DataOutputStream(fos);

        Set set = solarTermMap.entrySet();
        Iterator i = set.iterator();
        while (i.hasNext()) {
            Map.Entry<DateTime, SolarTerm> solar = (Map.Entry<DateTime, SolarTerm>) i.next();
            dos.writeLong(solar.getKey().getTimeInMillis());
            dos.writeInt(solar.getValue().getValue());
        }
        dos.flush();
        dos.close();
        fos.close();
    }

    /**
     * 从C#导出的二进制文件获取节气数据。
     *
     * @param resId 资源文件ID
     * @return
     */
    private Map<DateTime, SolarTerm> loadCsharpSolarTerms(int resId) throws Exception {
        Map<DateTime, SolarTerm> solarTermMap = new HashMap<DateTime, SolarTerm>();
        InputStream stream = getResources().openRawResource(resId);
        byte[] longBt = new byte[8];
        byte[] intBt = new byte[4];
        byte[] nullBt = new byte[4];
        stream.read(longBt);
        stream.read(intBt);
        int cursor = stream.read(nullBt);

        while (cursor != -1) {
            DateTime cal = new DateTime();
            cal.setTimeInMillis(bytesToLong(longBt));
            int solar = bytesToInt(intBt);
            SolarTerm solarTerm = SolarTerm.Int2SolarTerm(solar);
            solarTermMap.put(cal, solarTerm);
            stream.read(longBt);
            stream.read(intBt);
            cursor = stream.read(nullBt);
        }
        stream.close();
        return solarTermMap;
    }

    /**
     * byte[] 转化为int。
     *
     * @param bytes 需要转化的byte[]
     * @return
     */
    public int bytesToInt(byte[] bytes) {
        return bytes[0] & 0xFF | (bytes[1] & 0xFF) << 8 | (bytes[2] & 0xFF) << 16 | (bytes[3] & 0xFF) << 24;
    }

    private ByteBuffer buffer;

    /**
     * byte[] 转化为long。
     *
     * @param bytes 需要转化的byte[]
     * @return
     */
    public long bytesToLong(byte[] bytes) {
        buffer = ByteBuffer.allocate(8);
        for (int i = bytes.length - 1; i >= 0; i--) {
            buffer.put(bytes[i]);
        }
        buffer.flip();//need flip
        return buffer.getLong();
    }


    @Override
    public void onBackPressed() {
        try {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                super.onBackPressed();
            }
        } catch (Exception ex) {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        menuItemSelected(item);
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        menuItemSelected(item);

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private boolean menuItemSelected(MenuItem menuItem) {

        int id = menuItem.getItemId();
        if (id == R.id.menu_settings) {
            startActivityForResult(new Intent(MainActivity.this, SettingActivity.class), TO_SETTING);
        }
//        else if (id == R.id.menu_select) {
//            try {
//                MonthPickerDialog monthPickerDialog = new MonthPickerDialog(currentYear, currentMonth);
//                monthPickerDialog.show();
//            } catch (Exception ex) {
//
//            }
//        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public static final int TO_SETTING = 1;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == TO_SETTING) {
            if (SettingActivity.calenderChanged) {
                refreshCalendarWithDialog("配置已更改，正在重新加载...");
            }
            if (SettingActivity.bannerChanged) {
                int itemPosition = Integer.parseInt(dataContext.getSetting(Setting.KEYS.banner.toString()).getValue());
                imageView_banner.setImageResource(_Session.banners.get(itemPosition).getResId());
            }
            if (SettingActivity.bannerPositionChanged) {
                int itemPosition = Integer.parseInt(dataContext.getSetting(Setting.KEYS.bannerPositoin.toString()).getValue());
                setBannerPosition(itemPosition);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setBannerPosition(int itemPosition) {
        switch (itemPosition) {
            case 0:
                layout_under_banner.removeAllViews();
                layout_upper_banner.addView(include_banner);
                break;
            case 1:
                layout_upper_banner.removeAllViews();
                layout_under_banner.addView(include_banner);
                break;
            case 2:
                layout_under_banner.removeAllViews();
                layout_upper_banner.removeAllViews();
                break;
        }
    }
}

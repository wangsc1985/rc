package com.wang17.religiouscalendar.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.wang17.religiouscalendar.R;
import com.wang17.religiouscalendar.emnu.MDrelation;
import com.wang17.religiouscalendar.emnu.MDtype;
import com.wang17.religiouscalendar.emnu.Zodiac;
import com.wang17.religiouscalendar.fragment.ActionBarFragment;
import com.wang17.religiouscalendar.fragment.ActionBarFragment.OnActionFragmentBackListener;
import com.wang17.religiouscalendar.helper._Helper;
import com.wang17.religiouscalendar.helper._Session;
import com.wang17.religiouscalendar.helper._String;
import com.wang17.religiouscalendar.model.DataContext;
import com.wang17.religiouscalendar.model.LunarDate;
import com.wang17.religiouscalendar.model.MemorialDay;
import com.wang17.religiouscalendar.model.Setting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class SettingActivity extends AppCompatActivity implements OnActionFragmentBackListener {

    private Spinner spinner_zodiac1, spinner_zodiac2, spinner_mdtype, spinner_mdrelation, spinner_month, spinner_day, spinner_welcome, spinner_duration, spinner_banner, spinner_bannerPosition;
    private Button btn_addMD;
    private CheckBox checkBox_szr;
    private TextView textView_guide, textView_update;

    public static boolean calenderChanged, bannerChanged, bannerPositionChanged;
    private DataContext dataContext;
    private MDlistdAdapter mdListAdapter;
    private List<HashMap<String, String>> mdListItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("wangsc", "SettingActivity is loading ...");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_action, ActionBarFragment.newInstance()).commit();

        try {
            spinner_zodiac1 = (Spinner) findViewById(R.id.spinner_zodiac1);
            spinner_zodiac2 = (Spinner) findViewById(R.id.spinner_zodiac2);
            spinner_mdtype = (Spinner) findViewById(R.id.spinner_mdtype);
            spinner_mdrelation = (Spinner) findViewById(R.id.spinner_mdrelation);
            spinner_month = (Spinner) findViewById(R.id.spinner_month);
            spinner_day = (Spinner) findViewById(R.id.spinner_day);
            spinner_welcome = (Spinner) findViewById(R.id.spinner_welcome);
            spinner_duration = (Spinner) findViewById(R.id.spinner_duration);
            spinner_banner = (Spinner) findViewById(R.id.spinner_banner);
            spinner_bannerPosition = (Spinner) findViewById(R.id.spinner_bannerPositio);

            btn_addMD = (Button) findViewById(R.id.button_addMD);
            textView_guide = (TextView) findViewById(R.id.textView_guide);
            textView_update = (TextView) findViewById(R.id.textView_update);

            checkBox_szr = (CheckBox) findViewById(R.id.checkBox_szr);

            this.initializeFields();
            this.initializeEvents();
            Log.i("wangsc", "SettingActivity have loaded ...");
        } catch (Exception ex) {
            _Helper.printExceptionSycn(SettingActivity.this, uiHandler, ex);
        }
    }

    private void initializeFields() {
        dataContext = new DataContext(SettingActivity.this);
        calenderChanged = false;
        bannerChanged = false;
        bannerPositionChanged = false;

//        if (UpdateManager.isUpdate())
//            textView_update.setVisibility(View.VISIBLE);

        Setting szr = dataContext.getSetting(Setting.KEYS.szr.toString());
        if (szr == null) {
            dataContext.addSetting(Setting.KEYS.szr.toString(), false + "");
            checkBox_szr.setChecked(false);
        } else {
            checkBox_szr.setChecked(Boolean.parseBoolean(szr.getValue()));
        }

        this.initializeZodiac(spinner_zodiac1);
        this.initializeZodiac(spinner_zodiac2);
        Setting zodiac1 = dataContext.getSetting(Setting.KEYS.zodiac1.toString());
        Setting zodiac2 = dataContext.getSetting(Setting.KEYS.zodiac2.toString());
        if (zodiac1 != null) {
            spinner_zodiac1.setSelection(Zodiac.fromString(zodiac1.getValue()).toInt());
        }
        if (zodiac2 != null) {
            spinner_zodiac2.setSelection(Zodiac.fromString(zodiac2.getValue()).toInt());
        }

        List<String> list = new ArrayList<String>();
        for (int i = 0; i < MDtype.count(); i++) {
            list.add(MDtype.fromInt(i).toString());
        }
        this.fillSpinner(spinner_mdtype, list);
        List<String> list2 = new ArrayList<String>();
        for (int i = 0; i < MDrelation.count(); i++) {
            list2.add(MDrelation.fromInt(i).toString());
        }
        this.fillSpinner(spinner_mdrelation, list2);

        this.initializeLunarMonth(spinner_month);
        this.initializeLunarDay(spinner_day);

        this.initializeWelcome();
        spinner_welcome.setSelection(Integer.parseInt(dataContext.getSetting(Setting.KEYS.welcome.toString(), 0).getValue()));

        this.initializeDuration();
        spinner_duration.setSelection(Integer.parseInt(dataContext.getSetting(Setting.KEYS.welcome_duration.toString(), 1).getValue()));

        this.initializeBanner();
        spinner_banner.setSelection(Integer.parseInt(dataContext.getSetting(Setting.KEYS.banner.toString(), 0).getValue()));

        this.initializeBannerPosition();
        spinner_bannerPosition.setSelection(Integer.parseInt(dataContext.getSetting(Setting.KEYS.bannerPositoin.toString(), 0).getValue()));

        List<MemorialDay> memorialDays = dataContext.getMemorialDays();

        mdListItems = new ArrayList<HashMap<String, String>>();
        for (MemorialDay md : memorialDays) {
            this.addListItem(md);
        }
        refreshMdList();
//        mdListAdapter = new MDlistdAdapter();
//        listViewMD.setAdapter(mdListAdapter);
    }

    private void addListItem(MemorialDay md) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("id", md.getId().toString());
        map.put("relation", md.getRelation().toString());
        map.put("type", md.getType().toString());
        map.put("lunarDate", md.getLunarDate().toString());
        mdListItems.add(map);
    }

    private void refreshMdList() {
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.mdList);
        linearLayout.removeAllViews();
        for (int position = 0; position < mdListItems.size(); position++) {
            final int index = position;
            View convertView = View.inflate(SettingActivity.this, R.layout.inflate_md_list_item, null);
            HashMap<String, String> map = mdListItems.get(position);
            TextView textViewRelation = (TextView) convertView.findViewById(R.id.textView_relation);
            TextView textViewType = (TextView) convertView.findViewById(R.id.textView_type);
            TextView textViewLunarDate = (TextView) convertView.findViewById(R.id.textView_lunarDate);
            LinearLayout btnDel = (LinearLayout) convertView.findViewById(R.id.linear_delete);
            final String relation = map.get("relation");
            final String type = map.get("type");
            String lunarDate = map.get("lunarDate");
            textViewRelation.setText(relation);
            textViewType.setText(type);
            textViewLunarDate.setText(lunarDate);
            btnDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
                    builder.setTitle("删除确认");
                    builder.setMessage(_String.concat("是否要删除【", relation, "\t", type, "】?"));
                    builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dataContext.deleteMemorialDay(UUID.fromString(mdListItems.get(index).get("id")));
                            mdListItems.remove(index);
                            refreshMdList();
                            calenderChanged = true;
                            dialog.cancel();
                            snackbar("删除成功");
                        }
                    });
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.create().show();
                }
            });
            linearLayout.addView(convertView, 0);
        }
    }

    private Handler uiHandler = new Handler();

    private void initializeEvents() {
//        textView_update.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                UpdateManager manager = new UpdateManager(SettingActivity.this);
//                manager.startDownload();
//            }
//        });
        textView_guide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GuideActivity.btnText = "返回软件";
                startActivity(new Intent(SettingActivity.this, GuideActivity.class));
            }
        });
        checkBox_szr.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                dataContext.editSetting("szr", isChecked + "");
                calenderChanged = true;
            }
        });
        btn_addMD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    MemorialDay md = new MemorialDay();
                    String relation = spinner_mdrelation.getSelectedItem().toString();
                    String type = spinner_mdtype.getSelectedItem().toString();
                    String month = spinner_month.getSelectedItem().toString();
                    String day = spinner_day.getSelectedItem().toString();
                    md.setRelation(MDrelation.fromString(relation));
                    md.setType(MDtype.fromString(type));
                    md.setLunarDate(new LunarDate(month, day));
                    dataContext.addMemorialDay(md);

                    //
                    SettingActivity.this.addListItem(md);
                    refreshMdList();
                    calenderChanged = true;
                    snackbar("添加成功");
                } catch (Exception ex) {
                    _Helper.printExceptionSycn(SettingActivity.this, uiHandler, ex);
                }
            }
        });
        spinner_zodiac1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Setting setting = dataContext.getSetting(Setting.KEYS.zodiac1.toString());
                String zodiac = spinner_zodiac1.getItemAtPosition(position).toString();
                dataContext.editSetting(Setting.KEYS.zodiac1.toString(), zodiac);
                if (setting != null && !setting.getValue().equals(zodiac)) {
                    calenderChanged = true;
                    snackbarSaved();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner_zodiac2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Setting setting = dataContext.getSetting(Setting.KEYS.zodiac2.toString());
                String zodiac = spinner_zodiac2.getItemAtPosition(position).toString();
                dataContext.editSetting(Setting.KEYS.zodiac2.toString(), zodiac);
                if (setting != null && !setting.getValue().equals(zodiac)) {
                    calenderChanged = true;
                    snackbarSaved();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner_welcome.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                      @Override
                                                      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                          Setting setting = dataContext.getSetting(Setting.KEYS.welcome.toString(), 0);
                                                          if (!setting.getValue().equals(spinner_welcome.getSelectedItemPosition() + "")) {
                                                              dataContext.editSetting(Setting.KEYS.welcome.toString(), spinner_welcome.getSelectedItemPosition() + "");
                                                              snackbarSaved();
                                                          }
                                                      }

                                                      @Override
                                                      public void onNothingSelected(AdapterView<?> parent) {

                                                      }
                                                  }

        );
        spinner_duration.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Setting setting = dataContext.getSetting(Setting.KEYS.welcome_duration.toString(), 1);
                if (!setting.getValue().equals(spinner_duration.getSelectedItemPosition() + "")) {
                    dataContext.editSetting(Setting.KEYS.welcome_duration.toString(), (spinner_duration.getSelectedItemPosition()) + "");
                    snackbarSaved();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner_banner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                     @Override
                                                     public void onItemSelected(AdapterView<?> parent, View view, int position,
                                                                                long id) {

                                                         Setting setting = dataContext.getSetting(Setting.KEYS.banner.toString());
                                                         if (setting != null) {
                                                             if (!setting.getValue().equals(spinner_banner.getSelectedItemPosition() + "")) {
                                                                 bannerChanged = true;
                                                                 dataContext.editSetting(Setting.KEYS.banner.toString(), spinner_banner.getSelectedItemPosition() + "");
                                                                 snackbarSaved();
                                                             }
                                                         } else {
                                                             if (spinner_banner.getSelectedItemPosition() != 0) {
                                                                 bannerChanged = true;
                                                                 dataContext.addSetting(Setting.KEYS.banner.toString(), spinner_banner.getSelectedItemPosition() + "");
                                                                 snackbarSaved();
                                                             }
                                                         }
                                                     }

                                                     @Override
                                                     public void onNothingSelected(AdapterView<?> parent) {

                                                     }
                                                 }

        );
        spinner_bannerPosition.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Setting setting = dataContext.getSetting(Setting.KEYS.bannerPositoin.toString());
                if (setting != null) {
                    if (!setting.getValue().equals(spinner_bannerPosition.getSelectedItemPosition() + "")) {
                        bannerPositionChanged = true;
                        dataContext.editSetting(Setting.KEYS.bannerPositoin.toString(), spinner_bannerPosition.getSelectedItemPosition() + "");
                        snackbarSaved();
                    }
                } else {
                    if (spinner_bannerPosition.getSelectedItemPosition() != 0) {
                        bannerPositionChanged = true;
                        dataContext.addSetting(Setting.KEYS.bannerPositoin.toString(), spinner_bannerPosition.getSelectedItemPosition() + "");
                        snackbarSaved();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initializeDuration() {
        List<String> list = new ArrayList<String>();
        list.add("2秒");
        list.add("3秒");
        list.add("4秒");
        list.add("5秒");
        list.add("6秒");
        list.add("7秒");
        this.fillSpinner(spinner_duration, list);
    }

    private void initializeBannerPosition() {
        List<String> list = new ArrayList<String>();
        list.add("顶部显示");
        list.add("下部显示");
        list.add("不显示");
        this.fillSpinner(spinner_bannerPosition, list);
    }

    private void initializeBanner() {
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < _Session.banners.size(); i++) {
            list.add(_Session.banners.get(i).getListItemString());
        }
        this.fillSpinner(spinner_banner, list);
    }

    private void initializeWelcome() {
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < _Session.welcomes.size(); i++) {
            list.add(_Session.welcomes.get(i).getListItemString());
        }
        this.fillSpinner(spinner_welcome, list);
    }

    private void initializeZodiac(Spinner spinner) {
        List<String> mItems = new ArrayList<String>();
        for (int i = 0; i < Zodiac.count(); i++) {
            mItems.add(Zodiac.fromInt(i).toString());
        }
        this.fillSpinner(spinner, mItems);
    }

    private void initializeLunarMonth(Spinner spinner) {
        this.fillSpinner(spinner, LunarDate.Months);
    }

    private void initializeLunarDay(Spinner spinner) {
        this.fillSpinner(spinner, LunarDate.Days);
    }

    private void fillSpinner(Spinner spinner, List<String> values) {
        ArrayAdapter<String> aspn = new ArrayAdapter<String>(SettingActivity.this, R.layout.inflate_spinner, values);
        aspn.setDropDownViewResource(R.layout.inflate_spinner_dropdown);
        spinner.setAdapter(aspn);
    }

    @Override
    public void onBackListener() {
        this.finish();
    }

    /**
     *
     */
    protected class MDlistdAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mdListItems.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final int index = position;
            final View vew = convertView;
            getApplicationContext().getResources();
            convertView = View.inflate(SettingActivity.this, R.layout.inflate_md_list_item, null);
            HashMap<String, String> map = mdListItems.get(position);
            TextView textViewRelation = (TextView) convertView.findViewById(R.id.textView_relation);
            TextView textViewType = (TextView) convertView.findViewById(R.id.textView_type);
            TextView textViewLunarDate = (TextView) convertView.findViewById(R.id.textView_lunarDate);
            ImageView btnDel = (ImageView) convertView.findViewById(R.id.imageView_Del);
            btnDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
                    builder.setTitle("删除确认");
                    builder.setMessage("是否要删除此纪念日?");
                    builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dataContext.deleteMemorialDay(UUID.fromString(mdListItems.get(index).get("id")));
                            mdListItems.remove(index);
                            mdListAdapter.notifyDataSetChanged();
                            calenderChanged = true;
                            dialog.cancel();
                            snackbar("删除成功");
//                            Toast.makeText(SettingActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                        }
                    });
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.create().show();
                }
            });
            textViewRelation.setText(map.get("relation"));
            textViewType.setText(map.get("type"));
            textViewLunarDate.setText(map.get("lunarDate"));
            return convertView;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void snackbarSaved() {
        snackbar("设置已保存");
    }

    private void snackbar(String message) {
        RelativeLayout root = (RelativeLayout) findViewById(R.id.layout_setting_root);
        Snackbar.make(root, message, Snackbar.LENGTH_LONG).show();
    }
}

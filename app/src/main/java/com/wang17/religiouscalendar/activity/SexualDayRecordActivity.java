package com.wang17.religiouscalendar.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wang17.religiouscalendar.R;
import com.wang17.religiouscalendar.fragment.ActionBarFragment;
import com.wang17.religiouscalendar.model.DataContext;
import com.wang17.religiouscalendar.helper._Helper;
import com.wang17.religiouscalendar.helper._String;
import com.wang17.religiouscalendar.model.DateTime;
import com.wang17.religiouscalendar.model.SexualDay;

import java.util.Calendar;
import java.util.List;

public class SexualDayRecordActivity extends AppCompatActivity  implements ActionBarFragment.OnActionFragmentBackListener {

    // 视图变量
    RelativeLayout root;
    // 类变量
    private DataContext dataContext;
    private List<SexualDay> sexualDays;
    private SexualDayListdAdapter recordListdAdapter;
    // 值变量
    private int max;

    /**

    20 4
    21 4.4
    22 4.8
    23 5.2
    24 5.6
    25 6
    26 6.4
    27 6.8
    28 7.2
    29 7.6
    30 8

    31 8.8
    32 9.6
    33 10.4
    34 11.2
    35 12
    36 12.8
    37 13.6
    38 14.4
    39 15.2
    40 16

    41 16.5
    42 17
    43 17.5
    44 18
    45 18.5
    46 19
    47 19.5
    48 20
    49 20.5
    50 21

    51 21.9
    52 22.8
    53 23.7
    54 24.6
    55 25.5
    56 26.4
    57 27.3
    58 28.2
    59 29.1
    60 30

    20 4
    30 8
    40 16
    50 21
    60 30
    年二十者四日一泄;年三十者，
    八日一泄;年四十者，
    十六日一泄;
    年五十者，
    二十一日一泄;
    年六十者，毕，闭精勿复泄也。
    若体力犹壮者，一月一泄。
    凡人气力，自相有强盛过人，亦不可抑忍。
    久而不泄，至生痈疽。
    若年过六十，而有数旬不得交接，意中平平者，可闭精勿泄也。
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_sexual_day_record);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_action, ActionBarFragment.newInstance()).commit();

            dataContext = new DataContext(this);
            root = (RelativeLayout) findViewById(R.id.activity_sexual_day_record);
            sexualDays = dataContext.getSexualDays(true);

            max = _Helper.getTarget(33);

            ListView listView_sexualDays = (ListView) findViewById(R.id.listView_sexualDays);
            listView_sexualDays.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if(view.findViewById(R.id.layout_operate).getVisibility()== View.VISIBLE){
                        view.findViewById(R.id.layout_operate).setVisibility(View.INVISIBLE);
                    }else{
                        for(int i=0;i<parent.getChildCount();i++){
                            parent.getChildAt(i).findViewById(R.id.layout_operate).setVisibility(View.INVISIBLE);
                        }
                        view.findViewById(R.id.layout_operate).setVisibility(View.VISIBLE);
                    }
                }
            });

            recordListdAdapter = new SexualDayListdAdapter();
            listView_sexualDays.setAdapter(recordListdAdapter);
        } catch (Exception e) {
            _Helper.printException(this, e);
        }
    }

    @Override
    public void onBackListener() {
        this.finish();
    }

    protected class SexualDayListdAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return sexualDays.size();
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
            try {
                convertView = View.inflate(SexualDayRecordActivity.this, R.layout.inflate_list_item_sexual_day, null);
                final SexualDay sexualDay = sexualDays.get(position);
                DateTime nextDateTime = new DateTime();
                if (position > 0) {
                    nextDateTime = sexualDays.get(position - 1).getDateTime();
                }
                int interval = (int) (nextDateTime.getTimeInMillis() - sexualDay.getDateTime().getTimeInMillis());

                final RelativeLayout layoutRoot = (RelativeLayout) convertView.findViewById(R.id.layout_root);
                TextView textViewStartDay = (TextView) convertView.findViewById(R.id.textView_startDay);
                TextView textViewStartTime = (TextView) convertView.findViewById(R.id.textView_startTime);
                TextView textViewInterval = (TextView) convertView.findViewById(R.id.textView_interval);
                final LinearLayout layoutOperate = (LinearLayout) convertView.findViewById(R.id.layout_operate);
                FrameLayout layoutEdit = (FrameLayout) convertView.findViewById(R.id.layout_edit);
                FrameLayout layoutDel = (FrameLayout) convertView.findViewById(R.id.layout_del);
                ProgressBar progressBar = (ProgressBar) convertView.findViewById(R.id.progressBar);
                progressBar.setMax(max);
                layoutEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditSexualDayDialog editSexualDayDialog = new EditSexualDayDialog(sexualDay);
                        editSexualDayDialog.show();
                    }
                });
                layoutDel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        try {
                            new AlertDialog.Builder(SexualDayRecordActivity.this).setTitle("删除确认").setMessage("是否要删除当前记录?").setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dataContext.deleteSexualDay(sexualDays.get(index).getId());
                                    sexualDays.remove(index);
                                    recordListdAdapter.notifyDataSetChanged();
                                    dialog.cancel();
                                    snackbar("删除成功");
                                }
                            }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            }).show();
                        } catch (Exception e) {
                            _Helper.printException(SexualDayRecordActivity.this, e);
                        }
                    }
                });
                DateTime date = sexualDay.getDateTime();
                textViewStartDay.setText(date.getMonthStr() + "月" + date.getDayStr() + "日");
                textViewStartTime.setText(date.getHour() + ":" + date.getMiniteStr());
                int day = (int) (interval / 60000 / 60 / 24);
                int hour = (int) (interval / 60000 / 60 % 24);
                textViewInterval.setText(_String.concat(day > 0 ? day + "天" : "", hour + "小时"));
                progressBar.setProgress((int)(interval/3600000));
            } catch (Exception e) {
                _Helper.printException(SexualDayRecordActivity.this, e);
            }
            return convertView;
        }
    }


    public class EditSexualDayDialog {
        private Dialog dialog;

        public EditSexualDayDialog(SexualDay sexualDay) {

            try {
                final SexualDay sd = sexualDay;
                dialog = new Dialog(SexualDayRecordActivity.this);
                dialog.setContentView(R.layout.inflate_dialog_date_picker);
                dialog.setTitle("设定时间");

                int year = sexualDay.getDateTime().getYear();
                int month = sexualDay.getDateTime().getMonth();
                int maxDay = sexualDay.getDateTime().getActualMaximum(Calendar.DAY_OF_MONTH);
                int day = sexualDay.getDateTime().getDay();
                int hour = sexualDay.getDateTime().getHour();

                final NumberPicker npYear = (NumberPicker) dialog.findViewById(R.id.npYear);
                final NumberPicker npMonth = (NumberPicker) dialog.findViewById(R.id.npMonth);
                final NumberPicker npDay = (NumberPicker) dialog.findViewById(R.id.npDay);
                final NumberPicker npHour = (NumberPicker) dialog.findViewById(R.id.npHour);
                Button btnOK = (Button) dialog.findViewById(R.id.btnOK);
                Button btnCancle = (Button) dialog.findViewById(R.id.btnCancel);
                npYear.setMinValue(year - 2);
                npYear.setMaxValue(year);
                npYear.setValue(year);
                npYear.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS); // 禁止对话框打开后数字选择框被选中
                npMonth.setMinValue(1);
                npMonth.setMaxValue(12);
                npMonth.setValue(month + 1);
                npMonth.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS); // 禁止对话框打开后数字选择框被选中
                npDay.setMinValue(1);
                npDay.setMaxValue(maxDay);
                npDay.setValue(day);
                npDay.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS); // 禁止对话框打开后数字选择框被选中
                npHour.setMinValue(0);
                npHour.setMaxValue(23);
                npHour.setValue(hour);
                npHour.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS); // 禁止对话框打开后数字选择框被选中
                btnOK.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            int year = npYear.getValue();
                            int month = npMonth.getValue() - 1;
                            int day = npDay.getValue();
                            int hour = npHour.getValue();
                            DateTime selectedDateTime = new DateTime(year, month, day, hour, 0, 0);
                            sd.setDateTime(selectedDateTime);
                            dataContext.updateSexualDay(sd);
                            recordListdAdapter.notifyDataSetChanged();
                            dialog.cancel();
                        } catch (Exception e) {
                            _Helper.printException(SexualDayRecordActivity.this, e);
                        }
                    }
                });
                btnCancle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            dialog.cancel();
                        } catch (Exception e) {
                            _Helper.printException(SexualDayRecordActivity.this, e);
                        }
                    }
                });
            } catch (Exception e) {
                _Helper.printException(SexualDayRecordActivity.this, e);
            }
        }

        public void show() {
            try {
                dialog.show();
            } catch (Exception e) {
                _Helper.printException(SexualDayRecordActivity.this, e);
            }
        }
    }


    public class AddSexualDayDialog {
        private Dialog dialog;

        public AddSexualDayDialog(DateTime dateTime) {
            try {
                dialog = new Dialog(SexualDayRecordActivity.this);
                dialog.setContentView(R.layout.inflate_dialog_date_picker);
                dialog.setTitle("设定时间");

                int year = dateTime.getYear();
                int month = dateTime.getMonth();
                int maxDay = dateTime.getActualMaximum(Calendar.DAY_OF_MONTH);
                int day = dateTime.getDay();
                int hour = dateTime.getHour();

                final NumberPicker npYear = (NumberPicker) dialog.findViewById(R.id.npYear);
                final NumberPicker npMonth = (NumberPicker) dialog.findViewById(R.id.npMonth);
                final NumberPicker npDay = (NumberPicker) dialog.findViewById(R.id.npDay);
                final NumberPicker npHour = (NumberPicker) dialog.findViewById(R.id.npHour);
                Button btnOK = (Button) dialog.findViewById(R.id.btnOK);
                Button btnCancle = (Button) dialog.findViewById(R.id.btnCancel);
                npYear.setMinValue(year - 2);
                npYear.setMaxValue(year);
                npYear.setValue(year);
                npYear.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS); // 禁止对话框打开后数字选择框被选中
                npMonth.setMinValue(1);
                npMonth.setMaxValue(12);
                npMonth.setValue(month + 1);
                npMonth.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS); // 禁止对话框打开后数字选择框被选中
                npDay.setMinValue(1);
                npDay.setMaxValue(maxDay);
                npDay.setValue(day);
                npDay.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS); // 禁止对话框打开后数字选择框被选中
                npHour.setMinValue(0);
                npHour.setMaxValue(23);
                npHour.setValue(hour);
                npHour.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS); // 禁止对话框打开后数字选择框被选中
                btnOK.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            int year = npYear.getValue();
                            int month = npMonth.getValue() - 1;
                            int day = npDay.getValue();
                            int hour = npHour.getValue();
                            DateTime selectedDateTime = new DateTime(year, month, day, hour, 0, 0);
                            SexualDay sexualDay = new SexualDay(selectedDateTime);
                            dataContext.addSexualDay(sexualDay);
                            recordListdAdapter.notifyDataSetChanged();
                            dialog.cancel();
                        } catch (Exception e) {
                            _Helper.printException(SexualDayRecordActivity.this, e);
                        }
                    }
                });
                btnCancle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            dialog.cancel();
                        } catch (Exception e) {
                            _Helper.printException(SexualDayRecordActivity.this, e);
                        }
                    }
                });
            } catch (Exception e) {
                _Helper.printException(SexualDayRecordActivity.this, e);
            }
        }

        public void show() {
            try {
                dialog.show();
            } catch (Exception e) {
                _Helper.printException(SexualDayRecordActivity.this, e);
            }
        }
    }

    private void snackbar(String message) {
        try {
            Snackbar.make(root, message, Snackbar.LENGTH_LONG).show();
        } catch (Exception e) {
            Log.e("wangsc", e.getMessage());
        }
    }
}

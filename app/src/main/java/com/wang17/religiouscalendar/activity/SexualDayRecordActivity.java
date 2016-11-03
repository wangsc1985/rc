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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wang17.religiouscalendar.R;
import com.wang17.religiouscalendar.fragment.ActionBarFragment;
import com.wang17.religiouscalendar.helper._String;
import com.wang17.religiouscalendar.model.DataContext;
import com.wang17.religiouscalendar.model.DateTime;
import com.wang17.religiouscalendar.model.SexualDay;

import java.util.Calendar;
import java.util.List;

public class SexualDayRecordActivity extends AppCompatActivity implements ActionBarFragment.OnActionFragmentBackListener {

    // 视图变量
    RelativeLayout root;
    // 类变量
    private DataContext dataContext;
    private List<SexualDay> sexualDays;
    private SexualDayListdAdapter recordListdAdapter;
    // 值变量
//    public static boolean lastDateTimeChanged=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_sexual_day_record);

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_action, ActionBarFragment.newInstance()).commit();
            dataContext = new DataContext(this);
            root = (RelativeLayout) findViewById(R.id.activity_sexual_day_record);
            sexualDays = dataContext.getSexualDays();
            ListView listView_sexualDays = (ListView) findViewById(R.id.listView_sexualDays);
            recordListdAdapter = new SexualDayListdAdapter();
            listView_sexualDays.setAdapter(recordListdAdapter);
        } catch (Exception e) {
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
            convertView = View.inflate(SexualDayRecordActivity.this, R.layout.inflate_list_item_sexual_day, null);
            final SexualDay sexualDay = sexualDays.get(position);
            DateTime nextDateTime = new DateTime();
            if (position + 1 < sexualDays.size()) {
                nextDateTime = sexualDays.get(position + 1).getDateTime();
            }
            int interval = (int)(nextDateTime.getTimeInMillis()-sexualDay.getDateTime().getTimeInMillis());

            TextView textView_start = (TextView) convertView.findViewById(R.id.textView_start);
            TextView textView_interval = (TextView) convertView.findViewById(R.id.textView_interval);
            ImageButton btnEdit = (ImageButton) convertView.findViewById(R.id.imageButton_edit);
            ImageButton btnDel = (ImageButton) convertView.findViewById(R.id.imageButton_del);
            btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditSexualDayDialog editSexualDayDialog = new EditSexualDayDialog(sexualDay);
                    editSexualDayDialog.show();
                }
            });
            btnDel.setOnClickListener(new View.OnClickListener() {
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
                    }
                }
            });
            textView_start.setText(sexualDay.getDateTime().toShortDateString()+"  "+sexualDay.getDateTime().getHour()+"点");
            int day = (int) (interval / 60000 / 60 / 24);
            int hour = (int) (interval / 60000 / 60 % 24);
            textView_interval.setText(_String.concat(day > 0 ? day + "天" : "", hour + "小时"));
            return convertView;
        }
    }


    public class EditSexualDayDialog {
        private Dialog dialog;

        public EditSexualDayDialog(SexualDay sexualDay) {

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
                    int year = npYear.getValue();
                    int month = npMonth.getValue() - 1;
                    int day = npDay.getValue();
                    int hour = npHour.getValue();
                    DateTime selectedDateTime = new DateTime(year, month, day, hour, 0, 0);
                    sd.setDateTime(selectedDateTime);
                    dataContext.updateSexualDay(sd);
                    recordListdAdapter.notifyDataSetChanged();
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


    public class AddSexualDayDialog {
        private Dialog dialog;

        public AddSexualDayDialog(DateTime dateTime) {
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
                    int year = npYear.getValue();
                    int month = npMonth.getValue() - 1;
                    int day = npDay.getValue();
                    int hour = npHour.getValue();
                    DateTime selectedDateTime = new DateTime(year, month, day, hour, 0, 0);
                    SexualDay sexualDay = new SexualDay(selectedDateTime);
                    dataContext.addSexualDay(sexualDay);
                    recordListdAdapter.notifyDataSetChanged();
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

    private void snackbar(String message) {
        try {
            Snackbar.make(root, message, Snackbar.LENGTH_LONG).show();
        } catch (Exception e) {
            Log.e("wangsc", e.getMessage());
        }
    }
}

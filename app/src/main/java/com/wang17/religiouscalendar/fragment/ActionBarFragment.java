package com.wang17.religiouscalendar.fragment;


import android.animation.AnimatorInflater;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.wang17.religiouscalendar.R;
import com.wang17.religiouscalendar.activity.SexualDayRecordActivity;
import com.wang17.religiouscalendar.helper._String;
import com.wang17.religiouscalendar.model.DataContext;
import com.wang17.religiouscalendar.model.DateTime;
import com.wang17.religiouscalendar.model.SexualDay;

import java.util.Calendar;

public class ActionBarFragment extends Fragment {

    //   视图变量
    View containerView;
    ImageView imageView_back;
    TextView textView_title;
    //   类变量
    private OnActionFragmentBackListener backListener;
    private DataContext dataContext;
    //   值变量
    public static final int TO_LIST = 0;


    public static ActionBarFragment newInstance() {
        ActionBarFragment fragment = new ActionBarFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        //  2016/10/19 初始化类变量和值变量
        super.onCreate(savedInstanceState);
        try {
            dataContext = new DataContext(getActivity());
        } catch (Exception e) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        containerView = inflater.inflate(R.layout.fragment_action_bar, container, false);
        //
        imageView_back = (ImageView) containerView.findViewById(R.id.imageView_back);
        imageView_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (backListener != null)
                    backListener.onBackListener();
            }
        });
        if(backListener ==null){
            imageView_back.setVisibility(View.INVISIBLE);
        }
        //
        textView_title = (TextView) containerView.findViewById(R.id.textView_title);
        textView_title.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/GONGFANG.ttf"));
        //
        ObjectAnimator objectAnimator = (ObjectAnimator) AnimatorInflater.loadAnimator(getContext(), R.animator.color_animator);
        objectAnimator.setEvaluator(new ArgbEvaluator());
        objectAnimator.setTarget(textView_title);
        objectAnimator.start();
        //
        textView_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getActivity(), SexualDayRecordActivity.class), TO_LIST);
            }
        });
        textView_title.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AddSexualDayDialog addSexualDayDialog = new AddSexualDayDialog(new DateTime());
                addSexualDayDialog.show();
                return true;
            }
        });
        setTextViewSexualText();
        return containerView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            if (context instanceof OnActionFragmentBackListener) {
                backListener = (OnActionFragmentBackListener) context;
            }
        } catch (Exception e) {
        }
    }


    private void setTextViewSexualText() {
        try {
            SexualDay lastSexualDay = dataContext.getLastSexualDay();
            if (lastSexualDay == null) {
                textView_title.setText("无记录");
            } else {
                long span = new DateTime().getTimeInMillis() - lastSexualDay.getDateTime().getTimeInMillis();
                int day = (int) (span / 60000 / 60 / 24);
                int hour = (int) (span / 60000 / 60 % 24);
                textView_title.setText(_String.concat(day > 0 ? day + "天" : "", hour + "小时"));
            }
        } catch (Exception e) {
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == TO_LIST) {
//            if(SexualDayRecordActivity.lastDateTimeChanged){
            setTextViewSexualText();
//            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 如果要使用返回按钮，需实现此接口
     */
    public interface OnActionFragmentBackListener {
        //  更新参数的类型和名字
        void onBackListener();
    }

    public class AddSexualDayDialog {
        private Dialog dialog;

        public AddSexualDayDialog(DateTime dateTime) {
            dialog = new Dialog(getContext());
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
                    setTextViewSexualText();
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
}

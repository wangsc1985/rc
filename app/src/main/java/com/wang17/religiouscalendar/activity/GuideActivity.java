package com.wang17.religiouscalendar.activity;

import android.animation.AnimatorInflater;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.wang17.religiouscalendar.R;
import com.wang17.religiouscalendar.helper.ViewPagerAdapter;
import com.wang17.religiouscalendar.helper._Helper;

import java.util.ArrayList;
import java.util.List;

public class GuideActivity extends AppCompatActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {

    private ViewPager vp;
    private ViewPagerAdapter vpAdapter;
    private List<View> views;
    //引导图片资源
    private static final int[] pics = {R.mipmap.guide001, R.mipmap.guide002, R.mipmap.guide003};
    public static String btnText = "进入软件";

    //底部小店图片
    private ImageView[] dots;

    //记录当前选中位置
    private int currentIndex;
    private TextView textView_close ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        try {
            Log.i("wangsc", "GuideActivity is loading ...");
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_guide);

            views = new ArrayList<View>();
            textView_close = (TextView)findViewById(R.id.textView_close);
            textView_close.setText(btnText);
            textView_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GuideActivity.this.finish();
                }
            });
            ObjectAnimator objectAnimator = (ObjectAnimator) AnimatorInflater.loadAnimator(GuideActivity.this, R.animator.warning_animator);
            objectAnimator.setEvaluator(new ArgbEvaluator());
            objectAnimator.setTarget(textView_close);
            objectAnimator.start();

            LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);

            //初始化引导图片列表
            for (int i = 0; i < pics.length; i++) {
                ImageView iv = new ImageView(this);
                iv.setLayoutParams(mParams);
                iv.setImageResource(pics[i]);
                views.add(iv);
            }
            vp = (ViewPager) findViewById(R.id.viewPage_content);
            //初始化Adapter
            vpAdapter = new ViewPagerAdapter(views);
            vp.setAdapter(vpAdapter);
            //绑定回调
            vp.setOnPageChangeListener(this);

            //初始化底部小点
            initDots();


        } catch (Exception ex) {
            _Helper.printExceptionSycn(this,uiHandler,ex);
        }
    }
private Handler uiHandler = new Handler();
    private void initDots() {
        LinearLayout layout_dots = (LinearLayout) findViewById(R.id.layout_dots);

        dots = new ImageView[pics.length];

        //循环取得小点图片
        for (int i = 0; i < pics.length; i++) {
            View view = getLayoutInflater().inflate(R.layout.inflate_dot, null);
            dots[i] = (ImageView) view.findViewById(R.id.imageView_dot);
            dots[i].setEnabled(true);//都设为灰色
            dots[i].setOnClickListener(this);
            dots[i].setTag(i);//设置位置tag，方便取出与当前位置对应
            layout_dots.addView(view);
        }

        currentIndex = 0;
        dots[currentIndex].setEnabled(false);//设置为白色，即选中状态
    }

    /**
     * 设置当前的引导页
     */
    private void setCurView(int position) {
        if (position < 0 || position >= pics.length) {
            return;
        }

        vp.setCurrentItem(position);
    }

    /**
     * 这只当前引导小点的选中
     */
    private void setCurDot(int positon) {
        if (positon < 0 || positon > pics.length - 1 || currentIndex == positon) {
            return;
        }

        dots[positon].setEnabled(false);
        dots[currentIndex].setEnabled(true);

        currentIndex = positon;

        if(positon==pics.length-1){
            textView_close.setVisibility(View.VISIBLE);
        }else{
            textView_close.setVisibility(View.GONE);
        }
    }

    //当滑动状态改变时调用
    @Override
    public void onPageScrollStateChanged(int arg0) {

    }

    //当当前页面被滑动时调用
    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {

    }

    //当新的页面被选中时调用
    @Override
    public void onPageSelected(int arg0) {
        //设置底部小点选中状态
        setCurDot(arg0);
    }

    @Override
    public void onClick(View v) {
        int position = (Integer) v.getTag();
        setCurView(position);
        setCurDot(position);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }
}

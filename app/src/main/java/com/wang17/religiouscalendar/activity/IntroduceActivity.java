package com.wang17.religiouscalendar.activity;

import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.TextView;

import com.wang17.religiouscalendar.R;
import com.wang17.religiouscalendar.fragment.ActionBarFragment;
import com.wang17.religiouscalendar.helper._Session;

public class IntroduceActivity extends AppCompatActivity implements ActionBarFragment.OnActionFragmentBackListener  {

    private TextView textViewIntroduce;
    public static final String PARAM_NAME = "ItemName";
    private Typeface fontHWZS, fontGF;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduce);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_action, ActionBarFragment.newInstance()).commit();


        AssetManager mgr = getAssets();//得到AssetManager
        fontHWZS = Typeface.createFromAsset(mgr, "fonts/STZHONGS.TTF");
        fontGF = Typeface.createFromAsset(mgr, "fonts/GONGFANG.ttf");

        String itemName = getIntent().getStringExtra(PARAM_NAME);
        textViewIntroduce = (TextView)findViewById(R.id.textView_introduce);
        textViewIntroduce.setTypeface(fontHWZS);
        textViewIntroduce.setLineSpacing(1f, 1.2f);
        textViewIntroduce.getPaint().setFakeBoldText(true);
        textViewIntroduce.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
        if(itemName.equals(ItemName.天地人禁忌.toString())) {
            textViewIntroduce.setText(R.string.tdr);
        }else if(itemName.equals(ItemName.文昌帝君戒淫文.toString())) {
            textViewIntroduce.setText(R.string.jyw);
        }else if(itemName.equals(ItemName.关于软件.toString())) {
            textViewIntroduce.setText(R.string.about);
        }
    }

    @Override
    public void onBackListener() {
        this.finish();
    }

    public enum ItemName{
        天地人禁忌,文昌帝君戒淫文,关于软件
    }
}

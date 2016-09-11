package com.onecivilization.MyOptimize.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.onecivilization.MyOptimize.R;
import com.onecivilization.MyOptimize.Util.AppManager;

import java.util.Locale;

/**
 * Created by CGZ on 2016/8/17.
 */
public class AboutActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.contact_developer).setOnClickListener(this);
        findViewById(R.id.help_translate).setOnClickListener(this);
        findViewById(R.id.view_source_code).setOnClickListener(this);
        findViewById(R.id.support_developer).setOnClickListener(this);
        findViewById(R.id.rate_app).setOnClickListener(this);
        findViewById(R.id.qq_group).setOnClickListener(this);
        if (AppManager.LOCALE.equals(Locale.ENGLISH)) {
            findViewById(R.id.qq_group).setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.contact_developer:
                intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:onecivilization.cn@gmail.com"));
                intent.putExtra(Intent.EXTRA_SUBJECT, "Contact：My Optimize");
                try {
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(this, R.string.email_not_supported, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.help_translate:
                intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:onecivilization.cn@gmail.com"));
                intent.putExtra(Intent.EXTRA_SUBJECT, "Translation：My Optimize");
                try {
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(this, R.string.email_not_supported, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.view_source_code:
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://github.com/OneCivilization/MyWorld"));
                startActivity(intent);
                break;
            case R.id.support_developer:
                View view = LayoutInflater.from(this).inflate(R.layout.dialog_support_developer, null);
                view.findViewById(R.id.alipay).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("https://ds.alipay.com/?from=mobilecodec&scheme=alipayqr%3A%2F%2Fplatformapi%2Fstartapp%3FsaId%3D10000007%26clientVersion%3D3.7.0.0718%26qrcode%3Dhttps%253A%252F%252Fqr.alipay.com%252Fapx09315ymtd22do9gmro15%253F_s%253Dweb-other"));
                        startActivity(intent);
                    }
                });
                view.findViewById(R.id.paypal).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("https://paypal.me/onecivilization"));
                        startActivity(intent);
                    }
                });
                new AlertDialog.Builder(this).setTitle(R.string.support_developer)
                        .setView(view)
                        .setPositiveButton(R.string.close, null)
                        .create().show();
                break;
            case R.id.rate_app:
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("market://details?id=" + getPackageName()));
                try {
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(this, R.string.rate_not_supported, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.qq_group:
                if (!joinQQGroup("q9XRXWfXvdVqmQB6Myd5mzIcP5CqcnXr")) {
                    Toast.makeText(this, "您的手机中未安装QQ或QQ版本不支持一键加群，请手动加群至479150319", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    /****************
     * 发起添加群流程。群号：My Optimize 用户交流群(479150319) 的 key 为： q9XRXWfXvdVqmQB6Myd5mzIcP5CqcnXr
     * 调用 joinQQGroup(q9XRXWfXvdVqmQB6Myd5mzIcP5CqcnXr) 即可发起手Q客户端申请加群 My Optimize 用户交流群(479150319)
     *
     * @param key 由官网生成的key
     * @return 返回true表示呼起手Q成功，返回fals表示呼起失败
     ******************/
    public boolean joinQQGroup(String key) {
        Intent intent = new Intent();
        intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D" + key));
        // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            startActivity(intent);
            return true;
        } catch (Exception e) {
            // 未安装手Q或安装的版本不支持
            return false;
        }
    }

}

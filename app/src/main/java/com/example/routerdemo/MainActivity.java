package com.example.routerdemo;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.callback.NavigationCallback;
import com.alibaba.android.arouter.launcher.ARouter;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_login).setOnClickListener(v -> {
            ARouter.getInstance().build("/login/LoginActivity").navigation();
        });

        findViewById(R.id.btn_share).setOnClickListener(v -> {
//            ARouter.getInstance().build("/share/ShareActivity").navigation();
            ARouter.getInstance().build("/share/ShareActivity")
                    .withBoolean("check_login",true)//先判断程序是否登录
                    .navigation(null, navigationCallback);
        });

    }
    //拦截器回调
    private NavigationCallback navigationCallback = new NavigationCallback() {
        @Override
        public void onFound(Postcard postcard) {
            System.out.println("navigationCallback onFound():"+postcard.getPath());
        }
        @Override
        public void onLost(Postcard postcard) {
            System.out.println("navigationCallback onLost():"+postcard.getPath());
        }

        @Override
        public void onArrival(Postcard postcard) {
        }

        @Override
        public void onInterrupt(Postcard postcard) {
            //如果被拦截器拦截之后会收到onInterrupt()回调
            System.out.println("navigationCallback onInterrupt():"+postcard.getPath());
            if(postcard.getPath().equals("/share/ShareActivity")){
                ARouter.getInstance().build("/login/LoginActivity").navigation();
            }
        }
    };
}
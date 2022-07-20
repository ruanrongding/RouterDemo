package com.example.share;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.example.commonlibs.service.LoginService;
import com.example.commonlibs.service.RegisterService;
import com.example.commonlibs.service.ServiceFactory;
@Route(path = "/share/ShareActivity")
public class ShareActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);


        findViewById(R.id.share).setOnClickListener(v -> {
            if(ServiceFactory.getInstance().getLoginService().isLogin()){
                Toast.makeText(ShareActivity.this,"分享成功！",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(ShareActivity.this,"分享失败，请先登录！",Toast.LENGTH_SHORT).show();
                ARouter.getInstance().build("/login/LoginActivity").navigation();
        }
        });


        /**
         *通过ARouter调用login模块中RegisterServiceImpl中的doRegister()方法
         */
        RegisterService registerService = (RegisterService) ARouter.getInstance().build("/login/doRegister").navigation();
        findViewById(R.id.share_rigester).setOnClickListener(v -> {
            ((TextView)findViewById(R.id.tv_show)).setText(registerService.doRegister("aa","bb"));
        });
    }
}



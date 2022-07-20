package com.example.login;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.example.commonlibs.service.ServiceFactory;
import com.example.login.service.AccountService;

@Route(path = "/login/LoginActivity")
public class LoginActivity extends AppCompatActivity {

    // 2.需要由ARouter传递的参数需要加@Autowired注解
    @Autowired
    public int intValue;
    @Autowired
    public String strValue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginUtils.isLogin = true;
                ServiceFactory.getInstance().setLoginService(new AccountService(true,LoginUtils.password));
                Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
            }
        });


        // 3.ARouter.getInstance().inject(this)方法会自动完成参数注入
        ARouter.getInstance().inject(this);

        System.out.println("intValue =" +intValue +"===> strValue="+strValue);
    }
}
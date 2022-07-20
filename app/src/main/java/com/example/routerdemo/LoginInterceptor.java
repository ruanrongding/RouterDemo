package com.example.routerdemo;
import android.content.Context;
import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.annotation.Interceptor;
import com.alibaba.android.arouter.facade.callback.InterceptorCallback;
import com.alibaba.android.arouter.facade.template.IInterceptor;
import com.alibaba.android.arouter.launcher.ARouter;
import com.example.login.LoginUtils;

/**
 * 登录拦截器，判断App是否初始化了登录状态
 */
@Interceptor(priority = 1,name = "登录状态拦截器")
public class LoginInterceptor implements IInterceptor {
    @Override
    public void process(Postcard postcard, InterceptorCallback callback) {
        System.out.println("LoginIntercepter process()");
        System.out.println("path:"+ postcard.getPath());
         if(postcard.getExtras().getBoolean("check_login")){
             if(!LoginUtils.isLogin){//判断出如果没有进行登录，去登录页面登录
                 System.out.println("没有登录去登录");
                 ARouter.getInstance().build("/login/LoginActivity").navigation();
                 //没有登录抛出异常
//                callback.onInterrupt(new RuntimeException());
             }

         }
         callback.onContinue(postcard);
    }

    @Override
    public void init(Context context) {

    }
}

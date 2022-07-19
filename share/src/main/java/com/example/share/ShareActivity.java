package com.example.share;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.commonlibs.service.ServiceFactory;

public class ShareActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        findViewById(R.id.share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ServiceFactory.getInstance().getLoginService().isLogin()){
                    Toast.makeText(ShareActivity.this,"分享成功！",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(ShareActivity.this,"分享失败，请先登录！",Toast.LENGTH_SHORT).show();
            }
            }
        });
    }
}



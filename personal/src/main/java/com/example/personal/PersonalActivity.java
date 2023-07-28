package com.example.personal;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.annotation.MyRouter;
import com.example.router_api.RouterManger;

@MyRouter(path = "/personal/PersonalActivity", group = "personal")
public class PersonalActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal);
        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            String enterFrom = getIntent().getExtras().getString("enterFrom","");
            if("OrderActivity".equals(enterFrom)){
                Toast.makeText(this, "路由跳转成功", Toast.LENGTH_SHORT).show();
            }
        }
        View viewById = findViewById(R.id.personalTx);
        viewById.setOnClickListener(view -> {
            RouterManger.build("/order/OrderActivity")
                    .withBoolean("show",true)
                    .withString("Str","这是来自Person版本")
                    .withString("enterFrom", "PersonalActivity")
                    .navigation(this);
        });
    }
}
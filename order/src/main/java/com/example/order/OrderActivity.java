package com.example.order;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.annotation.MyRouter;
import com.example.router_api.Router;
import com.example.router_api.RouterManger;

import java.util.Objects;


@MyRouter(path = "/order/OrderActivity", group = "order")
public class OrderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        TextView view = findViewById(R.id.orderTx);
        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            String enterFrom = getIntent().getExtras().getString("enterFrom","");
            if("PersonalActivity".equals(enterFrom)){
                Toast.makeText(this, "路由跳转成功", Toast.LENGTH_SHORT).show();
            }
        }
        view.setOnClickListener(tv -> {
            RouterManger.build("/personal/PersonalActivity")
                    .withBoolean("show",true)
                    .withString("Str","这是来自order版本")
                    .withString("enterFrom", "OrderActivity")
                    .navigation(this);
//            Intent intent = new Intent(OrderActivity.this, PersonalActivity.class);
//            startActivity(intent);
        });
    }
}
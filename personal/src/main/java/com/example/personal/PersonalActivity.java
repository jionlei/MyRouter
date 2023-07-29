package com.example.personal;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.annotation.MyRouter;
import com.example.annotation.MyRouterParameter;
import com.example.router_api.RouterManger;

@MyRouter(path = "/personal/PersonalActivity", group = "personal")
public class PersonalActivity extends AppCompatActivity {

    @MyRouterParameter(key = "index") // 从index中获取值
    public int fromIndex;

    @MyRouterParameter
    public boolean isFromActivity;

    public String fromActivityName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String enterFrom = getIntent().getExtras().getString("enterFrom", "");
            switch (enterFrom) {
                case "OrderActivity":
                    Toast.makeText(this, "路由跳转成功", Toast.LENGTH_SHORT).show();
                    break;
                case "OrderActivity_ref":
                    Toast.makeText(this, "反射跳转也能成功，没有使用组件依赖", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Toast.makeText(this, "其他方式跳转成功", Toast.LENGTH_SHORT).show();
            }
        }
        View viewById = findViewById(R.id.personalTx);
        viewById.setOnClickListener(view -> {
            RouterManger.build("/order/OrderActivity")
                    .withBoolean("show", true)
                    .withString("Str", "这是来自Person版本")
                    .withString("enterFrom", "PersonalActivity")
                    .withInt("index", 1)
                    .withBoolean("isFromActivity", true)
                    .navigation(this);
        });
    }
}
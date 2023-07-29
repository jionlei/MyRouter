package com.example.order;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.annotation.MyRouter;
import com.example.annotation.MyRouterParameter;
import com.example.router_api.ParameterManger;
import com.example.router_api.RouterManger;



@MyRouter(path = "/order/OrderActivity", group = "order")
public class OrderActivity extends AppCompatActivity {
    private static final String TAG = "OrderActivity";
    @MyRouterParameter(key = "index")
    public int fromIndex;

    @MyRouterParameter
    public boolean isFromActivity;

    @MyRouterParameter
    public String fromActivityName;
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

        ParameterManger.getInstance().loadParameter(this);
        view.setOnClickListener(tv -> {


            // 通过反射跳转
//            try {
//                Intent intent = new Intent(this, Class.forName("com.example.personal.PersonalActivity"));
//                intent.putExtra("enterFrom","OrderActivity_ref");
//                startActivity(intent);
//            } catch (ClassNotFoundException e) {
//                throw new RuntimeException(e);
//            }


     //         路由本质上也是反射跳转
            RouterManger.build("/personal/PersonalActivity")
                    .withBoolean("show",true)
                    .withString("Str","这是来自order版本")
                    .withInt("index", 1)
                    .withBoolean("isFromActivity",true)
                    .withString("enterFrom", "OrderActivity")
                    .navigation(this);


//            Intent intent = new Intent(OrderActivity.this, PersonalActivity.class);
//            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: fromIndex = " + fromIndex);
        Log.d(TAG, "onResume: isFromActivity = " + isFromActivity);
        Log.d(TAG, "onResume: fromActivityName = " + fromActivityName);
    }
}
package com.yidont.al;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {


//    NewWinImage mWinImage;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(),NewMainActivity.class));
            }
        });



//
//        mWinImage = new NewWinImage(this);
//
//        mWinImage.setOnMenuClick(new onMenuClick() {
//            @Override
//            public void onExit() {
//
//                Toast.makeText(getBaseContext(),"退出",Toast.LENGTH_SHORT).show();
//
//            }
//
//            @Override
//            public void onRefresh() {
//                Toast.makeText(getBaseContext(),"刷新",Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onShare() {
//                Toast.makeText(getBaseContext(),"分享",Toast.LENGTH_SHORT).show();
//            }
//        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

//        mWinImage.removeView();
    }
}

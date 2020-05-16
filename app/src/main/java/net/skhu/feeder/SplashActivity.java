package net.skhu.feeder;

/*
참고사이트 https://yongtech.tistory.com/100
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class SplashActivity extends Activity {

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        try {
            Thread.sleep(4000); // 4초 인트로 화면 보여주기

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // next Activity 기재.

        startActivity(new Intent(this,MainActivity.class));
        finish();
    }

}

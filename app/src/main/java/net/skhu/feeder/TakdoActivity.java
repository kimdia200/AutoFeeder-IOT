package net.skhu.feeder;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class TakdoActivity extends AppCompatActivity {

    TextView txt1,txt2,txt3,txt4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_takdo);

        txt1 = (TextView)findViewById(R.id.textView30);
        txt2 = (TextView)findViewById(R.id.textView31);
        txt3 = (TextView)findViewById(R.id.textView32);
        txt4 = (TextView)findViewById(R.id.textView33);

        String takdo = getIntent().getStringExtra("takdo");

        if(takdo==null){
            txt4.setText("연결되지 않음");
        }else{
            if(Integer.parseInt(takdo)<=23){
                txt2.setText("더러움");
            }else if(Integer.parseInt(takdo)<=26){
                txt2.setText("보통");
            }else{
                txt2.setText("좋음");
            }
            txt3.setText("  "+takdo);
        }
    }
}

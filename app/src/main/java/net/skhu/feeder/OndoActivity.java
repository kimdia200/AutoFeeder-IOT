package net.skhu.feeder;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class OndoActivity extends AppCompatActivity {

    TextView txt1,txt2,txt3,txt4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ondo);

        txt1 = (TextView)findViewById(R.id.textView20);
        txt2 = (TextView)findViewById(R.id.textView21);
        txt3 = (TextView)findViewById(R.id.textView22);
        txt4 = (TextView)findViewById(R.id.textView23);

        String ondo = getIntent().getStringExtra("ondo");

        if(ondo==null){
            txt4.setText("연결되지 않음");
        }else{
            if(Integer.parseInt(ondo)<15){
                txt2.setText("낮음");
            }else if(Integer.parseInt(ondo)<25){
                txt2.setText("좋음");
            }else{
                txt2.setText("높음");
            }
            txt3.setText(ondo+"도");
        }
    }
}

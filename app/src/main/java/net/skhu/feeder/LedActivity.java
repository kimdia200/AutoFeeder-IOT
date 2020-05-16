package net.skhu.feeder;
/*
ColorPicker 라이브러리 참고URL : https://github.com/QuadFlask/colorpicker

LED색상 8자리를 명도채도 배제하고 6자리로 전달함
 */
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorChangedListener;
import com.flask.colorpicker.OnColorSelectedListener;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class    LedActivity extends AppCompatActivity {
    ColorPickerView colorPickerView;
    Button button;
    Button button2;
    int selectColor;
    private MqttClient mqttClient;
    MqttConnectOptions options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_led);

        //적용 버튼객체 생성
        button = (Button)findViewById(R.id.btn_setColor);
        //LED OFF버튼 객체생성
        button2 = (Button)findViewById(R.id.btn_offColor);

        //설정했던 색으로 버튼색 변경 (sharedPreferences 불러와서 사용)
        SharedPreferences feed = getSharedPreferences("feed", Activity.MODE_PRIVATE);
        //sharedPreferences에 설정해둔 값을 가져옴
        selectColor = feed.getInt("setColor",0);
        //버튼의 색을 저장해두었던 색으로 변경
        button.setBackgroundColor(selectColor);

        //ColorPickerView 객체 생성
        colorPickerView = findViewById(R.id.color_picker_view);

        //손가락을 떼지않고 계속 드래그 하면서 선택해도 색깔마다 다 로그 적용되서 뜸
        colorPickerView.addOnColorChangedListener(new OnColorChangedListener() {
            @Override public void onColorChanged(int selectedColor) {
                // Handle on color change
                Log.d("로그", "onColorChanged: 0x" + Integer.toHexString(selectedColor));
                button.setBackgroundColor(selectedColor);
            }
        });

        //손가락뗄때 최종 선택되는 반응을 캐치하는 리스너
        colorPickerView.addOnColorSelectedListener(new OnColorSelectedListener() {
            @Override
            public void onColorSelected(int selectedColor) {
                Toast.makeText(LedActivity.this,"선택색상: " + Integer.toHexString(selectedColor).toUpperCase(),Toast.LENGTH_SHORT).show();
                Log.d("로그", "onColorChanged: 0x" + Integer.toHexString(selectedColor).toUpperCase());
                //변수 selectColor에 현재 colorPicker에서 선택된 색상값을 넣어줌
                selectColor = selectedColor;
            }
        });

        //클라이언트 url, id설정 및  mqtt연결
        try {
            mqttClient = new MqttClient("tcp://tailor.cloudmqtt.com:14221","Feeder_led",new MemoryPersistence());
            options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);
            options.setCleanSession(true);
            options.setUserName("tvtolaaa");
            options.setPassword("mlbD5GoD8tV_".toCharArray());
//            mqttClient.connect(options);
//            Toast.makeText(this, "mqtt연결됨", Toast.LENGTH_SHORT).show();
//            mqttClient.subscribeWithResponse("test/toAndroid", 0, new IMqttMessageListener() {
//                @Override
//                public void messageArrived(String topic, MqttMessage message) throws Exception {
//                    //전달받은값 표현해주는 로그
//                    Log.d("로그",topic+" : "+new String(message.getPayload()));
//                    //이 액티비티는 전달받은 값으로 표현해줄게 없고 전송만 목적이므로 확인용으로만 둔다
//                }
//            });
        } catch (MqttException e) {
            Toast.makeText(this, "mqtt연결되지 않음1", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            Log.d("로그","오류"+e.toString());
        }

        //색적용 버튼에 달아줄 리스너 작성
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //버튼 클릭시 버튼의 색을 선택된 색으로 변경해줌
                button.setBackgroundColor(selectColor);
                //sharedPreferences에 값을 저장함
                saveColor(selectColor);
                //액티비티 종료
                finish();
            }
        };
        //버튼에 리스너 부착
        button.setOnClickListener(listener);

        View.OnClickListener listener1 = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Led off함수
                sendColorOff();
                //액티비티 종료
                finish();
            }
        };
        button2.setOnClickListener(listener1);
    }

    //sharedpreference에 색저장하는 함수
    public void saveColor(int color){
        SharedPreferences feed = getSharedPreferences("feed",Activity.MODE_PRIVATE);
        SharedPreferences.Editor edit = feed.edit();
        edit.putInt("setColor",color);
        edit.commit();
        sendColor(color);
    }
    public void sendColor(int color){
        try {
            mqttClient.connect(options);
            Log.d("로그","mqtt연결됨");
        } catch (MqttException e) {
            Log.d("로그","mqtt연결실패");
            e.printStackTrace();
        }
        //즉시급여 통신 구현
        if(mqttClient.isConnected()==true){
            try {
                String temp = Integer.toHexString(color).toUpperCase().substring(2,8);
                mqttClient.publish("test/toArduino", new MqttMessage(temp.getBytes()));
                Log.d("로그",temp+"컬러색상 mqtt통신으로 보내는데 성공함");
                Toast.makeText(this, temp+"색으로 변경완료", Toast.LENGTH_SHORT).show();
                mqttClient.disconnect();
                Log.d("로그","mqtt연결 해제");
            } catch (MqttException e) {
                Log.d("로그","컬러색상 mqtt통신으로 보내는데 실패함");
                try {
                    mqttClient.disconnect();
                } catch (MqttException ex) {
                    ex.printStackTrace();
                }
                Log.d("로그","mqtt연결 해제");
                e.printStackTrace();
            }
        }else{
            Log.d("로그","mqtt통신실패");
        }
    }
    public void sendColorOff(){
        try {
            mqttClient.connect(options);
            Log.d("로그","mqtt연결됨");
        } catch (MqttException e) {
            Log.d("로그","mqtt연결실패");
            e.printStackTrace();
        }
        //즉시급여 통신 구현
        if(mqttClient.isConnected()==true){
            try {
                mqttClient.publish("test/toArduino", new MqttMessage("000000".getBytes()));
                Log.d("로그","Led Off메시지 mqtt통신으로 보내는데 성공함");
                Toast.makeText(this, "Led Off로 변경완료", Toast.LENGTH_SHORT).show();
                mqttClient.disconnect();
                Log.d("로그","mqtt연결 해제");
            } catch (MqttException e) {
                Log.d("로그","Led Off메시지 mqtt통신으로 보내는데 실패함");
                try {
                    mqttClient.disconnect();
                } catch (MqttException ex) {
                    ex.printStackTrace();
                }
                Log.d("로그","mqtt연결 해제");
                e.printStackTrace();
            }
        }else{
            Log.d("로그","mqtt통신실패");
        }
    }
}

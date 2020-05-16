package net.skhu.feeder;
/*
textView를 수정하는게 왜안되지.....
 */
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MainActivity extends AppCompatActivity {

    ImageView img1;
    ImageView img2;
    ImageView img3;
    MqttClient mqttClient;
    String intentOndo;
    String intentTakdo;
    MqttConnectOptions options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        img3= (ImageView)findViewById(R.id.imageView3);
        img1= (ImageView)findViewById(R.id.imageView);
        //온도계 이미지
        img2= (ImageView)findViewById(R.id.imageView2);
        //탁도계 이미지
        Button btnFeed = (Button)findViewById(R.id.button);
        //먹이주기 버튼 객체 생성
        Button btnLed = (Button)findViewById(R.id.button2);
        //LED설정 버튼 객체 생성

        intentOndo=null;
        intentTakdo=null;

        //클라이언트 url, id설정 및  mqtt연결
        try {
            mqttClient = new MqttClient("tcp://tailor.cloudmqtt.com:14221","Feeder_main",new MemoryPersistence());
            options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);
            options.setCleanSession(true);
            options.setUserName("tvtolaaa");
            options.setPassword("mlbD5GoD8tV_".toCharArray());
            mqttClient.connect(options);
            Log.d("로그","mqtt연결됨");
            Toast.makeText(this, "mqtt연결됨", Toast.LENGTH_SHORT).show();
            mqttClient.subscribeWithResponse("test/toAndroid", 0, new IMqttMessageListener() {
                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    String msg = new String(message.getPayload());
                    if(msg.contains(new String("온도"))==true){
                        //온도, 탁도정보를 받았을때
                        String a=msg.substring(2,msg.indexOf("탁"));
                        String b=msg.substring(msg.indexOf("탁")+2,msg.length());

                        Log.d("로그","전달받은값 : 온도:,"+a+" 탁도:,"+b);

                        setImg(a,b);

                        Log.d("로그","하잇");
                    }else{
                        //전달받은값 표현해주는 로그
                        Log.d("로그",topic+"전달받은 메시지 : "+new String(message.getPayload()));
                    }
                    //여기부분에 가져온값으로 어떻게 표현해줄건지 나타내야함
                }
            });
        } catch (MqttException e) {
            Toast.makeText(this, "mqtt연결되지 않음1", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            Log.d("로그","오류"+e.toString());
        }

        //처음 액티비티 실행시 온도, 탁도 정보를 보내달라느 신호를 보냄
        try {
            check();
        } catch (MqttException e) {
            e.printStackTrace();
        }

        //이미지뷰 클릭시 일어나는 온클릭
        View.OnClickListener  logoListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    check();
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        };img3.setOnClickListener(logoListener);

        //온도계 이미지 클릭시
        View.OnClickListener listener1 = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),OndoActivity.class);
                intent.putExtra("ondo",intentOndo);
                startActivity(intent);
            }
        };
        img1.setOnClickListener(listener1);

        //탁도계 이미지 클릭시
        View.OnClickListener listener2 = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),TakdoActivity.class);
                intent.putExtra("takdo",intentTakdo);
                startActivity(intent);
            }
        };
        img2.setOnClickListener(listener2);



        //먹이주기 버튼 클릭시 작동하는 리스너 작성, 버튼에 리스너 장착
        //클릭시 먹이주기 액티비티로 넘어감
        View.OnClickListener listener3 = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),FeedActivity.class);
                startActivity(intent);
            }
        };
        btnFeed.setOnClickListener(listener3);

        //LED설정 버튼 클릭시 작동하는 리스너 작성, 버튼에 리스너 장착
        //클릭시 LED설정 액티비티로 넘어감
        View.OnClickListener listener4 = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),LedActivity.class);
                startActivity(intent);
            }
        };
        btnLed.setOnClickListener(listener4);
    }

    //현재 수온, 탁도에 따른 이미지 배정 및 intent값 설정;
    //현재 기준값들은 임의 배정
    public void setImg(String ondo, String takdo){
        int a = Integer.parseInt(ondo);
        if(a<15){
            img1.setImageResource(R.mipmap.temp1);
        }else if(a<25){
            img1.setImageResource(R.mipmap.temp2);
        }else{
            img1.setImageResource(R.mipmap.temp3);
        }
        intentOndo = ondo;
        Log.d("로그","수온함수끝");
        int b= Integer.parseInt(takdo);

        if(b<=23){
            img2.setImageResource(R.mipmap.tak3);
        }else if(b<=26){
            img2.setImageResource(R.mipmap.tak2);
        }else{
            img2.setImageResource(R.mipmap.tak1);
        }
        intentTakdo=takdo;
        Log.d("로그","탁도함수끝");
    }
    //온도, 탁도 받아오는 함수
    public void check() throws MqttException {
        if(mqttClient.isConnected()==true){
            try {
                mqttClient.publish("test/toArduino", new MqttMessage("CHECK".getBytes()));
                Log.d("로그","check명령어 mqtt통신으로 보내는데 성공함");
                Toast.makeText(this, "온도 탁도 정보 요청", Toast.LENGTH_SHORT).show();
            } catch (MqttException e) {
                Log.d("로그","check명령어 mqtt통신으로 보내는데 실패함");
                e.printStackTrace();
            }
        }else{
            Log.d("로그","mqtt통신실패");
        }
    }
}

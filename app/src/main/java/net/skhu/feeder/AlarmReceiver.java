package net.skhu.feeder;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;


public class AlarmReceiver extends BroadcastReceiver {
    Context context;
    private MqttClient mqttClient;
    MqttConnectOptions options;

    @Override
    public void onReceive(Context context, Intent intent) {

        //알람작동시 일어나는 작업
        this.context = context;

        Log.d("로그","AlarmReceiver의 onReceive()작동!");
        //예약급여 작동
        //클라이언트 url, id설정 및  mqtt연결
        try {
            mqttClient = new MqttClient("tcp://tailor.cloudmqtt.com:14221","Feeder_Receiver",new MemoryPersistence());
            options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);
            options.setCleanSession(true);
            options.setUserName("tvtolaaa");
            options.setPassword("mlbD5GoD8tV_".toCharArray());
//            mqttClient.connect(options);
//            Toast.makeText(context, "mqtt연결됨", Toast.LENGTH_SHORT).show();
//            mqttClient.subscribeWithResponse("test/toAndroid", 0, new IMqttMessageListener() {
//                @Override
//                public void messageArrived(String topic, MqttMessage message) throws Exception {
//                    //전달받은값 표현해주는 로그
//                    Log.d("로그",topic+" : "+new String(message.getPayload()));
//                    //이 액티비티는 전달받은 값으로 표현해줄게 없고 전송만 목적이므로 확인용으로만 둔다
//                }
//            });
        } catch (MqttException e) {
            Toast.makeText(context, "mqtt연결되지 않음1", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            Log.d("로그","오류"+e.toString());
        }
        //송신
        //급여 통신 구현
        try {
            mqttClient.connect(options);
            Log.d("로그","mqtt연결됨");
        } catch (MqttException e) {
            Log.d("로그","mqtt연결실패");
            e.printStackTrace();
        }
        if(mqttClient.isConnected()==true){
            try {
                mqttClient.publish("test/toArduino", new MqttMessage("FEED".getBytes()));
                Log.d("로그","feed명령어 mqtt통신으로 보내는데 성공함");
                Toast.makeText(context, "예약급여 완료", Toast.LENGTH_SHORT).show();
                mqttClient.disconnect();
                Log.d("로그","mqtt연결 해제");
            } catch (MqttException e) {
                Log.d("로그","feed명령어 mqtt통신으로 보내는데 실패함");
                e.printStackTrace();
                try {
                    mqttClient.disconnect();
                    Log.d("로그","mqtt연결 해제");
                } catch (MqttException ex) {
                    ex.printStackTrace();
                }
            }
        }else{
            Log.d("로그","mqtt통신실패");
        }

        //노티피케이션을 작동시키는 서비스 작동
        Intent service_intent = new Intent(context, NotificationService.class);
//        NotificationService에 접근하는법
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){
            this.context.startForegroundService(service_intent);
        }else{
            this.context.startService(service_intent);
        }
    }
}

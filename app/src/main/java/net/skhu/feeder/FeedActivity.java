package net.skhu.feeder;
/*
모든기능 완료오~~~~
 */
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.QuickContactBadge;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.w3c.dom.Text;

import java.sql.Time;
import java.util.Calendar;


public class FeedActivity extends AppCompatActivity {

    Button btn_feedNow;
    Button btn_timeSet;
    CheckBox check;
    TextView txtTime;
    TextView txtAutoStatus;
    TextView txtAutoTime;
    TextView txtAutoTimeSet;
    TimePicker mTimePicker;
    int feedHour,feedMin;
    String checkStatus;
    AlarmManager alarmMgr;
    PendingIntent alarmIntent;
    private MqttClient mqttClient;
    MqttConnectOptions options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        //즉시급여 버튼
        btn_feedNow = (Button)findViewById(R.id.button3);
        //예약급여 시간설정버튼
        btn_timeSet = (Button)findViewById(R.id.button4);
        //예약급여 ON/OFF스위치
        check = (CheckBox)findViewById(R.id.checkBox);
        //현재 예약시간 TextView
        txtTime = (TextView)findViewById(R.id.textView10);
        //현재 예약급여 작동 여부 TextView
        txtAutoStatus = (TextView)findViewById(R.id.textView8);
        //현재 예약시간 TextView
        txtAutoTime = (TextView)findViewById(R.id.textView9);
        //시간설정 TextView
        txtAutoTimeSet = (TextView)findViewById(R.id.textView11);
        //TimePicker
        mTimePicker = (TimePicker)findViewById(R.id.timePicker);
//        mTimePicker.setIs24HourView(true);
        //클라이언트 url, id설정 및  mqtt연결
        try {
            mqttClient = new MqttClient("tcp://tailor.cloudmqtt.com:14221","Feeder_feed",new MemoryPersistence());
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
        alarmMgr = (AlarmManager)FeedActivity.this.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(FeedActivity.this,AlarmReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(FeedActivity.this, 0, intent, 0);

        //SharedPreferences객체 생성(저장해둔 값을 사용하기 위함) 및 저장했던값 불러오기
        SharedPreferences feed = getSharedPreferences("feed", Activity.MODE_PRIVATE);
        //sharedPreferences에 설정해둔 예약시간(setHour, setMin)이 있다면 가져옴, 없다면 초기값은 0시0분으로 설정함
        feedHour = feed.getInt("setHour",0);
        feedMin = feed.getInt("setMin",0);
        //sharedPreferences에 설정해둔 체크박스 상태(setStatus)를 가져옴, 없다면 초기값은 OFF로 설정함
        checkStatus = feed.getString("setStatus","OFF");

        Log.d("로그","sharedPreference불러오기 완료");

        //현재 예약시간 TextView 값 변경
        if(feedHour<12){
            txtTime.setText("[오전] "+feedHour+"시 "+feedMin+"분");
        }else{
            txtTime.setText("[오후] "+feedHour+"시 "+feedMin+"분");
        }


        //CheckBox관련
        //참고]]]checkBox.isChecked()체크 여부 확인 메서드, checkBox.setChecked(true)//체크상태 변경 메서드

        //설정되어있다면 값을 가져와서 체크박스 선택/해제 적용하기
        if(checkStatus.equals("ON")==true){
            Log.d("로그","체크박스 상태적용, 상태=on");
            check.setChecked(true);
            hide(checkStatus);
            txtAutoStatus.setText("현재 예약급여 상태 "+checkStatus);
        }else if(checkStatus.equals("OFF")==true){
            Log.d("로그","체크박스 상태 적용, 상태=off");
            check.setChecked(false);
            hide(checkStatus);
            txtAutoStatus.setText("현재 예약급여 상태 "+checkStatus);
        }

        //CheckBox에 달아줄 리스너 작성
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(check.isChecked()==true){
                    Log.d("로그","체크박스 리스너 상태 = on");
                    //체크를 한다면 SharedPreferences에 ON으로 저장하고 급여모드 상태 TextView 변경(함수 사용)
                    setCheck("ON");
                    startAlarm();
                }else if(check.isChecked()==false){
                    Log.d("로그","체크박스 리스너 상태 = off");
                    //체크를 푼다면 SharedPreferences에 OFF로 저장하고 급여모드 상태 TextView 변경(함수 사용)
                    setCheck("OFF");
                    stopAlarm();
                }
            }
        };
        check.setOnClickListener(listener);



        //즉시급여 버튼에 달아줄 리스너 작성
        View.OnClickListener listener1 = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //즉시급여 함수 호출
                Log.d("로그","즉시급여 버튼클릭");
                try {
                    feedNow();
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        };
        //즉시급여 버튼에 리스너 장착
        btn_feedNow.setOnClickListener(listener1);

        //예약급여 시간설정 버튼에 달아줄 리스너 작성
        View.OnClickListener listener2 = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //시간설정 함수 호출
                Log.d("로그","예약급여 버튼클릭");
                timeSet();
            }
        };
        //예약급여 시간설정 버튼에 리스너 장착
        btn_timeSet.setOnClickListener(listener2);


    }

    //즉시 급여 함수 구현
    public void feedNow() throws MqttException {
        //즉시급여 통신 구현
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
                Toast.makeText(this, "즉시급여 완료", Toast.LENGTH_SHORT).show();
                mqttClient.disconnect();
                Log.d("로그","mqtt연결 해제");

                //노티피케이션생성
                Log.d("로그","노티피케이션생성");
                String NOTIFICATION_CHANNEL_ID = "10001";
                NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

                Intent notificationIntent = new Intent(getApplicationContext(),MainActivity.class);
                notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK) ;
                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent,  PendingIntent.FLAG_UPDATE_CURRENT);

                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), NOTIFICATION_CHANNEL_ID)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_new)) //BitMap 이미지 요구
                        .setContentTitle("스마트 어항")
                        .setContentText("급여가 완료되었습니다")
                        // 더 많은 내용이라서 일부만 보여줘야 하는 경우 아래 주석을 제거하면 setContentText에 있는 문자열 대신 아래 문자열을 보여줌
                        //.setStyle(new NotificationCompat.BigTextStyle().bigText("더 많은 내용을 보여줘야 하는 경우..."))
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setContentIntent(pendingIntent) // 사용자가 노티피케이션을 탭시 ResultActivity로 이동하도록 설정
                        .setAutoCancel(true);

                //OREO API 26 이상에서는 채널 필요
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                    builder.setSmallIcon(R.drawable.ic_launcher_new); //mipmap 사용시 Oreo 이상에서 시스템 UI 에러남
                    CharSequence channelName  = "노티페케이션 채널";
                    String description = "오레오 이상을 위한 것임";
                    int importance = NotificationManager.IMPORTANCE_HIGH;

                    NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName , importance);
                    channel.setDescription(description);

                    // 노티피케이션 채널을 시스템에 등록
                    assert notificationManager != null;
                    notificationManager.createNotificationChannel(channel);

                }else builder.setSmallIcon(R.mipmap.ic_launcher); // Oreo 이하에서 mipmap 사용하지 않으면 Couldn't create icon: StatusBarIcon 에러남

                assert notificationManager != null;
                notificationManager.notify(1234, builder.build()); // 고유숫자로 노티피케이션 동작시킴

                Log.d("로그","노티피케이션 발행함!");
            } catch (MqttException e) {
                Log.d("로그","feed명령어 mqtt통신으로 보내는데 실패함");
                e.printStackTrace();
                mqttClient.disconnect();
                Log.d("로그","mqtt연결 해제");
            }
        }else{
            Log.d("로그","mqtt통신실패");
        }
    }

    //예약급여버튼 시간설정 함수 설정 작성
    public void timeSet(){
        //예약시간과 예약분을 현재 timePicker에서 나타낸값으로 변경
        feedHour = mTimePicker.getHour();
        feedMin = mTimePicker.getMinute();

        //sharedPreferences로 값저장
        SharedPreferences feed = getSharedPreferences("feed",Activity.MODE_PRIVATE);
        SharedPreferences.Editor edit = feed.edit();
        edit.putInt("setHour",feedHour);
        edit.putInt("setMin",feedMin);
        edit.commit();

        //예약시간 textView 변경
        if(feedHour<12){
            txtTime.setText("[오전] "+feedHour+"시 "+feedMin+"분");
        }else{
            txtTime.setText("[오후] "+feedHour+"시 "+feedMin+"분");
        }
        //기존알람중지
        stopAlarm();
        //신규알람사용
        startAlarm();

        //안내 Toast메시지
        Toast.makeText(this, feedHour+"시 "+ feedMin+"분으로 설정", Toast.LENGTH_SHORT).show();
    }


    //String값(ON//OFF)을 SharedPreferences에 저장해주는 함수
    //String status는 ON 또는 OFF값만 들어옴
    public void setCheck(String status){
        Log.d("로그","setCheck함수작동");
        txtAutoStatus.setText("현재 예약급여 상태 "+status);
        SharedPreferences feed = getSharedPreferences("feed",Activity.MODE_PRIVATE);
        SharedPreferences.Editor edit = feed.edit();
        edit.putString("setStatus",status);
        edit.commit();

        //ON, OFF값에 따른 hide함수 호출
        hide(status);
    }

    //뷰 숨김 함수, 파라미터 변수는 ON , OFF 두개만 받을예정
    public void hide(String status){
        Log.d("로그","hide함수 작동");
        if(status.equalsIgnoreCase("ON")==true){
            txtAutoTime.setVisibility(View.VISIBLE);
            txtTime.setVisibility(View.VISIBLE);
            txtAutoTimeSet.setVisibility(View.VISIBLE);
            mTimePicker.setVisibility(View.VISIBLE);
            btn_timeSet.setVisibility(View.VISIBLE);
        }else if(status.equalsIgnoreCase("OFF")==true){
            txtAutoTime.setVisibility(View.INVISIBLE);
            txtTime.setVisibility(View.INVISIBLE);
            txtAutoTimeSet.setVisibility(View.INVISIBLE);
            mTimePicker.setVisibility(View.INVISIBLE);
            btn_timeSet.setVisibility(View.INVISIBLE);
        }
    }

    public void startAlarm(){
        //알람매니저시작해서 예약급여 온

        Log.d("로그","startAlarm함수 작동");
        // 시간설정
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, feedHour);
        calendar.set(Calendar.MINUTE, feedMin);

        calendar.set(Calendar.MILLISECOND,0);

        //알람시작
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY, alarmIntent);

        Log.d("로그","알람매니저 시작완료");

    }
    public void stopAlarm(){
        Log.d("로그","stop함수 작동");
        //알람매니저 중지해서 예약급여 오프
        alarmMgr.cancel(alarmIntent);
        Log.d("로그","알람매니저 중지");
    }


}

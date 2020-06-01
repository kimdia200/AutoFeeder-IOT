# AutoFeeder-IOT
<h1>앱소개</h1>
<img src="https://user-images.githubusercontent.com/37800688/83323516-408f0700-a29a-11ea-911a-0e9000cc8ab4.png" width="90%"></img>
<p>
라이브러리 소개</br>
Paho 라이브러리 - 아두이노와 안드로이드간 MQTT를 이용한 통신을 쉽게 사용가능</br>
QuadFlask 라이브러리 - LedActivity에 배치될 ColorPickerView를 쉽게 사용가능</br>
Android X라이브러리 - FeedActivity에서 사용되는 예약급여 기능을 이용(Service 컴포넌트)</br>
</p>


<h1>WIFI통신 원리</h1>
<img src="https://user-images.githubusercontent.com/37800688/83323557-a54a6180-a29a-11ea-97ed-e445626d8f46.png" width="90%"></img>
<p>
IOT기기(아두이노) -> Wifi접속 -> MQTT서버 접속 -> 안드로이드 앱과 통신</br>
안드로이드 앱 -> Wifi 혹은 무선인터넷 -> MQTT서버 접속 -> IOT기기와 통신</br> 
</p>


<h1>SplashActivity</h1>
<img src="https://user-images.githubusercontent.com/37800688/83323810-21917480-a29c-11ea-9f18-47ae13397352.png" width="30%"></img>
<p>
앱 최초 실행(백그라운드 작업 안할때)시 4초간 나타나는 Splash화면</br>
하단에 저작권 표시법에 의거하여 프로젝트 팀명 명시함
</p>

<h1>MainActivity</h1>
<img src="https://user-images.githubusercontent.com/37800688/83323598-f0647480-a29a-11ea-969e-d0d2a17b5452.png" width="90%"></img>
<p>
로고 - 클릭시 새로고침 기능있음</br></br>
온도 - </br>
이미지. 아두이노로 부터 전달받은 온도값에 따라 이미지로 상태 표현(3단계)</br>
클릭시. OndoActivity로 Intend</br></br>
탁도 - </br>
이미지. 아두이노로 부터 전달받은 탁도값에 따라 이미지로 상태 표현(3단계)</br>
클릭시. TakdoActivity로 Intend</br></br>
먹이급여 - 클릭시 FeedActivity로 Intend</br></br>
LED 설정 - 클릭시 LedActivity로 Intend(색은 현재 색과 상관없음)
</p>

<h1>OndoActivity, TakdoActivity</h1>
<img src="https://user-images.githubusercontent.com/37800688/83323644-2f92c580-a29b-11ea-9290-32202c3cc143.png" width="90%"></img>
<p>
아두이노로 전달받은 온도와 탁도값을 수치로 표현</br>
연결되지 않았을시 수치값 위치에 연결되지 않은 상태를 표시
</p>

<h1>FeedActivity</h1>
<img src="https://user-images.githubusercontent.com/37800688/83323711-8dbfa880-a29b-11ea-9c53-ef45f77e8ba1.png" width="90%"></img>
<p>
즉시급여 - 클릭시 아두이노로 Feed명령어가 전달되어 급여가 되며 급여가 완료되었다는 Notification발생</br></br>
예약급여 - </br>
정해신 시간에 예약급여를 가능 하게 해주는 기능</br></br>
체크표시 Check - </br>
박스 체크시 하단부에 예약급여 관련 정보를 InVisible -> Visible상태로 변경</br></br>
현재 예약시간 - </br>
현재 예약급여 설정된 시간을 보여줌(최초 값 = 00시00분)</br></br>
시간설정(TimePicker) - </br>
원하는 시간을 선택하여 적용 버튼을 누르면 예약급여 시간이 설정됨=상단 현재 예약시간 변경
</p>

<h1>LedActivity</h1>
<img src="https://user-images.githubusercontent.com/37800688/83323728-aa5be080-a29b-11ea-8155-5e1f28631152.png" width="90%"></img>
<p>
색상표(ColorPicker) - </br>
QuadFlask 라이브러리를 사용하여 구현</br></br>
LED ON 버튼 - </br>
최초값 = #000000</br>
원하는 색을 고른뒤 Led On버튼을 클릭시 아두이노로 색상값을 전달하여 Led색을 점등</br>
현재 설정한 색이 아닌 현재 Pick한 색으로 BackGround Color가 변경</br></br>
LED OFF 버튼 - </br>
클릭시 아두이노로 LED 소등 명령어를 전달하여 LED소등</br>

</p>

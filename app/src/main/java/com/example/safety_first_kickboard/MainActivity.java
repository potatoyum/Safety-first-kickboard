package com.example.safety_first_kickboard;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.DialogInterface;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.Marker;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;


import java.io.IOException;
import java.util.List;
import java.util.Locale;



public class MainActivity extends AppCompatActivity implements OnMapReadyCallback{
    private Button btn;
    private Button nextBtn;

    private MapView mapView;
    private static NaverMap naverMap;
    private GpsTracker gpsTracker;

    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS  = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};


    //그냥 공공데이터 받아와서 출력 볼려고 만든 함수 이며 실제적으로 필요한 데이터 짤라서 저장하는건 parseData에서 실행
    // 1.지자체별 사고 다발 지역 정보
    private void makeUrl(){
        URL url;
        StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/B552061/frequentzoneLg/getRestFrequentzoneLg");
        try{ // URI만들기(GET형식 요청보내기)
            urlBuilder.append("?"+ URLEncoder.encode("ServiceKey","UTF-8")+"=0MvIUe906%2FGQuhQtiBgXvDnORAxuFZjJZfU7U3%2BcnshFqhr8rKovyxud62403RMhZdIu4bxK1Bpqm8N88QbE0A%3D%3D");
            urlBuilder.append("&"+URLEncoder.encode("searchYearCd","UTF-8")+"=2018");
            urlBuilder.append("&"+URLEncoder.encode("siDo","UTF-8")+"=27");//시도코드(대구)
            urlBuilder.append("&"+URLEncoder.encode("guGun","UTF-8")+"=230");//시군구코드(북구)
            //  urlBuilder.append("&"+URLEncoder.encode("type","UTF-8")+"=json");
            urlBuilder.append("&"+URLEncoder.encode("numOfRows","UTF-8")+"=10");//검색건수
            urlBuilder.append("&"+URLEncoder.encode("pageNo","UTF-8")+"=1");
        }catch (Exception e){
            Log.d("service","ServiceKey error");
            System.out.println("ServiceKey error");
        }
        try{
            url = new URL(urlBuilder.toString());
            Log.d("service","conn전");
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            Log.d("service","conn후");
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/json");
            Log.d("service " ,"response : "+ conn.getResponseCode()+"");

            BufferedReader rd;
            if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                Log.d("service","success");
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                Log.d("service","error");
            }
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                Log.d("service",line+"\n");//공공데이터로 부터 받아온 데이터 출력하기
            }
            rd.close();
            conn.disconnect();
            parseData(url); // url로부터 데이터를 다시 받아와서 파싱하는 함수

        }catch (Exception e){
            Log.d("service",e.toString());
            Log.d("service","url error");
        }

    }

    // 2.자전거 사고 다발 지역 정보
    private void makeUrl2(){
        URL url;
        StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/B552061/frequentzoneBicycle/getRestFrequentzoneBicycle");
        try{ // URI만들기(GET형식 요청보내기)
            urlBuilder.append("?"+ URLEncoder.encode("ServiceKey","UTF-8")+"=0MvIUe906%2FGQuhQtiBgXvDnORAxuFZjJZfU7U3%2BcnshFqhr8rKovyxud62403RMhZdIu4bxK1Bpqm8N88QbE0A%3D%3D");
            urlBuilder.append("&"+URLEncoder.encode("searchYearCd","UTF-8")+"=2018");
            urlBuilder.append("&"+URLEncoder.encode("siDo","UTF-8")+"=27");//시도코드(대구)
            urlBuilder.append("&"+URLEncoder.encode("guGun","UTF-8")+"=230");//시군구코드(북구)
            // urlBuilder.append("&"+URLEncoder.encode("type","UTF-8")+"=json");
            urlBuilder.append("&"+URLEncoder.encode("numOfRows","UTF-8")+"=10");//검색건수
            urlBuilder.append("&"+URLEncoder.encode("pageNo","UTF-8")+"=1");
        }catch (Exception e){
            Log.d("service","ServiceKey error");
            System.out.println("ServiceKey error");
        }
        try{
            url = new URL(urlBuilder.toString());
            Log.d("service","conn전");
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            Log.d("service","conn후");
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/json");
            Log.d("service " ,"response : "+ conn.getResponseCode()+"");

            BufferedReader rd;
            if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                Log.d("service","success");
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                Log.d("service","error");
            }
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                Log.d("service",line+"\n");//공공데이터로 부터 받아온 데이터 출력하기
            }
            rd.close();
            conn.disconnect();
            parseData(url); // url로부터 데이터를 다시 받아와서 파싱하는 함수

        }catch (Exception e){
            Log.d("service",e.toString());
            Log.d("service","url error");
        }

    }

    // 3.공사정보
    private void makeUrl3(String minX,String maxX,String minY,String maxY){
        URL url;
        StringBuilder urlBuilder = new StringBuilder("http://openapi.its.go.kr:8082/api/NEventIdentity");
        try{ // URI만들기(GET형식 요청보내기)
            urlBuilder.append("?"+ URLEncoder.encode("key","UTF-8")+"=1607884783718");
            urlBuilder.append("&"+URLEncoder.encode("ReqType","UTF-8")+"=2");//boundary 요청여부(2)
            //urlBuilder.append("&"+URLEncoder.encode("Eventidentity","UTF-8")+"=230");
            urlBuilder.append("&"+URLEncoder.encode("MinX","UTF-8")+"="+minX);//경도 min
            urlBuilder.append("&"+URLEncoder.encode("MaxX","UTF-8")+"="+maxX);//경도 max
            urlBuilder.append("&"+URLEncoder.encode("MinY","UTF-8")+"="+minY);//위도 min
            urlBuilder.append("&"+URLEncoder.encode("MaxY","UTF-8")+"="+maxY);//위도 min
            urlBuilder.append("&"+URLEncoder.encode("type","UTF-8")+"=its");// 공사정보(국도)
        }catch (Exception e){
            Log.d("service","ServiceKey error");
            System.out.println("ServiceKey error");
        }
        try{
            url = new URL(urlBuilder.toString());
            Log.d("service","conn전");
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            Log.d("service","conn후");
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/json");
            Log.d("service " ,"response : "+ conn.getResponseCode()+"");

            BufferedReader rd;
            if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                Log.d("service","success");
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                Log.d("service","error");
            }
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                Log.d("service",line+"\n");//공공데이터로 부터 받아온 데이터 출력하기
            }
            rd.close();
            conn.disconnect();
            work_parseData(url); // url로부터 데이터를 다시 받아와서 파싱하는 함수

        }catch (Exception e){
            Log.d("service",e.toString());
            Log.d("service","url error");
        }

    }

    // 4.사고정보
    private void makeUrl4(String minX,String maxX,String minY,String maxY){
        URL url;
        StringBuilder urlBuilder = new StringBuilder("http://openapi.its.go.kr:8082/api/NIncidentIdentity");
        try{ // URI만들기(GET형식 요청보내기)
            urlBuilder.append("?"+ URLEncoder.encode("key","UTF-8")+"=1607884783718");
            urlBuilder.append("&"+URLEncoder.encode("ReqType","UTF-8")+"=2");//boundary 요청여부(2)
            //urlBuilder.append("&"+URLEncoder.encode("Eventidentity","UTF-8")+"=230");
            urlBuilder.append("&"+URLEncoder.encode("MinX","UTF-8")+"="+minX);//경도 min
            urlBuilder.append("&"+URLEncoder.encode("MaxX","UTF-8")+"="+maxX);//경도 max
            urlBuilder.append("&"+URLEncoder.encode("MinY","UTF-8")+"="+minY);//위도 min
            urlBuilder.append("&"+URLEncoder.encode("MaxY","UTF-8")+"="+maxY);//위도 min
            urlBuilder.append("&"+URLEncoder.encode("type","UTF-8")+"=its");// 공사정보(국도)
        }catch (Exception e){
            Log.d("service","ServiceKey error");
            System.out.println("ServiceKey error");
        }
        try{
            url = new URL(urlBuilder.toString());
            Log.d("service","conn전");
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            Log.d("service","conn후");
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/json");
            Log.d("service " ,"response : "+ conn.getResponseCode()+"");

            BufferedReader rd;
            if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                Log.d("service","success");
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                Log.d("service","error");
            }
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                Log.d("service",line+"\n");//공공데이터로 부터 받아온 데이터 출력하기
            }
            rd.close();
            conn.disconnect();
            Incident_parseData(url); // url로부터 데이터를 다시 받아와서 파싱하는 함수

        }catch (Exception e){
            Log.d("service",e.toString());
            Log.d("service","url error");
        }

    }

    private void parseData(URL url){ //받아와서 데이터 파싱하기
        JSONArray jsonArray = new JSONArray();
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xpp = factory.newPullParser();
            InputStream is = url.openStream();
            xpp.setInput(new InputStreamReader(is, "UTF-8")); //xml 데이터 받아오기
            int eventType = xpp.getEventType();

            boolean spotBool = false;// 장소이름
            boolean loBool = false;// 경도
            boolean laBool = false;//위도

            while (eventType != XmlPullParser.END_DOCUMENT) { // START_TAG는 태그의 시작부분, TEXT는 태그안에 있는  데이터
                //JSONObject jsonObject = new JSONObject();
                if (eventType == XmlPullParser.START_TAG) {
                    if (xpp.getName().equals("spot_nm")) { // 태그가 spot_nm으로 시작하면 spotBool값 true로 바꾸기
                        spotBool = true;
                    }
                    else if (xpp.getName().equals("lo_crd")) { // 태그가 lo_crd으로 시작하면 spotBool값 true로 바꾸기
                        loBool = true;
                    }
                    else if (xpp.getName().equals("la_crd")) { // 태그가 la_crd으로 시작하면 spotBool값 true로 바꾸기
                        laBool = true;
                    }
                } else if (eventType == XmlPullParser.TEXT) { //
                    if (spotBool) { // spot_nm 태그에 해당하는 값이면 실행한다.
                        spotBool = false;
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("spot_nm", xpp.getText());
                        jsonArray.put(jsonObject);
                    }
                    else if (loBool) { // la_crd 태그에 해당하는 값이면 실행한다.
                        loBool = false;
                        JSONObject jsonObject = (JSONObject)jsonArray.get(jsonArray.length()-1);
                        jsonObject.put("lo_crd",xpp.getText());
                    }
                    else if(laBool){
                        laBool = false;
                        JSONObject jsonObject = (JSONObject)jsonArray.get(jsonArray.length()-1);
                        jsonObject.put("la_crd",xpp.getText());

                    }
                }
                eventType = xpp.next();
            }
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                Log.d("service",""+jsonObject.keys()+" : "+jsonObject.get("spot_nm")+" "+jsonObject.get("lo_crd")+" "+jsonObject.get("la_crd"));
            }
        }
        catch (Exception e){
            Log.d("service","Data parser error.");
            Log.e("service",e.toString());
        }

    }

    private void work_parseData(URL url){ //받아와서 데이터 파싱하기
        JSONArray jsonArray = new JSONArray();
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xpp = factory.newPullParser();
            InputStream is = url.openStream();
            xpp.setInput(new InputStreamReader(is, "UTF-8")); //xml 데이터 받아오기
            int eventType = xpp.getEventType();

            boolean EventBool = false;// 장소이름
            boolean XBool = false;// 경도
            boolean YBool = false;//위도

            while (eventType != XmlPullParser.END_DOCUMENT) { // START_TAG는 태그의 시작부분, TEXT는 태그안에 있는  데이터
                //JSONObject jsonObject = new JSONObject();
                if (eventType == XmlPullParser.START_TAG) {
                    if (xpp.getName().equals("eventstatusmsg")) { // 태그가 spot_nm으로 시작하면 spotBool값 true로 바꾸기
                        EventBool = true;
                    }
                    else if (xpp.getName().equals("coordx")) { // 태그가 lo_crd으로 시작하면 spotBool값 true로 바꾸기
                        XBool = true;
                    }
                    else if (xpp.getName().equals("coordy")) { // 태그가 la_crd으로 시작하면 spotBool값 true로 바꾸기
                        YBool = true;
                    }
                } else if (eventType == XmlPullParser.TEXT) { //
                    if(YBool){ // YBool 태그에 해당하는 값이면 실행한다.
                        YBool = false;
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("CoordY",xpp.getText());
                        jsonArray.put(jsonObject);
                    }
                    else if (XBool) { // XBool 태그에 해당하는 값이면 실행한다.
                        XBool = false;
                        JSONObject jsonObject = (JSONObject)jsonArray.get(jsonArray.length()-1);
                        jsonObject.put("CoordX",xpp.getText());
                    }
                    else if (EventBool) { // EventBool 태그에 해당하는 값이면 실행한다.
                        EventBool = false;
                        JSONObject jsonObject = (JSONObject)jsonArray.get(jsonArray.length()-1);
                        jsonObject.put("EventStatusMsg", xpp.getText());

                    }
                }
                eventType = xpp.next();
            }
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                Log.d("service",""+jsonObject.keys()+" : "+jsonObject.get("EventStatusMsg")+" "+jsonObject.get("CoordX")+" "+jsonObject.get("CoordY"));
            }
        }
        catch (Exception e){
            Log.d("service","Data parser error.");
            Log.e("service",e.toString());
        }
    }

    private void Incident_parseData(URL url){ //받아와서 데이터 파싱하기
        JSONArray jsonArray = new JSONArray();
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xpp = factory.newPullParser();
            InputStream is = url.openStream();
            xpp.setInput(new InputStreamReader(is, "UTF-8")); //xml 데이터 받아오기
            int eventType = xpp.getEventType();

            boolean EventBool = false;// 장소이름
            boolean XBool = false;// 경도
            boolean YBool = false;//위도

            while (eventType != XmlPullParser.END_DOCUMENT) { // START_TAG는 태그의 시작부분, TEXT는 태그안에 있는  데이터
                //JSONObject jsonObject = new JSONObject();
                if (eventType == XmlPullParser.START_TAG) {
                    if (xpp.getName().equals("incidentmsg")) { // 태그가 spot_nm으로 시작하면 spotBool값 true로 바꾸기
                        EventBool = true;
                    }
                    else if (xpp.getName().equals("coordy")) { // 태그가 lo_crd으로 시작하면 spotBool값 true로 바꾸기
                        YBool = true;
                    }
                    else if (xpp.getName().equals("coordx")) { // 태그가 la_crd으로 시작하면 spotBool값 true로 바꾸기
                        XBool = true;
                    }
                } else if (eventType == XmlPullParser.TEXT) { //
                    if(EventBool){ // YBool 태그에 해당하는 값이면 실행한다.
                        EventBool = false;
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("IncidentMsg",xpp.getText());
                        jsonArray.put(jsonObject);
                    }
                    else if (YBool) { // XBool 태그에 해당하는 값이면 실행한다.
                        YBool = false;
                        JSONObject jsonObject = (JSONObject)jsonArray.get(jsonArray.length()-1);
                        jsonObject.put("CoordY",xpp.getText());
                    }
                    else if (XBool) { // EventBool 태그에 해당하는 값이면 실행한다.
                        XBool = false;
                        JSONObject jsonObject = (JSONObject)jsonArray.get(jsonArray.length()-1);
                        jsonObject.put("CoordX", xpp.getText());

                    }
                }
                eventType = xpp.next();
            }
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                Log.d("service",""+jsonObject.keys()+" : "+jsonObject.get("IncidentMsg")+" "+jsonObject.get("CoordX")+" "+jsonObject.get("CoordY"));
            }
        }
        catch (Exception e){
            Log.d("service","Data parser error.");
            Log.e("service",e.toString());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn = (Button)findViewById(R.id.btn);
        nextBtn = (Button)findViewById(R.id.nextBtn);

        if (!checkLocationServicesStatus()) {

            showDialogForLocationServiceSetting();
        }else {

            checkRunTimePermission();
        }

        gpsTracker = new GpsTracker(MainActivity.this);

        double latitude = gpsTracker.getLatitude(); // 위도
        double longitude = gpsTracker.getLongitude(); //경도

        String address = getCurrentAddress(latitude, longitude); //위경도 기반 주소

        Toast.makeText(MainActivity.this, "현재위치 \n위도 " + latitude + "\n경도 " + longitude + "\n주소:" + address, Toast.LENGTH_LONG).show();

        //네이버 지도
        mapView = (MapView) findViewById(R.id.map_fragment);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        //btn 누르면 데이터 파싱
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        makeUrl();
                        Log.d("service","*****자전거*****");
                        makeUrl2();
                        Log.d("service","*****공사*****");
                        makeUrl3(""+127.100000,""+128.890000,""+34.100000,""+39.100000);
                        Log.d("service","*****사고*****");
                        makeUrl4(""+127.100000,""+128.890000,""+34.100000,""+39.100000);
                    }
                }.start();
            }
        });
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),MainActivity2.class);
                startActivity(intent);

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grandResults) {

        if ( permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {

            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면

            boolean check_result = true;


            // 모든 퍼미션을 허용했는지 체크합니다.

            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }


            if ( check_result ) {

                //위치 값을 가져올 수 있음
                ;
            }
            else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료합니다.2 가지 경우가 있습니다.

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {

                    Toast.makeText(MainActivity.this, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요.", Toast.LENGTH_LONG).show();
                    finish();


                }else {

                    Toast.makeText(MainActivity.this, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ", Toast.LENGTH_LONG).show();

                }
            }

        }
    }

    void checkRunTimePermission(){

        //런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION);


        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {

            // 2. 이미 퍼미션을 가지고 있다면
            // ( 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식합니다.)


            // 3.  위치 값을 가져올 수 있음



        } else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.

            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, REQUIRED_PERMISSIONS[0])) {

                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                Toast.makeText(MainActivity.this, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Toast.LENGTH_LONG).show();
                // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(MainActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);


            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(MainActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }

        }

    }


    public String getCurrentAddress( double latitude, double longitude) {

        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses;

        try {

            addresses = geocoder.getFromLocation(
                    latitude,
                    longitude,
                    7);
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";

        }



        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";

        }

        Address address = addresses.get(0);
        return address.getAddressLine(0).toString()+"\n";

    }


    //여기부터는 GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하실래요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case GPS_ENABLE_REQUEST_CODE:

                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {

                        Log.d("@@@", "onActivityResult : GPS 활성화 되있음");
                        checkRunTimePermission();
                        return;
                    }
                }

                break;
        }
    }

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        this.naverMap = naverMap;

       /* double latitude = gpsTracker.getLatitude(); // 위도
        double longitude = gpsTracker.getLongitude(); //경도*/

        //이부분을 내가 못해서 느리게 지도가 뜨는것 같음
        CameraPosition cameraPosition = new CameraPosition(
                new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude()),  // 위치 지정
                15 // 줌 레벨
        );
        naverMap.setCameraPosition(cameraPosition);
    }

}
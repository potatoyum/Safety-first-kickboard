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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {
    private Button btn;
    private Button nextBtn;

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
            parseData(sb,url); // url로부터 데이터를 다시 받아와서 파싱하는 함수

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
            //parseData(sb,url); // url로부터 데이터를 다시 받아와서 파싱하는 함수

        }catch (Exception e){
            Log.d("service",e.toString());
            Log.d("service","url error");
        }

    }

    // 3.공사정보
    private void makeUrl3(){
        URL url;
        StringBuilder urlBuilder = new StringBuilder("http://openapi.its.go.kr:8082/api/NEventIdentity");
        try{ // URI만들기(GET형식 요청보내기)
            urlBuilder.append("?"+ URLEncoder.encode("key","UTF-8")+"=1607884783718");
            urlBuilder.append("&"+URLEncoder.encode("ReqType","UTF-8")+"=2");//boundary 요청여부(2)
            //urlBuilder.append("&"+URLEncoder.encode("Eventidentity","UTF-8")+"=230");
            urlBuilder.append("&"+URLEncoder.encode("MinX","UTF-8")+"=127.100000");//경도 min
            urlBuilder.append("&"+URLEncoder.encode("MaxX","UTF-8")+"=34.100000");//경도 max
            urlBuilder.append("&"+URLEncoder.encode("MinY","UTF-8")+"=128.890000");//위도 min
            urlBuilder.append("&"+URLEncoder.encode("MaxY","UTF-8")+"=39.100000");//위도 min
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
            //parseData(sb,url); // url로부터 데이터를 다시 받아와서 파싱하는 함수

        }catch (Exception e){
            Log.d("service",e.toString());
            Log.d("service","url error");
        }

    }

    // 4.사고정보
    private void makeUrl4(){
        URL url;
        StringBuilder urlBuilder = new StringBuilder("http://openapi.its.go.kr:8082/api/NEventIdentity");
        try{ // URI만들기(GET형식 요청보내기)
            urlBuilder.append("?"+ URLEncoder.encode("key","UTF-8")+"=1607884783718");
            urlBuilder.append("&"+URLEncoder.encode("ReqType","UTF-8")+"=2");//boundary 요청여부(2)
            //urlBuilder.append("&"+URLEncoder.encode("Eventidentity","UTF-8")+"=230");
            urlBuilder.append("&"+URLEncoder.encode("MinX","UTF-8")+"=127.100000");//경도 min
            urlBuilder.append("&"+URLEncoder.encode("MaxX","UTF-8")+"=34.100000");//경도 max
            urlBuilder.append("&"+URLEncoder.encode("MinY","UTF-8")+"=128.890000");//위도 min
            urlBuilder.append("&"+URLEncoder.encode("MaxY","UTF-8")+"=39.100000");//위도 min
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
            //parseData(sb,url); // url로부터 데이터를 다시 받아와서 파싱하는 함수

        }catch (Exception e){
            Log.d("service",e.toString());
            Log.d("service","url error");
        }

    }

    private void parseData(StringBuilder data,URL url){ //받아와서 데이터 파싱하기
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
                JSONObject jsonObject = new JSONObject();

                if (eventType == XmlPullParser.START_TAG) {
                    if (xpp.getName().equals("spot_nm")) { // 태그가 spot_nm으로 시작하면 spotBool값 true로 바꾸기
                        spotBool = true;
                    }
                    if (xpp.getName().equals("lo_crd")) { // 태그가 lo_crd으로 시작하면 spotBool값 true로 바꾸기
                        loBool = true;
                    }
                } else if (eventType == XmlPullParser.TEXT) { //
                    if (spotBool) { // spot_nm 태그에 해당하는 값이면 실행한다.
                        spotBool = false;
                        //JSONObject jsonObject = new JSONObject();
                        jsonObject.put("spot_nm", xpp.getText());
                        //jsonArray.put(jsonObject);
                    }
                    if (loBool) { // la_crd 태그에 해당하는 값이면 실행한다.
                        loBool = false;
                        jsonObject.put("lo_crd", xpp.getText());
                    }
                    jsonArray.put(jsonObject);
                }
                eventType = xpp.next();
            }
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                Log.d("service", "" + jsonObject.keys() + " : " + jsonObject.get("spot_nm")); //사고다발구간 명칭
                String longitude = jsonObject.getString("lo_crd");
                Log.d("service", "위도:" + longitude);
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
                        makeUrl3();
                        Log.d("service","*****사고*****");
                        makeUrl4();
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
}
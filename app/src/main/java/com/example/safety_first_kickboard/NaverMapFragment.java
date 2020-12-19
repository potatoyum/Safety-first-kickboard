package com.example.safety_first_kickboard;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.InfoWindow;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.Overlay;
import com.naver.maps.map.util.FusedLocationSource;
import com.naver.maps.map.util.MarkerIcons;

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

import static com.naver.maps.map.LocationTrackingMode.Face;


public class NaverMapFragment extends Fragment implements OnMapReadyCallback {
    private MapView mapView;
    NaverMap naverMap;
    FusedLocationSource locationSource;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    Double latitude, longitude, minlat, maxlat, minlon, maxlon;
    Integer m;

    //그냥 공공데이터 받아와서 출력 볼려고 만든 함수 이며 실제적으로 필요한 데이터 짤라서 저장하는건 parseData에서 실행
    // 1.지자체별 사고 다발 지역 정보
    public void makeUrl(){
        URL url;
        StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/B552061/frequentzoneLg/getRestFrequentzoneLg");
        try{ // URI만들기(GET형식 요청보내기)
            urlBuilder.append("?"+ URLEncoder.encode("ServiceKey","UTF-8")+"=0MvIUe906%2FGQuhQtiBgXvDnORAxuFZjJZfU7U3%2BcnshFqhr8rKovyxud62403RMhZdIu4bxK1Bpqm8N88QbE0A%3D%3D");
            urlBuilder.append("&"+URLEncoder.encode("searchYearCd","UTF-8")+"=2018");
            urlBuilder.append("&"+URLEncoder.encode("siDo","UTF-8")+"=27");//시도코드(대구)
            urlBuilder.append("&"+URLEncoder.encode("guGun","UTF-8")+"=110");//시군구코드(중구)
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
            loc_parseData(url); // url로부터 데이터를 다시 받아와서 파싱하는 함수

        }catch (Exception e){
            Log.d("service",e.toString());
            Log.d("service","url error");
        }

    }

    // 2.자전거 사고 다발 지역 정보
    public void makeUrl2(){
        URL url;
        StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/B552061/frequentzoneBicycle/getRestFrequentzoneBicycle");
        try{ // URI만들기(GET형식 요청보내기)
            urlBuilder.append("?"+ URLEncoder.encode("ServiceKey","UTF-8")+"=0MvIUe906%2FGQuhQtiBgXvDnORAxuFZjJZfU7U3%2BcnshFqhr8rKovyxud62403RMhZdIu4bxK1Bpqm8N88QbE0A%3D%3D");
            urlBuilder.append("&"+URLEncoder.encode("searchYearCd","UTF-8")+"=2018");
            urlBuilder.append("&"+URLEncoder.encode("siDo","UTF-8")+"=27");//시도코드(대구)
            urlBuilder.append("&"+URLEncoder.encode("guGun","UTF-8")+"=110");//시군구코드(중구)
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
            bike_parseData(url); // url로부터 데이터를 다시 받아와서 파싱하는 함수

        }catch (Exception e){
            Log.d("service",e.toString());
            Log.d("service","url error");
        }

    }

    // 3.공사정보
    public void makeUrl3(double minX,double maxX,double minY,double maxY){
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
    public void makeUrl4(double minX,double maxX,double minY,double maxY){
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

    public void loc_parseData(URL url){ //받아와서 데이터 파싱하기
        int type = 1;
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


                String lat = (String)jsonObject.get("la_crd");
                String lng = (String)jsonObject.get("lo_crd");
                String name = (String)jsonObject.get("spot_nm");

                if(minlat<=Double.parseDouble(lat) && maxlat>=Double.parseDouble(lat) && minlon<=Double.parseDouble(lng) && maxlon>=Double.parseDouble(lng)){
                    Marker(type,Double.parseDouble(lat),Double.parseDouble(lng),name); //마커 생성
                   /* FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.detach(this).attach(this).commit();*/
                }
            }
        }
        catch (Exception e){
            Log.d("service","Data parser error.");
            Log.e("service",e.toString());
        }

    }

    public void bike_parseData(URL url){ //받아와서 데이터 파싱하기
        int type = 2;
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


                String lat = (String)jsonObject.get("la_crd");
                String lng = (String)jsonObject.get("lo_crd");
                String name = (String)jsonObject.get("spot_nm");

                if(minlat<=Double.parseDouble(lat) && maxlat>=Double.parseDouble(lat) && minlon<=Double.parseDouble(lng) && maxlon>=Double.parseDouble(lng)){
                    Marker(type,Double.parseDouble(lat),Double.parseDouble(lng),name); //마커 생성
                }
            }
        }
        catch (Exception e){
            Log.d("service","Data parser error.");
            Log.e("service",e.toString());
        }

    }

    public void work_parseData(URL url){ //받아와서 데이터 파싱하기
        int type = 3;
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
            boolean BlockBool = false; // 차로차단 방법

            while (eventType != XmlPullParser.END_DOCUMENT) { // START_TAG는 태그의 시작부분, TEXT는 태그안에 있는  데이터
                //JSONObject jsonObject = new JSONObject();
                if (eventType == XmlPullParser.START_TAG) {
                    if (xpp.getName().equals("eventstatusmsg")) { // 태그가 spot_nm으로 시작하면 spotBool값 true로 바꾸기
                        EventBool = true;
                    }
                    else if (xpp.getName().equals("lanesblocktype")) { // 태그가 lo_crd으로 시작하면 spotBool값 true로 바꾸기
                        BlockBool = true;
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
                    else if (BlockBool) {
                        BlockBool = false;
                        JSONObject jsonObject = (JSONObject)jsonArray.get(jsonArray.length()-1);
                        jsonObject.put("lanesblocktype",xpp.getText());
                    }
                }
                eventType = xpp.next();
            }
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                Log.d("service",""+jsonObject.keys()+" : "+jsonObject.get("EventStatusMsg")+" "+jsonObject.get("CoordX")+" "+jsonObject.get("CoordY"));

                String lng = (String)jsonObject.get("CoordX");
                String lat = (String)jsonObject.get("CoordY");
                String name = (String)jsonObject.get("EventStatusMsg");
                int block = Integer.valueOf((String)jsonObject.get("lanesblocktype"));

                Log.e("service",""+block);

                //차로통제가 있는 경우에만
                if(block >= 1) {
                    Marker(type, Double.parseDouble(lat), Double.parseDouble(lng), name); //마커 생성
                }

            }
        }
        catch (Exception e){
            Log.d("service","Data parser error.");
            Log.e("service",e.toString());
        }
    }

    public void Incident_parseData(URL url){ //받아와서 데이터 파싱하기
        int type = 4;
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
            boolean BlockBool = false; // 차로차단 방법

            while (eventType != XmlPullParser.END_DOCUMENT) { // START_TAG는 태그의 시작부분, TEXT는 태그안에 있는  데이터
                //JSONObject jsonObject = new JSONObject();
                if (eventType == XmlPullParser.START_TAG) {
                    if (xpp.getName().equals("incidentmsg")) {
                        EventBool = true;
                    }
                    else if (xpp.getName().equals("lanesblocktype")) {
                        BlockBool = true;
                    }
                    else if (xpp.getName().equals("coordy")) {
                        YBool = true;
                    }
                    else if (xpp.getName().equals("coordx")) {
                        XBool = true;
                    }

                } else if (eventType == XmlPullParser.TEXT) { //
                    if(EventBool){ // 태그에 해당하는 값이면 실행한다.
                        EventBool = false;
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("IncidentMsg",xpp.getText());
                        jsonArray.put(jsonObject);
                    }
                    else if (BlockBool) {
                        BlockBool = false;
                        JSONObject jsonObject = (JSONObject)jsonArray.get(jsonArray.length()-1);
                        jsonObject.put("lanesblocktype",xpp.getText());
                    }
                    else if (YBool) {
                        YBool = false;
                        JSONObject jsonObject = (JSONObject)jsonArray.get(jsonArray.length()-1);
                        jsonObject.put("CoordY",xpp.getText());
                    }
                    else if (XBool) {
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

                String lng = (String)jsonObject.get("CoordX");
                String lat = (String)jsonObject.get("CoordY");
                String name = (String)jsonObject.get("IncidentMsg");
                int block = Integer.valueOf((String)jsonObject.get("lanesblocktype"));

                //차로통제가 있는 경우에만
                if(block >= 1) {
                    Marker(type, Double.parseDouble(lat), Double.parseDouble(lng), name); //마커 생성
                }
            }
        }
        catch (Exception e){
            Log.d("service","Data parser error.");
            Log.e("service",e.toString());
        }
    }

    //마커생성 함수
    public void Marker(int type,double la,double lo,String name) {
        Marker marker = new Marker();
        marker.setPosition(new LatLng(la, lo));
        InfoWindow infoWindow = new InfoWindow();

        Log.d("service",la+" "+lo);
        Log.d("service",String.valueOf(naverMap));

        int a = ResourcesCompat.getColor(getResources(),R.color.marker1,null);
        int b = ResourcesCompat.getColor(getResources(),R.color.marker2,null);
        int c = ResourcesCompat.getColor(getResources(),R.color.marker3,null);
        int d = ResourcesCompat.getColor(getResources(),R.color.marker4,null);

        //정보창
        infoWindow.setAdapter(new InfoWindow.DefaultTextAdapter(getActivity()) {
            @NonNull
            @Override
            public CharSequence getText(@NonNull InfoWindow infoWindow) {
                return name;
            }
        });

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                marker.setMap(naverMap);
                Log.d("service",String.valueOf(naverMap));
                marker.setIcon(MarkerIcons.BLACK);

                // 마커 색 지정
                if(type == 1){
                    marker.setIconTintColor(a);
                }
                else if(type == 2){
                    marker.setIconTintColor(b);
                }
                else if(type == 3)
                {
                    marker.setIconTintColor(c);
                }
                else if(type == 4)
                {
                    marker.setIconTintColor(d);
                }

            }
        });

        Overlay.OnClickListener listener = overlay -> {
            Marker markers = (Marker)overlay;

            if (markers.getInfoWindow() == null) {
                // 현재 마커에 정보 창이 열려있지 않을 경우 엶
                infoWindow.open(markers);
            } else {
                // 이미 현재 마커에 정보 창이 열려있을 경우 닫음
                infoWindow.close();
            }

            return true;
        };

        marker.setOnClickListener(listener);

    }

    public NaverMapFragment() {
        // Required empty public constructor
    }


    public static NaverMapFragment newInstance(String param1, String param2) {
        NaverMapFragment fragment = new NaverMapFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_naver_map,
                container, false);

        Bundle bundle = getArguments();
        if (bundle != null) {
            latitude = bundle.getDouble("latitude");
            longitude = bundle.getDouble("longitude");
            minlat = bundle.getDouble("minlat");
            maxlat = bundle.getDouble("maxlat");
            minlon = bundle.getDouble("minlon");
            maxlon = bundle.getDouble("maxlon");
        }
        // System.out.println("받는 : "+latitude+longitude+minlat+maxlat +minlon +maxlon);

        mapView = (MapView) rootView.findViewById(R.id.naverMap);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        locationSource= new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);

        return rootView;
    }
    @Override
    public void onMapReady(@NonNull NaverMap naverMap){ //여기서 지도 관련 하면됨~~~~~~~~~~~!
        naverMap = naverMap;

        setNaverMap(naverMap);

        naverMap.setLocationSource(locationSource);
        naverMap.getUiSettings().setLocationButtonEnabled(true); //현위치 버튼 활성화
        naverMap.setLocationTrackingMode(Face); //face모드로 위치 트래킹
        naverMap.addOnLocationChangeListener(location -> Toast.makeText(getActivity(), location.getLatitude() +", " +location.getLongitude(), Toast.LENGTH_SHORT).show()); //위치이동되면 토스트 뜸


        //액티비티에서 읽은 값 여기서 마커찍기
        /*new Thread(){
            @Override
            public void run() {
                super.run();
                Log.d("service","*****지자체*****");
                makeUrl();
                Log.d("service","*****자전거*****");
                makeUrl2();
                Log.d("service","**********공사**********");
                makeUrl3(+127.100000,+128.890000,+34.100000,+39.100000); // 샘플
                //makeUrl3(minlon,maxlon,minlat,maxlat); // 현 위치 기반
                Log.d("service","**********사고**********");
                makeUrl4(+127.100000,+128.890000,+34.100000,+39.100000); // 샘플
                //makeUrl4(minlon,maxlon,minlat,maxlat); // 현 위치 기반
            }
        }.start();*/

    }

    public void setNaverMap(NaverMap naverMap) {
        this.naverMap = naverMap;
    }

    public NaverMap getNaverMap() {
        return naverMap;
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

}
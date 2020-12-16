package com.example.safety_first_kickboard;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.LocationSource;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.util.FusedLocationSource;

import static com.naver.maps.map.LocationTrackingMode.Face;


public class NaverMapFragment extends Fragment implements OnMapReadyCallback {
    private MapView mapView;
    NaverMap naverMap;
    FusedLocationSource locationSource;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;

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

        mapView = (MapView) rootView.findViewById(R.id.naverMap);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        locationSource= new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);

        return rootView;
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap){ //여기서 지도 관련 하면됨~~~~~~~~~~~!
        setNaverMap(naverMap);

        naverMap.setLocationSource(locationSource);
        naverMap.getUiSettings().setLocationButtonEnabled(true); //현위치 버튼 활성화
        naverMap.setLocationTrackingMode(Face); //face모드로 위치 트래킹
        naverMap.addOnLocationChangeListener(location -> Toast.makeText(getActivity(), location.getLatitude() +", " +location.getLongitude(), Toast.LENGTH_SHORT).show()); //위치이동되면 토스트 뜸

        Marker marker = new Marker();
        marker.setPosition(new LatLng(37.5670135, 126.9783740)); //서울역마크
        marker.setMap(naverMap);


        //액티비티에서 읽은 값 여기서 마커찍기






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
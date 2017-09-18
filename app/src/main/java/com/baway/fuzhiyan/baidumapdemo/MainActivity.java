package com.baway.fuzhiyan.baidumapdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.cloud.CloudListener;
import com.baidu.mapapi.cloud.CloudManager;
import com.baidu.mapapi.cloud.CloudPoiInfo;
import com.baidu.mapapi.cloud.CloudRgcResult;
import com.baidu.mapapi.cloud.CloudSearchResult;
import com.baidu.mapapi.cloud.DetailSearchResult;
import com.baidu.mapapi.cloud.NearbySearchInfo;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;

public class MainActivity extends AppCompatActivity implements CloudListener {

    MapView mMapView;
    BaiduMap mBaiduMap;
    private LocationClient mLocClient;
    private MyLocationListenner myListener = new MyLocationListenner();
    boolean isFirstLoc = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //舒适化地图云检索的sdk
        CloudManager.getInstance().init(this);
        initBaiduMap();
      /*  //定义Maker坐标点
        LatLng point = new LatLng(40.047862, 116.306586);
        //构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.drawable.icon_openmap_mark);
        //构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option1 = new MarkerOptions()
                .position(point)
                .icon(bitmap);
        //在地图上添加Marker，并显示
        mBaiduMap.addOverlay(option1);
        //调用BaiduMap对象的setOnMarkerDragListener方法设置marker拖拽的监听
        mBaiduMap.setOnMarkerDragListener(new BaiduMap.OnMarkerDragListener() {
            public void onMarkerDrag(Marker marker) {
                //拖拽中
            }

            public void onMarkerDragEnd(Marker marker) {
                //拖拽结束
            }

            public void onMarkerDragStart(Marker marker) {
                //开始拖拽
            }
        });*/
    }

    /*执行lbs云检索的功能，拼接字符窜发送get请求，只不过百度地图封装了逻辑
    **调用API方法
    *
    *
    */
    public void checkLBS(View view){

        NearbySearchInfo info = new NearbySearchInfo();
        info.ak = "wFmyh5Uggy6LR7RcW6MyhzvQeKaj5iBT\n";
         //此处info.ak为服务端ak，非Adnroid sdk端ak， 且此服务端ak和Adnroid sdk端ak 是在同一个账户。
        info.geoTableId = 160758;
        // info.geoTableId 是存储在于info.ak相同开发账户中。
        //搜索的最大半径
        info.radius = 30000;
        //确定搜索中心的坐标
        info.location = "116.403152,39.925932";
        //把设置好的数据装入容器中
        CloudManager.getInstance().nearbySearch(info);
    }
    /**
     * 1. 检索的回调结果
     * 2. 检索的回调详情
     */

    @Override
    public void onGetSearchResult(CloudSearchResult result, int i) {
        //处理从服务器得到的结果前,先判断结果对象是否存在,结果里面的表对象是否存在,结果里面的表是否有内容
        if (result != null && result.poiList != null && result.poiList.size() > 0) {
            //画画前,先把黑板擦干净,所以在地图上面定位表示前,先把地图清空
            mBaiduMap.clear();
            //创建定位标识的图片对象,使用Bitmap工厂从资料里面获取图片资源,建立对象.
            BitmapDescriptor bd = BitmapDescriptorFactory.fromResource(R.drawable.icon_openmap_mark);
            //建立定位点对象
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            //建立点对象
            LatLng ll;
            //使用高级for循环,把从服务器得到的json结果位置拿出来,在添加图片,成为标识显示在地图上
            for (CloudPoiInfo info : result.poiList) {
                //从表中拿出维度和经度,设置给点对象
                ll = new LatLng(info.latitude, info.longitude);
                //添加坐标图片的对象给MarkerOptions,传图片BitmapDescriptor参数,和点LatLng参数
                OverlayOptions oo = new MarkerOptions().icon(bd).position(ll);
                mBaiduMap.addOverlay(oo);
                builder.include(ll);
            }
        }

    }

    @Override
    public void onGetDetailSearchResult(DetailSearchResult detailSearchResult, int i) {

    }

    @Override
    public void onGetCloudRgcResult(CloudRgcResult cloudRgcResult, int i) {

    }

    public class MyLocationListenner implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                return;
            }
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(18.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }
        }
    }

    private void initBaiduMap() {
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        //普通地图
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        //卫星地图
//        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
        //空白地图, 基础地图瓦片将不会被渲染。在地图类型中设置为NONE，将不会使用流量下载基础地图瓦片图层。使用场景：与瓦片图层一起使用，节省流量，提升自定义瓦片图下载速度。
        //mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NONE);

        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        // 创建定位所用到的类
        mLocClient = new LocationClient(this);
        //注册定位的监听器
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型,使用的地图加密算法
        option.setScanSpan(1000);//可选设置定位间隔时间,注意间隔需要大于等于1000ms
        mLocClient.setLocOption(option);//地图界面显示到定位位置

        mLocClient.start();//开启定位


    }
    @Override
    protected void onDestroy() {
        // 退出时销毁定位
        mLocClient.stop();
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
        CloudManager.getInstance().destroy();
        super.onDestroy();

    }
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }




}

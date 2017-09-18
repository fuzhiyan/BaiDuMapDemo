package com.example.baidu;

/**
 * Created by 小亚 on 2017/9/16.
 */

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

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
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;

/**
 * 实现手机定位的三种方式:1.GPS定位 2.WIFI定位 3.基站定位
 * 注意定位:要在清单文件里加上service服务
 * 1.布局
 * 2.初始化定位的设置
 * 3.设置监听
 * 4.优化,
 */
public class LocationActivity extends AppCompatActivity implements CloudListener
{

    private MapView mapView;
    private BaiduMap mBaiduMap;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.activity_location);
        initBaiduMap();
    }

    private void initBaiduMap() {
        mapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mapView.getMap();
        mBaiduMap.setMyLocationEnabled(true);
        CloudManager.getInstance().init(this);



    }
    @Override
    public void onGetSearchResult(CloudSearchResult result, int i) {
        //处理从服务器得到的结果前,先判断结果对象是否存在,结果里面的表对象是否存在,结果里面的表是否有内容
        if (result != null && result.poiList != null && result.poiList.size() > 0) {
            //画画前,先把黑板擦干净,所以在地图上面定位表示前,先把地图清空
            mBaiduMap.clear();
            //创建定位标识的图片对象,使用Bitmap工厂从资料里面获取图片资源,建立对象.
            BitmapDescriptor bd = BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding);
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
    private void check(View view){
        //创建百度地图特有的容器
        NearbySearchInfo info=new NearbySearchInfo();
        //服务器AK之,必须字符串
        info.ak="";
        //百度地图自动数据库的Key值int
        info.geoTableId=111111;
        //设置搜索的最大半径
        info.radius=3000;
        //
        info.location="";
    }
}

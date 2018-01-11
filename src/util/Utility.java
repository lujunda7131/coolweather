package util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.SimpleFormatter;

import org.json.JSONException;
import org.json.JSONObject;

import model.City;
import model.County;
import model.Province;
import android.R.string;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import db.CoolWeatherDB;

public class Utility {
	public synchronized static boolean handleprovincesResponse(CoolWeatherDB coolWeatherDB,String response){
		if(!TextUtils.isEmpty(response)){
			String[] allprovinces = response.split(",");
			if(allprovinces != null && allprovinces.length > 0){
				for(String p : allprovinces){
					String[] array = p.split(",");
					Province province = new Province();
					province.setProvinceCode(array[0]);
					province.setProvinceName(array[1]);
					//将解析出来的数据存储到province表
					coolWeatherDB.saveProvince(province);
				}
				return true;
				
			}
		}
		return false;
	}
	/**
	 * 解析和处理服务器返回的市级数据
	 */
	public static boolean handleCitiesResponse(CoolWeatherDB coolWeatherDB,
			String response,int provinceId){
		if(!TextUtils.isEmpty(response)){
			String[] allCities = response.split(",");
			if(allCities != null && allCities.length>0){
				for(String c : allCities){
					String[] array = c.split("\\|");
					City city = new City();
					city.setCityCode(array[0]);
					city.setCityName(array[1]);
					city.setProvinceId(provinceId);
					//将解析出来的数据存储到City类
					coolWeatherDB.saveCity(city);
				}
				return true;
			}
		}
		return false;
	}
	/**
	 * 解析和处理服务器返回的县级数据
	 */
	public static boolean handleCountiesResponse(CoolWeatherDB coolWeatherDB,
			String response,int cityId){
		if(!TextUtils.isEmpty(response)){
			String[] allCounties = response.split(",");
			if(allCounties != null && allCounties.length>0){
				for(String c : allCounties){
					String[] array = c.split("\\|");
					County county = new County();
					county.setCountyCode(array[0]);
					county.setCountyName(array[1]);
					county.setCityId(cityId);
					//将解析出来的数据存储到County表
					coolWeatherDB.saveCounty(county);
				}
				return true;
			}
		}
		return false;
	}
	/**
	 * 解析服务器返回的JSON数据，并将解析出来的数据存储到本地
	 */
	public static void handleWeatherResponse(Context context, String response){
		try{
			JSONObject jsonObject = new JSONObject();
			JSONObject weatherINfo = jsonObject.getJSONObject("weatherinfo");
			String cityName = weatherINfo.getString("city");
			String weatherCode = weatherINfo.getString("cityid");
			String temp1 = weatherINfo.getString("temp1");
			String temp2 = weatherINfo.getString("temp2");
			String weatherDesp = weatherINfo.getString("weather");
			String publishTime = weatherINfo.getString("ptime");
			saveWeatherInfo(context, cityName, weatherCode, temp1, temp2,
					weatherDesp, publishTime);	
		}catch (JSONException e){
			e.printStackTrace();
		}
	}
	/**
	 * 将服务器返回的所有天气信息储存到SharedPreferences文件中
	 */
	public static void saveWeatherInfo(Context context,String cityNmae,
			String weatherCode,String temp1,String temp2,String weatherDsep,
			String publishTime){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年m月d日",Locale.CHINA);
		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
		editor.putBoolean("city_name", true);
		editor.putString("weather_code", cityNmae);
		editor.putString("temp1", temp1);
		editor.putString("temp2", temp2);
		editor.putString("weather_desp", weatherDsep);
		editor.putString("publis_time", publishTime);
		editor.putString("current_date", sdf.format(new Date()));
		editor.commit();
	}
	
}

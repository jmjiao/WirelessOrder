package com.wirelessorder.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.content.res.TypedArray;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager.LayoutParams;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ViewSwitcher.ViewFactory;

import com.wirelessorder.R;
import com.wirelessorder.domain.Menus;
import com.wirelessorder.util.Base64ToImage;
import com.wirelessorder.webservice.MyService;

public class MainActivity extends Activity {

	private String nameSpace = "http://tempuri.org/";
	private String methodName = "getAMenus";
	private String url = "http://172.20.9.136/MyService.asmx";
	private String methodNames[] = new String[] {};
	private Object methodValues[] = new Object[] {};
	MyService service = new MyService(nameSpace, methodName, url, methodNames,
			methodValues);
	List<Menus> list = new ArrayList<Menus>();

	int[] imageId = new int[]{
			R.drawable.alert_error,R.drawable.alert_ok,R.drawable.progress,R.drawable.ic_launcher
	};
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		final Gallery gallery = (Gallery) findViewById(R.id.gallery);
		final ImageSwitcher switcher = (ImageSwitcher) findViewById(R.id.switcher);
		//为ImageSwitcher对象设置ViewFactory对象
		switcher.setFactory(new ViewFactory() {
			
			public View makeView() {
				ImageView imageView = new ImageView(MainActivity.this);
				imageView.setBackgroundColor(0xff0000);
				imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
				imageView.setLayoutParams(new ImageSwitcher.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
				return imageView;
			}
		});
		//设置图片更换的动画效果
		switcher.setInAnimation(AnimationUtils.loadAnimation(this,android.R.anim.fade_in));
		switcher.setOutAnimation(AnimationUtils.loadAnimation(this,android.R.anim.fade_in));
		SoapSerializationEnvelope envelope = null;
		try {
			envelope = service.getWebServiceReturner();
			Log.e("xxx", "11111111111111111111111111111111111111111111111111");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		}
		try {
			if (envelope.getResponse() != null) {
				SoapObject result = (SoapObject) envelope.bodyIn;
				SoapObject items = (SoapObject) result.getProperty(methodName
						+ "Result");
				for (int i = 0; i < items.getPropertyCount(); i++) {
					SoapObject item = (SoapObject) items.getProperty(i);
					Menus menu = new Menus();
					menu.setFoodCategory(item.getProperty("strFoodCategory")
							.toString());
					menu.setFoodName(item.getProperty("strFoodName").toString());
					menu.setUnitPrice(Integer.parseInt(item.getProperty(
							"strUnitPrice").toString()));
					menu.setUnit(item.getProperty("strUnit").toString());
					menu.setIntroduce(item.getProperty("strIntroduce")
							.toString());
					menu.setPicture(Base64ToImage.GenerateImage(item
							.getProperty("strPicBytes").toString()));
					list.add(menu);
				}
			}
		} catch (Exception e) {
			Log.d("exception", e.getMessage());
		}
		
		//创建一个BaseAdapter对象，该对象负责提供Gallery所显示的每张图片
		BaseAdapter adapter = new BaseAdapter() {
			
			//该方法返回的View就代表了每个列表项
			public View getView(int position, View convertView, ViewGroup parent) {
				ImageView imageView = new ImageView(MainActivity.this);
				imageView.setImageBitmap(list.get(position%list.size()).getPicture());
//				imageView.setImageResource(imageId[position%imageId.length]);
				//设置ImageView的缩放类型
				imageView.setScaleType(ImageView.ScaleType.FIT_XY);
				imageView.setLayoutParams(new Gallery.LayoutParams(75,100));
				TypedArray typedArray = obtainStyledAttributes(R.styleable.Gallery);
				imageView.setBackgroundResource(typedArray.getResourceId(R.styleable.Gallery_android_galleryItemBackground, 0));
				return imageView;
			}
			
			public long getItemId(int position) {
				return position;
			}
			
			public Object getItem(int position) {
				return position;
			}
			
			public int getCount() {
				return list.size();
//				return imageId.length;
			}
		};
		gallery.setAdapter(adapter);
		gallery.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				BitmapDrawable bitmapDrawable = new BitmapDrawable(list.get(position%list.size()).getPicture());
				switcher.setImageDrawable(bitmapDrawable);
//				switcher.setImageResource(imageId[position%imageId.length]);
			}

			public void onNothingSelected(AdapterView<?> parent) {
				
			}
		});

	}

	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}

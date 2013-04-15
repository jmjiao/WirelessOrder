package com.wirelessorder.activity;

import java.io.IOException;

import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.wirelessorder.R;
import com.wirelessorder.domain.OrderStringUtil;
import com.wirelessorder.webservice.MyService;

public class LoginActivity extends Activity implements OnClickListener {

	MyService service;
	private String nameSpace = "http://tempuri.org/";
	private String methodName = "login";
	private String url = "http://172.20.9.136/MyService.asmx";
	private String methodNames[];
	private Object methodValues[];
	private Button loginBtn;
	private Button exitBtn;
	private ProgressDialog prgDialog;
	private Boolean flag;
	private String connState;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		loginBtn = (Button) findViewById(R.id.login);
		exitBtn = (Button) findViewById(R.id.exit);
		loginBtn.setOnClickListener(this);
		exitBtn.setOnClickListener(this);
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	public void onClick(View v) {
		String username = ((EditText) findViewById(R.id.username)).getText()
				.toString();
		String password = ((EditText) findViewById(R.id.password)).getText()
				.toString();
		methodNames = new String[] { "username", "password" };
		methodValues = new Object[] { username, password };
		service = new MyService(nameSpace, methodName, url, methodNames,
				methodValues);
		switch (v.getId()) {
		case R.id.login:

			// 显示登陆对话框
			prgDialog = new ProgressDialog(LoginActivity.this);
			prgDialog.setIcon(R.drawable.progress);
			prgDialog.setTitle("请稍等");
			prgDialog.setMessage("正在登陆，请稍等...");
			prgDialog.setCancelable(false);
			prgDialog.setIndeterminate(true);
			prgDialog.show();
			login();
			break;
		case R.id.exit:
			finish();
			break;
		}

	}

	private void login() {
		new Thread() {
			public void run() {
				Message msg = new Message();
				SoapSerializationEnvelope envelope = null;
				try {
					envelope = service.getWebServiceReturner();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (XmlPullParserException e) {
					e.printStackTrace();
				}
				try {
					if (envelope.getResponse() != null) {
						SoapObject result = (SoapObject) envelope.bodyIn;
						connState = result.getProperty(0).toString();
						if (connState.equals("success")) {
							msg.what = OrderStringUtil.LOGIN_SUCCESS;
						} else if (connState.equals("fail")) {
							msg.what = OrderStringUtil.LOGIN_ERROR;
						}
					} else {
						msg.what = OrderStringUtil.LOGIN_ERROR;
					}
				} catch (Exception e) {
//					Log.d("sss", "XmlPullParserException" + e.getMessage());
					msg.what = OrderStringUtil.LOGIN_ERROR;
				}
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				msg.what = OrderStringUtil.LOGIN_SUCCESS;
				proHandle.sendMessage(msg);
			}
		}.start();
	}

	private Handler proHandle = new Handler() {
		public void handleMessage(Message msg) {
			AlertDialog.Builder builder = new AlertDialog.Builder(
					LoginActivity.this);
			prgDialog.dismiss();
			switch (msg.what) {
			case OrderStringUtil.LOGIN_SUCCESS:
				builder.setIcon(R.drawable.alert_ok)
						.setTitle("登陆成功")
						.setMessage("恭喜您，登陆成功")
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									// 点击确定按钮
									public void onClick(DialogInterface dialog,
											int which) {
										Intent intent = new Intent(
												LoginActivity.this,
												MainActivity.class);

										/*
										 * Bundle bundle = new Bundle();
										 * bundle.putString("data", flag);
										 * intent.putExtra("data", bundle);
										 */

										startActivity(intent);
									}
								}).show();
				break;
			case OrderStringUtil.LOGIN_ERROR:
				builder.setIcon(R.drawable.alert_error)
						.setTitle("错误")
						.setMessage("用户名或密码错误，请确认")
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									// 点击确定按钮
									public void onClick(DialogInterface dialog,
											int which) {
									}
								}).show();
				break;
			}
		}
	};

}

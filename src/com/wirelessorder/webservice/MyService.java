package com.wirelessorder.webservice;

import java.io.IOException;
import java.util.HashMap;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

/*
 * @author jmjiao
 * Android WebService
 */
public class MyService {

	private String nameSpace; //命名空间
	private String methodName; //方法名
	private String url; //服务器发布的ip信息
	private String soapAction; //应用程序字符串
	private String methodNames[]; //参数名
	private Object methodValues[]; //参数值
	private int methodLenth = 0; //参数个数
	private HashMap<String,Object> maps; //装载参数的map
	
	/**
	 * @param nameSpace 命名空间http://tempuri.org/
	 * @param methodName 方法名 login
	 * @param url 服务器发布的ip信息  http://172.20.9.82/MyService.asmx
	 * @param methodNames 参数变量 String methodNames[] = new String[]{"x","y","z"}
	 * @param methodValues 参数值 Object methodValues[] = new Object[]{9,14,8}
	 */
	
	public MyService(String nameSpace,String methodName,String url,String methodNames[],Object methodValues[]){
		this.nameSpace = nameSpace;
		this.url = url;
		this.methodName = methodName;
		this.methodNames = methodNames;
		this.methodValues = methodValues;
		this.soapAction = this.nameSpace + this.methodName;
		this.maps = new HashMap<String, Object>();
	}
	
	/**
	 * 加载参数
	 * @return boolean
	 */
	private boolean isMethodMapsOk(){
		int lenthN = methodNames.length;
		int lenthV = methodValues.length;
		if (lenthN != lenthV) {
			return false;
		}else{
			for (int i = 0; i < lenthV; i++) {
				maps.put(methodNames[i], methodValues[i]);
			}
			this.methodLenth = methodValues.length;
			return true;
		}
	}
	
	/**
	 * 取得WebService方法的返回值
	 * @return SoapSerializationEnvelope类型的返回值
	 * @throws IOExceptio io 流异常
	 * @throws XmlPullParserException XmlPullParser 异常
	 */
	public SoapSerializationEnvelope getWebServiceReturner () throws IOException,XmlPullParserException{
		//step1  指定WebService 的命名空间和调用的方法名
		SoapObject request = new SoapObject(nameSpace, methodName);
		//step2 设置调用方法的参数值，这里的参数要与Webservice的完全一致
		if (isMethodMapsOk()) {
			for (int i = 0; i < this.methodLenth; i++) {
				request.addProperty(methodNames[i], maps.get(methodNames[i]));
			}
		}
		//step3 生成调用WebService方法的SOAP请求信息，并指定SOAP的版本
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		//设置是否调用的是dotNet下的WebService
		envelope.dotNet = true;
		//必须，等价于envelope.bodyOut = request
		envelope.setOutputSoapObject(request);//设置请求
		
		//step4 创建HttpTransportSE对象
		HttpTransportSE ht = new HttpTransportSE(url);//服务其发布的ip信息
		
		//step5 调用WebService
		ht.call(soapAction, envelope);
		
		//step6 使用getResponse方法获得WebService方法的返回结果
		if (envelope.getResponse() != null) {
			//取值
			return envelope;
		}else{
			return null;
		}
		
	}
}

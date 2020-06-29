package il.co.ilrd.servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import il.co.ilrd.dbdriver.DbDriver;
import il.co.ilrd.dbdriver.DriverUtils;
import il.co.ilrd.dbdriver.KeyType;
import il.co.ilrd.dbdriver.ResourceType;

public class ServerUtils {
	protected static final String DB_URL = "jdbc:mysql://127.0.0.1/";
	protected static final String DB_USER = "erez";
	protected static final String DB_PASS = "erez";
	protected static final String DB_NAME = "customers";
	protected static final String TOKEN_FIELD = "token";
	protected static final String BAD_REQUEST = "Bad request";
	protected static final String LOGIN_ERROR_MSG = "user does not match password and/or type";
	protected static final String WRONG_USER = "no such user";
	protected static final String WRONG_TOKEN = "Login not authorized. please re-login";
	
	
	public static Map<String, Object> fillMapFromBody(HttpServletRequest request) {
		Map<String, Object> requestMap = new HashMap<>();
//		Map <String, String[]> map = request.getParameterMap();
		requestMap.put(DriverUtils.USER_TYPE, request.getParameter(DriverUtils.USER_TYPE));
		requestMap.put(DriverUtils.USER_NAME, request.getParameter(DriverUtils.USER_NAME));
		requestMap.put(DriverUtils.PASSWORD, request.getParameter(DriverUtils.PASSWORD));
		requestMap.put(DriverUtils.FIRST_NAME, request.getParameter(DriverUtils.FIRST_NAME));
		requestMap.put(DriverUtils.LAST_NAME, request.getParameter(DriverUtils.LAST_NAME));
		requestMap.put(DriverUtils.ADDRESS, request.getParameter(DriverUtils.ADDRESS));
		requestMap.put(DriverUtils.ADDRESS_ID, request.getParameter(DriverUtils.ADDRESS_ID));
		requestMap.put(DriverUtils.USER_EMAIL, request.getParameter(DriverUtils.USER_EMAIL));
		requestMap.put(DriverUtils.PHONE, request.getParameter(DriverUtils.USER_TYPE));
		requestMap.put(DriverUtils.CC_ID, request.getParameter(DriverUtils.CC_ID));
		requestMap.put(DriverUtils.CC_NUMBER, request.getParameter(DriverUtils.CC_NUMBER));
		requestMap.put(DriverUtils.CC_COMPANY_ID, request.getParameter(DriverUtils.CC_COMPANY_ID));
		requestMap.put(DriverUtils.CC_HOLDER_ID, request.getParameter(DriverUtils.CC_HOLDER_ID));
		requestMap.put(DriverUtils.CC_CV, request.getParameter(DriverUtils.CC_CV));
		requestMap.put(DriverUtils.CC_EXP_DATE, request.getParameter(DriverUtils.CC_EXP_DATE));
		requestMap.put(DriverUtils.COUNTRY_ID, request.getParameter(DriverUtils.COUNTRY_ID));
		requestMap.put(DriverUtils.PAYMENT_ID, request.getParameter(DriverUtils.PAYMENT_ID));
		requestMap.put(DriverUtils.CITY_ID, request.getParameter(DriverUtils.CITY_ID));
		requestMap.put(DriverUtils.STREET_NAME, request.getParameter(DriverUtils.STREET_NAME));
		requestMap.put(DriverUtils.STREET_NUMBER, request.getParameter(DriverUtils.STREET_NUMBER));
		requestMap.put(DriverUtils.BILLING_ADDRESS, request.getParameter(DriverUtils.BILLING_ADDRESS));
		requestMap.put(DriverUtils.ZIP, request.getParameter(DriverUtils.ZIP));
		requestMap.put(DriverUtils.PAYMENT_DETAILS_ID, request.getParameter(DriverUtils.PAYMENT_DETAILS_ID));
		requestMap.put(DriverUtils.PERSON_DETAILS_ID, request.getParameter(DriverUtils.PERSON_DETAILS_ID));
		requestMap.put(DriverUtils.CC_COMPANY_NAME, request.getParameter(DriverUtils.CC_COMPANY_NAME));		
		requestMap.put(DriverUtils.COMPANY_ID, request.getParameter(DriverUtils.COMPANY_ID));		
		requestMap.put(DriverUtils.CONTACT_ID, request.getParameter(DriverUtils.CONTACT_ID));		
		requestMap.put(DriverUtils.PRODUCT_NAME, request.getParameter(DriverUtils.PRODUCT_NAME));		
		requestMap.put(DriverUtils.DESCRIPTION, request.getParameter(DriverUtils.DESCRIPTION));		
		requestMap.put(DriverUtils.PRODUCT_ID, request.getParameter(DriverUtils.PRODUCT_ID));		
		requestMap.put(DriverUtils.ROW_ID, request.getParameter(DriverUtils.ROW_ID));		
		
		System.out.println("MAP:");
		for (Map.Entry<String, Object> elem : requestMap.entrySet()) {
			System.out.println("KEY: " + elem.getKey() + " VALUE: " + elem.getValue());
		}
		
		return requestMap;
	}
	
	public static Map<String, Object> getParameterMapPUT(HttpServletRequest request) {
	    Map<String, Object> dataMap = new HashMap<>();

	    try {
	        InputStreamReader reader = new InputStreamReader(request.getInputStream());
	        String data[] = new BufferedReader(reader).readLine().split("&");
	        for (String dataElem : data) {
	        	String[] Split = dataElem.split("=");
	        	if (Split.length == 2) {
	        		dataMap.put(Split[0], Split[1]);
	        	}
	        }
				
		} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}
	       
		System.out.println("MAP:");
		for (Map.Entry<String, Object> elem : dataMap.entrySet()) {
			System.out.println("KEY: " + elem.getKey() + " VALUE: " + elem.getValue());
		}
	    
	    return dataMap;
	}
	
	protected static boolean isUserPrivate(String userid, DbDriver dbDriver) {
		List<Map<String, Object>> list = dbDriver.get(ResourceType.USER, KeyType.ID, userid);
		return list.get(0).get(DriverUtils.USER_TYPE).equals("Private");
	}
	
	protected static boolean isRegisterPrivate(HttpServletRequest request) {
		return request.getParameter(DriverUtils.USER_TYPE).equals("Private");
	}
}
package il.co.ilrd.dbdriver;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

public class DriverUtils {
	public final static String NULL = "0";
	public final static String ADDRESS = "address";
	public final static String STREET_NAME = "street_name";
	public final static String STREET_NUMBER = "street_no";
	public final static String ADDRESS_TABLE = "Address";
	public final static String ADDRESS_ID = "address_id";
	public final static String BILLING_ADDRESS = "billing_address";
	public final static String BUSINESS_USER_ID = "business_user_id";
	public final static String BUSINESS_USER_TABLE = "BusinessUser";
	public final static String CC_DETAILS = "CardDetails";	
	public final static String CC_ID = "credit_card_id";	
	public final static String CITY = "City";
	public final static String CITY_ID = "city_id";	
	public final static String CITY_NAME = "city_name";	
	public final static String COUNTRY = "Country";
	public final static String COUNTRY_ID = "country_id";
	public final static String COUNTRY_NAME = "country_name";
	public final static String CC_NUMBER = "credit_card_number";
	public final static String CC_COMPANY_ID = "cc_comapny_id";
	public final static String CC_COMPANY_NAME = "company_name";
	public final static String COMPANY_NAME = "company_name";
	public final static String CONTACT_ID = "contact_id";
	public final static String CC_HOLDER_ID = "credit_card_holder_id";
	public final static String CC_EXP_DATE = "expire_date";
	public final static String CC_CV = "security_code";
	public final static String COMPANY_ID = "company_id";
	public final static String DESCRIPTION = "description";
	public final static String FIRST_NAME = "first_name";
	public final static String HISTORY_ID = "payment_history_id";		
	public final static String ID = "id";
	public final static String LAST_NAME = "last_name";
	public final static String PAYMENT_HISTORY_TABLE = "PaymentHistory";
	public final static String PRODUCT_TABLE = "Product";
	public final static String PRODUCT_ID = "product_id";
	public final static String PRODUCT_NAME = "product_name";
	public final static String PRIVATE_USER_TABLE = "PrivateUser";	
	public final static String PRIVATE_USER_ID = "private_user_id";	
	public final static String PERSON_DETAILS = "PersonDetails";	
	public final static String PERSON_DETAILS_ID = "person_details_id";		
	public final static String PAYMENT_DETAILS = "PaymentDetails";	
	public final static String PAYMENT_ID = "payment_id";		
	public final static String PAYMENT_DATE = "payment_date";		
	public final static String PAYMENT_AMOUNT = "amount";		
	public final static String PAYMENT_APPROVED = "approved";		
	public final static String PRODUCT_TO_COMPANY_TABLE = "ProductToCompany";
	public final static String PRODUCT_TO_COMPANY_KEY = "company_id";
	public final static String PRODUCT_TO_USER_TABLE = "ProductToPrivateUser";
	public final static String PHONE = "phone";
	public final static String PASSWORD = "pass";
	public final static String PAYMENT_DETAILS_ID = "payment_details_id";
	public final static String ROW_ID = "row_id";
	public final static String USER_TABLE = "Users";
	public final static String USER_TYPE = "user_type";
	public final static String USER_NAME = "username";
	public final static String USER_ID = "user_id";
	public final static String USER_EMAIL = "email";
	public final static String USER_DETAILS = "user_details";	
	public final static String ZIP = "zip";
	
	public static String createSQLAddRow(String tablename, String ... params) {
		String toReturn = "INSERT INTO " + tablename + " VALUES ("; 
		for (String x : params) {
			toReturn += "'" + x  + "', ";
		}
		toReturn = toReturn.substring(0, toReturn.length() - 2);
		toReturn += ")";
//		System.out.println(toReturn);
		return toReturn;
	}
	
	public static String createSQLUpdateRow(String tablename, Map<String, Object> resourceData, List<String> editList) {
		String toReturn = "UPDATE " + tablename + " SET "; 
		for (String elem : editList) {
			if (null != resourceData.get(elem)) {
				toReturn += elem + " = '" + resourceData.get(elem) + "' , ";
			}
		}
		toReturn = toReturn.substring(0, toReturn.length()- 2);
		toReturn += " WHERE " + editList.get(0) + " = '" + resourceData.get(editList.get(0)) +"'";
		return toReturn;
	}
	
	
	public static String ScramblePassword(String password) {
	       byte [] strAsByteArray = password.getBytes(); 
	        byte [] result = new byte [strAsByteArray.length]; 
	  
	        // Store result in reverse order into the 
	        // result byte[] 
	        for (int i = 0; i<strAsByteArray.length; i++) {
	            result[i] = strAsByteArray[strAsByteArray.length-i-1];
	        }
		return new String(result);
	}
}
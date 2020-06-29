package il.co.ilrd.dbdriver;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import il.co.ilrd.databasemanagement.DatabaseManagement;

public class SqlDbDriver implements DbDriver {
	Map<ResourceType, SqlDBResource> resourceMap = new HashMap<>();
	DatabaseManagement databaseManagement;
	
	
	public SqlDbDriver(String url, String username, String password, String databaseName) {
		try {
			databaseManagement = new DatabaseManagement(url, username, password, databaseName);
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		resourceMap.put(ResourceType.ADDRESS, new SqlAddressResource(databaseManagement));
		resourceMap.put(ResourceType.BUSINESS_USER, new SqlBusinessUserResource(databaseManagement));
		resourceMap.put(ResourceType.CARD_DETAILS, new SqlCardDetailsResource(databaseManagement));
		resourceMap.put(ResourceType.CITY, new SqlCityResource(databaseManagement));
		resourceMap.put(ResourceType.COMPANY, new SqlCompanyResource(databaseManagement));
		resourceMap.put(ResourceType.CONTACT, new SqlContactResource(databaseManagement));
		resourceMap.put(ResourceType.COUNTRY, new SqlCountryResource(databaseManagement));
		resourceMap.put(ResourceType.CC_COMPANY, new SqlCreditCardCompanyResource(databaseManagement));
		resourceMap.put(ResourceType.PAYMENT_DETAILS, new SqlPaymentDetailsResource(databaseManagement));
		resourceMap.put(ResourceType.PAYMENT_HISTORY, new SqlPaymentHistoryResource(databaseManagement));
		resourceMap.put(ResourceType.PERSON_DETAILS, new SqlPersonDetailsResource(databaseManagement));
		resourceMap.put(ResourceType.PRIVATE_USER, new SqlPrivateUserResource(databaseManagement));
		resourceMap.put(ResourceType.PRODUCT, new SqlProductResource(databaseManagement));
		resourceMap.put(ResourceType.USER, new SqlUsersResource(databaseManagement));
		
		resourceMap.put(ResourceType.COMPANY_TO_CONTACT, new SqlCompanyToContactJunction(databaseManagement));
		resourceMap.put(ResourceType.COMPANY_TO_USER, new SqlCompanyToUserJunction(databaseManagement));
		resourceMap.put(ResourceType.PRODUCT_TO_COMPANY, new SqlProductToCompanyJunction(databaseManagement));
		resourceMap.put(ResourceType.PRODUCT_TO_PRIVATE_USER, new SqlProductToPrivateUserJunction(databaseManagement));

	}

	@Override
	public List<Map<String, Object>> get(ResourceType resourceType, KeyType key, Object value) {
		return resourceMap.get(resourceType).get(key, value);
	}

	@Override
	public List<Map<String, Object>> getAll(ResourceType resourceType) {
		return resourceMap.get(resourceType).getAll();
	}

	@Override
	public int add(ResourceType resourceType, Map<String, Object> resourceData) {
		return resourceMap.get(resourceType).add(resourceData);
	}

	@Override
	public int edit(ResourceType resourceType, Map<String, Object> resourceData) {
		return resourceMap.get(resourceType).edit(resourceData);
	}

	@Override
	public void remove(ResourceType resourceType, Object key, Object value) {
		resourceMap.get(resourceType).remove(key, value);
	}

}
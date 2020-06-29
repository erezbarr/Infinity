package il.co.ilrd.dbdriver;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import il.co.ilrd.databasemanagement.DatabaseManagement;

public class SqlCreditCardCompanyResource implements SqlDBResource {
	private DatabaseManagement db;
	private final String TABLENAME = ResourceType.CC_COMPANY.toString();
	
	public SqlCreditCardCompanyResource(DatabaseManagement databaseManagement) {
		db = databaseManagement;
	}
	
	@Override
	public List<Map<String, Object>> get(KeyType key, Object value) {
		String keyToSearch = null;
		switch (key) {
		case ID:
			keyToSearch = DriverUtils.CC_COMPANY_ID;
			break;
		case NAME:
			keyToSearch = DriverUtils.CC_COMPANY_NAME;
			break;
		default:
			return null;
		}
		
		try {
			return db.readRow(TABLENAME, keyToSearch, value);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public List<Map<String, Object>> getAll() {
		return this.get(KeyType.ID,"*");
	}

	@Override
	public synchronized int add(Map<String, Object> resourceData) {
		try {
			return db.createRow(DriverUtils.createSQLAddRow(TABLENAME, 
															 DriverUtils.NULL, 
															 resourceData.get(DriverUtils.CC_COMPANY_NAME).toString()),
							TABLENAME,
							DriverUtils.CC_COMPANY_ID);		
			} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}
	}

	@Override
	public int edit(Map<String, Object> resourceData) {
		List<String> editList = new ArrayList<>();
		editList.add(DriverUtils.CC_COMPANY_ID);
		editList.add(DriverUtils.CC_COMPANY_NAME);
		try {
			db.updateRow(DriverUtils.createSQLUpdateRow(TABLENAME, resourceData, editList)); 
															
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}
		return Integer.parseInt(resourceData.get(DriverUtils.CC_COMPANY_ID).toString());
	}

	@Override
	public void remove(Object key, Object value) {
		try {
			db.deleteRow(TABLENAME, (String) key, value);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
package il.co.ilrd.dbdriver;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import il.co.ilrd.databasemanagement.DatabaseManagement;

public class SqlCompanyResource implements SqlDBResource {
	private DatabaseManagement db;
	private final String TABLENAME = ResourceType.COMPANY.toString();
	
	public SqlCompanyResource(DatabaseManagement databaseManagement) {
		db = databaseManagement;
	}
	
	@Override
	public List<Map<String, Object>> get(KeyType key, Object value) {
		String keyToSearch = null;
		switch (key) {
		case ID:
			keyToSearch = DriverUtils.COMPANY_ID;
			break;
		case NAME:
			keyToSearch = DriverUtils.COMPANY_NAME;
			break;		
		case PAYMENT_DETAILES_ID:
			keyToSearch = DriverUtils.PAYMENT_DETAILS_ID;
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
															 resourceData.get(DriverUtils.COMPANY_NAME).toString() , 
															 resourceData.get(DriverUtils.PAYMENT_DETAILS_ID).toString()),
							TABLENAME,
							DriverUtils.COMPANY_ID);	
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}
	}

	@Override
	public int edit(Map<String, Object> resourceData) {
		List<String> editList = new ArrayList<>();
		editList.add(DriverUtils.COMPANY_ID);
		editList.add(DriverUtils.COMPANY_NAME);
		editList.add(DriverUtils.PAYMENT_DETAILS_ID);
		try {
			db.updateRow(DriverUtils.createSQLUpdateRow(TABLENAME, resourceData, editList)); 
															
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}
		return Integer.parseInt(resourceData.get(DriverUtils.COMPANY_ID).toString());
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
package il.co.ilrd.dbdriver;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import il.co.ilrd.databasemanagement.DatabaseManagement;

public class SqlBusinessUserResource implements SqlDBResource {
	private DatabaseManagement db;
	private final String TABLENAME = ResourceType.BUSINESS_USER.toString();

	public SqlBusinessUserResource(DatabaseManagement databaseManagement) {
		db = databaseManagement;
	}
	
	@Override
	public List<Map<String, Object>> get(KeyType key, Object value) {
		String keyToSearch = null;
		switch (key) {
		case ID:
			keyToSearch = DriverUtils.BUSINESS_USER_ID;
			break;		
		case USER_ID:
			keyToSearch = DriverUtils.USER_ID;
			break;
		case FIRST_NAME:
			keyToSearch = DriverUtils.FIRST_NAME;
			break;
		case LAST_NAME:
			keyToSearch = DriverUtils.LAST_NAME;
			break;
		case EMAIL:
			keyToSearch = DriverUtils.USER_EMAIL;
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
														     resourceData.get(DriverUtils.USER_ID).toString(), 
														     resourceData.get(DriverUtils.FIRST_NAME).toString(), 
														     resourceData.get(DriverUtils.LAST_NAME).toString(), 
														     resourceData.get(DriverUtils.USER_EMAIL).toString()),
							TABLENAME,
							DriverUtils.BUSINESS_USER_ID);
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}
	}

	@Override
	public int edit(Map<String, Object> resourceData) {
		List<String> editList = new ArrayList<>();
		editList.add(DriverUtils.BUSINESS_USER_ID);
		editList.add(DriverUtils.USER_ID);
		editList.add(DriverUtils.FIRST_NAME);
		editList.add(DriverUtils.LAST_NAME);
		editList.add(DriverUtils.USER_EMAIL);
		try {
			db.updateRow(DriverUtils.createSQLUpdateRow(TABLENAME, resourceData, editList)); 
															
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}
		return Integer.parseInt(resourceData.get(DriverUtils.BUSINESS_USER_ID).toString());
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
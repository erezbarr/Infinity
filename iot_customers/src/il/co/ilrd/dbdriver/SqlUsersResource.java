package il.co.ilrd.dbdriver;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import il.co.ilrd.databasemanagement.DatabaseManagement;

public class SqlUsersResource implements SqlDBResource {
	private DatabaseManagement db;
	private final String TABLENAME = ResourceType.USER.toString();

	public SqlUsersResource(DatabaseManagement databaseManagement) {
		db = databaseManagement;
	}
	
	@Override
	public List<Map<String, Object>> get(KeyType key, Object value) {
		String keyToSearch = null;
		switch (key) {
		case ID:
			keyToSearch = DriverUtils.USER_ID;
			break;
		case NAME:
			keyToSearch = DriverUtils.USER_NAME;
			break;		
		case PASSWORD:
			keyToSearch = DriverUtils.PASSWORD;
			break;
		case USER_TYPE:
			keyToSearch = DriverUtils.USER_TYPE;
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
														    resourceData.get(DriverUtils.USER_NAME).toString(), 
														    DriverUtils.ScramblePassword(resourceData.get(DriverUtils.PASSWORD).toString()), 
														    resourceData.get(DriverUtils.USER_TYPE).toString()),
								TABLENAME,
								DriverUtils.USER_ID);
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}
	}

	@Override
	public int edit(Map<String, Object> resourceData) {
		List<String> editList = new ArrayList<>();
		editList.add(DriverUtils.USER_ID);
		editList.add(DriverUtils.USER_NAME);
		editList.add(DriverUtils.PASSWORD);
		editList.add(DriverUtils.USER_TYPE);
		try {
			db.updateRow(DriverUtils.createSQLUpdateRow(TABLENAME, resourceData, editList)); 
															
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}
		return Integer.parseInt((String)resourceData.get(DriverUtils.USER_ID));
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
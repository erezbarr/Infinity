package il.co.ilrd.dbdriver;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import il.co.ilrd.databasemanagement.DatabaseManagement;

public class ProductToCompany implements SqlDBResource {
	private DatabaseManagement db;
	private final String TABLENAME = ResourceType.ADDRESS.toString();

	
	public ProductToCompany(DatabaseManagement databaseManagement) {
		db = databaseManagement;
	}
	
	@Override
	public List<Map<String, Object>> get(KeyType key, Object value) {
		String keyToSearch = null;
		switch (key) {
		case ID:
			keyToSearch = DriverUtils.ROW_ID;
			break;
		case COMPANY_ID:
			keyToSearch = DriverUtils.COMPANY_ID;
			break;
		case USER_ID:
			keyToSearch = DriverUtils.USER_ID;
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
	public int add(Map<String, Object> resourceData) {
		try {
			return db.createRow(DriverUtils.createSQLAddRow(TABLENAME, 
															DriverUtils.NULL, 
															resourceData.get(DriverUtils.COMPANY_ID).toString(),	
															resourceData.get(DriverUtils.USER_ID).toString()),
								TABLENAME,
								DriverUtils.ROW_ID);	
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}
	}

	@Override
	public int edit(Map<String, Object> resourceData) {
		List<String> editList = new ArrayList<>();
		editList.add(DriverUtils.ROW_ID);
		editList.add(DriverUtils.COMPANY_ID);
		editList.add(DriverUtils.USER_ID);
		try {
			db.updateRow(DriverUtils.createSQLUpdateRow(TABLENAME, resourceData, editList)); 
															
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}
		return (int) resourceData.get(DriverUtils.ROW_ID);
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
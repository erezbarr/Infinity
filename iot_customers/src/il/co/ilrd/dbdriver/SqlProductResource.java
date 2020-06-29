package il.co.ilrd.dbdriver;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import il.co.ilrd.databasemanagement.DatabaseManagement;

public class SqlProductResource implements SqlDBResource {
	private DatabaseManagement db;
	private final String TABLENAME = ResourceType.PRODUCT.toString();

	public SqlProductResource(DatabaseManagement databaseManagement) {
		db = databaseManagement;
	}
	
	@Override
	public List<Map<String, Object>> get(KeyType key, Object value) {
		String keyToSearch = null;
		switch (key) {
		case ID:
			keyToSearch = DriverUtils.PRODUCT_ID;
			break;
		case USER_NAME:
			keyToSearch = DriverUtils.PRODUCT_NAME;
			break;
		case DESCRIPTION:
			keyToSearch = DriverUtils.DESCRIPTION;
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
			System.out.println("Adding product");
			return db.createRow(DriverUtils.createSQLAddRow(TABLENAME, 
														     DriverUtils.NULL, 
														     resourceData.get(DriverUtils.PRODUCT_NAME).toString(), 
														     resourceData.get(DriverUtils.DESCRIPTION).toString()),
								TABLENAME,
								DriverUtils.PRODUCT_ID);
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}
	}

	@Override
	public int edit(Map<String, Object> resourceData) {
		List<String> editList = new ArrayList<>();
		editList.add(DriverUtils.PRODUCT_ID);
		editList.add(DriverUtils.PRODUCT_NAME);
		editList.add(DriverUtils.DESCRIPTION);
		try {
			db.updateRow(DriverUtils.createSQLUpdateRow(TABLENAME, resourceData, editList)); 
															
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}
		return Integer.parseInt(resourceData.get(DriverUtils.PRODUCT_ID).toString());
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
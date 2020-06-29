package il.co.ilrd.dbdriver;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import il.co.ilrd.databasemanagement.DatabaseManagement;

public class SqlCardDetailsResource implements SqlDBResource {
	private DatabaseManagement db;
	private final String TABLENAME = ResourceType.CARD_DETAILS.toString();
	
	public SqlCardDetailsResource(DatabaseManagement databaseManagement) {
		db = databaseManagement;
	}
	
	@Override
	public List<Map<String, Object>> get(KeyType key, Object value) {
		String keyToSearch = null;
		switch (key) {
		case ID:
			keyToSearch = DriverUtils.CC_ID;
			break;
		case CC_NUMBER:
			keyToSearch = DriverUtils.CC_NUMBER;
			break;
		case CC_COMPANY_ID:
			keyToSearch = DriverUtils.CC_COMPANY_ID;
			break;
		case CC_HOLDER_ID:
			keyToSearch = DriverUtils.CC_HOLDER_ID;
			break;		
		case CC_EXP_DATE:
			keyToSearch = DriverUtils.CC_EXP_DATE;
			break;
		case CC_CV:
			keyToSearch = DriverUtils.CC_CV;
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
															 resourceData.get(DriverUtils.CC_NUMBER).toString() , 
															 resourceData.get(DriverUtils.CC_COMPANY_ID).toString(), 
															 resourceData.get(DriverUtils.CC_HOLDER_ID).toString(), 
															 resourceData.get(DriverUtils.CC_EXP_DATE).toString(), 
															 resourceData.get(DriverUtils.CC_CV).toString()),
							TABLENAME,
							DriverUtils.CC_ID);		
			} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}
	}

	@Override
	public int edit(Map<String, Object> resourceData) {
		List<String> editList = new ArrayList<>();
		editList.add(DriverUtils.CC_ID);
		editList.add(DriverUtils.CC_NUMBER);
		editList.add(DriverUtils.CC_COMPANY_ID);
		editList.add(DriverUtils.CC_HOLDER_ID);
		editList.add(DriverUtils.CC_EXP_DATE);
		editList.add(DriverUtils.CC_CV);
		try {
			db.updateRow(DriverUtils.createSQLUpdateRow(TABLENAME, resourceData, editList)); 
															
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}
		return Integer.parseInt(resourceData.get(DriverUtils.CC_ID).toString());
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
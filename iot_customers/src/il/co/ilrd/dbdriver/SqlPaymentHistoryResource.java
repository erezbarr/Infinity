package il.co.ilrd.dbdriver;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import il.co.ilrd.databasemanagement.DatabaseManagement;

public class SqlPaymentHistoryResource implements SqlDBResource {
	private DatabaseManagement db;
	private final String TABLENAME = ResourceType.PAYMENT_HISTORY.toString();
	
	public SqlPaymentHistoryResource(DatabaseManagement databaseManagement) {
		db = databaseManagement;
	}
	
	@Override
	public List<Map<String, Object>> get(KeyType key, Object value) {
		String keyToSearch = null;
		switch (key) {
		case ID:
			keyToSearch = DriverUtils.HISTORY_ID;
			break;
		case PAYMENT_ID:
			keyToSearch = DriverUtils.PAYMENT_ID;
			break;
		case DATE:
			keyToSearch = DriverUtils.PAYMENT_DATE;
			break;
		case AMOUNT:
			keyToSearch = DriverUtils.PAYMENT_AMOUNT;
			break;		
		case APPROVED:
			keyToSearch = DriverUtils.PAYMENT_APPROVED;
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
															 resourceData.get(DriverUtils.PAYMENT_ID).toString() , 
															 resourceData.get(DriverUtils.PAYMENT_DATE).toString(), 
															 resourceData.get(DriverUtils.PAYMENT_AMOUNT).toString(), 
															 resourceData.get(DriverUtils.PAYMENT_APPROVED).toString()),
							TABLENAME,
							DriverUtils.HISTORY_ID);	
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}
	}

	@Override
	public int edit(Map<String, Object> resourceData) {
		List<String> editList = new ArrayList<>();
		editList.add(DriverUtils.HISTORY_ID);
		editList.add(DriverUtils.PAYMENT_ID);
		editList.add(DriverUtils.PAYMENT_DATE);
		editList.add(DriverUtils.PAYMENT_AMOUNT);
		editList.add(DriverUtils.PAYMENT_APPROVED);
		try {
			db.updateRow(DriverUtils.createSQLUpdateRow(TABLENAME, resourceData, editList)); 
															
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}
		return Integer.parseInt(resourceData.get(DriverUtils.HISTORY_ID).toString());
	}

	public void remove(Object key, Object value) {
		try {
			db.deleteRow(TABLENAME, (String) key, value);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
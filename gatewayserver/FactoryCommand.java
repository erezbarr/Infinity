package il.co.ilrd.gatewayserver;

public interface FactoryCommand {
	public String run(Object data, DatabaseManagementInterface databaseManagement);
}

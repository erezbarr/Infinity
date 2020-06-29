package il.co.ilrd.DatabaseManagement;

import java.sql.SQLException;

public class runMySQL {
	static final String DB_NAME = "tadiran";
	static final String USER = "erez";
	static final String PASS = "erez";
	static final String URL = "jdbc:mysql://ubuntu:3306/";
	
	public static void main(String[] args) throws SQLException, ClassNotFoundException {
		DatabaseManagement databaseManagement = new DatabaseManagement(URL, USER, PASS, DB_NAME);
		NewTables(databaseManagement);		
		CreateRow(databaseManagement);
	//	DropTables(databaseManagement);
		AlterTables(databaseManagement); // ask on why this needs to come after some data was entered

		
		System.out.println("Done");

	}
	
	private static void NewTables(DatabaseManagement databaseManagement) throws ClassNotFoundException, SQLException {
		final String CompanyContactsTABLE = "CREATE TABLE IF NOT EXISTS CompanyContacts (\n" + "    pk_company_user_id int AUTO_INCREMENT PRIMARY KEY,\n" + "    fk_contact_id int NOT NULL\n" + ");";
		final String PaymentHistoryTABLE = "CREATE TABLE IF NOT EXISTS PaymentHistory (\n" + "	pk_payment_history_id int AUTO_INCREMENT PRIMARY KEY,\n" + "	fk_payment_id int NOT NULL,\n" + "    payment_date date,\n" + "    amount int NOT NULL,\n" + "    approved boolean NOT NULL\n" + ");";
		final String PaymentDetailTABLEs = "CREATE TABLE IF NOT EXISTS PaymentDetails (\n" + "    pk_payment_id int AUTO_INCREMENT PRIMARY KEY,\n" + "    fk_credit_card_number varchar(16),\n" + "    fk_billing_address int NOT NULL\n" + ");";
		final String IOTToUserContactTABLE = "CREATE TABLE IF NOT EXISTS IOTToUserContact (\n" + "    pk_id int AUTO_INCREMENT PRIMARY KEY,\n" + "    fk_serial_number varchar(16) NOT NULL,\n" + "    fk_contact_id int NOT NULL\n" + ");";
		final String IOTItemTABLE = "CREATE TABLE IF NOT EXISTS IOTItem (\n" + "    pk_serial_number varchar(16) PRIMARY KEY,\n" + "    fk_product_id int NOT NULL\n" + ");";
		final String IOTEventTABLE = "CREATE TABLE IF NOT EXISTS IOTEvent (\n" + "    pk_iot_event_id int AUTO_INCREMENT PRIMARY KEY,\n" + "	fk_serial_number varchar(16) NOT NULL,\n" + "    description varchar(255) NOT NULL,\n" + "    time_stamp TimeStamp DEFAULT CURRENT_TIMESTAMP\n" + ");";
		final String ContactTABLE = "CREATE TABLE IF NOT EXISTS Contact (\n" + "    pk_contact_id int AUTO_INCREMENT PRIMARY KEY,\n" + "    firstName varchar(20) NOT NULL,\n" + "    lastName varchar(40) NOT NULL,\n" + "    email varchar(20) NOT NULL UNIQUE,\n" + "    phone varchar(20) NOT NULL UNIQUE,\n" + "    fk_address_id int\n" + ");";
		final String ProductTABLE = "CREATE TABLE IF NOT EXISTS Product (\n" + "    pk_product_id int AUTO_INCREMENT PRIMARY KEY,\n" + "    product_name varchar(20) NOT NULL UNIQUE,\n" + "    description varchar(80)\n" + ");";
		final String AddressTABLE = "CREATE TABLE IF NOT EXISTS Address (\n" + "    pk_address_id int AUTO_INCREMENT PRIMARY KEY,\n" + "    zip varchar(10),\n" + "    address varchar(80) NOT NULL,\n" + "    fk_city_id int NOT NULL\n" + ");";
		final String CityTABLE = "CREATE TABLE IF NOT EXISTS City (\n" + "    pk_city_id int AUTO_INCREMENT PRIMARY KEY,\n" + "    city_name varchar(40) NOT NULL,\n" + "    fk_country_id int NOT NULL\n" + ");";
		final String CountryTABLE = "CREATE TABLE IF NOT EXISTS Country (\n" + "    pk_country_id int AUTO_INCREMENT PRIMARY KEY,\n" + "    country_name varchar(20) NOT NULL UNIQUE\n" + ");";
		final String CardDetailesTABLE = "CREATE TABLE IF NOT EXISTS CardDetails (\n" + "    pk_credit_card_number varchar(16) PRIMARY KEY,\n" + "    fk_cc_comapny_id int NOT NULL UNIQUE,\n" + "    credit_card_holder_id int NOT NULL,\n" + "    expire_date Date,\n" + "    security_code int\n" + ");";
		final String CreditCardCompanyTABLE = "CREATE TABLE IF NOT EXISTS CreditCardCompany (\n" + "    pk_cc_company_id int AUTO_INCREMENT PRIMARY KEY,\n" + "    company_name varchar(40) UNIQUE\n" + ");";
		
		databaseManagement.createTable(CompanyContactsTABLE);
		databaseManagement.createTable(PaymentHistoryTABLE);
		databaseManagement.createTable(PaymentDetailTABLEs);
		databaseManagement.createTable(IOTToUserContactTABLE);
		databaseManagement.createTable(IOTItemTABLE);
		databaseManagement.createTable(IOTEventTABLE);
		databaseManagement.createTable(ContactTABLE);
		databaseManagement.createTable(ProductTABLE);
		databaseManagement.createTable(AddressTABLE);
		databaseManagement.createTable(CityTABLE);
		databaseManagement.createTable(CountryTABLE);
		databaseManagement.createTable(CardDetailesTABLE);
		databaseManagement.createTable(CreditCardCompanyTABLE);	
	}	
	
	private static void AlterTables(DatabaseManagement databaseManagement) throws ClassNotFoundException, SQLException {
		final String ALTERPaymentHistory = "PaymentHistory \n" + "ADD FOREIGN KEY (fk_payment_id) REFERENCES PaymentDetails(pk_payment_id);";
		final String ALTERCompanyContacts = "CompanyContacts \n" + "ADD FOREIGN KEY (fk_contact_id) REFERENCES Contact(pk_contact_id);";
		final String ALTERIOTToUserContact = "IOTToUserContact \n" + "ADD FOREIGN KEY (fk_serial_number) REFERENCES IOTItem(pk_serial_number),\n" + "ADD FOREIGN KEY (fk_contact_id) REFERENCES Contact(pk_contact_id);";
		final String ALTERIOTEvent = "IOTEvent \n" + "ADD FOREIGN KEY (fk_serial_number) REFERENCES IOTItem(pk_serial_number);";
		final String ALTERIOTItem = "IOTItem \n" + "ADD FOREIGN KEY (fk_product_id) REFERENCES Product(pk_product_id);";
		final String ALTERContact = "Contact \n" + "ADD FOREIGN KEY (fk_address_id) REFERENCES Address(pk_address_id);";
		final String ALTERPaymentDetails = "PaymentDetails \n" + "ADD FOREIGN KEY (fk_credit_card_number) REFERENCES CardDetails(pk_credit_card_number),\n" +	"ADD FOREIGN KEY (fk_billing_address) REFERENCES Address(pk_address_id);";
		final String ALTERCardDetails = "CardDetails \n" + "ADD FOREIGN KEY (fk_cc_comapny_id) REFERENCES CreditCardCompany(pk_cc_company_id);";
		final String ALTERAddress = "Address \n" + "ADD FOREIGN KEY (fk_city_id) REFERENCES City(pk_city_id);";
		final String ALTERCity = "City \n" + "ADD FOREIGN KEY (fk_country_id) REFERENCES Country(pk_country_id);";
	
		databaseManagement.alterTable(ALTERPaymentHistory);
		databaseManagement.alterTable(ALTERCompanyContacts);
		databaseManagement.alterTable(ALTERIOTToUserContact);
		databaseManagement.alterTable(ALTERIOTEvent);
		databaseManagement.alterTable(ALTERIOTItem);
		databaseManagement.alterTable(ALTERContact);
		databaseManagement.alterTable(ALTERPaymentDetails);
		databaseManagement.alterTable(ALTERCardDetails);
		databaseManagement.alterTable(ALTERAddress);
		databaseManagement.alterTable(ALTERCity);
	
	}
	
	@SuppressWarnings("unused")
	private static void DropTables(DatabaseManagement databaseManagement) throws ClassNotFoundException, SQLException {
		final String DROPPaymentHistory = "PaymentHistory";
		final String DROPCompanyContacts = "CompanyContacts";
		final String DROPIOTToUserContact = "IOTToUserContact";
		final String DROPIOTEvent = "IOTEvent";
		final String DROPIOTItem = "IOTItem";
		final String DROPContact = "Contact";
		final String DROPPaymentDetails = "PaymentDetails";
		final String DROPCardDetails = "CardDetails";
		final String DROPAddress = "Address";
		final String DROPCity = "City";
		final String DROPCountry = "Country";
		final String DROPCreditCardCompany = "CreditCardCompany";
		final String DROPProduct = "Product";
	
		databaseManagement.deleteTable(DROPPaymentHistory);
		databaseManagement.deleteTable(DROPCompanyContacts);
		databaseManagement.deleteTable(DROPIOTToUserContact);
		databaseManagement.deleteTable(DROPIOTEvent);
		databaseManagement.deleteTable(DROPIOTItem);
		databaseManagement.deleteTable(DROPContact);
		databaseManagement.deleteTable(DROPPaymentDetails);
		databaseManagement.deleteTable(DROPCardDetails);
		databaseManagement.deleteTable(DROPAddress);
		databaseManagement.deleteTable(DROPCity);
		databaseManagement.deleteTable(DROPCountry);
		databaseManagement.deleteTable(DROPCreditCardCompany);
		databaseManagement.deleteTable(DROPProduct);
	
	}
	
	private static void CreateRow(DatabaseManagement databaseManagement) throws ClassNotFoundException, SQLException {
		final String INSERTCompanyContacts = "INSERT INTO CompanyContacts values(null, 1);";
		final String INSERTPaymentHistory = "INSERT INTO PaymentHistory values(null, 1, \"14-03-20\", 1, false), (null, 1, \"15-03-20\", 1, true);";
		final String INSERTPaymentDetails = "INSERT INTO PaymentDetails values(null, \"1234678910111213\", 1);";
		final String INSERTIOTToUserContact = "INSERT INTO IOTToUserContact values(null, \"00001\", 1);";
		final String INSERTIOTItem = "INSERT INTO IOTItem values(\"00001\", 1);";
		final String INSERTIOTEvent = "INSERT INTO IOTEvent values(null, \"00001\", \"software update 1.1\", null);";
		final String INSERTContact = "INSERT INTO Contact values(null, \"Israel\", \"Israeli\", \"israel@gmail.com\", \"0521111111\", 1);";
		final String INSERTProduct = "INSERT INTO Product values(null, \"Alpha\", \"Best Air Cond\");";
		final String INSERTAddress = "INSERT INTO Address values(null, \"11111\" ,\"Haatzamaut 12\", 1), (null, \"22222\", \"Trump st. 99\", 2);";
		final String INSERTCity = "INSERT INTO City values(null, \"Ramat Gan\", 1), (null, \"Texas\", 2);";
		final String INSERTCountry = "INSERT INTO Country values(null, \"Israel1\"), (null, \"USA1\");";
		final String INSERTCardDetails = "INSERT INTO CardDetails values(\"1234678910111213\" , 1, 32331, \"09-04-25\", 123);";
		final String INSERTCreditCardCompany = "INSERT INTO CreditCardCompany values(null, \"Visa\");";

		databaseManagement.createRow(INSERTPaymentHistory);
		databaseManagement.createRow(INSERTCompanyContacts);
		databaseManagement.createRow(INSERTIOTToUserContact);
		databaseManagement.createRow(INSERTIOTEvent);
		databaseManagement.createRow(INSERTIOTItem);
		databaseManagement.createRow(INSERTContact);
		databaseManagement.createRow(INSERTPaymentDetails);
		databaseManagement.createRow(INSERTCardDetails);
		databaseManagement.createRow(INSERTAddress);
		databaseManagement.createRow(INSERTCity);
		databaseManagement.createRow(INSERTCountry);
		databaseManagement.createRow(INSERTCreditCardCompany);
		databaseManagement.createRow(INSERTProduct);
		
	}
}
package il.co.ilrd.dbdriver;

public enum ResourceType {
	ADDRESS ("Address"),
	BUSINESS_USER ("BusinessUser"),
	CARD_DETAILS ("CardDetails"),
	CC_COMPANY ("CreditCardCompany"),
	CITY ("City"),
	COUNTRY ("Country"),
	COMPANY_TO_CONTACT ("CompanyToContact"),
	COMPANY_TO_USER ("CompanyToUser"),
	COMPANY ("Company"),
	CONTACT ("Contact"),
	PAYMENT_HISTORY ("PaymentHistory"),
	PERSON_DETAILS ("PersonDetails"),
	PAYMENT_DETAILS ("PaymentDetails"),
	PRODUCT_TO_COMPANY ("ProductToCompany"),
	PRODUCT ("Product"),
	PRIVATE_USER ("PrivateUser"),
	PRODUCT_TO_PRIVATE_USER ("ProductToPrivateUser"),
	USER ("Users");

	private final String name; 

    private ResourceType(String s) {
        name = s;
    }
	
    public String toString() {
        return this.name;
     }
}
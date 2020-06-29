package il.co.ilrd.servlets;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import il.co.ilrd.dbdriver.DbDriver;
import il.co.ilrd.dbdriver.DriverUtils;
import il.co.ilrd.dbdriver.KeyType;
import il.co.ilrd.dbdriver.ResourceType;
import il.co.ilrd.dbdriver.SqlDbDriver;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private DbDriver dbDriver;

    public RegisterServlet() {
    	dbDriver = new SqlDbDriver(ServerUtils.DB_URL, ServerUtils.DB_USER, ServerUtils.DB_PASS, ServerUtils.DB_NAME);
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub - need to finish this - will send to the post page
		response.sendError(HttpServletResponse.SC_BAD_REQUEST);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Map<String, Object> requestMap = ServerUtils.fillMapFromBody(request);
		
		if(dbDriver.get(ResourceType.USER, KeyType.NAME, request.getParameter(DriverUtils.USER_NAME)).size() != 0 || 
				dbDriver.get(ResourceType.PERSON_DETAILS, KeyType.EMAIL, requestMap.get(DriverUtils.USER_EMAIL)).size() != 0) {
			System.out.println("LOSER");
		}else {
			requestMap.put(DriverUtils.USER_ID, dbDriver.add(ResourceType.USER, requestMap));
			if (ServerUtils.isRegisterPrivate(request)) {
				requestMap.put(DriverUtils.CC_ID, dbDriver.add(ResourceType.CARD_DETAILS, requestMap));
				requestMap.put(DriverUtils.PAYMENT_DETAILS_ID, dbDriver.add(ResourceType.PAYMENT_DETAILS, requestMap));
//				System.out.println("patmeny id" +requestMap.get(DriverUtils.PAYMENT_DETAILS_ID).toString());
//				requestMap.put(DriverUtils.ADDRESS_ID, dbDriver.add(ResourceType.ADDRESS, requestMap)); - should get this from a list?????
				requestMap.put(DriverUtils.USER_DETAILS, dbDriver.add(ResourceType.PERSON_DETAILS, requestMap));
				dbDriver.add(ResourceType.PRIVATE_USER, requestMap);
			} else {
				requestMap.put(DriverUtils.USER_DETAILS, dbDriver.add(ResourceType.PERSON_DETAILS, requestMap));
				dbDriver.add(ResourceType.BUSINESS_USER, requestMap);
			}
		doGet(request, response);
		}
	}
}
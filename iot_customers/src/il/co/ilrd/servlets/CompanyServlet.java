package il.co.ilrd.servlets;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonObject;

import il.co.ilrd.dbdriver.DbDriver;
import il.co.ilrd.dbdriver.DriverUtils;
import il.co.ilrd.dbdriver.ResourceType;
import il.co.ilrd.dbdriver.SqlDbDriver;

/**
 * Servlet implementation class UsersServlet
 */
@WebServlet("/companies/*")
public class CompanyServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private DbDriver dbDriver;
	private SessionManagment sessionManagment = SessionManagment.getInstance();

    public CompanyServlet() {
    	dbDriver = new SqlDbDriver(ServerUtils.DB_URL, ServerUtils.DB_USER, ServerUtils.DB_PASS, ServerUtils.DB_NAME);
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub - need to finish this - will send to the post page
		response.sendError(HttpServletResponse.SC_BAD_REQUEST);
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String userid = request.getRequestURI().substring(request.getRequestURI().lastIndexOf("/") + 1);
		JsonObject responseJson = new JsonObject();
		Map<String, Object> requestMap = ServerUtils.fillMapFromBody(request);
		
		if(!sessionManagment.isTokenValid(Integer.parseInt(userid), request.getHeader(ServerUtils.TOKEN_FIELD))) {
			response.getWriter().append(ServerUtils.WRONG_TOKEN).append(request.getContextPath());
			return;
		}
		
		requestMap.put(DriverUtils.USER_ID, userid);
		
		requestMap.put(DriverUtils.CC_ID, dbDriver.add(ResourceType.CARD_DETAILS, requestMap));
		requestMap.put(DriverUtils.PAYMENT_DETAILS_ID, dbDriver.add(ResourceType.PAYMENT_DETAILS, requestMap));
		requestMap.put(DriverUtils.ADDRESS_ID, dbDriver.add(ResourceType.ADDRESS, requestMap));
		requestMap.put(DriverUtils.CONTACT_ID, dbDriver.add(ResourceType.CONTACT, requestMap));
		requestMap.put(DriverUtils.COMPANY_ID, dbDriver.add(ResourceType.COMPANY, requestMap));
		dbDriver.add(ResourceType.COMPANY_TO_CONTACT, requestMap);
		
		responseJson.addProperty("Company Status ", "Company Added");
		
		response.getWriter().append(responseJson.toString());
	}

	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String userid = request.getRequestURI().substring(request.getRequestURI().lastIndexOf("/") + 1);
		JsonObject responseJson = new JsonObject();
		Map<String, Object> requestMap = ServerUtils.getParameterMapPUT(request);
		
		if(!sessionManagment.isTokenValid(Integer.parseInt(userid), request.getHeader(ServerUtils.TOKEN_FIELD))) {
			response.getWriter().append(ServerUtils.WRONG_TOKEN).append(request.getContextPath());
			return;
		}
		
		requestMap.put(DriverUtils.USER_ID, userid);
		requestMap.put(DriverUtils.CC_EXP_DATE, requestMap.get(DriverUtils.CC_EXP_DATE).toString().replace("%20", " ").replace("%3A", ":"));
		requestMap.put(DriverUtils.CC_ID, dbDriver.edit(ResourceType.CARD_DETAILS, requestMap));
		requestMap.put(DriverUtils.PAYMENT_DETAILS_ID, dbDriver.edit(ResourceType.PAYMENT_DETAILS, requestMap));
		requestMap.put(DriverUtils.ADDRESS_ID, dbDriver.edit(ResourceType.ADDRESS, requestMap));
		requestMap.put(DriverUtils.CONTACT_ID, dbDriver.edit(ResourceType.CONTACT, requestMap));
		requestMap.put(DriverUtils.COMPANY_ID, dbDriver.edit(ResourceType.COMPANY, requestMap));
		dbDriver.edit(ResourceType.COMPANY_TO_CONTACT, requestMap);
		
		responseJson.addProperty("Product Status ", "Product Updated");
				
		response.getWriter().append(responseJson.toString());	
	}

	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String userid = request.getRequestURI().substring(request.getRequestURI().lastIndexOf("/") + 1);
		JsonObject responseJson = new JsonObject();
		Map<String, Object> requestMap = ServerUtils.getParameterMapPUT(request);
		
		if(!sessionManagment.isTokenValid(Integer.parseInt(userid), request.getHeader(ServerUtils.TOKEN_FIELD))) {
			response.getWriter().append(ServerUtils.WRONG_TOKEN).append(request.getContextPath());
			return;
		}
		
		dbDriver.remove(ResourceType.COMPANY_TO_CONTACT, DriverUtils.COMPANY_ID, requestMap.get(DriverUtils.COMPANY_ID));
		dbDriver.remove(ResourceType.COMPANY_TO_USER, DriverUtils.COMPANY_ID, requestMap.get(DriverUtils.COMPANY_ID));
		dbDriver.remove(ResourceType.PRODUCT_TO_COMPANY, DriverUtils.COMPANY_ID, requestMap.get(DriverUtils.COMPANY_ID));
		dbDriver.remove(ResourceType.COMPANY, DriverUtils.COMPANY_ID, requestMap.get(DriverUtils.COMPANY_ID));
		
		responseJson.addProperty("Delete Status ", "Company Deleted");
				
		response.getWriter().append(responseJson.toString());
	}
}

package il.co.ilrd.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonObject;

import il.co.ilrd.dbdriver.DbDriver;
import il.co.ilrd.dbdriver.DriverUtils;
import il.co.ilrd.dbdriver.KeyType;
import il.co.ilrd.dbdriver.ResourceType;
import il.co.ilrd.dbdriver.SqlDbDriver;

/**
 * Servlet implementation class UsersServlet
 */
@WebServlet("/users/*")
public class UsersServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private DbDriver dbDriver;
	private SessionManagment sessionManagment = SessionManagment.getInstance();

    public UsersServlet() {
    	dbDriver = new SqlDbDriver(ServerUtils.DB_URL, ServerUtils.DB_USER, ServerUtils.DB_PASS, ServerUtils.DB_NAME);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String userid = request.getRequestURI().substring(request.getRequestURI().lastIndexOf("/") + 1);
		JsonObject responseJson = new JsonObject();
		List<Map<String,Object>> resultList = null;
			
		if(!sessionManagment.isTokenValid(Integer.parseInt(userid), request.getHeader(ServerUtils.TOKEN_FIELD))) {
			response.getWriter().append(ServerUtils.WRONG_TOKEN).append(request.getContextPath());
			return;
		}
		
		if (ServerUtils.isUserPrivate(userid, dbDriver)) {
			resultList = dbDriver.get(ResourceType.PRODUCT, KeyType.USER_ID, userid);
			
			if(resultList.size() == 0) {
				responseJson.addProperty("Bad request", "no products in list");
			}else {
				responseJson.addProperty("products", resultList.toString());
			}
		} else {
			List<Map<String,Object>> copmanyList = new ArrayList<>();;
			resultList = dbDriver.get(ResourceType.COMPANY_TO_USER, KeyType.USER_ID, userid);
			
			for (Map<String,Object> companyID : resultList) {
				List<Map<String,Object>> copmanyTempList = dbDriver.get(ResourceType.COMPANY, KeyType.ID, companyID.get(DriverUtils.COMPANY_ID));
				copmanyList.add(copmanyTempList.get(0));
			}

			if(copmanyList.size() == 0) {
				responseJson.addProperty("Bad request", "no companies in list");
			}else {
				responseJson.addProperty("companies", resultList.toString());
			}

		}
		
		response.getWriter().append(responseJson.toString());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.sendError(HttpServletResponse.SC_BAD_REQUEST);
	}

	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String userid = request.getRequestURI().substring(request.getRequestURI().lastIndexOf("/") + 1);
		JsonObject responseJson = new JsonObject();
		
		if(!sessionManagment.isTokenValid(Integer.parseInt(userid), request.getHeader(ServerUtils.TOKEN_FIELD))) {
			response.getWriter().append(ServerUtils.WRONG_TOKEN).append(request.getContextPath());
			return;
		}
		
		dbDriver.remove(ResourceType.USER, DriverUtils.USER_ID, userid);
		
		responseJson.addProperty("Delete Status ", "User Deleted");
				
		response.getWriter().append(responseJson.toString());
	}
	
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String userid = request.getRequestURI().substring(request.getRequestURI().lastIndexOf("/") + 1);
		System.out.println(userid);
		JsonObject responseJson = new JsonObject();
		Map<String, Object> requestMap = ServerUtils.getParameterMapPUT(request);
		
		if(!sessionManagment.isTokenValid(Integer.parseInt(userid), request.getHeader(ServerUtils.TOKEN_FIELD))) {
			response.getWriter().append(ServerUtils.WRONG_TOKEN).append(request.getContextPath());
			return;
		}
		
		requestMap.put(DriverUtils.USER_ID, userid);
		dbDriver.edit(ResourceType.USER, requestMap);
		if (ServerUtils.isUserPrivate(userid, dbDriver)) {
			dbDriver.edit(ResourceType.CARD_DETAILS, requestMap);
			dbDriver.edit(ResourceType.PAYMENT_DETAILS, requestMap);
			dbDriver.edit(ResourceType.PERSON_DETAILS, requestMap);
		} else {
			dbDriver.edit(ResourceType.BUSINESS_USER, requestMap);
		}
		
		responseJson.addProperty("Update Status ", "User Updated");
				
		response.getWriter().append(responseJson.toString());	
	}
}
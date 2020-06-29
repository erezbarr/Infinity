package il.co.ilrd.servlets;

import java.io.IOException;
import java.util.Map;

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

@WebServlet("/products/*")
public class ProductServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private DbDriver dbDriver;
	private SessionManagment sessionManagment = SessionManagment.getInstance();

    public ProductServlet() {
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
		requestMap.put(DriverUtils.PRODUCT_ID, dbDriver.add(ResourceType.PRODUCT, requestMap));
		System.out.println("Adding product No: " + requestMap.get(DriverUtils.PRODUCT_ID));
		if (ServerUtils.isUserPrivate(userid, dbDriver)) {
			dbDriver.add(ResourceType.PRODUCT_TO_PRIVATE_USER, requestMap);
		} else {
			requestMap.put(DriverUtils.USER_ID, userid);
			dbDriver.add(ResourceType.PRODUCT_TO_COMPANY, requestMap);
		}
		
		responseJson.addProperty("Product Status ", "Product Added");
		
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
		
		requestMap.put(DriverUtils.PRODUCT_ID, requestMap.get(DriverUtils.PRODUCT_ID));
		dbDriver.edit(ResourceType.PRODUCT, requestMap);
		
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
		String productid = (String) requestMap.get(DriverUtils.PRODUCT_ID);
		System.out.println("productid: " + productid);
		if (ServerUtils.isUserPrivate(userid, dbDriver)) {
			dbDriver.remove(ResourceType.PRODUCT_TO_PRIVATE_USER, DriverUtils.PRODUCT_ID, productid);
		} else {
			dbDriver.remove(ResourceType.PRODUCT_TO_COMPANY, DriverUtils.PRODUCT_ID, productid);
		}
		dbDriver.remove(ResourceType.PRODUCT, DriverUtils.PRODUCT_ID, productid);
		
		responseJson.addProperty("Delete Status ", "Product Deleted");
				
		response.getWriter().append(responseJson.toString());
	}
}
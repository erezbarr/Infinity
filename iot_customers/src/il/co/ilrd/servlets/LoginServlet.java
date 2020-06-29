package il.co.ilrd.servlets;

import java.io.IOException;
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

@WebServlet("/")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private DbDriver dbDriver;
	private SessionManagment sessionManagment = SessionManagment.getInstance();
	
    public LoginServlet() {
    	dbDriver = new SqlDbDriver(ServerUtils.DB_URL, ServerUtils.DB_USER, ServerUtils.DB_PASS, ServerUtils.DB_NAME);
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub - need to finish this - will send to the post page
		response.sendError(HttpServletResponse.SC_BAD_REQUEST);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		JsonObject responseJson = new JsonObject();
		String pass = request.getParameter(DriverUtils.PASSWORD);
		String user = request.getParameter(DriverUtils.USER_NAME);
		String userType = request.getParameter(DriverUtils.USER_TYPE);
		System.out.println("pass: " + pass + " user: " + user + " type: " + userType);
		List<Map<String, Object>> result = dbDriver.get(ResourceType.USER, KeyType.NAME, user);
		
		if(result.size() == 0) {
			responseJson.addProperty(ServerUtils.BAD_REQUEST, ServerUtils.WRONG_USER);
		}else if (sessionManagment.isLoginValid(result, DriverUtils.ScramblePassword(pass), userType)) {
			String token = sessionManagment.addSession(Integer.parseInt(result.get(0).get(DriverUtils.USER_ID).toString())).getToken();
			responseJson.addProperty(ServerUtils.TOKEN_FIELD, token);
			responseJson.addProperty(DriverUtils.USER_ID, result.get(0).get(DriverUtils.USER_ID).toString());
		}else {
			responseJson.addProperty(ServerUtils.BAD_REQUEST, ServerUtils.LOGIN_ERROR_MSG);
		}
		response.getWriter().append(responseJson.toString());
	}
}
package servlet;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import dao.NewsDAO;

public class DeleteRecordServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2256747730443895159L;

	/**
	 * Constructor of the object.
	 */
	public DeleteRecordServlet() {
		super();
	}

	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}

	/**
	 * The doGet method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		//直接调用doPost方法
		doPost(request, response);
	}

	/**
	 * The doPost method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to post.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		//设置编码
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		//获取用户编号
		String userId = request.getParameter("user_id");
		System.out.println("user_id = " + userId);
		//获取记录类型
		String recordType = request.getParameter("record_type");
		System.out.println("record_type" + recordType);
		//获取新闻编号集合
		String data = request.getParameter("news_id_list");
		//将数据转换成集合形式
		Gson gson = new Gson();
		Type type = new TypeToken<ArrayList<Integer>>(){}.getType();
		List<Integer> newsIdList = gson.fromJson(data, type);
		for (int i = 0; i < newsIdList.size(); i++) {
			System.out.println("id = " + newsIdList.get(i));
		}
		//从数据库中删除记录
		NewsDAO dao = NewsDAO.newInstance();
		dao.deleteRecord(Integer.valueOf(userId), newsIdList, recordType);
		//向客户端返回信息
		response.getWriter().write("delete successful");
	}

	/**
	 * Initialization of the servlet. <br>
	 *
	 * @throws ServletException if an error occurs
	 */
	public void init() throws ServletException {
		// Put your code here
	}

}

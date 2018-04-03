package servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.NewsDAO;

public class CollectNewsServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3072114550093610607L;

	/**
	 * Constructor of the object.
	 */
	public CollectNewsServlet() {
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
		String result = null;	//处理结果
		//获取请求参数
		String requestType = request.getParameter("request_type");
		int userId = Integer.valueOf(request.getParameter("user_id"));
		int newsId = Integer.valueOf(request.getParameter("news_id"));
		//根据请求类型，执行不同处理
		if ("condition".equals(requestType)) {
			//获取收藏情况
			NewsDAO dao = NewsDAO.newInstance();
			result = dao.getCollectCondition(userId, newsId) + "";
		} else if ("operation".equals(requestType)) {
			NewsDAO dao = NewsDAO.newInstance();	//数据库操作对象
			//获取收藏情况
			boolean collected = Boolean.valueOf(request.getParameter("collected"));
			if (collected) {
				//执行收藏新闻操作
				dao.insertCollectedNews(userId, newsId);
				result = "收藏成功";
			} else {
				//删除已收藏新闻
				dao.deleteCollectedNews(userId, newsId);
				result = "取消收藏";
			}
		} else {
			return;
		}
		//返回结果
		PrintWriter writer = response.getWriter();
		writer.write(result);
		writer.flush();
		writer.close();
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

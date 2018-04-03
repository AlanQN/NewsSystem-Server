package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import model.NewsBean;

import dao.NewsDAO;

public class SchemaServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 845765304125079355L;

	/**
	 * 构造函数
	 */
	public SchemaServlet() {
		super();
	}

	/**
	 * 析构函数
	 */
	public void destroy() {
		super.destroy();
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
		//直接调用doPost
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
		//新闻数据
		List<NewsBean> newsData = new ArrayList<NewsBean>();
		//设置请求和响应的编码统一为UTF-8
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		//获取新闻类型
		String type = request.getParameter("news_type");
		//获取新闻数据的最新时间
		String lastTime = request.getParameter("last_time");
		//获取数据库连接对象
		NewsDAO dao = NewsDAO.newInstance();
		//获取新闻数据
		if (type != null) {
			newsData = dao.queryNewsData(type, lastTime);
		}
		//将数据转换为json格式
		Gson gson = new Gson();
		String data = gson.toJson(newsData);
		System.out.println(data);
		//以json格式返回数据
		response.setContentType("text/html;charset=utf-8;");
		PrintWriter writer = response.getWriter();
		writer.write(data);
		writer.flush();
		writer.close();
	}

	/**
	 * 初始化
	 */
	public void init() throws ServletException {
	}

}

package servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.NewsBean;

import dao.NewsDAO;

public class DetailServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor of the object.
	 */
	public DetailServlet() {
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
		//调用doPost
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
		//设置请求和响应的编码统一为UTF-8
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
		//获取编号
        int newsId = Integer.parseInt(request.getParameter("news_id"));
        int userId = Integer.parseInt(request.getParameter("user_id"));
        //创建数据库操作DAO对象
        NewsDAO dao = NewsDAO.newInstance();
        //查询新闻数据
        NewsBean news = dao.queryNews(userId, newsId);
        //显示新闻信息
        if (news != null) {
			request.setAttribute("title", news.getTitle());
			request.setAttribute("publishTime", news.getPublishTime());
			request.setAttribute("content", news.getContent());
			request.setAttribute("publisher", news.getPublisher());
			//转发到content.jsp
            request.getRequestDispatcher("content.jsp").forward(request, response);
		} else{
			response.setContentType("text/html; charset=utf-8;");
            response.getWriter().append("新闻不存在！！！");
        }
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

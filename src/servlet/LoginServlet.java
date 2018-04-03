package servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import dao.NewsDAO;

import util.TextUtils;

public class LoginServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3091463350951773552L;

	/**
	 * Constructor of the object.
	 */
	public LoginServlet() {
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
		//设置编码
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		//获取输出流
		PrintWriter writer = response.getWriter();
		//获取用户提交的用户名和密码
		String user = request.getParameter("user");
		String publisher = request.getParameter("publisher");
		String password = request.getParameter("password");
		//根据不同的用户执行不同的操作
		if ((TextUtils.isEmpty(user) && TextUtils.isEmpty(publisher)) || (!TextUtils.isEmpty(user) && !TextUtils.isEmpty(publisher))) {
			writer.print(false);
		} else {
			if (!TextUtils.isEmpty(user)) {
				//1.执行普通用户登录校验
				//创建数据库操作对象
				NewsDAO dao = NewsDAO.newInstance();
				//获取用户的密码
				String userPassword = dao.getUserPassword(user);
				//比对密码
				boolean result = password.equals(userPassword);
				//返回结果
				writer.print(result);
			} else if (!TextUtils.isEmpty(publisher)) {
				//2.执行新闻发布者登录校验
				//创建数据库操作对象
				NewsDAO dao = NewsDAO.newInstance();
				//获取发布者密码
				String publisherPassword = dao.getPublisherPassword(publisher);
				//比对密码
				boolean result = password.equals(publisherPassword);
				//根据比对结果，跳转
				if (result) {
					//登录成功
					HttpSession session = request.getSession();
					session.setAttribute("publisher", publisher);
					//跳转到首页
					request.getRequestDispatcher("index.jsp").forward(request, response);
				} else {
					//登录失败，跳转到登录界面
					response.setContentType("text/html;charset=utf-8;");
					response.getWriter().print("<script type='text/javascript'>alert('用户名不存在或密码错误');location.href='login.html';</script>");
				}
			}
		}
		//关闭流
		writer.flush();
		writer.close();
	}

	/**
	 * Initialization of the servlet. <br>
	 *
	 * @throws ServletException if an error occurs
	 */
	public void init() throws ServletException {

	}

}

package servlet;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import model.NewsBean;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import util.TextUtils;
import dao.NewsDAO;


public class UeditorServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");	//时间格式化
	private static final String BASE_URL = "http://192.168.191.1:9634/NewsSystem";	//项目地址
	private static final String ICON_DIR = "/upload/resource/icon";	//存放图标的目录

	/**
	 * Constructor of the object.
	 */
	public UeditorServlet() {
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
		//采用doPost处理逻辑处理
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
		//获取发布者
		HttpSession session = request.getSession();
		String publisher = (String) session.getAttribute("publisher");
		System.out.println("publisher = " + publisher);
		//拿到编辑器的内容
		String content = null;
		String title = null;
		String type = null;
		String icon = "";
		//处理图标
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		if (isMultipart) {
			FileItemFactory factory = new DiskFileItemFactory();
			ServletFileUpload upload = new ServletFileUpload(factory);
			upload.setHeaderEncoding("UTF-8");
			Iterator<FileItem> items;
			try {
				items = upload.parseRequest(request).iterator();
				while (items.hasNext()) {
					FileItem item = items.next();
					if (!item.isFormField()) {
						//去除上传文件的名称
						String name = item.getName();
						if (name != null && !"".equals(name)) {
							System.out.println("上传的文件：" + name);
							String fileName = name.substring(name.lastIndexOf("\\")+1, name.length());
							//上传文件
							String parentDir = this.getServletContext().getRealPath(ICON_DIR);
							File uploadFile = new File(parentDir,fileName);
							if(! uploadFile.getParentFile().exists()){  //父目录不存在
								uploadFile.getParentFile().mkdirs();        //mkdirs():创建文件夹，如果上级目录没有的话，也一并创建出来。
							}
							item.write(uploadFile);
							//设置图标地址
							icon = BASE_URL + ICON_DIR + "/" + fileName;
							System.out.println("图片地址" +icon);
						}
					} else {
						//表单字段名称
						String fieldName = item.getFieldName();
						//表单字段值
						String fieldValue = item.getString("UTF-8");
						//根据字段名称设置相应字段值
						if ("news_title".equals(fieldName)) {
							title = fieldValue;
						} else if ("news_type".equals(fieldName)) {
							type = fieldValue;
						} else if ("editorValue".endsWith(fieldName)) {
							content = fieldValue;
						}
					}

				}
			} catch (FileUploadException e) {
				e.printStackTrace();
				System.out.println("文件上传失败");
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("文件上传时失败");
			}
		}

		//如果不为空
		if(!TextUtils.isEmpty(title) && !TextUtils.isEmpty(type) && !TextUtils.isEmpty(content) && !TextUtils.isEmpty(publisher)){
			//创建新闻对象
			NewsBean news = new NewsBean();
			//设置属性值
			news.setPublisher(publisher);
			news.setContent(content);
			news.setTitle(title);
			news.setType(type);
			news.setIcon(icon);
			//获取时间
			request.setAttribute("publishTime", sdf.format(new Date()));
			request.setAttribute("title", title);
			request.setAttribute("content",content);
			request.setAttribute("publisher", publisher);
			//转发到content.jsp
			request.getRequestDispatcher("content.jsp").forward(request, response);
			//执行数据库插入操作
			NewsDAO dao = NewsDAO.newInstance();
			dao.insertNews(news);
		}else{
			response.setContentType("text/html; charset=utf-8");
			response.getWriter().append("内容为空!");
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

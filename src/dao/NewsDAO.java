package dao;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import model.NewsBean;
import model.RecordBean;

import com.mysql.jdbc.Connection;

/**
 * 数据库操作类
 */

public class NewsDAO {

	private static NewsDAO instance = null;	//单个实例

	private static final SimpleDateFormat timeDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");	//时间格式化对象，精确到秒
	private static final SimpleDateFormat dateDF = new SimpleDateFormat("yyyy-MM-dd");	//日期格式化对象，精确到日期
	private static final String DB_URL = "jdbc:mysql://192.168.191.1:3306/NewsDB"/*"jdbc:mysql://172.16.30.120:3306/NewsDB"*/;	//数据库连接地址
	private static final String USER = "root";
	private static final String PASSWORD = "Qiunan19960304";

	/**
	 * 构造函数
	 */
	private NewsDAO() {
		//加载数据库驱动
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.out.println("加载数据库驱动失败");
			e.printStackTrace();
		}
	}

	/**
	 * 使用单例模式，获取NewsDAO实例
	 * @return
	 */
	public static NewsDAO newInstance() {
		if (instance == null) {
			//创建DAO对象
			instance = new NewsDAO();
		}
		return instance;
	}

	/**
	 * 发布一则新闻
	 * @param news 新闻
	 * @return int 新闻编号
	 */
	public int insertNews(NewsBean news) {
		int id = -1;	//新闻编号
		Connection connection = null;	//数据库连接
		PreparedStatement pstmt = null;	//SQL语句
		try {
			//获取数据库连接
			connection = (Connection) DriverManager.getConnection(DB_URL, USER, PASSWORD);
			//向数据库添加新闻信息
			//创建SQL语句
			pstmt = connection.prepareStatement("insert into news(title, icon, content, time, type, publisher)" +
					" values(?, ?, ?, ?, ?, ?);");
			pstmt.setString(1, news.getTitle());
			pstmt.setString(2, news.getIcon());
			pstmt.setString(3, news.getContent());
			pstmt.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
			pstmt.setString(5, news.getType());
			pstmt.setString(6, news.getPublisher());
			//执行SQL语句
			pstmt.execute();
			//获取插入新闻的id编号
			pstmt = connection.prepareStatement("select last_insert_id();");
			ResultSet result = pstmt.executeQuery();
			if (result.next()) {
				id = result.getInt(1);
			}
			result.close();
		} catch (SQLException e) {
			System.out.println("数据库连接失败");
			e.printStackTrace();
		} finally {
			//关闭连接
			try {
				if (pstmt != null)
					pstmt.close();
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return id;
	}


	/**
	 * 查找指定编号的新闻数据
	 * @param userId 用户编号
	 * @param newsId 新闻编号
	 * @return NewsBean
	 */
	public NewsBean queryNews(int userId, int newsId) {
		NewsBean news = null;	//结果
		Connection connection = null;	//数据库连接
		PreparedStatement pstmt = null;	//SQL语句
		ResultSet result = null;	//查询结果
		try {
			//连接数据库
			connection = (Connection) DriverManager.getConnection(DB_URL, USER, PASSWORD);
			//构造SQL语句
			pstmt = connection.prepareStatement("select title, content, time, publisher" +
					" from news where id = ?");
			pstmt.setInt(1, newsId);
			//执行SQL语句
			result = pstmt.executeQuery();
			//1.获取新闻数据
			while (result.next()) {
				news = new NewsBean();
				//获取标题和内容
				news.setTitle(result.getString("title"));
				news.setContent(result.getString("content"));
				//获取发布时间
				news.setPublishTime(timeDF.format(result.getTimestamp("time")));
				//获取新闻作者
				news.setPublisher(result.getString("publisher"));
			}
			//2.添加新闻阅读记录
			if (isHistoryExist(userId, newsId)) {
				//记录存在，更新时间
				pstmt = connection.prepareStatement("update history set time = ? where " +
						"user_id = ? and news_id = ?;");
				pstmt.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
				pstmt.setInt(2, userId);
				pstmt.setInt(3, newsId);
				pstmt.executeUpdate();
			} else {
				//添加阅读记录
				pstmt = connection.prepareStatement("insert into history(user_id, news_id, time)" +
						" values(?, ?, ?);");
				pstmt.setInt(1, userId);
				pstmt.setInt(2, newsId);
				pstmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
				pstmt.execute();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("数据库连接失败");
		} finally {
			//关闭数据库连接
			try {
				if (result != null)
					result.close();
				if (pstmt != null)
					pstmt.close();
				if (connection != null)
					connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
				System.out.println("关闭数据库连接失败");
			}
		}
		//返回结果
		return news;
	}
	
	/**
	 * 判断用户是否已经阅读过这条新闻
	 * @param userId 用户编号
	 * @param newsId 新闻编号
	 * @return boolean
	 */
	private boolean isHistoryExist(int userId, int newsId) {
		boolean exist = false;	//结果
		Connection connection = null;   //连接
		PreparedStatement pstmt = null; //查询SQL语句
		ResultSet resultSet = null; //查询结果
		try {
			//连接到数据库
			connection = (Connection) DriverManager.getConnection(DB_URL, USER, PASSWORD);
			//构造查询语句
			pstmt = connection.prepareStatement("select news_id from history" +
					" where user_id = ? and news_id = ?;");
			pstmt.setInt(1, userId);
			pstmt.setInt(2, newsId);
			//执行查询语句
			resultSet = pstmt.executeQuery();
			if (resultSet.next()) {
				exist = true;	//阅读记录存在
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("数据库连接失败");
		} finally {
			try {
				//关闭资源
				if (resultSet != null)
					resultSet.close();
				if (pstmt != null)
					pstmt.close();
				if (connection != null)
					connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
				System.out.println("关闭资源失败");
			}
		}
		//返回结果
		return exist;
	}

	/**
	 * 查询指定类型的新闻信息数据
	 * @param type 新闻类型
	 * @param lastTime 新闻数据的最新时间
	 * @return List<NewsBean> 新闻信息
	 */
	public List<NewsBean> queryNewsData(String type, String lastTime){
		List<NewsBean> newsData = new ArrayList<NewsBean>(20);  //新闻数据
		Connection connection = null;   //连接
		PreparedStatement pstmt = null; //查询SQL语句
		ResultSet resultSet = null; //查询结果
		try {
			//连接到数据库
			connection = (Connection) DriverManager.getConnection(DB_URL, USER, PASSWORD);
			//构造查询语句
			if (lastTime != null) {
				pstmt = connection.prepareStatement("select id, title, icon, time, publisher" +
						" from news where type = ? and time > '" + lastTime + "'" +
						" order by time desc;");
			} else {
				pstmt = connection.prepareStatement("select id, title, icon, time, publisher" +
						" from news where type = ?" +
						" order by time desc;");
			}
			pstmt.setString(1, type);
			//执行查询语句
			resultSet = pstmt.executeQuery();
			int count = 0;	//计数
			//封装查询结果
			while(resultSet.next() && count < 20) {
				//创建NewsBean对象
				NewsBean news = new NewsBean();
				//设置所需的属性
				news.setTitle(resultSet.getString("title"));
				news.setId(resultSet.getInt("id"));
				news.setIcon(resultSet.getString("icon"));
				news.setType(type);
				news.setPublishTime(timeDF.format(resultSet.getTimestamp("time")));
				//获取评论次数
				news.setContentsCount(0);
				//获取新闻作者
				news.setPublisher(resultSet.getString("publisher"));
				//加入到集合
				newsData.add(news);
				//计数加一
				count++;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("数据库连接失败");
		} finally {
			try {
				//关闭资源
				if (resultSet != null)
					resultSet.close();
				if (pstmt != null)
					pstmt.close();
				if (connection != null)
					connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
				System.out.println("关闭资源失败");
			}
		}
		//返回数据
		return newsData;
	}
	
	/**
	 * 搜索相应的新闻
	 * @param searchText 搜索内容
	 * @return List<NewsBean>
	 */
	public List<NewsBean> searchNewsData(String searchText) {
		List<NewsBean> newsData = new ArrayList<NewsBean>(10);  //新闻数据
		Connection connection = null;   //连接
		PreparedStatement pstmt = null; //查询SQL语句
		ResultSet resultSet = null; //查询结果
		try {
			//连接到数据库
			connection = (Connection) DriverManager.getConnection(DB_URL, USER, PASSWORD);
			//构造查询语句
			pstmt = connection.prepareStatement("select id, title, icon, type, publisher" +
					" from news where title like '%" + searchText + "%'" +
					" order by time desc;");
			//执行查询语句
			resultSet = pstmt.executeQuery();
			//封装查询结果
			while(resultSet.next()) {
				//创建NewsBean对象
				NewsBean news = new NewsBean();
				//设置所需的属性
				news.setTitle(resultSet.getString("title"));
				news.setId(resultSet.getInt("id"));
				news.setIcon(resultSet.getString("icon"));
				news.setType(resultSet.getString("type"));
				//获取评论次数
				news.setContentsCount(0);
				//获取新闻作者
				news.setPublisher(resultSet.getString("publisher"));
				//加入到集合
				newsData.add(news);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("数据库连接失败");
		} finally {
			try {
				//关闭资源
				if (resultSet != null)
					resultSet.close();
				if (pstmt != null)
					pstmt.close();
				if (connection != null)
					connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
				System.out.println("关闭资源失败");
			}
		}
		//返回数据
		return newsData;
	}

	/**
	 * 查询所有新闻类型
	 * @return List<String> 新闻类型
	 */
	public List<String> queryNewsTypes() {
		List<String> types = new ArrayList<String>(); //新闻类型集合
		Connection connection = null;   //连接
		PreparedStatement pstmt = null; //查询SQL语句
		ResultSet resultSet = null; //查询结果
		try {
			//连接到数据库
			connection = (Connection) DriverManager.getConnection(DB_URL, USER, PASSWORD);
			//构造查询语句
			pstmt = connection.prepareStatement("select type from news_type;");
			//执行查询语句
			resultSet = pstmt.executeQuery();
			//封装查询结果
			while(resultSet.next()) {
				//获取新闻类型
				String type = resultSet.getString("type");
				types.add(type);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("数据库连接失败");
		} finally {
			try {
				//关闭资源
				if (resultSet != null)
					resultSet.close();
				if (pstmt != null)
					pstmt.close();
				if (connection != null)
					connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
				System.out.println("关闭资源失败");
			}
		}
		//返回数据
		return types;
	}

	/**
	 * 获取用户的密码
	 * @return
	 */
	public String getUserPassword(String name) {
		String password = null;	//用户密码
		Connection connection = null;	//数据库连接
		ResultSet result = null;	//查询结果
		PreparedStatement pstmt = null;	//SQL语句
		try {
			//获取连接
			connection = (Connection) DriverManager.getConnection(DB_URL, USER, PASSWORD);
			//创建SQL语句
			pstmt = connection.prepareStatement("select password from user where name = ?;");
			pstmt.setString(1, name);
			//执行SQL语句
			result = pstmt.executeQuery();
			//获取密码
			if (result.next()) {
				password = result.getString("password");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			//关闭连接
			try {
				if (result != null) 
					result.close();
				if (pstmt != null)
					pstmt.close();
				if (connection != null) 
					connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return password;
	}

	/**
	 * 获取发布者的密码
	 * @return
	 */
	public String getPublisherPassword(String name) {
		String password = null;	//用户密码
		Connection connection = null;	//数据库连接
		ResultSet result = null;	//查询结果
		PreparedStatement pstmt = null;	//SQL语句
		try {
			//获取连接
			connection = (Connection) DriverManager.getConnection(DB_URL, USER, PASSWORD);
			//创建SQL语句
			pstmt = connection.prepareStatement("select password from publisher where name = ?;");
			pstmt.setString(1, name);
			//执行SQL语句
			result = pstmt.executeQuery();
			//获取密码
			if (result.next()) {
				password = result.getString("password");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			//关闭连接
			try {
				if (result != null) 
					result.close();
				if (pstmt != null)
					pstmt.close();
				if (connection != null) 
					connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return password;
	}

	/**
	 * 获取记录（记录可以是历史记录，也可以是收藏记录）
	 * @param useId 用户id
	 * @param type 记录类型
	 * @return List<RecordBean>
	 */
	public List<RecordBean> queryRecord(int useId, String type) {
		List<RecordBean> records = new ArrayList<RecordBean>();	//记录
		Connection connection = null;	//数据库连接
		PreparedStatement pstmt = null;	//SQL语句
		ResultSet result = null;	//查询结果
		try {
			///获取连接
			connection = (Connection) DriverManager.getConnection(DB_URL, USER, PASSWORD);
			//根据不同的记录类型创建SQL语句
			if ("history".equals(type)) {
				pstmt = connection.prepareStatement("select title, history.time, news_id " +
						"from news, history where news_id = id and user_id = ? " +
						"order by time desc;");
				pstmt.setInt(1, useId);
			} else if ("collection".equals(type)) {
				pstmt = connection.prepareStatement("select title, collection.time, news_id " +
						"from news, collection where news_id = id and user_id = ? " +
						"order by time desc");
				pstmt.setInt(1, useId);
			}
			//执行查询
			if (pstmt != null) {
				result = pstmt.executeQuery();
				//封装结果
				while (result.next()) {
					RecordBean record = new RecordBean();
					record.setTitle(result.getString("title"));
					record.setTime(dateDF.format(result.getTimestamp("time")));
					record.setNewsId(result.getInt("news_id"));
					record.setUserId(useId);
					records.add(record);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("数据库连接失败");
		} finally {
			//关闭连接
			try {
				if (result != null) 
					result.close();
				if (pstmt != null)
					pstmt.close();
				if (connection != null) 
					connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		//返回结果
		return records;
	}
	
	/**
	 * 删除记录
	 * @param userId 用户编号
	 * @param newsIdList 新闻编号
	 */
	public void deleteRecord(int userId, List<Integer> newsIdList, String type) {
		Connection connection = null;	//数据库连接
		PreparedStatement pstmt = null;	//SQL语句
		ResultSet result = null;	//查询结果
		try {
			///获取连接
			connection = (Connection) DriverManager.getConnection(DB_URL, USER, PASSWORD);
			//根据不同的记录类型创建SQL语句
			if ("history".equals(type)) {
				pstmt = connection.prepareStatement("delete from history" +
						" where user_id = ? and news_id = ?;");
			} else if ("collection".equals(type)) {
				pstmt = connection.prepareStatement("delete from collection" +
						" where user_id = ? and news_id = ?;");
			}
			//设置参数
			pstmt.setInt(1, userId);
			for (int i = 0; i < newsIdList.size(); i++) {
				pstmt.setInt(2, newsIdList.get(i));
				//执行删除
				pstmt.execute();
			}	
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("数据库连接失败");
		} finally {
			//关闭连接
			try {
				if (result != null) 
					result.close();
				if (pstmt != null)
					pstmt.close();
				if (connection != null) 
					connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 获取收藏情况
	 * @param userId 用户编号
	 * @param newsId 新闻编号
	 * @return boolean
	 */
	public boolean getCollectCondition(int userId, int newsId) {
		boolean collected = false;	//收藏情况
		Connection connection = null;	//数据库连接
		PreparedStatement pstmt = null;	//SQL语句
		ResultSet result = null;	//查询结果
		try {
			///获取连接
			connection = (Connection) DriverManager.getConnection(DB_URL, USER, PASSWORD);
			//SQL语句
			pstmt = connection.prepareStatement("select user_id from collection" +
					" where user_id = ? and news_id = ?;");
			pstmt.setInt(1, userId);
			pstmt.setInt(2, newsId);
			//执行查询
			result = pstmt.executeQuery();
			if (result.next()) {
				collected = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("数据库连接失败");
		} finally {
			//关闭连接
			try {
				if (result != null) 
					result.close();
				if (pstmt != null)
					pstmt.close();
				if (connection != null) 
					connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		//返回结果
		return collected;
	}
	
	/**
	 * 添加收藏新闻
	 * @param userId 用户编号
	 * @param newsId 新闻编号
	 */
	public void insertCollectedNews(int userId, int newsId) {
		Connection connection = null;	//数据库连接
		PreparedStatement pstmt = null;	//SQL语句
		try {
			///获取连接
			connection = (Connection) DriverManager.getConnection(DB_URL, USER, PASSWORD);
			//SQL语句
			pstmt = connection.prepareStatement("insert into collection values(?, ?, ?);");
			pstmt.setInt(1, userId);
			pstmt.setInt(2, newsId);
			pstmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
			//执行添加
			pstmt.execute();
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("数据库连接失败");
		} finally {
			//关闭连接
			try {
				if (pstmt != null)
					pstmt.close();
				if (connection != null) 
					connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 删除已收藏新闻
	 * @param userId 用户编号
	 * @param newsId 新闻编号
	 */
	public void deleteCollectedNews(int userId, int newsId) {
		Connection connection = null;	//数据库连接
		PreparedStatement pstmt = null;	//SQL语句
		try {
			///获取连接
			connection = (Connection) DriverManager.getConnection(DB_URL, USER, PASSWORD);
			//SQL语句
			pstmt = connection.prepareStatement("delete from collection" +
					" where user_id = ? and news_id = ?;");
			pstmt.setInt(1, userId);
			pstmt.setInt(2, newsId);
			//执行添加
			pstmt.execute();
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("数据库连接失败");
		} finally {
			//关闭连接
			try {
				if (pstmt != null)
					pstmt.close();
				if (connection != null) 
					connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		NewsDAO dao = NewsDAO.newInstance();
		List<NewsBean> newsBeans = dao.queryNewsData("国内", null);
		for (int i = 0; i < newsBeans.size(); i++) {
			System.out.println(newsBeans.get(i).getTitle());
			System.out.println(newsBeans.get(i).getPublishTime());
		}
		
		System.out.println();
		
		newsBeans = dao.queryNewsData("国内", newsBeans.get(0).getPublishTime());
		for (int i = 0; i < newsBeans.size(); i++) {
			System.out.println(newsBeans.get(i).getTitle());
			System.out.println(newsBeans.get(i).getPublishTime());
		}
	}

}

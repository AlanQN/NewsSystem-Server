package dao;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import model.RecordBean;

public class TestTime {

	private static final DateFormat df = DateFormat.getDateInstance();
	
	public static void main(String[] args){
		NewsDAO dao = NewsDAO.newInstance();
		List<RecordBean> records = dao.queryRecord(1111, "history");
		List<RecordBean> data = processData(records);
		for (int i = 0; i < data.size(); i++) {
			System.out.println("title = " + data.get(i).getTitle());
			System.out.println("time = " + data.get(i).getTime());
			System.out.println("count = " + data.get(i).getCount());
		}
	}

	/**
	 * 对记录数据进行加工，获取需要的数据集合
	 * @param records 记录数据
	 * @return List<RecordBean>
	 */
	private static List<RecordBean> processData(List<RecordBean> records) {
		List<RecordBean> data = new ArrayList<RecordBean>();  //加工后的数据集合
		Date currentDate = getCurrentDate(); //当前时间
		if (records != null) {
			for (int i = 0; i < records.size(); i++) {
				RecordBean time = new RecordBean(); //时间戳
				RecordBean bean = records.get(i);   //当前记录
				data.add(time); //时间戳加入到集合中
				int count = 0;  //记录个数
				int diffDays = 0;   //相差天数
				//计算时间差
				try {
					Date date = df.parse(bean.getTime());
					diffDays = differentDays(date, currentDate);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				//获取与当前记录同一天的记录总数
				while (i < records.size()) {
					if (bean.getTime().equals(records.get(i).getTime())) {
						//日期相同，加入到集合中，个数加一
						data.add(records.get(i));
						count++;
						i++;
					} else {
						//日期不同，退出，进行下一轮操作
						i--;
						break;
					}
				}
				//设置属性
				time.setCount(count);
				if (diffDays == 0) {
					time.setTime("今天");
				} else if (diffDays == 1) {
					time.setTime("昨天");
				} else {
					time.setTime(records.get(i-1).getTime());
				}
			}
		}
		/*for (int i = 0; i < data.size(); i++) {
            Log.d("debug", "processData: time = " + data.get(i).getTime());
            Log.d("debug", "processData: count = " + data.get(i).getCount());
        }*/
		return data;
	}

	/**
	 * 获取当前日期时间
	 * @return Date
	 */
	private static Date getCurrentDate() {
		Date date = null; //当前日期
		// 获取时间
		try {
			date = df.parse(df.format(new Date()));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		//返回结果
		return date;
	}

	/**
	 * date2比date1多的天数
	 * @param date1    
	 * @param date2
	 * @return    
	 */
	public static int differentDays(Date date1, Date date2)
	{
		Calendar cal1 = Calendar.getInstance();
		cal1.setTime(date1);

		Calendar cal2 = Calendar.getInstance();
		cal2.setTime(date2);
		int day1= cal1.get(Calendar.DAY_OF_YEAR);
		int day2 = cal2.get(Calendar.DAY_OF_YEAR);

		int year1 = cal1.get(Calendar.YEAR);
		int year2 = cal2.get(Calendar.YEAR);
		if(year1 != year2)   //同一年
		{
			int timeDistance = 0 ;
			for(int i = year1 ; i < year2 ; i ++)
			{
				if(i%4==0 && i%100!=0 || i%400==0)    //闰年            
				{
					timeDistance += 366;
				}
				else    //不是闰年
				{
					timeDistance += 365;
				}
			}

			return timeDistance + (day2-day1) ;
		}
		else    //不同年
		{
			return day2-day1;
		}
	}

}

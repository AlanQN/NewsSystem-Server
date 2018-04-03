package util;

public class TextUtils {
	
	/**
	 * 判断文本是否为空
	 * @param text 文本
	 * @return boolean
	 */
	public static boolean isEmpty(String text) {
		if (text == null || text.trim().isEmpty()) {
			return true;
		}
		return false;
	}

}

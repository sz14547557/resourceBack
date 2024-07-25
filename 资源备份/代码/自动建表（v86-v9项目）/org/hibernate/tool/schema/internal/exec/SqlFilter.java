package org.hibernate.tool.schema.internal.exec;

import java.util.regex.Pattern;

public class SqlFilter {
	//阻止删表，添加外键
	private static final Pattern PATTERN=Pattern.compile(".*(drop|truncate|foreign).*",Pattern.CASE_INSENSITIVE);
	public static boolean doFilter(String cmd) {
		if(PATTERN.matcher(cmd).matches()) {
			return true;
		}
		return false;
	}
}

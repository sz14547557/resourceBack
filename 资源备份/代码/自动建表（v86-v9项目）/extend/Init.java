package com.eplugger.extend;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@ConfigurationProperties("spring.datasource.primary")
@Order(Ordered.HIGHEST_PRECEDENCE)
@Getter
@Setter
public class Init {

	private String driverClassName;
	private String url;
	private String username;
	private String password;
	private String validationQuery;

	// 数据库连接对象
	private Connection connection = null;
	// 执行sql对象
	private Statement statement = null;

	/**
	 * 释放连接
	 */
	private void releaseConnection() {
		if (statement != null) {
			try {
				statement.close();
			} catch (Throwable ignored) {
				log.error("数据库连接关闭异常");
			}
		}
		if (connection != null) {
			try {
				connection.close();
			} catch (Throwable ignored) {
				log.error("数据库连接关闭异常");
			}
		}
	}

	/**
	 * 决定是否执行初始化数据库操作 通过查询SYS_PARAM判断 如果没有该表则认为需要初始化数据库
	 */
	public boolean determineWhether2Initialize() {
		try {
			getConnection();
			statement.execute("select 1 from SYS_PARAM");
		} catch (SQLException throwable) {
			return true;
		} finally {
			releaseConnection();
		}
		return false;

	}

	/**
	 * 连接数据库
	 */
	@SneakyThrows
	public void getConnection() {
		Class.forName(getDriverClassName());
		connection = DriverManager.getConnection(getUrl(), getUsername(), getPassword());
		statement = connection.createStatement();
	}
}

package com.DBTool;

import java.sql.*;

public class DBUtil {
	private static String driverClass="com.mysql.jdbc.Driver";

	private static Connection conn;
	
	//加载驱动
	static{
		try{
			Class.forName(driverClass);
		}
		catch(ClassNotFoundException e){
			e.printStackTrace();
		}
	}
	
	//连接checkin数据库
	public static Connection getConnection(){
		try{
			conn=DriverManager.getConnection("jdbc:mysql://localhost:3306/checkin?user=root&password=123456&useUnicode=true&characterEncoding=utf8&autoReconnect=true&failOverReadOnly=false");
		}
		catch(SQLException e){
			e.printStackTrace();
		}
		return conn;
	}
	
	//打印连接结果
	public static void main(String[] args){
		Connection conn=DBUtil.getConnection();
		if(conn!=null){
			System.out.println("success!");
		}
		else{
			System.out.println("error!");
		}
	}
	
	//关闭数据库连接
	public static void Close(){
		if(conn!=null){
			try{
				conn.close();
			}
			catch(SQLException e){
				e.printStackTrace();
			}
		}
	}
}

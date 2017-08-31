package com.Servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.DBTool.DBUtil;
import java.math.*;

/**
 * Servlet implementation class Login
 */
@WebServlet("/Signup")
public class Signup extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public static String ID = "";
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Signup() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String ID = request.getParameter("ID"); 
        String PW= request.getParameter("PW");
        System.out.println("UserName = " + ID);
        System.out.println("PassWord = " + PW);
        boolean type=false;
        boolean done=true;
        
        String IMEI="";
        BigInteger imei=BigInteger.ZERO;
        int usertype=0;
        int id=0;
        
        String[] represent_ID = {"2014301500036","2014301500313","2014301500296"};//课代表账号列表
        List<String> represent_list = Arrays.asList(represent_ID);

        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();
        try
        {
        	Connection con=DBUtil.getConnection();
        	Statement stmt=con.createStatement();
        	
        	String query = "select * from UserInfo where UserName='"+ID+"'";
        	ResultSet rs = stmt.executeQuery(query);
        	if(rs != null) {
        		while(rs.next())
        		{
        			done=false;//这个账号已经注册过了
        			type=false;
        		}
        	}
        	if(done) 
        	{
        		String query0 = "select MAX(IMEI) from UserInfo";
        		ResultSet rs0=stmt.executeQuery(query0);
        		if(!rs0.wasNull())
        		{
        			while(rs0.next())
            		{
            			IMEI = rs0.getString(1);
            			if(IMEI==null)
                			IMEI = "868753027077440";
            			BigInteger im=new BigInteger(IMEI);
            			BigInteger i = BigInteger.ONE;
            			imei=im.add(i);       			
            		}
    		    
            		String query1 = "select MAX(ID) from UserInfo";
            		ResultSet rs1=stmt.executeQuery(query1);
            		while(rs1.next())
            		{
            			id=rs1.getInt(1)+1;
            			int length = ID.length();
            			if(length==13)//学生账号
            			{
            				
            				if(represent_list.contains(ID))
            					usertype = 2;//是课代表
            				else
            					usertype = 1;//普通学生
            			}
            			else//教师或者辅导员账号
            			{
            				String i = ID.substring(0, 4);
            				if((i.equals("0000"))||(i.equals("0200")))
            					usertype = 4;//辅导员
            				else
            					usertype = 3;//任课教师
            			}
            			
            		}
    		    
            		String sql="insert into UserInfo set ID="+String.valueOf(id)+",UserName='"+ID+"',PassWord='"+PW+"',IMEI="+imei.toString()+",UserType="+String.valueOf(usertype);
            		int rs2=stmt.executeUpdate(sql);
            		if(rs2 != 0)	    					
            			type = true;
            		else
            			type = false;
            	}
        	}   		
        }
        catch(Exception ex)
        {
        	ex.printStackTrace();
        }
        finally
        {
        	DBUtil.Close();
        	out.print(done+"."+String.valueOf(usertype)+"."+ type);
        	out.flush();
        	out.close();
        }
	}

}

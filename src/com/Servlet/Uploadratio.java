package com.Servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.DBTool.DBUtil;


/**
 * Servlet implementation class Login
 */
@WebServlet("/Uploadratio")
public class Uploadratio extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public static String ID = "";
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Uploadratio() {
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
		String CourseNum = request.getParameter("CourseNum"); 
        String Percentage = request.getParameter("Percentage");
        
        boolean type=true;

        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();
        try
        {
        	Connection con=DBUtil.getConnection();
        	Statement stmt=con.createStatement();
        	
        	String query = "select * from GradePercentage where CourseNum ='"+CourseNum+"'";
        	ResultSet rs = stmt.executeQuery(query);        	
        	while(rs.next())
        	{       			
        		type=false;//已经上传过
        	}
        	
        	if(type)//还没有上传过该课程的考勤成绩占比
        	{
        		String sql = "insert into GradePercentage set CourseNum='"+CourseNum+"',Percentage="+Percentage;
        		int rs1 = stmt.executeUpdate(sql);
        		if(rs1==0)
        			type=false;
        	}    	
        }
        catch(Exception ex)
        {
        	ex.printStackTrace();
        }
        finally
        {
        	DBUtil.Close();
        	out.print(type);
        	out.flush();
        	out.close();
        }
	}

}

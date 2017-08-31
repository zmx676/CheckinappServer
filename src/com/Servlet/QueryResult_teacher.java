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
@WebServlet("/QueryResult_teacher")
public class QueryResult_teacher extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public static String ID = "";
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public QueryResult_teacher() {
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
		
        String CourseNum= request.getParameter("CourseNum");
        String Time = request.getParameter("Time");
        String time = Time+"%";
        
        boolean type=false;
        String InResult="";
        String OutResult="";
        String ID="";
        String ret="";//最终要返回的结果

        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();
        try
        {
        	Connection con=DBUtil.getConnection();
        	Statement stmt=con.createStatement();
			String sql="select * from Attendanceinfo where CourseNum='"+CourseNum+"' and InTime like'"+time+"'";
			ResultSet rs=stmt.executeQuery(sql);
		    while(rs.next())
		    {
		    	InResult = rs.getString("InResult");
		    	OutResult = rs.getString("OutResult");
		    	ID = rs.getString("StudentID");
		    	ret = ret + ID + "@" + InResult + "@" + OutResult + "-";
		    	type=true;
		    }
        }
        catch(Exception ex)
        {
        	ex.printStackTrace();
        }
        finally
        {
        	DBUtil.Close();
        	out.print(type+"."+ret);
        	out.flush();
        	out.close();
        }
	}

}

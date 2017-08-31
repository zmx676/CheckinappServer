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
@WebServlet("/DownloadAppealList")
public class DownloadAppealList extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public static String ID = "";
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DownloadAppealList() {
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
		String CourseTime = request.getParameter("CourseTime"); 
        
        boolean type = false;
        String ret = "";//要返回的list

        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();
        try
        {
        	Connection con=DBUtil.getConnection();
        	Statement stmt=con.createStatement();
			String sql="select * from Appeal where CourseTime='"+CourseTime+"'";
			ResultSet rs=stmt.executeQuery(sql);
		    while(rs.next())
		    {
		    	String CourseNum = rs.getString(1);
		    	String StudentID = rs.getString(3);
		    	ret = ret + StudentID + "@" + CourseNum + "-";
		    	type = true;
		    }
        }
        catch(Exception ex)
        {
        	ex.printStackTrace();
        }
        finally
        {
        	DBUtil.Close();
        	out.print(type+"."+ ret);
        	out.flush();
        	out.close();
        }
	}

}

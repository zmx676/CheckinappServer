package com.Servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.DBTool.DBUtil;
import com.mysql.jdbc.Statement;

@WebServlet("/DownloadProof")
public class DownloadProof extends HttpServlet {

	private static final long serialVersionUID = 1L;
		
	/**
     * @see HttpServlet#HttpServlet()
     */
    public DownloadProof() {
        super();
        // TODO Auto-generated constructor stub
    }

	public void doGet(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	@SuppressWarnings("deprecation")
	public void doPost(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
		
		String CourseTime = request.getParameter("CourseTime");
		String StudentID = request.getParameter("StudentID"); 
		String CourseNum = request.getParameter("CourseNum");
		String AdminID = request.getParameter("AdminID");
		
		boolean type = false;
		String path = "";
		
		request.setCharacterEncoding("utf-8");  //设置编码  
		PrintWriter out = response.getWriter();
		
        try 
        {  
        	
             Connection con = DBUtil.getConnection();
             Statement stmt = (Statement)con.createStatement();
             
             String sql = "select filename from Appeal where CourseTime='"+CourseTime+"' and StudentID = '"+StudentID+"' and CourseNum='"+CourseNum+"'";
             ResultSet rs=stmt.executeQuery(sql);
 		     while(rs.next())
 		     {
 		    	 path = rs.getString(1);
 		    	 
 		    	 Statement stmt1 = (Statement)con.createStatement();
 		    	 String sql1 = "update Appeal set AdminID='"+AdminID+"' where CourseTime='"+CourseTime+"' and StudentID = '"+StudentID+"' and CourseNum='"+CourseNum+"'";
 		    	 int rs1 = stmt1.executeUpdate(sql1);
 		    	 if(rs1!=0)
 		    	     type = true;
 		    	 else
 		    		 type = false;
 		     }
        }                   	
        catch(Exception ex)
        {
             type = false;
             ex.printStackTrace();
        }
        finally
        {
             DBUtil.Close();
             out.print(type+" "+path);
             out.flush();
             out.close();
        }
    }
	
}
            



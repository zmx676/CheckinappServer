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
 * Servlet implementation class Checkin
 */
@WebServlet("/Checkin")
public class Checkin extends HttpServlet {
	private static final long serialVersionUID = 1L;
    //public static String ID = "";
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Checkin() {
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

		String Longitude = request.getParameter("Longitude"); 
		String Latitude = request.getParameter("Latitude");
        String ID =  request.getParameter("ID");
        String CourseNum = request.getParameter("CourseNum");
        String InTime = request.getParameter("InTime");
        
        String[] name = InTime.split(" ");
        String time = name[0]+"%";
        
        boolean type = false;
        boolean start = true;

        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();
        try
        {
        	Connection con=DBUtil.getConnection();
        	Statement stmt=con.createStatement();
        	
        	if(ID.length()==8)//教师开启签到
        	{
        		String query = "select * from AttendanceInfo where CourseNum='"+CourseNum+"' and InTime like'"+time+"'";
            	ResultSet rs0 = stmt.executeQuery(query);
            	while(rs0.next())
            	{
            		if(rs0.getString(1).length()==8)//当天已经有老师对这门课开启过签到
            		{
            			start = false;
            			break;
            		}
            	}
            	
            	if(start)//教师1st次开启签到
            	{
            		OutlierNodeDetect detector_checkin = new OutlierNodeDetect();
            		OutlierNodeDetect.CourseNum = CourseNum;
            		OutlierNodeDetect.Time = time;
            		OutlierNodeDetect.intime = InTime;
            		detector_checkin.start();//开启线程
            	} 
        	}
        	 
        	      	
			String sql="insert into AttendanceInfo set CourseNum='"+CourseNum+"',StudentID='"+ID+"',InTime='"+InTime+"',InLongitude="+Longitude+",InLatitude="+Latitude;
			int rs = stmt.executeUpdate(sql);
			if(rs != 0)	    	
				type = true;
			else
				type = false;
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

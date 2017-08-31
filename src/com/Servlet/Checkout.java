package com.Servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.Date;
import java.sql.ResultSet;
//import java.sql.ResultSet;
import java.sql.Statement;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.DBTool.DBUtil;

/**
 * Servlet implementation class Checkin
 */
@WebServlet("/Checkout")
public class Checkout extends HttpServlet {
	private static final long serialVersionUID = 1L;
    //public static String ID = "";
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Checkout() {
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
        String OutTime = request.getParameter("OutTime");
        String CourseNum = request.getParameter("CourseNum");
        
        String[] names = OutTime.split(" ");
        String date = names[0];//exact date for signing out
        String time = date+"%";
        String intime="";
        
        boolean type=false;
        boolean start = true;

        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();
        try
        {
        	Connection con=DBUtil.getConnection();
        	Statement stmt=con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
        	
        	if(ID.length()==8)//教师开启签退
        	{
        		String query = "select * from AttendanceInfo where CourseNum='"+CourseNum+"' and OutTime like'"+time+"'";
            	ResultSet rs0 = stmt.executeQuery(query);
            	while(rs0.next())
            	{
            		if(rs0.getString(1).length()==8)//当天已经有老师对这门课开启过签退
            		{
            			start = false;
            			break;
            		}
            	}  
            	
            	if(start)//教师1st次开启签退
            	{
            		OutlierNodeDetect_Out detector_checkin = new OutlierNodeDetect_Out();
            		OutlierNodeDetect_Out.CourseNum = CourseNum;
            		OutlierNodeDetect_Out.Time = time;
            		OutlierNodeDetect_Out.outtime = OutTime;
            		detector_checkin.start();//开启线程
            	}
        	}  
        	
        	String query = "select * from AttendanceInfo where CourseNum='"+CourseNum+"' and StudentID='"+ID+"' and InTime like'"+time+"'";//查找当天的签到记录
        	ResultSet rs0 = stmt.executeQuery(query);
        	
        	SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        	ParsePosition pos = new ParsePosition(0);
        	Date d1 = (Date) sd.parse(OutTime, pos);
        	while(rs0.next())
		    { 
        		intime = rs0.getString(4);//取出签到时间
        		
        		SimpleDateFormat sd1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            	ParsePosition pos1 = new ParsePosition(0);
        		Date d2 = (Date) sd1.parse(intime, pos1);
        		
        		long interval = d1.getTime() - d2.getTime();// 得出的时间间隔是毫秒  
        		int min_interval = (int) ((interval%3600000)/60000);//得出的时间间隔的单位是分钟 
        		if((min_interval>=0)&&(min_interval<=155))//要求签退在签到提交至少0min后，且不能超过155min
        		{
        			type=true;
        			break;
        		}
		    }
        	if(type)//已经签到过
        	{
        		String sql = "UPDATE AttendanceInfo SET OutTime='"+OutTime+"',OutLongitude="+Longitude+",OutLatitude="+Latitude+" where StudentID='"+ID+"' and InTime='"+intime+"'";
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

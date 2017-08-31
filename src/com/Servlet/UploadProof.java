package com.Servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.DBTool.DBUtil;

@WebServlet("/UploadProof")
public class UploadProof extends HttpServlet {

	private static final long serialVersionUID = 1L;
		
	/**
     * @see HttpServlet#HttpServlet()
     */
    public UploadProof() {
        super();
        // TODO Auto-generated constructor stub
    }

	public void doGet(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	@SuppressWarnings("deprecation")
	public void doPost(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");  //设置编码  
		PrintWriter out = response.getWriter();
		//获得磁盘文件条目工厂 
        DiskFileItemFactory factory = new DiskFileItemFactory();  
        //获取文件需要上传到的路径
        String path = request.getRealPath("/proof");  
        File file=new File(path);
        if(!file.exists()){
        	file.mkdirs();
        }
        factory.setRepository(new File(path));  
        //设置 缓存的大小 
        factory.setSizeThreshold(1024*1024) ;  
        //文件上传处理  
        ServletFileUpload upload = new ServletFileUpload(factory);  
        try {  
        	//可以上传多个文件   
            List<FileItem> list = (List<FileItem>)upload.parseRequest(request);  
            for(FileItem item : list){  
            	//获取属性名字  
                String name = item.getFieldName();  
                //如果获取的 表单信息是普通的 文本 信息  
                if(item.isFormField()){                     
                	//获取用户具体输入的字符串,因为表单提交过来的是字符串类型的  
                    String value = item.getString() ;  
                    request.setAttribute(name, value);  
                }
                else{  
                	//获取路径名  
                    String value = item.getName() ;  
                    //索引到最后一个反斜杠  
                    int start = value.lastIndexOf("\\");  
                    //截取 上传文件的 字符串名字，加1是 去掉反斜杠 
                    String init = value.substring(start+1);  
                    String[] name1 = init.split("@");
                    String filename = name1[0];//文件名，非路径
                    String ID = name1[1];//当前用户的ID
                    String CourseNum = name1[2];//用户输入的课程号
                    String uploadtime = name1[3];//上传时间
                    String CourseTime = name1[4];//要申诉的课程时间
                    
                    request.setAttribute(name, filename);  
                    //写到磁盘上  
                    item.write( new File(path,filename) );//第三方提供的 
                    System.out.println("上传成功："+filename);
                    
                    
                    /*response.getWriter().print(filename);//将路径返回给客户端*/
                    boolean type=false;
                    String i = path+"\\"+filename;
                    File filecur = new File(i);
                    FileInputStream fis = new FileInputStream(filecur);

                    try
                    {
                    	Connection con=DBUtil.getConnection();
                    	//Statement stmt=con.createStatement();
                    	
                    	java.sql.PreparedStatement ps = con.prepareStatement("insert into Appeal values (?,?,?,?,?,?,?,?)");
                    	ps.setString(1, CourseNum);
                    	ps.setString(2, CourseTime);
                    	ps.setString(3, ID);
                    	ps.setBinaryStream(4, fis, (int) filecur.length());
                    	ps.setString(5, "null");
                    	ps.setString(6, "null");
                    	ps.setString(7, filename);
                    	ps.setString(8, uploadtime);
            			ps.executeUpdate();    	
            			type = true;
                    }
                    catch(Exception ex)
                    {
                    	type = false;
                    	ex.printStackTrace();
                    }
                    finally
                    {
                    	DBUtil.Close();
                    	out.print(type+" "+filename);
                    	out.flush();
                    	out.close();
                    }
            	}
            }         
              
        } 
        catch (Exception e) {  
        	System.out.println("上传失败");
        	//out.print("上传失败："+e.getMessage());
        	out.print("false");
        }  
		
	}


}

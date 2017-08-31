package com.Servlet;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.*;
import com.DBTool.DBUtil;

/*
 * 输入：ArrayList<float[]>类型的list，float[]大小2，分别存放经度纬度
 * 输出：一个字符串，按输入顺序得出结果，Y为签到成功，N为离群点
 * 
 * 测试用main：
 * public static void main(String[] args)
	{
		ArrayList<float[]> list=new ArrayList();
		float[] a = { (float) 0.00001, (float) 0.00001 }; 
		float[] b = { (float) 0.00002, (float) 0.00001 }; 
		list.add(a);
		list.add(a);
		list.add(a);
		list.add(a);
		list.add(a);
		list.add(b);
		
		OutlierNodeDetect A = new OutlierNodeDetect();
		String end=A.progress(list);
		System.out.println(end);
	}
 */    
public class OutlierNodeDetect extends Thread{  
	/*k表示第几邻近*/
	private static int INT_K = 5;
	public static String CourseNum = "";
	public static String Time = "";
	public static String intime = "";//开启签到的教师签到时间，格式yyyy-MM-dd HH:mm:ss
	
	
    public List<DataNode> getOutlierNode(List<DataNode> allNodes) {  
  
        List<DataNode> kdAndKnList = getKDAndKN(allNodes);  
        calReachDis(kdAndKnList);  
        calReachDensity(kdAndKnList);  
        calLof(kdAndKnList);  
        //降序排序  
        Collections.sort(kdAndKnList, new LofComparator());  
  
        return kdAndKnList;  
    }  
  
    /*计算每个点的局部离群点因子*/  
    private void calLof(List<DataNode> kdAndKnList) {  
        for (DataNode node : kdAndKnList) {  
            List<DataNode> tempNodes = node.getkNeighbor();  
            double sum = 0.0;  
            for (DataNode tempNode : tempNodes) {  
                double rd = getRD(tempNode.getNodeName(), kdAndKnList);  
                sum = rd / node.getReachDensity() + sum;  
            }  
            sum = sum / (double) INT_K;  
            node.setLof(sum);  
        }  
    }  
  
    /*计算每个点的可达距离*/  
    private void calReachDensity(List<DataNode> kdAndKnList) {  
        for (DataNode node : kdAndKnList) {  
            List<DataNode> tempNodes = node.getkNeighbor();  
            double sum = 0.0000001;  
            double rd = 0.0;  
            for (DataNode tempNode : tempNodes) {  
                sum = tempNode.getReachDis() + sum;  
            }  
            rd = (double) INT_K / sum;  
            node.setReachDensity(rd);  
        }  
    }  
      
    /*计算每个点的可达密度,reachdis(p,o)=max{ k-distance(o),d(p,o)} */  
    private void calReachDis(List<DataNode> kdAndKnList) {  
        for (DataNode node : kdAndKnList) {  
            List<DataNode> tempNodes = node.getkNeighbor();  
            for (DataNode tempNode : tempNodes) {  
            	//获取tempNode点的k-距离 
                double kDis = getKDis(tempNode.getNodeName(), kdAndKnList);  
                //reachdis(p,o)=max{ k-distance(o),d(p,o)}  
                if (kDis < tempNode.getDistance()) {  
                    tempNode.setReachDis(tempNode.getDistance());  
                } else {  
                    tempNode.setReachDis(kDis);  
                }  
            }  
        }  
    }  
  
    /* 获取某个点的k-距离（kDistance） */   
    private double getKDis(String nodeName, List<DataNode> nodeList) {  
        double kDis = 0;  
        for (DataNode node : nodeList) {  
            if (nodeName.trim().equals(node.getNodeName().trim())) {  
                kDis = node.getkDistance();  
                break;  
            }  
        }  
        return kDis;  
  
    }  
  
    /* 获取某个点的可达距离*/  
    private double getRD(String nodeName, List<DataNode> nodeList) {  
        double kDis = 0;  
        for (DataNode node : nodeList) {  
            if (nodeName.trim().equals(node.getNodeName().trim())) {  
                kDis = node.getReachDensity();  
                break;  
            }  
        }  
        return kDis;  
  
    }  
      
    /*
     * 计算给定点NodeA与其他点NodeB的欧几里得距离（distance）,并找到NodeA点的前5位NodeB，然后记录到NodeA的k-领域（kNeighbor）变量。 
     * 同时找到NodeA的k距离，然后记录到NodeA的k-距离（kDistance）变量中。 
     * 处理步骤如下： 
     * 1,计算给定点NodeA与其他点NodeB的欧几里得距离，并记录在NodeB点的distance变量中。 
     * 2,对所有NodeB点中的distance进行升序排序。 
     * 3,找到NodeB点的前5位的欧几里得距离点，并记录到到NodeA的kNeighbor变量中。 
     * 4,找到NodeB点的第5位距离，并记录到NodeA点的kDistance变量中。 
     */  
    private List<DataNode> getKDAndKN(List<DataNode> allNodes) {  
        List<DataNode> kdAndKnList = new ArrayList<DataNode>();  
        for (int i = 0; i < allNodes.size(); i++) {  
            List<DataNode> tempNodeList = new ArrayList<DataNode>();  
            DataNode nodeA = new DataNode(allNodes.get(i).getNodeName(), allNodes  
                    .get(i).getDimensioin());  
            //1,找到给定点NodeA与其他点NodeB的欧几里得距离，并记录在NodeB点的distance变量中。  
            for (int j = 0; j < allNodes.size(); j++) {  
                DataNode nodeB = new DataNode(allNodes.get(j).getNodeName(), allNodes  
                        .get(j).getDimensioin());  
              //计算NodeA与NodeB的distance
                double tempDis = getDis(nodeA, nodeB);  
                nodeB.setDistance(tempDis);  
                tempNodeList.add(nodeB);  
            }  
  
            //2,对所有NodeB点中的distance进行升序排序  
            Collections.sort(tempNodeList, new DistComparator());  
            for (int k = 1; k < INT_K; k++) {  
            	//3,找到NodeB点的前K位的欧几里得距离点，并记录到到NodeA的kNeighbor变量中 
                nodeA.getkNeighbor().add(tempNodeList.get(k));  
                if (k == INT_K - 1) {  
                	//4,找到NodeB点的第K-1位距离，并记录到NodeA点的kDistance变量中
                    nodeA.setkDistance(tempNodeList.get(k).getDistance());  
                }  
            }  
            kdAndKnList.add(nodeA);  
        }  
  
        return kdAndKnList;  
    }  
      
    /*计算给定点A与其他点B之间的欧几里得距离。 */  
    private double getDis(DataNode A, DataNode B) {  
        double dis = 0.0;  
        double[] dimA = A.getDimensioin();  
        double[] dimB = B.getDimensioin();  
        if (dimA.length == dimB.length) {  
            for (int i = 0; i < dimA.length; i++) {  
                double temp = Math.pow(dimA[i] - dimB[i], 2);  
                dis = dis + temp;  
            }  
            dis = Math.pow(dis, 0.5);  
        }  
        return dis;  
    }  
  
    /*升序排序*/  
    class DistComparator implements Comparator<DataNode> {  
        public int compare(DataNode A, DataNode B) {  
            //return A.getDistance() - B.getDistance() < 0 ? -1 : 1;  
            if((A.getDistance()-B.getDistance())<0)     
                return -1;    
            else if((A.getDistance()-B.getDistance())>0)    
                return 1;    
            else return 0;    
        }  
    }  
  
    /* 降序排序*/ 
    class LofComparator implements Comparator<DataNode> {  
        public int compare(DataNode A, DataNode B) {  
            //return A.getLof() - B.getLof() < 0 ? 1 : -1;  
            if((A.getLof()-B.getLof())<0)     
                return 1;    
            else if((A.getLof()-B.getLof())>0)    
                return -1;    
            else return 0;    
        }  
    } 
  
    public void run()
    {
    	ArrayList<float[]> list = new ArrayList<float[]>();//允许其他类访问的离群点检测的input list
    	int count = 0;
    	String[] id = new String[200];		//记录所有candidate的ID
    	String[] time = new String[200];	//记录所有candidate签到的时间，格式yyyy-MM-dd HH:mm:ss
    	
    	try
    	{
    		for(int k=0;k<600;k++)//空转10min
    			Thread.sleep(1000);
    		
    		SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        	ParsePosition pos = new ParsePosition(0);
    		Date d1 = (Date) sd.parse(intime, pos);   		
    		
    		Connection con=DBUtil.getConnection();
    		Statement stmt=con.createStatement();
    		
    		String query = "select * from AttendanceInfo where CourseNum='"+CourseNum+"'and InTime like '"+Time+"'";
    		ResultSet rs0 = stmt.executeQuery(query);
    		
    		while(rs0.next())
    		{
    			float longitude = rs0.getFloat(5);
    			float latitude = rs0.getFloat(6);
    			float[] i = new float[2];   i[0] = longitude;   i[1] = latitude;
    			id[count] = rs0.getString(1);   time[count] = rs0.getString(4);   count++;
    			list.add(i);//放入input list中
    		}
    		OutlierNodeDetect A = new OutlierNodeDetect();
    		String end=A.progress(list);
    		System.out.println(end);
    		
    		for(int j=0; j<count; j++)
    		{	
    			String inresult = end.substring(j, j+1);
    			
    			SimpleDateFormat sd1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            	ParsePosition pos1 = new ParsePosition(0);
        		Date d2 = (Date) sd1.parse(time[j], pos1);        		
        		long interval = d1.getTime() - d2.getTime();// 得出的时间间隔是毫秒
        		if(interval >= 0)  inresult = "N";//学生签到时间在老师之前，则签到失败
        		
    			String cur_id = id[j];
    			String sql = "update AttendanceInfo set InResult='" + inresult + "' where StudentID='" + cur_id +"' and CourseNum='"+CourseNum+"' and InTime like '" + Time + "'";
    			stmt.executeUpdate(sql);
    		}
    	}
    	catch(Exception ex)
        {
        	ex.printStackTrace();
        }
		
    }
    
    public String progress (ArrayList<float[]> list) 
    {
        //java.text.DecimalFormat  df  =new   java.text.DecimalFormat("#.####");    
  
        ArrayList<DataNode> dpoints = new ArrayList<DataNode>();  
        

        int iname=1;
        /*输入*/
        for (float[] j : list) 
        {
        	double[] temp=new double[2];
        	temp[0]=(double)j[0];
            temp[1]=(double)j[1];
            String sname = String.valueOf(iname++);
            dpoints.add(new DataNode(sname, temp));
        }

        char[] restmp=new char[list.size()];
        
        if(dpoints.isEmpty())return "无输入";
        else if(dpoints.size()<INT_K)return "样本点过少，无法进行离群点检测";  
        else
        {
        	OutlierNodeDetect lof = new OutlierNodeDetect(); 
	        
	        List<DataNode> nodeList = lof.getOutlierNode(dpoints);  
	  
	        for (DataNode node : nodeList) 
	        {
	        	if(node.getLof()>100)restmp[Integer.parseInt(node.getNodeName())-1]='N';
	        	else restmp[Integer.parseInt(node.getNodeName())-1]='Y';
	        	
	           // System.out.println(node.getDimensioin()[0] + "  " + node.getDimensioin()[1] + "  " + node.getLof());  
	        }
        }
        
        String ret=new String(restmp);
        return ret;
        
    }
}  
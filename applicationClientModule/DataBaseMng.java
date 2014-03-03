import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.Time;
import java.util.List;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;


public class DataBaseMng {
	
	
	public static String url = "jdbc:mysql://localhost:3306/";
	public static String dbName = "tickets";
	public static String userName = "root";
	public static String code = "123123";
	
	public void addData(List<TicketInformation> allTicketData, int from_ID) {
		try {
			Connection connection = DriverManager.getConnection(url+dbName,userName,code);
			connection.setAutoCommit(false);
			
			PreparedStatement pstmt = null;
			String sql = "insert into ticket20140301 values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			
			pstmt = connection.prepareStatement(sql);
			
			for (int i = 0; i < allTicketData.size(); i++) {
				pstmt.setInt(1, i*10000+from_ID);
				pstmt.setString(2, (allTicketData.get(i)).from_station_name);
				pstmt.setString(3, (allTicketData.get(i)).to_station_name);
				pstmt.setTime(4, (allTicketData.get(i)).start_time);
				pstmt.setTime(5, (allTicketData.get(i)).arrive_time);
				pstmt.setString(6, (allTicketData.get(i)).station_train_code);
				pstmt.setString(7, (allTicketData.get(i)).rw_num);
				pstmt.setString(8, (allTicketData.get(i)).rz_num);
				pstmt.setString(9, (allTicketData.get(i)).tz_num);
				pstmt.setString(10, (allTicketData.get(i)).wz_num);
				pstmt.setString(11, (allTicketData.get(i)).yw_num);
				pstmt.setString(12, (allTicketData.get(i)).yz_num);
				pstmt.setString(13, (allTicketData.get(i)).ze_num);
				pstmt.setString(14, (allTicketData.get(i)).zy_num);
				pstmt.setString(15, (allTicketData.get(i)).swz_num);	
				pstmt.addBatch();
				
			    if (i%5000 == 0) {
			    	pstmt.executeBatch();
			    	connection.commit();
			    	pstmt.clearBatch();
				}
			}
			
			pstmt.executeBatch();
			connection.commit();
			pstmt.close();
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		}	
	}
	
	public static void main(String[] args) {
		System.setProperty("javax.net.ssl.trustStore", "C:/Program Files/Java/jdk1.7.0_45/jre/lib/security/jssecacerts");
		
		HttpClient httpClient = new HttpClient();
		String response = null;
		byte[] responseBody = null;
		
		String url = "https://kyfw.12306.cn/otn/lcxxcx/query?purpose_codes=ADULT&queryDate=2014-03-03&from_station=AOH&to_station=EPH";
		GetMethod getMethod = new GetMethod(url);
		getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
		   
		 try {
			 //执行getMethod
			 int statusCode = httpClient.executeMethod(getMethod);
			 if (statusCode != HttpStatus.SC_OK) {
				 System.err.println("Method failed: " + getMethod.getStatusLine());
			 }
			 //读取内容 
			 responseBody = getMethod.getResponseBody();
			 response = getMethod.getResponseBodyAsString();
			 System.out.println("Errors is " + response);	
			 
			 String newResponse = new String(responseBody);
			 System.out.println("New Errors is " + newResponse);
			 //System.out.println("Errors is " + responseBody[]);
			
			 JSONObject jsr = (JSONObject) JSONSerializer.toJSON(newResponse);
		 } catch (JSONException e) {
			System.out.println("Errors is " + response);
			//System.out.println(responseBody);
		 }catch (HttpException e) {
			 //发生致命的异常，可能是协议不对或者返回的内容有问题
			 System.out.println("Please check your provided http address!");
			 e.printStackTrace();
		 } catch (IOException e) {
			 //发生网络异常
			 e.printStackTrace();
		 }finally {
			 //释放连接
			 getMethod.releaseConnection();
    	}
	}
}

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.apache.commons.httpclient.params.HttpMethodParams;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import net.sf.json.*;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.codehaus.jackson.*;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.sun.mail.iap.Response;

public class TicketSearch implements Runnable{
	
	
	public int from_ID;
	public int num_search;
	public String train_date;
	
	public static MultiThreadedHttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();
	
	static {
		connectionManager.getParams().setDefaultMaxConnectionsPerHost(1000);
		connectionManager.getParams().setMaxTotalConnections(1000);
	}
	
	
	public TicketSearch(int from_ID, int num_search,String train_date) {
		this.from_ID = from_ID;
		this.num_search = num_search;
		this.train_date = train_date;
		
	}
	
	
	public void run() {
		//构造HttpClient的实例
		//MultiThreadedHttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();
		
		 HttpClient httpClient = new HttpClient(connectionManager);
		  
		 List<TicketInformation> ticketDataCollection = new ArrayList<TicketInformation>();
		 TicketConstants ticketConstants = new TicketConstants();
		 
		 String from_station, to_station;
		 int stationNum = ticketConstants.station_code.size();
		 String response = null;
		 byte[] responseBody = null;
		 InputStream resStream = null;
		 
		 long start = System.currentTimeMillis();
		 
		 for(int i=from_ID; i<from_ID+num_search; i++) {
		     for(int j=0; j<stationNum; j++) {
		    	 if(i == j) {
		    		 continue;
		    	 } else {
		    		 
		    		 from_station = ticketConstants.station_code.get(i); //"leftTicketDTO.from_station="+ "WHN";//ticketInformation.station_code.get(i);
		    		 to_station = ticketConstants.station_code.get(j); //"leftTicketDTO.to_station="+ "CDW";//ticketInformation.station_code.get(j);
		    		 	    		 
		    		 //新版查询url = "https://kyfw.12306.cn/otn/leftTicket/query?" + train_data + "&" + from_station + "&" + to_station + "&" + purpose_codes;
		    		 String url = "https://kyfw.12306.cn/otn/lcxxcx/query?purpose_codes=ADULT"+"&"+"queryDate="+train_date+"&"+"from_station="+from_station+"&"+"to_station="+to_station;
		    		
		    		 //创建GET方法的实例
		    		 GetMethod getMethod = new GetMethod(url);   //"http://yupiao.info/"
		    		 	  
		    		 //使用系统提供的默认的恢复策略
		    		 getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
		   
		    		 try {
		    			 //执行getMethod
		    			 int statusCode = httpClient.executeMethod(getMethod);
		    			 
		    			 if (statusCode != HttpStatus.SC_OK) {
		    				 System.err.println("Method failed: " + getMethod.getStatusLine());
		    			 }
		    			 //读取内容 
		    			 responseBody = getMethod.getResponseBody();
		    			 //response = getMethod.getResponseBodyAsString();
		    			 
		    			 BufferedReader resBufferedReader = null;
		    			 resStream = getMethod.getResponseBodyAsStream();
		    			 resBufferedReader = new BufferedReader(new InputStreamReader(resStream));
		    			 StringBuffer resBuffer = new StringBuffer();
		    			 
		    			 String resTemp = "";
		    			 while((resTemp = resBufferedReader.readLine()) != null) {
		    				 resBuffer.append(resTemp);
		    			 }
		    			 
		    			 response = resBuffer.toString();
		    			 	
		    			 JSONObject jsr = (JSONObject) JSONSerializer.toJSON(response);
		    			  		    			 
		    			 //新版查询JSONArray ticket = (JSONArray) JSONSerializer.toJSON(jsr.get("data"));
		    			 JSONObject ticketData = (JSONObject) JSONSerializer.toJSON(jsr.get("data"));
		    			//新版查询JSONObject ticket1 = (JSONObject) JSONSerializer.toJSON(ticket.get(1));
		    			 
		    			 if((Boolean)ticketData.get("flag")) {          //当起点站和终点站在同一个城市的时候，messages为"没有符合条件的数据！" 正常情况下为"[]"
		    				 
		    			    JSONArray ticket1 = (JSONArray) JSONSerializer.toJSON(ticketData.get("datas"));
		    			    //新版查询JSONObject trainNO = (JSONObject) JSONSerializer.toJSON(ticket1.get("queryLeftNewDTO"));
		    			 
		    			    
		    			    
		    			    for (int k = 0; k < ticket1.size(); k++) {
		    				   JSONObject trainNO = (JSONObject) JSONSerializer.toJSON(ticket1.get(k));
		    				   TicketInformation ticket = new TicketInformation();
		    				   
		    				   ticket.SetValues(trainNO);
		    				   ticketDataCollection.add(ticket);
		    				   //输出结果
		    				   //System.out.println("            " + trainNO.toString());
		    			    }
		    			    
		    			    System.out.println(ticketConstants.station_name.get(i)+">>>>>>>>"+ticketConstants.station_name.get(j)+"from_ID======="+ i + "   to_ID======" + j );
		    			 }

		    		 } catch (HttpException e) {
		    			 //发生致命的异常，可能是协议不对或者返回的内容有问题
		    			 j--;
		    			 System.out.println("Please check your provided http address!");
		    			 e.printStackTrace();
		    		 } catch (IOException e) {
		    			 //发生网络异常
		    			 j--;
		    			 e.printStackTrace();
		    		 } catch (JSONException e) {
						// TODO: handle exception
		    			 j--;
		    			 System.out.println("异常是"+ response);
		    			 String errString = new String(responseBody);
		    			 System.out.println("2异常是"+ errString);
					}
		    		 
		    		 finally {
		    			 //释放连接
		    			 getMethod.releaseConnection();
		        	}
		    		 
		    	 }
		     }
		     
		 }		
		 
		 long end = System.currentTimeMillis();
		 //System.out.println("Thread___"+from_ID+"___to___"+(from_ID+num_search)+" : "+(end-start));
		 
		 DataBaseMng dbManage = new DataBaseMng();
		 dbManage.addData(ticketDataCollection,from_ID);
		 
		 /*if(this.ticketDataCollection.size()>0) {
			 Main.addTiecketInformationData(this.ticketDataCollection);
		 }*/
		
	}
	
	

}

import java.io.IOException;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.enterprise.inject.New;

import net.sf.json.*;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.codehaus.jackson.*;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

public class Main {
	
	public static List<TicketInformation> allTicketData = new LinkedList<TicketInformation>();
	public static int num_thread_end =0;
	
	public static void addTiecketInformationData(List<TicketInformation> ticketInformation) {
		synchronized (allTicketData) {
			allTicketData.addAll(ticketInformation);	
			System.out.println("This is thread **************"+num_thread_end);
			num_thread_end++;
		}
		
	}
		
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		  //安全证书
		  System.setProperty("javax.net.ssl.trustStore", "C:/Program Files/Java/jdk1.7.0_45/jre/lib/security/jssecacerts");
		  
		  int search_num = 2;
		  int thread_num = 1000;
		  String train_date = "2014-03-04";
		  		  
		  long start = System.currentTimeMillis();
		  
		  for (int i = 0; i < thread_num; i++) {
			  TicketSearch ticketSearch = new TicketSearch(i*search_num, search_num, train_date);
			  Thread t = new Thread(ticketSearch);
			  t.start();		
		}
		  
 		  long end = System.currentTimeMillis();
		  
		  System.out.println("********************It wastes" + (end -start) + " ms");		
	}
}
import net.sf.json.*;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.codehaus.jackson.*;


public class JSONHelp {
	 public ObjectMapper mapper;
	 
	 public JSONHelp(Inclusion inclusion) {
			mapper = new ObjectMapper();
			// 设置输出包含的属性
			//mapper.getSerializationConfig().setSerializationInclusion(inclusion);    //withSerializationInclusion(inclusion);
			// 设置输入时忽略JSON字符串中存在而Java对象实际没有的属性
			//mapper.getDeserializationConfig().set(org.codehaus.jackson.map.DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES,false);
	 }
	 
	 public static JSONHelp buildNormalIgnoreBinder() {
			return new JSONHelp(Inclusion.ALWAYS);
		}
		

}

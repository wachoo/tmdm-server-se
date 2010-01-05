package talend.mdm.libraries;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;

import bsh.EvalError;
import bsh.Interpreter;

public class Test {

	/**
	 * @param args
	 */
	
		public static void main(String[] args) {
			String condition="C1 and C2";
			Pattern p=Pattern.compile("C([0-9]+)", Pattern.CASE_INSENSITIVE);
			Matcher m=p.matcher(condition);
			while(m.find()){
				System.out.println(m.group());
			}
			
		      boolean truth = false;
		      try {
		          Interpreter ntp = new Interpreter();
		          
		          ntp.set("a", true);
		          ntp.set("b", false);
		          ntp.set("c", true);
		          ntp.set("d", true);
		          ntp.set("e", true);
		          ntp.eval("truth = a && b && ( c || !(d || e) ) == a && b && c && d;");
		          truth = ((Boolean)ntp.get("truth")).booleanValue();
		      } catch (EvalError e) {
		          e.printStackTrace();
		      }
		      System.out.println(truth);
		  }

	
	private void escapexml(){
		String xml="<Country><isoCode>CN</isoCode><label>CHINA2</label><Address><line1>line11</line1><id>id11</id><line2>line22</line2><zipcode>zipcode11</zipcode></Address></Country>" ;
		System.out.println(StringEscapeUtils.escapeXml(xml));

	}
	
}

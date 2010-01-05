package talend.mdm.test;

import junit.framework.TestCase;


public class MDMTestCase extends TestCase {

  public static void log(String msg) {
    System.out.println(System.currentTimeMillis()+" [Talend MDM Test]:"+msg);
  }

  public void setUp() {
    log("starting '"+getName()+"'");
  }

  public void tearDown() {
    log("'"+getName()+"' done.");
  }

  public static void logTxBegin() {
    log("");
    log("tx begin");
  }

  public static void logTxEnd() {
    log("tx end");
    log("");
  }
}

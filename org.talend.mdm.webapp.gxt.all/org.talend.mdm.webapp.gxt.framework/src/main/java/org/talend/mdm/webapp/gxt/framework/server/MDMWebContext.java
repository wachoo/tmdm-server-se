package org.talend.mdm.webapp.gxt.framework.server;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.talend.mdm.webapp.gxt.common.Configuration;

/**
 * A bean representing the App context.
 *
 */
public class MDMWebContext {

  /** the log used by this class */
  private static Log log = LogFactory.getLog(MDMWebContext.class);

  private Configuration configuration;

  private String buildVersion;
  private String buildDate;

  private static final String BUILD_VERSION_KEY = "build.version";
  private static final String BUILD_DATE_KEY = "build.date";

  /** the time that App was started */
  private Date startTime;

  /** the directory where App is deployed */
  private String webApplicationRoot;

  private static final MDMWebContext instance = new MDMWebContext();

  public static MDMWebContext getInstance() {
    return instance;
  }

  private MDMWebContext() {
    // and note when App was started
    this.startTime = new Date();

    // TODO load release info from the build.properties file
  }

  /**
   * Gets the version of App being used.
   *
   * @return    the version number as a String
   */
  public String getBuildVersion() {
    return this.buildVersion;
  }

  /**
   * Gets the date that App was built.
   *
   * @return    the date as a String
   */
  public String getBuildDate() {
    return this.buildDate;
  }

  /**
   * Gets the amount of time that App has been running for.
   *
   * @return  a number of milliseconds
   */
  public long getUptime() {
    return new Date().getTime() - startTime.getTime();
  }

  public long getMemoryUsageInKB() {
    return (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024;
  }

  public long getTotalMemoryInKB() {
    return Runtime.getRuntime().totalMemory() / 1024;
  }

  public Configuration getConfiguration() {
    return configuration;
  }

  public void setConfiguration(Configuration configuration) {
    this.configuration = configuration;
  }

  /**
   * Sets the directory where themes are located.
   *
   * @param webApplicationRoot    the absolute path to the webapp root on disk
   */
  public void setWebApplicationRoot(String webApplicationRoot) {
    this.webApplicationRoot = webApplicationRoot;
  }

  public String getWebApplicationRoot() {
    return webApplicationRoot;
  }

}
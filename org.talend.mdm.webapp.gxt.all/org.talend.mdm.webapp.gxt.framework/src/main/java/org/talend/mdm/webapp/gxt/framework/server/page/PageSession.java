package org.talend.mdm.webapp.gxt.framework.server.page;
import java.util.Enumeration;
import java.util.Vector;

import javax.servlet.http.HttpSession;

public class PageSession 
{
    
  public static final int PAGE_SCOPE = 0x2;

  public PageSession(String pageId, HttpSession session)
  {
    myPageId = pageId;
    mySession = session;
    myScopedPrefix = "org.talend.mdm.webapp.page." + myPageId + "?";
  }

  public Object getAttribute(String inputKey)
  {
    if (inputKey == null)
    {
      throw new IllegalArgumentException("Name can not be null");
    }
    checkStatus();
    String key = makePageScopedKey(inputKey);
    Object result =  mySession.getAttribute(key);
    return result;
  }

  public Object getAttribute(String inputKey, int scope)
  {
    if (inputKey == null)
    {
      throw new IllegalArgumentException("Name can not be null");
    }
    checkStatus();
    if (scope == PageSession.PAGE_SCOPE)
    {
      return getAttribute(inputKey);
    }
    else
    {
      return mySession.getAttribute(inputKey);
    }
  }

  public Enumeration getAttributeNames()
  {
    // strip out names from the session which do not start with
    // the scoped key
    checkStatus();
    Vector<String> result = new Vector<String>();
    Enumeration<String> names = mySession.getAttributeNames();
    while(names.hasMoreElements())
    {
      String name = names.nextElement();
      if (name.startsWith(myScopedPrefix))
      {
        result.add(decodeScopedKey(name));
      }
    }
    return result.elements();
  }

  public Enumeration getAttributeNames(int scope)
  {
    checkStatus();
    if (scope == PageSession.PAGE_SCOPE)
    {
      return getAttributeNames();
    }
    else
    {
      return mySession.getAttributeNames();
    }
  }

  public long getCreationTime()
  {
    checkStatus();
    return mySession.getCreationTime();
  }

  public String getId()
  {
    checkStatus();
    return mySession.getId();
  }

  public long getLastAccessedTime()
  {
    checkStatus();
    return mySession.getLastAccessedTime();
  }

  public int getMaxInactiveInterval()
  {
    return mySession.getMaxInactiveInterval();
  }

  public void invalidate()
  {
    myInvalidated = true;
    mySession.invalidate();
  }

  public boolean isNew()
  {
    return mySession.isNew();
  }

  public void removeAttribute(String inputKey)
  {
    if (inputKey == null)
    {
      throw new IllegalArgumentException("Name can not be null");
    }
    checkStatus();
    if (mySession != null)
    {
      mySession.removeAttribute(makePageScopedKey(inputKey));
    }
  }

  public void removeAttribute(String inputKey, int scope)
  {
    if (inputKey == null)
    {
      throw new IllegalArgumentException("Name can not be null");
    }
    checkStatus();
    if (scope == PageSession.PAGE_SCOPE)
    {
      removeAttribute(inputKey);
    }
    else
    {
      mySession.removeAttribute(inputKey);
    }
  }

  public void setAttribute(String inputKey, Object inputValue)
  {
    if (inputKey == null)
    {
      throw new IllegalArgumentException("Name can not be null");
    }
    checkStatus();
    mySession.setAttribute(makePageScopedKey(inputKey),inputValue);
  }

  public void setAttribute(String inputKey, Object inputValue, int scope)
  {
    if (inputKey == null)
    {
      throw new IllegalArgumentException("Name can not be null");
    }
    if (scope == PageSession.PAGE_SCOPE)
    {
      setAttribute(inputKey,inputValue);
    }
    else
    {
      mySession.setAttribute(inputKey,inputValue);
    }
  }

  public void setMaxInactiveInterval(int time)
  {
    mySession.setMaxInactiveInterval(time);
  }


  private String makePageScopedKey(String name)
  {
    String key = myScopedPrefix + name;
    return key;
  }
  
  private String decodeScopedKey(String name)
  {
    String result = name;
    if (name.startsWith(myScopedPrefix))
    {
      result = name.substring(myScopedPrefix.length());
    }
    return result;
  }
  
  private void checkStatus()
  {
    if (myInvalidated)
    {
      throw new IllegalStateException("Session is invalidated");
    }
  }
  
  private String myPageId;
  private HttpSession mySession;
  private boolean myInvalidated = false;
  private String myScopedPrefix;
}
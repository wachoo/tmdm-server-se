package com.allen_sauer.gwt.log.client.impl;

import com.google.gwt.resources.client.ResourcePrototype;
import com.google.gwt.core.client.GWT;

public class com_allen_sauer_gwt_log_client_impl_LogClientBundle_default_InlineClientBundleGenerator implements com.allen_sauer.gwt.log.client.impl.LogClientBundle {
  public com.allen_sauer.gwt.log.client.impl.LogClientBundle.LogCssResource css() {
    if (css == null) {
      css = new com.allen_sauer.gwt.log.client.impl.LogClientBundle.LogCssResource() {
        private boolean injected;
        public boolean ensureInjected() {
          if (!injected) {
            injected = true;
            com.google.gwt.dom.client.StyleInjector.inject(getText());
            return true;
          }
          return false;
        }
        public String getName() {
          return "css";
        }
        public String getText() {
          return com.google.gwt.i18n.client.LocaleInfo.getCurrentLocale().isRTL() ? ((".log-panel{background-color:" + ("#ecf2fc")  + ";border:" + ("1px"+ " " +"solid"+ " " +"red")  + ";margin:" + ("0")  + ";filter:" + ("alpha(opacity\\=95)")  + ";opacity:" + ("0.95")  + ";z-index:" + ("1000")  + ";}.log-panel .Gkjd45cI{font-size:" + ("10pt")  + ";margin:" + ("0")  + ";text-align:" + ("right")  + ";}.log-panel BUTTON{font-size:" + ("10pt")  + ";margin:") + (("0")  + ";}.log-panel .Gkjd45cJ{cursor:" + ("move")  + ";font-weight:" + ("bold")  + ";}.log-panel .Gkjd45cB{margin:" + ("0"+ " " +"1.2em")  + ";}.log-panel BUTTON.Gkjd45cA{color:" + ("#444")  + " !important;}.log-panel .Gkjd45cH{white-space:" + ("nowrap")  + ";}.log-panel .Gkjd45cC{white-space:" + ("pre")  + ";font-family:" + ("monospace")  + ";cursor:" + ("help")  + ";}.log-panel .Gkjd45cD{background-color:" + ("#f0f0f0")  + ";}.log-panel .Gkjd45cG{background-color:" + ("#fff") ) + (";}.log-panel .Gkjd45cF{cursor:" + ("sw-resize")  + ";}")) : ((".log-panel{background-color:" + ("#ecf2fc")  + ";border:" + ("1px"+ " " +"solid"+ " " +"red")  + ";margin:" + ("0")  + ";filter:" + ("alpha(opacity\\=95)")  + ";opacity:" + ("0.95")  + ";z-index:" + ("1000")  + ";}.log-panel .Gkjd45cI{font-size:" + ("10pt")  + ";margin:" + ("0")  + ";text-align:" + ("left")  + ";}.log-panel BUTTON{font-size:" + ("10pt")  + ";margin:") + (("0")  + ";}.log-panel .Gkjd45cJ{cursor:" + ("move")  + ";font-weight:" + ("bold")  + ";}.log-panel .Gkjd45cB{margin:" + ("0"+ " " +"1.2em")  + ";}.log-panel BUTTON.Gkjd45cA{color:" + ("#444")  + " !important;}.log-panel .Gkjd45cH{white-space:" + ("nowrap")  + ";}.log-panel .Gkjd45cC{white-space:" + ("pre")  + ";font-family:" + ("monospace")  + ";cursor:" + ("help")  + ";}.log-panel .Gkjd45cD{background-color:" + ("#f0f0f0")  + ";}.log-panel .Gkjd45cG{background-color:" + ("#fff") ) + (";}.log-panel .Gkjd45cF{cursor:" + ("se-resize")  + ";}"));
        }
        public java.lang.String logClearAbout(){
          return "Gkjd45cA";
        }
        public java.lang.String logClearButton(){
          return "Gkjd45cB";
        }
        public java.lang.String logMessage(){
          return "Gkjd45cC";
        }
        public java.lang.String logMessageHover(){
          return "Gkjd45cD";
        }
        public java.lang.String logPanel(){
          return "log-panel";
        }
        public java.lang.String logResizeSe(){
          return "Gkjd45cF";
        }
        public java.lang.String logScrollPanel(){
          return "Gkjd45cG";
        }
        public java.lang.String logStacktrace(){
          return "Gkjd45cH";
        }
        public java.lang.String logTextArea(){
          return "Gkjd45cI";
        }
        public java.lang.String logTitle(){
          return "Gkjd45cJ";
        }
      }
      ;
    }
    return css;
  }
  private static com.allen_sauer.gwt.log.client.impl.LogClientBundle.LogCssResource css;
  
  public ResourcePrototype[] getResources() {
    return new ResourcePrototype[] {
      css(), 
    };
  }
  public native ResourcePrototype getResource(String name) /*-{
    switch (name) {
      case 'css': return this.@com.allen_sauer.gwt.log.client.impl.LogClientBundle::css()();
    }
    return null;
  }-*/;
}

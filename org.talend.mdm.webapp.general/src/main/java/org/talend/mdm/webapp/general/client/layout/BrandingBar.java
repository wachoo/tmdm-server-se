// ============================================================================
//
// Copyright (C) 2006-2014 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.webapp.general.client.layout;

import java.util.List;

import org.talend.mdm.webapp.base.client.SessionAwareAsyncCallback;
import org.talend.mdm.webapp.base.client.util.UrlUtil;
import org.talend.mdm.webapp.base.client.util.UserContextUtil;
import org.talend.mdm.webapp.general.client.General;
import org.talend.mdm.webapp.general.client.GeneralServiceAsync;
import org.talend.mdm.webapp.general.client.i18n.MessageFactory;
import org.talend.mdm.webapp.general.model.LanguageBean;
import org.talend.mdm.webapp.general.model.ProductInfo;
import org.talend.mdm.webapp.general.model.UserBean;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;

public class BrandingBar extends ContentPanel {

    private static BrandingBar instance;

    private FlowPanel bar = new FlowPanel();

    private HorizontalPanel hp = new HorizontalPanel();

    private HTML versionLabel = new HTML();

    private Image logoMdm = new Image("/talendmdm/secure/img/logo-mdm.png"); //$NON-NLS-1$

    private ListBox languageBox = new ListBox();

    Button logout = new Button(MessageFactory.getMessages().logout());

    private BrandingBar() {
        super();
        setHeaderVisible(false);
        setBorders(false);
        buildBar();
        initEvent();
    }

    public static BrandingBar getInstance() {
        if (instance == null) {
            instance = new BrandingBar();
        }
        return instance;
    }

    private void initEvent() {
        languageBox.addChangeHandler(new ChangeHandler() {

            @Override
            public void onChange(ChangeEvent event) {

                String path = Location.getPath();

                String query = Location.getQueryString();
                String lang = Location.getParameter("language"); //$NON-NLS-1$

                if (lang == null || lang.trim().length() == 0) {
                    if (query == null || query.length() == 0) {
                        setHref(path + "?language=" + languageBox.getValue(languageBox.getSelectedIndex())); //$NON-NLS-1$
                    } else {
                        setHref(path + query + "&language=" + languageBox.getValue(languageBox.getSelectedIndex())); //$NON-NLS-1$
                    }
                } else {

                    if (query.indexOf("&language=" + lang + "&") != -1) { //$NON-NLS-1$ //$NON-NLS-2$
                        query = query.replace("&language=" + lang + "&", "&"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                    } else if (query.endsWith("&language=" + lang)) { //$NON-NLS-1$
                        query = query.replace("&language=" + lang, ""); //$NON-NLS-1$ //$NON-NLS-2$
                    } else if (query.startsWith("?language=" + lang)) { //$NON-NLS-1$
                        query = query.replaceAll("language=" + lang + "&?", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                    }
                    setHref(path + query + "&language=" + languageBox.getValue(languageBox.getSelectedIndex())); //$NON-NLS-1$                    
                }
            }
        });

        logout.addSelectionListener(new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {
                GeneralServiceAsync service = (GeneralServiceAsync) Registry.get(General.OVERALL_SERVICE);
                service.logout(new SessionAwareAsyncCallback<Void>() {

                    @Override
                    public void onSuccess(Void result) {
                        Cookies.removeCookie("JSESSIONID"); //$NON-NLS-1$
                        Cookies.removeCookie("JSESSIONIDSSO"); //$NON-NLS-1$
                        setHref("/talendmdm/secure"); //$NON-NLS-1$
                    }
                });

            }
        });
    }

    private native void setHref(String href)/*-{
		$wnd.location.href = href;
    }-*/;

    public void setProductInfo(ProductInfo info) {
        if (info != null) {
            logoMdm.setUrl("/general/secure/img/branding/" + info.getProductKey() + "_header.png"); //$NON-NLS-1$//$NON-NLS-2$
            versionLabel.setHTML(""); //$NON-NLS-1$
        } else {
            logoMdm.setUrl("/talendmdm/secure/img/logo-mdm.png"); //$NON-NLS-1$
            UserBean userBean = Registry.get(General.USER_BEAN);
            versionLabel.setHTML(userBean.isEnterprise() ? MessageFactory.getMessages().enterprise() : MessageFactory
                    .getMessages().community() + "<br>" + MessageFactory.getMessages().edition()); //$NON-NLS-1$
        }
    }

    private void buildBar() {
        UserBean userBean = Registry.get(General.USER_BEAN);
        bar.add(new Image("/talendmdm/secure/img/header-back-title.png")); //$NON-NLS-1$
        String html = userBean.getName() + "<br>"; //$NON-NLS-1$
        if (userBean.getUniverse() != null && userBean.getUniverse().trim().length() != 0
                && !"UNKNOWN".equals(userBean.getUniverse())) { //$NON-NLS-1$
            html += MessageFactory.getMessages().connected_to() + ": [" + userBean.getUniverse() + "]"; //$NON-NLS-1$ //$NON-NLS-2$
        } else {
            html += MessageFactory.getMessages().connected_to() + ": [HEAD]"; //$NON-NLS-1$
        }
        HTML userLabel = new HTML(html);
        userLabel.setStyleName("username"); //$NON-NLS-1$
        userLabel.getElement().setId("username-div"); //$NON-NLS-1$
        bar.add(userLabel);

        hp.getElement().getStyle().setProperty("position", "absolute"); //$NON-NLS-1$ //$NON-NLS-2$
        hp.getElement().getStyle().setProperty("top", "-2px"); //$NON-NLS-1$ //$NON-NLS-2$
        hp.getElement().getStyle().setProperty("right", "1px"); //$NON-NLS-1$ //$NON-NLS-2$

        logoMdm.getElement().getStyle().setMarginTop(2D, Unit.PX);
        hp.add(logoMdm);
        hp.setCellVerticalAlignment(logoMdm, HasVerticalAlignment.ALIGN_BOTTOM);

        versionLabel.setStyleName("version-label"); //$NON-NLS-1$
        hp.add(versionLabel);
        hp.setCellVerticalAlignment(versionLabel, HasVerticalAlignment.ALIGN_MIDDLE);

        hp.add(new HTML("&nbsp;&nbsp;")); //$NON-NLS-1$

        languageBox.getElement().setId("languageSelect"); //$NON-NLS-1$
        languageBox.setStyleName("language-box"); //$NON-NLS-1$
        // Enforce height
        languageBox.setHeight("20px"); //$NON-NLS-1$

        hp.add(languageBox);
        hp.setCellVerticalAlignment(languageBox, HasVerticalAlignment.ALIGN_MIDDLE);
        hp.add(new HTML("&nbsp;&nbsp;")); //$NON-NLS-1$
        hp.add(logout);
        hp.setCellVerticalAlignment(logout, HasVerticalAlignment.ALIGN_MIDDLE);
        hp.add(new HTML("&nbsp;&nbsp;")); //$NON-NLS-1$

        bar.setSize("100%", "100%"); //$NON-NLS-1$ //$NON-NLS-2$
        bar.setStyleName("generic-header-background"); //$NON-NLS-1$
        bar.add(hp);

        this.add(bar);

    }

    public void buildLanguage(List<LanguageBean> languages) {
        String language = languages.get(0).getValue();
        String dateFormat = languages.get(0).getDateTimeFormat();
        for (LanguageBean lang : languages) {
            languageBox.addItem(lang.getText(), lang.getValue());
            if (lang.isSelected()) {
                language = lang.getValue();
                dateFormat = lang.getDateTimeFormat();
                languageBox.setSelectedIndex(languageBox.getItemCount() - 1);
            }
        }
        UrlUtil.setCurrentLocale(language);
        UserContextUtil.setLanguage(language);
        UserContextUtil.setDateTimeFormat(dateFormat);
    }
}
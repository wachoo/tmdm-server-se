package org.talend.mdm.webapp.general.client.layout;

import java.util.List;

import org.talend.mdm.webapp.general.model.MenuBean;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HTML;

public class AccordionMenus extends ContentPanel {  

    private static AccordionMenus instance;
    
    private HTMLMenuItem activeItem;
    
    private AccordionMenus(){
        super();
        this.setHeading("Menus");
        this.addStyleName("menus-list");
        this.setLayout(new FlowLayout());
        this.setScrollMode(Scroll.AUTO);
    }
    
    public static AccordionMenus getInstance(){
        if (instance == null){
            instance = new AccordionMenus();
        }
        return instance;
    }

    public void initMenus(List<MenuBean> menus){
        
        for (int i = 0;i < menus.size();i++){
            MenuBean item = menus.get(i);
            StringBuffer str = new StringBuffer();
            str.append("<span class='body'>");
            str.append("<img src='" + item.getIcon() + "'/>");
            str.append("<span class='desc'>" + item.getDesc() + "</span></span>");
            HTML html = new HTMLMenuItem(item, str.toString());
            html.addClickHandler(clickHander);
            this.add(html);
        }
        this.layout();
    }
    
    ClickHandler clickHander = new ClickHandler() {

        @Override
        public void onClick(ClickEvent event) {
            HTMLMenuItem item = (HTMLMenuItem) event.getSource();
            if (activeItem != null){
                activeItem.removeStyleName("selected");
            }
            item.addStyleName("selected");
            activeItem = item;
            MenuBean menuBean = item.getMenuBean();
            WorkSpace.getInstance().addWorkTab(menuBean.getName(), menuBean.getName(), menuBean.getUrl());
        }

    };
    
    class HTMLMenuItem extends HTML{
        MenuBean menuBean;
        public HTMLMenuItem(MenuBean menuBean, String html){
            super(html);
            this.setStyleName("menu-item");
            this.menuBean = menuBean;
        }
        
        public MenuBean getMenuBean() {
            return menuBean;
        }
    }
}

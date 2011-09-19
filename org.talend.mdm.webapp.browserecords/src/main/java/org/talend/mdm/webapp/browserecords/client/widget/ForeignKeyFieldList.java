package org.talend.mdm.webapp.browserecords.client.widget;

import java.util.ArrayList;
import java.util.List;

import org.talend.mdm.webapp.browserecords.client.i18n.MessagesFactory;
import org.talend.mdm.webapp.browserecords.client.model.ForeignKeyBean;
import org.talend.mdm.webapp.browserecords.client.model.ItemNodeModel;
import org.talend.mdm.webapp.browserecords.client.resources.icon.Icons;
import org.talend.mdm.webapp.browserecords.client.widget.inputfield.ForeignKeyField;
import org.talend.mdm.webapp.browserecords.shared.ComplexTypeModel;
import org.talend.mdm.webapp.browserecords.shared.TypeModel;

import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ToggleButton;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.Validator;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.LabelToolItem;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.HTML;



public class ForeignKeyFieldList extends ContentPanel{
    
    public ItemNodeModel itemNode;
    
    protected TypeModel typeModel;
    
    protected int pageStartIndex;
    
    protected int pageSize;
    
    protected int pageNumber;
    
    protected int pageCount;
    
    protected int pageToggleButtonIndex;
    
    protected VerticalPanel verticalPanel;
    
    protected List<Field<?>> fields;
    
    protected HorizontalPanel pagingBar;
    
    protected Label pageSpace1;
    
    protected Label pageSpace2;
    
    protected HorizontalPanel separatorLeft;    
    
    protected HorizontalPanel separatorRight; 
    
    protected Button addButton; 
    
    protected List<ToggleButton> pageButtonList;
    
    protected ToggleButton pageButton1;
    
    protected ToggleButton pageButton2;
    
    protected ToggleButton pageButton3;
    
    protected ToggleButton pageButton4;
    
    protected ToggleButton pageButton5;
    
    protected boolean pageSpace1Visible;
    
    protected boolean pageSpace2Visible;
    
    protected LabelToolItem sizeLabel;
    
    protected El inputEl;
    
    protected NumberField sizeField;   
    
    public ForeignKeyFieldList(ItemNodeModel itemNode,TypeModel typeModel){
        this.itemNode = itemNode;
        this.typeModel = typeModel;
        pageSize = 5;
        pageNumber = 1;        
        fields = new ArrayList<Field<?>>();
        pagingBar = new HorizontalPanel();
        this.initPagingBar();
        this.setHeaderVisible(false);
        this.createWidget();
    }
    
    /**
     * Adds a field (pre-render).
     * 
     * @param field the field to add
     */
    public void add(Field<?> field) {      
      fields.add(field);
    }

    /**
     * Returns the field at the index.
     * 
     * @param index the index
     * @return the field
     */
    public Field<?> getField(int index) {
      return fields.get(index);
    }

    /**
     * Returns all the child field's.
     * 
     * @return the fields
     */
    public List<Field<?>> getFieldList() {
      return fields;
    }
    
    private void initPagingBar()
    {        
        addButton = new Button("Add"); //$NON-NLS-1$
        addButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
          
            @Override
            public void componentSelected(ButtonEvent ce) {                
                addField();     
            }
        });
        
        pageSpace1 = new Label("..."); //$NON-NLS-1$
        
        pageSpace2 = new Label("..."); //$NON-NLS-1$
        
        separatorLeft = new HorizontalPanel();
        separatorLeft.setWidth(40);
        separatorRight = new HorizontalPanel();
        separatorRight.setWidth(40);
        
        pageButtonList = new ArrayList<ToggleButton>();
        pageButton1 = new ToggleButton("1"); //$NON-NLS-1$
        pageButton1.addSelectionListener(new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {                
                refreshPage(Integer.valueOf(pageButton1.getText()));  
                verticalPanel.focus();
            }
        });
        pageButtonList.add(pageButton1);
        pageButton2 = new ToggleButton("2"); //$NON-NLS-1$
        pageButton2.addSelectionListener(new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {
                refreshPage(Integer.valueOf(pageButton2.getText()));
                verticalPanel.focus();
            }
        });
        pageButtonList.add(pageButton2);
        pageButton3 = new ToggleButton("3"); //$NON-NLS-1$
        pageButton3.addSelectionListener(new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {
                refreshPage(Integer.valueOf(pageButton3.getText()));
                verticalPanel.focus();
            }
        });
        pageButtonList.add(pageButton3);       
        pageButton4 = new ToggleButton("4"); //$NON-NLS-1$
        pageButton4.addSelectionListener(new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {
                refreshPage(Integer.valueOf(pageButton4.getText()));  
                verticalPanel.focus();
            }
        });
        pageButtonList.add(pageButton4);
        pageButton5 = new ToggleButton("5"); //$NON-NLS-1$
        pageButton5.addSelectionListener(new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {
                refreshPage(Integer.valueOf(pageButton5.getText()));  
                verticalPanel.focus();
            }
        });
        pageButtonList.add(pageButton5);
        
        sizeLabel = new LabelToolItem(MessagesFactory.getMessages().page_size_label());
        
        sizeField = new NumberField(){
            protected void onRender(Element target, int index) {
                super.onRender(target, index);
                inputEl = this.input;
            }
        };
        
        sizeField.setWidth(30);
        sizeField.setValue(pageSize);
        sizeField.setValidator(validator);
        sizeField.addListener(Events.Change, new Listener<BaseEvent>() {

            public void handleEvent(BaseEvent be) {
                if (sizeField.isValid() && sizeField.getValue() != null){
                    pageSize = sizeField.getValue().intValue();
                    pageNumber = 1;
                    refreshPage(pageNumber);
//                    setPageSize((int)Double.parseDouble(sizeField.getValue()+""));//$NON-NLS-1$
//                    first();
                }
            }
        });
        sizeField.addListener(Events.KeyDown, new Listener<FieldEvent>() {

            public void handleEvent(FieldEvent fe) {
                if (fe.getKeyCode() == KeyCodes.KEY_ENTER) {
//                    blur(inputEl.dom);
                }
            }
        });
        sizeField.setValue(pageSize);
        
        pagingBar.add(addButton);    
        addButton.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.add()));
        pagingBar.add(separatorLeft);
        pagingBar.add(pageButton1);
        pagingBar.add(pageSpace1);
        pagingBar.add(pageButton2);
        pagingBar.add(pageButton3);
        pagingBar.add(pageButton4);
        pagingBar.add(pageSpace2);
        pagingBar.add(pageButton5);
        pagingBar.add(separatorRight);
        pagingBar.add(sizeLabel);
        pagingBar.add(sizeField);
        pagingBar.setSpacing(5);
        pagingBar.setStyleName("x-toolbar"); //$NON-NLS-1$
    }   
    
    public void createWidget() {  
        
        verticalPanel = new VerticalPanel();        
        verticalPanel.setLayout(new FitLayout());
        pageCount = this.getPageCount();
//        if (typeModel.isSimpleType()
//                || (!typeModel.isSimpleType() && ((ComplexTypeModel) typeModel).getReusableComplexTypes().size() > 0)) {
//            Field<?> field = TreeDetailGridFieldCreator.createField(node, typeModel, Locale.getLanguage());
//            this.add(field);            
//        }        
        
        List<ForeignKeyBean> foreignKeyBeans = (List<ForeignKeyBean>)itemNode.getObjectValue();
        if (foreignKeyBeans == null || foreignKeyBeans.size() == 0) {
            if (typeModel.getMinOccurs() > 0){
                addField();
            }            
        }else{
            for (int i=0;i<foreignKeyBeans.size();i++){
                ForeignKeyBean foreignKeyBean = foreignKeyBeans.get(i);
                Field<?> field = createField(foreignKeyBean);
                add(field);            
            }
        }
        
        verticalPanel.setSpacing(10);
        this.add(verticalPanel);
        refreshPage(pageNumber);
        this.setBottomComponent(pagingBar); 
    }    
    
    private void refreshPage(int currentPageNumber){
        pageNumber = currentPageNumber;
        pageCount = this.getPageCount();  
        pageStartIndex  = (currentPageNumber-1)*pageSize;
        verticalPanel.removeAll();
        if (this.fields.size() > 0){
            for (int i=pageStartIndex;i<pageStartIndex+pageSize&&i<this.fields.size();i++){
                HorizontalPanel recordPanel = new HorizontalPanel();                
                // create Field
                HTML label = new HTML();
                String html = itemNode.getDescription();
                if (itemNode.isKey() || typeModel.getMinOccurs() >= 1)
                    html = html + "<span style=\"color:red\"> *</span>"; //$NON-NLS-1$
                label.setHTML(html);
                recordPanel.add(label);
                if (typeModel.isSimpleType()
                        || (!typeModel.isSimpleType() && ((ComplexTypeModel) typeModel).getReusableComplexTypes().size() > 0)) {
                    Field<?> field = this.getField(i);               
                    field.setWidth(260);
                    recordPanel.add(field);               
          
                    //recordPanel.setCellWidth(label, "200px"); //$NON-NLS-1$

                }    
                verticalPanel.add(recordPanel);
            }
        }
        refreshPageButton();      
        refresh();
    }    
    
    private void refreshPageButton(){
        
        resetPageButtonList();           
       
        if (this.pageCount >= 0 && this.pageCount <=5){ 

            for (int i=4;i>=this.pageCount;i--)
            {
                pageButtonList.get(i).setVisible(false);
            }
            pageToggleButtonIndex = pageNumber-1;          
        }
        else{            
            
            pageSpace1.setVisible(pageNumber<4?false:true);
            pageSpace2.setVisible(pageNumber>this.pageCount-3?false:true); 
            
            if (pageNumber<3){
                pageToggleButtonIndex = pageNumber - 1;
            }   
            else if (pageNumber>this.pageCount-2){            
                pageToggleButtonIndex = 4 + this.pageNumber - pageCount;
                pageButtonList.get(1).setText(String.valueOf(this.pageCount-3));
                pageButtonList.get(2).setText(String.valueOf(this.pageCount-2));              
                pageButtonList.get(3).setText(String.valueOf(this.pageCount-1));
            }
            else {
                pageToggleButtonIndex = 2;
                pageButtonList.get(1).setText(String.valueOf(Integer.valueOf(pageNumber)-1));
                pageButtonList.get(2).setText(String.valueOf(pageNumber));              
                pageButtonList.get(3).setText(String.valueOf(Integer.valueOf(pageNumber)+1));
            }
            pageButtonList.get(4).setText(String.valueOf(this.pageCount));
        }
        if (pageToggleButtonIndex >= 0){
            pageButtonList.get(pageToggleButtonIndex).toggle(true);   
        }             
    }
    
    private void resetPageButtonList(){
        pageSpace1.setVisible(false);
        pageSpace2.setVisible(false);
        
        pageButtonList.get(0).setVisible(true);
        pageButtonList.get(1).setVisible(true);
        pageButtonList.get(2).setVisible(true);
        pageButtonList.get(3).setVisible(true);
        pageButtonList.get(4).setVisible(true);
        
        pageButtonList.get(0).toggle(false);
        pageButtonList.get(1).toggle(false);
        pageButtonList.get(2).toggle(false);
        pageButtonList.get(3).toggle(false);
        pageButtonList.get(4).toggle(false);
        
        pageButtonList.get(0).setText("1"); //$NON-NLS-1$
        pageButtonList.get(1).setText("2"); //$NON-NLS-1$
        pageButtonList.get(2).setText("3"); //$NON-NLS-1$
        pageButtonList.get(3).setText("4"); //$NON-NLS-1$
        pageButtonList.get(4).setText("5"); //$NON-NLS-1$
    }
    
    public void addField(){
        if (this.fields.size() < typeModel.getMaxOccurs()){
            this.add(createField(new ForeignKeyBean()));        
            refreshPage(this.getPageCount());
            this.refresh();   
        }else{
            
        }

    }
    
    public Field<?> createField(Object value)
    {
        Field<?> field = null;
        if (typeModel.getForeignkey() != null) {
            ForeignKeyField foreignKeyField = new ForeignKeyField(typeModel.getForeignkey(), typeModel.getForeignKeyInfo(),
                    ForeignKeyFieldList.this);
            foreignKeyField.setValue((ForeignKeyBean)value);            
            field = foreignKeyField;
//            ((List<ForeignKeyBean>) itemNode.getObjectValue()).add((ForeignKeyBean)value);
            addForeignKeyFieldListener(field, (ForeignKeyBean) value);
        }
        return field;
    }
    
    public void refresh(){
        this.doLayout();
    }
    
    private int getPageCount()
    {
        return (this.fields.size()%this.pageSize==0?this.getFieldList().size()/this.pageSize:this.getFieldList().size()/this.pageSize+1);
    }
    
    private void addForeignKeyFieldListener(final Field<?> field, final ForeignKeyBean foreignKeyBean) {
        
        @SuppressWarnings("unchecked")
        List<ForeignKeyBean> list = (List<ForeignKeyBean>) itemNode.getObjectValue();
        if (list == null){
            list = new ArrayList<ForeignKeyBean>();            
        }
        list.add(foreignKeyBean);            
        
        field.addListener(Events.Change, new Listener<FieldEvent>() {

            public void handleEvent(FieldEvent fe) {
                List<ForeignKeyBean> list = (List<ForeignKeyBean>) itemNode.getObjectValue();
                int index = list.indexOf(foreignKeyBean);
                list.set(index, (ForeignKeyBean) fe.getValue());

                System.out.println("###########################################################");
                for (int i=0;i<list.size();i++){
                    System.out.println(list.get(i));
                }
                System.out.println("###########################################################");
            }
        });
    }
    
    Validator validator = new Validator() {
        
        public String validate(Field<?> field, String value) {
            String valueStr = value == null? "": value.toString();//$NON-NLS-1$
            boolean success = true;
            try{
                int num = Integer.parseInt(valueStr);
                if (num <= 0) {
                    success = false;
                }
            } catch (NumberFormatException e){
                success = false;
            }
            if (!success){
                return MessagesFactory.getMessages().page_size_notice();
            }
            return null;
        }
    };

    public void removeForeignKeyWidget(ForeignKeyBean value) {
        @SuppressWarnings("unchecked")
        List<ForeignKeyBean> fkList = (List<ForeignKeyBean>) this.itemNode.getObjectValue();
        int index = fkList.indexOf(value);
        fkList.remove(index);
        fields.remove(index);    
        refreshPage(this.getPageCount());
    
    }
}

package org.talend.mdm.webapp.browserecords.client.widget.inputfield;

import java.util.Date;

import org.talend.mdm.webapp.base.client.SessionAwareAsyncCallback;
import org.talend.mdm.webapp.browserecords.client.BrowseRecords;
import org.talend.mdm.webapp.browserecords.client.BrowseRecordsServiceAsync;
import org.talend.mdm.webapp.browserecords.client.model.FormatModel;
import org.talend.mdm.webapp.browserecords.client.model.ItemNodeModel;
import org.talend.mdm.webapp.browserecords.client.util.DateUtil;
import org.talend.mdm.webapp.browserecords.client.util.Locale;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.util.Format;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;

public class FormatDateField extends DateField {

    BrowseRecordsServiceAsync service = (BrowseRecordsServiceAsync) Registry.get(BrowseRecords.BROWSERECORDS_SERVICE);

    private String formatPattern;

    private String ojbectValue;

    private String diplayValue;
    
    private boolean showFormateValue;

    private boolean isDateTime = false;
    
    private ItemNodeModel node;
    
    private boolean initFlag = true;
    
    private Date date;

    public FormatDateField() {

    }
    
    public FormatDateField(ItemNodeModel node){
        this.node = node;
    }
    
    public boolean isDateTime() {
        return isDateTime;
    }

    public void setDateTime(boolean isDateTime) {
        this.isDateTime = isDateTime;
    }

    public String getFormatPattern() {
        return formatPattern;
    }

    public void setFormatPattern(String formatPattern) {
        this.formatPattern = formatPattern;
    }

    public String getOjbectValue() {
        return ojbectValue;
    }

    private boolean isDiffValue(String scrOjbectValue, String desOjbectValue){
        if(scrOjbectValue == null && desOjbectValue == null)
            return false;
        if(scrOjbectValue != null && desOjbectValue == null) 
            return true;
        if(scrOjbectValue == null && desOjbectValue != null) 
            return true;
        if(scrOjbectValue.equalsIgnoreCase(desOjbectValue))
            return false;
        else
            return true;
    }
    
    private boolean compareDateAndString(Date date, String ojbectValue){
        if(date == null && ojbectValue == null)
            return false;
        if(date != null && ojbectValue == null)
            return true;
        if(date == null && ojbectValue != null)
            return true;
        // convert date to string according to the DateTimePropertyEditor
        String str = getPropertyEditor().getStringValue(date);
        if(str.equalsIgnoreCase(ojbectValue))
            return false;
        else
            return true;
    }
    
    public void setOjbectValue(String ojbectValue) {
        if(this.node != null){
            if(this.getValue() == null && this.ojbectValue == null && ojbectValue == null){
                this.initFlag = false;
                return;
            }                
            if(this.ojbectValue != null){
                if(this.isDiffValue(this.ojbectValue, ojbectValue)){
                    this.node.setChangeValue(true);
                }               
            }else{
                if(!initFlag && ojbectValue != null)
                    this.node.setChangeValue(true);
                if(this.compareDateAndString(this.getValue(), ojbectValue)){
                    this.node.setChangeValue(true);
                } 
            }
        }
        this.ojbectValue = ojbectValue;                     
    }

    public String getDiplayValue() {
        return diplayValue;
    }

    public void setDiplayValue(String diplayValue) {
        this.diplayValue = diplayValue;
    }
    
    public boolean isShowFormateValue() {
        return showFormateValue;
    }

    
    public void setShowFormateValue(boolean showFormateValue) {
        this.showFormateValue = showFormateValue;
    }

    @Override
    protected boolean validateValue(String value) {
        // TMDM-3487 inputing the date manually
        if (formatPattern != null && formatPattern.trim().length() > 0 && value != null && value.trim().length() > 0) {
            boolean defaultDateFormat = value.contains("-") && value.indexOf("-") == 4; //$NON-NLS-1$ //$NON-NLS-2$
            if (!defaultDateFormat)
                try {
                    Date date = DateUtil.convertStringToDateByFormat(value, formatPattern);
                    value = getPropertyEditor().getStringValue(date);
                } catch (Exception e) {
                    String error = GXT.MESSAGES.dateField_invalidText(value, getPropertyEditor().getFormat().getPattern()
                            .toUpperCase());
                    markInvalid(error);
                    return false;
                }
        }
        if (value.equals(this.getDiplayValue())) {
            return true;
        } else {
            if (!super.validateValue(value)) {
                return false;
            }
            if (value.length() < 1) { // if it's blank and textfield didn't flag it then
                // it's valid
                this.setOjbectValue(null);
                return true;
            }

            DateTimeFormat format = getPropertyEditor().getFormat();

            Date date = null;

            try {

                date = getPropertyEditor().convertStringValue(value);

            } catch (Exception e) {

            }

            if (date == null) {
                String error = null;
                if (getMessages().getInvalidText() != null) {
                    error = Format.substitute(getMessages().getInvalidText(), value, format.getPattern().toUpperCase());
                } else {
                    error = GXT.MESSAGES.dateField_invalidText(value, format.getPattern().toUpperCase());
                }
                markInvalid(error);
                return false;
            }

            if (super.getMinValue() != null && date.before(super.getMinValue())) {
                String error = null;
                if (getMessages().getMinText() != null) {
                    error = Format.substitute(getMessages().getMinText(), format.format(super.getMinValue()));
                } else {
                    error = GXT.MESSAGES.dateField_minText(format.format(super.getMinValue()));
                }
                markInvalid(error);
                return false;
            }
            if (super.getMaxValue() != null && date.after(super.getMaxValue())) {
                String error = null;
                if (getMessages().getMaxText() != null) {
                    error = Format.substitute(getMessages().getMaxText(), format.format(super.getMaxValue()));
                } else {
                    error = GXT.MESSAGES.dateField_maxText(format.format(super.getMaxValue()));
                }
                markInvalid(error);
                return false;
            }

            this.setOjbectValue(value);
            this.date = date;

            return true;
        }
    }

    public void setDate(Date date) {
        this.date = date;
    }
    
    public void setFormatedValue() {
        if (formatPattern != null && date != null) {
            final FormatModel model = new FormatModel(formatPattern, date, Locale.getLanguage());
            DeferredCommand.addCommand(new Command() {

                public void execute() {
                    service.formatValue(model, new SessionAwareAsyncCallback<String>() {

                        public void onSuccess(String result) {
                            setDiplayValue(result);
                            if (isShowFormateValue()) {
                                setRawValue(result);
                            }
                        }
                    });
                }
            });

        }
    }
}

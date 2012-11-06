package org.talend.mdm.webapp.browserecords.client.widget.inputfield;

import java.util.Date;

import org.talend.mdm.webapp.base.client.SessionAwareAsyncCallback;
import org.talend.mdm.webapp.browserecords.client.BrowseRecords;
import org.talend.mdm.webapp.browserecords.client.BrowseRecordsServiceAsync;
import org.talend.mdm.webapp.browserecords.client.model.FormatModel;
import org.talend.mdm.webapp.browserecords.client.util.Locale;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.widget.form.DateField;

public class FormatDateField extends DateField {

    static interface FormatValueCallback {

        void formatValue(String value);
    }

    BrowseRecordsServiceAsync service = (BrowseRecordsServiceAsync) Registry.get(BrowseRecords.BROWSERECORDS_SERVICE);

    private String formatPattern;

    private boolean isDateTime = false;
    
    private boolean validateFlag = true;

    public FormatDateField() {
        super();
        validateFlag = BrowseRecords.getSession().getAppHeader().isAutoValidate();
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
        if (value != null) {
            return propertyEditor.getStringValue(value);
        }
        return ""; //$NON-NLS-1$
    }

    public void setValue(Date value) {
        super.setValue(value);
        setFormatedValue(new FormatValueCallback() {

            public void formatValue(String value) {
                setRawValue(value);
            }
        });
    }

    public String getRawValue() {
        String rawValue = super.getRawValue();
        if (rawValue == null || rawValue.trim().length() == 0) {
            return ""; //$NON-NLS-1$
        }
        try {
            Date d = propertyEditor.convertStringValue(rawValue);
            if (d != null) {
                return rawValue;
            }
            return value == null ? "" : propertyEditor.getStringValue(value); //$NON-NLS-1$
        } catch (Exception e) {
            super.validateValue(rawValue);
            return value == null ? "" : propertyEditor.getStringValue(value); //$NON-NLS-1$
        }
    }

    public void setRawValue(String value) {
        if (formatPattern != null && formatPattern.trim().length() > 0) {
            if (hasFocus) {
                super.setRawValue(this.value == null ? "" : propertyEditor.getStringValue(this.value)); //$NON-NLS-1$);
            } else {
                super.setRawValue(value);
            }
        } else {
            super.setRawValue(value);
        }
    }

    protected void onFocus(ComponentEvent ce) {
        if (formatPattern != null && formatPattern.trim().length() > 0) {
            setRawValue(value == null ? "" : propertyEditor.getStringValue(value)); //$NON-NLS-1$);
        }
        super.onFocus(ce);
    }

    protected void onBlur(ComponentEvent ce) {
        if (formatPattern != null && formatPattern.trim().length() > 0) {
            setFormatedValue(new FormatValueCallback() {

                public void formatValue(String value) {
                    setRawValue(value);
                }
            });
        }
    }
    
    private void setFormatedValue(final FormatValueCallback callback) {
        if (formatPattern != null && formatPattern.trim().length() > 0) {
            if (value != null) {
                final FormatModel model = new FormatModel(formatPattern, value, Locale.getLanguage());
                service.formatValue(model, new SessionAwareAsyncCallback<String>() {
                    public void onSuccess(String result) {
                        if (callback != null) {
                            callback.formatValue(result);
                        }
                    }
                });
            } else {
                if (callback != null) {
                    callback.formatValue(""); //$NON-NLS-1$
                }
            }
        }
    }
    
    public boolean validateValue(String value) {
        if(!validateFlag)
            return true;
        return super.validateValue(value);
    }
    
    public void setValidateFlag(boolean validateFlag) {
        this.validateFlag = validateFlag;
    }
}

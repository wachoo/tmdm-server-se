package org.talend.mdm.webapp.browserecords.client.widget.inputfield;

import org.talend.mdm.webapp.base.client.SessionAwareAsyncCallback;
import org.talend.mdm.webapp.browserecords.client.BrowseRecords;
import org.talend.mdm.webapp.browserecords.client.BrowseRecordsServiceAsync;
import org.talend.mdm.webapp.browserecords.client.model.FormatModel;
import org.talend.mdm.webapp.browserecords.client.util.Locale;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.util.Format;
import com.extjs.gxt.ui.client.widget.form.TextField;

public class FormatTextField extends TextField<String> {

    BrowseRecordsServiceAsync service = (BrowseRecordsServiceAsync) Registry.get(BrowseRecords.BROWSERECORDS_SERVICE);

    private String formatPattern;

    private String ojbectValue;

    private String diplayValue;

    private boolean validateFlag = true;
    
    public FormatTextField() {
        super();
        validateFlag = BrowseRecords.getSession().getAppHeader().isAutoValidate();
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

    public void setOjbectValue(String ojbectValue) {
        this.ojbectValue = ojbectValue;
    }

    public String getDiplayValue() {
        return diplayValue;
    }

    public void setDiplayValue(String diplayValue) {
        this.diplayValue = diplayValue;
    }

    @Override
    public boolean validateValue(String value) {
        if (value.equals(this.getDiplayValue())) {
            this.fireEvent(Events.Change);
            return true;
        } else {
          if(!validateFlag)
              return true;
            boolean result = super.validateValue(value);
            if (!result) {
                return false;
            }
            int length = value.length();
            if (value.length() < 1 || value.equals("")) { //$NON-NLS-1$
                this.setOjbectValue(null);
                if (super.getAllowBlank()) {
                    clearInvalid();
                    return true;
                } else {
                    markInvalid(getMessages().getBlankText());
                    return false;
                }
            }
            if (length < super.getMinLength()) {
                String error = ""; //$NON-NLS-1$
                if (getMessages().getMinLengthText() == null) {
                    error = GXT.MESSAGES.textField_minLengthText(super.getMinLength());
                } else {
                    error = Format.substitute(getMessages().getMinLengthText(), super.getMinLength());
                }
                markInvalid(error);
                return false;
            }

            if (length > super.getMaxLength()) {
                String error = ""; //$NON-NLS-1$
                if (getMessages().getMaxLengthText() == null) {
                    error = GXT.MESSAGES.textField_maxLengthText(super.getMaxLength());
                } else {
                    error = Format.substitute(getMessages().getMaxLengthText(), super.getMaxLength());
                }
                markInvalid(error);
                return false;
            }

            if (validator != null) {
                String msg = validator.validate(this, value);
                if (msg != null) {
                    markInvalid(msg);
                    return false;
                }
            }

            if (super.getRegex() != null && !value.matches(super.getRegex())) {
                markInvalid(getMessages().getRegexText());
                return false;
            }

            this.setOjbectValue(value);

            if (formatPattern != null) {
                FormatModel model = new FormatModel(formatPattern, value, Locale.getLanguage());
                service.formatValue(model, new SessionAwareAsyncCallback<String>() {

                    public void onSuccess(String result) {
                        setDiplayValue(result);
                        setRawValue(result);
                    }
                });
            }

            return true;
        }
    }
    
    public void setValidateFlag(boolean validateFlag) {
        this.validateFlag = validateFlag;
    }

}

package org.talend.mdm.webapp.browserecords.client.widget.inputfield;

import org.talend.mdm.webapp.base.client.SessionAwareAsyncCallback;
import org.talend.mdm.webapp.browserecords.client.BrowseRecords;
import org.talend.mdm.webapp.browserecords.client.BrowseRecordsServiceAsync;
import org.talend.mdm.webapp.browserecords.client.model.FormatModel;
import org.talend.mdm.webapp.browserecords.client.util.Locale;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.util.Format;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.Validator;

public class FormatNumberField extends NumberField {

    BrowseRecordsServiceAsync service = (BrowseRecordsServiceAsync) Registry.get(BrowseRecords.BROWSERECORDS_SERVICE);

    private String formatPattern;

    private Number ojbectValue;

    private String diplayValue;

    private boolean validateFlag = true;
    
    public FormatNumberField() {
        super();
        validateFlag = BrowseRecords.getSession().getAppHeader().isAutoValidate();
    }    
    
    public String getFormatPattern() {
        return formatPattern;
    }

    public void setFormatPattern(String formatPattern) {
        this.formatPattern = formatPattern;
    }

    public Number getOjbectValue() {
        return ojbectValue;
    }

    public void setOjbectValue(Number ojbectValue) {
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
            return true;
        } else {
            if(!validateFlag)
                return true;
            // validator should run after super rules
            Validator tv = validator;
            validator = null;
            if (!super.validateValue(value)) {
                validator = tv;
                return false;
            }
            validator = tv;
            if (value.length() < 1) { // if it's blank and textfield didn't flag it then
                // its valid it's valid
                this.setOjbectValue(null);
                return true;
            }

            String v = value;

            Number d = null;
            try {
                d = getPropertyEditor().convertStringValue(v);
            } catch (Exception e) {
                String error = ""; //$NON-NLS-1$
                if (getMessages().getNanText() == null) {
                    error = GXT.MESSAGES.numberField_nanText(v);
                } else {
                    error = Format.substitute(getMessages().getNanText(), v);
                }
                markInvalid(error);
                return false;
            }
            if (d.doubleValue() < super.getMinValue().doubleValue()) {
                String error = ""; //$NON-NLS-1$
                if (getMessages().getMinText() == null) {
                    error = GXT.MESSAGES.numberField_minText(super.getMinValue().doubleValue());
                } else {
                    error = Format.substitute(getMessages().getMinText(), super.getMinValue());
                }
                markInvalid(error);
                return false;
            }

            if (d.doubleValue() > super.getMaxValue().doubleValue()) {
                String error = ""; //$NON-NLS-1$
                if (getMessages().getMaxText() == null) {
                    error = GXT.MESSAGES.numberField_maxText(super.getMaxValue().doubleValue());
                } else {
                    error = Format.substitute(getMessages().getMaxText(), super.getMaxValue());
                }
                markInvalid(error);
                return false;
            }

            if (!super.getAllowNegative() && d.doubleValue() < 0) {
                markInvalid(getMessages().getNegativeText());
                return false;
            }

            this.setOjbectValue(d);

            if (validator != null) {
                String msg = validator.validate(this, value);
                if (msg != null) {
                    markInvalid(msg);
                    return false;
                }
            }

            if (formatPattern != null) {
                FormatModel model = new FormatModel(formatPattern, d, Locale.getLanguage());
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

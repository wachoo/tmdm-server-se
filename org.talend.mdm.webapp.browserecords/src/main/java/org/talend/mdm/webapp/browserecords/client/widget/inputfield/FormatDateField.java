package org.talend.mdm.webapp.browserecords.client.widget.inputfield;

import java.util.Date;

import org.talend.mdm.webapp.base.client.SessionAwareAsyncCallback;
import org.talend.mdm.webapp.browserecords.client.BrowseRecords;
import org.talend.mdm.webapp.browserecords.client.BrowseRecordsServiceAsync;
import org.talend.mdm.webapp.browserecords.client.model.FormatModel;
import org.talend.mdm.webapp.browserecords.client.util.Locale;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.util.Format;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.google.gwt.i18n.client.DateTimeFormat;

public class FormatDateField extends DateField {

    BrowseRecordsServiceAsync service = (BrowseRecordsServiceAsync) Registry.get(BrowseRecords.BROWSERECORDS_SERVICE);

    private String formatPattern;

    private Date ojbectValue;

    private String diplayValue;

    public String getFormatPattern() {
        return formatPattern;
    }

    public void setFormatPattern(String formatPattern) {
        this.formatPattern = formatPattern;
    }

    public Date getOjbectValue() {
        return ojbectValue;
    }

    public void setOjbectValue(Date ojbectValue) {
        this.ojbectValue = ojbectValue;
    }

    public String getDiplayValue() {
        return diplayValue;
    }

    public void setDiplayValue(String diplayValue) {
        this.diplayValue = diplayValue;
    }

    @Override
    protected boolean validateValue(String value) {
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

            this.setOjbectValue(date);

            if (formatPattern != null) {
                FormatModel model = new FormatModel(formatPattern, date, Locale.getLanguage());
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
}

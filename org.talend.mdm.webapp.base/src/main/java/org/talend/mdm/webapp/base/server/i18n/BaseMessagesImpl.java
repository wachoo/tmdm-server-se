// ============================================================================
//
// Copyright (C) 2006-2012 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.webapp.base.server.i18n;

import java.util.Locale;

import org.talend.mdm.webapp.base.client.i18n.BaseMessages;

import com.amalto.core.util.Messages;
import com.amalto.core.util.MessagesFactory;

@SuppressWarnings("nls")
public final class BaseMessagesImpl implements BaseMessages {

    private static final Messages MESSAGES = MessagesFactory.getMessages("org.talend.mdm.webapp.base.client.i18n.BaseMessages",
            BaseMessagesImpl.class.getClassLoader());

    public String exception_parse_illegalChar(int beginIndex) {
        return MESSAGES.getMessage("exception_parse_illegalChar", beginIndex);
    }

    public String exception_parse_unknownOperator(String value) {
        return MESSAGES.getMessage("exception_parse_unknownOperator", value);
    }

    public String exception_parse_missEndBlock(char endBlock, int i) {
        return MESSAGES.getMessage("exception_parse_missEndBlock", endBlock, i);
    }

    public String exception_parse_tooManyEndBlock(char endBlock, int i) {
        return MESSAGES.getMessage("exception_parse_tooManyEndBlock", endBlock, i);
    }

    public String page_size_label() {
        return MESSAGES.getMessage("page_size_label");
    }

    public String page_size_notice() {
        return MESSAGES.getMessage("page_size_notice");
    }

    public String info_title() {
        return MESSAGES.getMessage("info_title");
    }

    public String error_title() {
        return MESSAGES.getMessage("error_title");
    }

    public String warning_title() {
        return MESSAGES.getMessage("warning_title");
    }

    public String confirm_title() {
        return MESSAGES.getMessage("confirm_title");
    }

    public String unknown_error() {
        return MESSAGES.getMessage("unknown_error");
    }

    public String session_timeout_error() {
        return MESSAGES.getMessage("session_timeout_error");
    }

    public String typemode_notfound_error(String typePath, String language) {
        if (typePath == null)
            typePath = "";
        if (language != null)
            return MESSAGES.getMessage(new Locale(language), "typemode_notfound_error", typePath);
        else
            return MESSAGES.getMessage("typemode_notfound_error", typePath);
    }

    public String typemode_notfound_error(String typePath) {
        return typemode_notfound_error(typePath, null);
    }

    public String open_mls_title() {
        return MESSAGES.getMessage("open_mls_title");
    }

    public String language_title() {
        return MESSAGES.getMessage("language_title");
    }

    public String value_title() {
        return MESSAGES.getMessage("value_title");
    }

    public String multiLanguage_edit_failure() {
        return MESSAGES.getMessage("multiLanguage_edit_failure");
    }

    public String multiLangauge_language_duplicate() {
        return MESSAGES.getMessage("multiLangauge_language_duplicate");
    }

    public String edit_success_info() {
        return MESSAGES.getMessage("edit_success_info");
    }

    public String message_success() {
        return MESSAGES.getMessage("message_success");
    }

    public String message_fail() {
        return MESSAGES.getMessage("message_fail");
    }

    public String edititem() {
        return MESSAGES.getMessage("edititem");
    }

    public String add_btn() {
        return MESSAGES.getMessage("add_btn");
    }

    public String remove_btn() {
        return MESSAGES.getMessage("remove_btn");
    }
    
    public String exception_fk_malform(String fk) {
        return MESSAGES.getMessage("exception_fk_malform",fk);
    }

    public String overwrite_confirm() {
        return MESSAGES.getMessage("overwrite_confirm");
    }
}

// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.webapp.itemsbrowser2.server.i18n;

import org.talend.mdm.webapp.itemsbrowser2.client.i18n.ItemsbrowserMessages;

import com.amalto.core.util.Messages;
import com.amalto.core.util.MessagesFactory;

@SuppressWarnings("nls")
public final class ItemsbrowserMessagesImpl implements ItemsbrowserMessages {

    private static final Messages MESSAGES = MessagesFactory.getMessages(
            "org.talend.mdm.webapp.itemsbrowser2.client.i18n.ItemsbrowserMessages",
            ItemsbrowserMessagesImpl.class.getClassLoader());

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

    public String search_expression() {
        return MESSAGES.getMessage("search_expression");
    }

    public String search_btn() {
        return MESSAGES.getMessage("search_btn");
    }

    public String search_initMsg() {
        return MESSAGES.getMessage("search_initMsg");
    }

    public String advsearch_btn() {
        return MESSAGES.getMessage("advsearch_btn");
    }

    public String cancel_btn() {
        return MESSAGES.getMessage("cancel_btn");
    }

    public String close_btn() {
        return MESSAGES.getMessage("close_btn");
    }

    public String empty_entity() {
        return MESSAGES.getMessage("empty_entity");
    }

    public String required_field() {
        return MESSAGES.getMessage("required_field");
    }

    public String search_expression_notempty() {
        return MESSAGES.getMessage("search_expression_notempty");
    }

    public String openitem_window() {
        return MESSAGES.getMessage("openitem_window");
    }

    public String openitem_tab() {
        return MESSAGES.getMessage("openitem_tab");
    }

    public String edititem() {
        return MESSAGES.getMessage("edititem");
    }

    public String bookmark_heading() {
        return MESSAGES.getMessage("bookmark_heading");
    }

    public String bookmarkmanagement_heading() {
        return MESSAGES.getMessage("bookmarkmanagement_heading");
    }

    public String bookmark_name() {
        return MESSAGES.getMessage("bookmark_name");
    }

    public String bookmark_shared() {
        return MESSAGES.getMessage("bookmark_shared");
    }

    public String bookmark_edit() {
        return MESSAGES.getMessage("bookmark_edit");
    }

    public String bookmark_del() {
        return MESSAGES.getMessage("bookmark_del");
    }

    public String bookmark_update() {
        return MESSAGES.getMessage("bookmark_update");
    }

    public String bookmark_search() {
        return MESSAGES.getMessage("bookmark_search");
    }

    public String yes_btn() {
        return MESSAGES.getMessage("yes_btn");
    }

    public String ok_btn() {
        return MESSAGES.getMessage("ok_btn");
    }

    public String bookmark_DelMsg() {
        return MESSAGES.getMessage("bookmark_DelMsg");
    }

    public String bookmark_existMsg() {
        return MESSAGES.getMessage("bookmark_existMsg");
    }

    public String bookmark_saveFailed() {
        return MESSAGES.getMessage("bookmark_saveFailed");
    }

    public String bookmark_saveSuccess() {
        return MESSAGES.getMessage("bookmark_saveSuccess");
    }

    public String advsearch_filter() {
        return MESSAGES.getMessage("advsearch_filter");
    }

    public String valid_expression() {
        return MESSAGES.getMessage("valid_expression");
    }

    public String invalid_expression() {
        return MESSAGES.getMessage("invalid_expression");
    }

    public String search_tab_name() {
        return MESSAGES.getMessage("search_tab_name");
    }

    public String search_modifiedon() {
        return MESSAGES.getMessage("search_modifiedon");
    }

    public String search_modifiedto() {
        return MESSAGES.getMessage("search_modifiedto");
    }

    public String advsearch_lessinfo() {
        return MESSAGES.getMessage("advsearch_lessinfo");
    }

    public String advsearch_subclause() {
        return MESSAGES.getMessage("advsearch_subclause");
    }

    public String advsearch_bookmark() {
        return MESSAGES.getMessage("advsearch_bookmark");
    }

    public String criteria_CONTAINS() {
        return MESSAGES.getMessage("criteria_CONTAINS");
    }

    public String criteria_EQUALS() {
        return MESSAGES.getMessage("criteria_EQUALS");
    }

    public String criteria_NOT_EQUALS() {
        return MESSAGES.getMessage("criteria_NOT_EQUALS");
    }

    public String criteria_GREATER_THAN() {
        return MESSAGES.getMessage("criteria_GREATER_THAN");
    }

    public String criteria_GREATER_THAN_OR_EQUAL() {
        return MESSAGES.getMessage("criteria_GREATER_THAN_OR_EQUAL");
    }

    public String criteria_LOWER_THAN() {
        return MESSAGES.getMessage("criteria_LOWER_THAN");
    }

    public String criteria_LOWER_THAN_OR_EQUAL() {
        return MESSAGES.getMessage("criteria_LOWER_THAN_OR_EQUAL");
    }

    public String criteria_STARTSWITH() {
        return MESSAGES.getMessage("criteria_STARTSWITH");
    }

    public String criteria_STRICTCONTAINS() {
        return MESSAGES.getMessage("criteria_STRICTCONTAINS");
    }

    public String criteria_FULLTEXTSEARCH() {
        return MESSAGES.getMessage("criteria_FULLTEXTSEARCH");
    }

    public String criteria_DATEEQUALS() {
        return MESSAGES.getMessage("criteria_DATEEQUALS");
    }

    public String criteria_DATELOWER_THAN() {
        return MESSAGES.getMessage("criteria_DATELOWER_THAN");
    }

    public String criteria_DATEGREATER_THAN() {
        return MESSAGES.getMessage("criteria_DATEGREATER_THAN");
    }

    public String criteria_BOOLEQUALSTRUE() {
        return MESSAGES.getMessage("criteria_BOOLEQUALSTRUE");
    }

    public String criteria_BOOLEQUALSFALSE() {
        return MESSAGES.getMessage("criteria_BOOLEQUALSFALSE");
    }

    public String check_totalDigits() {
        return MESSAGES.getMessage("check_totalDigits");
    }

    public String check_fractionDigits() {
        return MESSAGES.getMessage("check_fractionDigits");
    }

    public String check_minInclusive() {
        return MESSAGES.getMessage("check_minInclusive");
    }

    public String check_maxInclusive() {
        return MESSAGES.getMessage("check_maxInclusive");
    }

    public String check_minExclusive() {
        return MESSAGES.getMessage("check_minExclusive");
    }

    public String check_maxExclusive() {
        return MESSAGES.getMessage("check_maxExclusive");
    }

    public String check_length() {
        return MESSAGES.getMessage("check_length");
    }

    public String check_minLength() {
        return MESSAGES.getMessage("check_minLength");
    }

    public String check_maxLength() {
        return MESSAGES.getMessage("check_maxLength");
    }

    public String create_btn() {
        return MESSAGES.getMessage("create_btn");
    }

    public String delete_btn() {
        return MESSAGES.getMessage("delete_btn");
    }

    public String trash_btn() {
        return MESSAGES.getMessage("trash_btn");
    }

    public String save_btn() {
        return MESSAGES.getMessage("save_btn");
    }

    public String save_tip() {
        return MESSAGES.getMessage("save_tip");
    }

    public String savaClose_btn() {
        return MESSAGES.getMessage("savaClose_btn");
    }

    public String saveClose_tip() {
        return MESSAGES.getMessage("saveClose_tip");
    }

    public String duplicate_btn() {
        return MESSAGES.getMessage("duplicate_btn");
    }

    public String duplicate_tip() {
        return MESSAGES.getMessage("duplicate_tip");
    }

    public String journal_btn() {
        return MESSAGES.getMessage("journal_btn");
    }

    public String jouranl_tip() {
        return MESSAGES.getMessage("jouranl_tip");
    }

    public String refresh() {
        return MESSAGES.getMessage("refresh");
    }

    public String page_size_label() {
        return MESSAGES.getMessage("page_size_label");
    }

    public String page_size_notice() {
        return MESSAGES.getMessage("page_size_notice");
    }

    public String save_confirm() {
        return MESSAGES.getMessage("save_confirm");
    }

    public String delete_confirm() {
        return MESSAGES.getMessage("delete_confirm");
    }

    public String path() {
        return MESSAGES.getMessage("path");
    }

    public String path_desc() {
        return MESSAGES.getMessage("path_desc");
    }

    public String fk_RelatedRecord() {
        return MESSAGES.getMessage("fk_RelatedRecord");
    }

    public String loading() {
        return MESSAGES.getMessage("loading");
    }

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

    public String multiOccurrence_minimize(int value) {
        return MESSAGES.getMessage("multiOccurrence_minimize", value);
    }

    public String multiOccurrence_maximize(int value) {
        return MESSAGES.getMessage("multiOccurrence_maximize", value);
    }

    public String message_success() {
        return MESSAGES.getMessage("message_success");
    }

    public String message_fail() {
        return MESSAGES.getMessage("message_fail");
    }

    public String button_upload() {
        return MESSAGES.getMessage("button_upload");
    }

    public String button_reset() {
        return MESSAGES.getMessage("button_reset");
    }

    public String picture_field_title() {
        return MESSAGES.getMessage("picture_field_title");
    }

    public String picture_field_label() {
        return MESSAGES.getMessage("picture_field_label");
    }

    public String url_field_title() {
        return MESSAGES.getMessage("url_field_title");
    }

    public String advsearch_morelabel() {
        return MESSAGES.getMessage("advsearch_morelabel");
    }

    public String save_process_validation_success() {
        return MESSAGES.getMessage("save_process_validation_success");
    }

    public String save_process_validation_failure() {
        return MESSAGES.getMessage("save_process_validation_failure");
    }

    public String delete_process_validation_failure() {
        return MESSAGES.getMessage("delete_process_validation_failure");
    }

    public String delete_record_failure() {
        return MESSAGES.getMessage("delete_record_failure");
    }

    public String delete_item_record_failure(int value) {
        return MESSAGES.getMessage("delete_item_record_failure", value);
    }

    public String delete_record_success() {
        return MESSAGES.getMessage("delete_record_success");
    }

    public String delete_item_record_success(int value) {
        return MESSAGES.getMessage("delete_item_record_success", value);
    }

    public String select_delete_item_record() {
        return MESSAGES.getMessage("select_delete_item_record");
    }

    public String save_record_success() {
        return MESSAGES.getMessage("save_record_success");
    }

    public String data_model_not_specified() {
        return MESSAGES.getMessage("data_model_not_specified");
    }

    public String grid_record_select() {
        return MESSAGES.getMessage("grid_record_select");
    }

    public String itemsBrowser_Import_Export() {
        return MESSAGES.getMessage("itemsBrowser_Import_Export");
    }

    public String itemsBrowser_Import() {
        return MESSAGES.getMessage("itemsBrowser_Import");
    }

    public String page_displaying_records() {
        return MESSAGES.getMessage("page_displaying_records");
    }

    public String label_items_browser() {
        return MESSAGES.getMessage("label_items_browser");
    }

    public String label_combo_select() {
        return MESSAGES.getMessage("label_combo_select");
    }

    public String label_button_upload_data() {
        return MESSAGES.getMessage("label_button_upload_data");
    }

    public String label_combo_filetype_select() {
        return MESSAGES.getMessage("label_combo_filetype_select");
    }

    public String label_field_filetype() {
        return MESSAGES.getMessage("label_field_filetype");
    }

    public String label_field_header_first() {
        return MESSAGES.getMessage("label_field_header_first");
    }

    public String label_field_encoding() {
        return MESSAGES.getMessage("lable_field_encoding");
    }

    public String label_button_submit() {
        return MESSAGES.getMessage("lable_button_submit");
    }

    public String label_field_delete_table() {
        return MESSAGES.getMessage("label_field_delete_table");
    }

    public String message_delete_table() {
        return MESSAGES.getMessage("message_delete_table");
    }

    public String label_button_new_table() {
        return MESSAGES.getMessage("label_button_new_table");
    }

    public String label_field_table_name() {
        return MESSAGES.getMessage("label_field_table_name");
    }

    public String label_update_table() {
        return MESSAGES.getMessage("label_update_table");
    }

    public String label_add_row() {
        return MESSAGES.getMessage("label_add_row");
    }

    public String label_button_export() {
        return MESSAGES.getMessage("label_button_export");
    }

    public String label_exception_xpath_not_match(String xpath, int length) {
        return MESSAGES.getMessage("label_exception_xpath_not_match", xpath, length);
    }

    public String label_error_delete_template_null() {
        return MESSAGES.getMessage("label_error_delete_template_null");
    }

    public String label_exception_id_malform(String id) {
        return MESSAGES.getMessage("label_exception_id_malform", id);
    }

    public String label_exception_upload_table_not_found(String table) {
        return MESSAGES.getMessage("label_exception_upload_table_not_found", table);
    }

    public String label_select_type() {
        return MESSAGES.getMessage("label_select_type");
    }

}

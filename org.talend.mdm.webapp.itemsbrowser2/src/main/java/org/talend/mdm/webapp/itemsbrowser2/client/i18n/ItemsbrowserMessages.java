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
package org.talend.mdm.webapp.itemsbrowser2.client.i18n;

import com.google.gwt.i18n.client.Messages;

public interface ItemsbrowserMessages extends Messages {

    String info_title();

    String error_title();

    String warning_title();

    String confirm_title();

    String search_expression();

    String search_btn();

    String search_initMsg();

    String advsearch_btn();

    String cancel_btn();

    String close_btn();

    String empty_entity();

    String required_field();

    String search_expression_notempty();

    String openitem_window();

    String openitem_tab();

    String edititem();

    String bookmark_heading();

    String bookmarkmanagement_heading();

    String bookmark_name();

    String bookmark_shared();

    String bookmark_edit();

    String bookmark_del();

    String bookmark_update();

    String bookmark_search();

    String yes_btn();

    String ok_btn();

    String bookmark_DelMsg();

    String bookmark_existMsg();

    String bookmark_saveFailed();

    String bookmark_saveSuccess();

    String advsearch_filter();

    String valid_expression();

    String invalid_expression();

    String search_tab_name();

    String search_modifiedon();

    String search_modifiedto();

    String advsearch_lessinfo();

    String advsearch_subclause();

    String advsearch_bookmark();

    String criteria_CONTAINS();

    String criteria_EQUALS();

    String criteria_NOT_EQUALS();

    String criteria_GREATER_THAN();

    String criteria_GREATER_THAN_OR_EQUAL();

    String criteria_LOWER_THAN();

    String criteria_LOWER_THAN_OR_EQUAL();

    String criteria_STARTSWITH();

    String criteria_STRICTCONTAINS();

    String criteria_FULLTEXTSEARCH();

    String criteria_DATEEQUALS();

    String criteria_DATELOWER_THAN();

    String criteria_DATEGREATER_THAN();

    String criteria_BOOLEQUALSTRUE();

    String criteria_BOOLEQUALSFALSE();

    String check_totalDigits();

    String check_fractionDigits();

    String check_minInclusive();

    String check_maxInclusive();

    String check_minExclusive();

    String check_maxExclusive();

    String check_length();

    String check_minLength();

    String check_maxLength();

    String create_btn();

    String delete_btn();

    String trash_btn();

    String save_btn();

    String save_tip();

    String savaClose_btn();

    String saveClose_tip();

    String duplicate_btn();

    String duplicate_tip();

    String journal_btn();

    String jouranl_tip();

    String refresh();

    String page_size_label();

    String page_size_notice();

    String page_displaying_records();

    String save_confirm();

    String delete_confirm();

    String path();

    String path_desc();

    String fk_RelatedRecord();

    String loading();

    String exception_parse_illegalChar(int beginIndex);

    String exception_parse_unknownOperator(String value);

    String exception_parse_missEndBlock(char endBlock, int i);

    String exception_parse_tooManyEndBlock(char endBlock, int i);

    String multiOccurrence_minimize(int value);

    String multiOccurrence_maximize(int value);

    String message_success();

    String message_fail();

    String button_upload();

    String button_reset();

    String picture_field_title();

    String picture_field_label();

    String url_field_title();

    String advsearch_morelabel();

    String save_process_validation_success();

    String save_process_validation_failure();

    String delete_process_validation_failure();

    String delete_record_failure();

    String delete_item_record_failure(int value);

    String delete_record_success();

    String delete_item_record_success(int value);

    String select_delete_item_record();

    String save_record_success();

    String data_model_not_specified();

    String grid_record_select();

    String itemsBrowser_Import_Export();

    String itemsBrowser_Import();

    String label_items_browser();

    String label_combo_select();

    String label_button_upload_data();

    String label_combo_filetype_select();

    String label_field_filetype();

    String label_field_header_first();

    String label_field_encoding();

    String label_button_submit();

    String label_field_delete_table();

    String message_delete_table();

    String label_button_new_table();

    String label_field_table_name();

    String label_update_table();

    String label_add_row();

    String label_button_export();
    
    String label_exception_xpath_not_match(String xpath, int length);

    String label_error_delete_template_null();

    String label_exception_id_malform(String id);

    String label_exception_upload_table_not_found(String table);

    String label_select_type();

    String invalid_tableName();

    String invalid_field(String field);

    String no_key();

    String label_field();

    String label_key();

    String label_add_field();
    
    String import_progress_bar_title();
    
    String import_progress_bar_message();
    
    String import_progress_bar_laod();

    String add_table_duplicated();
    
    String add_table_empty_field();
    
    String error_incompatible_file_type();
    
    String error_column_width();
}

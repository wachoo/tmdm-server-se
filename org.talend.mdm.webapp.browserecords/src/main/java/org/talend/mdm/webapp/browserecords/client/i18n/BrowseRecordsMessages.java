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
package org.talend.mdm.webapp.browserecords.client.i18n;

import com.google.gwt.i18n.client.Messages;


/**
 * DOC Administrator  class global comment. Detailled comment
 */
public interface BrowseRecordsMessages extends Messages {

    String info_title();

    String error_title();

    String confirm_title();

    String warning_title();

    String cancel_btn();

    String ok_btn();

    String create_btn();

    String create(String thing);

    String delete_btn();
    
    String duplicate_btn();
    
    String duplicate_tip();
    
    String journal_btn();
    
    String journal_tip();
    
    String refresh_tip(); 

    String button_reset();

    String save_btn();
    
    String save_tip();

    String button_upload();

    String itemsBrowser_Import_Export();

    String page_size_label();

    String page_size_notice();

    String page_displaying_records();

    String edititem();

    String grid_record_select();

    String trash_btn();

    String path();

    String path_desc();

    String delete_confirm();

    String select_delete_item_record();

    String delete_item_record_failure(int value);

    String delete_item_record_success(int value);

    String delete_item_record_successNoupdate(String value);

    String save_success();

    String save_successEx(String value, String msg);

    String save_fail(String value);

    String save_failEx(String value);

    String empty_entity();

    String loading();

    String search_btn();

    String search_initMsg();

    String advsearch_btn();

    String search_expression();

    String valid_expression();

    String invalid_expression();

    String search_expression_notempty();

    String search_modifiedon();

    String search_modifiedto();

    String advsearch_filter();

    String advsearch_morelabel();

    String advsearch_subclause();

    String advsearch_lessinfo();

    String advsearch_bookmark();

    String required_field();

    String fk_RelatedRecord();

    String label_select_type();

    String exception_parse_illegalChar(int beginIndex);

    String exception_parse_unknownOperator(String value);

    String exception_parse_missEndBlock(char endBlock, int i);

    String exception_parse_tooManyEndBlock(char endBlock, int i);

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

    String bookmark_heading();

    String bookmarkmanagement_heading();

    String bookmark_name();

    String bookmark_shared();

    String bookmark_edit();

    String bookmark_del();

    String bookmark_update();

    String bookmark_search();

    String bookmark_DelMsg();

    String bookmark_existMsg();

    String bookmark_saveFailed();

    String bookmark_saveSuccess();

    String check_totalDigits();

    String check_fractionDigits();

    String check_minInclusive();

    String check_maxInclusive();

    String check_minExclusive();

    String check_maxExclusive();

    String check_length();

    String check_minLength();

    String check_maxLength();

    String check_pattern(String value, String pattern);

    String status();

    String multiOccurrence_minimize(int value);

    String multiOccurrence_maximize(int value);

    String clone_title();

    String deepclone_title();

    String remove_title();

    String picture_field_title();

    String picture_field_label();

    String url_field_title();

    String message_success();

    String message_fail();

    String data_model_not_specified();

    String delete_process_validation_failure();

    String delete_process_validation_success();

    String delete_record_failure();

    String delete_record_success();
    
    String save_close_btn();
    
    String save_close_tip();
    
    String label_field_table_name();
    
    String add_table_duplicated();
    
    String add_table_empty_field();
    
    String add_table_primary_key();
    
    String label_combo_filetype_select();
    
    String label_field_filetype();
    
    String label_field_header_first();
    
    String label_field_encoding();
    
    String label_button_submit();
    
    String error_incompatible_file_type();
    
    String import_progress_bar_title();
    
    String import_progress_bar_message();
    
    String import_progress_bar_laod();
    
    String label_items_browser();
    
    String label_combo_select();
    
    String label_button_upload_data();
    
    String label_button_new_table();
    
    String launch_process_tooltip();

    String fk_integrity_fail_open_relations();
    
    String relations_btn();
    
    String relations_tooltip();

    String fk_integrity_fail_override();

    String msg_confirm_refresh_tree_detail();

    String msg_confirm_save_tree_detail(String entityName);

    String fk_del_title();

    String fk_select_title();

    String fk_open_title();

    String fk_add_title();

    String personalview_btn();

    String generatedview_btn();

    String smartview_defaultoption();

    String print_btn();

    String fk_integrity_list_partial_delete();

    String fk_info();

    String browse_record_title();

    String auto();
    
    String message_validate_title();
    
    String message_validate_min_occurence(int value);
    
    String message_validate_max_occurence(int value);
    
    String open_task();

    String abstract_type();
}

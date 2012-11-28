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
package org.talend.mdm.webapp.stagingareacontrol.client.i18n;

import com.google.gwt.i18n.client.Messages;

public interface StagingareaMessages extends Messages {

    String stagingarea_title();

    String status();

    String current_validation();

    String previous_validation();

    String server_error();

    String server_error_notification();

    String data_container();

    String data_model();

    String refresh();

    String valid();

    String invalid();

    String waiting();

    String nodata();

    String staging_area_title();

    String total_desc(String total);

    String waiting_desc(String waiting);

    String invalid_desc(String prefix, String invalid, String postfix);

    String open_invalid_record();

    String valid_desc(String prefix, String valid, String postfix);

    String start_validation();

    String no_validation();

    String auto_refresh();

    String record_to_process();

    String invalid_record();

    String eta();

    String percentage(int process, int total, double percentage);

    String display_before();

    String search();

    String start_date();

    String end_date();

    String process_records();

    String invalid_records();

    String total_record();

    String on();

    String off();

    String cancel();

    String please_confirm();

    String confirm_message();

    String loading();

    String staging_area_error();

    String staging_area_exception();

    String underlying_cause();
}

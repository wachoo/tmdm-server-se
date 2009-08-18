package com.amalto.workbench.dialogs;

import java.util.Calendar;
import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.vafada.swtcalendar.SWTCalendar;
import org.vafada.swtcalendar.SWTCalendarListener;

public class CalendarDialog  {

	protected Shell shell;
    private SWTCalendar swtcal;
    private Display display;


	/**
	 * @param parentShell
	 */
	public CalendarDialog(Shell parentShell) {
		display =parentShell.getDisplay();
        shell = new Shell(display, SWT.CLOSE);
        shell.setText("Date Picker");
        shell.setLayout(new RowLayout());
        swtcal = new SWTCalendar(shell);
	}

    public void open() {
        shell.pack();
        shell.open();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) display.sleep();
        }
    }
    
    public void close() {
    	shell.dispose();
	}

    public Calendar getCalendar() {
        return swtcal.getCalendar();
    }

    public void setDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        swtcal.setCalendar(calendar);
    }

    public void addDateChangedListener(SWTCalendarListener listener) {
        swtcal.addSWTCalendarListener(listener);
    }

	public Shell getShell() {
		return shell;
	}
	
    

}

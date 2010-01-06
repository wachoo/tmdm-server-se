package talend.ext.service.schedule;
/**
 * 
 */


public class SrvScheduleLogicStatusManager {
	
	public static final String STARTED = "STARTED";
	
	public static final String PAUSED = "PAUSED";
	
	public static final String STOPPED = "STOPPED";

	/** unique instance */
	private static SrvScheduleLogicStatus srvScheduleLogicStatus = null;

	/** 
	 * Private constuctor
	 */
	private SrvScheduleLogicStatusManager() {
		super();
	}

	/** 
	 * Get the unique instance of this class.
	 */
	public static synchronized SrvScheduleLogicStatus getUniqueInstance() {

		if (srvScheduleLogicStatus == null) {
			srvScheduleLogicStatus = new SrvScheduleLogicStatus();
		}

		return srvScheduleLogicStatus;

	}
	
	public static void updateStatus(String status) {
		
		SrvScheduleLogicStatus instance=getUniqueInstance();
		instance.setStatus(status);
		if(status.equals(STARTED)){
			instance.setHasBeenStartedOnce(new Boolean(true));
		}

	}
	
	public static boolean isInitial(){
		Boolean hasBeenStartedOnce=getUniqueInstance().getHasBeenStartedOnce();
		return !hasBeenStartedOnce.booleanValue();
		
	}


}

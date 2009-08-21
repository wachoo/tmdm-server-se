package talend.ext.service.schedule;

public class SrvScheduleLogicStatus {
	
	private String status;
	
	private Boolean hasBeenStartedOnce;
	
	public SrvScheduleLogicStatus() {
		super();
		this.status = SrvScheduleLogicStatusManager.STOPPED;
		this.hasBeenStartedOnce=new Boolean(false);
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Boolean getHasBeenStartedOnce() {
		return hasBeenStartedOnce;
	}

	public void setHasBeenStartedOnce(Boolean hasBeenStartedOnce) {
		this.hasBeenStartedOnce = hasBeenStartedOnce;
	}

}

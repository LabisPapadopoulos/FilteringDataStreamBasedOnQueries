package gr.di.uoa.mde515.grad1353.similarity;

public class TimeType {
	
	private double currentTime;
	private String timeType;

	public TimeType(final double currentTime, final String timeType) {
		this.currentTime = currentTime;
		this.timeType = timeType;
	}

	public double getCurrentTime() {
		return currentTime;
	}
	
	public void setCurrentTime(double currentTime) {
		this.currentTime = currentTime;
	}
	
	public String getTimeType() {
		return timeType;
	}
	
	public void setTimeType(String timeType) {
		this.timeType = timeType;
	}
}

package framework;

public class DurationTracker {
	
	private long startTime;
    private long durationMillis;

    /**
     * The DurationTracker class is used for checking if a certain amount of time has passed
     * since the last occurence of an event.
     * 
     * @param durationMillis The amount of time that needs to pass for the event to be over, in milliseconds.
     */
    public DurationTracker(long durationMillis) {
        this.durationMillis = durationMillis;
        startTime = durationMillis;
    }

    public void start() {
        startTime = System.currentTimeMillis();
    }

    public boolean hasDurationElapsed() {
        long currentTime = System.currentTimeMillis();
        return (currentTime - startTime) >= durationMillis;
    }
    
    public void setDuration(long durationMillis) {
    	this.durationMillis = durationMillis;
    }
    
    public long getDuration() {
    	return durationMillis;
    }
    
    public long getRemainingDuration() {
    	return durationMillis - (System.currentTimeMillis() - startTime);
    }
    
}

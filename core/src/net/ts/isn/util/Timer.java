package net.ts.isn.util;

public class Timer {
	private int duration; // dur�e du timer en ms
	private long timeInitiated; // temps � la mise en route du timer
	private boolean started; // si le timer est en route
	private boolean paused;
	
	public Timer(int duration) {
		this.duration = duration;
		this.started = false;
		this.paused = false;
	}
	
	public void start() {
		this.started = true;
		this.timeInitiated = System.currentTimeMillis();
	}
	
	public void start(int duration) {
		this.setDuration(duration);
		this.start();
	}
	
	public void pause() {
		this.paused = true;
	}
	
	public void resume() {
		this.paused = false;
	}
	
	public void stop() {
		this.started = false;
	}
	
	public void reset() {
		this.timeInitiated = System.currentTimeMillis();
	}
	
	public void setDuration(int duration) {
		this.duration = duration;
	}
	
	/**
	 * v�rifie si la la dur�e est �puis�e
	 * @return retourne vrai si le timer est en route et si delta-t est > � la dur�e du timer
	 */
	public boolean isComplete() {
		if (this.started)
			return (System.currentTimeMillis() - this.timeInitiated >= this.duration);
		else 
			return false;
	}
	
	public int getDuration() {
		return this.duration;
	}
	
	public boolean isPaused() {
		return this.paused;
	}
	
	public boolean isStarted() {
		return this.started;
	}
}

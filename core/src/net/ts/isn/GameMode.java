package net.ts.isn;

public enum GameMode {
	TIME("Time", "Game duration : ", " seconds", 600, 30, 5),
	LIFE("Life", "Stocks : ", "", 10, 1, 1);
	
	public final String name;
	public final String paramName;
	public final String paramSuffix;
	public final int paramMax;
	public final int paramMin;
	public final int paramStep;
	
	private int param;

	private GameMode(String name, String paramName, String paramSuffix, int paramMax, int paramMin, int paramStep) {
		this.name = name;
		this.paramName = paramName;
		this.paramSuffix = paramSuffix;
		this.paramMax = paramMax;
		this.paramMin = paramMin;
		this.paramStep = paramStep;
	}
	
	public GameMode setParam(int param) {
		this.param = param;
		return this;
	}
	
	public int getParam() {
		return this.param;
	}
}

package entpack.bean;

public class BetLimit {
	private String ruleId;
	private int min;
	private int max;

	public BetLimit(String ruleId, int min, int max) {
		this.ruleId = ruleId;
		this.min = min;
		this.max = max;
	}

	public String getRuleId() {
		return ruleId;
	}

	public void setRuleId(String ruleId) {
		this.ruleId = ruleId;
	}

	public int getMin() {
		return min;
	}

	public void setMin(int min) {
		this.min = min;
	}

	public int getMax() {
		return max;
	}

	public void setMax(int max) {
		this.max = max;
	}
}

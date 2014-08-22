package eu.bibl.core.loader.os;


public class CompatibilityRule {
	
	private Action action = Action.ALLOW;
	private OSRestriction os;
	
	public CompatibilityRule() {
	}
	
	public CompatibilityRule(CompatibilityRule compatibilityRule) {
		action = compatibilityRule.action;
		
		if (compatibilityRule.os != null)
			os = new OSRestriction(compatibilityRule.os);
	}
	
	public Action getAppliedAction() {
		if ((os != null) && (!os.isCurrentOperatingSystem()))
			return null;
		
		return action;
	}
	
	public Action getAction() {
		return action;
	}
	
	public OSRestriction getOs() {
		return os;
	}
	
	@Override
	public String toString() {
		return "Rule{action=" + action + ", os=" + os + '}';
	}
}
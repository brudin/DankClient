package eu.bibl.test;

public class Test implements IFace {

	private Test test;

	public void setTest(IFace iface) {
		test = (Test) iface;
	}
}
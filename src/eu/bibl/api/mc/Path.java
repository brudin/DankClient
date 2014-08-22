package eu.bibl.api.mc;

public class Path {
	
	private Location[] blocks;
	
	public Path(Location[] targets) {
		this.blocks = targets;
	}
	
	public void traverse() {
		
	}
	
	public Location[] getBlocks() {
		return blocks;
	}
}
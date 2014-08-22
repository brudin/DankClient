package eu.bibl.api.mod;

public abstract class Mod {
	
	protected final String modKey;
	protected final ModType modType;
	
	public Mod(ModType modType) {
		this.modType = modType;
		modKey = this.getClass().getName();
	}
	
	public final String getModKey() {
		return modKey;
	}
	
	public final ModType getModType() {
		return modType;
	}
}
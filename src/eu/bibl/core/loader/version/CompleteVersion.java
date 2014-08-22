package eu.bibl.core.loader.version;

import java.util.Date;

import eu.bibl.core.loader.rel.ReleaseType;

public abstract interface CompleteVersion extends Version {
	
	@Override
	public abstract String getId();
	
	@Override
	public abstract ReleaseType getType();
	
	@Override
	public abstract Date getUpdatedTime();
	
	@Override
	public abstract Date getReleaseTime();
	
	public abstract int getMinimumLauncherVersion();
	
	public abstract boolean appliesToCurrentEnvironment();
	
	public abstract String getIncompatibilityReason();
	
	public abstract boolean isSynced();
	
	public abstract void setSynced(boolean paramBoolean);
}
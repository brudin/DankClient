package eu.bibl.core.loader.version;

import java.util.Date;

import eu.bibl.core.loader.rel.ReleaseType;

public abstract interface Version {
	
	public abstract String getId();
	
	public abstract ReleaseType getType();
	
	public abstract Date getUpdatedTime();
	
	public abstract Date getReleaseTime();
}
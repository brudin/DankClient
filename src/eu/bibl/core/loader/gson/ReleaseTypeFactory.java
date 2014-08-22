package eu.bibl.core.loader.gson;

import eu.bibl.core.loader.rel.ReleaseType;

/**
 * Interface that can generate release types for given values
 * @param <T> The release type to generate.
 */
public abstract interface ReleaseTypeFactory<T extends ReleaseType> extends Iterable<T> {
	
	public abstract T getTypeByName(String paramString);
	
	public abstract T[] getAllTypes();
	
	public abstract Class<T> getTypeClass();
}
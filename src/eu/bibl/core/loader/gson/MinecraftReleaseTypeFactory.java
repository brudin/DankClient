package eu.bibl.core.loader.gson;

import com.google.common.collect.Iterators;
import eu.bibl.core.loader.rel.MinecraftReleaseType;

import java.util.Iterator;

/**
 * A factory for generating Minecraft release types.
 * @author bibl
 */
public class MinecraftReleaseTypeFactory implements ReleaseTypeFactory<MinecraftReleaseType> {
	
	private static final MinecraftReleaseTypeFactory FACTORY = new MinecraftReleaseTypeFactory();
	
	@Override
	public MinecraftReleaseType getTypeByName(String name) {
		return MinecraftReleaseType.getByName(name);
	}
	
	@Override
	public MinecraftReleaseType[] getAllTypes() {
		return MinecraftReleaseType.values();
	}
	
	@Override
	public Class<MinecraftReleaseType> getTypeClass() {
		return MinecraftReleaseType.class;
	}
	
	@Override
	public Iterator<MinecraftReleaseType> iterator() {
		return Iterators.forArray(MinecraftReleaseType.values());
	}
	
	public static MinecraftReleaseTypeFactory instance() {
		return FACTORY;
	}
}
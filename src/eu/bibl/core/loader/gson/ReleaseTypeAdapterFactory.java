package eu.bibl.core.loader.gson;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import eu.bibl.core.loader.rel.ReleaseType;

import java.io.IOException;

/**
 * Gson stuff
 * @param <T> Type parameter of ReleaseType
 */
public class ReleaseTypeAdapterFactory<T extends ReleaseType> extends TypeAdapter<T> {
	
	private final ReleaseTypeFactory<T> factory;
	
	public ReleaseTypeAdapterFactory(ReleaseTypeFactory<T> factory) {
		this.factory = factory;
	}
	
	@Override
	public void write(JsonWriter out, T value) throws IOException {
		out.value(value.getName());
	}
	
	@Override
	public T read(JsonReader in) throws IOException {
		return this.factory.getTypeByName(in.nextString());
	}
}
package megaminds.actioninventory.serialization.wrappers;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;

public class WrapperAdapterFactory implements TypeAdapterFactory {
	private final TypeAdapterWrapper[] adapters;

	/**
	 * Wrappers are applied in order. If a wrapper potentially ignores previous adapters, it should be listed first.
	 */
	public WrapperAdapterFactory(TypeAdapterWrapper... adapters) {
		this.adapters = adapters!=null ? adapters : new TypeAdapterWrapper[0];
	}
	
	@Override
	public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
		TypeAdapter<T> adapter = gson.getDelegateAdapter(this, type);
		for (int i = 0; i < adapters.length; i++) {
			if (adapter==null) return null;
			adapter = adapters[i].wrapAdapter(adapter, gson, type);
		}
		return adapter;
	}
}
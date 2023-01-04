package ro.aesm.qc.engine.meta;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.osgi.service.component.annotations.Component;

import ro.aesm.qc.api.meta.IMetaCache;
import ro.aesm.qc.api.meta.IMetaResourceModel;

@Component(service = IMetaCache.class)
public class MetaCache implements IMetaCache {

	protected final ConcurrentMap<String, MetaCacheEntry> cache;

	public MetaCache() {
		super();
		this.cache = new ConcurrentHashMap<String, MetaCacheEntry>();
	}

	@Override
	public void put(String key, IMetaResourceModel model) {
		if (!this.cache.containsKey(key)) {
			this.cache.put(key, new MetaCacheEntry(model));
		}
	}

	@Override
	public IMetaResourceModel get(String key) {
		if (this.cache.containsKey(key)) {
			MetaCacheEntry entry = this.cache.get(key);
			if (entry != null) {
				return entry.getModel();
			}
		}
		return null;
	}

	@Override
	public boolean contains(String key) {
		return this.cache.containsKey(key);
	}

	@Override
	public void clearCache() {
		this.cache.clear();
	}

	@Override
	public Set<String> getKeys() {
		return this.cache.keySet();
	}

}

package ro.aesm.qc.engine.meta;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import ro.aesm.qc.api.base.IModuleContentProvider;
import ro.aesm.qc.api.exception.QcResourceException;
import ro.aesm.qc.api.meta.IMetaBuilder;
import ro.aesm.qc.api.meta.IMetaCache;
import ro.aesm.qc.api.meta.IMetaOid;
import ro.aesm.qc.api.meta.IMetaResourceModel;
import ro.aesm.qc.api.meta.IMetaResourceParser;
import ro.aesm.qc.api.meta.component.IMetaComponentCompiler;
import ro.aesm.qc.api.meta.component.IMetaComponentModel;
import ro.aesm.qc.base.AbstractBase;

@Component(service = IMetaBuilder.class)
public class MetaBuilder extends AbstractBase implements IMetaBuilder {

	@Reference
	protected IMetaCache cache;

	@Reference
	protected IMetaResourceParser resourceParser;

	@Reference
	protected List<IMetaComponentCompiler> compilers;

	@Reference
	protected List<IModuleContentProvider> contentProviders;

	protected boolean useCache;

	protected ConcurrentMap<String, Map<String, Object>> compileCache = new ConcurrentHashMap<>();

	// ====================================================
	// ====================== resource ====================
	// ====================================================

	// ------------ load ------------

	@Override
	public InputStream loadResource(String location) {
		InputStream is = null;
		for (IModuleContentProvider cp : this.contentProviders) {
			// String location = this.getLocation(templateName, cp.getTemplatesPathPrefix(),
			// cp.getTemplatesPathSuffix());
			// logger.debug("Looking for template in {}.", location);
			is = cp.getResourceAsStream(location);
			if (is != null) {
				break;
			}
		}
		return is;
	}

	// ------------ parse ------------

	@Override
	public IMetaResourceModel parseResource(String location) throws QcResourceException {
		return this.parseResource(this.loadResource(location));
	}

	@Override
	public IMetaResourceModel parseResource(InputStream inputStream) throws QcResourceException {
		if (inputStream != null) {
			return (IMetaResourceModel) this.resourceParser.parse(inputStream);
		}
		return null;
	}

	// ====================================================
	// ======================== model =====================
	// ====================================================

	// ------------ compile ------------

	@Override
	public Map<String, Object> compile(IMetaOid moid, IMetaComponentModel model) throws QcResourceException {
		Map<String, Object> result = null;
		for (IMetaComponentCompiler compiler : this.compilers) {
			Map<String, Object> cr = compiler.compile(model);
			if (cr != null) {
				if (result == null) {
					result = new HashMap<>();
				}
				result.putAll(cr);
			}
		}
		if (result != null) {
			this.compileCache.put(moid.getId(), result);
		}
		return result;
	}

	@Override
	public Map<String, Object> getCompilationResult(String id) throws QcResourceException {
		IMetaOid moid = new MetaOid(id);
		return this.getCompilationResult(moid);
	}
	
	@Override
	public Map<String, Object> getCompilationResult(IMetaOid moid) throws QcResourceException {
		if (this.useCache) {
			Map<String, Object> result = this.compileCache.get(moid.getId());
			if (result == null) {
				this.buildModel(moid);
				result = this.compileCache.get(moid.getId());
			}
			return result;
		} else {
			this.buildModel(moid);
			return this.compileCache.get(moid.getId());
		}
	}

	// ------------ build ------------

	@Override
	public IMetaComponentModel buildModel(String id) throws QcResourceException {
		IMetaOid moid = new MetaOid(id);
		return this.buildModel(moid);
	}

	@Override
	public IMetaComponentModel buildModel(IMetaOid moid) throws QcResourceException {
		InputStream inputStream = this.loadResource(moid.getLocation());
		IMetaResourceModel rmodel = this.parseResource(inputStream);
		if (this.useCache) {
			this.cache.put(moid.getResource(), rmodel);
		}
		IMetaComponentModel model = rmodel.getModel(moid.getName());
		this.compile(moid, model);
		return model;
	}

	@Override
	public IMetaComponentModel getModel(IMetaOid moid) throws QcResourceException {
		if (this.useCache) {
			IMetaResourceModel rmodel = this.cache.get(moid.getResource());
			if (rmodel == null) {
				return this.buildModel(moid);
			}
			return rmodel.getModel(moid.getName());
		} else {
			return this.buildModel(moid);
		}
	}

	// ====================================================
	// ================== getters / setters ===============
	// ====================================================

//	@Override
//	public IMetaCache getCache() {
////		if (this.cache == null) {
////			this.cache = this.getFactory().getIMetaCache();
////		}
//		return null;// this.cache;
//	}
//
//	@Override
//	public void setCache(IMetaCache cache) {
//		this.cache = cache;
//	}

	@Override
	public boolean isUseCache() {
		return this.useCache;
	}

	@Override
	public void setUseCache(boolean useCache) {
		this.useCache = useCache;
	}

}

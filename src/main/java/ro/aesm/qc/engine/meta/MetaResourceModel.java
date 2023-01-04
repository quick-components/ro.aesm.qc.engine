package ro.aesm.qc.engine.meta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ro.aesm.qc.api.meta.IMetaOid;
import ro.aesm.qc.api.meta.IMetaResourceModel;
import ro.aesm.qc.api.meta.component.IMetaComponentModel;

public class MetaResourceModel implements IMetaResourceModel {

	private List<IMetaComponentModel> modelList = new ArrayList<IMetaComponentModel>();
	private Map<String, IMetaComponentModel> modelMap = new HashMap<String, IMetaComponentModel>();

	public Object getMetaModelComponent(IMetaOid moid) {
		return this.modelMap.get(moid.getName());
	}

	@Override
	public void add(IMetaComponentModel model) {
		this.modelList.add(model);
		this.modelMap.put(model.getName(), model);
	}

	@Override
	public void add(List<IMetaComponentModel> list) {
		for (IMetaComponentModel model : list) {
			this.modelList.add(model);
			this.modelMap.put(model.getName(), model);
		}
	}

	@Override
	public List<IMetaComponentModel> getModelList() {
		return modelList;
	}

	@Override
	public Map<String, IMetaComponentModel> getModelMap() {
		return modelMap;
	}

	@Override
	public IMetaComponentModel getModel(String name) {
		return this.modelMap.get(name);
	}

}

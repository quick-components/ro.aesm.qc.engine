package ro.aesm.qc.engine.meta;

import java.time.LocalDateTime;

import ro.aesm.qc.api.meta.IMetaResourceModel;

public class MetaCacheEntry {
	private IMetaResourceModel model;
	private LocalDateTime createdAt;

	public MetaCacheEntry(IMetaResourceModel model) {
		super();
		this.model = model;
		this.createdAt = LocalDateTime.now();
	}

	public IMetaResourceModel getModel() {
		return model;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
}
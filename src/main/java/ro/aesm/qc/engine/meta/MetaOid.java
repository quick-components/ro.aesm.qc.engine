package ro.aesm.qc.engine.meta;

import java.nio.file.Paths;

import ro.aesm.qc.api.exception.QcResourceException;
import ro.aesm.qc.api.meta.IMetaOid;

public class MetaOid implements IMetaOid {

	private String id;

	private String name;
	// private String alias;

	// private String module;

	public static final String RESOURCE_TYPE_FILE = "file";
	public static final String RESOURCE_TYPE_CPR = "cpr";
	public static final String RESOURCE_TYPE_FQN = "fqn";
	/**
	 * resource represents an:
	 * <li>absolute file path (<code>file</code>) looked up as a file</li>
	 * <li>relative file path (<code>cpr</code>) looked up as a class-path
	 * resource</li>
	 * <li>fully qualified name <code>fqn</code> transformed to a class-path
	 * resource</li>
	 */
	private String resourceType;
	private String resource;
	private String location;

	private boolean isClasspathResource;

	public MetaOid(String id) throws QcResourceException {
		this.id = id;
		this.parseId(id);
	}

	private void parseId(String id) throws QcResourceException {
		String _id = id;
		if (id.startsWith(":")) {
			_id = "file" + _id;
		}

		if (_id.startsWith("file:")) {
			String[] parts = _id.substring(5).split(":");
			if (parts.length != 2) {
				this.invalidId(id);
			}
			this.name = parts[1];
			this.resource = parts[0];
			if (Paths.get(this.resource).isAbsolute()) {
				this.resourceType = RESOURCE_TYPE_FILE;
				this.location = this.resource;
			} else {
				this.resourceType = RESOURCE_TYPE_CPR;
				this.isClasspathResource = true;
				this.location = this.resource; //
			}
			return;
		}

		String[] parts = _id.split(":");
		if (parts.length != 2) {
			this.invalidId(id);
		}
		this.name = parts[1];
		this.resource = parts[0];
		this.resourceType = RESOURCE_TYPE_FQN;
		this.isClasspathResource = true;
		this.location = this.resource.replace(".", "/") + ".xml";
		return;
	}

	private void invalidId(String componentId) throws QcResourceException {
		throw new QcResourceException("Invalid meta component: `" + componentId
				+ "`. Expected formats are: absolute-or-relative-file-path:component or :package-naming-format-fully-qualified-name:component");
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getName() {
		return name;
	}

	public String getResourceType() {
		return this.resourceType;
	}

	@Override
	public String getResource() {
		return this.resource;
	}

	@Override
	public boolean isClasspathResource() {
		return this.isClasspathResource;
	}

	@Override
	public String getLocation() {
		return this.location;
	}

}

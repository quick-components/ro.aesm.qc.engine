package ro.aesm.qc.engine.meta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.w3c.dom.Node;

import ro.aesm.qc.api.Constants;
import ro.aesm.qc.api.exception.QcResourceException;
import ro.aesm.qc.api.meta.IMetaResourceParser;
import ro.aesm.qc.api.meta.component.IMetaComponentParser;
import ro.aesm.qc.base.AbstractXmlResourceParser;

@Component(service = IMetaResourceParser.class)
public class MetaResourceParser extends AbstractXmlResourceParser implements IMetaResourceParser {

	@Reference
	protected List<IMetaComponentParser> parsers;

	public MetaResourceParser() {
		super();
		this.getNamespaceMap().put(Constants.META_XMLNS_ALIAS, Constants.META_XMLNS);
	}

	@Override
	protected MetaResourceModel parse(Node node) throws QcResourceException {
		MetaResourceModel mm = new MetaResourceModel();
		for (IMetaComponentParser parser : this.parsers) {
			mm.add(parser.parseChildrenAsList(node));
		}
		return mm;
	}

	protected List<IMetaComponentParser> getParsers() {
		List<IMetaComponentParser> parsers = new ArrayList<IMetaComponentParser>();
		BundleContext ctx = FrameworkUtil.getBundle(this.getClass()).getBundleContext();
		Collection<ServiceReference<IMetaComponentParser>> srefs;
		try {
			srefs = ctx.getServiceReferences(IMetaComponentParser.class, null);
			for (ServiceReference<IMetaComponentParser> sr : srefs) {
				IMetaComponentParser parser = ctx.getService(sr);
				parsers.add(parser);
			}

		} catch (InvalidSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return parsers;
	}
}

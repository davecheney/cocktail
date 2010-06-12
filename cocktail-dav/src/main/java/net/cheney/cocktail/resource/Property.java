package net.cheney.cocktail.resource;

import static net.cheney.cocktail.resource.Elements.DAV_NAMESPACE;
import net.cheney.snax.model.QName;

public final class Property {
	
	public static final QName GET_LAST_MODIFIED = QName.valueOf(DAV_NAMESPACE, "getlastmodified");
	public static final QName GET_CONTENT_LENGTH = QName.valueOf(DAV_NAMESPACE, "getcontentlength");
	public static final QName RESOURCE_TYPE = QName.valueOf(DAV_NAMESPACE, "resourcetype");
	public static final QName CREATION_DATE = QName.valueOf(DAV_NAMESPACE, "creationdate");
	public static final QName DISPLAY_NAME = QName.valueOf(DAV_NAMESPACE, "displayname");
	
	private Property() { } 
}

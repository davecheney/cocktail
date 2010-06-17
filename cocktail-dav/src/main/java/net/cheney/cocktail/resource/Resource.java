package net.cheney.cocktail.resource;

import java.util.Collection;

import javax.annotation.Nonnull;

import net.cheney.cocktail.message.Request.Method;
import net.cheney.snax.model.Element;
import net.cheney.snax.model.QName;

public interface Resource extends Lockable, Getable, Deletable, CollectionResource, Moveable, Copyable {
	
	enum ComplianceClass { LEVEL_1, LEVEL_2 };

	@Nonnull String name();
	
	boolean exists();
	
	@Nonnull Collection<Method> supportedMethods();

	@Nonnull Collection<ComplianceClass> davOptions();
	
	@Nonnull Element property(QName name);
	
	@Nonnull Iterable<Element> properties();
	
	@Nonnull Resource parent();
	
	boolean hasParent();

	boolean makeCollection(String name);

	@Nonnull boolean isCollection();

}

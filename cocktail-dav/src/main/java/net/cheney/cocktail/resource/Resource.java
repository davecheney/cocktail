package net.cheney.cocktail.resource;

import java.util.Collection;

import javax.annotation.Nonnull;

import net.cheney.cocktail.message.Request.Method;
import net.cheney.snax.model.Element;
import net.cheney.snax.model.QName;

public abstract class Resource implements Lockable, Getable, CollectionResource, Moveable {
	
	public enum ComplianceClass { LEVEL_1, LEVEL_2 };

	@Nonnull public abstract String name();
	
	public abstract boolean exists();
	
	@Nonnull public abstract Collection<Method> supportedMethods();

	@Nonnull public abstract Collection<ComplianceClass> davOptions();
	
	@Nonnull public abstract Element property(QName name);
	
	@Nonnull public abstract Iterable<Element> properties();
	
	@Nonnull public abstract Resource parent();

	@Nonnull public abstract boolean makeCollection(String name);

	@Nonnull public abstract boolean isCollection();

}

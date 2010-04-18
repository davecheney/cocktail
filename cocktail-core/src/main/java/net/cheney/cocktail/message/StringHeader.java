package net.cheney.cocktail.message;

public class StringHeader extends Header<String> {

	private final String name;
	private final Type type;

	public StringHeader(String name, Type type) {
		this.name = name;
		this.type = type;
	}

	@Override
	public Type type() {
		return type;
	}

	@Override
	public String name() {
		return name;
	}

}

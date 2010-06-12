package net.cheney.cocktail.application;

import java.util.Arrays;
import java.util.Iterator;

public abstract class Path implements Comparable<Path>, Iterable<String> {

	public static Path emptyPath() {
		return new Path() {

			@Override
			public int compareTo(Path o) {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public Iterator<String> iterator() {
				return Arrays.asList(new String[0]).iterator();
			} 
			
			@Override
			public String toString() {
				return "/";
			}
		};
	}

	
}

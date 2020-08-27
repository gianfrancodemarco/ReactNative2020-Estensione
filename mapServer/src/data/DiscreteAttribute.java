package data;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

/**
 * Estende la classe Attribute. Modella un attributo di tipo discreto.
 */
public class DiscreteAttribute extends Attribute implements Iterable<String>, Serializable {
	private Set<String> values = new TreeSet<>(); // order by asc

	public DiscreteAttribute(String name, int index, Set<String> values) {
		super(name,index);
		this.values=values;
	}

	public int getNumberOfDistinctValues(){
		return values.size();
	}

	public Set<String> getValues() {
		return values;
	}

	@Override
	public Iterator<String> iterator() {
		// TODO Auto-generated method stub
		return values.iterator();
	}


}

package br.dcc.ufba.themoviefinder.utils;

import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class ItemValue<T> implements Comparable<ItemValue<T>>
{
	public T item;
	public double value;
	public boolean randomEqualCompareTo;
	private double randomValue;
	
	public ItemValue(T item, double value)
	{
		this(item, value, false);
	}
	
	public ItemValue(T item, double value, boolean randomEqualComparison)
	{
		if(item != null) {
			this.item = item;
			this.value = value;
			this.randomEqualCompareTo = randomEqualComparison;
			if(randomEqualComparison) {
				Random random = ThreadLocalRandom.current();
				randomValue = random.nextDouble();
			}
		}		
	}

	@Override
	public int compareTo(ItemValue<T> toCompare) 
	{
		int c = Double.compare(value, toCompare.value);
		if(c == 0 && randomEqualCompareTo) {
			return Double.compare(randomValue, toCompare.getRandomValue());
		}
		return c;
	}
	
	@SuppressWarnings("unchecked")
	@Override
    public boolean equals(Object o) 
    {
        if (this == o) return true;
        if (! this.getClass().equals(o.getClass())) return false;
        ItemValue<T> that = (ItemValue<T>) o;
        if(item != null) {
        	if(item instanceof String) {
        		return ((String) item).equalsIgnoreCase((String) that.item) && value == that.value;
        	} else {
        		return item.equals(that.item) && value == that.value;	
        	}
        }
        return false;
    }
	
    @Override
    public int hashCode() 
    {
    	if(item != null) {
        	if(item instanceof String) {
        		return Objects.hash(item.toString().toLowerCase());
        	} else {
        		return Objects.hash(item);
        	}
    	}
        return Objects.hash(this);
    }
    
	public double getRandomValue() 
	{
		return randomValue;
	}

	@Override
	public String toString() 
	{
		return "ItemValue [item=" + item + ", value=" + value + "]";
	}
}

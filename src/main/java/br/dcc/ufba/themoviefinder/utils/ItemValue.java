package br.dcc.ufba.themoviefinder.utils;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class ItemValue<T> implements Comparable<ItemValue<T>>
{
	public T item;
	public double value;
	public boolean randomEqualCompareTo;
	
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
		}		
	}

	@Override
	public int compareTo(ItemValue<T> toCompare) 
	{
		int c = -Double.compare(value, toCompare.value);
		if(c == 0 && randomEqualCompareTo) {
			Random random = ThreadLocalRandom.current();
			if(random.nextInt(2) == 0) {
				return -1;
			}
			return 1;
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
	public String toString() 
	{
		return "ItemValue [item=" + item + ", value=" + value + "]";
	}	
}

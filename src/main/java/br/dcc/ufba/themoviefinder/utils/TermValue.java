package br.dcc.ufba.themoviefinder.utils;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class TermValue implements Comparable<TermValue>
{
	public String term;
	public double value;
	
	public TermValue(String term, double value)
	{
		if(term != null) {
			this.term = term;
			this.value = value;
		}
	}

	public int compareTo(TermValue toCompare) 
	{
		int c = Double.compare(value, toCompare.value);
		if(c == 0) {
			Random random = ThreadLocalRandom.current();
			if(random.nextInt(2) == 0) {
				return -1;
			}
			return 1;
		}
		return -c;
	}

	@Override
	public String toString() 
	{
		return "TermValue [term=" + term + ", value=" + value + "]";
	}	
}

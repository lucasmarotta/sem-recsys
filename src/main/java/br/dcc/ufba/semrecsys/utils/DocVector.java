package br.dcc.ufba.semrecsys.utils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.math3.linear.OpenMapRealVector;
import org.apache.commons.math3.linear.RealVectorFormat;

public class DocVector
{
	public Map<String, Integer> vectorTerms;
    public OpenMapRealVector vector;
    
    public DocVector(List<String> terms)
    {
    	if(terms != null) {
    		Set<String> uniqueTermsSet = new TreeSet<String>(terms);
    		vectorTerms = new LinkedHashMap<String, Integer>();
    		int index = 0;
    		for (String term : uniqueTermsSet) {
    			if(term != null && term.length() > 0) {
    				vectorTerms.put(term, index);
    				index++;	
    			}
			}
            vector = new OpenMapRealVector(uniqueTermsSet.size());
    	} else {
    		throw new NullPointerException();
    	}
    }

    public void setVectorValue(String term, float value)
    {
        if (vectorTerms.containsKey(term)) {
            vector.setEntry(vectorTerms.get(term), value);
        }
    }

    public void normalize()
    {
        double sum = vector.getL1Norm();
        vector = (OpenMapRealVector) vector.mapDivide(sum);
    }

    @Override
    public String toString()
    {
        RealVectorFormat formatter = new RealVectorFormat();
        return formatter.format(vector);
    }
}

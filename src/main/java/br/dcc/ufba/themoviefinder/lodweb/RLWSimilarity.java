package br.dcc.ufba.themoviefinder.lodweb;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RLWSimilarity 
{
	@Autowired
	private SparqlWalk sparqlWalk;
	
	public double getSimilarity(List<String> terms1, List<String> terms2)
	{
		if(ObjectUtils.allNotNull(terms1, terms2)) {
			if(terms1.equals(terms2)) {
				return 1;
			}
			terms1 = uniqueValues(terms1);
			terms2 = uniqueValues(terms2);
			double qtTerms = terms1.size();
			if(qtTerms > 0) {
				int countDirect = 0, 
					countIndirect = 0, 
					weightDirectCount = 0, 
					weightIndirectCount = 0,
					auxDirect = 0,
					auxIndirect = 0;
				for (String term1 : terms1) {
					auxDirect = sparqlWalk.countDirectLinksBetweenNResources(term1, terms2, true);
					auxIndirect = sparqlWalk.countTotalNumberOfIndirectOutgoingLinksBetweenNResources(term1, terms2, true);
					if(auxDirect > auxIndirect) {
						weightDirectCount++;
					} else {
						weightIndirectCount++;
					}
					countDirect += auxDirect;
					countIndirect += auxIndirect;
				}
				double maxLinks = countDirect + countIndirect;
				if(maxLinks > 0) {
					System.out.println();
					System.out.println(terms1);
					System.out.println(terms2);
					System.out.println("(" + countDirect + "/" + maxLinks + ") * (" + weightDirectCount + "/" + qtTerms + ") + (" + countIndirect + "/" + maxLinks + ") * (" + weightIndirectCount + "/" + qtTerms + ")");
					return (countDirect / maxLinks) * (weightDirectCount / qtTerms) + (countIndirect / maxLinks) * (weightIndirectCount / qtTerms);
				}
				return 0;
			}
		} else {
			throw new NullPointerException("terms1 and terms2 must not be null");
		}
		return 0;
	}
	
	public double getSimilarityWithWeights(List<String> terms1, List<String> terms2, double directWeight, double indirectWeight)
	{
		if(ObjectUtils.allNotNull(terms1, terms2)) {
			if(terms1.equals(terms2)) {
				return 1;
			}
			terms1 = uniqueValues(terms1);
			terms2 = uniqueValues(terms2);
			double qtTerms = terms1.size();
			if(qtTerms > 0) {
				int countDirect = 0, 
					countIndirect = 0;
				for (String term1 : terms1) {
					countDirect += sparqlWalk.countDirectLinksBetweenNResources(term1, terms2, true);
					countIndirect += sparqlWalk.countTotalNumberOfIndirectOutgoingLinksBetweenNResources(term1, terms2, true);
				}
				double maxLinks = countDirect + countIndirect;
				if(maxLinks > 0) {
					System.out.println();
					System.out.println(terms1);
					System.out.println(terms2);
					System.out.println("((" + countDirect + "/" + maxLinks + ") * " + directWeight + " + (" + countIndirect + "/" + maxLinks + ") * " + indirectWeight + ") / (" + directWeight + " + " + indirectWeight + ")");
					return ((countDirect / maxLinks) * directWeight + (countIndirect / maxLinks) * indirectWeight) / (directWeight + indirectWeight);
				}
				return 0;
			}
		} else {
			throw new NullPointerException("terms1 and terms2 must not be null");
		}
		return 0;
	}
	
	/*
	public double getSimilarityWithWeights(List<String> terms1, List<String> terms2, double directWeight, double indirectWeight)
	{
		if(ObjectUtils.allNotNull(terms1, terms2)) {
			if(terms1.equals(terms2)) {
				return 1;
			}
			terms1 = uniqueValues(terms1);
			terms2 = uniqueValues(terms2);
			int countDirect = 0, countIndirect = 0;
			for (String term1 : terms1) {
				countDirect += sparqlWalk.countDirectLinksBetweenNResources(term1, terms2, true);
				countIndirect += sparqlWalk.countTotalNumberOfIndirectOutgoingLinksBetweenNResources(term1, terms2, true);
			}
			double maxLinks = countDirect + countIndirect;
			if(maxLinks > 0 && directWeight + indirectWeight > 0) {
				return ((countDirect / maxLinks) * directWeight + (countIndirect / maxLinks) * indirectWeight) / directWeight + indirectWeight;
			}
			return 0;
		} else {
			throw new NullPointerException("terms1 and terms2 must not be null");
		}
	}*/
	
	private List<String> uniqueValues(List<String> listValues)
	{
		return new ArrayList<String>(new TreeSet<String>(listValues));
	}
}

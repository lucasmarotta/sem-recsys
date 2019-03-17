package br.dcc.ufba.themoviefinder.utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.TFIDFSimilarity;

/**
 * @author Mohamed Guendouz
 */
public class TFIDFCalculator 
{
	private static final TFIDFSimilarity TFIDF_SIMILARITY = new ClassicSimilarity();
	
    /**
     * @param doc  list of strings
     * @param term String represents a term
     * @return term frequency of term in document
     */
    public static double tf(List<String> doc, String term) 
    {
        float result = 0;
        for (String word : doc) {
            if (term.equalsIgnoreCase(word))
                result++;
        }
        //return TFIDF_SIMILARITY.tf(result / doc.size());
        return result / doc.size();
    }

    /**
     * @param docs list of list of strings represents the dataset
     * @param term String represents a term
     * @return the inverse term frequency of term in documents
     */
    public static double idf(List<List<String>> docs, String term) 
    {
        long n = 0;
        for (List<String> doc : docs) {
            for (String word : doc) {
                if (term.equalsIgnoreCase(word)) {
                    n++;
                    break;
                }
            }
        }
        //return TFIDF_SIMILARITY.idf(n, docs.size());
        return Math.log(docs.size() / n);
    }

    /**
     * @param doc  a text document
     * @param docs all documents
     * @param term term
     * @return the TF-IDF of term
     */
    public static double tfIdf(List<String> doc, List<List<String>> docs, String term) 
    {
        return tf(doc, term) * idf(docs, term);
    }
    
    /**
     * Return a list of unique values
     * @param listValues
     * @return
     */
	public static List<String> uniqueValues(List<String> listValues)
	{
		TreeSet<String> treeSet = new TreeSet<String>(new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				return o1.compareToIgnoreCase(o2);
			}
		});
		treeSet.addAll(listValues);
		return new ArrayList<String>(treeSet);
	}
}

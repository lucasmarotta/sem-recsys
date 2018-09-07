package br.dcc.ufba.themoviefinder.utils;

import org.apache.commons.math3.linear.RealVector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CosineSimilarity 
{
	private static final Logger LOGGER = LogManager.getLogger(CosineSimilarity.class);
	
	public static double getSimilarity(DocVector d1, DocVector d2) 
	{
		double cosineSimilarity;
		if(d1 != null && d2 != null) {
			try {
				d1.normalize();
				d2.normalize();
				cosineSimilarity = (((RealVector) d1.vector).dotProduct(d2.vector)) / (d1.vector.getNorm() * d2.vector.getNorm());
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
				return 0.0;
			}	
		} else {
			throw new NullPointerException("d1 and d2 must not be null");
		}
		cosineSimilarity = Math.min(1.0, cosineSimilarity);
		if(Double.compare(Math.min(1.0, cosineSimilarity), Double.NaN) == 0) {
			return 0.0;
		}
		return cosineSimilarity;
	}
}

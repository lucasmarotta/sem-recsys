package br.dcc.ufba.themoviefinder.utils;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import br.dcc.ufba.themoviefinder.entities.models.Movie;

public class MovieSimilarity implements Comparable<MovieSimilarity>
{
	public Movie movie;
	public double similarity;
	
	public MovieSimilarity(Movie movie, double similarity)
	{
		if(movie != null) {
			this.movie = movie;
			this.similarity = similarity;
		}
	}

	public int compareTo(MovieSimilarity toCompare) 
	{
		int c = Double.compare(similarity, toCompare.similarity);
		if(c == 0) {
			Random random = ThreadLocalRandom.current();
			if(random.nextInt(2) == 0) {
				return -1;
			}
			return 1;
			//c = movie.getTitle().compareToIgnoreCase(toCompare.movie.getTitle());
		}
		return -c;
	}	
	
	@Override
	public String toString() 
	{
		return "MovieSimilarity [movie=" + movie.getId() + ", " + movie.getTitle() + ", similarity=" + similarity + "]";
	}
}

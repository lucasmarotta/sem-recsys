package br.dcc.ufba.themoviefinder.utils;

import br.dcc.ufba.themoviefinder.models.Movie;

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
			c = movie.getTitle().compareToIgnoreCase(toCompare.movie.getTitle());
		}
		return -c;
	}	
	
	@Override
	public String toString() 
	{
		return "MovieSimilarity [movie=(" + movie.getId() + ", " + movie.getTitle() + ", similarity=" + similarity + "]";
	}
}

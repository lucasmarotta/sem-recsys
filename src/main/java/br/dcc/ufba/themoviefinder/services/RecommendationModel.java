package br.dcc.ufba.themoviefinder.services;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.dcc.ufba.themoviefinder.entities.models.RecommendationType;
import br.dcc.ufba.themoviefinder.services.similarity.CosineSimilarityService;
import br.dcc.ufba.themoviefinder.services.similarity.RLWSimilarityService;
import br.dcc.ufba.themoviefinder.services.similarity.SimilarityService;

@Component
@Scope("prototype")
public class RecommendationModel implements Serializable
{
	private static final long serialVersionUID = -6491390207547724316L;

	@Autowired
	private ConfigurableApplicationContext springContext;
	
	@Value("${app.recmodel-size: 20}")
	public int recomendationSize = 20;
	
	@Value("${app.recmodel-user-preferences-size: 10}")
	public int userPreferencesSize = 10;
	
	@Value("${app.recmodel-user-model-size: 15}")
	public int userModelSize = 15;
	
	@Value("${app.recmodel-relevance-threshold: 3.5}")
	public double relevanceThreshold = 3.5;
	
	@Value("${app.recmodel-random-equal-order: true}")
	public boolean randomEqualOrder = true;
	
	@Value("${app.recmodel-type: RLWS_DIRECT}")
	public RecommendationType type = RecommendationType.RLWS_DIRECT;
	
	public SimilarityService getServiceByType()
	{
		if(type.equals(RecommendationType.RLWS_DIRECT) || type.equals(RecommendationType.RLWS_INDIRECT)) {
			RLWSimilarityService service = springContext.getBean(RLWSimilarityService.class);
			service.setType(type);
			return service;
		} else {
			return springContext.getBean(CosineSimilarityService.class);
		}		
	}
	
	@Override
	public String toString() 
	{
		return "RecomendationModel [recomendationSize=" + recomendationSize + ", userPreferencesSize="
				+ userPreferencesSize + ", userModelSize=" + userModelSize + ", relevanceThreshold="
				+ relevanceThreshold + ", randomEqualOrder=" + randomEqualOrder + ", type=" + type + "]";
	}

	@Override
	public int hashCode() 
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + (randomEqualOrder ? 1231 : 1237);
		result = prime * result + recomendationSize;
		long temp;
		temp = Double.doubleToLongBits(relevanceThreshold);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((springContext == null) ? 0 : springContext.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + userModelSize;
		result = prime * result + userPreferencesSize;
		return result;
	}

	@Override
	public boolean equals(Object obj) 
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RecommendationModel other = (RecommendationModel) obj;
		if (randomEqualOrder != other.randomEqualOrder)
			return false;
		if (recomendationSize != other.recomendationSize)
			return false;
		if (Double.doubleToLongBits(relevanceThreshold) != Double.doubleToLongBits(other.relevanceThreshold))
			return false;
		if (springContext == null) {
			if (other.springContext != null)
				return false;
		} else if (!springContext.equals(other.springContext))
			return false;
		if (type != other.type)
			return false;
		if (userModelSize != other.userModelSize)
			return false;
		if (userPreferencesSize != other.userPreferencesSize)
			return false;
		return true;
	}
}

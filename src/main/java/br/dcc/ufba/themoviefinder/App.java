package br.dcc.ufba.themoviefinder;

import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import br.dcc.ufba.themoviefinder.controllers.launcher.LauncherContext;
import br.dcc.ufba.themoviefinder.entities.models.LodRelationId;
import br.dcc.ufba.themoviefinder.entities.services.UserService;
import br.dcc.ufba.themoviefinder.lodweb.Sparql;
import br.dcc.ufba.themoviefinder.services.ConsoleServices;
import br.dcc.ufba.themoviefinder.services.RecommendationModel;
import net.codecrafting.springfx.context.ViewStage;
import net.codecrafting.springfx.core.SpringFXApplication;
import net.codecrafting.springfx.core.SpringFXLauncher;

@SpringBootApplication
public class App extends SpringFXApplication
{
	@Autowired
	private ConsoleServices consoleServices;
	
	@Autowired
	private RecommendationModel recModel;
	
	@Autowired
	private UserService userService;
	
	private static final Logger LOGGER = LogManager.getLogger(App.class);
	
	public static void main(String args[]) throws Exception
	{
    	SpringFXLauncher.setRelaunchable(true);
		SpringFXLauncher.launch(new LauncherContext(App.class), args);
	}

	@Override
	public void start(ViewStage viewStage) throws Exception 
	{	
		//Gera tokens através de uma string
		//consoleServices.generateTokens("The quick brown fox jumps over the lazy dog.");
		
		//Gera tokens do filme pelo primeiro título contento
		//consoleServices.generateMovieTokensByTitleLike("The Matrix");
		
		//Gera tokens do filme pelo id
		//consoleServices.generateMovieById(10);
		
		//Atualiza os tokens dos filmes
		//consoleServices.updateMovieTokens();
		
		//Atualiza o cache de IDF dos termos dos filmes
		//consoleServices.updateIdfCache();
		
		//Verifica se duas URIs são redirecionamentos
		//consoleServices.isRedirect(Sparql.wrapStringAsResource("Car"), Sparql.wrapStringAsResource("Automobile"));
		//consoleServices.isRedirect(Sparql.wrapStringAsResource("Tom_Cruise"), Sparql.wrapStringAsResource("Apple"));
		
		//Conta o número de links diretos distintos alcançados pela URI. Sem cache
		//consoleServices.countDirectLinksFromResource(Sparql.wrapStringAsResource("Paris"));
		
		//Conta o número de links indiretos distintos alcançados pela URI. Sem cache
		//consoleServices.countIndirectLinksFromResource(Sparql.wrapStringAsResource("Paris"));
		
		//Conta o número de links diretos entre duas URIs. Sem cache
		//consoleServices.countDirectLinksFrom2Resources(Sparql.wrapStringAsResource("Paris"), Sparql.wrapStringAsResource("France"));
		
		//Conta o número de links indiretos entre duas URIs. Sem cache
		//consoleServices.countIndirectLinksFrom2Resources(Sparql.wrapStringAsResource("Paris"), Sparql.wrapStringAsResource("France"));
		
		//Gera exemplos de comparações com RLWS por RecommendationType (apenas RLWS_DIRECT e RLWS_INDIRECT). Com cache
		//consoleServices.generateRLWSExamplesByType(recModel.type);
		
		//Gera exemplos de comparações com RLWS por pesos. Com cache
		//consoleServices.generateRLWSExamplesByWeights(.0, 1d);
		
		//Gera exemplos de comparações com RLWS por pesos. Com cache
		//consoleServices.generateRLWSExamplesByWeights(.0, 1d);
		
		/*
		//Gera comparações com RLWS por pesos. Com cache
		consoleServices.generateRLWSByLodIds(Arrays.asList(
				new LodRelationId("Luiz_Inácio_Lula_da_Silva", "Curitiba"),
				new LodRelationId("Paraná_(state)", "Curitiba"),
				new LodRelationId("Media", "News_media")
		), .0, 1d);
		*/
		
		/*
		//Gera comparações com RLWS por pesos. Com cache
		consoleServices.generateRLWSByLodIds(Arrays.asList(
				new LodRelationId("Luiz_Inácio_Lula_da_Silva", "Curitiba"),
				new LodRelationId("Paraná_(state)", "Curitiba"),
				new LodRelationId("Media", "News_media")
		), recModel.type);
		*/
		
		//Gera os melhores termos por usuário id
		//consoleServices.getBestTermsByUserId(1, recModel);
		
		//Gera os melhores termos por usuário id
		//consoleServices.getBestTermsByUserEmail("igor.silva@email.com", recModel);
		
		//Gera recomendações por id de usuário
		//consoleServices.generateRecommendationsByUserId(1, recModel);
		
		//Gera recomendações por email de usuário
		//consoleServices.generateRecommendationsByUserEmail("igor.silva@email.com", recModel);
		
		//Gera recomendações por email de usuário
		//consoleServices.generateRecommendationsByUserEmail("igor.silva@email.com", recModel);
		
		//Gera recomendações para os próximos 30 usuários online que não possuem recomendações
		//consoleServices.generateRecomendationsByOnlineUsers(recModel);
		
		//Gera recomendações para os próximos 30 usuários offline que não possuem recomendações
		//consoleServices.generateRecomendationsByOfflineUsers(recModel);
		
		//Gera recomendações por usuários.
		//consoleServices.generateRecommendationsByUsers(userService.getOfflineUsersToRecomendation(recModel.type), recModel);
		
		//Atualiza as recomendações por id de usuário
		//consoleServices.updateRecommendationsByUserId(1, recModel);
		
		//Atualiza as recomendações por email de usuário
		//consoleServices.updateRecommendationsByUserEmail("igor.silva@email.com", recModel);
		
		//Atualiza as recomendações por usuários. Defina runTwice como true caso seja para novos usuários e for do tipo RLWS
		//consoleServices.updateRecommendationsByUsers(userService.getOfflineUsersToRecomendation(recModel.type), recModel, false);
		
		System.exit(0);
	}
}

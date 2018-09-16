package br.dcc.ufba.themoviefinder.launcher.controllers;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import net.codecrafting.springfx.annotation.ViewController;
import net.codecrafting.springfx.context.StageContext;

@ViewController
public class MainController extends StageContext
{
	@FXML
	private AnchorPane contentScreen;
	
	@Override
	public void setViewStageTitle(String title) 
	{
		
	}

	@Override
	public AnchorPane getMainNode() 
	{
		return contentScreen;
	}

	@Override
	protected void onCreate() 
	{
		
	}

	@Override
	protected void onStart() 
	{
		
	}
}

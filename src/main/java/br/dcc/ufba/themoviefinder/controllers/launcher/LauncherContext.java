package br.dcc.ufba.themoviefinder.controllers.launcher;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

import net.codecrafting.springfx.annotation.HeadlessApplication;
import net.codecrafting.springfx.core.SpringFXApplication;
import net.codecrafting.springfx.core.SpringFXContext;
import net.codecrafting.springfx.core.SpringFXContextImpl;
import net.codecrafting.springfx.core.SpringFXLauncher;

/**
 * Implementation class of {@link SpringFXContext} that is used for the initialization of the Spring Boot framework 
 * and as a context for the SpringFX. This is the default implementation used on {@link SpringFXLauncher}
 * 
 * @author Lucas Marotta
 * @see #getSpringContext()
 * @see #getApplication()
 * @see #getEnvironment()
 * @see #run(String[])
 * @see #isHeadless()
 */
public class LauncherContext implements SpringFXContext
{
	/**
	 * The Spring {@link ConfigurableApplicationContext}
	 */
	private ConfigurableApplicationContext springContext;	
	
	/**
	 * The user application
	 */
	private SpringFXApplication application;
	
	/**
	 * The user application class.
	 */
	private Class<? extends SpringFXApplication> appClass;
	private SpringApplicationBuilder springBuilder;
	
	
	/**
	 * Create a new {@link SpringFXContextImpl} instance.
	 * Here will be created a internal {@link SpringApplicationBuilder} to initialize 
	 * and configure a {@link ConfigurableApplicationContext}
	 * @param appClass the user application class
	 */
	public LauncherContext(Class<? extends SpringFXApplication> appClass)
	{
		if(appClass != null) {
			this.appClass = appClass;
			springBuilder = new SpringApplicationBuilder()
					.sources(appClass)
					.web(WebApplicationType.SERVLET)
					.headless(isHeadless());
		} else {
			throw new IllegalArgumentException("Application class must not be null");
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ConfigurableApplicationContext getSpringContext()
	{
		return springContext;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SpringFXApplication getApplication()
	{
		return application;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Environment getEnvironment()
	{
		if(springContext != null) {
			return springContext.getEnvironment();
		}
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void run(String args[])
	{
		if(args != null) {
			springContext = springBuilder.run(args);
			application = springContext.getBean(appClass);	
		} else {
			throw new IllegalArgumentException("Args must not be null");
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stop()
	{
		if(springContext != null) {
			if(springContext.isActive() && springContext.isRunning())
				springContext.stop();	
		}
	}

	/**
	 * Check if the user application have {@link HeadlessApplication} annotation and its value
	 * @return the value of {@link HeadlessApplication} annotation if present.
	 * @defaultValue true
	 */
	public boolean isHeadless() 
	{
		HeadlessApplication headlessAnn = appClass.getAnnotation(HeadlessApplication.class);
		return (headlessAnn != null) ? headlessAnn.value() : true;
	}
}

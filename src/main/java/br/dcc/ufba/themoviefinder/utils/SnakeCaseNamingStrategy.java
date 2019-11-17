package br.dcc.ufba.themoviefinder.utils;

import org.apache.jena.ext.com.google.common.base.CaseFormat;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;

public class SnakeCaseNamingStrategy extends PhysicalNamingStrategyStandardImpl
{
	private static final long serialVersionUID = -4145430811342804998L;

	@Override
	public Identifier toPhysicalTableName(Identifier name, JdbcEnvironment context) 
	{
        return context.getIdentifierHelper()
        		.toIdentifier(CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, name.getText()));
	}

	@Override
	public Identifier toPhysicalColumnName(Identifier name, JdbcEnvironment context) 
	{
        return context.getIdentifierHelper()
        		.toIdentifier(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, name.getText()));
	}	
}

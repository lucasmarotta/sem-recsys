# The Movie Finder

The Movie Finder é o projeto construído para a implementação do **Sistema de Recomendação com Similaridade Semaântica Ponderada por Links na [DBPedia](https://wiki.dbpedia.org)**, apresentado no [meu trabalho de conclusão do curso](https://github.com/lucasmarotta/tcc) da Universidade Federal da Bahia. No projeto é apresentado um sistema de recomendação basedo em conteúdo, utilizando sinopses de filmes para recomendar filmes, além de utilizar a nova similaridade semântica proposta, a RLWS (Resource Link Weighted Similarity).

## Tecnologias do Projeto

Este projeto foi desenvolvido na linguagem JAVA (versão 8), utilizando as seguintes tecnologias

- [Gradle](https://gradle.org), para estrutura de pastas e gerenciador de dependências.
- [Spring Boot](https://spring.io/projects/spring-boot), para construção de aplicações Web, banco de dados com [Hibernate](https://hibernate.org) e controle de inversão.
- [MySQL](https://www.mysql.com) Para solução de banco de dados.
- [Apache Jena](https://jena.apache.org) para construção de consultas SPARQL no DBPedia.
- [Apache OpenNLP](https://opennlp.apache.org) para processamento de linguagem natural

## Estrutura do projeto

```txt
├───data //Contém a estrutura do banco de dados assim com algumas queries essenciais, inclusive queries SPARQL
├───gradle //Versão específica do Gradle Wrapper utilizada
├───src
└───main/java/br/dcc/ufba/themoviefinder //Código fonte principal
│   ├───controllers
│   │   ├───http //Controllers para Web
│   │   └───launcher //Controllers para aplicação offline
│   ├───entities //Modelos e serviços para acesso e controle do Banco de Dados
│   │   ├───models
│   │   ├───repositories
│   │   └───services
│   ├───exception
│   ├───lodweb //Serviços para acesso ao DBPedia e manipulação LODs
│   ├───services
│   │   ├───cache //Serviços para controle da esturtura de cache da aplicação
│   │   └───similarity //Serviços que implementam similaridades utilizadas na aplicação
│   └───utils //Classes Utiliários da aplicação
└───main/resources //Arquivos utilizados pela aplicação
   ├───nlp_models //Modelos pré-treinados NLP
   ├───public //Arquivos públicos para Web
   ├───templates //Templates para construção das views
   ├───application.properties //Arquivo de configuração do projeto
   └───log4j2.xml //Arquivo de configuração de Log (Log4J2)
```

**OBS:** Este projeto foi construído utilizando codificação `UTF-8`, portanto tenha certeza que seu ambiente de execução esteja de acordo.

## Download dos dados

Para baixas os dados do banco de dados, veja os links abaixo:

- [DADOS EM CSV](https://1drv.ms/u/s!Ako5giy76NI4gZNIxPMB4T5xHLrpBw?e=aCai2O)
- [SCHEMA MYSQL](https://1drv.ms/u/s!Ako5giy76NI4gZNJd6o7PTSwSN9aAw?e=BPyv2W)
- [DUMP DO MYSQL](https://1drv.ms/u/s!Ako5giy76NI4gZNK8EwgQ2pjN6orOw?e=euZqJU)

**OBS:** Você também pode ver o schema no arquivo `data\database\schema.sql`.

## Instruções para execução

Caso você prefira utilizar alguma IDE, importe como um projeto Gradle, do contrário utilize os arquivos `gradlew` (Windows ou Bash), e execute o seguinte comando:

```
gradlew bootRun
```

**OBS:** Observe que é necessário ter o JAVA 8 nas variáveis de ambiente do seu sistema.

### Funcionalidades offline

Para facilitar o teste da aplicação foram construídos uma série de serviços para serem executados no console que podem ser conferidos na classe `ConsoleServices`. O objetivo é facilitar a execução de diversas funcionalidades da aplicação, sem a necessidade de uma interface. Na classe principal da aplicação (`App`), conteém exemplos e descrições das funcionalidades abordadas pelos serviços.

### Funcionalidades Web

EM BREVE

## Configurações do projeto

O projeto possui dois arquivos de configuração, o `application.properties` e o `log4j2.xml`, que tratam-se das configurações carregadas pelo framework Spring Boot e Log4J2. Para as configurações do Spring Boot, segue abaixo exemplo das configurações mais importantes:

```properties
#DEFINE O DIALETO DO BANCO DE DADOS, OU SEJA, QUAL A TECNOLOGIA DO BANCO USADA
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL5InnoDBDialect

#ESTRATÉGIA DE TRANSFORMAÇÃO DOS NOMES DAS MODELS PARA OS NOMES UTILIZADO NO SCHEMA DO BANCO. PADRÂO CaseCamel -> snake_case
spring.jpa.hibernate.naming.physical-strategy=br.dcc.ufba.themoviefinder.utils.SnakeCaseNamingStrategy

#STRING DE CONEXÃO JDBC
spring.datasource.url=jdbc:mysql://localhost:3306/the_movie_finder?serverTimezone=UTC

#USUÁRIO DE CONEXÂO DO BANCO
spring.datasource.username=the_movie_finder

#SENHA DE CONEXÃO DO BANCO
spring.datasource.password=yx8e9UZwaQt05Rc8

#SE CONFIGURADO PARA TRUE EXIBE AS CONSULTAS REALIZADAS PELO HIBERNATE
spring.jpa.properties.hibernate.show_sql=true

#SE CONFIGURADO PARA TRUE FORMATA AS CONSULTAS EXIBIDAS PARA MELHOR COMPREEESÂO
spring.jpa.properties.hibernate.format_sql=true

#DEFINE A URL DO SERVIÇO DBPEDIA
app.dbpedia-service-uri=http://dbpedia.org/sparql

#SE CONFIGURADO PARA TRUE EXIBE AS QUERIES SPARQL
app.dbpedia-service-log-queries=true

#DEFINIÇÂO DA QUANTIDADE DE THREADS UTILIZADAS PARA COMPARAR FILMES DURANTE UMA RECOMENDAÇÃO
app.recommendation-batch-size=5

#DEFINIÇÂO DO TAMANHO DA PAGINAÇÂO DE FILMES DURANTE UMA RECOMENDAÇÃO
app.recommendation-batch-movie-size=150

#SE CONFIGURADO UTILIZA A ESTRUTURA DE CACHE PARA RECOMENDAÇÕES RLWS. ALTAMENTE RECOMENDADO
app.rlws-use-cache=true

#DEFINIÇÃO DO NÚMERO DE RECOMENDAÇÔES A SEREM GERADAS
app.recmodel-size=20

#DEFINIÇÂO DA QUANTIDADE MÁXIMA DE FILMES PARA CONSTRUÇÂO DO MODELO DE RECOMENDAÇÂO DO USUÁRIO
app.recmodel-user-preferences-size=10

#DEFINIÇÂO DA QUANTIDADE MÁXIMA DE TERMOS PARA CONSTRUÇÂO DO MODELO DE RECOMENDAÇÂO DO USUÁRIO
app.recmodel-user-model-size=15

#DEFINIÇÂO DO LIMIAR DE RATE DOS ITENS PARA CONSTRUÇÂO DO MODELO DE RECOMENDAÇÂO DO USUÁRIO 
app.recmodel-relevance-threshold=3.5

#SE CONFIGURADO PARA TRUE ESTABELECE QUE SCORES DE RECOMENDAÇÂO IGUAIS UTILIZARÂO COMPARAÇÂO ALEATÓRIA
app.recmodel-random-equal-order=true

#DEFINIÇÂO DO TIPO DE SIMILARIDADE UTILIZADA NA RECOMENDAÇÂO
app.recmodel-type=RLWS_DIRECT
```

Quanto as configurações do Log4J2, é possível definir o nível de LOG, alterando o trecho `level="DEBUG"` em `<Logger name="br.dcc.ufba.themoviefinder" level="DEBUG" additivity="false">` para outro nível. Abaixo consta algumas infomrações configuradas para serem exibidas de acordo com o nível:

- **DEBUG:** Exibe o progresso e comparação de cada filme durante uma recomendação.
- **TRACE:** Além de exibir o DEBUG, adiciona o resultadas das comparações individuais dos termos.
- **INFO:** Para qualquer nível igual a este ou superior, sem informações extras.

Caso queira alterar as configurações JVM durante a excução da aplicação, como memória utilizada, altere o trecho `jvmArgs = ["-Xms128M", "-Xmx448M", "-XX:TieredStopAtLevel=1", "-noverify"]`.

Caso queira também realizar um LOG em arquivos, descomente as linhas `<!-- <AppenderRef ref="FileConsole"/> -->`.

> Feito com :heart: por Lucas Lara Marotta


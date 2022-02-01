# JpaMaven

Atualizando SE em Maven Project

# Sumário

# [Configurando pom.xml](#configurando-pom-xml)
# [Implementando CRUD](#implementando-crud)
# [EntityManager e EntityManagerFactory](#entityManager-e-entityManagerFactory)


## Configurando pom.xml
```java
<project xmlns="http://maven.apache.org/POM/4.0.0"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>
	<groupId>com.dev</groupId>
	<artifactId>JpaMaven</artifactId>
	<version>0.0.1-SNAPSHOT</version>

	<properties>
		<maven.compiler.source>11</maven.compiler.source>
		<maven.compiler.target>11</maven.compiler.target>
	</properties>

	<dependencies>

		<!-- https://mvnrepository.com/artifact/org.hibernate/hibernate-core -->
		<dependency>
			<groupId>org.hibernate</groupId>
			<artifactId>hibernate-core</artifactId>
			<version>5.6.5.Final</version>
		</dependency>

		<!-- https://mvnrepository.com/artifact/org.hibernate/hibernate-entitymanager -->
		<dependency>
			<groupId>org.hibernate</groupId>
			<artifactId>hibernate-entitymanager</artifactId>
			<version>5.6.5.Final</version>
		</dependency>

		<!-- https://mvnrepository.com/artifact/mysql/mysql-connector-java -->
		<dependency>
			<groupId>mysql</groupId>
			<artifactId>mysql-connector-java</artifactId>
			<version>8.0.28</version>
		</dependency>

	</dependencies>

</project>
```
Dentro do pom.xml devemos ter, como propriedade, a versão do Maven e, como dependências, ou seja, importações de bibliotecas extenas, o conector com MySQL e o Hibernate e sua função EntityManager.

Assim que terminamos suas configurações, devemos agora incluir uma pasta META-INF, dentro de resources, com o arquivo persistence.xml.
<strong>Este arquivo será responsável por definir a URL, o drive e o user e password do Banco MySQL</strong><br>No caso estou utilizando Xampp para inicializá-lo.

As configurações são as seguintes:
```java
<?xml version="1.0" encoding="UTF-8"?>
<persistence xmlns="http://xmlns.jcp.org/xml/ns/persistence" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/persistence
    http://xmlns.jcp.org/xml/ns/persistence/persistence_2_1.xsd" version="2.1">

	<persistence-unit name="jpaMavenHibernate" transaction-type="RESOURCE_LOCAL">
		<properties>
			<property name="javax.persistence.jdbc.url" value="jdbc:mysql://localhost/jpatest?useSSL=false&amp;serverTimezone=UTC" />
			<property name="javax.persistence.jdbc.driver" value="com.mysql.jdbc.Driver" />
			<property name="javax.persistence.jdbc.user" value="root" />
			<property name="javax.persistence.jdbc.password" value="" />
			<!-- 
			Gerar automaticamente o DB para nós, value="update" atualiza o banco 
			Pode receber o value="create", assim toda vez que rodar a aplicação, o banco de dados será recriado
			-->
			<property name="hibernate.hbm2ddl.auto" value="update" />

			<!-- https://docs.jboss.org/hibernate/orm/5.4/javadocs/org/hibernate/dialect/package-summary.html -->
			<property name="hibernate.dialect" value="org.hibernate.dialect.MySQL8Dialect" />
		</properties>
	</persistence-unit>
</persistence>
```
O projeto está rodando na versão persistence_2_1.

Assim como Maps, temos uma relação de key/value, porém, neste caso NAME/value. E definimos da seguinte forma:
<!-- URL do SQL, alterando trecho de valor 'localhost' por IP, podemos conectar com um banco online -->
```java
	<property 
	name="javax.persistence.jdbc.url" 
	value="jdbc:mysql://localhost/jpatest?useSSL=false&amp;serverTimezone=UTC" 
	/>
```
Então, temos a tag name, que irá nos dizer qual a property à ser manipulada. Seus valor é referente ao link do banco de dados que instanciaremos, nesse caso basta entrar no localhost:8080, acessar o PhpMyAdmin e "CREATE DATABASE jpatest", para que possamos associar o valor "localhost/jpatest" ao DB.
```java
	<!-- Configuração padrão do drive do mysql-->
	<property name="javax.persistence.jdbc.driver" value="com.mysql.jdbc.Driver" />
	<!-- User e Password do xampp -->
	<property name="javax.persistence.jdbc.user" value="root" />
	<property name="javax.persistence.jdbc.password" value="" />
```
Driver, user e password são padrões, pode ser alterado caso necessário. E, por fim.
```java
	<property name="hibernate.hbm2ddl.auto" value="update" />
```
Esta última propriedade serva para gerar automaticamente o Database, 
O <i>value="update"</i> atualiza o banco. Ele pode receber valores como <i>value="create"</i>, assim toda vez que rodarmos a aplicação, o banco de dados será recriado.

Feitas as configurações, finalmente conseguimos entrar em "contato" com o Banco de Dados proveniente da injeção de dependências.
Agora podemos começar a implementar nossa aplicação.

## Implementando CRUD

Agora, o nosso projeto usará deste Banco para fazer seu CRUD.
Primeiramente, iremos definir um modelo Pessoa, que servirá de tabela.
Começaremos por implementar Serializable e configurar como uma Entidade.
```java
@Entity
public class Pessoa implements Serializable {
```
Agora iremos definir as variáveis que serão utilizadas como colunas no nosso banco, sendo assim:
```java
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private String nome;
	private String email;
```
Os @ significam anotações, que são configurações para identificação. 
O <i>@Id</i> identifica o atributo como Chave-Primária e o <i>@GeneratedValue(strategy = GenerationType.IDENTITY)</i> define um autoincremento para o id.

***Nota:** Poderiamos definir @Column() para configurar o nome da coluna, porém o banco já faz isso para nós, mas, caso queira, segue o exemplo:*
```java
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(name = "nome_completo")
	private String nome;
	private String email;
```
A partir disso, já temos o suficiente para começar a conexão, só fica faltando a implementação do constructor e dos getters e setters, para posterior manipulação em código.
Assim, terminamos a classe Pessoa, como demonstra abaixo.
```java
@Entity
public class Pessoa implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private String nome;
	private String email;

	public Pessoa() {
	}

	public Pessoa(Integer id, String nome, String email) {
		super();
		this.id = id;
		this.nome = nome;
		this.email = email;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String toString() {
		return "Pessoa [id=" + id + ", nome=" + nome + ", email=" + email + "]";
	}

}

```
Ctrl+Shift+O para fazer as importações e Ctrl+Shift+F para organizar o código e PRONTO!

Agora, para manipulação das informações, devemos criar uma nova classe, que receberá o método main  e utilizaremos a classe Pessoa, criando novas instâncias da mesma para que possamos fazer nosso CRUD.
Então, começaremos a instanciação da seguinte forma.
```java
public class Programa {

	public static void main(String[] args) {
		Pessoa p1 = new Pessoa(null, "Lelezinha", "Lele@gmail.com");
		Pessoa p2 = new Pessoa(null, "Joao", "joao@pedro.com.br");
		Pessoa p3 = new Pessoa(null, "Felipe", "fe@lipe.com");
	}

}

```
*O ID consta como nulo pois definimos o autoincremento na classe Pessoa, o que quer dizer que o próprio banco de dados irá definir e adicionar conforme inserção.*

## EntityManager e EntityManagerFactory

Instanciadas as classes, agora iremos definir um EntityManagerFactory para que possamos criar um EntityManager. Essas duas <strong>INTERFACES</strong> serão responsáveis por gerar a <Strong>Fabrica(Factory)</strong> de manipulação e pelo <strong>Entity Management(Manipulação de Entidades)</strong> no banco de dados.

O EntityManagerFactory irá disponibilizar o EntityManager e passa o persistenceUnit, o nome que colocamos no persistence.xml, como parâmetro. Assim que for instanciado e configurado, podemos criar o EntityManager.
```java

	EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpaMavenHibernate");
	EntityManager em = emf.createEntityManager();
```

Para entendermos como funciona o EntityManager, um teste será feito. Nele, iremos buscar a entidade de ID = 2. Para isso usamos o método find do EntityManager, e.g. "em.find()", seus parâmetros necessários são, o Objeto que será procurado e o seu ID. Após isso, demostraremos o dado obtido no terminal, temos:
```java
	Pessoa p = em.find(Pessoa.class, 2);
	System.out.println(p);
```
Antes do EntityManager "em.find()" era necessário fazer todo o trabalho à mão, então era preciso 
<ol>
	<li>Fazer uma pesquisa no DB</li> 
	<li>Converte o dado para objeto</li> 
	<li>Instancia o objeto</li>
</ol>
Isso tudo é evitado com um simples .find() *MAGIC*

Até então, tudo bem. Porém, temos um pequeno empecilho no caminho. Para que possamos fazer um alteração, ou seja, inserção ou deleção. Precisamos definir uma transação e, por fins de segurança, encerrá-la. Toda alteração será inserida entre esse bloco. 
```java
	em.getTransaction().begin();
	em.getTransaction().commit();
	em.close();
	emf.close();
```
<ul>
	<li>.begin() Incia a transação</li> 
	<li>.commit() Confirma as alterações feitas</li> 
	<li.close() encerra comunicação.</li>
</ul>
Enfim, temos um aplicação rodando, com implementação de CRUD em banco de dados e persistência em JPA e Hibernate.


package aplicacao;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import dominio.Pessoa;

/* 
 * The projects is made with the concept of practice with 
 * Maven, Hibernate and JPA
 * First step: Configure pom.xml
 * Second step: To manage persistence.xml, We need to create a META-INF folder in resources
 * Inside META-INF, We create the persistence.xml file
 * Open with -> Generic text editor Then
 * Configure persistence.xml
 */

public class Programa {

	public static void main(String[] args) {
		/* DB atribui ID automaticamente e com autoincremento */
		Pessoa p1 = new Pessoa(null, "Lelezinha", "Lele@gmail.com");
		Pessoa p2 = new Pessoa(null, "Joao", "joao@pedro.com.br");
		Pessoa p3 = new Pessoa(null, "Felipe", "fe@lipe.com");

		/*
		 * Disponibiliza o EntityManager e passa o persistenceUnit, o nome que colocamos
		 * no persistence.xml
		 */
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpaMavenHibernate");
		/*
		 * EntityManagerFactory instanciado e configurado, aí sim podemos criar um
		 * EntityManager
		 */
		EntityManager em = emf.createEntityManager();

		/*
		 * Faz uma pesquisa no DB, Converte o dado para objeto, Instancia o objeto
		 */
		Pessoa p = em.find(Pessoa.class, 2);
		System.out.println(p);
		/*
		 * Remoção no banco inválida, pois a instância está detached Para remoção bem
		 * sucedida. É obrigatória a monitoração do objeto
		 * 
		 * Entidade monitorada = Entidade inserida em execução ou recuperada(find) do
		 * banco de dados
		 * 
		 * Jeito errado: Pessoa pRemove = new Pessoa(2, null, null); em.remove(pRemove);
		 * Jeito certo:
		 */
		/*
		 * Inicia a transação com o Banco de Dados Todo procedimento que não seja
		 * consulta necessita de uma transação
		 */
		em.getTransaction().begin();

		Pessoa pRemove = em.find(Pessoa.class, 2);
		em.remove(pRemove);

		/* Implementamos a persistencia para inserção */
//		em.persist(p1);
//		em.persist(p2);
//		em.persist(p3);

		/* Confirma as alterações feitas */
		em.getTransaction().commit();
		System.out.println(
				"Run Programa.java as Java Application e... \nPronto!\nInserção no Banco feita com sucesso!\nAcesse o localhost PhpMyAdmin, inicializado pelo Xampp e verá a tabela criada!");
		em.close();
		emf.close();
	}

}

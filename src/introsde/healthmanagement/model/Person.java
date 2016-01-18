package introsde.healthmanagement.model;

import introsde.healthmanagement.dao.LifeCoachDao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@Entity
@Table(name="Person")
@NamedQuery(name="Person.findAll", query="SELECT p FROM Person p")
@XmlType (propOrder={"personId", "firstname", "lastname", "birthdate", "access_token", "username", "email", "measure"})
@XmlRootElement(name = "People")
public class Person implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(generator="sqlite_person")
	@TableGenerator(name="sqlite_person", table="sqlite_sequence",
	    pkColumnName="name", valueColumnName="seq",
	    pkColumnValue="Person")
	@Column(name="personId")
	private Long personId;

	@Column(name="lastname")
	private String lastname;

	@Column(name="firstname")
	private String firstname;
	
	@Column(name="birthdate")
	private String birthdate;
	
	@Column(name="access_token")
	private String access_token;
	
	@Column(name="username")
	private String username;
	
	@Column(name="email")
	private String email;


	@OneToMany
	@JoinTable(name="personMeasure")
	private List<Measure> measure;
	
	@OneToMany
	@JoinTable(name="personMeasureHistory")
	private List<Measure> healthHistory;
	
	public Person() {
	}
	
	public Long getPersonId() {
		return personId;
	}

	public String getLastname() {
		return lastname;
	}

	public String getFirstname() {
		return firstname;
	}

	public String getBirthdate() {
		return birthdate;
	}

	@XmlElementWrapper(name = "currentHealth")
	public List<Measure> getMeasure() {
		return measure;
	}

	@XmlTransient
	public List<Measure> getHealthHistory() {
		return healthHistory;
	}

	public void setPersonId(Long personId) {
		this.personId = personId;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public void setBirthdate(String birthdate) {
		this.birthdate = birthdate;
	}

	public void setMeasure(List<Measure> measure) {
		this.measure = measure;
	}
	
	public String getAccess_token() {
		return access_token;
	}

	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setHealthHistory(List<Measure> healthHistory) {
		this.healthHistory = healthHistory;
	}

	public static Person getPersonById(Long personId) {
		EntityManager em = LifeCoachDao.instance.createEntityManager();
		Person p = em.find(Person.class, personId);
		LifeCoachDao.instance.closeConnections(em);
		return p;
	}
	
	public static List<Person> getAll() {
		EntityManager em = LifeCoachDao.instance.createEntityManager();
	    List<Person> list = em.createNamedQuery("Person.findAll", Person.class).getResultList();
	    LifeCoachDao.instance.closeConnections(em);
	    return list;
	}
	
	public static Person savePerson(Person p) {
		EntityManager em = LifeCoachDao.instance.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		if(p.getMeasure() != null && p.getMeasure().size() > 0) {
			for(Measure m : p.getMeasure()) {
				em.persist(m);
			}
		}
		em.persist(p);
		tx.commit();
	    LifeCoachDao.instance.closeConnections(em);
	    return p;
	}
	
	public static Measure saveMeasure(String type, Long value, String valuetype, Long id) {
		Measure m = new Measure();
		m.setMeasureType(type);
		m.setMeasureValue(value);
		m.setMeasureValueType(valuetype);
		
		EntityManager em = LifeCoachDao.instance.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		Person p = em.find(Person.class, id);
		em.persist(m);
		Measure old = null;
		for(Measure temp : p.getMeasure()) {
			if(temp.getMeasureType().equals(m.getMeasureType())) {
				old = temp;
			}
		}
		p.getMeasure().remove(old);
		p.getMeasure().add(m);
		p.getHealthHistory().add(old);
		tx.commit();
	    LifeCoachDao.instance.closeConnections(em);
	    return m;
	}
	
	public static Person updatePerson(Person p) {
		EntityManager em = LifeCoachDao.instance.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		if(p.getFirstname() != null) {
			em.createQuery("UPDATE Person p SET p.firstname = ?1 WHERE p.personId LIKE ?2")
					.setParameter(1, p.getFirstname())
					.setParameter(2, p.getPersonId())
					.executeUpdate();
		}
		if(p.getLastname() != null) {
		em.createQuery("UPDATE Person p SET p.lastname = ?1 WHERE p.personId LIKE ?2")
				.setParameter(1, p.getLastname())
				.setParameter(2, p.getPersonId())
				.executeUpdate();
		}
		if(p.getBirthdate() != null) {
		em.createQuery("UPDATE Person p SET p.birthdate = ?1 WHERE p.personId LIKE ?2")
				.setParameter(1, p.getBirthdate())
				.setParameter(2, p.getPersonId())
				.executeUpdate();
		}
		if(p.getAccess_token() != null) {
			em.createQuery("UPDATE Person p SET p.access_token = ?1 WHERE p.personId LIKE ?2")
					.setParameter(1, p.getAccess_token())
					.setParameter(2, p.getPersonId())
					.executeUpdate();
		}
		tx.commit();
	    LifeCoachDao.instance.closeConnections(em);
	    return getPersonById(p.getPersonId());
	}
	
	public static void removePerson(Person p) {
		EntityManager em = LifeCoachDao.instance.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
	    p=em.merge(p);
	    em.remove(p);
	    tx.commit();
	    LifeCoachDao.instance.closeConnections(em);
	}
	
	public static List<Measure> readPersonHistory(Long personId, String measureType) {
		EntityManager em = LifeCoachDao.instance.createEntityManager();
		Person p = em.find(Person.class, personId);
		LifeCoachDao.instance.closeConnections(em);
		List<Measure> lm = new ArrayList<Measure>();
		for(Measure temp : p.getHealthHistory()) {
			if(temp.getMeasureType().equals(measureType)) lm.add(temp);
		} 
		for(Measure temp : p.getMeasure()) {
			if(temp.getMeasureType().equals(measureType)) lm.add(temp);
		} 
		return lm;
	}
	
	public static List<Person> readPersonListByMeasurement(String measureType,
			String maxValue, String minValue) {
		List<Person> lp = Person.getAll();
		List<Person> lpOk = new ArrayList<Person>();
		Boolean isOut = false;
		for(Person p : lp) {
			for(Measure m : Person.readPersonHistory(p.getPersonId(), measureType)) {
				if(minValue != null && m.getMeasureValue() < Float.parseFloat(minValue)) {
					isOut = true;
				}
				if(maxValue != null && m.getMeasureValue() > Float.parseFloat(maxValue)) {
					isOut = true;
				}
			}
			if(!isOut) lpOk.add(p);
		}
		return lpOk;
	}
}

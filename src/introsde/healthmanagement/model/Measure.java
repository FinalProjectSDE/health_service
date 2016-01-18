package introsde.healthmanagement.model;

import introsde.healthmanagement.dao.LifeCoachDao;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@Entity
@Table(name="Measure")
@NamedQuery(name="Measure.findAll", query="SELECT m FROM Measure m")
@XmlType (propOrder={"mid", "dateRegistered", "measureType", "measureValue", "measureValueType"})
@XmlRootElement(name="measure")
public class Measure implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(generator="sqlite_measuredef")
	@TableGenerator(name="sqlite_measuredef", table="sqlite_sequence",
	    pkColumnName="name", valueColumnName="seq",
	    pkColumnValue="Measure")
	@Column(name="idMeasureDef")
	private Long mid;
	
	@Column(name="dateRegistered")
	private String dateRegistered;
	
	@Column(name="measureType")
	private String measureType;
	
	@Column(name="measureValue")
	private float measureValue;
	
	@Column(name="measureValueType")
	private String measureValueType;
	
	public Measure() {
		this.dateRegistered = getCurrentTimeStamp();
	}

	public Long getMid() {
		return mid;
	}

	public String getDateRegistered() {
		return dateRegistered;
	}

	public String getMeasureType() {
		return measureType;
	}

	public float getMeasureValue() {
		return measureValue;
	}

	public String getMeasureValueType() {
		return measureValueType;
	}

	public void setMid(Long mid) {
		this.mid = mid;
	}

	public void setDateRegistered(String dateRegistered) {
		this.dateRegistered = dateRegistered;
	}

	public void setMeasureType(String measureType) {
		this.measureType = measureType;
	}

	public void setMeasureValue(float measureValue) {
		this.measureValue = measureValue;
	}

	public void setMeasureValueType(String measureValueType) {
		this.measureValueType = measureValueType;
	}

	
	public static Measure readPersonMeasurement(Long id, String measureType, Long mid) {
		EntityManager em = LifeCoachDao.instance.createEntityManager();
		Measure m = null;
		for(Measure temp : Person.readPersonHistory(id, measureType)) {
			if(temp.getMid().equals(mid)) m = temp;
		}
		LifeCoachDao.instance.closeConnections(em);
		return m;
	}
	
	public static List<Measure> getAll() {
		EntityManager em = LifeCoachDao.instance.createEntityManager();
	    List<Measure> list = em.createNamedQuery("Measure.findAll", Measure.class).getResultList();
	    LifeCoachDao.instance.closeConnections(em);
	    return list;
	}
	
	public static Measure savePersonMeasurement(Measure m, Long id) {
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
	
	public static Measure updateMeasureDefinition(Long id, Measure m) {
		EntityManager em = LifeCoachDao.instance.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		Person p = em.find(Person.class, id);
		List<Measure> lm = new ArrayList<Measure>();
		lm.addAll(p.getHealthHistory());
		lm.addAll(p.getMeasure());
		tx.begin();
		Measure result = null;
		for(Measure temp : lm) {
			if(m.getMid().equals(temp.getMid())) result=em.merge(m);
		}
		tx.commit();
	    LifeCoachDao.instance.closeConnections(em);
	    return result;
	}
	
	public static void removeMeasureDefinition(Measure p) {
		EntityManager em = LifeCoachDao.instance.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
	    p=em.merge(p);
	    em.remove(p);
	    tx.commit();
	    LifeCoachDao.instance.closeConnections(em);
	}
	
	public static String getCurrentTimeStamp() {
		Date now = new Date();
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strDate = sdfDate.format(now);
        return strDate;
    }
	
	public static List<String> readMeasureTypes() {
		List<Measure> lm = Measure.getAll();
		Set<String> ss = new TreeSet<String> ();
		for(Measure temp : lm) {
			ss.add(temp.getMeasureType());
		}
		return new ArrayList<String>(ss);
	}
}

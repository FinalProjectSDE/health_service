package introsde.healthmanagement.soap;

import introsde.healthmanagement.model.Measure;
import introsde.healthmanagement.model.Person;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.jws.WebService;

@WebService(endpointInterface = "introsde.healthmanagement.soap.People",
	serviceName="PeopleService")
public class PeopleImpl implements People {

	@Override
	public Person readPerson(Long id) {
		System.out.println("---> Read person by his ID = "+id);
		Person p = Person.getPersonById(id);
		if (p!=null) {
			System.out.println("---> Found Person by id = "+id+" ===> "+p.getFirstname());
		} else {
			System.out.println("---> No person with id = "+id);
		}
		return p;
	}

	@Override
	public List<Person> readPersonList() {
		return Person.getAll();
	}

	@Override
	public Person createPerson(Person person) {
		Person.savePerson(person);
		return person;
	}

	@Override
	public Person updatePerson(Person person) {
		Person.updatePerson(person);
		return Person.getPersonById(person.getPersonId());
	}

	@Override
	public Long deletePerson(Long id) {
		Person p = Person.getPersonById(id);
		if (p!=null) {
			Person.removePerson(p);
			return new Long(0);
		} else {
			return new Long(-1);
		}
	}

	@Override
	public Measure savePersonMeasurement(String type, Long value, String valuetype, Long id) {
		Measure m = Person.saveMeasure( type,  value,  valuetype,  id);
		return m;
	}

	@Override
	public List<Measure> readPersonHistory(Long id, String measureType) {
		return Person.readPersonHistory(id, measureType);
	}

	@Override
	public Measure readPersonMeasurement(Long id, String measureType, Long mid) {
		return Measure.readPersonMeasurement(id, measureType, mid);
	}

	@Override
	public List<String> readMeasureTypes() {
		return Measure.readMeasureTypes();
	}

	@Override
	public Measure updatePersonMeasure(Long id, Measure m) {
		return Measure.updateMeasureDefinition(id, m);
	}

	@Override
	public List<Measure> readPersonMeasureByDates(Long id, String measureType,
			Date before, Date after) throws ParseException {
		List<Measure> ml = Person.readPersonHistory(id, measureType);
		List<Measure> mlByDates = new ArrayList<Measure>();
		System.out.println(before);
		System.out.println(after);
		System.out.println("New date: "+new Date());
		for(Measure temp : ml) {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date tempDate = formatter.parse(temp.getDateRegistered());
			if(tempDate.before(before) && tempDate.after(after)) mlByDates.add(temp);
		} 
		return mlByDates;
	}

	@Override
	public List<Person> readPersonListByMeasurement(String measureType,
			String maxValue, String minValue) {
		return Person.readPersonListByMeasurement(measureType, maxValue, minValue);
	}

}

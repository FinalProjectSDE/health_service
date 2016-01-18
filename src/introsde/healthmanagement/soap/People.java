package introsde.healthmanagement.soap;
import introsde.healthmanagement.model.Measure;
import introsde.healthmanagement.model.Person;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;

@WebService
@SOAPBinding(style = Style.DOCUMENT, use=Use.LITERAL) //optional
public interface People {
    @WebMethod(operationName="readPerson")
    @WebResult(name="person") 
    public Person readPerson(@WebParam(name="personId") Long id);
 
    @WebMethod(operationName="readPersonList")
    @WebResult(name="person")
    public List<Person> readPersonList();
 
    @WebMethod(operationName="createPerson")
    @WebResult(name="person") 
    public Person createPerson(@WebParam(name="person") Person person);
 
    @WebMethod(operationName="updatePerson")
    @WebResult(name="person") 
    public Person updatePerson(@WebParam(name="person") Person person);
    
    @WebMethod(operationName="deletePerson")
    @WebResult(name="personId") 
    public Long deletePerson(@WebParam(name="personId") Long id);

    @WebMethod(operationName="savePersonMeasurement")
    @WebResult(name="measure") 
    public Measure savePersonMeasurement(@WebParam(name="type") String type, @WebParam(name="value") Long value, @WebParam(name="valuetype") String valuetype, @WebParam(name="personId") Long id);
    
    @WebMethod(operationName="readPersonHistory")
    @WebResult(name="measure") 
    public List<Measure> readPersonHistory(@WebParam(name="personId") Long id, @WebParam(name="measureType") String measureType);
    
    @WebMethod(operationName="readPersonMeasurement")
    @WebResult(name="measure")
    public Measure readPersonMeasurement(@WebParam(name="personId") Long id, @WebParam(name="measureType") String measureType, @WebParam(name="mid") Long mid);
    
    @WebMethod(operationName="readMeasureTypes")
    @WebResult(name="measureType")
    public List<String> readMeasureTypes();
    
    @WebMethod(operationName="updatePersonMeasure")
    @WebResult(name="measure")
    public Measure updatePersonMeasure(@WebParam(name="personId") Long id, @WebParam(name="measure") Measure m);
    
    @WebMethod(operationName="readPersonMeasureByDates")
    @WebResult(name="measure")
    public List<Measure> readPersonMeasureByDates(@WebParam(name="personId") Long id, 
    		@WebParam(name="measureType") String measureType, 
    		@WebParam(name="before") Date before, 
    		@WebParam(name="after") Date after) throws ParseException;
    
    @WebMethod(operationName="readPersonListByMeasurement")
    @WebResult(name="person")
    public List<Person> readPersonListByMeasurement( @WebParam(name="measureType") String measureType, 
    		@WebParam(name="maxValue")  String maxValue, 
    		@WebParam(name="minValue")  String minValue);
}
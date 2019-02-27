package rest;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement
@XmlSeeAlso(Person.class)
public class Persons extends ArrayList<Person> {
//    public Persons(){
//        super();
//    }

//    public Persons(Collection<? extends Person> person){
//        super();
//    }

    @XmlElement(name = "person")
    public List<Person> getPersons(){
        return this;
    }

//    public void setPersons(List<Person> persons){
//        this.addAll(persons);
//    }

}

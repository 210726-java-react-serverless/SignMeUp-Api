package com.revature.registrar.models;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.revature.registrar.exceptions.CapacityReachedException;
import com.revature.registrar.exceptions.InvalidUserTypesException;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import com.revature.registrar.web.dtos.minis.ClassModelMini;
import org.bson.Document;

import javax.print.Doc;

/**
 * POJO
 * Basic ClassModel class with all user info and getters/setters
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClassModel {
    private String id;
    private String name; //id based on name
    private int capacity;
    private String description;

    private long openWindow;
    private long closeWindow;


    private Set<Student> students;
    private Set<Faculty> faculty; //Could have multiple faculty members per class

    public ClassModel(String name, String description, int capacity, long open, long close) {
        this.name = name;
        this.description = description;
        this.id = String.valueOf(name.hashCode());
        this.capacity = capacity;
        this.openWindow = open;
        this.closeWindow = close;
        this.students = new HashSet<>();
        this.faculty = new HashSet<>();
    }

    public ClassModel(String name, String description, int capacity, long open, long close, Set<Faculty> faculty) {
        this(name,description,capacity,open,close);
        this.faculty = faculty;
    }

    public ClassModel() {
        super();
        this.students = new HashSet<>();
        this.faculty = new HashSet<>();
    }

    public ClassModel(ClassModelMini CMM){
        this.id = CMM.getId();
        this.name = CMM.getName();
        this.description = CMM.getDescription();
        this.capacity = CMM.getCapacity();
        this.openWindow = CMM.getOpenWindow();
        this.closeWindow = CMM.getCloseWindow();
        this.students = new HashSet<>();
        this.faculty = new HashSet<>();
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        if(id.equals("hash"))
            this.id = String.valueOf(name.hashCode());
        else
            this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public Set<Student> getStudents() {
        if(students==null)
            return new HashSet<>();
        return students;
    }

    public Set<Document> getStudentsAsDoc() {
        Set<Document> docs = new HashSet<>();

        if(students == null)
            return docs;

        for(Student stu : students) {
            Document doc = stu.getAsDoc();
            docs.add(doc);
        }
        return docs;
    }

    public Set<Document> getFacultyAsDoc() {
        Set<Document> docs = new HashSet<>();

        if(faculty == null)
            return docs;

        for(Faculty fac : faculty) {
            Document doc = fac.getAsDoc();
            docs.add(doc);
        }
        return docs;
    }

    public Document getAsDoc() {
        Document doc = new Document("name", getName())
                .append("id", getId())
                .append("description", getDescription())
                .append("capacity", getCapacity())
                .append("openWindow", getOpenWindow())
                .append("closeWindow", getCloseWindow());
        return doc;
    }


    public Set<Faculty> getFaculty() {
        if(faculty == null)
            return new HashSet<>();
        return faculty;
    }

    public long getOpenWindow() {
        return openWindow;
    }

    public void setOpenWindow(long openWindow) {
        this.openWindow = openWindow;
    }

    public long getCloseWindow() {
        return closeWindow;
    }

    public void setCloseWindow(long closeWindow) {
        this.closeWindow = closeWindow;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void addUser(User user) {
        if(user.isFaculty()) {
            Faculty fac = (Faculty)user;
            addFaculty(fac);
        } else {
            Student stu = (Student) user;
            addStudent(stu);
        }
    }

    public void addFaculty(Faculty fac) {
        if(faculty==null)
            faculty = new HashSet<>();
        faculty.add(fac);
    }

    public void addStudent(Student stu) {
        if(students.size() < capacity) {
            students.add(stu);
        } else {
            //No more room in the class! Throw an exception
            throw new CapacityReachedException("Class capacity for " + this.name + " is reached");
        }
    }

    public void setFaculty(Set<Faculty> f){
        if(f == null)
            return;
        faculty = new HashSet<>();
        for(Faculty fac: f){
            faculty.add(fac);
        }
    }

    public void setStudents(Set<Student> s){
        if(s == null)
            return;
        students = new HashSet<>();
        for(Student stu: s){
            students.add(stu);
        }
    }

    //Remove the user from the class. There must be at least 1 faculty member per class.
    public void removeUser(User user) {
        if(user.isFaculty()) {
            if(faculty.size() > 1) {
                Faculty fac = (Faculty) user;
                removeFac(fac);
            }
        } else {
            Student stu = (Student) user;
            removeStudent(stu);
        }
    }

    public void removeFac(Faculty fac) {
        for(Faculty f : faculty) {
            if(f.getId() == fac.getId()) {
                students.remove(f);
                return;
            }
        };
    }

    public void removeStudent(Student stu) {
        for(Student s : students) {
            if(s.getId() == stu.getId()) {
                students.remove(s);
                return;
            }
        }
    }

    public void switchUser(User existing, User swap) {
        if(existing.isFaculty() != swap.isFaculty()) {
            //Cannot swap users of different types
            throw new InvalidUserTypesException("Cannot swap users of different types (both must be Student or Faculty)");
        }
    }

    @Override
    public String toString() {
        return "ClassModel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", capacity=" + capacity +
                ", description='" + description + '\'' +
                ", openWindow=" + openWindow +
                ", closeWindow=" + closeWindow + "}";
//                ", students=" + students +        //TODO: fix ping pong
//                ", faculty=" + faculty + "}";
    }
}

package com.revature.registrar.web.dtos;

import com.revature.registrar.models.ClassModel;
import com.revature.registrar.models.Faculty;
import com.revature.registrar.models.Student;
import com.revature.registrar.models.User;
import org.bson.Document;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

public class ClassModelDTO {
    private String id;
    private String name;
    private int capacity;
    private String description;
    private long openWindow;
    private long closeWindow;

    private Set<UserDTO> students = new HashSet<>();
    private Set<UserDTO> faculty = new HashSet<>(); //Could have multiple faculty members per class

    public ClassModelDTO(ClassModel subject) {
        this.id = subject.getId();
        this.name = subject.getName();
        this.capacity = subject.getCapacity();
        this.description = subject.getDescription();
        this.openWindow = subject.getOpenWindow();
        this.closeWindow = subject.getCloseWindow();

        //Convert Users into UserDTOs, we don't care about their classes
        for (Student stu : subject.getStudents()) {
            students.add(new UserDTO(stu));
        }

        for (Faculty fac : subject.getFaculty()) {
            faculty.add(new UserDTO(fac));
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public Set<UserDTO> getStudents() {
        return students;
    }

    public void setStudents(Set<UserDTO> students) {
        this.students = students;
    }

    public Set<UserDTO> getFaculty() {
        return faculty;
    }

    public void setFaculty(Set<UserDTO> faculty) {
        this.faculty = faculty;
    }

    @java.lang.Override
    public java.lang.String toString() {
        return "ClassModelDTO{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", capacity=" + capacity +
                ", description='" + description + '\'' +
                ", openWindow=" + openWindow +
                ", closeWindow=" + closeWindow +
                ", students=" + students +
                ", faculty=" + faculty +
                '}';
    }

}

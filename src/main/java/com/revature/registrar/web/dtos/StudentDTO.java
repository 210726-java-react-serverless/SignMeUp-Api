package com.revature.registrar.web.dtos;

import com.revature.registrar.models.ClassModel;
import com.revature.registrar.models.Student;
import com.revature.registrar.models.User;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import com.revature.registrar.web.dtos.minis.ClassModelMini;

public class StudentDTO extends UserDTO {
    private Set<ClassModelMini> classes = new HashSet<>();

    public StudentDTO(Student subject) {
        super(subject);

        for (ClassModel classModel : subject.getClasses()) {
            classes.add(new ClassModelMini(classModel));
        }
    }

    @Override
    public String toString() {
        return "UserDTO{" +
                "id=" + this.getId() +
                ", firstName='" + this.getFirstName() + '\'' +
                ", lastName='" + this.getLastName() + '\'' +
                ", email='" + this.getEmail() + '\'' +
                ", username='" + this.getUsername() + '\'' +
                ", isFaculty=" + "false" +
                '}';
    }
}

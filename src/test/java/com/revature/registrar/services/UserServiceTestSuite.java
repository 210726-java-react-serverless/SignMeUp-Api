package com.revature.registrar.services;

import com.revature.registrar.exceptions.InvalidRequestException;
import com.revature.registrar.exceptions.ResourceNotFoundException;
import com.revature.registrar.exceptions.ResourcePersistenceException;
import com.revature.registrar.models.ClassModel;
import com.revature.registrar.models.Faculty;
import com.revature.registrar.models.Student;
import com.revature.registrar.models.User;
import com.revature.registrar.repository.UserRepository;
import com.revature.registrar.util.PasswordUtils;
import org.junit.*;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


public class UserServiceTestSuite {
    UserService sut; //SUT = System Under Test
    UserRepository mockUserRepo;
    PasswordUtils passwordUtils;

    /*
    common junit4 annotations
        - @BeforeClass
        - @AfterClass
        - @Before
        - @After
        - @Test
        - @Ignore
     */

    @BeforeClass //runs before all test case; runs only once
    public static void setUpSuite() {

    }

    @AfterClass //runs after all test cases; runs only once
    public static void tearDownSuite() {

    }

    @Before // runs before each test case
    public void beforeEachTest() {
        mockUserRepo = Mockito.mock(UserRepository.class);
        passwordUtils = Mockito.mock(PasswordUtils.class);
        sut = new UserService(mockUserRepo, passwordUtils);
    }

    @After // runs after each test case
    public void afterEachTest() {
        sut = null;
    }

    @Test
    public void isUserValid_returnsTrue_givenValidUser() {
        ///AAA - Arrange, Act, Assert

        // Arrange expectations
        boolean expected = true;
        User valid = new User("valid", "valid", "valid", "valid", "valid", false);

        // Act
        boolean actual = sut.isValid(valid); //This method is private... need to mock

        //Assert
        Assert.assertEquals("Expected user to be considered valid", expected, actual);
    }

    @Test
    public void isUserValid_returnsFalse_givenUserWithNoFirstName() {
        boolean expected = false;
        User invalid = new User("", "valid", "valid", "valid", "valid", false);

        boolean actual = sut.isValid(invalid); //This method is private... need

        Assert.assertEquals("Expected user to be considered valid", expected, actual);
    }

    @Test
    public void register_returnsSuccessfully_whenGivenValidUser() {
        // Arrange
        User expected = new User("valid", "valid", "valid","valid","valid", false);
        User validUser = new User("valid", "valid", "valid","valid","valid", false);

        when(mockUserRepo.save(any())).thenReturn(expected);
        when(passwordUtils.generateSecurePassword(any())).thenReturn("valid");

        // Act
        User actual = sut.register(validUser);
        System.out.println(actual);

        // Assert
        Assert.assertEquals(expected.getId(), actual.getId());
        Assert.assertEquals(expected.getFirstName(), actual.getFirstName());
        Assert.assertEquals(expected.getLastName(), actual.getLastName());
        Assert.assertEquals(expected.getEmail(), actual.getEmail());
        Assert.assertEquals(expected.getPassword(), actual.getPassword());
        Assert.assertEquals(expected.isFaculty(), actual.isFaculty());
        verify(mockUserRepo, times(1)).save(any());
    }

    @Test(expected = InvalidRequestException.class)
    public void register_throwsException_whenGivenInvalidUser() {
        // Arrange
        User invalid = new User("", "","","","", false);

        // Act
        try {
            sut.register(invalid);
        } catch (InvalidRequestException e) {
            //Assert
            verify(mockUserRepo, times(0)).save(any());
            throw e;
        }
    }

    @Test (expected = ResourcePersistenceException.class)
    public void register_throwsException_whenGivenUserWithDuplicateUsername() {
        // Arrange
        User existing = new User("first", "last", "email", "duplicate", "pass", false);
        User duplicate = new User("first", "last", "email", "duplicate", "pass", false);
        when(mockUserRepo.findById(duplicate.getId())).thenReturn(existing);

        // Act
        try {
            sut.register(duplicate);
        } finally {
            // Assert
            verify(mockUserRepo, times(1)).findById(duplicate.getId());
            verify(mockUserRepo, times(0)).save(duplicate);
        }
    }

    @Test
    public void login_returnsSuccessfully_whenGivenValidCredentials() {
        // Arrange
        User expected = new User("valid", "valid", "valid","valid","valid", false);
        User validUser = new User("valid", "valid", "valid","valid","valid", false);

        when(mockUserRepo.findUserByCredentials(any(), any())).thenReturn(expected);

        // Act
        User actual = sut.login("username", "password");

        // Assert
        Assert.assertEquals(expected, actual);
        verify(mockUserRepo, times(1)).findUserByCredentials(any(), any());
    }

    @Test
    public void getUserWithId_returnsSuccessfully_whenGivenValidId() {
        // Arrange
        User expected = new User("valid", "valid", "valid","valid","valid", false);

        when(mockUserRepo.findById(expected.getId())).thenReturn(expected);

        // Act
        User actual = sut.getUserWithId(expected.getId());

        // Assert
        Assert.assertEquals(expected, actual);
        verify(mockUserRepo, times(1)).findById(expected.getId());
    }

    @Test (expected = ResourceNotFoundException.class)
    public void getUserWithId_throwsInvalidRequestException_whenGivenInValidId() {
        // Arrange
        User expected = new User("valid", "valid", "valid","valid","valid", false);

        when(mockUserRepo.findById(expected.getId())).thenReturn(null);

        // Act
        User actual = sut.getUserWithId(expected.getId());

        // Assert
        verify(mockUserRepo, times(1)).findById(expected.getId());
    }

    @Test
    public void update_returnsSuccessfully_whenGivenValidUser() {
        // Arrange
        User valid = new User("valid", "valid", "valid","valid","valid", false);
        boolean expected = true;
        when(mockUserRepo.update(valid)).thenReturn(true);

        // Act
        boolean actual = sut.update(valid);

        // Assert
        Assert.assertEquals(expected, actual);
        verify(mockUserRepo, times(1)).update(valid);
    }

    @Test (expected = InvalidRequestException.class)
    public void update_throwsInvalidRequestException_whenGivenInValidUser() {
        // Arrange
        User invalid = new User("", "valid", "valid","valid","valid", false);

        when(mockUserRepo.update(invalid)).thenReturn(true);
        boolean expected = false;

        // Act
        boolean actual = sut.update(invalid);

        // Assert
        verify(mockUserRepo, times(0)).update(invalid);
    }

    @Test (expected = ResourceNotFoundException.class)
    public void getUserWithUsername_throwsResourceNotFoundException_whenGivenBadUserId() {
        // Arrange
        String username = "test";
        when(mockUserRepo.findByUsername(username)).thenReturn(null);

        // Act
        User actual = sut.getUserWithUsername(username);

        // Assert
        verify(mockUserRepo, times(1)).findByUsername(username);
    }

    @Test
    public void getUserWithUsername_returnsWhenGivenValidUserId() {
        // Arrange
        String username = "test";
        User valid = new User("valid", "valid", "valid","valid","valid", false);
        when(mockUserRepo.findByUsername(username)).thenReturn(valid);

        // Act
        User actual = sut.getUserWithUsername(username);

        // Assert
        verify(mockUserRepo, times(1)).findByUsername(username);
        Assert.assertEquals(valid, actual);
    }

    @Test
    public void deleteClassFromAll_returnsWhenGivenClass() {
        // Arrange
        Calendar curr = Calendar.getInstance();
        Date d = new Date(curr.getTimeInMillis() - 100000);
        Calendar open = new Calendar.Builder()
                .setInstant(d)
                .build();
        d = new Date(curr.getTimeInMillis() + 100000);
        Calendar close = new Calendar.Builder()
                .setInstant(d)
                .build();
        ClassModel validClass = new ClassModel("valid", "valid", 5, open.getTimeInMillis(), close.getTimeInMillis());

        Student valid1 = new Student("valid", "valid", "valid","valid","valid");
        Student valid2 = new Student("valid2", "valid2", "valid2","valid2","valid2");
        Student valid3 = new Student("valid3", "valid3", "valid3","valid3","valid3");

        Faculty fac = new Faculty("valid3", "valid3", "valid3","valid3","valid3");

        valid1.addClass(validClass);
        valid2.addClass(validClass);
        valid3.addClass(validClass);
        fac.addClass(validClass);
        validClass.addStudent(valid1);
        validClass.addStudent(valid2);
        validClass.addStudent(valid3);
        validClass.addFaculty(fac);

        List<User> list = new ArrayList<>();
        list.add(valid1);
        list.add(valid2);
        list.add(valid3);
        list.add(fac);

        boolean expected = true;
        when(mockUserRepo.findWithClass(validClass.getId())).thenReturn(list);

        // Act
        boolean actual = sut.deleteClassFromAll(validClass);

        // Assert
        verify(mockUserRepo, times(1)).findWithClass(validClass.getId());
        verify(mockUserRepo, times(4)).update(any());

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void updateClassForAll_returnsWhenGivenClass() {
        // Arrange
        Calendar curr = Calendar.getInstance();
        Date d = new Date(curr.getTimeInMillis() - 100000);
        Calendar open = new Calendar.Builder()
                .setInstant(d)
                .build();
        d = new Date(curr.getTimeInMillis() + 100000);
        Calendar close = new Calendar.Builder()
                .setInstant(d)
                .build();
        ClassModel validClass = new ClassModel("valid", "valid", 5, open.getTimeInMillis(), close.getTimeInMillis());

        Student valid1 = new Student("valid", "valid", "valid","valid","valid");
        Student valid2 = new Student("valid2", "valid2", "valid2","valid2","valid2");
        Student valid3 = new Student("valid3", "valid3", "valid3","valid3","valid3");

        Faculty fac = new Faculty("valid3", "valid3", "valid3","valid3","valid3");

        valid1.addClass(validClass);
        valid2.addClass(validClass);
        valid3.addClass(validClass);
        fac.addClass(validClass);
        validClass.addStudent(valid1);
        validClass.addStudent(valid2);
        validClass.addStudent(valid3);
        validClass.addFaculty(fac);

        List<User> list = new ArrayList<>();
        list.add(valid1);
        list.add(valid2);
        list.add(valid3);
        list.add(fac);

        boolean expected = true;
        when(mockUserRepo.findWithClass(validClass.getId())).thenReturn(list);

        // Act
        boolean actual = sut.updateClassForAll(validClass);

        // Assert
        verify(mockUserRepo, times(1)).findWithClass(validClass.getId());
        verify(mockUserRepo, times(4)).update(any());

        Assert.assertEquals(expected, actual);
    }

}


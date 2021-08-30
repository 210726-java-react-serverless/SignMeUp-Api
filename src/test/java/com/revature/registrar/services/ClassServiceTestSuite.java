package com.revature.registrar.services;

import com.revature.registrar.exceptions.InvalidRequestException;
import com.revature.registrar.exceptions.OpenWindowException;
import com.revature.registrar.exceptions.ResourceNotFoundException;
import com.revature.registrar.models.ClassModel;
import com.revature.registrar.models.Faculty;
import com.revature.registrar.models.Student;
import com.revature.registrar.models.User;
import com.revature.registrar.repository.ClassModelRepo;
import org.junit.*;
import org.mockito.Mockito;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


public class ClassServiceTestSuite {
    ClassService sut; //SUT = System Under Test
    ClassModelRepo mockClassRepo;
    UserService mockUserService;

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
        mockClassRepo = Mockito.mock(ClassModelRepo.class);
        mockUserService = Mockito.mock(UserService.class);
        sut = new ClassService(mockClassRepo, mockUserService);
    }

    @After // runs after each test case
    public void afterEachTest() {
        sut = null;
    }

    @Test (expected = OpenWindowException.class)
    public void isValid_throwsOpenWindowException_givenValidUser() {
        ///AAA - Arrange, Act, Assert

        Calendar open = Calendar.getInstance();
        Date d = new Date(open.getTimeInMillis() + 100000);
        Calendar close = new Calendar.Builder()
                .setInstant(d)
                .build();
        Set<Faculty> fac = new HashSet<>();
        Set<Student> stu = new HashSet<>();

        // Arrange expectations
        boolean expected = true;
        ClassModel valid = new ClassModel("valid", "valid", 2, open.getTimeInMillis(), close.getTimeInMillis(), fac);


        // Act
        boolean actual = sut.isValid(valid); //This method is private... need to mock

        //Assert
        Assert.assertEquals("Expected user to be considered valid", expected, actual);
    }

    @Test
    public void register_returnsSuccessfully_whenGivenValidUser() {
        // Arrange
        Calendar curr = Calendar.getInstance();
        Date d = new Date(curr.getTimeInMillis() + 10000);
        Calendar open = new Calendar.Builder()
                .setInstant(d)
                .build();
        d = new Date(curr.getTimeInMillis() + 100000);
        Calendar close = new Calendar.Builder()
                .setInstant(d)
                .build();

        ClassModel expected = new ClassModel("valid", "valid", 2, open.getTimeInMillis(), close.getTimeInMillis());
        ClassModel valid = new ClassModel("valid", "valid", 2, open.getTimeInMillis(), close.getTimeInMillis());


        when(mockClassRepo.save(any())).thenReturn(expected);

        // Act
        ClassModel actual = sut.register(valid);

        // Assert
        Assert.assertEquals(expected.getId(), actual.getId());
        verify(mockClassRepo, times(1)).save(any());
    }

    @Test (expected = InvalidRequestException.class)
    public void register_throwsInvalidRequestException_whenGivenInvalidClassModel() {
        // Arrange
        Calendar curr = Calendar.getInstance();
        Date d = new Date(curr.getTimeInMillis() + 10000);
        Calendar open = new Calendar.Builder()
                .setInstant(d)
                .build();
        d = new Date(curr.getTimeInMillis() + 100000);
        Calendar close = new Calendar.Builder()
                .setInstant(d)
                .build();

        ClassModel invalid = new ClassModel("", "valid", 2, open.getTimeInMillis(), close.getTimeInMillis());


        when(mockClassRepo.save(any())).thenReturn(invalid);

        // Act
        ClassModel actual = sut.register(invalid);

        // Assert
        verify(mockClassRepo, times(0)).save(any());
    }

    @Test
    public void getUserWithUsername_returnsWhenGivenValidUserId() {
        // Arrange
        // Arrange
        Calendar curr = Calendar.getInstance();
        Date d = new Date(curr.getTimeInMillis() + 10000);
        Calendar open = new Calendar.Builder()
                .setInstant(d)
                .build();
        d = new Date(curr.getTimeInMillis() + 100000);
        Calendar close = new Calendar.Builder()
                .setInstant(d)
                .build();
        ClassModel valid = new ClassModel("valid", "valid", 2, open.getTimeInMillis(), close.getTimeInMillis());

        when(mockClassRepo.findById(valid.getId())).thenReturn(valid);

        // Act
        ClassModel actual = sut.getClassWithId(valid.getId());

        // Assert
        verify(mockClassRepo, times(1)).findById(valid.getId());
        Assert.assertEquals(valid, actual);
    }

    @Test (expected = InvalidRequestException.class)
    public void getUserWithUsername_throwsInvalidRequestExceptionWhenGivenBadUserId() {
        // Arrange
        // Arrange
        Calendar curr = Calendar.getInstance();
        Date d = new Date(curr.getTimeInMillis() + 10000);
        Calendar open = new Calendar.Builder()
                .setInstant(d)
                .build();
        d = new Date(curr.getTimeInMillis() + 100000);
        Calendar close = new Calendar.Builder()
                .setInstant(d)
                .build();
        ClassModel invalid = new ClassModel("valid", "valid", 2, open.getTimeInMillis(), close.getTimeInMillis());

        when(mockClassRepo.findById(invalid.getId())).thenReturn(null);

        // Act
        ClassModel actual = sut.getClassWithId(invalid.getId());

        // Assert
        verify(mockClassRepo, times(1)).findById(invalid.getId());
    }

    @Test (expected = ResourceNotFoundException.class)
    public void enroll_throwsResourceNotFoundExceptionWhenGivenBadClassId() {
        // Arrange
        // Arrange
        Calendar curr = Calendar.getInstance();
        Date d = new Date(curr.getTimeInMillis() + 10000);
        Calendar open = new Calendar.Builder()
                .setInstant(d)
                .build();
        d = new Date(curr.getTimeInMillis() + 100000);
        Calendar close = new Calendar.Builder()
                .setInstant(d)
                .build();
        ClassModel invalid = new ClassModel("valid", "valid", 2, open.getTimeInMillis(), close.getTimeInMillis());
        User valid = new User("valid", "valid", "valid","valid","valid", false);

        when(mockClassRepo.findById(invalid.getId())).thenReturn(null);

        // Act
        sut.enroll(valid.getId(), invalid.getId());

        // Assert
        verify(mockClassRepo, times(1)).findById(invalid.getId());
    }

    @Test (expected = ResourceNotFoundException.class)
    public void enroll_throwsResourceNotFoundExceptionWhenGivenBadUserId() {
        // Arrange
        // Arrange
        Calendar curr = Calendar.getInstance();
        Date d = new Date(curr.getTimeInMillis() + 10000);
        Calendar open = new Calendar.Builder()
                .setInstant(d)
                .build();
        d = new Date(curr.getTimeInMillis() + 100000);
        Calendar close = new Calendar.Builder()
                .setInstant(d)
                .build();
        ClassModel valid = new ClassModel("valid", "valid", 2, open.getTimeInMillis(), close.getTimeInMillis());
        User invalid = new User("valid", "valid", "valid","valid","valid", false);

        when(mockUserService.getUserWithId(invalid.getId())).thenThrow(ResourceNotFoundException.class);
        when(mockClassRepo.findById(valid.getId())).thenReturn(valid);

        // Act
        sut.enroll(invalid.getId(), valid.getId());

        // Assert
        verify(mockClassRepo, times(1)).findById(invalid.getId());
    }

    @Test
    public void enroll_returnsWhenUserAlreadyEnrolled() {
        // Arrange
        // Arrange
        Calendar curr = Calendar.getInstance();
        Date d = new Date(curr.getTimeInMillis() + 10000);
        Calendar open = new Calendar.Builder()
                .setInstant(d)
                .build();
        d = new Date(curr.getTimeInMillis() + 100000);
        Calendar close = new Calendar.Builder()
                .setInstant(d)
                .build();
        ClassModel validClass = new ClassModel("valid", "valid", 2, open.getTimeInMillis(), close.getTimeInMillis());
        Student validUser = new Student("valid", "valid", "valid","valid","valid");
        validUser.addClass(validClass);
        validClass.addStudent(validUser);

        when(mockUserService.getUserWithId(validUser.getId())).thenReturn(validUser);
        when(mockClassRepo.findById(validClass.getId())).thenReturn(validClass);

        // Act
        sut.enroll(validUser.getId(), validClass.getId());

        // Assert
        verify(mockClassRepo, times(1)).findById(validUser.getId());
        verify(mockClassRepo, times(0)).update(validClass);
        verify(mockUserService, times(0)).update(validUser);
    }

    @Test
    public void enroll_returnsWhenGivenValidIds() {
        // Arrange
        // Arrange
        Calendar curr = Calendar.getInstance();
        Date d = new Date(curr.getTimeInMillis() + 10000);
        Calendar open = new Calendar.Builder()
                .setInstant(d)
                .build();
        d = new Date(curr.getTimeInMillis() + 100000);
        Calendar close = new Calendar.Builder()
                .setInstant(d)
                .build();
        ClassModel validClass = new ClassModel("valid", "valid", 2, open.getTimeInMillis(), close.getTimeInMillis());
        Student validUser = new Student("valid", "valid", "valid","valid","valid");

        when(mockUserService.getUserWithId(validUser.getId())).thenReturn(validUser);
        when(mockClassRepo.findById(validClass.getId())).thenReturn(validClass);

        // Act
        sut.enroll(validUser.getId(), validClass.getId());

        // Assert
        verify(mockClassRepo, times(2)).findById(validUser.getId()); //Once in enroll and once in isValid
        verify(mockClassRepo, times(1)).update(validClass);
        verify(mockUserService, times(1)).update(validUser);
    }

    @Test (expected = ResourceNotFoundException.class)
    public void unenroll_throwsResourceNotFoundExceptionWhenGivenBadClassId() {
        // Arrange
        // Arrange
        Calendar curr = Calendar.getInstance();
        Date d = new Date(curr.getTimeInMillis() + 10000);
        Calendar open = new Calendar.Builder()
                .setInstant(d)
                .build();
        d = new Date(curr.getTimeInMillis() + 100000);
        Calendar close = new Calendar.Builder()
                .setInstant(d)
                .build();
        ClassModel invalid = new ClassModel("valid", "valid", 2, open.getTimeInMillis(), close.getTimeInMillis());
        User valid = new User("valid", "valid", "valid","valid","valid", false);

        when(mockClassRepo.findById(invalid.getId())).thenReturn(null);

        // Act
        sut.unenroll(valid.getId(), invalid.getId());

        // Assert
        verify(mockClassRepo, times(1)).findById(invalid.getId());
    }

    @Test (expected = ResourceNotFoundException.class)
    public void unenroll_throwsResourceNotFoundExceptionWhenGivenBadUserId() {
        // Arrange
        // Arrange
        Calendar curr = Calendar.getInstance();
        Date d = new Date(curr.getTimeInMillis() + 10000);
        Calendar open = new Calendar.Builder()
                .setInstant(d)
                .build();
        d = new Date(curr.getTimeInMillis() + 100000);
        Calendar close = new Calendar.Builder()
                .setInstant(d)
                .build();
        ClassModel valid = new ClassModel("valid", "valid", 2, open.getTimeInMillis(), close.getTimeInMillis());
        User invalid = new User("valid", "valid", "valid","valid","valid", false);

        when(mockUserService.getUserWithId(invalid.getId())).thenThrow(ResourceNotFoundException.class);
        when(mockClassRepo.findById(valid.getId())).thenReturn(valid);

        // Act
        sut.unenroll(invalid.getId(), valid.getId());

        // Assert
        verify(mockClassRepo, times(1)).findById(invalid.getId());
    }

    @Test
    public void unenroll_returnsWhenUserNotEnrolled() {
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
        ClassModel validClass = new ClassModel("valid", "valid", 2, open.getTimeInMillis(), close.getTimeInMillis());
        Student validUser = new Student("valid", "valid", "valid","valid","valid");
        validUser.addClass(validClass);
        validClass.addStudent(validUser);

        when(mockUserService.getUserWithId(validUser.getId())).thenReturn(validUser);
        when(mockClassRepo.findById(validClass.getId())).thenReturn(validClass);

        // Act
        sut.unenroll(validUser.getId(), validClass.getId());

        // Assert
        verify(mockClassRepo, times(2)).findById(validUser.getId());
        verify(mockClassRepo, times(0)).update(validClass);
        verify(mockUserService, times(0)).update(validUser);
    }
}

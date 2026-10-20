package com.gradesystem.model;

import com.gradesystem.exception.InvalidGradeException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ModelTest {

    private final Student regular = new RegularStudent("STU001", "Alice Johnson", 16, "alice@school.edu", "0788000001");
    private final Student honors = new HonorsStudent("STU002", "Bob Smith", 17, "bob@school.edu", "0788000002");

    @Test
    void regularStudentPassesAtFifty() {
        assertEquals("Regular", regular.getStudentType());
        assertTrue(regular.isPassing(50));
        assertFalse(regular.isPassing(49.9));
        assertFalse(regular.isHonorsEligible(99));   // regular students are never honors eligible
    }

    @Test
    void honorsStudentPassesAtSixtyAndIsEligibleAtEightyFive() {
        assertEquals("Honors", honors.getStudentType());
        assertFalse(honors.isPassing(59));
        assertTrue(honors.isPassing(60));
        assertTrue(honors.isHonorsEligible(85));
        assertFalse(honors.isHonorsEligible(84.9));
    }

    @Test
    void liskovSubstitutionAnyStudentWorksAsAStudent() {
        Student[] students = {regular, honors};
        for (Student s : students) {
            assertTrue(s.getPassingGrade() > 0);
            assertEquals("Active", s.getStatus());
        }
    }

    @Test
    void searchMatchesPartOfTheNameIgnoringCase() {
        assertTrue(regular.matchesKeyword("john"));
        assertTrue(regular.matchesKeyword("  ALICE "));
        assertFalse(regular.matchesKeyword("bob"));
    }

    @Test
    void exportLineAndSetters() {
        assertEquals("STU001 | Alice Johnson | Regular | alice@school.edu", regular.toExportLine());
        regular.setName("Alice J");
        regular.setAge(17);
        regular.setEmail("aj@school.edu");
        regular.setPhone("0788999999");
        regular.setStatus("Inactive");
        assertEquals("Alice J", regular.getName());
        assertEquals(17, regular.getAge());
        assertEquals("aj@school.edu", regular.getEmail());
        assertEquals("0788999999", regular.getPhone());
        assertEquals("Inactive", regular.getStatus());
    }

    @Test
    void subjectsKnowTheirType() {
        Subject core = new CoreSubject("Mathematics", "MTH101");
        Subject elective = new ElectiveSubject("Music", "MUS101");
        assertEquals("Core", core.getSubjectType());
        assertTrue(core.isMandatory());
        assertEquals("Elective", elective.getSubjectType());
        assertFalse(elective.isMandatory());
        assertEquals("MUS101", elective.getSubjectCode());
    }

    @Test
    void gradeStoresValuesAndExports() throws InvalidGradeException {
        Subject math = new CoreSubject("Mathematics", "MTH101");
        Grade grade = new Grade("GRD001", "STU001", math, 85, LocalDate.of(2026, 10, 18));
        assertEquals("GRD001", grade.getGradeId());
        assertEquals("STU001", grade.getStudentId());
        assertEquals(math, grade.getSubject());
        assertEquals(85.0, grade.getValue());
        assertEquals(LocalDate.of(2026, 10, 18), grade.getDate());
        assertEquals("18-10-2026", grade.getFormattedDate());
        assertEquals("GRD001 | 18-10-2026 | Mathematics | Core | 85", grade.toExportLine());
    }

    @Test
    void gradeRejectsInvalidValues() {
        Subject math = new CoreSubject("Mathematics", "MTH101");
        assertThrows(InvalidGradeException.class, () -> new Grade("G", "S", math, 150, LocalDate.now()));
        assertThrows(InvalidGradeException.class, () -> new Grade("G", "S", math, -1, LocalDate.now()));
    }
}

package com.gradesystem.service;

import com.gradesystem.exception.SubjectNotFoundException;
import com.gradesystem.model.CoreSubject;
import com.gradesystem.model.Subject;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SubjectCatalogTest {

    private final SubjectCatalog catalog = new SubjectCatalog();

    @Test
    void defaultCatalogHasSixSubjectsInTwoTypes() {
        assertEquals(6, catalog.getAllSubjects().size());
        assertEquals(List.of("Core", "Elective"), catalog.getSubjectTypes());
        assertEquals(3, catalog.getSubjectsByType("core").size());
    }

    @Test
    void findsSubjectIgnoringCase() throws SubjectNotFoundException {
        Subject subject = catalog.findSubject(" physical education ", "ELECTIVE");
        assertEquals("PHE101", subject.getSubjectCode());
    }

    @Test
    void wrongNameOrWrongTypeThrows() {
        assertThrows(SubjectNotFoundException.class, () -> catalog.findSubject("Chemistry", "Core"));
        SubjectNotFoundException e = assertThrows(SubjectNotFoundException.class,
                () -> catalog.findSubject("Music", "Core"));
        assertTrue(e.getRecoveryHint().contains("Music (Elective)"));
    }

    @Test
    void customCatalogCanBeInjected() {
        SubjectCatalog custom = new SubjectCatalog(List.of(new CoreSubject("Physics", "PHY101")));
        assertEquals(List.of("Core"), custom.getSubjectTypes());
    }
}

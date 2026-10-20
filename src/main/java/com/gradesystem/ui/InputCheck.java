package com.gradesystem.ui;

import com.gradesystem.exception.InvalidInputException;

/**
 * A rule that a piece of text must follow.
 *
 * <p>It lets {@link ConsoleIO#readValidText(String, InputCheck)} reuse ONE
 * "ask until valid" loop for names, emails, phones and file names, instead of
 * writing four almost identical loops. You pass a rule like this:</p>
 * <pre>io.readValidText("Enter email: ", InputValidator::validateEmail);</pre>
 */
public interface InputCheck {

    /**
     * Checks the text against this rule.
     *
     * @param text the text to check
     * @throws InvalidInputException if the text breaks the rule
     */
    void check(String text) throws InvalidInputException;
}

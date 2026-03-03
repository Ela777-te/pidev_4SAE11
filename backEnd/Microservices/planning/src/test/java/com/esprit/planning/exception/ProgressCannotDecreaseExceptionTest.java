package com.esprit.planning.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for ProgressCannotDecreaseException. Verifies constructor sets message, minAllowed, and provided
 * and that getters return the correct values.
 */
class ProgressCannotDecreaseExceptionTest {

    @Test
    void constructor_setsMessageAndGetters() {
        ProgressCannotDecreaseException ex = new ProgressCannotDecreaseException(70, 40);

        assertThat(ex.getMinAllowed()).isEqualTo(70);
        assertThat(ex.getProvided()).isEqualTo(40);
        assertThat(ex.getMessage()).contains("70");
        assertThat(ex.getMessage()).contains("40");
        assertThat(ex.getMessage()).contains("cannot be less");
    }

    @Test
    void isInstanceOfRuntimeException() {
        ProgressCannotDecreaseException ex = new ProgressCannotDecreaseException(50, 30);
        assertThat(ex).isInstanceOf(RuntimeException.class);
    }
}

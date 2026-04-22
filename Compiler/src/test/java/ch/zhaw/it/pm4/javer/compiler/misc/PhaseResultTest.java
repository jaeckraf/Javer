package ch.zhaw.it.pm4.javer.compiler.misc;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PhaseResultTest {

    @Test
    void constructorStoresSuccessAndPayload() {
        PhaseResult<String> result = new PhaseResult<>(true, "payload");

        assertTrue(result.isSuccess());
        assertEquals("payload", result.getPayload());
    }

    @Test
    void successFactoryCreatesSuccessfulResult() {
        PhaseResult<Integer> result = PhaseResult.success(9);

        assertTrue(result.isSuccess());
        assertEquals(9, result.getPayload());
    }

    @Test
    void failureFactoryCreatesFailedResultWithoutPayload() {
        PhaseResult<Object> result = PhaseResult.failure();

        assertFalse(result.isSuccess());
        assertNull(result.getPayload());
    }
}


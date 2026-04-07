package ch.zhaw.it.pm4.javer.compiler.misc;

public class PhaseResult<T> {

    /**
     * Standard result object for all compiler phases.
     *
     * Provides:
     * - success flag
     * - optional payload (phase output)
     */

    private final boolean success;
    private final T payload;

    public PhaseResult(boolean success, T payload) {
        this.success = success;
        this.payload = payload;
    }

    public boolean isSuccess() {
        return success;
    }

    public T getPayload() {
        return payload;
    }

    // Factory methods (optional but useful)

    public static <T> PhaseResult<T> success(T payload) {
        return new PhaseResult<>(true, payload);
    }

    public static <T> PhaseResult<T> failure() {
        return new PhaseResult<>(false, null);
    }
}
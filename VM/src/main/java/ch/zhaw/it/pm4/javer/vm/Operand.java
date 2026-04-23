package ch.zhaw.it.pm4.javer.vm;

public class Operand<T> {
    private T value;

    public Operand(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }
}

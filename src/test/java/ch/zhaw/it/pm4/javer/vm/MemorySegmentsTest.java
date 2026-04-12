package ch.zhaw.it.pm4.javer.vm;

import ch.zhaw.it.pm4.javer.vm.memorySegments.DataSegment;
import ch.zhaw.it.pm4.javer.vm.memorySegments.Heap;
import ch.zhaw.it.pm4.javer.vm.memorySegments.MemorySegment;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class MemorySegmentsTest {

    @Test
    void dataSegmentExtendsMemorySegment() {
        assertInstanceOf(MemorySegment.class, new DataSegment());
    }

    @Test
    void heapExtendsMemorySegment() {
        assertInstanceOf(MemorySegment.class, new Heap());
    }
}


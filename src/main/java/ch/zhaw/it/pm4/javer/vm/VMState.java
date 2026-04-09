package ch.zhaw.it.pm4.javer.vm;

import ch.zhaw.it.pm4.javer.vm.memorySegments.DataSegment;
import ch.zhaw.it.pm4.javer.vm.memorySegments.Heap;
import ch.zhaw.it.pm4.javer.vm.memorySegments.MemorySegment;

// TODO Initialize with defaults
public class VMState {
    private int programCounter = 0;
    private boolean isRunning = false;
    private DataSegment dataSegment; // ROM
    private MemorySegment staticSegment; // TODO sind im Moment Dummies (MemorySegment)
    private MemorySegment argumentSegment; // TODO sind im Moment Dummies (MemorySegment)
    private MemorySegment localSegment; // TODO sind im Moment Dummies (MemorySegment)
    private MemorySegment pointerSegment; // TODO sind im Moment Dummies (MemorySegment)
    private Heap heap;

    public VMState() {
    }
}

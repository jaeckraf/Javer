package ch.zhaw.it.pm4.javer.vm;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class VM {

    private String[] instructions;
    private List<Integer> dataSegment;
    private int stack[] = new int[4096];
    private int stackPointer;

    public VM(String filePath) {
        File file = new File(filePath);
        try {
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            instructions = content.split("\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String run() {
       int programCounter = 0;
       boolean isRunning = true;

       while(isRunning) {
           String instruction = instructions[programCounter];
           String[] tokens = instruction.split(" ");
           switch(tokens[0]) {
               case "ADD": {


               } break;
               case "PUSH": {
                   Double.parseDouble(tokens[1]);
                   tokens[1].charAt(1);
               } break;
           }


           programCounter++;
       }


        return "";
    }

    private void processCodeAndDataSegments() {

    }

    private void push(int value) {
        stack[stackPointer] = value;
    }

}

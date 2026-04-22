package ch.zhaw.it.pm4.javer.vm;


/*
* code:
*
*
*
* data:
*
* */



public class VM {



    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java VM <filePath>");
            return;
        }
        String filePath = args[0];
        VM vm = new VM(filePath);
        vm.run();
    }

    public VM(String filePath) {

    }

    public void run() {

    }


}

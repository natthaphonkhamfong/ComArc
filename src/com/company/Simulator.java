package com.company;

import java.io.*;
import java.util.ArrayList;

public class Simulator {

    static ArrayList<Integer> memory = new ArrayList<Integer>();
    static int[] register = new int[8];
    static int pc = 0;
    static boolean isRun , ishalt;
    static int count;
    static String FileRead = "E:\\CPE304\\Project\\ComArc\\src\\com\\company\\OutputAssembly.txt";
    static String FileWrite = "E:\\CPE304\\Project\\ComArc\\src\\com\\company\\OutputSimulator.txt";
    static BufferedWriter bw = null;
    static FileWriter fw = null;
    static PrintWriter pw = null;
    static int NumZero , regA ,regB;

    public static void main(String[] args) throws IOException {
        checkWriteFile(); //check file OutputSimulator
        read(FileRead); // read line  file Simulator
        printmem(); // print memory
        isRun = true; // initiate isRun
        ishalt = false; // initiate ishalt
        count = 0; //initiate count
        execution(); //run program
    }

    public static void checkWriteFile(){ //delete FileWrite
        try{
            File file = new File(FileWrite);
            file.delete();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void read(String file) { // read file
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String Currentline;
            while ((Currentline = br.readLine()) != null) {
                memory.add(Integer.valueOf(Currentline)); // add instruction to memory
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void write(String Currentline) throws IOException { // write Currentline in FileWrite
        try {
            fw = new FileWriter(FileWrite, true);
            bw = new BufferedWriter(fw);
            pw = new PrintWriter(bw);

            pw.println(Currentline);
            pw.flush();
        }finally {
            try {
                pw.close();
                bw.close();
                fw.close();
            } catch (IOException io){
            }
        }
    }

    public static void execution() throws IOException { // run program
        while (isRun) {
            int Currentline = memory.get(pc); // fetch
            String biStr = Integer.toBinaryString(Currentline); //convert decimal to binary

            if ( biStr.length() < 25) { // do bit equal 25 bit
                for (int j = biStr.length(); j < 25; j++)
                    biStr = "0" + biStr;
            }

            printExe(); // print state execution

            String instruction = biStr.substring(0, 3); // select opcode

            pc++; // fetch
            count++; // number state

            setReg(biStr); // set value regA and regB
            if (instruction.equals("000")) add(biStr); // check opcode equal add ?
            else if (instruction.equals("001")) nand(biStr);  // check opcode equal nand ?
            else if (instruction.equals("010")) lw(biStr);  // check opcode equal lw ?
            else if (instruction.equals("011")) sw(biStr);  // check opcode equal sw ?
            else if (instruction.equals("100")) beq(biStr);  // check opcode equal beq ?
            else if (instruction.equals("101")) jalr(biStr);  // check opcode equal jalr ?
            else if (instruction.equals("110")) halt(biStr);  // check opcode equal halt ?
            else if (instruction.equals("111")) noop(biStr);  // check opcode equal noop ?
            else noopcode(); // no instruction for this opcode

            regZero(); //check register[0]
            printEnd(); // print machine halted
        }
    }

    public static void add(String biStr) throws IOException { // instruction add $regDst = $regA & $regB
        String offsetField = biStr.substring(9, 22);
        isOne(offsetField); // check have offsetField bit 1
        int regDst = Integer.parseInt(biStr.substring(22, 25) ,2); //rd convert String to integer and convert binary to decimal
        register[regDst] = register[regA] + register[regB]; // rd = rs+ rt
        tohalt(); // check halt
    }

    public static void nand(String biStr) throws IOException { // instruction  nand $regDst = !($regA & $regB)
        String offsetField = biStr.substring(9, 22);
        isOne(offsetField); // check have offsetField bit 1
        int regDst = Integer.parseInt(biStr.substring(22, 25), 2); //rd convert String to integer and convert binary to decimal
        register[regDst] = ~(register[regA] & register[regB]); //rd = rs nand rt
        tohalt(); // check halt
    }


    public static void lw(String biStr) throws IOException { // instruction lw $regB offsetField($regA)
        int offsetField = Integer.parseInt(biStr.substring(9, 25) , 2); //offsetField  convert String to integer and convert binary to decimal
        register[regB] = memory.get(register[regA] + offsetField); //load[regB] = me[regA +  offsetField]
        tohalt(); // check halt
    }

    public static void sw(String biStr) throws IOException { // instruction sw $regB offsetField($regA)
        int offsetField = Integer.parseInt(biStr.substring(9, 25) , 2); //offsetField convert String to integer and convert binary to decimal
        arraysize(register[regA] + offsetField);
        memory.set(register[regA] + offsetField, register[regB]); //memory[regA +  offsetField]  = register[regB]
        tohalt(); // check halt
    }
    public static void beq(String biStr) throws IOException { // instruction beq $regA $regB offsetField
        int offsetField = getTwosComplement(biStr.substring(9, 25)); // offsetField convert String to integer and convert binary to decimal

        if (register[regA] == register[regB]){ // $regA = $regB
            pc = pc +  offsetField;  //jump label pc
        }
        tohalt(); // check halt
    }

    public static void jalr(String biStr) throws IOException { // instruction jalr $regA
        String offsetField = biStr.substring(9, 25);
        isOne(offsetField); // check have offsetField bit 1

        register[regB] = pc; // $regB = pc
        pc = register[regA]; // jump $regA

        tohalt(); // check halt
    }
    public static void halt(String biStr) throws IOException { // instruction fetch but stop program
        String offsetField = biStr.substring(3, 25);
        isOne(offsetField); // check offsetField have bit 1
        ishalt = true;
    }
    public static void noop(String biStr) throws IOException { // no instruction and no fetch
        String offsetField = biStr.substring(3, 25);
        isOne(offsetField); // check have offsetField bit 1
    }

    public static void setReg(String biStr){
         regA = Integer.parseInt(biStr.substring(3, 6), 2); //rs convert String to integer and convert binary to decimal
         regB = Integer.parseInt(biStr.substring(6, 9), 2); //rt convert String to integer and convert binary to decimal
    }

    public static void tohalt(){
        isRun = (ishalt) ? false : true ;
    } // check halt


    public static  void printExe() throws IOException { // print execution value memory and register
        System.out.println();
        System.out.println("@@@");
        System.out.println("state:");
        System.out.println("\tpc " + pc);
        System.out.println("\tmemory:");
        write("\n");
        write("@@@");
        write("state:");
        write("\tpc " + pc);
        write("\tmemory:");
        printmem();
        System.out.println("\tregister:");
        write("\tregister:");
        printreg();
        System.out.println("end state");
        write("end state");

    }

    public static void printEnd() throws IOException { // print count run instruction
        if (ishalt && isRun){
            System.out.println("machine halted");
            System.out.println("total of " + count + " instructions executed");
            System.out.println("final state of machine:");
            String StrCount = Integer.toString(count);
            write("machine halted");
            write("total of " + StrCount + " instructions executed");
            write("final state of machine:");
        }
    }

    public static void printmem() throws IOException { // print value in memory
        for (int i = 0; i < memory.size() ; i++){
            if (memory.get(i) == 0) {
                NumZero++;
            }else{
                NumZero = 0;
            }
            if (NumZero < 10) {
                System.out.println("\t\tmemory[" + i + "]=" + memory.get(i));
                String StrI = Integer.toString(i);
                String StrMem = Integer.toString(memory.get(i));
                String print = "\t\tmemory[" + StrI + "]=" + StrMem;
                write(print);
            }
        }
    }

    public static void printreg() throws IOException { // print value in register
        for (int i=0; i< 8; i++) {
            System.out.println("\t\treg[" + i + "]" + register[i]);
            String StrI =  Integer.toString(i);
            String StrReg = Integer.toString(register[i]);
            String print = "\t\tmemory["+ StrI + "]=" + StrReg;
            write(print);
        }
    }

    public static void noopcode() throws IOException { // no instruction
        ishalt = true;
        tohalt();
        System.out.println("error(opcode) instruction: " + (pc-1));
        String Strpc = Integer.toString(pc-1); // convert String to interger
        write("error(opcode) instruction: " + Strpc);
    }

    public static void regZero() throws IOException { // check convert $reg0
        if (register[0] != 0) {
            ishalt = true;
            tohalt();
            System.out.println("error(reg0) instruction: " + (pc-1));
            String Strpc = Integer.toString(pc-1); // convert String to interger
            write("error(reg0) instruction: " + Strpc);
        }
    }

    public static void isOne(String binary) throws IOException { // check have bit 1
        String one = "1";
        if (binary.contains(one)){
            ishalt = true;
            tohalt();
            System.out.println("error(have bit 1)  instruction: " + (pc-1));
            String Strpc = Integer.toString(pc-1); // convert String to interger
            write("error(have bit 1) instruction: " + Strpc);
        }
    }

    public static void arraysize(int size){
        for (int i = memory.size(); i < size + 1 ; i++)
            memory.add(0);
    }

    public static int getTwosComplement(String binary) { // 2' complement
        if (binary.charAt(0) == '1') { // check value minus
            String invertedInt = invertDigits(binary); // 1' complement
            int decimalValue = Integer.parseInt(invertedInt, 2); // convert String binary to integer decimal

            decimalValue = (decimalValue + 1) * -1; // -(1' complement + 1)

            return decimalValue;
        } else {

            return Integer.parseInt(binary, 2);
        }
    }

    public static String invertDigits(String binary) { // 1' complement
        String result = binary;
        result = result.replace("0", " "); //temp replace 0s
        result = result.replace("1", "0"); //replace 1s with 0s
        result = result.replace(" ", "1"); //put the 1s back in
        return result;
    }
}
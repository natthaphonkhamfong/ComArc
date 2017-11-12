package com.company;

import java.io.*;
import java.util.ArrayList;

public class Simulator {


    static ArrayList<Integer> memmory = new ArrayList<Integer>();
    static int[] register = new int[8];
    static int pc = 0;
    static boolean isRun;
    static boolean ishalt;
    static int count;
    static String FileRead = "E:\\CPE304\\Project\\ComArc\\src\\com\\company\\simulator.txt";
    static String FileWrite = "E:\\CPE304\\Project\\ComArc\\src\\com\\company\\OutputSimulator.txt";

    public static void main(String[] args) {

        read(FileRead);
        printmem(); // print memmory
        isRun = true;
        ishalt = false;
        count = 0;
        execution();
    }


    public static void read(String file) {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String Currentline;

            while ((Currentline = br.readLine()) != null) {
                memmory.add(Integer.valueOf(Currentline)); // add instruction to memmory
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void write(String Currentline) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FileWrite))) {

            bw.write(Currentline);

            // no need to close it.
            //bw.close();
            //System.out.println("Done");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public static void execution(){

        while (isRun) {
            int Currentline = memmory.get(pc);
            String biStr = Integer.toBinaryString(Currentline); //convert decimal to binary
           // System.out.println(Currentline);


            if ( biStr.length() < 25) {
                for (int j = biStr.length(); j < 25; j++)
                    biStr = "0" + biStr;
            }

            printExe();

            String instruction = biStr.substring(0, 3); // select opcode
           // System.out.println("\t\t\t"+instruction);

            if (instruction.equals("000")) add(biStr);
            else if (instruction.equals("001")) nand(biStr);
            else if (instruction.equals("010")) lw(biStr);
            else if (instruction.equals("011")) sw(biStr);
            else if (instruction.equals("100")) beq(biStr);
            else if (instruction.equals("101")) jalr(biStr);
            else if (instruction.equals("110")) halt(biStr);
            else if (instruction.equals("111")) noop();
            else noopcode();

            printEnd(); // print machine halted

        }
    }

    public static void add(String biStr) {
//        System.out.println("\t\t\tadd");
        int regA = Integer.parseInt(biStr.substring(3, 6), 2); //rs convert String to integer and convert binary to decimal
        int regB = Integer.parseInt(biStr.substring(6, 9), 2); //rt convert String to integer and convert binary to decimal
        String offsetField = biStr.substring(9, 22);
        offsetField = "0000000000000";
        int regDst = Integer.parseInt(biStr.substring(22, 25) ,2); //rd convert String to integer and convert binary to decimal
        register[regDst] = register[regA] + register[regB]; // rd = rs+ rt
        pc++;
        count++;
        tohalt(); // check halt
        regZero(); //check register[0]
    }

    public static void nand(String biStr) {
        pc++;
//        System.out.println("\t\t\tnand");
        int regA = Integer.parseInt(biStr.substring(3, 6), 2); //rs convert String to integer and convert binary to decimal
        int regB = Integer.parseInt(biStr.substring(6, 9), 2); //rt convert String to integer and convert binary to decimal
        String offsetField = biStr.substring(9, 22);
        offsetField = "0000000000000";
        int regDst = Integer.parseInt(biStr.substring(22, 25), 2); //rd convert String to integer and convert binary to decimal
        register[regDst] = ~(register[regA] & register[regB]); //rd = rs nand rt
        count++;
        tohalt(); // check halt
        regZero(); //check register[0]
    }


    public static void lw(String biStr) {
        pc++;
//        System.out.println("\t\t\tlw");
        int regA = Integer.parseInt(biStr.substring(3, 6), 2); //rs convert String to integer and convert binary to decimal
        int regB = Integer.parseInt(biStr.substring(6, 9), 2); //rt convert String to integer and convert binary to decimal
        int offsetField = Integer.parseInt(biStr.substring(9, 25) , 2); //offsetField  convert String to integer and convert binary to decimal
        register[regB] = memmory.get(register[regA] + offsetField); //load[regB] = me[regA +  offsetField]
        count++;
        tohalt(); // check halt
        regZero(); //check register[0]
    }

    public static void sw(String biStr) {
        pc++;
//        System.out.println("\t\t\tsw");
        int regA = Integer.parseInt(biStr.substring(3, 6), 2); //rs convert String to integer and convert binary to decimal
        int regB = Integer.parseInt(biStr.substring(6, 9), 2); //rt convert String to integer and convert binary to decimal
        int offsetField = Integer.parseInt(biStr.substring(9, 25) , 2); //offsetField convert String to integer and convert binary to decimal
        memmory.set(register[regA + offsetField], register[regB]); //memory[regA +  offsetField]  = register[regB]
        count++;
        tohalt(); // check halt
        regZero(); //check register[0]
    }
    public static void beq(String biStr) {
        pc++;
//        System.out.println("\t\t\tbeq");
        int regA = Integer.parseInt(biStr.substring(3, 6), 2); //rs convert String to integer and convert binary to decimal
        int regB = Integer.parseInt(biStr.substring(6, 9), 2); //rt convert String to integer and convert binary to decimal
        int offsetField = getTwosComplement(biStr.substring(9, 25)); // offsetField convert String to integer and convert binary to decimal

        if (register[regA] == register[regB]){
            pc = pc +  offsetField;
        }
        count++;
        tohalt(); // check halt
        regZero(); //check register[0]
    }

    public static void jalr(String biStr) {
        pc++;
//        System.out.println("\t\t\tjalr");
        int regA = Integer.parseInt(biStr.substring(3, 6), 2); //rs convert String to integer and convert binary to decimal
        int regB = Integer.parseInt(biStr.substring(6, 9), 2); //rt convert String to integer and convert binary to decimal
        String offsetField = biStr.substring(9, 25);
        offsetField = "0000000000000000";

        register[regB] = pc;
        pc = register[regA];
        count++;
        tohalt(); // check halt
        regZero(); //check register[0]
    }
    public static void halt(String biStr) {
        pc++;
//        System.out.println("\t\t\thalt");
        String offsetField = biStr.substring(3, 25);
        offsetField = "0000000000000000000000";
        count++;
        ishalt = true;
    }
    public static void noop() {
        count++;
        //System.out.println("\t\t\tnoop");
    }

    public static void tohalt(){
        isRun = (ishalt) ? false : true ;
    }


    public static  void printExe() {
        System.out.println("\n@@@");
        System.out.println("\tpc " + pc);
        System.out.println("\tmemory:");
        printmem();
        System.out.println("\tregister:");
        printreg();
        System.out.println("end state");

    }

    public static void printEnd(){
        if (ishalt && isRun){
            System.out.println("machine halted");
            System.out.println("total of " + count + " instructions executed");
            System.out.println("final state of machine:");
        }
    }

    public static void printmem(){
        for (int i=0 ; i < memmory.size() ;i++){
            System.out.println("memory["+ i + "]=" + memmory.get(i));
        }
    }

    public static void printreg(){
        for (int i=0; i< 8; i++)
            System.out.println("reg["+ i + "]" + register[i]);

    }

    public static void noopcode(){
        ishalt = true;
        tohalt();
        System.out.println("error opcode instruction: " + pc);
    }

    public static void regZero(){
        if (register[0] != 0) {
            ishalt = true;
            tohalt();
            System.out.println(" error reg0 instruction: " + pc);
        }
    }

    public static int getTwosComplement(String binary) {
        if (binary.charAt(0) == '1') {
            String invertedInt = invertDigits(binary);
            int decimalValue = Integer.parseInt(invertedInt, 2);

            decimalValue = (decimalValue + 1) * -1;

            return decimalValue;
        } else {

            return Integer.parseInt(binary, 2);
        }
    }


    public static String invertDigits(String binary) {
        String result = binary;
        result = result.replace("0", " "); //temp replace 0s
        result = result.replace("1", "0"); //replace 1s with 0s
        result = result.replace(" ", "1"); //put the 1s back in
        return result;
    }

}

package com.company;

import java.io.*;
import java.util.ArrayList;

public class Assembler {

    int l = 0; // number of line start at 0

    private String inst, rs, rt, rd;
    static String FileRead = "E:\\assembly.txt";
    //static String FileWrite = "E:\\CPE304\\Project\\ComArc\\src\\com\\company\\OutputSimulator.txt";
    static ArrayList<String> label = new ArrayList<String>();
    static ArrayList<Integer> valueOflabel = new ArrayList<Integer>();
    static ArrayList<String> memory = new ArrayList<String>();
    private static boolean isRun;
    private static int pc;
    static boolean checkIsLabel; // check ว่า lebel นี่มีอยู่ไหม
    static int result ;

    public static void main(String[] args) {
        read("E:\\CPE304\\Project\\ComArc\\src\\com\\company\\assembly.txt");
        isRun = true;
        pc = 0;
        Assembler();
    }

    public static void read(String file) { // read file
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String Currentline;
            while ((Currentline = br.readLine()) != null) {
                memory.add(Currentline); // add instruction to memory
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void Assembler(){
        checklabel();
        while (isRun) {
            if (checkIsLabel == false) break;
            String CurrentLine = memory.get(pc);
            String[] field = CurrentLine.split("\\s+"); // split white space

            pc++;
            result = 0;

            if (field[1].equals(".fll")) setFill(field[2]);
            else setInst(field[1], field[2], field[3], field[4]);

            print();
        }
    }

    private static void checklabel() {
        int count = 0;
        for (int j=0; j<memory.size(); j++,count++) {
            checkIsLabel = false;
            String CurrentLine = memory.get(count);
            String[] field = CurrentLine.split("\\s+"); // split white space
            int checklabel = 0;
            if (!field[0].isEmpty()) { //check is Label?
                if (field[1].equals(".fll")) { // check is Fill?
                    label.add(field[0]);
                    if (field[2].equals("-?\\d+")) { //เช็คว่าเป็นตัวเลขไหม
                        valueOflabel.add(convertStritoInt(field[2]));
                    }else {
                        for (int i = 0; i < label.size(); i++) {
                            if (field[2].equals(label.get(i))) {
                                valueOflabel.add(valueOflabel.get(i));
                                checkIsLabel = true;
                            }
                            if (field[0].equals(label.get(i))){
                                checkIsLabel = false;
                                System.out.println( "label is exist" + field[0]);
                            }
                        }
                        if (checkIsLabel == false){
                            System.out.println("error label" + field[0] + " does not exist" + field[2]);
                        }
                    }
                } else {
                    label.add(field[0]);
                    valueOflabel.add(count);
                }
            }
        }
    }

    private static void setFill(String field) {
        result = convertStritoInt(field);
    }

    public static void setInst(String inst, String field0 ,String field1 ,String field2) {
        String binary = "";
        if(inst.equals("add")){
            binary = "000";
        }

        else if (inst.equals("nand")){
            binary = "001";
        }

        else if (inst == "lw"){
            binary = "010";
        }

        else if (inst.equals("sw")){
            binary = "011";
        }
        else if (inst.equals("beq")){
            binary = "100";
        }

        else if (inst.equals("jalr")){
            binary = "101";
        }

        else if (inst.equals("halt")){
            binary = "110";
        }

        else if (inst.equals("noop")){
            binary = "000";
        }

        String regA = Integer.toBinaryString(convertStritoInt(field0)); // convert decimal to binary
        String regB = Integer.toBinaryString(convertStritoInt(field1)); // convert decimal to binary
        String regDst = "";
        if (field2.equals("\\d")) {
            regDst = Integer.toBinaryString(convertStritoInt(field2)); // convert decimal to binary
        }else{
            for (int i=0 ; i<label.size(); i++)
                if (field2.equals(label.get(i))) {
                    int valueDst = valueOflabel.get(i); // โหลดค่าจาก label
                    regDst = Integer.toBinaryString(valueDst); // convert decimal to binary
                }
        }

        if (regDst.equals("")){
            isRun = false;
            System.out.println("error field2 " + field2 + " does not exist");
        }

        String temp = binary + regA + regB + regDst ;
        result = Integer.parseInt(temp ,2); // convert binary to decimal

    }

    private static void print(){
        System.out.println(result);
    }

    private static int convertStritoInt(String field){ // convert String to int
        return Integer.parseInt(field); // return convert int
    }




}


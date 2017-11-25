package com.company;

import java.util.Arrays;
import java.util.regex.Pattern;
import java.io.*;
import java.util.ArrayList;
import java.lang.*;

public class Assembler {

    int l = 0; // number of line start at 0

    static String FileRead = "text\\assembly.txt";
    static String FileWrite = "text\\OutputAssembly.txt";
    static ArrayList<String> label = new ArrayList<String>();
    static ArrayList<Integer> valueOflabel = new ArrayList<Integer>();
    static ArrayList<String> memory = new ArrayList<String>();

    private static boolean isRun, isbeq;
    private static int pc, result;

    static String pattern = ("^[\\-]?\\d*$");  //number pattern
    static String patternAlphabet = ("^[A-Za-z]*"); // alphabet pattern

    static BufferedWriter bw = null;
    static FileWriter fw = null;
    static PrintWriter pw = null;
    static String[] LabelPattern = {"start", "done", "five", "neg1", "stAddr"};
    static String[] OpcodePattern = {"add", "nand", "lw", "sw", "beq", "jarl", "halt", "noop", ".fill"};  //Opcode Pattern for this PC


    public static void main(String[] args) throws IOException {
        checkWriteFile();
        read(FileRead);
        isRun = true;
        pc = 0;
        Assembler();
    }

    public static void checkWriteFile() { //delete FileWrite
        try {
            File file = new File(FileWrite);
            file.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void write(String Currentline) throws IOException {       // เขียนแต่ละบรรทัดลงไฟล์ .txt
        try {
            fw = new FileWriter(FileWrite, true);
            bw = new BufferedWriter(fw);
            pw = new PrintWriter(bw);

            pw.println(Currentline);
            pw.flush();
        } finally {
            try {
                pw.close();
                bw.close();
                fw.close();
            } catch (IOException io) {
            }
        }
    }

    public static void read(String file) {                              // อ่าน file assembly.txt
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String Currentline;
            while ((Currentline = br.readLine()) != null) {
                memory.add(Currentline);                                // add instruction to memory
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void Assembler() throws IOException {  //เริ่มแปลง assembly เป็น machine language

        checklabel();
        for (int i = 0; i < memory.size(); i++) {
            //if (checkIsLabel == false) break;
            isbeq = false;
            if (!isRun) break;
            String CurrentLine = memory.get(pc);
            String[] field = CurrentLine.split("\\s+"); // split white space
            pc++;

            if (field[1].equals(".fill")) {             //ถ้า opcode เป็น fill ใช้ฟังก์ชัน setFill

                setFill(field[2]);
            }

            if (field[1].equals("noop") || field[1].equals("halt")) { //ถ้าเป็น halt หรือ noop ใช้ฟังก์ชัน setOtype
                setOtype(field[1]);
            } else {
                setInst(field[1]);                      // คำสั่ง opcode ตาม Table1 ใช้ฟังก์ชัน setInst
            }

            print();
            result = 0;
        }
    }


    private static void checklabel() {
        for (int i = 0; i < memory.size(); i++) {
            String CurrentLine = memory.get(i);
            String[] field = CurrentLine.split("\\s+");      // split white space

            if (!field[0].isEmpty()) {                       //Check CurrentLine has label?
                if (field[0].length() > 6) {                              //Check Lebel > 6 bits
                    System.out.println("Label is more than 6 bits !!!");
                    isRun = false;
                }

                if (!Arrays.asList(LabelPattern).contains(field[0])) {
                    System.out.println("label does not exist ---> " + field[0]); //เช็คว่า Label defined?
                    isRun = false;
                }


                if (field[1].equals(".fill")) {                // check is Fill?

                    label.add(field[0]);                      // เก็บ field[0] ไว้ใน ArrayList label
                    valueOflabel.add(i);

                    if (Pattern.matches(patternAlphabet, field[2]) && !Arrays.asList(LabelPattern).contains(field[2])) {
                        System.out.println("label is not correct ---> " + field[2]); //เช็คว่า Is it correct label?
                        isRun = false;
                    }

                    if (Pattern.matches(pattern, field[2])) {     //Check field2 is Number ?
                        valueOflabel.add(convertStritoInt(field[2]));   //แปลง field[2] จาก String เป็น integer แล้วเก็บไว้ใน ArrayList ValueOflabel
                    } else {
                        for (int j = 0; j < label.size(); j++) {
                            if (label.get(j).equals(field[2])) {
                                valueOflabel.add(valueOflabel.get(j));  //เก็บค่า address ของ field[2] ไว้ใน ArrayLast ชื่อ valueOflabel
                            }
                        }
                    }


                } else {
                    label.add(field[0]);
                    valueOflabel.add(i);                       //เก็บ Address ที่ Label อยู่
                }

                int count = 0;
                for (int j = 0; j < label.size(); j++) {       //check label ซ้ำ
                    count = 0;
                    for (int k = 0; k < label.size(); k++)
                        if (label.get(j).equals(label.get(k)))
                            count++;
                }

                if (count >= 2) {
                    System.out.println("Label " + field[0] + " already has.");    //Check label does exists
                    isRun = false;
                }
            }
        }
    }


    private static void setFill(String field) {        //สำหรับคำสั่ง .fill
        if (Pattern.matches(pattern, field)) {         //
            result = convertStritoInt(field);

        } else {
            for (int i = 0; i < label.size(); i++) {
                if (field.equals(label.get(i))) {
                    result = valueOflabel.get(i);
                }
            }
        }
    }

    public static void setInst(String inst) {              //Convert each instruction to opcode
        String CurrentLine = memory.get(pc - 1);
        String[] field = CurrentLine.split("\\s+");         // split white space


        if (inst.equals("add")) {
            Rtype("000", field[2], field[3], field[4]);
        } else if (inst.equals("nand")) {
            Rtype("001", field[2], field[3], field[4]);
        } else if (inst.equals("lw")) {
            Itype("010", field[2], field[3], field[4]);
        } else if (inst.equals("sw")) {
            Itype("011", field[2], field[3], field[4]);
        } else if (inst.equals("beq")) {
            isbeq = true;
            Itype("100", field[2], field[3], field[4]);
        } else if (inst.equals("jalr")) {
            Jtype("101", field[2], field[3]);

        } else if (!Arrays.asList(OpcodePattern).contains(inst)) {      //Check Opcode is undefine
            System.out.println("Opcode is not define ---> " + inst);
            isRun = false;
        }

    }


    private static void setOtype(String field) {            //Set opcode of halt & noop
        if (field.equals("halt")) {
            Otype("110");
        } else if (field.equals("noop")) {
            Otype("111");
        }
    }

    private static void Rtype(String opcpde, String field0, String field1, String field2) {
        String regA = setReg(field0); //rs
        String regB = setReg(field1); //rt
        String regDst = setReg(field2); //rd
        String binary = opcpde + regA + regB + "0000000000000" + regDst;
        result = Integer.parseInt(binary, 2);
    }

    private static void Itype(String opcpde, String field0, String field1, String field2) {
        int temp = 0;
        String regA = setReg(field0); //rs
        String regB = setReg(field1); //rt
        String offsetField ="";
        if (isbeq) {
            if (Pattern.matches(pattern, field2)) {
                offsetField = setOffsetField(field2);
            } else {
                temp = setVariable(field2);
                temp = temp - pc;
                offsetField = setOffsetField(Integer.toString(temp));
            }
        }
        else {
            offsetField = setOffsetField(field2);
        }

        String binary = opcpde + regA + regB + offsetField;
       // System.out.println(binary);
        result = Integer.parseInt(binary, 2);
    }

    private static void Jtype(String opcpde, String field0, String field1) {
        String regA = setReg(field0); //rs
        String regB = setReg(field1); //rd
        String binary = opcpde + regA + regB + "0000000000000000";
        result = Integer.parseInt(binary, 2);
    }

    private static void Otype(String opcpde) {
        String binary = opcpde + "0000000000000000000000";
        result = Integer.parseInt(binary, 2);
    }

    private static String setReg(String field) {
        int test = Integer.valueOf(field);
        String temp = Integer.toBinaryString(convertStritoInt(field)); // convert decimal to binary
        if (test == 0 || test == 1) return "00" + temp;
        else if (test == 2 || test == 3) return "0" + temp;
        else return temp;
    }

    private static int convertStritoInt(String field) { // convert String to int
        return Integer.parseInt(field); // return convert int
    }

    private static String setOffsetField(String field) {
        int test = 0;
        if (Pattern.matches(pattern, field)) {  //ถ้า field เป็นตัวเลข
            test = Integer.valueOf(field);
        }
        else {
            test = setVariable(field);          //ถ้า field เป็นตัวอักษร
        }
        if (test > 32767 || test < -32768) {                            //Check offsetField > 16 bits
            System.out.println("error offsetField more than 16 bit");
            isRun = false;
        }

        String offsetField = Integer.toBinaryString(test);          //แปลง address เป็น binary string

        if (test < 0) {
            return offsetField.substring(16, 32);                  // ตัดเอาเฉพาะ 16 bits negative values ของ offsetField เพราะ assembler
        } else {
            for (int i = offsetField.length(); i < 16; i++) {
                offsetField = "0" + offsetField;
            }

            return offsetField;
        }
    }

    private static int setVariable(String field) {
        for (int i = 0; i < label.size(); i++) {
            if (field.equals(label.get(i))) {

                return valueOflabel.get(i); // โหลดค่าจาก label

            }
        }
        //not find in label
        System.out.println("does not exist variable " + field);
        isRun = false;
        return 0;

    }

    private static void print() throws IOException {
        write(Integer.toString(result));
        System.out.println(result);         //  แสดงผล

    }

}


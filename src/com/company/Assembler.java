package com.company;

import java.util.regex.Pattern;
import java.io.*;
import java.util.ArrayList;

public class Assembler {

    int l = 0; // number of line start at 0

    static String FileRead = "text\\assembly.txt";
    static String FileWrite = "texts\\OutputAssembly.txt";
    static ArrayList<String> label = new ArrayList<String>();
    static ArrayList<Integer> valueOflabel = new ArrayList<Integer>();
    static ArrayList<String> memory = new ArrayList<String>();
    private static boolean isRun ,isbeq;
    private static int pc, result;
    static String pattern = "^[\\-]?\\d*$";
    static BufferedWriter bw = null;
    static FileWriter fw = null;
    static PrintWriter pw = null;


    public static void main(String[] args) throws IOException {
        checkWriteFile();
        read(FileRead);
        isRun = true;
        pc = 0;
        Assembler();
    }

    public static void checkWriteFile(){ //delete FileWrite
        try{
            File file = new File(FileWrite);
            file.delete();
        }catch(Exception e){
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

    public static void Assembler() throws IOException {
        checklabel();
        for (int i=0; i<memory.size(); i++) {
            //if (checkIsLabel == false) break;
            isbeq = false;
            if (!isRun) break;
            String CurrentLine = memory.get(pc);
            String[] field = CurrentLine.split("\\s+"); // split white space
            pc++;

            if (field[1].equals(".fill")) {
                setFill(field[2]);
            }else if (field[1].equals("noop") || field[1].equals("halt")) {
                setOtype(field[1]);
            }else{
                setInst(field[1]);
            }

            print();
            result = 0;
        }
    }

    private static void checklabel(){
       for(int i=0; i<memory.size(); i++) {
           String CurrentLine = memory.get(i);
           String[] field = CurrentLine.split("\\s+"); // split white space
           if (!field[0].isEmpty()) { //check has label
               if (field[1].equals("fill")){ // check is Fill?
                   label.add(field[0]);
                   if (Pattern.matches(pattern, field[2])) { //เช็คว่าเป็นตัวเลขไหม
                       valueOflabel.add(convertStritoInt(field[2]));
                   }else {
                       for (int j=0; j<label.size(); j++){
                           if (label.get(j).equals(field[2])){
                               valueOflabel.add(valueOflabel.get(j));
                           }else {
                               System.out.println( "label does not exist" + field[2]);
                               isRun = false;
                           }

                       }
                   }
               }else{
                   label.add(field[0]);
                   valueOflabel.add(i);
               }
               int count = 0;
               for (int j=0; j <label.size(); j++) {
                   count = 0;
                   for (int k = 0; k < label.size(); k++)
                       if (label.get(j).equals(label.get(k)))
                           count++;
               }

               if (count >= 2){
                   System.out.println("label already has " + field[0]);
                   isRun = false;
               }
           }
       }
    }


    private static void setFill(String field) {
        if (Pattern.matches(pattern, field)) {
            result = convertStritoInt(field);
        } else {
            for (int i=0; i<label.size(); i++){
                if (field.equals(label.get(i))) {
                    result = valueOflabel.get(i);
                }
            }
        }
    }

    public static void setInst(String inst) {
        String CurrentLine = memory.get(pc-1);
        String[] field = CurrentLine.split("\\s+"); // split white space
        if(inst.equals("add")){
            Rtype("000", field[2], field[3] ,field[4]);
        }

        else if (inst.equals("nand")){
            Rtype("001", field[2], field[3] ,field[4]);
        }

        else if (inst.equals("lw")){
            Itype("010", field[2], field[3] ,field[4]);
        }

        else if (inst.equals("sw")){
            Itype("011", field[2], field[3] ,field[4]);
        }
        else if (inst.equals("beq")){
            isbeq = true;
            Itype("100", field[2], field[3] ,field[4]);
        }

        else if (inst.equals("jalr")){
            Jtype("101", field[2], field[3] );
        }
    }

    private static void setOtype(String field){
        if (field.equals("halt")) {
            Otype("110");
        }else if (field.equals("noop")){
            Otype("111");
        }
    }

    private static void Rtype(String opcpde, String field0 ,String field1, String field2){
        String regA = setReg(field0); //rs
        String regB = setReg(field1); //rt
        String regDst = setReg(field2); //rd
        String binary = opcpde + regA + regB + "0000000000000" + regDst;
        result = Integer.parseInt(binary ,2);
    }

    private static void Itype(String opcpde, String field0 ,String field1, String field2){
        int temp = 0;
        String regA = setReg(field0); //rs
        String regB = setReg(field1); //rt
        String offsetField = "";
        if (isbeq) {
            if (Pattern.matches(pattern, field2)) {
                offsetField =setOffsetField(field2);
            } else {
                temp = setVariable(field2);
                temp = temp - pc;
                offsetField = setOffsetField(Integer.toString(temp));
            }
        }else {
            offsetField = setOffsetField(field2);
        }

        String binary = opcpde + regA + regB + offsetField;
        result = Integer.parseInt(binary ,2);
    }

    private static void Jtype(String opcpde, String field0 ,String field1){
        String regA = setReg(field0); //rs
        String regB = setReg(field1); //rd
        String binary = opcpde + regA + regB + "0000000000000000";
        result = Integer.parseInt(binary ,2);
    }

    private static void Otype(String opcpde){
        String binary = opcpde + "0000000000000000000000" ;
        result = Integer.parseInt(binary ,2);
    }

    private static void print() throws IOException {
        write(Integer.toString(result));
        System.out.println(result);
    }

    private static int convertStritoInt(String field){ // convert String to int
        return Integer.parseInt(field); // return convert int
    }

    private static int setVariable(String field){
        for (int i=0 ; i<label.size(); i++)
            if (field.equals(label.get(i))) {
                return valueOflabel.get(i); // โหลดค่าจาก label
            }

        //not find in label
        System.out.println("does not exist variable " + field);
        isRun =false;
        return 0;
    }

    private static String  setRegDst(String field){
        if (Pattern.matches(pattern, field)) {
            return Integer.toBinaryString(convertStritoInt(field)); // convert decimal to binary
        }else{
            int temp =  setVariable(field);
            if (temp >7 || temp < 0){
                isRun =false;
                System.out.println("register has already 0-7");
                return "";
            }
            return Integer.toBinaryString(temp);
        }
    }


    private static String setOffsetField(String field){
        int test=0;
        if (Pattern.matches(pattern, field)) {
            test = Integer.valueOf(field);
        } else {
            test = setVariable(field);
        }
        if (test > 32767 || test < -32768) {
            isRun = false;
            System.out.println("error offsetField more than 16 bit");
        }

        String offsetField = Integer.toBinaryString(test);
        if (test < 0) {
            return offsetField.substring(16, 32);
        }else {
            for (int i=offsetField.length(); i<16; i++){
                offsetField = "0" + offsetField;
            }
            return offsetField;
        }
    }

    private static String setReg(String field){
        int test = Integer.valueOf(field);
        String temp = Integer.toBinaryString(convertStritoInt(field)); // convert decimal to binary
        if (test == 0 || test == 1) return "00"+ temp ;
        else if (test == 2 || test == 3) return "0" + temp;
        else return temp;
    }

}


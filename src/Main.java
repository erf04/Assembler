
import java.io.File;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.util.regex.Pattern;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) throws FileNotFoundException {

//        getInput();
//
//        if (!Assembler.checkSyntax()){
//            System.out.println("something wrong");
//            System.exit(-1);
//        }
//
//        String address="0x"+"0".repeat(16);
//        for (int i = 0; i < Assembler.getSourceCodeLines(); i++) {
//            Assembler assembler=new Assembler(Assembler.getLine(i+1));
//            if (!assembler.isLabel){
//                String machineCode= assembler.getMachineCode();
//                if (machineCode!=null){
//                    System.out.println(address+" : "+Assembler.convertToHex(assembler.getMachineCode()));
//                    address=assembler.generateNewAddress(address);
//                }
//                else{
//                    System.out.println("not correct");
//                }
//
//            }
//
//        }
        File file=new File("src/input.txt");
        HashMap<Segment,ArrayList<String>> result=segmentSeparator(file);
        for (String line:result.get(Segment.STACK)){
            System.out.println(line);
        }
        for (String line:result.get(Segment.DATA)){
            System.out.println(line);
        }
        for (String line:result.get(Segment.CODE)){
            System.out.println(line);
        }

//        Scanner scanner=new Scanner(file);
//
//        while(isNextLineBeginSegment(scanner)){
//            System.out.println(scanner.nextLine());
//        }



    }
    public static void getInput() throws FileNotFoundException{
        System.out.println("enter your choice:");
        System.out.println("1)cmd    2)file");
        Scanner scanner=new Scanner(System.in);
        int answer=Integer.parseInt(scanner.nextLine());
        if (answer==1){
            System.out.println("type the number of lines you want to assemble: ");
            int len=Integer.parseInt(scanner.nextLine());
            String[] lines=new String[len];
            System.out.println("ok!");
            for (int i = 0; i < len; i++) {
                lines[i]=scanner.nextLine();
            }
            Assembler.saveToDB(lines);


        }
        else if (answer==2){
            System.out.println("type your source file location:[leave blank to set it to (input.txt) next to the Main file] ");
            String addr=scanner.nextLine();
            if (addr.isEmpty()){
                addr="src/input.txt";
            }
            File inputFile=new File(addr);
            Assembler.saveToDB(inputFile);
        }

        else{
            System.out.println("not valid");
        }



    }

    public static Segment checkSegment(String name){
        if (name.equals("stack")){
            return Segment.STACK;
        }
        else if (name.equals("data")){
            return Segment.DATA;
        }
        else if (name.equals("code")){
            return Segment.CODE;
        }

        else{
            return null;
        }
    }



    public static HashMap<Segment,ArrayList<String>> segmentSeparator(File sourceFile) throws FileNotFoundException {
        Scanner scanner=new Scanner(sourceFile);
        HashMap<Segment,ArrayList<String>> result=new HashMap<>();
        String line=scanner.nextLine().trim();
        while(line.isEmpty()){
            line=scanner.nextLine().trim();
        }
        String segmentName=line.substring(1,6).trim();
        Segment segment=checkSegment(segmentName);
        String ss=line.substring(7,line.length()-1).trim();
        if (segment!=Segment.STACK){
            System.out.println("not valid segment");
            System.exit(-1);
        }

        else{
            line=scanner.nextLine().trim();
            ArrayList<String> segmentLines=new ArrayList<>();
            while(!line.startsWith(".")){
                if (!line.isEmpty())
                    segmentLines.add(line);
                line=scanner.nextLine().trim();
            }
            result.put(segment,segmentLines);

        }

        segmentName=line.substring(1,5).trim();
        segment=checkSegment(segmentName);
        String ds=line.substring(6,line.length()-1).trim();
        if (segment!=Segment.DATA){
            System.out.println("not valid segment");
            System.exit(-1);
        }
        else{
            line=scanner.nextLine().trim();
            ArrayList<String> segmentLines=new ArrayList<>();
            while(!line.startsWith(".")){
                if (!line.isEmpty())
                    segmentLines.add(line);
                line=scanner.nextLine().trim();
            }
            result.put(segment,segmentLines);

        }

        segmentName=line.substring(1,5).trim();
        segment=checkSegment(segmentName);
        String cs=line.substring(6,line.length()-1).trim();
        if (segment!=Segment.CODE){
            System.out.println("not valid segment");
            System.exit(-1);
        }
        else{
            ArrayList<String> segmentLines=new ArrayList<>();
            while(scanner.hasNextLine()){
                line=scanner.nextLine().trim();
                if (!line.isEmpty())
                    segmentLines.add(line);
            }
            result.put(segment,segmentLines);

        }
        return result;
    }

    public static void csHandler(String beginAddr,ArrayList<String> codes){

    }

    public static HashMap<String,String> dsHandler(String beginAddr,ArrayList<String> codes){
        for (String line:codes){
            try {
                String type= line.split(" ")[1];
            }catch (Exception e){
                System.out.println("syntax error in data segment");
            }



        }
    }




}


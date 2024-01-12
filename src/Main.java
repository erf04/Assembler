
import java.io.File;

import java.util.*;
import java.io.FileNotFoundException;

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
//        for (String line:result.get(Segment.STACK)){
//            System.out.println(line);
//        }
//        for (String line:result.get(Segment.DATA)){
//            System.out.println(line);
//        }
//        for (String line:result.get(Segment.CODE)){
//            System.out.println(line);
//        }
        Segment datasegment=Segment.findBySegmentType(SegmentType.DATA,result);
        Segment codesegment=Segment.findBySegmentType(SegmentType.CODE,result);
        Segment stacksegment=Segment.findBySegmentType(SegmentType.STACK,result);

        if (datasegment!=null){
          String ds=datasegment.addressPointer;
          HashMap <String,String> answer=dsHandler(ds,result.get(datasegment));
          ArrayList<HashMap<String,String>> hash=csHandler(codesegment.addressPointer, stacksegment.addressPointer,result.get(codesegment));
            System.out.println("oskfnhosknf");
        }
        else{
            System.out.println("segment not found");
        }


        System.out.println("the end");





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
            Assembler.saveToDB(new ArrayList<String>(List.of(lines)));


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




    public static HashMap<Segment,ArrayList<String>> segmentSeparator(File sourceFile) throws FileNotFoundException {
        Scanner scanner=new Scanner(sourceFile);
        HashMap<Segment,ArrayList<String>> result=new HashMap<>();
        String line=scanner.nextLine().trim();
        while(line.isEmpty()){
            line=scanner.nextLine().trim();
        }
        Segment stackSegment=new Segment();
        String segmentName=line.substring(1,6).trim();
        SegmentType segmentType=stackSegment.setSegmentType(segmentName);
        String ss=line.substring(7,line.length()-1).trim();
        stackSegment.addressPointer=ss;
        if (segmentType!=SegmentType.STACK){
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
            result.put(stackSegment,segmentLines);


        }
        Segment dataSegment=new Segment();
        segmentName=line.substring(1,5).trim();
        SegmentType segmentType1=dataSegment.setSegmentType(segmentName);
        String ds=line.substring(6,line.length()-1).trim();
        if (segmentType1!=SegmentType.DATA){
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
            dataSegment.addressPointer=ds;
            result.put(dataSegment,segmentLines);


        }
        Segment codeSegment=new Segment();
        segmentName=line.substring(1,5).trim();
        SegmentType segmentType2=codeSegment.setSegmentType(segmentName);
        String cs=line.substring(6,line.length()-1).trim();
        if (segmentType2!=SegmentType.CODE){
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
            codeSegment.addressPointer=cs;
            result.put(codeSegment,segmentLines);

        }
        return result;
    }

    public static ArrayList<HashMap<String,String>> csHandler(String codeBeginAddr, String stackBeginAddr, ArrayList<String> codes){
        String codeCurrentAddr=codeBeginAddr;
        String stackCurrentAddr=stackBeginAddr;
        HashMap<String,String> csResult=new HashMap<>();
        HashMap<String,String> ssResult=new HashMap<>();
        ArrayList<HashMap<String,String>> result=new ArrayList<>();
        Assembler.saveToDB(codes);
        if (!Assembler.checkSyntax()){
            System.out.println("something wrong in the code segment");
            System.exit(-1);
        }

        for (int i = 0; i < Assembler.getSourceCodeLines(); i++) {
            Assembler assembler=new Assembler(Assembler.getLine(i+1));
            if (!assembler.isLabel){
                String machineCode= assembler.getMachineCode();
                if (machineCode!=null){
//                    System.out.println(address+" : "+Assembler.convertToHex(assembler.getMachineCode()));
//                    address=assembler.generateNewAddress(address);
                try {
                    if (assembler.instruction.trim().equals("push")){
                        int codeCurrentAddrInt=Integer.parseInt(codeCurrentAddr);
                        int stackCurrentAddrInt=Integer.parseInt(stackCurrentAddr);
                        int size;
                        if (assembler.operand1.type==OperandType.REGISTER)
                            size=Assembler.getRegisterSize(assembler.operand1);
                        else
                            size=1;
                        for (int j=0;j<size;j++){
                            ssResult.put(stackCurrentAddr,assembler.operand1.name);
                            stackCurrentAddrInt++;
                            stackCurrentAddr=stackCurrentAddrInt+"";

                        }

                        for (int j=0;j!=4-size;j++){
                            ssResult.put(stackCurrentAddr,"MM");
                            stackCurrentAddrInt++;
                            stackCurrentAddr=stackCurrentAddrInt+"";
                        }




                    }
                    else if (assembler.instruction.trim().equals("pop")){

                    }
                }catch (Exception e){
                    System.out.println("not valid begin address for code segment or stack segment");
                }

                }
                else{
                    System.out.println("not correct");
                }

            }

        }
        result.add(ssResult);
        return result;




    }

    public static HashMap<String,String> dsHandler(String beginAddr,ArrayList<String> codes){
        String currentAddr=beginAddr;
        HashMap<String,String> result=new HashMap<>();
        for (String line:codes){
            String type="",name="";
            try {
                type= line.split(" ")[1];
                name=line.split(" ")[0];
            }catch (Exception e){
                System.out.println("syntax error in data segment");
                System.exit(-1);
            }

            try {

                int currentAddrInt=Integer.parseInt(currentAddr);
                int size=Data.types.get(type.trim());
                for (int i=0;i<size;i++){
                    result.put(currentAddrInt+i+"",name);
                }
                currentAddr=currentAddrInt+size+"";


            }catch (Exception e){
                System.out.println("not valid begin address for data segment");
                System.exit(-1);
            }


        }
        return result;
    }




}


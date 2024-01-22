import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Segment {
    SegmentType type;
    String addressPointer;

    private String currentPointer;

    public SegmentType setSegmentType(String name){
        if (name.equals("stack")){
            this.type=SegmentType.STACK;
            return type;
        }
        else if (name.equals("data")){
            this.type=SegmentType.DATA;
            return type;
        }
        else if (name.equals("code")){
            this.type=SegmentType.CODE;
            return type;
        }

       return null;

    }

    public static Segment findBySegmentType(SegmentType type){
        for (Map.Entry<Segment, ArrayList<String>> entry:Data.segments.entrySet() ){
            if (entry.getKey().type==type){
                return entry.getKey();
            }
        }
        return null;
    }

    public String getCurrentPointer(){
        if (currentPointer!=null)
            return currentPointer;
        System.out.println("current pointer isn't set yet");
        return null;
    }

    public void setCurrentPointer(String value){
        this.currentPointer=value;
    }


    public HashMap<String,String> getMemoryArrangement(){
        if (type==SegmentType.DATA){
            return Data.dataSegmentResult;
        }
        else if (type==SegmentType.STACK){
            return Data.stackSegmentResult;
        }
        else if (type==SegmentType.CODE){
            return Data.codeSegmentResult;
        }

        System.out.println("not valid");
        return null;

    }

    public static void segmentsHandler(){
        dsHandler();
        csHandler();

    }


    public static HashMap<Segment,HashMap<String,String>> csHandler(){
        Segment codesegment=Segment.findBySegmentType(SegmentType.CODE);
        Segment stacksegment=Segment.findBySegmentType(SegmentType.STACK);
        assert codesegment != null:"code segment doesnt exist";
        String codeCurrentAddr=codesegment.addressPointer;
        assert stacksegment != null:"stack segment doesnt exist";
        String stackCurrentAddr=stacksegment.addressPointer;
        int codeCurrentAddrInt=0,stackCurrentAddrInt=0;
        try {
            codeCurrentAddrInt=Integer.parseInt(codeCurrentAddr);

        }catch (Exception e){
            System.out.println("wrong pointer for code segment");
        }
        try {
            stackCurrentAddrInt=Integer.parseInt(stackCurrentAddr);
        }catch (Exception e){
            System.out.println("wrong pointer for stack segment");
        }

        HashMap<String,String> csResult=new HashMap<>();
        HashMap<String,String> ssResult=new HashMap<>();
        HashMap<Segment,HashMap<String,String>> result=new HashMap<>();
        Assembler.saveToDB(Data.segments.get(codesegment));
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

                    if (assembler.instruction.trim().equals("push")){

                        int size;
                        if (assembler.operand1.type==OperandType.REGISTER)
                            size=Assembler.getRegisterSize(assembler.operand1);
                        else
                            //it's an immediate(considering that is 8bit)
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
                        stackCurrentAddrInt--;
                        for (int j = 0; j < Assembler.getRegisterSize(assembler.operand1); j++) {
                            ssResult.remove(stackCurrentAddrInt-- +"");
                            stackCurrentAddr=stackCurrentAddrInt+"";
                        }

                    }
                    //add hex to code segment
                    String hexAnswer=Assembler.convertToHex(machineCode);
                    ArrayList<String> littleEndian=Assembler.toLittleEndian(hexAnswer);
                    for (String Byte:littleEndian){
                        csResult.put(codeCurrentAddr,Byte);
                        codeCurrentAddrInt++;
                        codeCurrentAddr=codeCurrentAddrInt+"";
                    }
                    //fill the rest
                    if (codeCurrentAddrInt%2!=0){
                        csResult.put(codeCurrentAddr,"MM");
                        codeCurrentAddrInt++;
                        codeCurrentAddr=codeCurrentAddrInt+"";
                    }


                }
                else{
                    System.out.println("not correct with the instructions");
                }

            }

        }

        //update the pointers
        codesegment.setCurrentPointer(codeCurrentAddr);
        stacksegment.setCurrentPointer(stackCurrentAddr);
        //fill till to byte32 for code segment
        int codebeginAddr=Integer.parseInt(codesegment.addressPointer);
        for (int i=codeCurrentAddrInt;i<32+codebeginAddr;i++){
            csResult.put(i+"","MM");
        }

        //fill till to byte32 for stack segment

        int stackBeginAddr=Integer.parseInt(stacksegment.addressPointer);
        for (int i=stackCurrentAddrInt;i<32+stackBeginAddr;i++){
            ssResult.put(i+"","MM");
        }
        //set result
        result.put(codesegment,csResult);
        result.put(stacksegment,ssResult);
        //save to db
        Data.codeSegmentResult=csResult;
        Data.stackSegmentResult=ssResult;
        return result;

    }


    public static HashMap<String,String> dsHandler(){

        HashMap<String,String> result=new HashMap<>();
        Segment data=Segment.findBySegmentType(SegmentType.DATA);
        ArrayList<String > codes=Data.segments.get(data);
        assert data != null:"data segment doesnt exist";
        String currentAddr=data.addressPointer;
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
        data.setCurrentPointer(currentAddr);
        //fill till the 32
        int currentAddrInt=Integer.parseInt(currentAddr);
        int beginAddr=Integer.parseInt(data.addressPointer);
        for (int i=currentAddrInt;i<32+beginAddr;i++){
            result.put(i+"","MM");
        }
        Data.dataSegmentResult=result;
        return result;
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

        Data.segments=result;
        return result;
    }

    public static void memoryVisualizeByType(SegmentType type){
        HashMap<String,String> hash;
        Segment segment=Segment.findBySegmentType(type);
        if (type==SegmentType.DATA){
            hash=Data.dataSegmentResult;
        }
        else if (type==SegmentType.STACK){
            hash=Data.stackSegmentResult;
        }
        else if (type==SegmentType.CODE){
            hash=Data.codeSegmentResult;
        }
        else{
            System.out.println("something wrong");
            return;
        }

        assert segment != null;
        showMemory(segment,hash);

    }

    public static void showMemory(Segment segment,HashMap<String,String> hash){
        String leftAlignFormat = "| %-7s --> %-10s |%n";
        System.out.format("+-----------------+------+%n");
        System.out.format("| address -->    value   |%n");
        System.out.format("+-----------------+------+%n");
        assert segment != null;
        int pointer=Integer.parseInt(segment.addressPointer);
        int currentPointer=Integer.parseInt(segment.currentPointer);
        int size=currentPointer-pointer;
        for (int i=0;i<size;i++){
            String value=hash.get(pointer+"");
            System.out.format(leftAlignFormat,"  "+pointer,  "   "+value);
            pointer++;
        }
        System.out.format("+-----------------+------+%n");
    }

    public static void showTheFinal(HashMap<String,String> hash){
        String leftAlignFormat = "| %-7s --> %-10s |%n";
        System.out.format("+-----------------+------+%n");
        System.out.format("| address -->    value   |%n");
        System.out.format("+-----------------+------+%n");
        int size=256;
        for (int i=0;i<size;i++){
            String value=hash.get(i+"");
            System.out.format(leftAlignFormat,"  "+i,  "   "+value);
        }
        System.out.format("+-----------------+------+%n");
    }

    public static ArrayList<Segment> findSegmentArrangement(){
        Segment codeSegment=Segment.findBySegmentType(SegmentType.CODE);
        Segment stackSegment=Segment.findBySegmentType(SegmentType.STACK);
        Segment dataSegment=Segment.findBySegmentType(SegmentType.DATA);
        assert codeSegment!=null;
        assert  stackSegment!=null;
        assert dataSegment!=null;
        int cs=0,ss=0,ds=0;
        try {
            cs=Integer.parseInt(codeSegment.addressPointer);
        }catch (Exception e){
            System.out.println("failed to process code pointer");
            System.exit(-1);
        }
        try {
            ss=Integer.parseInt(stackSegment.addressPointer);
        }catch (Exception e){
            System.out.println("failed to process stack pointer");
            System.exit(-1);
        }
        try {
            ds=Integer.parseInt(dataSegment.addressPointer);
        }catch (Exception e){
            System.out.println("failed to process data pointer");
            System.exit(-1);
        }

        TreeMap<Integer,Segment> list=new TreeMap<>(Integer::compareTo);
        list.put(cs,codeSegment);
        list.put(ss,stackSegment);
        list.put(ds,dataSegment);
//        System.out.println(list);
        ArrayList<Segment> answer=new ArrayList<>();
        for (Map.Entry<Integer,Segment> s:list.entrySet()){
            answer.add(s.getValue());
        }
        return answer;

    }
}

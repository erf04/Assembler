
import java.io.File;

import java.util.*;
import java.io.FileNotFoundException;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {

        File file=new File("src/input.txt");
        HashMap<Segment,ArrayList<String>> result=Segment.segmentSeparator(file);
        Segment.segmentsHandler();
        Segment datasegment=Segment.findBySegmentType(SegmentType.DATA);
        Segment codesegment=Segment.findBySegmentType(SegmentType.CODE);
        Segment stacksegment=Segment.findBySegmentType(SegmentType.STACK);
        ArrayList<Segment> segmentsByArrangement=Segment.findSegmentArrangement();

//        for (Segment segment:segmentsByArrangement){
//            segment.memoryVisualize();
//        }
        //handle xx's and mm's//

        Segment.showTheFinal(theFinalResult());

        System.out.println("the end");
    }



    public static HashMap<String, String> theFinalResult(){
        List<Segment> segmentArrangement=Segment.findSegmentArrangement();

        HashMap<String,String> finalResult=new HashMap<>();
        for (Segment segment:segmentArrangement){
            finalResult.putAll(segment.getMemoryArrangement());
        }

        for (int i=0;i<256;i++){
            finalResult.putIfAbsent(i + "", "XX");
        }
        return finalResult;

    }










}


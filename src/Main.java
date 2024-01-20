
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
        ArrayList<Segment> segmentsByArrangement=findSegmentArrangement();

        for (Segment segment:segmentsByArrangement){
            segment.memoryVisualize();
        }

        //handle xx's and mm's//


        System.out.println("the end");
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


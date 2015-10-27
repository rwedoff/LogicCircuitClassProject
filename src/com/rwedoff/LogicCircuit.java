package com.rwedoff;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.PriorityQueue;

/**
* This program is a digital logic circuit runSimulation, it takes in an input file, parses the
* data into objects then runs a digitial logic simulation of the gates and wire running, a log is printed to standard out.
* @author Ryan Wedoff
* @version mp3
*
* Citations:  Mostly Ryan Wedoff's solution to MP1 & MP2 with added elements of Professor Jones Solution to MP1 & MP2
*/

abstract class Event{
    /**Event is an abstract class that is the framework of GateEvent and WireEvent
     * @see GateEvent
     * @see WireEvent
     */
    private float eventDelay;
    private Object eventObject;
    public Event(Gate g, float simTime){
        eventDelay = simTime;
        eventObject = g;
    }
    public Event(Wire w, float simTime){
        eventDelay = simTime;
        eventObject = w;
    }

    public Object getEventObject() {
        return eventObject;
    }

    public float getEventDelay() {
        return eventDelay;
    }

    @Override
    public String toString(){
        return "Event: " + eventObject.toString();
    }
    public abstract ArrayList<Event> runEvent(float delay, HashMap<String, Gate> gates);

}
class GateEvent extends Event{
    /**Gate event is a subclass of event.  It is an event handler that is specific to gates
     * @see Gate
     * @see Event
     */
    private Gate gate;
    public GateEvent(Gate g, float simTime) {
        super(g, simTime);
        gate = g;
    }

    @Override
    public ArrayList<Event> runEvent(float delay, HashMap<String, Gate> gates) {
        return gate.runGateEvent(delay);
    }
}

class WireEvent extends Event{
    /**Wire event is a subclass of event.  It is an event that is specific to wires
     * @see Wire
     * @see Event
     */
    private Wire wire;
    public WireEvent(Wire w, float simTime) {
        super(w, simTime);
        wire  = w;
    }

    @Override
    public ArrayList<Event> runEvent(float delay, HashMap<String, Gate> gates) {
        ArrayList<Event> res = new ArrayList<>();
        res.add(wire.runWireEvent(delay,gates));
        return res;
    }

}

class EventComparator implements Comparator<Event>{
    /**Class to order/compare Event by durration
     * @see Event
     */
    @Override
    public int compare(Event x, Event y)
    {
        if (x.getEventDelay() < y.getEventDelay())
        {
            return -1;
        }
        if (x.getEventDelay() > y.getEventDelay())
        {
            return 1;
        }
        return 0;
    }
}

class RoundFloat{
    public static float round(float value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (float) tmp / factor;
    }
}

class Errors {
    /** Error reporting framework
     * This idea was taken from Prof. Jones solution MP1
    */
    static void fatal( String message ) {
        /** Report a fatal error with the given message
        */
        System.err.println( "Error: " + message );
        System.exit( 1 );
    }
    static void warn( String message ) {
    /** Report a nonfatal error with the given message
     */
        System.err.println( "Error: " + message );
    }
}

class SyntaxCheck {
/** Syntax checking support
 * This idea was taken from Prof Jones' solution to MP1, however the method is not the same
 */
    public static boolean lineEnd(Scanner sc, String c){
        if(sc.hasNext()){
            Errors.warn(c + " --has non-empty line end");
            return false;
        }else
            return true;
    }
}

 abstract class Gate{
     /**
      * A gate is a an input that has a digital logic operator.
      * There are many types of gates and this is just the parent class
      * Gates are connected by wires.
      * @see Wire
      * @see AndGate
      * @see OrGate
      * @see NotGate
      */
     private float delay;  //delay of the gate
     private String gateType;
     private String gateName;
     private Wire [] inputList;
     boolean gateValue; //Bool value of what the gate is
     private ArrayList<Wire> outWires;

     /**Getters and setters for gate class */
     public float getDelay() {  return delay;   }
     public void setDelay(float delay) {   this.delay = delay;   }
     public String getGateName() { return gateName;  }
     public void setGateType(String gateType) { this.gateType = gateType;    }
     public void setGateName(String gateName) {  this.gateName = gateName;  }
     public String getGateType() {  return gateType;  }
     public Wire[] getInputList() {  return inputList;    }
     public void setInputList(Wire[] inputList) { this.inputList = inputList;   }
     public ArrayList<Wire> getOutWires() {
         return outWires;
     }

     public void addToOutWire(Wire w){
         outWires.add(w);
     }

     public boolean scanGate(String operation, String gt){
         /**reads the text file input and checks if all the input is valid
          *
          * @param operation
          * @param gt
          * @return
          */
         outWires = new ArrayList<>();
         gateValue = false;
         Scanner sc = new Scanner(operation);
         try {
             setGateName(sc.next());
             if(sc.hasNextFloat()){
                 setDelay(sc.nextFloat());
        setGateType(gt);
        if(SyntaxCheck.lineEnd(sc, "gate " + gt + " " + getGateName()))
            return true;
    }
    else Errors.warn( sc.next() + ", number expected");
} catch (NoSuchElementException e) {
        Errors.warn("Gate: Not enough arguments given");
        }
        return false;
        }

public boolean setAllInputs(Wire w, int inputNum){
        Wire[] currInputList = inputList;
        if(inputNum >= currInputList.length){
        Errors.warn(inputNum + " --Input type not valid for  source gate: " + gateName + " type: " + gateType);
        return false;
        } else if(currInputList[inputNum] != null){
        Errors.warn(gateName + ", input " + inputNum + " is already taken");
        return false;
        } else{
        currInputList[inputNum] = w;
        return true;
        }
 }

    public String inputName(int in){
        if(in == 0){
        return "in1";
        } else if(in == 1){
        return "in2";
        } else{
        Errors.warn( in + " is not a valid input");
        return "";
        }
    }
     public abstract ArrayList<Event> runGateEvent(float delay);

    //Abstract Methods
    public abstract String toString();


 }

class AndGate extends Gate{
    //Gate constructor
    public AndGate(String operation, HashMap<String,Gate> gates) {

        /*And gate is a subclass of Gate.  And gate takes two inputs
      */
        Wire[] inputs = new Wire[2];
        super.setInputList(inputs);
        if(scanGate(operation, "and"))
            if(gates.containsKey(getGateName()))
                Errors.warn("Gate '" + getGateName() + "' has already been defined");
            else
                gates.put(getGateName(),this);
    }

    @Override
    public ArrayList<Event> runGateEvent(float simDelay) {
        //System.out.println("CHANGE AND");
        Wire [] inList = this.getInputList();
        LinkedList<Boolean> logicVals = new LinkedList<>();
        for(int i = 0; i<inList.length; i++)
            logicVals.add(inList[i].wireValue);
        boolean changeVal = logicVals.get(0) && logicVals.get(1);
       // System.out.println(gateValue + " : CHANGE VAL: " + changeVal);
        if(gateValue == changeVal) {
            return null;
        } else{

            gateValue = changeVal;
            float totalDelay = simDelay + getDelay();
            totalDelay = RoundFloat.round(totalDelay,2);
            System.out.println("Time " + totalDelay + " Gate " + getGateName() + " goes to " + gateValue);
            ArrayList<Event> res = new ArrayList<>();
            for(Wire w: getOutWires()){
                res.add(new WireEvent(w, totalDelay));
            }
            return res;
        }
    }

    @Override
    public String toString(){
        return  "gate and" + " " +  this.getGateName() +  " " + this.getDelay();
    }
}

class OrGate extends  Gate{
    /**
     * OrGate is a subclass of Gate
     * OrGate takes two inputs
     * @see Gate
     */
    public OrGate(String operation, HashMap<String,Gate> gates) {
        /*Gate constructor reads the text file input and checks if all the input is valid*/
        Wire[] inputs = new Wire[2];
        super.setInputList(inputs);
        if(scanGate(operation, "or"))
            if(gates.containsKey(getGateName()))
                Errors.warn("Gate '" + getGateName() + "' has already been defined");
            else
                gates.put(getGateName(),this);
    }

    @Override
    public String toString(){
        return  "gate or" + " " +  this.getGateName() +  " " + this.getDelay();

    }
    @Override
    public ArrayList<Event> runGateEvent(float simDelay) {
        Wire [] inList = this.getInputList();
        LinkedList<Boolean> logicVals = new LinkedList<>();
        //Boolean [] logicVals = new Boolean[2];
        for(int i = 0; i<inList.length; i++)
            logicVals.add(inList[i].wireValue);
        boolean changeVal = logicVals.get(0) || logicVals.get(1);
        if(gateValue == changeVal) {
            return null;
        } else{
            gateValue = changeVal;
            float totalDelay = simDelay + getDelay();
            totalDelay = RoundFloat.round(totalDelay,2);
            System.out.println("Time " + totalDelay + " Gate " + getGateName() + " goes to " + gateValue);
            ArrayList<Event> res = new ArrayList<>();
            for(Wire w: getOutWires()){
                res.add( new WireEvent(w, totalDelay));
            }
            return res;
        }
    }
}


class NotGate extends Gate{
    //Gate constructor reads the text file input and checks if all the input is valid
    public NotGate(String operation, HashMap<String,Gate> gates) {
        /** NotGate is a subclass of Gate  NotGate takes only 1 input     */
        Wire[] inputs = new Wire[1];
        super.setInputList(inputs);
        if(scanGate(operation, "not")){
            if(gates.containsKey(getGateName()))
                Errors.warn("Gate '" + getGateName() + "' has already been defined");
            else
                gates.put(getGateName(),this);
        }
    }

    @Override
    public String toString(){
        return  "gate not" + " " +  this.getGateName() +  " " + this.getDelay();
    }

    @Override
    public String inputName(int in){
        if(in == 0) {
            return "in";
        }else{
            Errors.warn( in + " is not a valid input");
            return "";
        }
    }
    @Override
    public ArrayList<Event> runGateEvent(float simDelay) {
        /**
         * Run gate is an abstract method of Gate that returns a list of events that are triggered in a simulartion
         * @see LogicCircuit.Simulation
         */
        Wire [] inList = this.getInputList();
        LinkedList<Boolean> logicVals = new LinkedList<>();
        //Boolean [] logicVals = new Boolean[2];
        for(int i = 0; i<inList.length; i++)
            logicVals.add(inList[i].wireValue);
            gateValue = !logicVals.get(0);
            float totalDelay = simDelay + getDelay();
            totalDelay = RoundFloat.round(totalDelay,2);
            System.out.println("Time " + totalDelay + " Gate " + getGateName() + " goes to " + gateValue);
            ArrayList<Event> res = new ArrayList<>();
            for(Wire w: getOutWires()){
                res.add( new WireEvent(w, totalDelay));
            }
            return res;
    }
}

class Wire{
    /** Wires connect gates and have a time delay
     * @see Gate
     */
    float delay; //Delay of this wire
    Gate driven;  //what gates does this wire drive
    Gate driver;
    int input; //What input of that gate does this wire drive
    String sourceGate, destGate;
    boolean wireValue;  //Bool value of what the wire is

    //Wire constructor
    public Wire(HashMap<String,Gate> gates, String ops, ArrayList<Wire> wires){
        wireValue = false;
        if(scanWire(gates,ops))
            wires.add(this);
    }

    public boolean scanWire(HashMap<String,Gate> gates, String ops){
        /**reads the text file input and checks if all the input is valid
         */
        try {
            try (Scanner sc = new Scanner(ops)) {
                sourceGate = sc.next();
                destGate = sc.next();
                if(sc.hasNext()){
                    //StringInput to int conversion
                    String strInput = sc.next();
                    if(strInput.equals("in1")) input = 0;
                    else if(strInput.equals("in2")) input = 1;
                    else if(strInput.equals("in") && gates.get(destGate).getGateType().equals("not")) input = 0;
                    else Errors.warn("wire " + sourceGate + " "+ destGate +  " Input String Expected (in1, in2, in)");
                }
                if(sc.hasNextFloat()) {
                    delay = sc.nextFloat();
                } else{
                    Errors.warn("wire " + sourceGate + " " + destGate + ", --number for delay expected");
                    return false;
                }
                if(SyntaxCheck.lineEnd(sc, this.toString()) && checkWire(gates)) {
                    return true;
                }
            }
        } catch (NoSuchElementException e) {
            Errors.warn("Wire -- wrong format");
        }
        return  false;
    }
    public boolean checkWire(HashMap<String,Gate> gates){
        /**Calls methods that check is the source and driver gate are valid and have valid inputs
         */
        return setSourceGate(gates) && checkDriverGate(gates) && setInput(gates);
    }

    private boolean setSourceGate(HashMap<String,Gate> gates){
        /**Checks to see if the sourceGate is valid
         */
        if(gates.containsKey(sourceGate)){
            Gate g = gates.get(sourceGate);
            g.addToOutWire(this);
            return true;
        } else{
            Errors.warn(sourceGate + ", no such source gate");
            return false;
        }
    }
    private boolean setInput(HashMap<String, Gate> gates){
        /**Checks to see if the source gate given has empty inputs*/
        Gate g = gates.get(destGate);
        if(g.setAllInputs(this, input)) {
            driven = gates.get(destGate);
            return true;

        } else{
            return false;
        }
    }
    private boolean checkDriverGate(HashMap<String,Gate> gates){
        /**Checks to see if the driver gate is a valid gate*/
        if(gates.containsKey(destGate)){
            driver = gates.get(destGate);
            return true;
        } else{
            Errors.warn(destGate + ", no such destination gate");
            return false;
        }
    }

    @Override
    public String toString(){
        return  "wire" + " " + sourceGate + " " + destGate + " " + input + " " + delay;
    }

    /**RunWireEvent is the method that a event uses to change the value of a wire and then schedule a new event
     * @see Event
     */
    public Event runWireEvent(float simDelay, HashMap<String, Gate> gates) {
        Gate sourceGate = gates.get(this.sourceGate);
        Gate destGate = gates.get(this.destGate);
        wireValue = sourceGate.gateValue;
        float totalDelay = simDelay + delay;
        totalDelay = RoundFloat.round(totalDelay,2);
        return new GateEvent(destGate, totalDelay);

    }
}

public class LogicCircuit {
    /**
     * A logic circuit consists of gates with inputs that are connected with wires
     * @see Gate
     * @see Wire
     */
    static HashMap<String, Gate> gates = new HashMap<>();
    static ArrayList<Wire> wires = new ArrayList<>();

    static void initCircuit(Scanner sc) {
        while (sc.hasNext()) { //Scan each line until the input file is finished
            String command = sc.next();
            if ((command.equalsIgnoreCase("gate"))) {
                String gateType = sc.next();
                String ops = sc.nextLine();
                if (gateType.equalsIgnoreCase("and")) {
                    new AndGate(ops, gates);
                } else if (gateType.equalsIgnoreCase("not")) {
                    new NotGate(ops, gates);
                } else if (gateType.equalsIgnoreCase("or")) {
                    new OrGate(ops, gates);
                } else {
                    Errors.warn(gateType + ", no such type of gate");
                }
            } else if (command.equalsIgnoreCase("wire")) {
                String ops = sc.nextLine();
                new Wire(gates, ops, wires);
            } else if (command.contains("//")) { //This allows for line comments in the input language
                sc.nextLine();
            } else {
                Errors.warn(command + " is not a valid operation (Gate or Wire)");
            }
        }


    }

    public static void writeCircuit() {
        /**Write out the whole circuit minus error
         */
        for (String key : gates.keySet()) {
            System.out.println(gates.get(key));
        }

        for (Wire w : wires) {
            System.out.println(w);
        }
    }

    public static boolean completeness() {
        for (String key : gates.keySet()) {
            Gate g = gates.get(key);
            Wire[] inList = g.getInputList();
            for (int i = 0; i < inList.length; i++) {
                if (inList[i] == null) {
                    Errors.warn("Unused Input in gate: '" + g.getGateName() + "' type: " + g.getGateType() + " input: " + g.inputName(i));
                    return false;
                }
            }
        }
        return true;
    }


static class Simulation{
    /**
     * Simulation is an inner class of LogicCircuit and is used to run a runSimulation of the logic circuit
     * @see LogicCircuit
     */

    public static void runSimulation() {
        /**
         * Runs initialization of a LogicCircuit simulation and then runs the simulation
         * @see LogicCircuit
         */
        Comparator<Event> comparator = new EventComparator();
        PriorityQueue<Event> eventQueue = new PriorityQueue<>(11, comparator);
        initSimulation(eventQueue);  //Initializes the simulation
        run(eventQueue);  //Runs the simulation if the event queue isn't empty
    }

    public static void initSimulation(PriorityQueue<Event> eventQueue){
         /**  Initialize all values false in order switch not to true
         */
        for (String key : gates.keySet()) {
            Gate g = gates.get(key);
            if (g.getGateType().equals("not")) {
                Event e = new GateEvent(g, 0);
                eventQueue.add(e);
            }
        }
    }
    public static void run(PriorityQueue<Event> eventQueue){
        while (!eventQueue.isEmpty()) {
            Event e = eventQueue.poll();

            ArrayList<Event> list = e.runEvent(e.getEventDelay(), gates);
                if (list != null) {
                    for (Event newEvent : list)
                        eventQueue.add(newEvent);
                }
        }
    }
}

    public static void main(String[] args) {

        Scanner sc;
        if (args.length < 1) {
            Errors.fatal("Missing filename argument");
        }
        if (args.length > 1) {
            Errors.fatal("Extra command-line arguments");
        }
        try {
            sc = new Scanner(new File(args[0]));
            LogicCircuit.initCircuit(sc);
        } catch (FileNotFoundException e) {
            Errors.fatal("The file is not found!");
        }

        if(completeness()) { //Checks to see if the LogicCircuit is complete
            System.out.println("Logic Circuit: ");
            writeCircuit();
            System.out.println("\nSimulation: ");
            Simulation.runSimulation();  //Runs methods that run a simulation of a LogicCircuit
        }
    }
}
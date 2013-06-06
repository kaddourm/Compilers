
// main method is at end of code

import java.io.IOException;
import java.util.Stack;
import java.util.LinkedList;

public class RUM {
        private interface Visitor {
                
                void visit (Left left);
                void visit (Right right);
                void visit (Increment increment);
                void visit (Decrement decrement);
                void visit (Input input);
                void visit (Output output);
                void visit (Loop loop);
                void visit (Program program);
                void visit (Repeat repeat);
                void visit (StringInit stringInit);
                void visit (StringExit stringExit);
                void visit (Declaration declaration);
                void visit (Procedure procedure);
                void visit (Car car);
                void visit (BreakPnt breakpnt);
                
        }
               
        private interface Node {
                void accept (Visitor v);
        }
        
        // Define node types
        private class Left implements Node {
                public void accept (Visitor v) {
                        v.visit(this);
                }
        }
        private class Right implements Node {
                public void accept (Visitor v) {
                        v.visit(this);
                }
        }
        private class Increment implements Node {
                public void accept (Visitor v) {
                        v.visit(this);
                }
        }
        private class Decrement implements Node {
                public void accept (Visitor v) {
                        v.visit(this);
                }
        }
        private class Input implements Node {
                public void accept (Visitor v) {
                        v.visit(this);
                }
        }
        private class Output implements Node {
                public void accept (Visitor v) {
                        v.visit(this);
                }
        }
        private class Loop implements Node {
                public Node child;
                public Loop (Node child) {
                        this.child = child;
                }
                public void accept (Visitor v) {
                        v.visit (this);
                }
        }
        public static class Program implements Node {
                public Node child;
                public Program (Node child) {
                        this.child = child;
                }
                public void accept(Visitor v) {
                        v.visit (this);
                }
        }
        private class Sequence implements Node {
                private LinkedList<Node> children;
                public Sequence() {
                        children = new LinkedList<Node>();
                }
                public void accept (Visitor v) {
                        for (Node child : children) {
                                child.accept(v);
                        }
                }
                public void add (Node instruction) {
                        children.add(instruction);
                }
        }
        private class Repeat implements Node {
                public Node child;
                public int repeat;
                public Repeat(Node child, int times) {
                        this.child = child;
                        this.repeat = times;
                }
                public void accept (Visitor v) {
                        v.visit(this);
                }
        }
        private class StringInit implements Node {
                public void accept (Visitor v) {
                        v.visit(this);
                }
        }
        private class StringExit implements Node {
                public void accept (Visitor v) {
                        v.visit(this);
                }
        }
        private class Declaration implements Node {
                public Node child;
                public Declaration(Node child) {
                        this.child = child;
                }
                public void accept (Visitor v) {
                        v.visit(this);
                }
        }
        private class Procedure implements Node {
        public void accept(Visitor v) {
            v.visit(this);
            }
        }
        public class Car implements Node {
                public char car;
                public Car (char c) { this.car = c; }
                public void accept (Visitor v) {
                        v.visit(this);
                }
        }
        private class BreakPnt implements Node {
                public void accept (Visitor v) {
                        v.visit(this);
                }
        }

        // define visitors themselves
        public static class Printer implements Visitor {
                public void visit (Left node) {
                        System.out.print('<'); 
                }
                public void visit (Right node) { 
                        System.out.print('>'); 
                }
                public void visit (Increment node) { 
                        System.out.print('+'); 
                }
                public void visit (Decrement node) { 
                        System.out.print('-'); 
                }
                public void visit (Input node) { 
                        System.out.print(','); 
                }
                public void visit (Output node) { 
                        System.out.print('.'); 
                }
                public void visit (Loop node) {
                        System.out.print('[');
                        node.child.accept(this);
                        System.out.print(']');
                }
                public void visit (Program node) {
                        node.child.accept(this);
                }
                public void visit (Repeat node) {
                        System.out.println(node.repeat);
                        node.child.accept(this);
                }
                public void visit (StringInit node) { 
                        System.out.print("\""); 
                }
                public void visit (StringExit node) { 
                        System.out.print("\""); 
                }
                public void visit (Declaration declaration) {
                        System.out.print('(');
                        declaration.child.accept(this);
                }
                public void visit (Procedure procedure) {
                        System.out.print(')');
                }
                public void visit (Car node) { 
                        System.out.print(node.car); 
                }
                public void visit (BreakPnt node) { 
                        System.out.println("Debug Break..");
                }
        }
        
        // Interpreter visitors
        public static class Interpreter implements Visitor {
                private byte[] cell;
                private int pointer;
                Interpreter.Procedure[] procedures;
                boolean inputFromString;                
                private LinkedList<Byte> stringBuffer;

                public void visit (Left node) { 
                    pointer--;
                }
                public void visit (Right node) { 
                    pointer++; 
                }
                public void visit (Increment node) { 
                    cell[pointer]++;
                }
                public void visit (Decrement node) { 
                    cell[pointer]--;
                }
                public void visit (Input node) {
                        if (stringBuffer.size()>0 || inputFromString) {
                                cell[pointer] = stringBuffer.pollLast();
                        } else {
                                try {
                                        cell[pointer] = (byte) System.in.read();
                                } catch (IOException e) {
                                       
                                }
                        }
                }
                public void visit (Output node) {
                        System.out.print((char)cell[pointer]);
                }
                public void visit (Loop node) {
                        while (cell[pointer] != 0) {
                                node.child.accept(this);
                        }
                }
                public void visit (Program node) {
                        cell = new byte[30000];
                        pointer = 0;
                        procedures = new Interpreter.Procedure[256];
                        stringBuffer = new LinkedList<Byte>();
                        inputFromString = false;

                        node.child.accept(this);
                }
                public void visit (Repeat node) {
                        for(int i=0; i<node.repeat; i++)
                        node.child.accept(this);
                }
                public void visit (StringInit node) {
                        this.inputFromString = true;
                }
                public void visit (StringExit node) {
                        this.inputFromString = false;
                }
                public void visit (Declaration node) {
                        final Interpreter that = this;
                        final Declaration o = node;
                        
                        procedures[cell[pointer]] = new Interpreter.Procedure() {
                                public void execute() {
                                        o.child.accept(that);
                                }
                        };
                }
                public interface Procedure {
                    void execute();             
                }
                public void visit(RUM.Procedure procedure) {
                        procedures[cell[pointer]].execute();
                }
                public void visit(Interpreter.Procedure procedure) {
                        procedures[cell[pointer]].execute();
                }
                public void visit (Car node) {
                        this.stringBuffer.push((byte)node.car);
                }
                public void visit(BreakPnt node) {
                }

        }
        
        private int i = 0;
        private Sequence parseSequence (String source) {
        int loop = 0;
        char command;
        Sequence sequence = new Sequence();
        Stack<Byte> stringBuffer = new Stack<Byte>();

        while (i < source.length ()) {
            command = source.charAt(i);

            if(loop==0) {
                i++;

                    if (Character.isDigit(command)) {
                        String temp = "";
                        for (; Character.isDigit(command); i++) {
                            temp+=command;
                            command = source.charAt(i);
                        }
                        loop = Integer.parseInt(temp);
                        i--;
                    }
                    if (command == '"') {
                        command = source.charAt(i);
                        i++; 
                        sequence.add(new StringInit ());
                        for(; command!='"'; i++) {
                                
                        sequence.add (new Car (command));
                        command = source.charAt(i);
                        }
                        sequence.add(new StringExit ());
                    }
                    if (command == '<') sequence.add (new Left ());
                    if (command == '>') sequence.add (new Right ());
                    if (command == '+') sequence.add (new Increment ());
                    if (command == '-') sequence.add (new Decrement ());
                    if (command == '.') sequence.add (new Output ());
                    if (command == ',') sequence.add (new Input ());
                    if (command == '[') sequence.add (new Loop (parseSequence (source)));
                    if (command == ']') return sequence;
                    if (command == ':') sequence.add (new Procedure());
                    if (command == '(') sequence.add (new Declaration (parseSequence (source)));
                    if (command == ')') return sequence;
            } else {
                loop--;
                for (; loop>0; loop--) {
                    if (command == '<') sequence.add (new Left ());
                    if (command == '>') sequence.add (new Right ());
                    if (command == '+') sequence.add (new Increment ());
                    if (command == '-') sequence.add (new Decrement ());
                    if (command == '.') sequence.add (new Output ());
                    if (command == ',') sequence.add (new Input ());
                    if (command == '[') sequence.add (new Loop (parseSequence (source)));
                    if (command == ']') return sequence;
                    if (command == ':') sequence.add (new Procedure());
                    if (command == '(') sequence.add (new Declaration (parseSequence (source)));
                    if (command == ')') return sequence;
                }
                i++;
            }
        }
                return sequence;
        }
        
        
        public static Program parse (String source) {
                return new Program (new RUM().parseSequence(source));
        }
       
    public static void HelloTest() {
        System.out.println("Hello World");
        Node helloTest = RUM.parse("++++++++++[>+++++++>++++++++++>+++>+<<<<-]>++.>+.+++++++..+++.>++.<<+++++++++++++++.>.+++.------.--------.>+.>.");
        helloTest.accept(new RUM.Interpreter());
        System.out.println("\nTree structure:");
        helloTest.accept(new RUM.Printer());
        System.out.println("\n");
    }
    
    public static void PrintMyLastName() {
        System.out.println("My last name in brainfk is:.");
        String k = "+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ .";
        String a = "+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ .";
        String d = "++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ .";
        String o = "+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ .";
        String u = "+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ .";
        String r = "++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ .";
        Node myName = RUM.parse(k+">"+a+">"+d+">"+d+">"+o+">"+u+">"+r);
        myName.accept(new RUM.Interpreter());
        System.out.println("\n");
    }
    public static void DisplayAllChars() {
        System.out.println("Basic");
        Node basicChars = RUM.parse("+[.+]");
        basicChars.accept(new RUM.Interpreter());
        System.out.println("\nTree structure:");
        basicChars.accept(new RUM.Printer());
        System.out.println("\n");
    }
    public static void ProcedureTest() {
        System.out.println("Procedure: Hello World");
        Node helloW = RUM.parse("(++++++++++<[>+>+<<-]>>[<<+>>-])>::::::::::::::<<<<<<<--------.>>>---------.+++++++..>---------.<<<<<<<------.<--------.>>>>>---.>>>.+++.<.--------.<<<<<<<+.");
        helloW.accept(new RUM.Interpreter());
        System.out.println("\nTree structure:");
        helloW.accept(new RUM.Printer());
        System.out.println("\n");
    }
    public static void RepeatTest() {
        System.out.println("Simple Repeat");
        Node repeatS = RUM.parse("110+ .");
        repeatS.accept(new RUM.Interpreter());
        System.out.println("\nTree structure:");
        repeatS.accept(new RUM.Printer());
        System.out.println("\n");
    }
    //Testers
    public static void StringTest() {
        System.out.println("Testing Strings");
        Node str = RUM.parse("\"Hello\",.");
        str.accept(new RUM.Interpreter());
        System.out.println("\nTree structure:");
        str.accept(new RUM.Printer());
        System.out.println("\n");
    }
    //Testers
    public static void HelloToUpper() {
        System.out.println("Hello World to uppercase");
        Node toUpperCase = RUM.parse("\"helloworld\",[--------------------------------.,]");
        toUpperCase.accept(new RUM.Interpreter());
       System.out.println("\nTree structure:");
        toUpperCase.accept(new RUM.Printer());
        System.out.println("\n");
    }
        
    
        public static void main(String[] args) {
                             
                //RUM.HelloTest();
            
                //RUM.PrintMyLastName();
               
                //DisplayAllChars();
            
                //RUM.ProcedureTest();
             
                //RUM.RepeatTest();
               
                //RUM.StringTest();
       
                //RUM.HelloToUpper();
            
        }
}
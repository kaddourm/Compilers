import java.util.HashMap;

public class Regex {
	public interface Node {
		public <T> T accept(Visitor<T> visitor);
	}
	public interface Visitor<T> {
		T visit(EmptySet node);
		T visit(EmptyString node);
		T visit(Symbol node);
		T visit(Star node);
		T visit(Sequence node);
		T visit(Or node);
                T visit(SequenceChild node);       
                T visit(OrChild node);  
	}
	// This Printer class implements Visitor class for string data type. Whenever any Node object (or its child object) is passed, this visitor shall process it
	public static class Printer implements Visitor<String> {

		@Override
		public String visit(EmptySet node) {
			return "Ø";
		}

		@Override
		public String visit(EmptyString node) {
			return "e";
		}

		@Override
		public String visit(Symbol node) {
			return ""+node.symbol;
		}

		@Override
		public String visit(Star node) {
			return "(" + node.child.accept(this) + ")*";
		}

		@Override
		public String visit(Sequence node) {
			// TODO Auto-generated method stub
			return node.sequenceChild.a.accept(this) + node.sequenceChild.b.accept(this);
		}

		@Override
		public String visit(Or node) {
			return node.orChild.a.accept(this) + '|' + node.orChild.b.accept(this);
		}
                
                @Override
		public String visit(SequenceChild node) {
			return node.a.accept(this) + '|' + node.b.accept(this);
		}
                
                @Override
		public String visit(OrChild node) {
			return node.a.accept(this) + '|' + node.b.accept(this);
		}

	}
	
	// EmptySet is Singleton class. This class is Static and its member emptySet is also static so only one copy shall be created per class.
	public static class EmptySet implements Node {
		private static EmptySet emptySet = new EmptySet();
		private EmptySet() {}
		public static EmptySet getInstance() {
			return emptySet;
		}
		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visit(this);
		}
	}
	// Matches "" Accept the end of a string
	// EmptyString is Singleton class. Since it doesn't contains any data member only making class static will result in Singleton
	public static class EmptyString implements Node {
                private static EmptyString emptyString = new EmptyString();
		public static EmptyString getInstance() {
			return emptyString;
		}
		private EmptyString() {}
		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visit(this);
		}
	}
	// Match a single symbol
	// Symbol class is implementing FlyWeight class. Here, there won't be two object created for one symbol.
        // Consider first symbol passed as 'x', first HashMap map shall be searched whether it contains previously created object for this symbol 'x'
        // if previous object present in map wrp 'x', that object shall be return without creating new object for 'x'
        // If map doesn't contains entry for symbol 'x' then new object shall be created and put in HashMap map for future use.
	public static class Symbol implements Node {
		char symbol;
		private static HashMap<Character, Symbol> map = new HashMap<Character, Symbol>();
		// It's private, as in, do not use outside this class
		private Symbol (char symbol) {
			this.symbol = symbol;
		}
		// How we actually "construct" a symbol
		public static Symbol getInstance(char symbol) {
			if (!map.containsKey(symbol)) {
				map.put(symbol, new Symbol(symbol));
			}
			return map.get(symbol);
		}
		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visit(this);
		}
	}
	// Match (child)*
	// Star class is implementing FlyWeight class. Here, there won't be two object created for one Star.
        // Consider first node passed as 'x', first HashMap map shall be searched whether it contains previously created object for this node 'x'
        // if previous object present in map wrp 'x', that object shall be return without creating new object for 'x'
        // If map doesn't contains entry for node 'x' then new object shall be created and put in HashMap map for future use.
	public static class Star implements Node {
		private static HashMap<Node, Star> map = new HashMap<Node, Star>();
		Node child;
		// Make the constructor private and have a hashmap here too
		private Star(Node child) {
			this.child = child;
		}
		// getInstance will return a Node but possibly not a Star
		// if the child is an emptyString, return emptyString
		public static Node getInstance(Node child) {
                    if (child == EmptySet.getInstance())
				return child;
                    if (!map.containsKey(child)) {
				map.put(child, new Star(child));
			}
			return map.get(child);
		}
		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visit(this);
		}
	}
        
        // This class contains state of Sequence. Since Sequence operate on two object, new class is created to hold value
        public static class SequenceChild implements Node
        {
           Node a, b;
           public SequenceChild(Node a, Node b) {
			this.a = a; this.b = b;
		}
           
           @Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visit(this);
		}
        }
        
	// Match a followed by b
	// This class implemented using Flyweight where only one object created for same sequence.
	public static class Sequence implements Node {
                private static HashMap<SequenceChild, Sequence> map = new HashMap<SequenceChild, Sequence>();
		//Node a, b;
                SequenceChild sequenceChild;
		/*public Sequence(Node a, Node b) {
			this.a = a; this.b = b;
		}*/
                public Sequence(SequenceChild c)
                {
                    this.sequenceChild = c;
                }
                public static Node getInstance(SequenceChild child) {
                    if (!map.containsKey(child)) {
				map.put(child, new Sequence(child));
			}
			return map.get(child);
		}
		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visit(this);
		}
	}
        
         // This class contains state of Or. Since Or operate on two object, new class is created to hold value
        public static class OrChild implements Node
        {
           Node a, b;
           public OrChild(Node a, Node b) {
			this.a = a; this.b = b;
		}
           @Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visit(this);
		}
        }
	// Match a or b
	// This Or class implement Flyweight and compaction
	public static class Or implements Node {
		// Node a, b;
                private static HashMap<OrChild, Or> map = new HashMap<OrChild, Or>();
		//Node a, b;
                OrChild orChild;
		/*public Or(Or a, Or b) {
			this.a = a; this.b = b;
		}*/
                  public Or(OrChild c)
                {
                    this.orChild = c;
                }
                public static Node getInstance(OrChild child) {
                    if (!map.containsKey(child)) {
				map.put(child, new Or(child));
			}
			return map.get(child);
		}
		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visit(this);
		}
	}
	
	public static class Derivative implements Visitor<Node> {
		Nullable nullable = new Nullable();
		public char c; // Derive with respect to c
		@Override
		public Node visit(EmptySet node) {
			// Dc(0) = 0
			return node;
		}
		@Override
		public Node visit(EmptyString node) {
			// Dc("") = 0
			return EmptySet.getInstance();
		}
		@Override
		public Node visit(Symbol node) {
			// Dc(c) = ""
			if (c == node.symbol)
				return EmptyString.getInstance(); // Do the same thing for the empty string
			// Dc(c') = 0 if c is not c'
			else
				return EmptySet.getInstance();
		}
		@Override
		public Node visit(Star node) {
			// Dc(a*) = Dc(a)a*
			//return new Sequence(node.child.accept(this), node);
                        return Sequence.getInstance(new SequenceChild(node.child.accept(this), node));
		}

		@Override
		public Node visit(Sequence node) {
			//Node result = new Sequence( node.a.accept(this), node.b);
                        Node result = Sequence.getInstance(new SequenceChild( node.sequenceChild.a.accept(this), node.sequenceChild.b));
			// Dc(AB) = Dc(A)B if A does not contain the empty string
			if (!node.sequenceChild.a.accept(nullable)) {
				return result;
			// Dc(AB) = Dc(A)B | Dc(B) if A contains the empty string
			} else {
				/* return new Or(
						result, // Dc(AB)
						node.sequenceChild.b.accept(this) // Dc(B)
						);
                                */
                                return Or.getInstance( new OrChild(
						result, // Dc(AB)
						node.sequenceChild.b.accept(this)) // Dc(B)
						);
			}
		}
		@Override
		public Node visit(Or node) {
			// Dc(A | B) = Dc(A) | Dc(B)
			return Or.getInstance(new OrChild( node.orChild.a.accept(this), node.orChild.b.accept(this)));
		}
                
                @Override
		public Node visit(SequenceChild node) {
			// Dc(A | B) = Dc(A) | Dc(B)
			// return new Or(node.a.accept(this), node.b.accept(this));
                        return  Or.getInstance(new OrChild( node.a.accept(this), node.b.accept(this)));
		}
                 @Override
		public Node visit(OrChild node) {
			// Dc(A | B) = Dc(A) | Dc(B)
			//return new Or(node.a.accept(this), node.b.accept(this));
                     return Or.getInstance(new OrChild( node.a.accept(this), node.b.accept(this)));
		}
	}
	// Does the regex match the empty string?
	public static class Nullable implements Visitor<Boolean> {
		@Override
		public Boolean visit(EmptySet node) {
			return false;
		}
		@Override
		public Boolean visit(EmptyString node) {
			return true;
		}
		@Override
		public Boolean visit(Symbol node) {
			return false;
		}
		@Override
		public Boolean visit(Star node) {
			return true;
		}
		@Override
		public Boolean visit(Sequence node) {
			return node.sequenceChild.a.accept(this) && node.sequenceChild.b.accept(this);
		}
		@Override
		public Boolean visit(Or node) {
			return node.orChild.a.accept(this) || node.orChild.b.accept(this);
		}
               @Override
		public Boolean visit(SequenceChild node) {
			return node.a.accept(this) || node.b.accept(this);
		}
                @Override
		public Boolean visit(OrChild node) {
			return node.a.accept(this) || node.b.accept(this);
		}
	}
	// Use derivatives to match regular expressions
	public static boolean match(Node regex, String string) {
		// Two visitors
		Derivative d = new Derivative();
		Nullable nullable = new Nullable();
		// For debugging, create the printer here
		Printer printer = new Printer();
                int index=1;
		// Just compute the derivative with respect to the first character, then the second, then the third and so on.
		for (char c : string.toCharArray()) {
			d.c = c; // Set the first character
			// For debugging purposes,
			// Print out the new regex
                        System.out.println("Derivative: " + index);
			System.out.println("Regex: " + regex.accept(printer));
			regex = regex.accept(d); // regex should match what it used to match, sans first character c
                        index++;
		}
                
		System.out.println("Final Regex: " + regex.accept(printer));
                
		// If the final language contains the empty string, then the original string was in the original language.
		// Does the regex match the empty string?
		return regex.accept(nullable);
	}
	// Match String s literally	
	public static Node fromString(String s) {
		if (s.length() == 0)
			return new EmptyString();
		//return new Sequence(new Symbol(s.charAt(0)),
		//		fromString(s.substring(1)));
                
                return Sequence.getInstance(new SequenceChild( Symbol.getInstance(s.charAt(0)),
				fromString(s.substring(1))));
	}
	// Create a nested sequence from an array of nodes
	public static Node seq(Node...s) {
		return null;
	}
	public static Node or(Node...s) {
		return null;
	}
	public static void main(String[] args) {
		String s = "H";
		s += "ello";
		if("Hello" == (s)) {
			System.out.println("WTF");
		}
                System.out.println("Computing Regular Expression...");
		// Does a|b match a?
		long then = System.nanoTime();
		for (int i = 0; i < 1; i++)
                {
                    
			// bob
			// ob
			// b
			// emptystring
			Regex.match(Regex.fromString("bob"), "bob");
                        
                }
                System.err.println("Total time taken (nanoseconds):" + (System.nanoTime() - then));
                        
	}
}
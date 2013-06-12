
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
	}
	
	// Reject everything
	// FIXME: Singleton
	public static class EmptySet implements Node {
		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visit(this);
		}
	}
	// Matches "" Accept the end of a string
	// FIXME: Singleton
	public static class EmptyString implements Node {
		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visit(this);
		}
	}
	// Match a single symbol
	// FIXME: Flyweight
	public static class Symbol implements Node {
		char symbol;
		public Symbol (char symbol) {
			this.symbol = symbol;
		}
		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visit(this);
		}
	}
	// Match (child)*
	public static class Star implements Node {
		Node child;
		public Star(Node child) {
			this.child = child;
		}
		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visit(this);
		}
	}
	// Match a followed by b
	public static class Sequence implements Node {
		Node a, b;
		public Sequence(Node a, Node b) {
			this.a = a; this.b = b;
		}
		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visit(this);
		}
	}
	// Match a or b
	public static class Or implements Node {
		Node a, b;
		public Or(Node a, Node b) {
			this.a = a; this.b = b;
		}
		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visit(this);
		}
	}
	// Rewrite the regex to match
	// the rest of a string without the first char c.
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
			return new EmptySet();
		}
		@Override
		public Node visit(Symbol node) {
			// Dc(c) = ""
			if (c == node.symbol)
				return new EmptyString();
			// Dc(c') = 0 if c is not c' 
			else
				return new EmptySet();
		}
		@Override
		public Node visit(Star node) {
			// Dc(a*) = Dc(a)a*
			return new Sequence(node.child.accept(this), node);
		}

		@Override
		public Node visit(Sequence node) {
			Node result = new Sequence(node.a.accept(this), node.b);
			// Dc(AB) = Dc(A)B if A does not contain the empty string
			if (!node.a.accept(nullable)) {
				return result;
			// Dc(AB) = Dc(A)B | Dc(B) if A contains the empty string
			} else {
				return new Or(
						result, // Dc(AB)
						node.b.accept(this) // Dc(B)
						);
			}
		}
		@Override
		public Node visit(Or node) {
			// Dc(A | B) = Dc(A) | Dc(B)
			return new Or(node.a.accept(this), node.b.accept(this));
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
			return node.a.accept(this) && node.b.accept(this);
		}
		@Override
		public Boolean visit(Or node) {
			return node.a.accept(this) || node.b.accept(this);
		}
	}
	// Use derivatives to match regular expressions
	public static boolean match(Node regex, String string) {
		// Two visitors
		Derivative d = new Derivative();
		Nullable nullable = new Nullable();
		
		// Just compute the derivative with respect to the first character, then the second, then the third and so on. 
		for (char c : string.toCharArray()) {
			d.c = c; // Set the first character
			regex = regex.accept(d); // regex should match what it used to match, sans first character c
		}
		// If the final language contains the empty string, then the original string was in the original language.
		// Does the regex match the empty string?
		return regex.accept(nullable);
	}
	public static void main(String[] args) {
		// Does a|b match a?
		long then = System.nanoTime();
		for (int i = 0; i < 1000000; i++)
			Regex.match(
				new Sequence(new Symbol('b'), new Sequence(new Symbol('o'), new Symbol('b'))), "a");
		System.out.println(System.nanoTime() - then);
	}
}

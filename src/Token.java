public class Token {
    private Lexem lexem;
    private String value;
    public Token(Lexem l, String v)
    {
        lexem = l;
        value = v;
    }

    @Override
    public String toString() {
        return lexem + " : " + value + "\n";
    }
}
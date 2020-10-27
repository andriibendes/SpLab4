import java.io.Console;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Lexer {
    private List<Token> lexems = new ArrayList<Token>();
    private State state = State.COMMON;

    private final List<String> reserved = Arrays.asList("if","else","switch","case","default","break","int","float","char",
            "double","long","for","while","do","void","goto","auto","signed","const","extern","register",
            "unsigned","return","continue","enum","sizeof","struct","typedef");
    private final List<String> directive = Arrays.asList("#include","#define","#undef","#ifdef","#ifndef","#if","#else",
            "#elif","#endif","#error","#pragma");
    private final List<String> bool = Arrays.asList("true","false");
    private final List<String> punctation = Arrays.asList("(",")","[","]","{","}",",",";",":",".");
    private final List<String> operator = Arrays.asList(">=","!=","++","--","==","+=","-=","*=","/=",
            "<=","+","-","=","*","%","/",">","<","!","^","&","?");
    private final String quote  = "\"";
    private final String lineComment = "//";
    private final String startComment = "/*";
    private final String endComment = "*/";

    private boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    private boolean isHex(String strHex) {
        if (strHex.length() > 2)
        {
            if (strHex.charAt(0) == '0' && (strHex.charAt(1) == 'x' || strHex.charAt(1) == 'X'))
            {
                for (int i = 2; i < strHex.length(); i++)
                {
                    if (!Character.isDigit(strHex.charAt(i)) && !(strHex.charAt(i) > 64 && strHex.charAt(i) < 71)
                    && !(strHex.charAt(i) > 96 && strHex.charAt(i) < 103))
                        return false;
                }
                return true;
            }
            return false;
        }
        else return false;
    }

    private boolean isIdentifier(String strIdent)
    {
        char first = strIdent.charAt(0);
        if(first == '_' || Character.isAlphabetic(first))
        {
            for (int i = 1; i < strIdent.length(); i++)
            {
                if (!Character.isAlphabetic(strIdent.charAt(i)) && !Character.isDigit(strIdent.charAt(i)))
                    return false;

            }
            return true;
        }
        else return false;
    }

    private boolean isHeader(String strHead)
    {
        if (strHead.endsWith(".h"))
        {
            for (int i = 0; i < (strHead.length() - 2); i++)
            {
                if (!Character.isAlphabetic(strHead.charAt(i)))
                    return false;
            }
            return true;
        }
        else return false;
    }

    private boolean isCharacter(String strChar)
    {
        return strChar.charAt(0) == '\'' && strChar.charAt(2) == '\'';
    }

    private boolean startNum(StringBuilder num)
    {
        for (int i = 0; i < num.length(); i++)
        {
            if (!Character.isDigit(num.charAt(i)))
                return false;
        }
        return true;
    }


    public void outResults()
    {
        for (Token t : lexems)
        {
            System.out.println(t.toString());
        }
    }

    private void addWord(StringBuilder w)
    {
        String word = w.toString();
        Token token;
        if (reserved.contains(word))
            token = new Token(Lexem.RESERVED, word);
        else if (directive.contains(word))
            token = new Token(Lexem.DIRECTIVE, word);
        else if (bool.contains(word))
            token = new Token(Lexem.BOOLEAN, word);
        else if (isNumeric(word) || isHex(word))
            token = new Token(Lexem.NUMBER, word);
        else if (word.length() == 3 && isCharacter(word))
            token = new Token(Lexem.CHARACTER, word);
        else if (word.length() > 2 && isHeader(word))
            token = new Token(Lexem.HEADER, word);
        else if (isIdentifier(word))
            token = new Token(Lexem.IDENTIFIER, word);
        else
            token = new Token(Lexem.ERROR, word);
        lexems.add(token);
    }

    public void eatLine(String l)
    {
        String line = l.trim();
        StringBuilder word = new StringBuilder();
        for (int i = 0; i < line.length(); i++)
        {
            switch (state)
            {
                case COMMENT: {
                    if (i != (line.length() - 1)) {
                        String doubleOp = Character.toString(line.charAt(i)) + Character.toString(line.charAt(i + 1));
                        if (doubleOp.equals(endComment)) {
                            state = State.COMMON;
                            i++;
                        }
                    }
                    break;
                }
                case QUOTES: {
                    if (Character.toString(line.charAt(i)).equals(quote))
                    {
                        state = State.COMMON;
                        word.append(line.charAt(i));

                        Token token = new Token(Lexem.SYMBOL, word.toString());
                        lexems.add(token);

                        word = new StringBuilder();
                    }
                    else if (i == (line.length() - 1))
                    {
                        word.append(line.charAt(i));
                        addWord(word);
                    }
                    else {
                        word.append(line.charAt(i));
                    }
                    break;
                }
                case COMMON: {
                    if (quote.equals(Character.toString(line.charAt(i))))
                    {
                        state = State.QUOTES;
                        word.append(line.charAt(i));
                    }
                    else if (punctation.contains(Character.toString(line.charAt(i))))
                    {
                        if (word.length() != 0) {
                            if (startNum(word) && line.charAt(i) == '.') {
                                word.append(line.charAt(i));
                            } else {
                                addWord(word);
                                word = new StringBuilder();
                                Token token = new Token(Lexem.PUNCTATION, Character.toString(line.charAt(i)));
                                lexems.add(token);
                            }
                        } else {
                            Token token = new Token(Lexem.PUNCTATION, Character.toString(line.charAt(i)));
                            lexems.add(token);
                        }
                    }
                    else if (operator.contains(Character.toString(line.charAt(i))))
                    {
                        if (word.length() != 0)
                        {
                            addWord(word);
                            word = new StringBuilder();
                        }
                        if (i != (line.length() - 1))
                        {
                            String doubleOp = Character.toString(line.charAt(i)) + Character.toString(line.charAt(i + 1));

                            if (startComment.equals(doubleOp)) {
                                state = State.COMMENT;
                            } else if (lineComment.equals(doubleOp)) {
                                if (word.length() != 0) {
                                    addWord(word);
                                }
                                return;
                            } else if (operator.contains(doubleOp)) {
                                Token token = new Token(Lexem.OPERATOR, doubleOp);
                                lexems.add(token);
                                i++;
                            } else {
                                Token token = new Token(Lexem.OPERATOR, Character.toString(line.charAt(i)));
                                lexems.add(token);
                            }
                        }
                        else {
                            Token token = new Token(Lexem.OPERATOR, Character.toString(line.charAt(i)));
                            lexems.add(token);
                        }
                    }
                    else if (Character.isWhitespace(line.charAt(i)))
                    {
                        if (word.length() != 0)
                        {
                            addWord(word);
                            word = new StringBuilder();
                        }
                    }
                    else {
                        word.append(line.charAt(i));
                    }
                    break;
                }
            }
        }
    }
}

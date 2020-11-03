import java.io.Console;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Lexer {
    private List<Token> lexems = new ArrayList<Token>();
    private boolean inComment = false;

    private final List<String> reserved = Arrays.asList("if","else","switch","case","default","break","int","float","char",
            "double","long","for","while","do","void","goto","auto","signed","const","extern","register",
            "unsigned","return","continue","enum","sizeof","struct","typedef", "true","false");
    private final List<String> directive = Arrays.asList("#include","#define","#undef","#ifdef","#ifndef","#if","#else",
            "#elif","#endif","#error","#pragma");

    private void processComment(String line)
    {
        for (int i = 0; i < line.length() - 1; i++)
        {
            if (line.charAt(i) == '*' && line.charAt(i + 1) == '/')
            {
                inComment = false;
                eatLine(line.substring(i + 2));
                break;
            }
        }
    }

    private void processQuotes(String line)
    {
        StringBuilder word = new StringBuilder();
        word.append('\"');
        for (int i = 0; i < line.length(); i++)
        {
            if (line.charAt(i) == '\"')
            {
                word.append('\"');
                Token token = new Token(Lexem.SYMBOL, word.toString());
                lexems.add(token);
                eatLine(line.substring(i + 1));
                return;
            }
            else
            {
                word.append(line.charAt(i));
            }
        }
        Token token = new Token(Lexem.ERROR, word.toString());
        lexems.add(token);
    }

    private void directive(String line)
    {
        StringBuilder word = new StringBuilder();
        word.append('#');
        Token token;
        for (int i = 0; i < line.length(); i++)
        {
            if (Character.isAlphabetic(line.charAt(i)))
            {
                word.append(line.charAt(i));
            }
            else
            {
                String w = word.toString();
                if (directive.contains(w))
                    token = new Token(Lexem.DIRECTIVE, w);
                else
                    token = new Token(Lexem.ERROR, w);
                lexems.add(token);
                eatLine(line.substring(i + 1));
                return;
            }
        }
        String w = word.toString();
        if (directive.contains(w))
            token = new Token(Lexem.DIRECTIVE, w);
        else
            token = new Token(Lexem.ERROR, w);
        lexems.add(token);
    }

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
                if (!Character.isAlphabetic(strIdent.charAt(i)) && !Character.isDigit(strIdent.charAt(i)) && strIdent.charAt(i) != '_')
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

    public void outResults()
    {
        for (Token t : lexems)
        {
            System.out.println(t.toString());
        }
    }

    public void process(String l)
    {
        if (inComment)
            processComment(l);
        else
            eatLine(l);
    }

    private void eatLine(String l)
    {
        String line = l.trim();
        int length = line.length();
        for (int i = 0; i < length; i++)
        {
            char curr = line.charAt(i);
            switch (curr)
            {
                case '>':
                {
                    Token token;
                    if (i != length - 1 && line.charAt(i + 1) == '=')
                    {
                        token = new Token(Lexem.OPERATOR, ">=");
                        i++;
                    }
                    else {
                        token = new Token(Lexem.OPERATOR, ">");
                    }
                    lexems.add(token);
                    break;
                }
                case '&':
                {
                    Token token;
                    if (i != length - 1 && line.charAt(i + 1) == '&')
                    {
                        token = new Token(Lexem.OPERATOR, "&&");
                        i++;
                    }
                    else {
                        token = new Token(Lexem.OPERATOR, "&");
                    }
                    lexems.add(token);
                    break;
                }
                case '|':
                {
                    Token token;
                    if (i != length - 1 && line.charAt(i + 1) == '|')
                    {
                        token = new Token(Lexem.OPERATOR, "||");
                        i++;
                    }
                    else {
                        token = new Token(Lexem.OPERATOR, "|");
                    }
                    lexems.add(token);
                    break;
                }
                case '<':
                {
                    Token token;
                    if (i != length - 1 && line.charAt(i + 1) == '=')
                    {
                        token = new Token(Lexem.OPERATOR, "<=");
                        i++;
                    }
                    else {
                        token = new Token(Lexem.OPERATOR, "<");
                    }
                    lexems.add(token);
                    break;
                }
                case '=':
                {
                    Token token;
                    if (i != length - 1 && line.charAt(i + 1) == '=')
                    {
                        token = new Token(Lexem.OPERATOR, "==");
                        i++;
                    }
                    else {
                        token = new Token(Lexem.OPERATOR, "=");
                    }
                    lexems.add(token);
                    break;
                }
                case '!':
                {
                    Token token;
                    if (i != length - 1 && line.charAt(i + 1) == '=')
                    {
                        token = new Token(Lexem.OPERATOR, "!=");
                        i++;
                    }
                    else {
                        token = new Token(Lexem.OPERATOR, "!");
                    }
                    lexems.add(token);
                    break;
                }
                case '*':
                {
                    Token token;
                    if (i != length - 1 && line.charAt(i + 1) == '=')
                    {
                        token = new Token(Lexem.OPERATOR, "*=");
                        i++;
                    }
                    else {
                        token = new Token(Lexem.OPERATOR, "*");
                    }
                    lexems.add(token);
                    break;
                }
                case '%':
                {
                    Token token;
                    if (i != length - 1 && line.charAt(i + 1) == '=')
                    {
                        token = new Token(Lexem.OPERATOR, "%=");
                        i++;
                    }
                    else {
                        token = new Token(Lexem.OPERATOR, "%");
                    }
                    lexems.add(token);
                    break;
                }
                case '/':
                {
                    Token token;
                    if (i != length - 1 && line.charAt(i + 1) == '=')
                    {
                        token = new Token(Lexem.OPERATOR, "/=");
                        lexems.add(token);
                        i++;
                        break;
                    }
                    else if (i != length - 1 && line.charAt(i + 1) == '/')
                    {
                        return;
                    }
                    else if (i != length - 1 && line.charAt(i + 1) == '*')
                    {
                        inComment = true;
                        processComment(line.substring(i));
                        return;
                    }
                    else {
                        token = new Token(Lexem.OPERATOR, "/");
                        lexems.add(token);
                        break;
                    }
                }
                case '+':
                {
                    Token token;
                    if (i != length - 1 && line.charAt(i + 1) == '=')
                    {
                        token = new Token(Lexem.OPERATOR, "+=");
                        i++;
                    }
                    else if (i != length - 1 && line.charAt(i + 1) == '+')
                    {
                        token = new Token(Lexem.OPERATOR, "++");
                        i++;
                    }
                    else {
                        token = new Token(Lexem.OPERATOR, "+");
                    }
                    lexems.add(token);
                    break;
                }
                case '-':
                {
                    Token token;
                    if (i != length - 1 && line.charAt(i + 1) == '=')
                    {
                        token = new Token(Lexem.OPERATOR, "-=");
                        i++;
                    }
                    else if (i != length - 1 && line.charAt(i + 1) == '-')
                    {
                        token = new Token(Lexem.OPERATOR, "--");
                        i++;
                    }
                    else {
                        token = new Token(Lexem.OPERATOR, "-");
                    }
                    lexems.add(token);
                    break;
                }
                case '(':
                {
                    Token token = new Token(Lexem.PUNCTATION, "(");
                    lexems.add(token);
                    break;
                }
                case ')':
                {
                    Token token = new Token(Lexem.PUNCTATION, ")");
                    lexems.add(token);
                    break;
                }
                case '{':
                {
                    Token token = new Token(Lexem.PUNCTATION, "{");
                    lexems.add(token);
                    break;
                }
                case '}':
                {
                    Token token = new Token(Lexem.PUNCTATION, "}");
                    lexems.add(token);
                }
                case '[':
                {
                    Token token = new Token(Lexem.PUNCTATION, "[");
                    lexems.add(token);
                    break;
                }
                case ']':
                {
                    Token token = new Token(Lexem.PUNCTATION, "]");
                    lexems.add(token);
                    break;
                }
                case ',':
                {
                    Token token = new Token(Lexem.PUNCTATION, ",");
                    lexems.add(token);
                    break;
                }
                case '.':
                {
                    Token token = new Token(Lexem.PUNCTATION, ".");
                    lexems.add(token);
                    break;
                }
                case ';':
                {
                    Token token = new Token(Lexem.PUNCTATION, ";");
                    lexems.add(token);
                    break;
                }
                case '\"':
                {
                    processQuotes(line.substring(i + 1));
                    return;
                }
                case '#':
                {
                    directive(line.substring(i + 1));
                    break;
                }
                default: {
                    if (Character.isDigit(curr)) {
                        processNumber(line.substring(i));
                        return;
                    }
                    else if (Character.isLetter(curr) || curr == '_' || curr == '\'') {
                        processWord(line.substring(i));
                        return;
                    }
                    else if (Character.isWhitespace(curr))
                    {
                        break;
                    }
                    else
                    {
                        Token token = new Token(Lexem.ERROR, Character.toString(curr));
                        lexems.add(token);
                        break;
                    }
                }
            }
        }
    }

    private void processWord(String line)
    {
        StringBuilder word = new StringBuilder();
        Token token;
        for (int i = 0; i < line.length(); i++)
        {
            if (Character.isAlphabetic(line.charAt(i)) || Character.isDigit(line.charAt(i)) || line.charAt(i) == '_' || line.charAt(i) == '\'')
            {
                word.append(line.charAt(i));
            }
            else
            {
                String w = word.toString();
                if (reserved.contains(w))
                    token = new Token(Lexem.RESERVED, w);
                else if (w.length() == 3 && isCharacter(w))
                    token = new Token(Lexem.CHARACTER, w);
                else if (isIdentifier(w))
                    token = new Token(Lexem.IDENTIFIER, w);
                else
                    token = new Token(Lexem.ERROR, w);
                lexems.add(token);
                eatLine(line.substring(i));
                return;
            }
        }
        String w = word.toString();
        if (reserved.contains(w))
            token = new Token(Lexem.RESERVED, w);
        else if (w.length() == 3 && isCharacter(w))
            token = new Token(Lexem.CHARACTER, w);
        else if (isIdentifier(w))
            token = new Token(Lexem.IDENTIFIER, w);
        else
            token = new Token(Lexem.ERROR, w);
        lexems.add(token);
    }

    private void processNumber(String line)
    {
        StringBuilder word = new StringBuilder();
        Token token;
        for (int i = 0; i < line.length(); i++)
        {
            if (Character.isDigit(line.charAt(i)) || Character.isAlphabetic(line.charAt(i)) || line.charAt(i) == '.')
            {
                word.append(line.charAt(i));
            }
            else
            {
                String w = word.toString();
                if (isHex(w) || isNumeric(w))
                    token = new Token(Lexem.NUMBER, w);
                else
                    token = new Token(Lexem.ERROR, w);
                lexems.add(token);
                eatLine(line.substring(i));
                return;
            }
        }
        String w = word.toString();
        if (isHex(w) || isNumeric(w))
            token = new Token(Lexem.NUMBER, w);
        else
            token = new Token(Lexem.ERROR, w);
        lexems.add(token);
    }
}

package ru.den_abr.commonlib.utility;

public enum DefaultFontInfo
{
    NUM_1('1', 5), 
    NUM_2('2', 5), 
    NUM_3('3', 5), 
    NUM_4('4', 5), 
    NUM_5('5', 5), 
    NUM_6('6', 5), 
    NUM_7('7', 5), 
    NUM_8('8', 5), 
    NUM_9('9', 5), 
    NUM_0('0', 5), 
    EXCLAMATION_POINT('!', 1), 
    AT_SYMBOL('@', 6), 
    NUM_SIGN('#', 5), 
    DOLLAR_SIGN('$', 5), 
    PERCENT('%', 5), 
    UP_ARROW('^', 5), 
    AMPERSAND('&', 5), 
    ASTERISK('*', 5), 
    LEFT_PARENTHESIS('(', 4), 
    RIGHT_PERENTHESIS(')', 4), 
    MINUS('-', 5), 
    UNDERSCORE('_', 5), 
    PLUS_SIGN('+', 5), 
    EQUALS_SIGN('=', 5), 
    LEFT_CURL_BRACE('{', 4), 
    RIGHT_CURL_BRACE('}', 4), 
    LEFT_BRACKET('[', 3), 
    RIGHT_BRACKET(']', 3), 
    COLON(':', 1), 
    SEMI_COLON(';', 1), 
    DOUBLE_QUOTE('\"', 3), 
    SINGLE_QUOTE('\'', 1), 
    LEFT_ARROW('<', 4), 
    RIGHT_ARROW('>', 4), 
    QUESTION_MARK('?', 5), 
    SLASH('/', 5), 
    BACK_SLASH('\\', 5), 
    LINE('|', 1), 
    TILDE('~', 5), 
    TICK('`', 2), 
    PERIOD('.', 1), 
    COMMA(',', 1), 
    SPACE(' ', 3), 
    DEFAULT('a', 4);
    
    private char character;
    private int length;
    
    private DefaultFontInfo(final char character, final int length) {
        this.character = character;
        this.length = length;
    }
    
    public char getCharacter() {
        return this.character;
    }
    
    public int getLength() {
        return this.length;
    }
    
    public int getBoldLength() {
        if (this == DefaultFontInfo.SPACE) {
            return this.getLength();
        }
        return this.length + 1;
    }
    
    public static DefaultFontInfo getDefaultFontInfo(final char c) {
        for (final DefaultFontInfo dFI : values()) {
            if (dFI.getCharacter() == c) {
                return dFI;
            }
        }
        return DefaultFontInfo.DEFAULT;
    }
}

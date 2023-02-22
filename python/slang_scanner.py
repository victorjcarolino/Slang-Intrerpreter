# [CSE 262] This file is a minimal skeleton for a Scheme scanner in Python.  It
# provides a transliteration of the TokenStream class, and the shell of a
# Scanner class.  Please see the README.md file for more discussion.
from enum import Enum

class Token:
    def __init__(self, type, text, row, col, value):
        self.type = type
        self.text = text
        self.row = row
        self.col = col
        self.value = value

class TokenStream:
    def __init__(self, tokens):
        self.__tokens = tokens
        self.__next = 0

    def reset(self): self.__next = 0

    def nextToken(self):
        return None if not self.hasNext() else self.__tokens[self.__next]

    def nextNextToken(self):
        return None if not self.hasNextNext() else self.__tokens[self.__next + 1]

    def popToken(self): self.__next += 1

    def hasNext(self): return self.__next < len(self.__tokens)

    def hasNextNext(self): return (self.__next + 1) < len(self.__tokens)

class State(Enum):
    #1-19 reserved for nonaccepting states
    START = 1
    CLEANBREAK = 2
    INCOMMENT = 3
    INSTRING = 4
    INSTRING2 = 5
    INID = 6
    PM = 7
    ININT = 8
    PREDBL = 9
    INDBL = 10
    VCB = 11
    PRECHAR = 12
    #20-39 reserved for non-terminating accepting states
    #20-29 for states that transition to START
    ABBREV = 20
    LPAREN = 21
    RPAREN = 22
    VEC = 23
    #30-39 for states that transition to CLEANBREAK
    STRING = 31
    IDENTIFIER = 32
    INT = 33
    DBL = 34
    CHAR = 35
    BOOL = 36
    #EOF and ERROR
    EOF = 0
    ERROR = -1
    NUM_SIZE_ERROR = -2

class Scanner:
    def __init__(self):
        self.source = ""
        self.source_index = -1
        self.current_char = ''
        self.state = State.START
        self.prev_state = State.START
        self.token_text = ""
        self.current_coords = [1,0]
        self.token_coords = [1,0]
    
    #Called by getNextChar() to increment the coordinates
    #Increments the column, unless in the case of a new line, then increments row and resets column
    def incrementCoords(self):
        if self.current_char == '\n':
            self.current_coords[0] += 1
            self.current_coords[1] = 0
        elif self.current_char != '\r':
            self.current_coords[1] += 1

    #Increments the index are sets current_char to the next char if it exists
    #If the end of source is reached, current_char is set to '\0'
    def getNextChar(self):
        self.source_index += 1
        if self.source_index < len(self.source):
            self.current_char = self.source[self.source_index]
        else:
            self.current_char = '\0'
        self.incrementCoords()

    #Adds current_char to token, and resets the token_coords if this is the first char being added
    def addToToken(self):
        if len(self.token_text) == 0:
            self.token_coords[0] = self.current_coords[0]
            self.token_coords[1] = self.current_coords[1]
        self.token_text += self.current_char
   
    def useThenConsume(self):
        self.addToToken()
        self.getNextChar()

    def startTransition(self):
        need_to_consume = True
        iden_triggers = "!$%&*/:<=>?~_^abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
        if self.current_char == "'":
            self.state = State.ABBREV
        elif self.current_char == '"':
            self.state = State.INSTRING
        elif self.current_char == '#':
            self.state = State.VCB
        elif self.current_char in iden_triggers:
            self.state = State.INID
        elif self.current_char in "0123456789":
            self.state = State.ININT
        elif self.current_char in "+-":
            self.state = State.PM
        else:
            self.prev_state = State.START
            self.state = State.CLEANBREAK
            need_to_consume = False
        if need_to_consume:
            self.useThenConsume()

    def cleanBreakTransition(self):
        if self.current_char == '(':
            self.useThenConsume()
            self.state = State.LPAREN
        elif self.current_char == ')':
            self.useThenConsume()
            self.state = State.RPAREN
        elif self.current_char in " \t\n\r":
            self.getNextChar()
            self.state = State.START
        elif self.current_char == ';':
            self.getNextChar()
            self.state = State.INCOMMENT
        elif self.current_char == '\0':
            self.state = State.EOF
        else:
            self.state = State.ERROR
        
    def inCommentTransition(self):
        if self.current_char == '\0':
            self.state = State.EOF
        elif self.current_char == '\n' or self.current_char == '\r':
            self.getNextChar()
            self.state = State.START
        else:
            self.getNextChar()
            self.state = State.INCOMMENT
    
    def inStringTransition(self):
        if self.current_char == '\"':
            self.useThenConsume()
            self.state = State.STRING
        elif self.current_char == '\\':
            self.getNextChar()
            self.state = State.INSTRING2
        elif self.current_char != '\0':
            self.useThenConsume()
            self.state = State.INSTRING
        else:
            self.prev_state = State.INSTRING
            self.state = State.ERROR
    
    def inString2Transition(self):
        if self.current_char in "\\\"":
            self.useThenConsume()
            self.state = State.INSTRING
        elif self.current_char == 'n':
            self.token_text += '\n'
            self.getNextChar()
            self.state = State.INSTRING
        elif self.current_char == 't':
            self.token_text += '\t'
            self.getNextChar()
            self.state = State.INSTRING
        elif self.current_char == 'r':
            self.token_text += '\r'
            self.getNextChar()
            self.state = State.INSTRING
        else:
            self.prev_state = State.INSTRING2
            self.state = State.ERROR
            
    def inIdTransition(self):
        symbols = "!$%&*/:<=>?~_^.+-"
        alpha1 = "abcdefghijklmnopqrstuvwxyz"
        alpha2 = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        digits = "0123456789"
        if self.current_char in symbols+alpha1+alpha2+digits:
            self.useThenConsume()
        else:
            self.state = State.IDENTIFIER

    def pmTransition(self):
        if self.current_char in "0123456789":
            self.useThenConsume()
            self.state = State.ININT
        else:
            self.state = State.IDENTIFIER

    def inIntTransition(self):
        if self.current_char in "0123456789":
            self.useThenConsume()
        elif self.current_char == '.':
            self.useThenConsume()
            self.state = State.PREDBL
        else:
            self.state = State.INT

    def preDblTransition(self):
        if self.current_char in "0123456789":
            self.useThenConsume()
            self.state = State.INDBL
        else:
            self.prev_state = State.PREDBL
            self.state = State.ERROR

    def inDblTransition(self):
        if self.current_char in "0123456789":
            self.useThenConsume()
        else:
            self.state = State.DBL

    def vcbTransition(self):
        if self.current_char == '(':
            self.useThenConsume()
            self.state = State.VEC
        elif self.current_char == '\\':
            self.useThenConsume()
            self.state = State.PRECHAR
        elif self.current_char in "tf":
            self.useThenConsume()
            self.state = State.BOOL
        else:
            self.prev_state = State.VCB
            self.state = State.ERROR

    def preCharTransition(self):
        if self.source[self.source_index:self.source_index+len("newline")] == "newline":
            self.token_text += '\n'
            for i in range(len("newline")):
                self.getNextChar()
            self.state = State.CHAR
        elif self.source[self.source_index:self.source_index+len("space")] == "space":
            self.token_text += ' '
            for i in range(len("space")):
                self.getNextChar()
            self.state = State.CHAR
        elif self.source[self.source_index:self.source_index+len("tab")] == "tab":
            self.token_text += '\t'
            for i in range(len("tab")):
                self.getNextChar()
            self.state = State.CHAR

        elif self.current_char not in " \t\n\r\0":
            self.useThenConsume()
            self.state = State.CHAR
        else:
            self.prev_state = State.PRECHAR
            self.state = State.ERROR

    def checkKeywords(self):
        token = None
        keyword_list = ["and", "begin", "cond", "define", "if", "lambda", "or", "quote", "set!"]
        for kw in keyword_list:     
            if self.token_text == kw:   #Check if token_text fits any keyword
                type = kw.capitalize()
                if kw == "set!": #Set type special case because of the !
                    type = "Set"    
                token = Token(type, self.token_text, self.token_coords[0], self.token_coords[1], None)
        return token
            
    def scanTokens(self, source):
        self.source = source
        token_list = []
        self.getNextChar()
        while(self.state is not State.EOF):
            # Non-accepting states - call respective transition method
            if self.state is State.START:
                self.startTransition()
            elif self.state is State.CLEANBREAK:
                self.cleanBreakTransition()
            elif self.state is State.INCOMMENT:
                self.inCommentTransition()
            elif self.state is State.INSTRING:
                self.inStringTransition()
            elif self.state is State.INSTRING2:
                self.inString2Transition()
            elif self.state is State.INID:
                self.inIdTransition()
            elif self.state is State.PM:
                self.pmTransition()
            elif self.state is State.ININT:
                self.inIntTransition()
            elif self.state is State.PREDBL:
                self.preDblTransition()
            elif self.state is State.INDBL:
                self.inDblTransition()
            elif self.state is State.VCB:
                self.vcbTransition()
            elif self.state is State.PRECHAR:
                self.preCharTransition()
            
            # Accepting states - create token and epsilon to START or CLEANBREAK
            #20-39 is an arbritrary reserved range designated in the State Class
            elif 20 <= self.state.value <= 39:
                if self.state is State.ABBREV:
                    token_list.append(Token("Abbrev", self.token_text, self.token_coords[0], self.token_coords[1], None))
                elif self.state is State.LPAREN:
                    token_list.append(Token("LParen", self.token_text, self.token_coords[0], self.token_coords[1], None))
                elif self.state is State.RPAREN:
                    token_list.append(Token("RParen", self.token_text, self.token_coords[0], self.token_coords[1], None))
                elif self.state is State.STRING:
                    str_val = self.token_text[1:len(self.token_text)-1]
                    token_list.append(Token("Str", self.token_text, self.token_coords[0], self.token_coords[1], str_val))
                elif self.state is State.IDENTIFIER:
                    #Check here for keywords
                    keywordToken = self.checkKeywords()
                    if not keywordToken is None:
                        token_list.append(keywordToken)
                    else:
                        token_list.append(Token("Identifier", self.token_text, self.token_coords[0], self.token_coords[1], self.token_text))
                elif self.state is State.INT:
                    #catch error for number too large for int
                    try:
                        int_val = int(self.token_text)
                    except:
                        self.state = State.ERROR
                        self.prev_state = State.NUM_SIZE_ERROR
                    else:
                        token_list.append(Token("Int", self.token_text, self.token_coords[0], self.token_coords[1], int_val))
                elif self.state is State.DBL:
                    #catch error for number too large for double
                    try:
                        dbl_val = float(self.token_text)
                    except:
                        self.state = State.ERROR
                        self.prev_state = State.NUM_SIZE_ERROR
                    else:
                        token_list.append(Token("Dbl", self.token_text, self.token_coords[0], self.token_coords[1], dbl_val))
                elif self.state is State.VEC:
                    token_list.append(Token("Vector", self.token_text, self.token_coords[0], self.token_coords[1], None))
                elif self.state is State.CHAR:
                    char_val = self.token_text[2]
                    token_list.append(Token("Char", self.token_text, self.token_coords[0], self.token_coords[1], char_val))
                elif self.state is State.BOOL:
                    bool_val = self.token_text[1] == 't'
                    token_list.append(Token("Bool", self.token_text, self.token_coords[0], self.token_coords[1], bool_val))
                #Reset token text
                self.token_text = ""
                #20-29 is START range - defined in State class
                if 20 <= self.state.value <= 29:
                    self.state = State.START
                #30-39 is CLEANBREAK range - defined in State class
                elif 30 <= self.state.value <= 39:
                    self.state = State.CLEANBREAK
                    self.prev_state = State.STRING #any State with value in this range works the same for prev_state
            elif self.state is State.ERROR:
                error_message = ""
                error_line = self.current_coords[0]
                error_col = self.current_coords[1]
                if self.current_char == '\0':
                    error_message = "Unexpected EOF"
                    self.state = State.EOF
                elif 30 <= self.prev_state.value <= 39: #from strings, ids, ints, doubles, chars, bools
                    error_message = "Expected a cleanbreak but found character: "+self.current_char
                    self.state = State.START
                elif self.prev_state == State.START:
                    error_message = "Invalid character: "+self.current_char
                    self.getNextChar()
                    self.state = State.START
                elif self.prev_state == State.INSTRING2:
                    error_message = "Invalid escape character: "+self.current_char
                    self.getNextChar()
                    self.state = State.INSTRING
                elif self.prev_state == State.VCB:
                    error_message = "# followed by unexpected character: "+self.current_char+" Skipping #"
                    self.state = State.START
                elif self.prev_state == State.PRECHAR:
                    error_message = "Incomplete char sequence. Skipping #\\"
                    self.state = State.START
                elif self.prev_state == State.PREDBL:
                    error_message = "Incomplete double. Skipping"
                    self.state = State.START
                elif self.prev_state == State.NUM_SIZE_ERROR:
                    error_message = "Number too large to be stored. Skipping"
                    self.state = State.CLEANBREAK
                    self.prev_state = State.INT
                #else block shouldn't ever trigger, but it's here to prevent bugs from crashing program
                else:
                    token_list = []
                    error_message = "Unclassified Error, stopping scan. Inavlid character: "+self.current_char
                    token_list.append(Token("Error", error_message, error_line, error_col, error_message))
                    return TokenStream(token_list)
                #reset the token_text, unless error was detected in INSTR+
                if self.state != State.INSTRING:
                    self.token_text = ""
                token_list.append(Token("Error", error_message, error_line, error_col, error_message))
        #add EOF token
        token_list.append(Token("Eof", "", self.current_coords[0], self.current_coords[1], None))
        return TokenStream(token_list)

#Returns the formatted string verions of value
#numbers are casted to strings, bools return "true" or "false", strings are escaped
def correctToXml(value):
    fixed = str(value)
    if type(value) == bool:
        if value:
            fixed = "true"
        else:
            fixed = "false"
    elif type(value) == str:
        fixed = value.replace("\\","\\\\").replace("\t","\\t").replace("\n", "\\n").replace("'","\\'")
    return fixed

def tokenToXml(token):
    xml = "<" + token.type + "Token"
    if token.type != "Eof":
        xml += " line=" + str(token.row) + " col=" + str(token.col)
    val = token.value
    if not val is None:
        val = correctToXml(val)
        xml += " val='" + val + "'"
    xml += " />"
    return xml
import slang_parser as parser


def escape(unescaped_string):
    return unescaped_string.replace("\\","\\\\").replace("\t","\\t").replace("\n", "\\n").replace("'","\\'")


def addWithIndent(additionalText, indent):
    return indent + additionalText


def AstToXml(expr, indent=""):
    """Convert an AST into its XML-like representation"""
    # [CSE 262] You will need to implement this after you decide on the types
    # for your nodes
    node = expr    #casting expr to be a Node
    xml = ""

    if node == None:
        return xml

    if node.type == parser.NodeType.IDENTIFIER:
        xml += addWithIndent("<Identifier val='" + escape(node.field1) + "' />\n", indent)        
    
    elif node.type == parser.NodeType.DEFINE:
        xml += addWithIndent("<Define>\n", indent)
        indent += " "
        xml += AstToXml(node.field1, indent)
        xml += AstToXml(node.field2, indent)
        indent = indent[:-1] 
        xml += addWithIndent("</Define>\n", indent)  

    elif node.type == parser.NodeType.BOOL:
        bool_word = "false"
        if node.field1:
            bool_word = "true"
        xml += addWithIndent("<Bool val='" + bool_word + "' />\n", indent)
    
    elif node.type == parser.NodeType.INT:
        xml += addWithIndent("<Int val='" + str(node.field1) + "' />\n", indent)
    
    elif node.type == parser.NodeType.DOUBLE:
        xml += addWithIndent("<Dbl val='" + str(node.field1) + "' />\n", indent)

    elif node.type == parser.NodeType.LAMBDADEF:
        xml += addWithIndent("<Lambda>\n", indent)
        indent += " "
        xml += addWithIndent("<Formals>\n", indent)
        indent += " "
        for formal in node.field1:
            xml += AstToXml(formal, indent)
        indent = indent[:-1]
        xml += addWithIndent("</Formals>\n", indent)
        xml += addWithIndent("<Expressions>\n", indent)
        indent += " "
        for expression in node.field2:
            xml += AstToXml(expression, indent)
        indent = indent[:-1]
        xml += addWithIndent("</Expressions>\n", indent)
        indent = indent[:-1]
        xml += addWithIndent("<Lambda>\n", indent)
    
    elif node.type == parser.NodeType.IF:
        xml += addWithIndent("<If>\n", indent)
        indent += " "
        xml += AstToXml(node.field1, indent)
        xml += AstToXml(node.field2[0], indent)
        xml += AstToXml(node.field2[1], indent)
        indent = indent[:-1]
        xml += addWithIndent("</If>\n", indent)

    elif node.type == parser.NodeType.SET:
        xml += addWithIndent("<Set>\n", indent)
        indent += " "
        xml += AstToXml(node.field1, indent)
        xml += AstToXml(node.field2, indent)
        indent = indent[:-1]
        xml += addWithIndent("</Set>\n", indent)
    
    elif node.type == parser.NodeType.AND:
        xml += addWithIndent("<And>\n", indent)
        indent += " "
        for expression in node.field1:
            xml += AstToXml(expression, indent)
        indent = indent[:-1]
        xml += addWithIndent("</And>\n", indent)
        
    elif node.type == parser.NodeType.OR:
        xml += addWithIndent("<Or>\n", indent)
        indent += " "
        for expression in node.field1:
            xml += AstToXml(expression, indent)
        indent = indent[:-1]
        xml += addWithIndent("</Or>\n", indent)
        
    elif node.type == parser.NodeType.BEGIN:
        xml += addWithIndent("<Begin>\n", indent)
        indent = " "
        for expression in node.field1:
            xml += AstToXml(expression, indent)
        indent = indent[:-1]
        xml += addWithIndent("<Begin>\n",indent)
        
    elif node.type == parser.NodeType.APPLY:
        xml += addWithIndent("<Apply>\n", indent)
        indent += " "
        for expression in node.field1:
            xml += AstToXml(expression, indent)
        indent = indent[:-1]
        xml += addWithIndent("</Apply>\n",indent)
    
    elif node.type == parser.NodeType.CONS:
        xml += addWithIndent("<Cons>\n", indent)
        indent += " "
        if node.field1 is not None:
            xml += AstToXml(node.field1, indent)
        else:
            xml += addWithIndent("<Null />\n", indent)
        if node.field2 is not None:
            xml += AstToXml(node.field2, indent)
        else:
            xml += addWithIndent("<Null />\n", indent)
        indent = indent[:-1]
        xml += addWithIndent("</Cons>\n", indent)
    
    
    elif node.type == parser.NodeType.VEC:
        xml += addWithIndent("<Vector>\n", indent)
        indent += " "
        for items in node.field1:
            xml += AstToXml(items, indent)
        indent = indent[:-1]
        xml += addWithIndent("</Vector>\n", indent)
        
    elif node.type == parser.NodeType.SYMBOL:
        xml += addWithIndent("<Symbol val ='" + str(node.field1) + "' />\n", indent)

    elif node.type == parser.NodeType.QUOTE:
        xml += addWithIndent("<Quote>\n", indent)
        indent += " "
        xml += AstToXml(node.field1)
        indent = indent[:-1]
        xml += addWithIndent("</Quote>\n")
    #...
    elif node.type == parser.NodeType.TICK:
        xml += addWithIndent("<Tick>\n", indent)
        indent += " "
        xml += AstToXml(node.field1, indent)
        indent = indent[:-1]
        xml += addWithIndent("</Tick>\n", indent)
    
    elif node.type == parser.NodeType.CHAR:
        xml += addWithIndent("<Char val='" + str(node.field1) + "' />\n", indent)

    elif node.type == parser.NodeType.STRING:
        xml += addWithIndent("<Str val='" + escape(node.field1) + "' />\n", indent)

    elif node.type == parser.NodeType.BUILTINFUNC:
        raise Exception("BuildInFunc should not be visited")
    
    elif node.type == parser.NodeType.LAMBDADEF:
        raise Exception("LambdaVal should not be visited in P3")
        
    elif node.type == parser.NodeType.COND:
        xml += addWithIndent("<Cond>\n", indent)
        indent += " "
        for expressions in node.field1:
            xml += AstToXml(expressions, indent)
        indent = indent[:-1]
        xml += addWithIndent("</Cond>\n", indent)

    elif node.type == parser.NodeType.CONDITION:
        xml += addWithIndent("<Condition>\n", indent)
        indent += " "
        xml += addWithIndent("<Test>\n", indent)
        indent += " "
        xml += AstToXml(node.field1, indent)
        indent = indent[:-1]
        xml += addWithIndent("</Test>\n", indent)
        
        xml += addWithIndent("<Actions>\n", indent)
        indent += " "
        for expression in node.field2:
            xml += AstToXml(expression, indent)
        indent = indent[:-1]
        xml += addWithIndent("</Actions>\n", indent)
        indent = indent[:-1]
        xml += addWithIndent("</Condition>\n", indent)

    return xml

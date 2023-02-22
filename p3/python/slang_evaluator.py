import slang_parser


class Env:
    """An environment/scope in which variables are defined"""

    def __init__(self, outer, poundF, poundT, empty):
        """Construct an environment.  We need a single global value for false,
        for true, and for empty.  We optionally have a link to an enclosing
        scope"""
        self.outer = outer
        self.map = {}
        self.poundF = poundF
        self.poundT = poundT
        self.empty = empty


def makeDefaultEnv():
    """Create a default environment by mapping all the built-in names (true,
    false, empty list, and the built-in functions)"""
    # Please remove this print statement once you've addressed it.
    print("Once the parser's nodes are defined, you should make poundF, poundT, and empty! (evaluator.py)")
    poundF = None
    poundT = None
    empty = None
    e = Env(None, poundF, poundT, empty)
    return e

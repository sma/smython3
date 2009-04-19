# Copyright (c) 2009, Stefan Matthias Aust. All rights reserved.

# TODO None shouldn't render as "null"

# pass statement
>>> pass
Suite[Pass]

# break & continue (TODO must only occur in loops)
>>> break
Suite[Break]
>>> continue
Suite[Continue]

# This file is part of ancat.

# Create the base table for a graph.
# description - a description of the graph stored here
# configuration - some information about the required configuration
# edge_table - the name of the table containing the edges
# vertex_table - the name of the table containing the vertices


CREATE table graphs( 
    id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY, 
    description varchar(2048), 
    configuration varchar(2048), 
    edge_table varchar(256), 
    vertex_table varchar(256))


# Create the table containing edges
# id - Autoincrement primary key
# source - the id of the source vertex
# target - the id of the target vertex
# capacity - the capacity of the edge
# weight - the weight of the edge
# flow - a flow value assigned to the edge

CREATE table edges( 
    id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY, 
    source int not null, 
    target int not null, 
    capacity DOUBLE, 
    weight DOUBLE, 
    flow DOUBLE )

# Statement creating the table containing vertices
# id - Autoincrement primary key
# b - transshipment values assigned to the table statement

CREATE table vertices( 
    id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY, 
    vertex_id INTEGER not null,
    b DOUBLE not null )
    
# make the vertex_id field unique.
ALTER table vertices add unique(vertex_id)

#ALTER table graph add id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY;
#ALTER table graph rename to graphs;
#ALTER table vertices add vertex_id INTEGER
#ALTER table vertices add unique(vertex_id)
#DROP table vertices


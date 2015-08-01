
select count(*)
   from information_schema.tables
   where table_schema = 'PUBLIC' and table_name = 'TEST';
   

select count(*)
   from information_schema.tables
   where table_schema = 'PUBLIC'
   and table_name = 'GRAPHS'; 

drop table graphs

insert into graphs(description, configuration, edge_table, vertex_table) values( 'test description', 'test configuration', 'edges', 'vertices' )

delete from graphs where id = 9
delete from vertices

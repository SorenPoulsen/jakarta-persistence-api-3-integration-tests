-- Check the SQL in a SQL client. SQL errors in the import.sql file are ignored silently,
-- the data is just not loaded from that point onwards.
insert into Department (id, name) values (1, 'Dwayne Enterprises R&D');
insert into Department (id, name) values (2, 'Dwayne Enterprises Leadership');
insert into Employee (id, name, department_id) values (1, 'Cruse Dwayne', 2);
insert into Employee (id, name, department_id) values (2, 'Alfredo', 2);
insert into Employee (id, name, department_id) values (3, 'Mr Foxy Weasel', 1);
insert into Project (id, name) values (1, 'Rocket Car');
insert into Project (id, name) values (2, 'Rat Shuriken');
insert into Employee_Project (employee_id, project_id) values (3, 1);
insert into Employee_Project (employee_id, project_id) values (3, 2);
insert into Employee_Project (employee_id, project_id) values (1, 2);
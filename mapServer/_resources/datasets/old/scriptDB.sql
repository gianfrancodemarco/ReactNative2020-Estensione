CREATE DATABASE MapDB;
CREATE USER 'MapUser'@'localhost' IDENTIFIED BY 'map';
# GRANT SELECT ON MapDB.* TO 'MapUser'@'localhost' IDENTIFIED BY 'map';
GRANT SELECT ON MapDB.* TO 'MapUser'@'localhost';
GRANT CREATE ON MapDB.* TO 'MapUser'@'localhost'; -- Estensione
GRANT INSERT ON MapDB.* TO 'MapUser'@'localhost'; -- Estensione
CREATE TABLE MapDB.provaC(
 X varchar(10),
 Y float(5,2),
C float(5,2)
);
insert into MapDB.provaC values('A',2,1);
insert into MapDB.provaC values('A',2,1);
insert into MapDB.provaC values('A',1,1);
insert into MapDB.provaC values('A',2,1);
insert into MapDB.provaC values('A',5,1.5);
insert into MapDB.provaC values('A',5,1.5);
insert into MapDB.provaC values('A',6,1.5);
insert into MapDB.provaC values('B',6,10);
insert into MapDB.provaC values('A',6,1.5);
insert into MapDB.provaC values('A',6,1.5);
insert into MapDB.provaC values('B',10,10);
insert into MapDB.provaC values('B',5,10);
insert into MapDB.provaC values('B',12,10);
insert into MapDB.provaC values('B',14,10);
insert into MapDB.provaC values('A',1,1);
commit;


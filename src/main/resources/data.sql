insert into USERS (nombre, password) values ('ADMIN', '1234');
insert into USERS (nombre, password) values ('CONSULTA', '1234');

insert into ROL (nombre) values ('CREATE');
insert into ROL (nombre) values ('READ');

insert into USER_ROL_REL (user_id, rol_id)
       values ((select id
                  from USERS
                  where nombre = 'ADMIN'),
               (select id
                  from ROL
                  where nombre = 'CREATE'));
insert into USER_ROL_REL (user_id, rol_id)
       values ((select id
                  from USERS
                  where nombre = 'ADMIN'),
               (select id
                  from ROL
                  where nombre = 'READ'));
insert into USER_ROL_REL (user_id, rol_id)
       values ((select id
                  from USERS
                  where nombre = 'CONSULTA'),
               (select id
                  from ROL
                  where nombre = 'READ'));

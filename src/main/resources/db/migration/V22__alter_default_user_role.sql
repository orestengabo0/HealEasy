alter table users
    alter column role set default 'USER'::user_roles;
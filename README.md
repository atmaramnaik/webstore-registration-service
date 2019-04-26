Requirements
------------------------------------------------------------
* You have MySQL db setup and running on your machine

Steps
------------------------------------------------------------
1. Create Database named 'webstore' (or anything) in mysql
2. Create User 'vUser' (or anything) with some password granting all permissions on above created database
3. load project into your ide by loading build.gradle file
4. Set following env variables in your run configurations
    1. DB_USER=vUser (or whatever user you created above)
    2. DB_PASSWORD=test123$ (or whatever password you set for above user)
    3. DB=webstore (or whatever database you have created)
    4. DB_HOST=localhost (Your mysql server ip)
    5. DB_PORT=3306 (Your mysql port)
    6. SERVICE_PORT=9001 (This is port on which your service will run)
5. If you are running project first time change 'spring.jpa.hibernate.ddl-auto' property in application.properties to 'create' (currently it is 'validate'), this will run migration and create required schema in database
6. Run Application class

Endpoints
------------------------------------------------------------
1. /auth/login
2. /auth/register
3. /auth/all
4. /auth/roles
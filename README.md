To start the database run the following command:

> docker run --ulimit memlock=-1:-1 -it --rm=true --memory-swappiness=0 --name quarkus_test -e POSTGRES_USER=quarkus_test -e POSTGRES_PASSWORD=test123 -e POSTGRES_DB=quarkus_test -p 5432:5432 postgres:10.5

If you want to use PSQL to connect to the DB do (enter `test123` as the password):

> docker run -it --rm --link quarkus_test:postgres postgres psql -h quarkus_test -U quarkus_test


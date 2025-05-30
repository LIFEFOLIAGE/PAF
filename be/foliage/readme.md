
# Foliage BE

Back end applicazione web per gestione tagli boschivi foliage


## Installazione

Requisiti:
*   Java 20 o superiore
*   Maven

```bash
set JAVA_HOME=<path-java20-o-superiore>
mvn clean install -DskipTests=true
```


## Predisposizione del database

### Database, utenza applicativa, estensioni e schemi

Avviare il client psql in connessione verso il server su cui creare il database con un'utenza amministrativa (di solito postgres) al database di gestione (di solito postgres).


```bash
psql -U <utente-amministratore> -H <server-host> -d <database-di-gestione>
```

Eseguire i comandi per creazione di un nuovo database, una nuova utenza per l'applicazione e gli schemi foliage2 e foliage_extra

```sql
--creazione di un nuovo database
CREATE DATABASE "<nome-del-nuovo-database>";

--accesso al database appena creato
\c "<nome-del-nuovo-database>";

--creazione delle estensioni necessarie
CREATE EXTENSION postgis WITH SCHEMA public;
CREATE EXTENSION postgis_raster WITH SCHEMA public;

-- creazione utenza applicativa
CREATE USER <nome-utenza-applicativa> PASSWORD '<password-utenza-applicativa>';

-- creazione degli schemi
CREATE SCHEMA foliage2 AUTHORIZATION <nome-utenza-applicativa>;
CREATE SCHEMA foliage_extra AUTHORIZATION <nome-utenza-applicativa>;

```

### Creazione delle tabelle e predisposizione della configurazione iniziale nel database

Gli script con i comandi per la creazione degli oggetti nel database sono [in questa cartella del progetto](https://dev.azure.com/DEV-PA/Agricoltura_BU/_git/foliage?version=GBv0.1&path=\be\foliage\src\main\resources\dbScripts\v0.1). In questa cartella vi sono 2 script principali che servono per predisporre lo schema foliage2:
* `main-lazio.sql`: predispone l'ambiente con la configurazione per la regione Lazio.
* `main-umbria.sql`: predispone l'ambiente con la configurazione per la regione Umbria.

Per procedere occorre quindi avviare lo script per la regione d'interesse.

```bash
cd src\main\resources\dbScripts\v1.0
psql -U <nome-utenza-applicativa> -H <server-host> -d <nome-del-nuovo-database> -f <nome-script-Predisposizione>
```

Le tabelle dello schema foliage_extra (che viene utilizzato per memorizzare i dati GIS esterni) possono essere recuperate da uno dei dump che sono disponibili [in questa cartella del progetto](https://dev.azure.com/DEV-PA/Agricoltura_BU/_git/foliage?version=GBv0.1&path=\extra\dump-foliage-extra):
* `foliage_extra_lazio.dmp`: tabelle per l'ambiente della regione Lazio.
* `foliage_extra_umbria.dmp`: tabelle per l'ambiente della regione Lazio.

Per procedere occorre quindi caricare il file della regione d'interesse.

```bash
cd src\main\resources\dbScripts\v1.0
pg_restore --host=<server-host> --username=<nome-utenza-applicativa> --dbname=<nome-del-nuovo-database> <path-file-dump>
```

## Avvio in locale

### Avvio con Maven
Per avviare l'applicazione con le impostazioni di default

```bash
set JAVA_HOME=<path-java20-o-superiore>
mvn spring-boot:run
```

Per avviare l'applicazione con le impostazioni per la regione Lazio

```bash
set JAVA_HOME=<path-java20-o-superiore>
mvn spring-boot:run -P lazio
```

Per avviare l'applicazione con le impostazioni per la regione Umbria

```bash
mvn spring-boot:run -P umbria
```

### Avvio Standard

Per avviare l'applicazione con le impostazioni di default

```bash
java -jar target\foliage-0.0.1-SNAPSHOT.jar
```

Per avviare l'applicazione con le impostazioni per la regione Lazio

```bash
java -jar target\foliage-0.0.1-SNAPSHOT.jar --spring.profiles.active=lazio
```

Per avviare l'applicazione con le impostazioni per la regione Umbria

```bash
java -jar target\foliage-0.0.1-SNAPSHOT.jar --spring.profiles.active=umbria
```
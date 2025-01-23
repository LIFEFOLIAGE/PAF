# LifeFoliage - PAF: Installazione e Avvio

## Requisiti

*	7zip e unzip
*	Apache 2 (con moduli rewrite, proxy e proxy_http)
*	GeoServer 2.15 e Tomcat 9 su java 11
*	Un Identity Manager Service compatibile con il protocollo oauth2
*   Java 21 e Maven per il backend del pacchetto PAF
*	PostgreSql v15 - Database
*	PostgreSql - Client
*	NodeJs v22.13


## Premessa

In questa guida si fa riferimento ai seguenti host:
*	**host-sviluppo**: dove vengono scaricati e compilati i file sorgenti dell'applicativo PAF
*	**host-backend**: dove viene eseguito il componente backend dell'applicativo PAF
*	**host-geoserver**: dove viene eseguito il componente backend dell'applicativo PAF
*	**host-pubblicazione**: con il server web con cui viene pubblicato l'applicativo PAF

Tali host non devono necessariamente esser diversi, ma è importante che i comandi forniti in questa guida vengano eseguiti sull'host a cui si fa riferimento nella sezione che li riporta.


## Guida all'installazione dei principali requisiti

### <a id="installZip">Installazione 7zip e unzip</a>

Sugli host **host-sviluppo** e **host-geoserver** occorre:
*	installare unzip per estrarre i file dei pacchetti per PAF, tomcat e geoserver

```bash
sudo apt install unzip
```

Sull'host **host-sviluppo** occorre:
*	installare 7zip per estrarre i file dove sono stati archiviati i dati GIS esterni

```bash
sudo apt install p7zip-full
```

### <a id="installApache">Installazione Apache 2</a>

Sull'host **host-pubblicazione** occorre installare il server web Apache 2 con le estensioni rewrite, proxy e proxy_http.

```bash
sudo apt install apache2
sudo a2enmod rewrite
sudo a2enmod proxy
sudo a2enmod proxy_http
```

### <a id="setupGeoserver">Predisposizione GeoServer</a>

Sull'host **host-geoserver** occorre installare Java 11 e Tomcat come requisiti di GeoServer.

#### <a id="installJdk11">Installazione Java 11</a>

```bash
sudo apt install openjdk-11-jdk
```

#### <a id="installTomcat">Download e installazione Tomcat</a>

```bash
sudo useradd -m -U -d /opt/tomcat -s /bin/false tomcat
wget http://mirror.nohup.it/apache/tomcat/tomcat-9/v9.0.37/bin/apache-tomcat-9.0.37.tar.gz -O apache-tomcat-9.0.37.tar.gz
sudo tar -xf apache-tomcat-9.0.37.tar.gz -C /opt/tomcat/
sudo ln -s /opt/tomcat/apache-tomcat-9.0.37 /opt/tomcat/latest
sudo chown -R tomcat: /opt/tomcat
sudo sh -c 'chmod +x /opt/tomcat/latest/bin/*.sh'
```

#### <a id="configTomcat">Configurazione script avvio Tomcat 9</a>

Aprire il file con lo script del servizio

```bash
sudo systemctl edit --full tomcat.service
```

Definire il file con il seguente contenuto

```text
[Unit]
Description=Tomcat 9 servlet container
After=network.target

[Service]
Type=forking

User=tomcat
Group=tomcat

Environment="JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64"
Environment="JAVA_OPTS=-Djava.security.egd=file:///dev/urandom -Djava.awt.headless=true"

Environment="CATALINA_BASE=/opt/tomcat/latest"
Environment="CATALINA_HOME=/opt/tomcat/latest"
Environment="CATALINA_PID=/opt/tomcat/latest/temp/tomcat.pid"
Environment="CATALINA_OPTS=-Xms512M -Xmx1024M -server -XX:+UseParallelGC"

ExecStart=/opt/tomcat/latest/bin/startup.sh
ExecStop=/opt/tomcat/latest/bin/shutdown.sh

[Install]
WantedBy=multi-user.target
```

Ricaricare il demone dei servizi

```bash
sudo systemctl daemon-reload
```

Abilitare il servizio tomcat

```bash
sudo systemctl enable --now tomcat
```

#### <a id="deployGeoserver">Pubblicazione GeoServer</a>

Download ed estrazione geoserver

```bash
mkdir Downloads 
cd Downloads/
wget http://sourceforge.net/projects/geoserver/files/GeoServer/2.17.1/geoserver-2.17.1-war.zip
unzip geoserver-2.17.1-war.zip
```

Spostare il pacchetto geoserver dentro tomcat

```bash
sudo mv geoserver.war /opt/tomcat/apache-tomcat-9.0.37/webapps
```

Riavviare tomcat

```bash
sudo systemctl restart tomcat
```

### <a id="installJdk21">Installazione Java 21 e Maven</a>

Sugli host **host-backend** e **host-sviluppo**, è necessario installare Java 21 con Maven per poter compilare ed eseguire il componente di backend del progetto PAF.

```bash
sudo apt install openjdk-21-jdk
sudo apt install maven
```

### <a id="installPgClient">Installazione client Postgres</a>

Sull'host **host-sviluppo** installare il client postgress

```bash
sudo apt install postgresql-client
```

### <a id="installNode">Installazione NodeJs</a>

Sull'host **host-sviluppo** installare NodeJs attraverso nvm

```bash
curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.39.3/install.sh | bash
source ~/.bashrc
nvm install v22.13.0
```

## Predisposizione dei sorgenti per l'applicativo PAF

Sull'host **host-sviluppo** viene scaricato ed estratto il pacchetto con i sorgenti del progetto PAF

### <a id="definePafFolder">Identificazione della cartella per il progetto PAF</a>
sostituire il percorso della directory di destinazione dei sorgenti del progetto PAF a `<cartella-sorgenti-paf-foliage>`

```bash
export FOLIAGE_HOME=<cartella-sorgenti-paf-foliage>
```

### <a id="createPafFolder">Creazione della cartella ed acceso al progetto PAF</a>

nel caso in cui la directory esista già assicurarsi che sia vuota

```bash
mkdir -p $FOLIAGE_HOME
```

### <a id="downloadSource">Download file contenente il progetto PAF da GitHub</a>

```bash
cd $FOLIAGE_HOME
curl -LO https://github.com/LIFEFOLIAGE/LIFEFOLIAGE/archive/refs/heads/main.zip
```

### <a id="exctratSource">Estrazione dei file del progetto PAF</a>

```bash
cd $FOLIAGE_HOME
unzip main.zip
```

## <a id="presetIms">Identity Manager Service</a>

In questa guida si presuppone che un Identity Manager Service compatibile con oauth2 sia già disponibile, che sia già configurato per gestire l'autenticazione alla piattaforma PAF che si sta configurando e che sia accessibile dagli utilizzatori dell'appplicazione e dall'host **host-backend**.

## <a id="presetDbServer">Predisposizione del database PAF</a>

In questa guida si presuppone che il server postgres sia già disponibile con un'utenza amministrativa da poter usare per creare il database PAF.

## <a id="initialConfig">Configurazione iniziale</a>

### <a id="createDb">Creazione database, utenza applicativa, estensioni e schemi</a>

Sull'host **host-sviluppo**, avviare il client psql in connessione verso il server Postgres in cui creare il database con un'utenza amministrativa (di solito è postgres) al database di gestione (di solito è postgres) ed esegiure i comandi SQL riportati.

In particolare occorre fare le seguenti sostituzioni:
*	Inserire l'indirizzo del server db al posto di `<host-db-server>`
*	Inserire la porta di ascolto del server db del database al posto di `<porta-db-server>`
*	Inserire il nome utente dell'amministatore del database al posto di `<utente-dba>`
*	Inserire il nome del nuovo database per l'applicativo della PAF al posto di `<nome-database-paf>`
*	Inserire il nome dell'utente da utilizzare per l'applicativo della PAF al posto di `<nome-utenza-applicativa>`
*	Inserire la password dell'utente da utilizzare per l'applicativo della PAF al posto di `<password-utenza-applicativa>`

```bash
psql -U <utente-dba> -H <host-db-server> -p <porta-db-server> -d <nome-database-paf>
```

Quindi eseguire con il client postgres i comandi per la creazione di un nuovo database, di una nuova utenza per l'applicazione e degli schemi foliage2 e foliage_extra in cui verranno definite le tabelle utilizzate dall'applicazione PAF. Per eseguire i compiti appena descritti si possono utilizzare i comandi riportati qui di seguito

```sql
--creazione di un nuovo database
CREATE DATABASE "<nome-del-database-foliage>";

--accesso al database appena creato
\c "<nome-del-database-foliage>";

--creazione delle estensioni necessarie
CREATE EXTENSION postgis WITH SCHEMA public;
CREATE EXTENSION postgis_raster WITH SCHEMA public;

-- creazione utenza applicativa
CREATE USER <nome-utenza-applicativa> PASSWORD '<password-utenza-applicativa>';

-- creazione degli schemi
CREATE SCHEMA foliage2 AUTHORIZATION <nome-utenza-applicativa>;
CREATE SCHEMA foliage_extra AUTHORIZATION <nome-utenza-applicativa>;
```

### <a id="createDbObjs">Creazione delle tabelle e generazione della configurazione iniziale nel database PAF</a>

Gli script con i comandi per la creazione degli oggetti nel database sono nella directory `$FOLIAGE_HOME/PAF/be/foliage/src/main/resources/dbScripts/v0.1` dove c'è lo script principale per predisporre lo schema foliage2 per la regione di interesse:

* `main-<regione>.sql`: predispone l'ambiente con la configurazione per la regione.

Per procedere occorre quindi avviare lo script dall'host **host-sviluppo**.

```bash
cd $FOLIAGE_HOME/PAF/be/foliage/src/main/resources/dbScripts/v0.1
psql -U <nome-utenza-applicativa> -H <host-db-server> -d <nome-del-database-foliage> -f main-<regione>.sql
```

Le tabelle dello schema foliage_extra (che viene utilizzato per memorizzare i dati GIS esterni) possono essere recuperate dal file di dump che presente nella directory `PAF/extra/dump-foliage_extra`:
* `foliage_extra_<regione>.dmp`: tabelle per l'ambiente della regione.

A causa delle limitazioni sulle dimensioni dei file su GitHub, il dump con i dati GIS esterni è stato archiviato e scomposto in 6 file più piccoli, quindi prima di poter essere utilizzato va estratto con 7zip. Dall'host **host-sviluppo**

```bash
cd $FOLIAGE_HOME/PAF/extra/dump-foliage_extra
7z x foliage_extra_<regione>.7z.001
pg_restore --host=<host-db-server> --username=<nome-utenza-applicativa> --dbname=<nome-del-database-foliage> foliage_extra_<regione>.dmp
```

## <a id="configGeoserver">Inizializzazione di GeoServer</a>

La configurazione per geoserver può essere importata copiando i file presenti in **host-sviluppo** nella directory del progetto `$FOLIAGE_HOME/PAF/extra/geoserver_config` dove è registrata la definizione dei servizi WFS per i layer vettoriali associati ai dati caricati precedentemente nello schema foliage_extra del database e dei servizi WMS che fanno da proxy verso le mappe esterne utilizzate dall'applicativo. I file di configurazione vanno copiati in **host-geoserver** nella directory di configurazione di geoserver (`/opt/tomcat/apache-tomcat-9.0.37/webapps/geoserver/data/workspaces/`). Inoltre dopo aver copiato i file occorre modificare manualmente alcune impostazioni per poter procedere.

In particolare occorre aprire il file del progetto `PAF/extra/geoserver_config/foliage/foliage_<nome_regione>/datastore.xml` che è riportato qui sotto e fare le seguenti sostituzioni:
*	Inserire il nome della regione al posto di `<nome_regione>`
*	Inserire l'indirizzo del database al posto di `<host-db-server>`
*	Inserire la porta di accesso al database al posto di `<porta-db-server>`
*	Inserire il nome del nuovo database per l'applicativo della PAF al posto di `<nome-database-paf>`
*	Inserire il nome dell'utenza applicativa sul database al posto di `<nome-utenza-applicativa>`
*	Inserire la password dell'utenza applicativa sul database al posto di `<password-utenza-applicativa>`

Per questi parametri vanno eseguite le stesse sostituzioni già effettuate per la [creazione del database](#createDb).

```xml
<dataStore>
	<id>DataStoreInfoImpl-4dd5f913:18c6c117e6a:-7d97</id>
	<name>foliage_<nome_regione></name>
	<type>PostGIS</type>
	<enabled>true</enabled>
	<workspace>
		<id>WorkspaceInfoImpl-7cd53224:17ff4993504:-7fff</id>
	</workspace>
	<connectionParameters>
		<entry key="schema">foliage_extra</entry>
		<entry key="Evictor run periodicity">300</entry>
		<entry key="Max open prepared statements">50</entry>
		<entry key="encode functions">false</entry>
		<entry key="Batch insert size">1</entry>
		<entry key="preparedStatements">false</entry>
		<entry key="database"><nome-database-paf></entry>
		<entry key="host"><host-db-server></entry>
		<entry key="Loose bbox">true</entry>
		<entry key="Estimated extends">true</entry>
		<entry key="fetch size">1000</entry>
		<entry key="Expose primary keys">true</entry>
		<entry key="validate connections">true</entry>
		<entry key="Support on the fly geometry simplification">true</entry>
		<entry key="Connection timeout">20</entry>
		<entry key="create database">false</entry>
		<entry key="port"><porta-db-server></entry>
		<entry key="passwd">plain:<password-utenza-applicativa></entry>
		<entry key="min connections">1</entry>
		<entry key="dbtype">postgis</entry>
		<entry key="namespace">foliage</entry>
		<entry key="max connections">10</entry>
		<entry key="Evictor tests per run">3</entry>
		<entry key="Test while idle">true</entry>
		<entry key="user"><nome-utenza-applicativa></entry>
		<entry key="Max connection idle time">300</entry>
	</connectionParameters>
	<__default>false</__default>
</dataStore>
```

## <a id="configApache">Configurazione e avvio server web Apache 2</a>

Sull'host **host-pubblicazione**, la configurazione del server web apache 2 può essere impostata sulla base di quanto riportato qui di seguito dove occorre effettuare le seguenti sostituzioni:
*	l'indirizzo dell'host host-pubblicazione al posto di `<indirizzo-host-pubblicazione>`
*	la porta in cui è in ascolto il server web nell'host host-pubblicazione al posto di `<porta-pubblicazione>`
*	il percorso del server web in cui viene pubblicata l'applicazione paf al posto di `<path-pubblicazione-paf>`
*	l'indirizzo dell'host host-backend al posto di `<indirizzo-host-backend>`
*	la porta per l'ascolto del backend PAF al posto di `<porta-backend>`
*	l'indirizzo dell'host host-geoserver al posto di `<indirizzo-host-geoserver>`
*	la porta per l'ascolto di tomcat nell'host host-geoserver al posto di `<porta-geoserver>`



> [!IMPORTANT]
> Assicurarsi che l'Identity Manager Service sia configurato per accettare richieste CORS con l'[header origin](https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Origin) che riporta l'url `http://<indirizzo-host-pubblicazione>:<porta-pubblicazione>` (dove occorre effettuare le stesse sostituzioni appena descritte per i parametri `<indirizzo-host-pubblicazione>` e `<porta-pubblicazione>`)



```text
<VirtualHost <indirizzo-host-pubblicazione>:<porta-pubblicazione>>
	ServerAdmin webmaster@localhost
	DocumentRoot <path-distribuzione-frontend>
	ServerPath "<path-pubblicazione-paf>"

	<Directory "<path-distribuzione-frontend>">
		RewriteEngine On
		RewriteCond %{REQUEST_FILENAME} !-f
		RewriteCond %{REQUEST_FILENAME} !-d
		RewriteCond %{REQUEST_FILENAME} !-l
		RewriteRule ^(.*)$ index.html [L]
	</Directory>

	ProxyPass        /geoserver http://<indirizzo-host-geoserver>:<porta-geoserver>/geoserver
	ProxyPassReverse /geoserver http://<indirizzo-host-geoserver>:<porta-geoserver>/geoserver

	ProxyPass        /backend http://<indirizzo-host-backend>:<porta-backend>
	ProxyPassReverse /backend http://<indirizzo-host-backend>:<porta-backend>
</VirtualHost>
```

## <a id="configPafFe">Configurazione e compilazione del frontend PAF</a>

Sull'host **host-sviluppo**, prima di avviare la compilazione del frontend occorre impostare alcune parametrizzazioni che vengono descritte qui di seguito in due file:
*	`PAF/fe/foliage-fe/src/environments/defs/pars.ts`
	riportato di seguito e dove occorre fare le seguenti sostituzioni:
	*	l'url per raggiungere il reverse proxy del backend sul server web apache al posto di `<url-reverse-proxy-be>`
	*	il nome della regione in maiuscolo al posto di `<nome-regione-uppercase>`

```ts
export const regione = '<nome-regione-uppercase>';
export const apiServerPath = '/backend/api/web';
export const mapMaxZoom = 20;
export const apiOrigin = undefined;
```

*	`PAF/fe/foliage-fe/src/environments/iam/iam.ts`
	riportato di seguito e dove occorre fare le seguenti sostituzioni:
	*	l'indirizzo dell'host host-pubblicazione al posto di `<indirizzo-host-pubblicazione>`
	*	la porta in cui è in ascolto il server web nell'host host-pubblicazione al posto di `<porta-pubblicazione>`
	*	il percorso del server web in cui viene pubblicata l'applicazione paf al posto di `<path-pubblicazione-paf>`
	*	l'url per raggiungere il discovery document dell'identity server oauth2 al posto di `<url-oauth2-discovery>`
	*	il client id dell'identity server per oauth2 al posto di `<client-id>`
	*	il client secret dell'identity server per oauth2 al posto di `<client-secret>`
Per i valori di `<indirizzo-host-pubblicazione>`, `<porta-pubblicazione>` e `<path-pubblicazione-paf>` utilizzare gli stessi valori indicati nella [configurazione del server web](#configApache)

```ts
export const iamConfig : Record<string, any>= {
	issuer: '<url-oauth2-discovery>',
	redirectUri: '<indirizzo-host-pubblicazione>:<porta-pubblicazione>/<path-pubblicazione-paf>',
	logoutUrl: '<indirizzo-host-pubblicazione>:<porta-pubblicazione>/<path-pubblicazione-paf>',
	clientId: '<client-id>',
	dummyClientSecret: '<client-secret>',
	scope: 'openid',
	responseType: 'code',
	sessionChecksEnabled: false,
	// begin: richiesto se il documento non e' completamente conforme
	strictDiscoveryDocumentValidation: false,
	skipIssuerCheck: true,
	// end: richiesto se il documento non e' completamente conforme
	requireHttps: false,
	showDebugInformation: true,
	//disablePKCE: false,
	useHttpBasicAuth: true,
	timeoutFactor: 0.5
};
```

> [!IMPORTANT]
> Assicurarsi che il valore indicato per `redirectUri` (`<indirizzo-host-pubblicazione>:<porta-pubblicazione>/<path-pubblicazione-paf>`) sia aggiunto agli indirizzi di reindirizzamento accettati dall'Identity Manager Service)

```bash
cd $FOLIAGE_HOME/PAF/fe/foliage-fe/
npm install
npm run build_<regione>
```

## <a id="deployPafFe">Rilascio del frontend PAF sul server web Apache 2</a>

```bash
cp $FOLIAGE_HOME/PAF/fe/foliage-fe/dist/foliage<regione> <path-distribuzione-frontend>
```

## <a id="configPafBe">Configurazione del backend PAF</a>

Sull'host **host-sviluppo**, prima di avviare la compilazione del componente di backend, occorre impostare alcune parametrizzazioni che vengono descritte qui di seguito nel file `$FOLIAGE_HOME/PAF/be/foliage/src/main/resources/application.properties`
*	il codice istat della regione gestita dall'applicazione al posto di `<cod-regione>`
*	il sistema di referimento da utilizzare per gestire le coordinate delle geometrie al posto di `<srid>`
*	Inserire l'indirizzo del database al posto di `<host-db-server>`
*	Inserire la porta di accesso al database al posto di `<porta-db-server>`
*	Inserire il nome dell'utenza applicativa sul database al posto di `<nome-utenza-applicativa>`
*	Inserire la password dell'utenza applicativa sul database al posto di `<password-utenza-applicativa>`
*	la porta di ascolto del backend PAF al posto di `<porta-backend>`
*	l'url del servizio jwks dell'identity manager al posto di `<url-oauth2-jwks>`
*	il campo nel token jwt contenente il codice fiscale dell'utente al posto di `<codiceFiscale-field>`
*	il campo nel token jwt contenente il nome dell'utente al posto di `<name-field>`, poiché non si tratta di un valore necessario, se il campo non è disponibile si può inserire una stringa vuota
*	il campo nel token jwt contenente il cognome dell'utente al posto di `<surname-field>`, poiché non si tratta di un valore necessario, se il campo non è disponibile si può inserire una stringa vuota
*	il campo nel token jwt contenente l'indirizzo dell'utente al posto di `<address-field>`, poiché non si tratta di un valore necessario, se il campo non è disponibile si può inserire una stringa vuota
*	il campo nel token jwt contenente la città di residenza dell'utente al posto di `<city-field>`, poiché non si tratta di un valore necessario, se il campo non è disponibile si può inserire una stringa vuota
*	il campo nel token jwt contenente il cap dell'utente al posto di `<cap-field>`, poiché non si tratta di un valore necessario, se il campo non è disponibile si può inserire una stringa vuota
*	il campo nel token jwt contenente la data di nascita dell'utente al posto di `<birthDate-field>`, poiché non si tratta di un valore necessario, se il campo non è disponibile si può inserire una stringa vuota
*	il campo nel token jwt contenente il luogo di nascita dell'utente al posto di `<birthPlace-field>`, poiché non si tratta di un valore necessario, se il campo non è disponibile si può inserire una stringa vuota
*	il campo nel token jwt contenente il numero di telefono dell'utente al posto di `<phoneNumber-field>`, poiché non si tratta di un valore necessario, se il campo non è disponibile si può inserire una stringa vuota
*	il campo nel token jwt contenente il genere dell'utente al posto di `<gender-field>`, poiché non si tratta di un valore necessario, se il campo non è disponibile si può inserire una stringa vuota
*	il campo nel token jwt contenente l'indirizzo email dell'utente al posto di `<email-field>`, poiché non si tratta di un valore necessario, se il campo non è disponibile si può inserire una stringa vuota
*	il campo nel token jwt contenente l'indirizzo pec dell'utente al posto di `<pec-field>`, poiché non si tratta di un valore necessario, se il campo non è disponibile si può inserire una stringa vuota
*	il formato del campo mel token jwt contenente la data di nascita [queste specifiche](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/time/format/DateTimeFormatter.html#patterns) al posto di `<birthDate-dateFormat>`, poiché la data di nascita è un valore necessario, se il campo non è disponibile si può inserire una stringa vuota

Per i parametri riguardanti il database le sostituzioni vanno eseguite con gli stessi valori scelti durante la [creazione del database](#configApache).
Per la porta di ascolto del backend PAF la sostituzione va eseguita con lo stesso valore scelto durante la [configurazione di Apache 2](#createDb)

```text
spring.profiles.active=@activatedProperties@

#codice istat regione corrente(10: UMBRIA, 12: LAZIO)
foliage.cod_regione=<cod-regione>

#sistema di riferimento delle geometrie nelle tabelle con i dati delle istanze
foliage.geometry_srid=<srid>

#configurazione del database (stringa connessione, utente, password)
spring.datasource.url=jdbc:postgresql://<host-db-server>:<porta-db-server>/<nome-database-paf>
spring.datasource.username=<nome-utenza-applicativa>
spring.datasource.password=<password-utenza-applicativa>


#altri parametri per la configurazione del database
spring.datasource.driver-class-name=org.postgresql.Driver
spring.datasource.platform=postgres
spring.datasource.hikari.idle-timeout=30000
spring.datasource.hikari.minimum-idle=2
spring.datasource.hikari.max-lifetime= 60000


#modalità di blocco utenze (black: black list, white: white list, <vuoto>: nessuna)
foliage.security.user-block-mode=<user-block-mode>

#porta di ascolto dell'applicazione
server.port=<porta-backend>

#path dell'applicazione
foliage.base-path=/

#url del servizio jwks dell'identity manager
foliage.jwk-jwksurl=<url-oauth2-jwks>

#mappatura campi token jwt del identity manager
foliage.jwt.codiceFiscale=<codiceFiscale-field>
foliage.jwt.username=<codiceFiscale-field>
foliage.jwt.name=<name-field>
foliage.jwt.surname=<surname-field>
foliage.jwt.address=<address-field>
foliage.jwt.city=<city-field>
foliage.jwt.cap=<cap-field>
foliage.jwt.birthDate=<birthDate-field>
foliage.jwt.birthPlace=<birthPlace-field>
foliage.jwt.phoneNumber=<phoneNumber-field>
foliage.jwt.gender=<gender-field>
foliage.jwt.email=<email-field>
foliage.jwt.pec=<pec-field>
foliage.jwt.dateFormat=<birthDate-dateFormat>


#configurazione per i messaggi d'errore
server.error.include-stacktrace=never
server.error.include-message=always
server.error.include-binding-errors= always
server.error.include-exception=true

#abilitazione configurazione di sviluppo (ignora validazione token)
foliage.is_development=false

#file configurazione di log
logging.config=classpath:log4j2.xml

#configurazione livello di log per i vary package
logging.level.org.springframework.security.web: INFO
logging.level.org.springframework.web = INFO
logging.level.it.almaviva.foliage = INFO
logging.level.org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping: INFO
logging.level.org.springframework.security.web.util.matcher.RegexRequestMatcher: INFO
```

## <a id="installPafBe">Compilazione del backend PAF</a>

Per la compilazione del backend, che va effetuata nell'host **host-backend**, occorre copiare i file sorgenti del componente di backend dall'host **host-sviluppo** (dalla directory `$FOLIAGE_HOME/PAF/be/foliage`) e impostare la variabile d'ambiente JAVA_HOME. Quindi dalla cartella in cui sono stati copiati i file del progetto eseguire i seguenti comandi:

```bash
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
mvn clean install -DskipTests=true
```

## <a id="runPafBe">Avvio del backend PAF</a>

Nell'host **host-backend** dalla cartella in cui sono stati compilati i file del componente di backend eseguire i seguenti comandi:
```bash
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
mvn spring-boot:run
```

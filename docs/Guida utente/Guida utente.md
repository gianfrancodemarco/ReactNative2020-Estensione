## **Regression Tree Miner** - Estensione

*Metodi avanzati di Programmazione - Caso di studio a.a. 2019-2020*

Progetto realizzato da: **Gianfranco Demarco** (mat. 708795) - gianfranco.demarco26@studenti.uniba.it



**Indice**

1. Introduzione 

2. React e React Native

3. Guida d'installazione

4. Riepilogo funzionalità aggiuntive

5. Casi di test

6. Javadoc
7. Link Utili

### 1. Introduzione

La documentazione del progetto base è disponibile al percorso 
*[**progettoBase**]/mapServer/doc/Guida utente*



Come estensione si è scelto di realizzare un'applicazione Android, sviluppata sfruttando React Native, che sostituisse l'applicativo client.

Inoltre sono presenti delle estensioni minori, sviluppate per migliorare l'esperienza utente o per ampliare la gamma di funzionalità dell'applicativo.

La comunicazione avviene tramite il meccanismo delle socket; si è scelto di mantenere questo meccanismo sebbene l'impiego di altre architetture (ad esempio l'esposizione di API Rest) avrebbe semplificato notevolmente la fase di sviluppo per non snaturare il progetto.



### 2. React e React Native



##### React

> **React **(noto anche come React.js o ReactJS) è una libreria JavaScript per la creazione di interfacce utente. È mantenuto da Facebook e da una comunità di singoli sviluppatori e aziende.
>
> React può essere utilizzato come base nello sviluppo di applicazioni a pagina singola o mobile. Tuttavia, React si occupa solo del rendering dei dati sul DOM, pertanto la creazione di applicazioni React richiede generalmente l'uso di librerie aggiuntive per lo state management e il routing. Redux e React Router sono i rispettivi esempi di tali librerie.
>
> - Wikipedia

React nasce per velocizzare lo sviluppo di applicativi di Front-End e per massimizzare il riutilizzo di codice.

Concetti cardine di React sono:

- JSX (JavaScript eXtended): permette di trattare blocchi di codici HTML come variabili;
- componente: unità base dello sviluppo in React; rappresenta un componente riutilizzabile e personalizzabile



##### React Native

React Native è un framework per lo sviluppo di applicazioni mobile sviluppato da Facebook e basato sull'ecosistema di React.

Con esso condivide molti concetti ed approcci, sebbene non sia possibile utilizzare JSX e HTML con React Native.

Lo scopo di React Native è quello di velocizzare lo sviluppo di applicativi mobile, nonché fornire un metodo agli sviluppatori per far sì che la stessa codebase (o comunque con modifiche minime) possa essere utilizzata per compilare gli applicativi per diversi sistemi operativi (Android/iOS).

Con React Native, lo sviluppatore costruisce le interfacce e le logiche attraverso i componenti messi a disposizione della libreria (potendone comunque creare di nuovi partendo da essi).

Il compilatore si occupa poi di tradurre questi componenti standard nel codice nativo della piattaforma scelta.

Questo fornisce alle applicazioni sviluppate in React Native delle prestazioni del tutto equiparabili a quelle native.



### 3. Guida di installazione

#### Server

La guida d'installazione per l'applicativo server è analoga a quella della versione base del progetto.

Si riportano di seguito i dettagli.



Pre-requisiti:

```
- MySQL; JRE 8+ installati sulla macchina.
- esistenza di un'utenza "root" per il server MySQL. Se la password è diversa da "root", modificare il file "setup.bat" alla riga 2 sostituendo la password corretta.
```

1. Eseguire lo script *setup.bat*.

Esso si occupa di creare l'utenza per l'applicativo sul database; creare e popolare le tabelle per 2 dataset di test; eseguire l'applicativo server e l'applicativo client.

Per le esecuzioni successive, eseguire gli script *runServer.bat* e *runClient.bat* in questo ordine.

### 

#### Client

Come anticipato, l'applicativo client per l'estensione è rappresentato da un'applicazione per sistema operativo mobile Android.

Pre-requisiti:

- Dispositivo Android versione Android 4.4W o superiore
- Connessione ad internet sul dispositivo Android



E' necessario trasferire il file compilato (*.apk*) sul dispositivo (ad esempio tramite Cloud o connessione USB da un PC).

Per poter testare agevolmente tutte le funzionalità dell'applicativo, trasferire anche i file .*dat* e .*sql* che si trovano nella cartella *src/script*. ****



1.  Eseguire l'apk per installare l'applicativo.
2. Il sistema operativo Android richiederà di abilitare un'impostazione per installare applicativi da Origini sconosciute; confermare.
3. Sarà inoltre richiesto il permesso di accedere alla connessione internet; confermare anche in questo caso.
4. Infine, all'esecuzione di una certa funzionalità (upload di file) verrà richiesto di garantire l'accesso alla memoria interna del dispositivo: confermare.****



### 4. Riepilogo funzionalità aggiuntive

### 5. Casi di test

### 6. Javadoc

### 7. Link Utili
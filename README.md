# Assigment 02 PCD [A.A. 2023/204] 
Repository relativo al secondo Assigment del corso di programmazion concorrente e distribuita.

## Part 1
Nella prima macro parte si richiede di affrontare il problema illustrato nel primo assignment (entrambi i punti) 
utilizzando un approccio asincrono basato su task (Framework Executors). 

## Part 2
Nella seconda macro parte si introduce un nuovo problema, che deve essere affrontato in tre modi diversi: (1) una 
soluzione basata su programmazione asincrona ad eventi/event-loop, (2) una soluzione basata su programmazione reattiva 
e (3) una soluzione basata su virtual thread.

### Descrizione del problema

Si vuole realizzare una sistema che, una dato un indirizzo web, una parola e un valore p, restituisca un report 
contenente l'elenco delle pagine che contengono quella parola e l'occorrenza della parola nella pagine, a partire 
dall'indirizzo specificato e considerando le pagine collegate, ricorsivamente, fino a un livello di profondità pari al 
valore p.

La richiesta è articolata in 2 punti:

1) Sviluppare una libreria che fornisca la funzionalità come metodo asincrono (in pseudocodice) getWordOccurrences, 
che avrà come parametri l'indirizzo web di partenza, la parola e la profondità e restituirà un report.  Insieme alla 
libreria, sviluppare un semplice programma che ne testi e esemplifichi l'utilizzo, senza GUI. I dettagli specifici della
libreria (struttura, API) possono variare considerando le tre versioni diverse da sviluppare (Eventi/Event-Loop, 
Programmazione Reattiva, Virtual Thread).

2) Sviluppare una versione con GUI, che usi la libreria sviluppata al punto (1), e fornisca all'utente una view 
grafica con caselle input di testo per specificare indirizzo, parola e profondità e un'area di testo di output dove
visualizzare incrementalmente e interattivamente aggiornamenti riguardo al contenuto del report, nonché un pulsante di 
"stop" per fermare il processo.

## NOTA IMPORTANTE

in ogni versione sviluppata, deve essere proposta una soluzione che cerchi di sfruttare al meglio gli aspetti 
specifici della tecnica/approccio usato (Eventi/Event-Loop, Programmazione Reattiva, Virtual Thread), sia in fase di 
design, sia in fase di implementazione prediligere questo punto rispetto alla possibilità di riusare codice e 
strategia risolutiva. 

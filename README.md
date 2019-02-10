# Revolut Backend Test

## Overview
* Account transaction design is taken from <a href="https://www.martinfowler.com/eaaDev/AccountingTransaction.html">Accounting Transaction article by Martin Fowler</a> 
* Supported currencies are EUR, GBP, USD.

## Technology stack
<pre>
Java 8

Gradle

Jooby

Undertow

Jackson

Lombok

Rest-assured

Hamcrest

JUnit
</pre>
## Build
<pre>gradlew build</pre>

## Test
<pre>gradlew test</pre>

## Run
<pre>gradlew joobyRun</pre>


http://localhost:8080/

## Rest API

### Account API

<pre>
GET  /accounts/{id}                           (/AccountApi.get)

GET  /accounts                                (/AccountApi.getAll)

POST /accounts                                (/AccountApi.create)
</pre>

### Transaction API
<pre>
POST /transactions                      (/TransactionApi.transfer)

GET  /transactions/accounts/{id}        (/TransactionApi.getAccountTransactions)
</pre>
# aeondb
AeonDB is an immutable, concurrent, clusterable, embeddable key-value store with transactions and ACID guarantees. AeonDB reads are referentially transparent and allow retrieving any past state of any value. AeonDB stores data as a replicable time-ordered stream of commit events.

AeonDB v0.1 is currently a prototype and does not fufill all of its proposed features. v0.1 implements the following features: 
* Immutable (v0.1)
* Concurrent (v0.1)
* Embeddable (for Scala only) (v0.1)
* Local transactions ACI(-D) (v0.1)
* Time-ordered commit event stream (v0.1)

See the roadmap below for planned feature support:
* Clusterable (v0.2)
* Cluster transactions (v0.2)
* Transactor ACI(+D) (v0.2)
* Multiple key-value stores (v0.3)
* Schema and schema-versioning (v0.3)
* Embeddable (for Scala, Java or Python) (v0.4)
* Stand-alone (v0.5)
* SQL processor (v0.6)
* Datalog processor (v0.7)

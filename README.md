Yahoogroups-archiver
====================

Yahoogroups-archiver was a tool to archive the messages of a Yahoo Group to a
Firebird database. Archiving itself is no longer available. The REST API was
removed by Yahoo on December 14th, 2019, and the related code was eventuall
removed from this project.

Since the Yahoo Groups archive is no longer accessible, its current function is
to build a HTML site with messages previously archived using this tool. The
configuration in this repository is specific to generate the archive site for
the Firebird groups, https://fb-list-archive.lawinegevaar.nl/. 

This tool is offered free of charge under the MIT license. It is offered without
any warranty or support. Use at your own risk.

Requirements
------------

- Java 21
- [Firebird 3 or higher](https://www.firebirdsql.org/)

Upgrade Note
------------

If you used Yahoogroups-archiver before the 2nd of August 2023, you'll need
to migrate your database manually to make it compatible with the switch to 
Flyway for migrations.

Otherwise, attempting to run any of the tools will produce the error

> Exception in thread "main" org.flywaydb.core.api.FlywayException: Found 
> non-empty schema(s) "default" but no schema history table.

To fix this, execute the following (e.g. using ISQL) on your database:

```sql
CREATE TABLE "flyway_schema_history" (
  "installed_rank" INTEGER CONSTRAINT "flyway_schema_history_pk" PRIMARY KEY,
  "version" VARCHAR(50),
  "description" VARCHAR(200) NOT NULL,
  "type" VARCHAR(20) NOT NULL,
  "script" VARCHAR(1000) NOT NULL,
  "checksum" INTEGER,
  "installed_by" VARCHAR(100) NOT NULL,
  "installed_on" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  "execution_time" INTEGER NOT NULL,
  "success" SMALLINT NOT NULL
);
CREATE INDEX "flyway_schema_history_s_idx" ON "flyway_schema_history" ("success");
COMMIT;

INSERT INTO "flyway_schema_history" ("installed_rank","version","description","type","script","checksum","installed_by","installed_on","execution_time","success") VALUES
  (1,'1','initial tables','SQL','V1__initial_tables.sql',1520187526,'SYSDBA','2023-08-02 16:42:47.788',80,1);

DROP TABLE DBVERSION;
COMMIT;
```

Setup
-----

Edit `src/main/resources/database.properties` with the connection details of
your Firebird database server. The tool will automatically create the 
database configured in `db.databaseName` if it does not yet exist.

Building HTML archive
---------------------

If you previously archived using this tool, you can build an HTML archive.

NOTE: I have only ever run this from within an IDE (IntelliJ IDEA), so these
instructions assume sufficient Java knowledge to get things compiled and
running in IntelliJ IDEA or the tool of your choice.

The file `src/main/resources/site.properties` can be used to configure parts of
the HTML generation.

To build a HTML archive of all messages, run

```
nl.lawinegevaar.yahoogroups.archiver.ArchiveBuilderMain --output <output-directory>
```

The output directory must be empty.

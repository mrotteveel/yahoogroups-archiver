Yahoogroups-archiver
====================

Yahoogroups-archiver is a tool to archive the messages of a Yahoo Group to a
Firebird database.

Be aware, this tool started out as an experiment that I abandoned over a year
ago. With the imminent closure of the archive of Yahoo Groups, I have decided to
publish it in the hope that maybe it is useful for someone else as well.

This tool is offered free of charge under the MIT license. It is offered without
any warranty or support. Use at your own risk.

Requirements
------------

- Java 11
- [Firebird 3](https://www.firebirdsql.org/)

Setup
-----

Edit `src/main/resources/database.properties` with the connection details of
your Firebird database server. The tool will automatically create the 
database configured in `db.databaseName` if it does not yet exist.

Archiving
---------

**With the removal of the API on December 14th, 2019, the archiving no longer
works**

NOTE: I have only ever run this from within an IDE (IntelliJ IDEA), so these 
instructions assume sufficient Java knowledge to get things compiled and
running in IntelliJ IDEA or the tool of your choice.

The build by default expects the database to already exist and contain the
expected structure. To compile without that database, comment out or remove the
entire `archivedb` section in the `jooq` section of the `build.gradle`. 

The archiver can archive multiple groups, but you need to archive them
individually. You can update to archive newer messages in existing archives.

### Archiving a group

To archive a group, run 

```
nl.lawinegevaar.yahoogroups.archiver.ScraperMain --group <name-of-group>
```

Be aware that archiving is pretty slow, so for large archives it can take a long
time to complete. If archiving starts to fail after a while, it may mean that
Yahoo has temporarily blocked you. In that case, wait a few hours and restart.
The archiver will pick up where it left off.

If your group is a closed group (in that case, the archiver will throw an
exception), you need to inspect the headers of your browser requests to 
https://groups.yahoo.com/ and copy the `Cookie` value and run the tool with:

```
nl.lawinegevaar.yahoogroups.archiver.ScraperMain --group <name-of-group> --cookie-string "<cookies-value>"
```

### Update archives 

To add new messages to existing archived groups, use:

```
nl.lawinegevaar.yahoogroups.archiver.ScraperMain --update
```

For closed groups, you'll need to add the `--cookie-string` option as well.

For each group, the update will use the last message id archived, and archive
all newer message ids.

When using `--update` combined with `--retry-gaps`, the updater will retry the
gaps in message ids to see if those messages now do exist. In general retrying
the gaps will not find new messages, although my guess is that it might catch
messages that were on hold for moderation in previous runs. 

The update option will not revisit previous message, for example to update the
references between messages.

Building HTML archive
---------------------

The file `src/main/resources/site.properties` can be used to configure parts of
the HTML generation.

To build a HTML archive of all messages, run

```
nl.lawinegevaar.yahoogroups.archiver.ArchiveBuilderMain --output <output-directory>
```

The output directory must be empty.

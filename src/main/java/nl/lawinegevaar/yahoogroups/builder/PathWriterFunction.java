package nl.lawinegevaar.yahoogroups.builder;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;

@FunctionalInterface
interface PathWriterFunction {

    Writer getWriter(Path path) throws IOException;

}

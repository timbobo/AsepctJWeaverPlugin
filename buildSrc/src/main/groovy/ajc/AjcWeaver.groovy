package ajc

import org.aspectj.bridge.IMessage
import org.aspectj.bridge.MessageHandler
import org.aspectj.tools.ajc.Main

import java.util.concurrent.Callable

class AjcWeaver implements Callable {

    /**
     * Specify default source encoding format.
     * Specify custom encoding on a per file basis by suffixing each input source file/folder name with '[encoding]'.
     */
    String encoding
    /**
     * Accept as source bytecode any .class files in the .jar files or directories on Path.
     * The output will include these classes, possibly as woven with any applicable aspects.
     * Path is a single argument containing a list of paths to zip files or directories,
     * delimited by the platform-specific path delimiter.
     */
    ArrayList<File> inPath
    /**
     * Weave binary aspects from jar files and directories on path into all sources.
     * The aspects should have been output by the same version of the compiler.
     * When running the output classes, the run classpath should contain all aspectpath entries.
     * Path, like classpath, is a single argument containing a list of paths to jar files,
     * delimited by the platform- specific classpath delimiter.
     */
    ArrayList<File> aspectPath
    /**
     * Specify where to find user class files.
     * Path is a single argument containing a list of paths to zip files or directories,
     * delimited by the platform-specific path delimiter.
     */
    ArrayList<File> classPath
    /**
     * Override location of VM's bootclasspath for purposes of evaluating types when compiling.
     * Path is a single argument containing a list of paths to zip files or directories,
     * delimited by the platform-specific path delimiter.
     */
    String bootClassPath
    /**
     * -source [1.3|1.4|1.5]
     * Toggle assertions (1.3, 1.4, or 1.5 - default is 1.4).
     * When using -source 1.3, an assert() statement valid under Java 1.4 will result in a compiler error.
     * When using -source 1.4, treat
     */
    String sourceCompatibility
    /**
     * -target [1.1 to 1.5]
     * Specify classfile target setting (1.1 to 1.5, default is 1.2)
     *
     * -1.3
     * Set compliance level to 1.3 This implies -source 1.3 and -target 1.1.
     *
     * -1.4
     * Set compliance level to 1.4 (default) This implies -source 1.4 and -target 1.2.
     *
     * -1.5
     * Set compliance level to 1.5. This implies -source 1.5 and -target 1.5.
     */
    String targetCompatibility
    /**
     * -d Directory
     * Specify where to place generated .class files. If not specified, Directory defaults to the current working dir.
     */
    String outputDir
    /**
     * -outjar output.jar
     * Put output classes in zip file output.jar.
     */
    String outputJar

    AjcWeaver() {

    }

    @Override
    Object call() throws Exception {
        ArrayList<String> args = [
                "-showWeaveInfo",
                "-encoding", encoding,
                "-source", sourceCompatibility,
                "-target", targetCompatibility,
                "-classpath", classPath.join(File.pathSeparator).toString(),
                "-bootclasspath", bootClassPath,
                "-warn:none",
                "-Xlint:ignore"
        ]

        if (!inPath.isEmpty()) {
            args.add("-inpath")
            args.add(inPath.join(File.pathSeparator).toString())
        }

        if (!aspectPath.isEmpty()) {
            args.add("-aspectpath")
            args.add(aspectPath.join(File.pathSeparator).toString())
        }

        if (outputDir != null && !outputDir.isEmpty()) {
            args.add("-d")
            args.add(outputDir)
        }

        if (outputJar != null && outputJar.isEmpty()) {
            args.add("-outjar")
            args.add(outputJar)
        }

        def main = new Main()
        def handler = new MessageHandler(true)

        main.run(args.toArray(), handler)
//        def log = project.logger
        handler.getMessages(null, true).each { IMessage message ->
            switch (message.getKind()) {
                case IMessage.ABORT:
                case IMessage.ERROR:
                case IMessage.FAIL:
//                    log.error message.message, message.thrown
                    break
                case IMessage.WARNING:
                case IMessage.INFO:
//                    log.info message.message, message.thrown
                    break
                case IMessage.DEBUG:
//                    log.debug message.message, message.thrown
                    break
            }
        }
        return null
    }
}
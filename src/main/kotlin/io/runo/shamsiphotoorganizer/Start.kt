package io.runo.shamsiphotoorganizer

import org.apache.commons.cli.*

fun main(args: Array<String>) {
    val inputDir = "in"
    val outputDir = "out"
    val rename = "rename"
    info("\n\n *** New Session  *** \n\n")
    val cliOptions = Options()
    cliOptions.addOption(inputDir, true, "Directory that contains photos (jpg, jpeg) | required")
    cliOptions.addOption(outputDir, true, "Directory to save organized photos | required")
    cliOptions.addOption(rename, false, "Also rename photos while organizing them | optional")
    val commandLineParser: CommandLineParser = DefaultParser()
    try {
        val commandLine = commandLineParser.parse(cliOptions, args)
        if (commandLine.hasOption(inputDir) && commandLine.hasOption(outputDir)) {
            val originPath = commandLine.getOptionValue(inputDir)
            val targetPath = commandLine.getOptionValue(outputDir)
            val shouldRename = commandLine.hasOption(rename)
            info("Input path: $originPath")
            PhotoRenamer(originPath, targetPath, shouldRename)
        } else {
            val helpFormatter = HelpFormatter()
            helpFormatter.printHelp("Shamsi Photo Organizer", cliOptions, true)
        }
    } catch (e: ParseException) {
        error(e)
    }
}
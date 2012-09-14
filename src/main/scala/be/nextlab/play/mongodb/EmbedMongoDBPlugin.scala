package be.nextlab.play.mongodb

import de.flapdoodle.embed.mongo.MongodProcess
import de.flapdoodle.embed.mongo.MongodStarter
import de.flapdoodle.embed.mongo.config.MongodConfig
import de.flapdoodle.embed.mongo.config.RuntimeConfig
import de.flapdoodle.embed.process.config.io.ProcessOutput;
import de.flapdoodle.embed.process.io.Processors;
import de.flapdoodle.embed.mongo.distribution.Version
import de.flapdoodle.embed.process.runtime.Network

import java.io.File

import play.api._

class EmbedMongoDBPlugin(app: Application) extends Plugin {

    lazy val embed = app.configuration.getConfig("embed.mongodb").getOrElse(Configuration.empty)


    lazy val start = embed.getBoolean("start").getOrElse(false)
    lazy val runtimeConfig = {
        val rc = new RuntimeConfig()

        val mongodOutput = Processors.named("[mongod>]", FileStreamProcessor(File.createTempFile("mongod", "log")))
        val mongodError = FileStreamProcessor(File.createTempFile("mongod-error", "log"))
        val commandsOutput = Processors.namedConsole("[console>]");

        rc.setProcessOutput(new ProcessOutput(mongodOutput, mongodError, commandsOutput))

        rc
    }
    lazy val runtime = MongodStarter.getInstance(runtimeConfig)
    lazy val version = embed.getString("version").map{v => Enum.valueOf(classOf[Version], v)}.getOrElse(Version.V2_2_0)
    lazy val port = embed.getInt("port").getOrElse(27017)
    lazy val mongodExe = runtime.prepare(new MongodConfig(version, port, Network.localhostIsIPv6()))
    lazy val mongod: MongodProcess = mongodExe.start()

    override def enabled = !embed.subKeys.isEmpty



    override def onStart() {
        if (app.mode != Mode.Prod && start) {
          Logger.debug("Starting MongoDB")
            mongod
        } else {
            Logger.warn("MongoDB not started in Prod mode or configured as is ("+start+")!")
        }
    }

    override def onStop() {
        if (app.mode != Mode.Prod && start) {
            Logger.debug("Stoping MongoDB")
            mongod.stop()
            mongodExe.cleanup()
        } else {
            Logger.warn("MongoDB not stoped in Prod mode or configured as is ("+start+")!")
        }
    }

}
package be.nextlab.play.mongodb

import de.flapdoodle.embedmongo._
import config.MongodConfig
import distribution.Version

import play.api._
import play.api.mvc._
import play.api.Play.current

class EmbedMongoDBPlugin(app: Application) extends Plugin {

    lazy val embed = app.configuration.getConfig("embed.mongodb").getOrElse(Configuration.empty)


    lazy val mongoDBRuntime: MongoDBRuntime = MongoDBRuntime.getDefaultInstance
    lazy val start = embed.getBoolean("start").getOrElse(false)
    lazy val version = embed.getString("version").map{v => Enum.valueOf(classOf[Version], v)}.getOrElse(Version.V2_1_1)
    lazy val mongodExe: MongodExecutable = mongoDBRuntime.prepare(new MongodConfig(version, embed.getInt("port").getOrElse(27017), true))
    lazy val mongod: MongodProcess = mongodExe.start()


    override def enabled = !embed.subKeys.isEmpty

    override def onStart() {
        if (app.mode != Mode.Prod && start) { 
            mongod
        } else {
            Logger.warn("MongoDB not started in Prod mode or configured as is ("+start+")!")
        }
    }

    override def onStop() {
        if (app.mode != Mode.Prod && start) { 
            mongod.stop()
            mongodExe.cleanup()
        } else {
            Logger.warn("MongoDB not stoped in Prod mode or configured as is ("+start+")!")
        }
    }

}
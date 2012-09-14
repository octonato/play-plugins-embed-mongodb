package be.nextlab.play.mongodb

import de.flapdoodle.embed.process.io.IStreamProcessor
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * copied from embed mongo project's manual
 */

case class FileStreamProcessor(file:File) extends IStreamProcessor {
    val outputStream = new FileOutputStream(file)

//    private FileOutputStream outputStream;

//    public FileStreamProcessor(File file) throws FileNotFoundException {
//        outputStream = new FileOutputStream(file);
//    }
    
    override def process(block:String) {
        try {
            outputStream.write(block.getBytes())
        } catch  {
            case e:IOException => e.printStackTrace()
        }
    }

    override def onProcessed() {
        try {
            outputStream.close()
        } catch {
            case e:IOException => e.printStackTrace()
        }
    }   
}

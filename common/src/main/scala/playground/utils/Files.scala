package playground.utils

import java.io.FileWriter

object Files {

  def appendFile(path: String, content: String): Unit = {
    val fw = new FileWriter(path, true)
    try {
      fw.write(content)
    }
    finally fw.close()
  }

}

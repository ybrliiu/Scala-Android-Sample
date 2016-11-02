package jp.ne.liiu.sample

import android.app.Activity
import android.app.AlertDialog
import android.content.{Context, Intent}
import android.os.Bundle
import android.net.Uri
import android.view.View
import android.view.View.OnClickListener
import android.widget.{Button, Toast, EditText}
import android.text.SpannableStringBuilder

import java.io.{
  FileInputStream,
  FileOutputStream,
  BufferedReader,
  BufferedWriter,
  InputStreamReader,
  OutputStreamWriter,
  IOException,
  FileNotFoundException
}

import scala.collection.Iterator

class TextEditer extends Activity with TypedFindView {

  lazy val textView  = this.findView(TR.explain)
  lazy val textField = this.findView(TR.input)
  lazy val saveButton = this.findViewById(R.id.save)
  lazy val loadButton = this.findViewById(R.id.load)
  lazy val intent = new Intent(Intent.ACTION_OPEN_DOCUMENT)
  intent.setType("*/*")

  val OPEN_DOCUMENT_REQUEST = 1

  override def onCreate(savedInstanceState: Bundle) = {
    super.onCreate(savedInstanceState)
    this.setContentView(R.layout.text_editer)

    loadButton.setOnClickListener(new View.OnClickListener {
      override def onClick(v: View) = {
        startActivityForResult(intent, OPEN_DOCUMENT_REQUEST)
        textView.setText("よみこみかんりょー")
      }
    })
    saveButton.setOnClickListener(new View.OnClickListener {
      override def onClick(v: View) = {
        saveFile(intent.getData, textField.getText.toString)
        textView.setText("ほぞんしますた！！")
      }
    })
  }

  override def onActivityResult(requestCode: Int, resultCode: Int, data: Intent) = {
    if (requestCode == OPEN_DOCUMENT_REQUEST) {
      // null pointer exception 回避
      if (data == null) {
        None
      } else {
        val uri = data.getData()
        intent.setData(uri)
        textField.setText(loadFile(uri))
      }
    }
  }

  def saveFile(uri: Uri, text: String) = {
    try {
      val out = getContentResolver.openOutputStream(uri)
      var writer = new BufferedWriter(new OutputStreamWriter(out))
      writer.write(text)
      writer.newLine()
      writer.close()
    }
  }

  def loadFile(uri: Uri): String = {
    try {
      val in = getContentResolver.openInputStream(uri)
      val reader = new BufferedReader(new InputStreamReader(in))
      val lines = Iterator.continually(reader.readLine).takeWhile(_ != null).mkString("", "\n", "")
      reader.close
      lines
    } catch {
      case _ => "ファイルを読み込めませんでした"
    }
  }

/*
  def readFile(fileName: String): String = {
    val stream = openFileInput(fileName)
    val bytes = new Array[Byte](stream.available())
    stream.read(bytes)
    new String(bytes)
  }

  def saveFile(fileName: String, text: String) = {
    val result = try {
      openFileOutput(fileName, Context.MODE_PRIVATE).write(text.getBytes())
      "保存しますたあ"
    } catch {
      case e: IOException           => "ファイルの保存に失敗しました"
      case e: FileNotFoundException => "ファイルがないですよー！"
    }
    textView.setText(result)
  }
*/

}

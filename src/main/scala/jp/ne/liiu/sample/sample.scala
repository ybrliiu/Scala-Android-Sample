package jp.ne.liiu.sample

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.widget.{Button, Toast, EditText}
import android.text.SpannableStringBuilder

import java.io._
import java.net._
import java.util.concurrent.TimeUnit

import org.apache.commons.io._

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.{Success,Failure}
import scala.concurrent.ExecutionContext.Implicits.global

import akka.actor.{Actor, ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout
import akka.routing.RoundRobinRouter

class URLActor extends Actor {

  override def preStart = {println("actor is started.")  }

  def receive = {
    case url: String => {
      val result = try {
        val stream = new URL(url).openStream()
        val result = IOUtils.toString(stream)
        IOUtils.closeQuietly(stream)
        result
      } catch {
        case _ => "取得失敗。URLが間違っているかインターネットにつながっていません。"
      }
      sender ! result
    }
    case "stop" => {
      context.stop(self)
    }
    case _     => {
      println("HELOO")
    }
  }

}

class MainActivity extends Activity with TypedFindView with OnClickListener {

  lazy val textview  = this.findView(TR.text)
  lazy val textField = this.findView(TR.url)
  lazy val addButton = this.findViewById(R.id.button1)
  lazy val switchButton = this.findViewById(R.id.switch_)
  val requestCode = 1;
  var number = 0;

  lazy val thread = new NetThread()
  lazy val system = ActorSystem("Sample")
  implicit val timeout = Timeout(50000, TimeUnit.MILLISECONDS)

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.main)
    textview.setText(s"Hello world, from ScalaSample $number")
    addButton.setOnClickListener(this)
    switchButton.setOnClickListener(this)
  }

  override def onClick(v: View): Unit = {
    v.getId() match {
      case R.id.button1 => this.getHTML
      case R.id.switch_ => this.switchTextEditer
    }
  }

  def getHTML = {
    number += 1;
    textview.setText(s"$number")

    val alert = new AlertDialog.Builder(this);
    alert.setTitle("取得結果")

    // actor, thread などで別threadの方でhttpから情報引っ張ってこないと例外発生
    val actor = system.actorOf(Props[URLActor].withRouter(RoundRobinRouter(1)))
    val url: String = textField.getText().asInstanceOf[SpannableStringBuilder].toString()
    val reply = actor ? url
    val resultActor = Await.result(reply, timeout.duration).asInstanceOf[String]
    alert.setMessage(resultActor)
    actor ! "stop"
    
    alert.setPositiveButton("OK",
      new DialogInterface.OnClickListener() {
        override def onClick(dialog: DialogInterface, which: Int) {
        }
      }
    )
    alert.setCancelable(true)

    val alertDialog = alert.create()
    alertDialog.show()
  }

  def switchTextEditer = {
    startActivityForResult (new Intent(this, classOf[TextEditer]), requestCode)
  }

}

/* actor 以前に使っていた thread版  */
class NetThread extends Thread {

  lazy val in = new URL("http://lunadraco.sakura.ne.jp/sinoa/script/sinoa.cgi/top/")
  var result: String = ""
  var isGeted = false

  override def run() = {
    val stream = in.openStream()
    val result = IOUtils.toString(stream)
    isGeted = true
    IOUtils.closeQuietly(stream)

    this.synchronized {
      this.isGeted = true
      this.result = result
      this.notifyAll()
    }
  }

  def getResult(): String = {
    this.synchronized {
      while (!this.isGeted) {
        this.wait()
      }
      return this.result
    }
  }

}


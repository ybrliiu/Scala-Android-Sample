androidBuild

name := "Sample Android App by Scala"

platformTarget := "android-22"

javacOptions in Compile ++= "-source" :: "1.7" :: "-target" :: "1.7" :: Nil

libraryDependencies += "org.apache.commons" % "commons-io" % "1.3.2"
// %% はscalaのバージョン番号を付与*ここでは_2.10
libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.3.15"

libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.4" % "test"
libraryDependencies += "com.android.support" % "support-v4" % "23.1.1"
libraryDependencies += "com.android.support" % "support-v13" % "23.1.1"

// androidでscala云々... これがないとakka使えません
proguardOptions in Android ++= Seq(
    "-ignorewarnings",
    "-keep class scala.Dynamic",
    "-keep class com.android.support.**",
    "-keep class com.typesafe.**",
    "-keep class akka.**",
    "-keep class scala.collection.immutable.StringLike { *; }",
    "-keepclasseswithmembers class * { public <init>(java.lang.String, akka.actor.ActorSystem$Settings, akka.event.EventStream, akka.actor.Scheduler, akka.actor.DynamicAccess); }",
    "-keepclasseswithmembers class * { public <init>(akka.actor.ExtendedActorSystem); }",
    "-keep class scala.collection.SeqLike { public protected *; }",
    "-keep class scala.concurrent.**",
    "-keep class scala.reflect.ScalaSignature.**",

    "-keep class akka.actor.LightArrayRevolverScheduler { *; }",
    "-keep class akka.actor.LocalActorRefProvider { *; }",
    "-keep class akka.actor.CreatorFunctionConsumer { *; }",
    "-keep class akka.actor.TypedCreatorFunctionConsumer { *; }",
    "-keep class akka.dispatch.BoundedDequeBasedMessageQueueSemantics { *; }",
    "-keep class akka.dispatch.UnboundedMessageQueueSemantics { *; }",
    "-keep class akka.dispatch.UnboundedDequeBasedMessageQueueSemantics { *; }",
    "-keep class akka.dispatch.DequeBasedMessageQueueSemantics { *; }",
    "-keep class akka.actor.LocalActorRefProvider$Guardian { *; }",
    "-keep class akka.actor.LocalActorRefProvider$SystemGuardian { *; }",
    "-keep class akka.dispatch.UnboundedMailbox { *; }",
    "-keep class akka.actor.DefaultSupervisorStrategy { *; }",
    "-keep class akka.event.Logging$LogExt { *; }"
)

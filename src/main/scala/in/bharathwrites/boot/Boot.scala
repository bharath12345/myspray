package in.bharathwrites.boot

import akka.event.slf4j.SLF4JLogging

object Boot extends App with BootedCore with CoreActors with Api with Web with SLF4JLogging
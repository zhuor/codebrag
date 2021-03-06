package com.softwaremill.codebrag.dao.sql

import com.softwaremill.codebrag.dao.DaoConfig
import com.typesafe.config.ConfigFactory

object H2BrowserConsole extends App {
  val config = new DaoConfig {
    def rootConfig = ConfigFactory.load()
  }

  new Thread(new Runnable {
    def run() = new org.h2.tools.Console().runTool("-url", SQLDatabase.connectionString(config))
  }).start()

  println("The console is now running in the background.")
}

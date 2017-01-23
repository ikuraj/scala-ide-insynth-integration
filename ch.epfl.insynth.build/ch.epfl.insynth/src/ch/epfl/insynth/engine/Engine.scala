package ch.epfl.insynth.engine

import ch.epfl.insynth.util.TimeOut
import ch.epfl.insynth.scheduler.Scheduler
import ch.epfl.insynth.env.Requests
import ch.epfl.insynth.env.InitialEnvironmentBuilder
import ch.epfl.insynth.env.Query
import ch.epfl.insynth.trees.Type
import ch.epfl.insynth.env.QueryBuilder
import ch.epfl.scala.trees.ScalaType

class Engine(builder:InitialEnvironmentBuilder, queryType:ScalaType, scheduler:Scheduler, timeout:TimeOut) {
  assert(builder != null && queryType != null && scheduler != null && timeout != null)
  
  def run() = {

    //It is obvious that we do not activate all decls properlly
    val queryBuilder = new QueryBuilder(queryType)
    
    val query = queryBuilder.getQuery
    
    //Adding query decl to the env
    builder.addDeclaration(query.getDeclaration)
    
    //Create request pool
    val requests = new Requests()
    
    //Register Scheduler as a listener of the request pool
    requests.registerListener(scheduler)
    
    //Initiate first request, that will find query decl and put it into Scheduler
    requests.addSender(query.getReturnType, builder.produceEnvirionment, query.getSender)

    timeout.start()
    while(!timeout.hasExpired() && !scheduler.hasFinished()){
      var ta = scheduler.next()
      ta.processRequests(requests)
    }
    
    query.getSolution
  }
}


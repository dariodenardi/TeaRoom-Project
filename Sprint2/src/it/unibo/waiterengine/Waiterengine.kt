/* Generated by AN DISI Unibo */ 
package it.unibo.waiterengine

import it.unibo.kactor.*
import alice.tuprolog.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
	
class Waiterengine ( name: String, scope: CoroutineScope  ) : ActorBasicFsm( name, scope ){

	override fun getInitialState() : String{
		return "s0"
	}
	@kotlinx.coroutines.ObsoleteCoroutinesApi
	@kotlinx.coroutines.ExperimentalCoroutinesApi			
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		 
					var StepTime = 430L
					val BackTime     = 2 * StepTime / 3
					
					val mapRoom  = "teaRoomExplored"
					var XPoint = "0"
					var YPoint = "0"
					
					var CmdToPerform = ""  
					
					var TableToStop = 0
		return { //this:ActionBasciFsm
				state("s0") { //this:State
					action { //it:State
						println("waiterengine 		|| START")
						updateResourceRep( "startState"  
						)
						itunibo.planner.plannerUtil.initAI(  )
						itunibo.planner.plannerUtil.loadRoomMap( mapRoom  )
						itunibo.planner.plannerUtil.showCurrentRobotState(  )
						forward("engineReady", "engineReady(P)" ,"waitermind" ) 
					}
					 transition( edgeName="goto",targetState="waitCmd", cond=doswitch() )
				}	 
				state("waitCmd") { //this:State
					action { //it:State
						println("waiterengine 		|| waitCmd")
						updateResourceRep( "waitCmd"  
						)
					}
					 transition(edgeName="t073",targetState="planPath",cond=whenDispatch("moveTo"))
					transition(edgeName="t074",targetState="cleanTable",cond=whenDispatch("clean"))
					transition(edgeName="t075",targetState="waitCmd",cond=whenDispatch("stopEngineMove"))
				}	 
				state("planPath") { //this:State
					action { //it:State
						println("waiterengine 		|| planPath")
						updateResourceRep( "planPath"  
						)
						if( checkMsgContent( Term.createTerm("moveTo(X,Y)"), Term.createTerm("moveTo(X,Y)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 XPoint = payloadArg(0)
											   YPoint = payloadArg(1)			  
						}
						println("moveTo ($XPoint,$YPoint)")
						itunibo.planner.plannerUtil.planForGoal( "$XPoint", "$YPoint"  )
					}
					 transition( edgeName="goto",targetState="readStep", cond=doswitch() )
				}	 
				state("readStep") { //this:State
					action { //it:State
						  CmdToPerform = itunibo.planner.plannerUtil.getNextPlannedMove()  
					}
					 transition( edgeName="goto",targetState="execStep", cond=doswitchGuarded({ CmdToPerform == "w" 
					}) )
					transition( edgeName="goto",targetState="execMove", cond=doswitchGuarded({! ( CmdToPerform == "w" 
					) }) )
				}	 
				state("checkStopEngine") { //this:State
					action { //it:State
						stateTimer = TimerActor("timer_checkStopEngine", 
							scope, context!!, "local_tout_waiterengine_checkStopEngine", 100.toLong() )
					}
					 transition(edgeName="t076",targetState="readStep",cond=whenTimeout("local_tout_waiterengine_checkStopEngine"))   
					transition(edgeName="t077",targetState="stopEngine",cond=whenDispatch("stopEngineMove"))
				}	 
				state("stopEngine") { //this:State
					action { //it:State
						itunibo.planner.plannerUtil.resetActions(  )
					}
					 transition( edgeName="goto",targetState="waitCmd", cond=doswitch() )
				}	 
				state("execMove") { //this:State
					action { //it:State
						forward("cmd", "cmd($CmdToPerform)" ,"basicrobot" ) 
						delay(200) 
					}
					 transition( edgeName="goto",targetState="updateMap", cond=doswitch() )
				}	 
				state("endPath") { //this:State
					action { //it:State
						println("waiterengine 		|| endPath")
						updateResourceRep( "endPath"  
						)
						println("done moveTo($XPoint,$YPoint)")
						forward("done", "done($XPoint,$YPoint)" ,"waitermind" ) 
					}
					 transition( edgeName="goto",targetState="waitCmd", cond=doswitch() )
				}	 
				state("execStep") { //this:State
					action { //it:State
						request("step", "step($StepTime)" ,"basicrobot" )  
					}
					 transition(edgeName="t178",targetState="updateMap",cond=whenReply("stepdone"))
					transition(edgeName="t179",targetState="errorHandler",cond=whenReply("stepfail"))
				}	 
				state("updateMap") { //this:State
					action { //it:State
						updateResourceRep( itunibo.planner.plannerUtil.getMapOneLine()  
						)
						itunibo.planner.plannerUtil.updateMap( "$CmdToPerform"  )
					}
					 transition( edgeName="goto",targetState="checkStopEngine", cond=doswitchGuarded({ CmdToPerform.length > 0  
					}) )
					transition( edgeName="goto",targetState="endPath", cond=doswitchGuarded({! ( CmdToPerform.length > 0  
					) }) )
				}	 
				state("errorHandler") { //this:State
					action { //it:State
						println("waiterengine 		|| errorHandler")
						if( checkMsgContent( Term.createTerm("stepfail(DURATION,CAUSE)"), Term.createTerm("stepfail(DURATION,CAUSE)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 
												val D = payloadArg(0).toLong()  
												val Dt = Math.abs(StepTime-D)
												val BackT = D/4 
								if(  D > BackTime  
								 ){forward("cmd", "cmd(s)" ,"basicrobot" ) 
								delay(BackT)
								forward("cmd", "cmd(h)" ,"basicrobot" ) 
								}
						}
					}
					 transition( edgeName="goto",targetState="updateMap", cond=doswitch() )
				}	 
				state("cleanTable") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("clean(R,TIMER)"), Term.createTerm("clean(R,TIMER)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								if(  payloadArg(0) == "1"  
								 ){println("waiterengine 		|| clearing")
								forward("startTimerCleaning", "startTimerCleaning(${payloadArg(1)})" ,"timercleaning" ) 
								}
								if(  payloadArg(0) == "2"  
								 ){println("waiterengine 		|| sanitizing")
								forward("startTimerCleaning", "startTimerCleaning(${payloadArg(1)})" ,"timercleaning" ) 
								}
								if(  payloadArg(0) == "3"  
								 ){println("waiterengine 		|| cleaning")
								forward("startTimerCleaning", "startTimerCleaning(${payloadArg(1)})" ,"timercleaning" ) 
								}
						}
					}
					 transition(edgeName="t080",targetState="informStateAboutEndCleaning",cond=whenDispatch("timerFinishedCorrectly"))
					transition(edgeName="t081",targetState="stopTimer",cond=whenDispatch("stopCleaningEngine"))
				}	 
				state("informStateAboutEndCleaning") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("stopTimerCleaningReply(TIMERDONE)"), Term.createTerm("stopTimerCleaningReply(TIMERDONE)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								forward("setTimerTableStopped", "setTimerTableStopped($TableToStop,${payloadArg(0)})" ,"tearoomglobalstate" ) 
								 TableToStop  = 0  
						}
						if( checkMsgContent( Term.createTerm("timerFinishedCorrectly(P)"), Term.createTerm("timerFinishedCorrectly(P)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								forward("doneCleanRun", "doneCleanRun(P)" ,"waitermind" ) 
						}
					}
					 transition( edgeName="goto",targetState="waitCmd", cond=doswitch() )
				}	 
				state("stopTimer") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("stopCleaningEngine(TAVOLO)"), Term.createTerm("stopCleaningEngine(TAVOLO)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 TableToStop = payloadArg(0).toInt()  
						}
						request("stopTimerCleaningReq", "stopTimerCleaningReq(P)" ,"timercleaning" )  
					}
					 transition(edgeName="t082",targetState="informStateAboutEndCleaning",cond=whenReply("stopTimerCleaningReply"))
				}	 
			}
		}
}

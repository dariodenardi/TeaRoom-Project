/* Generated by AN DISI Unibo */ 
package it.unibo.waitermind

import it.unibo.kactor.*
import alice.tuprolog.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
	
class Waitermind ( name: String, scope: CoroutineScope  ) : ActorBasicFsm( name, scope ){

	override fun getInitialState() : String{
		return "startState"
	}
	@kotlinx.coroutines.ObsoleteCoroutinesApi
	@kotlinx.coroutines.ExperimentalCoroutinesApi			
	override fun getBody() : (ActorBasicFsm.() -> Unit){
				
				val DoClear = 1
				val DoSanitize = 2
				val DoClean = 3
				
				val ClearTime = 6000L
				val SanitizeTime = 5000L
				val CleanTime = 5000L
				
				val CollectTime = 4000L
				val DelayTakeDrink = 2000L
				val DelayServeDrink = 2000L
				val DelayTakeClient = 2000L
						 
		
				var CleaningON = false
				
				var TableToClean = 0
				var TableStateToClean = ""
				var TableTimerDone = 0L
				
				var TableToOccupy = 0
				var IdClientOccupy = 0
				
				var TableForOrder = ""
				var IdForOrder = 0
				
				var TableForCollect = ""
				
				var TimerTable1 = 0 
				var TimerTable2 = 0
				
				var MoveX = 0
				var MoveY = 0
		
		return { //this:ActionBasciFsm
				state("startState") { //this:State
					action { //it:State
						println("waitermind 		|| START")
						updateResourceRep( "startState"  
						)
						solve("consult('tearoomstate.pl')","") //set resVar	
					}
					 transition(edgeName="t00",targetState="checkTableToClean",cond=whenDispatch("engineReady"))
				}	 
				state("checkQueue") { //this:State
					action { //it:State
						println("waitermind		|| checkingQueue")
						stateTimer = TimerActor("timer_checkQueue", 
							scope, context!!, "local_tout_waitermind_checkQueue", 100.toLong() )
					}
					 transition(edgeName="t121",targetState="checkTableToClean",cond=whenTimeout("local_tout_waitermind_checkQueue"))   
					transition(edgeName="t122",targetState="reachTableCollect",cond=whenDispatch("clientPaymentReady"))
					transition(edgeName="t123",targetState="reachTableOrder",cond=whenDispatch("clientOrderReady"))
					transition(edgeName="t124",targetState="reachBar",cond=whenDispatch("barmanOrderReady"))
					transition(edgeName="t125",targetState="reachTableKick",cond=whenDispatch("maxStayTimerExpired"))
					transition(edgeName="t126",targetState="acceptorinform",cond=whenRequest("smartbellEntryRequest"))
				}	 
				state("checkTableToClean") { //this:State
					action { //it:State
						println("waitermind		|| checkTableToClean")
						request("getTableToCleanReq", "getTableToCleanReq(P)" ,"tearoomglobalstate" )  
					}
					 transition(edgeName="t17",targetState="analizeReplyTableToClean",cond=whenReply("getTableToCleanReply"))
				}	 
				state("analizeReplyTableToClean") { //this:State
					action { //it:State
						println("waitermind		|| analizeReplyTableToClean")
						if( checkMsgContent( Term.createTerm("getTableToCleanReply(N,STATO,TIMERDONE)"), Term.createTerm("getTableToCleanReply(N,STATO,TIMERDONE)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 
												TableToClean =  payloadArg(0).toInt()
												TableStateToClean = payloadArg(1)
												TableTimerDone = payloadArg(2).toLong()
											
						}
					}
					 transition( edgeName="goto",targetState="reachHome", cond=doswitchGuarded({ TableToClean == 0  
					}) )
					transition( edgeName="goto",targetState="reachTableClean", cond=doswitchGuarded({! ( TableToClean == 0  
					) }) )
				}	 
				state("rest") { //this:State
					action { //it:State
						println("waitermind 		|| rest")
						updateResourceRep( "rest" 
						)
						forward("setWaiterState", "setWaiterState(rest)" ,"tearoomglobalstate" ) 
					}
					 transition(edgeName="t18",targetState="acceptorinform",cond=whenRequest("smartbellEntryRequest"))
					transition(edgeName="t19",targetState="reachTableOrder",cond=whenDispatch("clientOrderReady"))
					transition(edgeName="t110",targetState="reachBar",cond=whenDispatch("barmanOrderReady"))
					transition(edgeName="t111",targetState="endState",cond=whenDispatch("end"))
					transition(edgeName="t112",targetState="reachTableCollect",cond=whenDispatch("clientPaymentReady"))
					transition(edgeName="t113",targetState="reachTableKick",cond=whenDispatch("maxStayTimerExpired"))
				}	 
				state("reachHome") { //this:State
					action { //it:State
						println("waitermind 		|| reachHome")
						updateResourceRep( "reachHome"  
						)
						forward("setWaiterState", "setWaiterState(reachHome)" ,"tearoomglobalstate" ) 
						solve("pos(home,X,Y)","") //set resVar	
						if( currentSolution.isSuccess() ) { 
											MoveX = getCurSol("X").toString().toInt()
						                	MoveY = getCurSol("Y").toString().toInt()
						forward("moveTo", "moveTo($MoveX,$MoveY)" ,"waiterengine" ) 
						}
						else
						{}
					}
					 transition(edgeName="t214",targetState="rest",cond=whenDispatch("done"))
					transition(edgeName="t215",targetState="reachTableOrder",cond=whenDispatch("clientOrderReady"))
					transition(edgeName="t216",targetState="reachTableCollect",cond=whenDispatch("clientPaymentReady"))
					transition(edgeName="t217",targetState="reachBar",cond=whenDispatch("barmanOrderReady"))
					transition(edgeName="t218",targetState="reachTableKick",cond=whenDispatch("maxStayTimerExpired"))
					transition(edgeName="t219",targetState="acceptorinform",cond=whenRequest("smartbellEntryRequest"))
				}	 
				state("acceptorinform") { //this:State
					action { //it:State
						forward("stopEngineMove", "stopEngineMove(P)" ,"waiterengine" ) 
						if( CleaningON == true  
						 ){forward("stopCleaningEngine", "stopCleaningEngine($TableToClean)" ,"waiterengine" ) 
						 
										CleaningON = false 
										TableToClean = 0
						}
						println("waitermind 		|| accept or inform")
						request("getFreeCleanTableReq", "getFreeCleanTableReq(P)" ,"tearoomglobalstate" )  
						if( checkMsgContent( Term.createTerm("smartbellEntryRequest(ID)"), Term.createTerm("smartbellEntryRequest(ID)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								println("waitermind 		|| Ricevuta richiesta da ID: ${ payloadArg(0) }")
								IdClientOccupy = payloadArg(0).toInt() 
						}
					}
					 transition(edgeName="t120",targetState="analizeacceptorinform",cond=whenReply("getFreeCleanTableReply"))
				}	 
				state("analizeacceptorinform") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("getFreeCleanTableReply(N)"), Term.createTerm("getFreeCleanTableReply(N)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								
												TableToOccupy = payloadArg(0).toInt() 	
						}
					}
					 transition( edgeName="goto",targetState="inform", cond=doswitchGuarded({TableToOccupy == 0 
					}) )
					transition( edgeName="goto",targetState="accept", cond=doswitchGuarded({! (TableToOccupy == 0 
					) }) )
				}	 
				state("inform") { //this:State
					action { //it:State
						IdClientOccupy = 0 
						request("getTimerForInformReq", "getTimerForInformReq(P)" ,"tearoomglobalstate" )  
					}
					 transition(edgeName="t021",targetState="analizeTimeForInform",cond=whenReply("getTimerForInformReply"))
				}	 
				state("analizeTimeForInform") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("getTimerForInformReply(TIMER)"), Term.createTerm("getTimerForInformReply(TIMER)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								answer("smartbellEntryRequest", "smartbellEntryReply", "smartbellEntryReply(inform,${payloadArg(0)})"   )  
								updateResourceRep("inform ${payloadArg(0)}" 
								)
						}
					}
					 transition( edgeName="goto",targetState="checkQueue", cond=doswitch() )
				}	 
				state("accept") { //this:State
					action { //it:State
						println("waitermind 		|| accept")
						updateResourceRep( "accept"  
						)
						answer("smartbellEntryRequest", "smartbellEntryReply", "smartbellEntryReply(accept,$IdClientOccupy)"   )  
					}
					 transition( edgeName="goto",targetState="reachDoor", cond=doswitch() )
				}	 
				state("reachDoor") { //this:State
					action { //it:State
						println("waitermind 		|| reachDoor")
						updateResourceRep( "reachDoor"  
						)
						forward("setWaiterState", "setWaiterState(reachDoor)" ,"tearoomglobalstate" ) 
						solve("pos(entrance,X,Y)","") //set resVar	
						if( currentSolution.isSuccess() ) {
											MoveX = getCurSol("X").toString().toInt()
						                	MoveY = getCurSol("Y").toString().toInt()
						forward("moveTo", "moveTo($MoveX,$MoveY)" ,"waiterengine" ) 
						}
						else
						{}
					}
					 transition(edgeName="t322",targetState="convoyTable",cond=whenDispatch("done"))
				}	 
				state("convoyTable") { //this:State
					action { //it:State
						updateResourceRep(""+itunibo.planner.plannerUtil.getPosX()+","+itunibo.planner.plannerUtil.getPosY() 
						)
						println("waitermind 		|| wait Enter - pos-> EntranceDoor || (${itunibo.planner.plannerUtil.getPosX()},${itunibo.planner.plannerUtil.getPosY()})")
						 readLine()  
						println("waitermind 		|| convoyTable")
						updateResourceRep( "convoyTable" 
						)
						forward("setWaiterState", "setWaiterState(convoyTable)" ,"tearoomglobalstate" ) 
						delay(DelayTakeClient)
						forward("occupyTable", "occupyTable($TableToOccupy,$IdClientOccupy)" ,"tearoomglobalstate" ) 
						solve("pos('table$TableToOccupy',X,Y)","") //set resVar	
						if( currentSolution.isSuccess() ) {
												MoveX = getCurSol("X").toString().toInt()
							                	MoveY = getCurSol("Y").toString().toInt()
						forward("moveTo", "moveTo($MoveX,$MoveY)" ,"waiterengine" ) 
						}
						else
						{}
					}
					 transition(edgeName="t423",targetState="startTimerTable",cond=whenDispatch("done"))
				}	 
				state("startTimerTable") { //this:State
					action { //it:State
						forward("startTimer", "startTimer($TableToOccupy)" ,"maxstaytime" ) 
					}
					 transition( edgeName="goto",targetState="checkQueue", cond=doswitch() )
				}	 
				state("reachTableOrder") { //this:State
					action { //it:State
						forward("stopEngineMove", "stopEngineMove(P)" ,"waiterengine" ) 
						if( CleaningON == true  
						 ){forward("stopCleaningEngine", "stopCleaningEngine($TableToClean)" ,"waiterengine" ) 
						
										CleaningON = false 
										TableToClean = 0
						}
						println("waitermind 		|| reachTableOrder")
						updateResourceRep( "reachTableOrder" 
						)
						forward("setWaiterState", "setWaiterState(reachTableOrder)" ,"tearoomglobalstate" ) 
						if( checkMsgContent( Term.createTerm("clientOrderReady(ID)"), Term.createTerm("clientOrderReady(ID)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								println("waitermind 		|| client pronto per ordinare ID : ${payloadArg(0)}")
								
												IdForOrder = payloadArg(0).toInt()
						}
						request("getTableFromIdReq", "getTableFromIdReq($IdForOrder)" ,"tearoomglobalstate" )  
					}
					 transition(edgeName="t524",targetState="checkClientPresentInRoomOrder",cond=whenReply("getTableFromIdReply"))
				}	 
				state("checkClientPresentInRoomOrder") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("getTableFromIdReply(N)"), Term.createTerm("getTableFromIdReply(N)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								
												TableForOrder = payloadArg(0)	
						}
						if(  TableForOrder == "0" 
						 ){ IdForOrder = 0  
						}
					}
					 transition( edgeName="goto",targetState="checkQueue", cond=doswitchGuarded({ TableForOrder == "0" 
					}) )
					transition( edgeName="goto",targetState="moveReachTableOrder", cond=doswitchGuarded({! ( TableForOrder == "0" 
					) }) )
				}	 
				state("moveReachTableOrder") { //this:State
					action { //it:State
						forward("stopTimer", "stopTimer($TableForOrder)" ,"maxstaytime" ) 
						solve("pos('table$TableForOrder',X,Y)","") //set resVar	
						if( currentSolution.isSuccess() ) {
											MoveX = getCurSol("X").toString().toInt()
						                	MoveY = getCurSol("Y").toString().toInt()
						forward("moveTo", "moveTo($MoveX,$MoveY)" ,"waiterengine" ) 
						}
						else
						{}
					}
					 transition(edgeName="t525",targetState="waitOrderClient",cond=whenDispatch("done"))
				}	 
				state("waitOrderClient") { //this:State
					action { //it:State
						updateResourceRep(""+itunibo.planner.plannerUtil.getPosX()+","+itunibo.planner.plannerUtil.getPosY() 
						)
						println("waitermind 		|| wait Enter - pos-> Tavolo || (${itunibo.planner.plannerUtil.getPosX()},${itunibo.planner.plannerUtil.getPosY()})")
						 readLine()  
						println("waitermind 		|| waitOrderClient")
						updateResourceRep( "waitOrderClient"  
						)
						forward("setWaiterState", "setWaiterState(waitOrderClient)" ,"tearoomglobalstate" ) 
					}
					 transition(edgeName="t626",targetState="trasmit",cond=whenDispatch("clientOrder"))
				}	 
				state("trasmit") { //this:State
					action { //it:State
						println("waitermind 		|| trasmit")
						updateResourceRep( "trasmit" 
						)
						if( checkMsgContent( Term.createTerm("clientOrder(ID,ORDER)"), Term.createTerm("clientOrder(ID,ORDER)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								println("waitermind 		|| ricevuto ordine ID,ORDER: ${payloadArg(0)},${payloadArg(1)} ")
								if(  IdForOrder == payloadArg(0).toInt()  
								 ){forward("waiterOrderForward", "waiterOrderForward(${payloadArg(0)},${payloadArg(1)})" ,"barman" ) 
								
													IdForOrder = 0
													TableForOrder = "0"	
								}
						}
						stateTimer = TimerActor("timer_trasmit", 
							scope, context!!, "local_tout_waitermind_trasmit", 100.toLong() )
					}
					 transition(edgeName="t027",targetState="checkQueue",cond=whenTimeout("local_tout_waitermind_trasmit"))   
					transition(edgeName="t028",targetState="trasmit",cond=whenDispatch("clientOrder"))
				}	 
				state("reachBar") { //this:State
					action { //it:State
						forward("stopEngineMove", "stopEngineMove(P)" ,"waiterengine" ) 
						if( CleaningON == true  
						 ){forward("stopCleaningEngine", "stopCleaningEngine($TableToClean)" ,"waiterengine" ) 
						
										CleaningON = false 
										TableToClean = 0
						}
						println("waitermind 		|| reachBar")
						updateResourceRep( "reachBar" 
						)
						forward("setWaiterState", "setWaiterState(reachBar)" ,"tearoomglobalstate" ) 
						if( checkMsgContent( Term.createTerm("barmanOrderReady(ID)"), Term.createTerm("barmanOrderReady(ID)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								println("waitermind 		|| order ready client ID: ${payloadArg(0)}")
								
												IdForOrder = payloadArg(0).toInt()
						}
						solve("pos(bar,X,Y)","") //set resVar	
						if( currentSolution.isSuccess() ) {
											MoveX = getCurSol("X").toString().toInt()
						                	MoveY = getCurSol("Y").toString().toInt()
						forward("moveTo", "moveTo($MoveX,$MoveY)" ,"waiterengine" ) 
						}
						else
						{}
					}
					 transition(edgeName="t629",targetState="getDrink",cond=whenDispatch("done"))
				}	 
				state("getDrink") { //this:State
					action { //it:State
						updateResourceRep(""+itunibo.planner.plannerUtil.getPosX()+","+itunibo.planner.plannerUtil.getPosY() 
						)
						println("waitermind 		|| wait Enter - pos-> BARMAN || (${itunibo.planner.plannerUtil.getPosX()},${itunibo.planner.plannerUtil.getPosY()})")
						 readLine()  
						println("waitermind 		|| getDrink")
						updateResourceRep( "getDrink" 
						)
						forward("setWaiterState", "setWaiterState(getDrink)" ,"tearoomglobalstate" ) 
						forward("removeOrderReady", "removeOrderReady($IdForOrder)" ,"tearoomglobalstate" ) 
						request("getTableFromIdReq", "getTableFromIdReq($IdForOrder)" ,"tearoomglobalstate" )  
						delay(DelayTakeDrink)
					}
					 transition(edgeName="t730",targetState="checkClientPresentInRoomServe",cond=whenReply("getTableFromIdReply"))
				}	 
				state("checkClientPresentInRoomServe") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("getTableFromIdReply(N)"), Term.createTerm("getTableFromIdReply(N)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								
												TableForOrder = payloadArg(0)	
						}
						if(  TableForOrder == "0" 
						 ){ IdForOrder = 0  
						}
					}
					 transition( edgeName="goto",targetState="checkQueue", cond=doswitchGuarded({ TableForOrder == "0" 
					}) )
					transition( edgeName="goto",targetState="reachTableServe", cond=doswitchGuarded({! ( TableForOrder == "0" 
					) }) )
				}	 
				state("reachTableServe") { //this:State
					action { //it:State
						println("waitermind 		|| reachTableServe")
						solve("pos('table$TableForOrder',X,Y)","") //set resVar	
						if( currentSolution.isSuccess() ) {
											MoveX = getCurSol("X").toString().toInt()
						                	MoveY = getCurSol("Y").toString().toInt()
						forward("moveTo", "moveTo($MoveX,$MoveY)" ,"waiterengine" ) 
						}
						else
						{}
					}
					 transition(edgeName="t031",targetState="serveDrinkTable",cond=whenDispatch("done"))
				}	 
				state("serveDrinkTable") { //this:State
					action { //it:State
						delay(DelayServeDrink)
						forward("resumeTimer", "resumeTimer($TableForOrder)" ,"maxstaytime" ) 
						
									IdForOrder = 0
									TableForOrder = "0"	
					}
					 transition( edgeName="goto",targetState="checkQueue", cond=doswitch() )
				}	 
				state("reachTableCollect") { //this:State
					action { //it:State
						forward("stopEngineMove", "stopEngineMove(P)" ,"waiterengine" ) 
						if( CleaningON == true  
						 ){forward("stopCleaningEngine", "stopCleaningEngine($TableToClean)" ,"waiterengine" ) 
						
										CleaningON = false 
										TableToClean = 0
						}
						println("waitermind 		|| reachTableCollect")
						updateResourceRep( "reachTableCollect" 
						)
						forward("setWaiterState", "setWaiterState(reachTableCollect)" ,"tearoomglobalstate" ) 
						if( checkMsgContent( Term.createTerm("clientPaymentReady(ID)"), Term.createTerm("clientPaymentReady(ID)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								println("waitermind 		|| client wants to pay ID : ${payloadArg(0)}")
								request("getTableFromIdReq", "getTableFromIdReq(${payloadArg(0)})" ,"tearoomglobalstate" )  
						}
					}
					 transition(edgeName="t832",targetState="checkReachTableCollectID",cond=whenReply("getTableFromIdReply"))
				}	 
				state("checkReachTableCollectID") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("getTableFromIdReply(N)"), Term.createTerm("getTableFromIdReply(N)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								
												TableForCollect = payloadArg(0)
						}
						if(  TableForCollect != "0" 
						 ){forward("stopTimer", "stopTimer($TableForCollect)" ,"maxstaytime" ) 
						}
					}
					 transition( edgeName="goto",targetState="checkQueue", cond=doswitchGuarded({ TableForCollect == "0" 
					}) )
					transition( edgeName="goto",targetState="moveReachTableCollect", cond=doswitchGuarded({! ( TableForCollect == "0" 
					) }) )
				}	 
				state("moveReachTableCollect") { //this:State
					action { //it:State
						forward("setWaiterState", "setWaiterState(moveReachTableCollect)" ,"tearoomglobalstate" ) 
						solve("pos('table$TableForCollect',X,Y)","") //set resVar	
						if( currentSolution.isSuccess() ) {
										MoveX = getCurSol("X").toString().toInt()
						                MoveY = getCurSol("Y").toString().toInt()
						forward("moveTo", "moveTo($MoveX,$MoveY)" ,"waiterengine" ) 
						}
						else
						{}
					}
					 transition(edgeName="t033",targetState="collect",cond=whenDispatch("done"))
				}	 
				state("collect") { //this:State
					action { //it:State
						updateResourceRep(""+itunibo.planner.plannerUtil.getPosX()+","+itunibo.planner.plannerUtil.getPosY() 
						)
						println("waitermind 		|| wait Enter - pos-> Tavolo || (${itunibo.planner.plannerUtil.getPosX()},${itunibo.planner.plannerUtil.getPosY()})")
						 readLine()  
						println("waitermind 		|| collect")
						updateResourceRep( "collect" 
						)
						forward("setWaiterState", "setWaiterState(collect)" ,"tearoomglobalstate" ) 
						delay(CollectTime)
					}
					 transition( edgeName="goto",targetState="convoyExit", cond=doswitch() )
				}	 
				state("convoyExit") { //this:State
					action { //it:State
						println("waitermind 	|| convoyExit")
						updateResourceRep( "convoyExit" 
						)
						forward("setWaiterState", "setWaiterState(convoyExit)" ,"tearoomglobalstate" ) 
						forward("setStateTable", "setStateTable($TableForCollect,tableDirty,0)" ,"tearoomglobalstate" ) 
						solve("pos(exit,X,Y)","") //set resVar	
						if( currentSolution.isSuccess() ) {
											MoveX = getCurSol("X").toString().toInt()
						                	MoveY = getCurSol("Y").toString().toInt()
						forward("moveTo", "moveTo($MoveX,$MoveY)" ,"waiterengine" ) 
						}
						else
						{}
					}
					 transition(edgeName="t934",targetState="checkQueue",cond=whenDispatch("done"))
				}	 
				state("reachTableClean") { //this:State
					action { //it:State
						updateResourceRep(""+itunibo.planner.plannerUtil.getPosX()+","+itunibo.planner.plannerUtil.getPosY() 
						)
						println("waitermind 		|| wait Enter - pos-> Table || (${itunibo.planner.plannerUtil.getPosX()},${itunibo.planner.plannerUtil.getPosY()})")
						 readLine()  
						println("waitermind 	|| reachTableClean")
						updateResourceRep( "reachTableClean" 
						)
						forward("setWaiterState", "setWaiterState(reachTableClean)" ,"tearoomglobalstate" ) 
						delay(1000) 
						solve("pos('table$TableToClean',X,Y)","") //set resVar	
						if( currentSolution.isSuccess() ) {
										MoveX = getCurSol("X").toString().toInt()
						               	MoveY = getCurSol("Y").toString().toInt()
						forward("moveTo", "moveTo($MoveX,$MoveY)" ,"waiterengine" ) 
						}
						else
						{}
					}
					 transition(edgeName="t1035",targetState="whichCleanState",cond=whenDispatch("done"))
					transition(edgeName="t1036",targetState="reachTableOrder",cond=whenDispatch("clientOrderReady"))
					transition(edgeName="t1037",targetState="reachTableCollect",cond=whenDispatch("clientPaymentReady"))
					transition(edgeName="t1038",targetState="reachBar",cond=whenDispatch("barmanOrderReady"))
					transition(edgeName="t1039",targetState="reachTableKick",cond=whenDispatch("maxStayTimerExpired"))
					transition(edgeName="t1040",targetState="acceptorinform",cond=whenRequest("smartbellEntryRequest"))
				}	 
				state("whichCleanState") { //this:State
					action { //it:State
						if(  TableStateToClean == "tableClearing" || TableStateToClean == "tableDirty"  
						 ){forward("goToClearing", "goToClearing(P)" ,"waitermind" ) 
						}
						if(  TableStateToClean == "tableSanitizing"  
						 ){forward("goToSanitizing", "goToSanitizing(P)" ,"waitermind" ) 
						}
						if(  TableStateToClean == "tableCleaning"  
						 ){forward("goToCleaning", "goToCleaning(P)" ,"waitermind" ) 
						}
					}
					 transition(edgeName="t041",targetState="tableClearing",cond=whenDispatch("goToClearing"))
					transition(edgeName="t042",targetState="tableSanitizing",cond=whenDispatch("goToSanitizing"))
					transition(edgeName="t043",targetState="tableCleaning",cond=whenDispatch("goToCleaning"))
				}	 
				state("tableClearing") { //this:State
					action { //it:State
						updateResourceRep(""+itunibo.planner.plannerUtil.getPosX()+","+itunibo.planner.plannerUtil.getPosY() 
						)
						println("waitermind 		|| wait Enter - pos-> Tavolo || (${itunibo.planner.plannerUtil.getPosX()},${itunibo.planner.plannerUtil.getPosY()})")
						 readLine()  
						updateResourceRep( "tableClearing" 
						)
						forward("setWaiterState", "setWaiterState(tableClearing)" ,"tearoomglobalstate" ) 
						forward("setStateTable", "setStateTable($TableToClean,tableClearing,$TableTimerDone)" ,"tearoomglobalstate" ) 
						 
									CleaningON = true 
									TableTimerDone = ClearTime-TableTimerDone
						println("waitermind 	|| tableClearing || $TableTimerDone")
						forward("clean", "clean($DoClear,$TableTimerDone)" ,"waiterengine" ) 
						
									TableTimerDone = 0
					}
					 transition(edgeName="t1144",targetState="tableSanitizing",cond=whenDispatch("doneCleanRun"))
					transition(edgeName="t1145",targetState="reachTableOrder",cond=whenDispatch("clientOrderReady"))
					transition(edgeName="t1146",targetState="reachTableCollect",cond=whenDispatch("clientPaymentReady"))
					transition(edgeName="t1147",targetState="reachBar",cond=whenDispatch("barmanOrderReady"))
					transition(edgeName="t1148",targetState="reachTableKick",cond=whenDispatch("maxStayTimerExpired"))
					transition(edgeName="t1149",targetState="acceptorinform",cond=whenRequest("smartbellEntryRequest"))
				}	 
				state("tableSanitizing") { //this:State
					action { //it:State
						updateResourceRep( "tableSanitizing" 
						)
						forward("setWaiterState", "setWaiterState(tableSanitizing)" ,"tearoomglobalstate" ) 
						forward("setStateTable", "setStateTable($TableToClean,tableSanitizing,$TableTimerDone)" ,"tearoomglobalstate" ) 
						 
									CleaningON = true 
									TableTimerDone = SanitizeTime-TableTimerDone
						println("waitermind 	|| tableSanitizing || $TableTimerDone")
						forward("clean", "clean($DoSanitize,$TableTimerDone)" ,"waiterengine" ) 
						
									TableTimerDone = 0
					}
					 transition(edgeName="t1250",targetState="tableCleaning",cond=whenDispatch("doneCleanRun"))
					transition(edgeName="t1251",targetState="reachTableOrder",cond=whenDispatch("clientOrderReady"))
					transition(edgeName="t1252",targetState="reachTableCollect",cond=whenDispatch("clientPaymentReady"))
					transition(edgeName="t1253",targetState="reachBar",cond=whenDispatch("barmanOrderReady"))
					transition(edgeName="t1254",targetState="reachTableKick",cond=whenDispatch("maxStayTimerExpired"))
					transition(edgeName="t1255",targetState="acceptorinform",cond=whenRequest("smartbellEntryRequest"))
				}	 
				state("tableCleaning") { //this:State
					action { //it:State
						updateResourceRep( "tableCleaning" 
						)
						forward("setWaiterState", "setWaiterState(tableCleaning)" ,"tearoomglobalstate" ) 
						forward("setStateTable", "setStateTable($TableToClean,tableCleaning,$TableTimerDone)" ,"tearoomglobalstate" ) 
						 
									CleaningON = true 
									TableTimerDone = CleanTime-TableTimerDone
						println("waitermind 	|| tableCleaning || $TableTimerDone")
						forward("clean", "clean($DoClean,$TableTimerDone)" ,"waiterengine" ) 
						
									TableTimerDone = 0
					}
					 transition(edgeName="t1256",targetState="updateTableCleaned",cond=whenDispatch("doneCleanRun"))
					transition(edgeName="t1257",targetState="reachTableOrder",cond=whenDispatch("clientOrderReady"))
					transition(edgeName="t1258",targetState="reachTableCollect",cond=whenDispatch("clientPaymentReady"))
					transition(edgeName="t1259",targetState="reachBar",cond=whenDispatch("barmanOrderReady"))
					transition(edgeName="t1260",targetState="reachTableKick",cond=whenDispatch("maxStayTimerExpired"))
					transition(edgeName="t1261",targetState="acceptorinform",cond=whenRequest("smartbellEntryRequest"))
				}	 
				state("reachTableKick") { //this:State
					action { //it:State
						forward("stopEngineMove", "stopEngineMove(P)" ,"waiterengine" ) 
						if( CleaningON == true  
						 ){forward("stopCleaningEngine", "stopCleaningEngine($TableToClean)" ,"waiterengine" ) 
						
										CleaningON = false 
										TableToClean = 0
						}
						if( checkMsgContent( Term.createTerm("maxStayTimerExpired(N)"), Term.createTerm("maxStayTimerExpired(N)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								
												TableForCollect = payloadArg(0)
								println("STO CACCIANDO CLIENTE TAVOLO $TableForCollect")
								solve("pos('table$TableForCollect',X,Y)","") //set resVar	
								if( currentSolution.isSuccess() ) {
													MoveX = getCurSol("X").toString().toInt()
								                	MoveY = getCurSol("Y").toString().toInt()
								forward("moveTo", "moveTo($MoveX,$MoveY)" ,"waiterengine" ) 
								}
								else
								{}
						}
					}
					 transition(edgeName="t062",targetState="collect",cond=whenDispatch("done"))
				}	 
				state("updateTableCleaned") { //this:State
					action { //it:State
						println("waitermind 	|| updateTableCleaned")
						updateResourceRep( "updateTableCleaned"  
						)
						forward("setStateTable", "setStateTable($TableToClean,tableCleaned,$TableTimerDone)" ,"tearoomglobalstate" ) 
						 
									CleaningON = false 	
									TableToClean =  0		
						stateTimer = TimerActor("timer_updateTableCleaned", 
							scope, context!!, "local_tout_waitermind_updateTableCleaned", 100.toLong() )
					}
					 transition(edgeName="t1263",targetState="checkTableToClean",cond=whenTimeout("local_tout_waitermind_updateTableCleaned"))   
					transition(edgeName="t1264",targetState="reachTableOrder",cond=whenDispatch("clientOrderReady"))
					transition(edgeName="t1265",targetState="reachTableCollect",cond=whenDispatch("clientPaymentReady"))
					transition(edgeName="t1266",targetState="reachBar",cond=whenDispatch("barmanOrderReady"))
					transition(edgeName="t1267",targetState="reachTableKick",cond=whenDispatch("maxStayTimerExpired"))
					transition(edgeName="t1268",targetState="acceptorinform",cond=whenRequest("smartbellEntryRequest"))
				}	 
				state("endState") { //this:State
					action { //it:State
						println("waitermind 	|| TERMINATES")
						forward("end", "end(V)" ,"waiterengine" ) 
						terminate(0)
					}
				}	 
			}
		}
}

/* Generated by AN DISI Unibo */ 
package it.unibo.maxstaytime

import it.unibo.kactor.*
import alice.tuprolog.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
	
class Maxstaytime ( name: String, scope: CoroutineScope  ) : ActorBasicFsm( name, scope ){

	override fun getInitialState() : String{
		return "s0"
	}
	@kotlinx.coroutines.ObsoleteCoroutinesApi
	@kotlinx.coroutines.ExperimentalCoroutinesApi			
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		return { //this:ActionBasciFsm
				state("s0") { //this:State
					action { //it:State
						println("maxstaytime	|| START")
					}
					 transition( edgeName="goto",targetState="waitCmd", cond=doswitch() )
				}	 
				state("waitCmd") { //this:State
					action { //it:State
						println("maxstaytime	|| WaitCmd")
					}
					 transition(edgeName="t088",targetState="newTimer",cond=whenDispatch("startTimer"))
					transition(edgeName="t089",targetState="resume",cond=whenDispatch("resumeTimer"))
					transition(edgeName="t090",targetState="stop",cond=whenDispatch("stopTimer"))
					transition(edgeName="t091",targetState="timerExpired",cond=whenDispatch("maxStayTimerExpired"))
					transition(edgeName="t092",targetState="getTimeLeft",cond=whenRequest("askMaxStayTimeLeftReq"))
				}	 
				state("newTimer") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("startTimer(N)"), Term.createTerm("startTimer(N)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								if(  payloadArg(0) == "1"  
								 ){forward("startTimer", "startTimer(1)" ,"maxstaytime1" ) 
								}
								if(  payloadArg(0) == "2"  
								 ){forward("startTimer", "startTimer(2)" ,"maxstaytime2" ) 
								}
						}
					}
					 transition( edgeName="goto",targetState="waitCmd", cond=doswitch() )
				}	 
				state("resume") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("resumeTimer(N)"), Term.createTerm("resumeTimer(N)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								if(  payloadArg(0) == "1"  
								 ){forward("resumeTimer", "resumeTimer(1)" ,"maxstaytime1" ) 
								}
								if(  payloadArg(0) == "2"  
								 ){forward("resumeTimer", "resumeTimer(2)" ,"maxstaytime2" ) 
								}
						}
					}
					 transition( edgeName="goto",targetState="waitCmd", cond=doswitch() )
				}	 
				state("stop") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("stopTimer(N)"), Term.createTerm("stopTimer(N)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								if(  payloadArg(0) == "1"  
								 ){forward("stopTimer", "stopTimer(1)" ,"maxstaytime1" ) 
								}
								if(  payloadArg(0) == "2"  
								 ){forward("stopTimer", "stopTimer(2)" ,"maxstaytime2" ) 
								}
						}
					}
					 transition( edgeName="goto",targetState="waitCmd", cond=doswitch() )
				}	 
				state("timerExpired") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("maxStayTimerExpired(N)"), Term.createTerm("maxStayTimerExpired(N)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								if(  payloadArg(0) == "1"  
								 ){println("Ex 1")
								updateResourceRep("Expired 1" 
								)
								forward("maxStayTimerExpired", "maxStayTimerExpired(1)" ,"waitermind" ) 
								}
								if(  payloadArg(0) == "2"  
								 ){println("Ex 2")
								updateResourceRep("Expired 2" 
								)
								forward("maxStayTimerExpired", "maxStayTimerExpired(2)" ,"waitermind" ) 
								}
						}
					}
					 transition( edgeName="goto",targetState="waitCmd", cond=doswitch() )
				}	 
				state("getTimeLeft") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("askMaxStayTimeLeftReq(N)"), Term.createTerm("askMaxStayTimeLeftReq(N)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								if(  payloadArg(0) == "1"  
								 ){request("getMaxStayTimeLeftReq", "getMaxStayTimeLeftReq(1)" ,"maxstaytime1" )  
								}
								if(  payloadArg(0) == "2"  
								 ){request("getMaxStayTimeLeftReq", "getMaxStayTimeLeftReq(2)" ,"maxstaytime2" )  
								}
						}
					}
					 transition(edgeName="t093",targetState="analizeReplyAndForward",cond=whenReply("getMaxStayTimeLeftReply"))
				}	 
				state("analizeReplyAndForward") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("getMaxStayTimeLeftReply(TIMERLEFT)"), Term.createTerm("getMaxStayTimeLeftReply(TIMERLEFT)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								answer("askMaxStayTimeLeftReq", "askMaxStayTimeLeftReply", "askMaxStayTimeLeftReply(${payloadArg(0)})"   )  
						}
					}
					 transition( edgeName="goto",targetState="waitCmd", cond=doswitch() )
				}	 
			}
		}
}

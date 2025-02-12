package unibo.robot
/*
 -------------------------------------------------------------------------------------------------
 A factory that creates the proper support for each specific robot type
 
 NOV 2019:
 The operation create accept as last argument (filter) an ActorBasic to be used
 as a data-stream handler
 
 The operation subscribeToFilter subscribes to the given data-stream handler
 (dsh) another ActorBasic that should work as a data-stream handler
 -------------------------------------------------------------------------------------------------
 */

import it.unibo.kactor.ActorBasic
import it.unibo.kactor.ActorBasicFsm
import org.json.JSONObject
import java.io.File
import it.unibo.kactor.MsgUtil
 
@kotlinx.coroutines.ObsoleteCoroutinesApi
@kotlinx.coroutines.ExperimentalCoroutinesApi
object robotSupport{
	lateinit var robotKind  :  String
	var endPipehandler      :  ActorBasic? = null 
	
	fun create( owner: ActorBasic, configFileName: String, endPipe: ActorBasic? = null ){
		endPipehandler      =  endPipe
		
		//read Confif.json file
		val config = File("${configFileName}").readText(Charsets.UTF_8)
		//println( "		--- robotSupport | config=$config" )
		val jsonObject   = JSONObject( config )
		robotKind        = jsonObject.getString("type") 
		val robotPort    = jsonObject.getString("port")
		val robotHostName = jsonObject.getString("hostname") 
		println( "		--- robotSupport | CREATED for $robotKind port=$robotPort" )

		when( robotKind ){
			"mockrobot"  ->  { robotMock.mockrobotSupport.create(  ) }
			"virtual"    ->  { robotVirtual.virtualrobotSupport.create( owner, robotHostName, robotPort) }
			"realmbot"   ->  { robotMbot.mbotSupport.create( owner, robotPort  ) //robotPort="/dev/ttyUSB0"   "COM6"
				//create an actor named realsonar			
			} 
 			"realnano"   ->  { robotNano.motorscSupport.create( owner )
 				val realsonar = robotNano.sonarHCSR04SupportActor("realsonar")
				//Context injection  
				owner.context!!.addInternalActor(realsonar)  
  				println("		--- robotSupport | has created the realsonar")
			}	
 							 }
//			else -> println( "		--- robotSupport | robot $robotKind unknown" )
//		}
	}
	
	fun subscribe( obj : ActorBasic ) : ActorBasic {
		if( endPipehandler != null ) endPipehandler!!.subscribe( obj )
		return obj
	}
	 
@kotlinx.coroutines.ObsoleteCoroutinesApi
@kotlinx.coroutines.ExperimentalCoroutinesApi
	fun move( cmd : String ){ //cmd = msg(M) M=w | a | s | d | h
//		println("robotSupport move cmd=$cmd robotKind=$robotKind" )
		when( robotKind ){
			"mockrobot"  -> { robotMock.mockrobotSupport.move( cmd ) 					  }
			"virtual"    -> { robotVirtual.virtualrobotSupport.move(  cmd ) 	  }	
			"realmbot"   -> { robotMbot.mbotSupport.move( cmd ) 	}
 			"realnano"   -> { robotNano.motorscSupport.move( cmd)	}
			else         -> println( "		--- robotSupport | robot unknown")
		}		
	}
	
	fun terminate(){
		when( robotKind ){
			"mockrobot"  -> {  					                  }
			"virtual"    -> { robotVirtual.virtualrobotSupport.terminate(  ) 	  }	
 			"realmbot"   -> { /* robotMbot.mbotSupport.terminate(  ) */	}
 			"realnano"   -> {robotNano.motorscSupport.terminate( )	}
			else         -> println( "		--- robotSupport | robot unknown")
		}		
		
	}
}
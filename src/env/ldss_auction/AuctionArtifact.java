// CArtAgO artifact code for project auction_ag

package ldss_auction;

import jason.asSyntax.Atom;
import jia.Constants;

import java.util.HashMap;
import java.util.Map;

import cartago.*;

public class AuctionArtifact extends Artifact {
    String currentWinner = "no_winner";
    
    // temporary storage for agent estimations
 	private static Map<String, String> estimates;
 	static
     {
 		estimates = new HashMap<String, String>();
     }

    public void init()  {
        // observable properties
        defineObsProperty("running",     "no");
        defineObsProperty("best_bid",    Double.MAX_VALUE);
        defineObsProperty("winner",      new Atom(currentWinner)); // Atom is a Jason type
    }

    @OPERATION 
    public void start(String task)  {
        if (getObsProperty("running").stringValue().equals("yes"))
            failed("The protocol is already running and so you cannot start it!");

        defineObsProperty("task", task);
        getObsProperty("running").updateValue("yes");
    }

    @OPERATION 
    public void stop()  {
        if (! getObsProperty("running").stringValue().equals("yes"))
            failed("The protocol is not running, why to stop it?!");
        
        // general logic to handle stop of all procedures
        getObsProperty("running").updateValue("no");
        getObsProperty("winner").updateValue(new Atom(currentWinner));
    }

    @OPERATION
    public void aggregateAndChooseBest() {
    	// 1. perform calculation here
    	
    	// 2. set the winner
    	currentWinner = getCurrentOpAgentId().getAgentName();
    }
    
    @OPERATION 
    public void bid(String bidValue) {
        if (getObsProperty("running").stringValue().equals("no"))
            failed("You can not bid for this auction, it is not running!");

        String agentName = getCurrentOpAgentId().getAgentName();
        estimates.put(agentName, bidValue);
        
        String taskName = getObsProperty("task").stringValue();
        
        System.out.println("Received bid from "+agentName+" for "+taskName);
    }
    
    @OPERATION
    public String getMyEstimates(String name, String res){
    	res = "name";
    	return res;
    }
}


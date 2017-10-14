// CArtAgO artifact code for project auction_ag

package ldss_auction;

import jason.asSyntax.Atom;
import javafx.util.Pair;
import java.io.IOException;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;

import cartago.*;
import dss.lingvo.t2.TTNormalizedTranslator;
import dss.lingvo.t2hflts.TT2HFLTS;
import dss.lingvo.utils.models.input.TTAlternativeModel;
import dss.lingvo.utils.models.input.multilevel.TTJSONMultiLevelInputModel;

public class AuctionArtifact extends Artifact {
    String currentWinner = "no_winner";
    
    // temporary storage for agent estimations and scales
 	private static Map<String, String> estimates;
 	private static Map<String, String> scales;
 	static
    {
 		estimates = new HashMap<String, String>();
 		scales = new HashMap<String, String>();
    }

    public void init()  {
        // observable properties
        defineObsProperty("running",     "no");
        defineObsProperty("winner",      new Atom(currentWinner)); // Atom is a Jason type
    }

    @OPERATION 
    public void start(String task, String context)  {
        if (getObsProperty("running").stringValue().equals("yes")) {
            failed("The protocol is already running and so you cannot start it!");
        }
        
        defineObsProperty("task", task + "|||" + context);
        getObsProperty("running").updateValue("yes");
    }

    @OPERATION 
    public void stop()  {
        if (!getObsProperty("running").stringValue().equals("yes")) {
            failed("The protocol is not running, why to stop it?!");
        }
        
        // general logic to handle stop of all procedures
        getObsProperty("running").updateValue("no");
        getObsProperty("winner").updateValue(new Atom(currentWinner));
    }

    @OPERATION
    public void aggregateAndChooseBest(String context, String decisionSettings) throws IOException {
    	int targetScaleSize = 7;

    	String scalesJSON = Utils.scalesToJSON(scales);
    	String estimatesJSON = Utils.estimatesToJSON(estimates);
    	String finalJSON = "{\n" + 
    			scalesJSON +
    			",\n" + estimatesJSON +
    			",\n" + context +
    			",\n" + decisionSettings +
    			"\n}";
    	
    	// create an obscured version of the model which contains only estimations
    	ObjectMapper mapper = new ObjectMapper();
    	TTJSONMultiLevelInputModel newModel =  mapper.readValue(finalJSON, TTJSONMultiLevelInputModel.class);
    	
    	// scales are very important for further calculations
    	TTNormalizedTranslator.registerScalesBatch(newModel.getScales());
        
        List<Pair<String, TT2HFLTS>> resZippedVec = Utils.calculateAll(newModel, targetScaleSize);
        
        // 2. set the winner
        TTAlternativeModel altInstance = newModel.getAlternatives()
                .stream()
                .filter((TTAlternativeModel ttAlternativeModel) -> ttAlternativeModel.getAlternativeID()
                        .equals(resZippedVec.get(0).getKey()))
                .findFirst()
                .orElse(null);
        
    	currentWinner = resZippedVec.get(0).getKey() + " " + altInstance.getAlternativeName();
    }
    
    @OPERATION 
    public void bid(String bidValue, String scalesValue) {
        if (getObsProperty("running").stringValue().equals("no")) {
            failed("You can not bid for this auction, it is not running!");
        }

        String agentName = getCurrentOpAgentId().getAgentName();
        estimates.put(agentName, bidValue);
        scales.put(agentName, scalesValue);
        
        String taskName = Utils.splitTaskForName(getObsProperty("task").stringValue());
        
        System.out.println("Received bid from "+agentName+" for "+taskName);
    }
}


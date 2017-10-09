// CArtAgO artifact code for project auction_ag

package ldss_auction;

import jason.asSyntax.Atom;
import javafx.util.Pair;
import jia.Constants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.fasterxml.jackson.databind.ObjectMapper;

import cartago.*;
import dss.lingvo.t2.TTNormalizedTranslator;
import dss.lingvo.t2hflts.TT2HFLTS;
import dss.lingvo.t2hflts.multilevel.TT2HFLTSMHTWOWAMultiLevelOperator;
import dss.lingvo.utils.TTJSONUtils;
import dss.lingvo.utils.TTUtils;
import dss.lingvo.utils.models.input.TTAlternativeModel;
import dss.lingvo.utils.models.input.multilevel.TTJSONMultiLevelInputModel;

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
    public void aggregateAndChooseBest() throws IOException {
    	// 1. perform calculation here
    	TTJSONUtils ttjsonReader = TTJSONUtils.getInstance();
    	int targetScaleSize = 7;
    	String inputFilePath = "/Users/demidovs/Documents/Projects/lingvo-dss/src/main/resources/description_multilevel.json";
    	TTJSONMultiLevelInputModel model = ttjsonReader.readJSONMultiLevelDescription(inputFilePath);
    	
    	// we will set another estimations that we get from agents in this simulation
    	model.setEstimations(null);
    	
    	// scales are very important for further calculations
    	TTNormalizedTranslator.registerScalesBatch(model.getScales());

    	ObjectMapper mapper = new ObjectMapper();
    	
    	// create fake JSON string, like:
    	// {
    	// 		"estimations": {
    	//			"agent_name" : [
    	//				<...estimations...>
    	//			], ...
    	//		}
    	// }
    	String finalJSON = "{\n \"estimations\":\n {\n";
    	List<String> l = new ArrayList<String>(estimates.keySet());
    	for (int i = 0; i < l.size(); i++) {
    		finalJSON += "\"" + l.get(i) + "\": [" + estimates.get(l.get(i)) + "]";
    		if (i+1<l.size()){
    			finalJSON += ",";
    		}
    	}
    	finalJSON += "}\n}";
    	
    	// create an obscured version of the model which contains only estimations
    	TTJSONMultiLevelInputModel newModel =  mapper.readValue(finalJSON, TTJSONMultiLevelInputModel.class);
    	
    	// set those estimations to the full model
    	model.setEstimations(newModel.getEstimations());
    	
    	List<ArrayList<ArrayList<TT2HFLTS>>> all = TTUtils.getAllEstimationsFromMultiLevelJSONModel(model, 7);
        System.out.println("Successfully got all estimations");
        // Step 1. Aggregate by abstraction level
        TT2HFLTSMHTWOWAMultiLevelOperator tt2HFLTSMHTWOWAMultiLevelOperator = new TT2HFLTSMHTWOWAMultiLevelOperator();
        List<ArrayList<ArrayList<TT2HFLTS>>> allByLevel = tt2HFLTSMHTWOWAMultiLevelOperator
                .aggregateByAbstractionLevel(model.getCriteria(),
                        model.getAbstractionLevels(),
                        all,
                        targetScaleSize);

        List<ArrayList<ArrayList<TT2HFLTS>>> allByExpert = tt2HFLTSMHTWOWAMultiLevelOperator
                .transposeByAbstractionLevel(model.getAbstractionLevels().size(),
                        model.getAlternatives().size(),
                        model.getExperts().size(),
                        allByLevel);

        float[] a = new float[model.getExpertWeightsRule().values().size()];
        float curMax = 0f;
        for (Map.Entry<String, Float> e: model.getExpertWeightsRule().entrySet()){
            if (e.getKey().equals("1")){
                curMax = e.getValue();
                break;
            }
        }
        a[0] = curMax;
        a[1] = 1-curMax;
        List<ArrayList<TT2HFLTS>> altToLevel = tt2HFLTSMHTWOWAMultiLevelOperator
                .aggregateByExpert(model.getAbstractionLevels().size(),
                        model.getAlternatives().size(),
                        7,
                        allByExpert,
                        a);

        List<TT2HFLTS> altVec = tt2HFLTSMHTWOWAMultiLevelOperator
                .aggregateFinalAltEst(7,
                        altToLevel);

        List<Pair<String, TT2HFLTS>> resZippedVec = IntStream.range(0, altVec.size())
                .mapToObj(i -> new Pair<>(model.getAlternatives().get(i).getAlternativeID(), altVec.get(i)))
                .collect(Collectors.toList());

        Collections.sort(resZippedVec, Collections.reverseOrder(new Comparator<Pair<String, TT2HFLTS>>() {
            @Override
            public int compare(Pair<String, TT2HFLTS> o1, Pair<String, TT2HFLTS> o2) {
                return TTUtils.compareTT2HFLTS(o1.getValue(), o2.getValue());
            }
        }));
    	
    	// 2. set the winner
        TTAlternativeModel altInstance = model.getAlternatives()
                .stream()
                .filter((TTAlternativeModel ttAlternativeModel) -> ttAlternativeModel.getAlternativeID()
                        .equals(resZippedVec.get(0).getKey()))
                .findFirst()
                .orElse(null);
        
    	currentWinner = resZippedVec.get(0).getKey() + " " + altInstance.getAlternativeName();
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


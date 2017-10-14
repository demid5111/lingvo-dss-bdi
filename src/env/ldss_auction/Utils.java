package ldss_auction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import dss.lingvo.t2hflts.TT2HFLTS;
import dss.lingvo.t2hflts.multilevel.TT2HFLTSMHTWOWAMultiLevelOperator;
import dss.lingvo.utils.TTUtils;
import dss.lingvo.utils.models.input.multilevel.TTJSONMultiLevelInputModel;
import javafx.util.Pair;

public class Utils {
	public static String delimiter = "\\|\\|\\|";
	public static String splitTaskForName(String taskWithContext){
		return taskWithContext.split(Utils.delimiter)[0];
	}
	
	public static String splitTaskForContext(String taskWithContext){
		return taskWithContext.split(Utils.delimiter)[1];
	}
	
	public static String estimatesToJSON(Map<String, String> estimates){
		// create fake JSON string, like:
    	// "estimations": {
    	//		"agent_name" : [
    	//			<...estimations...>
    	//		], ...
    	//	}
    	String finalJSON = "\"estimations\":\n {\n";
    	List<String> l = new ArrayList<String>(estimates.keySet());
    	for (int i = 0; i < l.size(); i++) {
    		finalJSON += "\"" + l.get(i) + "\": [" + estimates.get(l.get(i)) + "]";
    		if (i+1<l.size()){
    			finalJSON += ",";
    		}
    	}
    	finalJSON += "}";
    	
    	return finalJSON;
	}

	public static String scalesToJSON(Map<String, String> scales){
		// create fake JSON string, like:
    	// "scales": {
    	//		<...scales...>
    	//	}
    	String finalJSON = "\"scales\":\n [\n";
    	List<String> l = new ArrayList<String>(scales.keySet());
    	for (int i = 0; i < l.size(); i++) {
    		finalJSON += scales.get(l.get(i));
    		if (i+1<l.size()){
    			finalJSON += ",";
    		}
    	}
    	finalJSON += "]";
    	
    	return finalJSON;
	}
	
	public static List<Pair<String, TT2HFLTS>> calculateAll(TTJSONMultiLevelInputModel model, int targetScaleSize){
		List<ArrayList<ArrayList<TT2HFLTS>>> all = TTUtils.getAllEstimationsFromMultiLevelJSONModel(model, targetScaleSize);
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
    	
    	return resZippedVec;
	}
}

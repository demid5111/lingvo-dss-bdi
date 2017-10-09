// Internal action code for project auction_ag

package jia;

import java.util.HashMap;
import java.util.Map;

import jason.*;
import jason.asSemantics.*;
import jason.asSyntax.*;

public class getMyEstimates extends DefaultInternalAction {

    /**
	 * Serialization UID
	 */
	private static final long serialVersionUID = -6842308552186235072L;
	
	// temporary storage for agent estimations
	private static Map<String, String> estimates;
	static
    {
		estimates = new HashMap<String, String>();
		estimates.put("rice_transporter", Constants.rice_transporter);
		estimates.put("melnitza", Constants.melnitza);
		estimates.put("miller_agent", Constants.miller_agent);
		estimates.put("ecolog", Constants.ecolog);
		estimates.put("gov_social_politics", Constants.gov_social_politics);
		estimates.put("gov_politics", Constants.gov_politics);
		estimates.put("fermers", Constants.fermers);
    }

	/**
	 * :param args - parameters that we receive from Jason
	 * arg[0] - Variable - pass estimations of the agent back to him
	 * arg[1] - Atom - name of the agent to get his estimates from a map
	 */
	@Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
        if (!args[0].isVar()) {
            throw new JasonException("The first argument of 'getMyEstimates' is not a variable.");                
        }
        if (!args[1].isAtom()) {
            throw new JasonException("The second argument of 'getMyEstimates' is not an atom.");                
        }
        
        String agentName = ((Atom)args[1]).toString();
//        ts.getAg().getLogger().info("[jia.getMyEstimates] agentName="+agentName);
        
        return un.unifies(args[0], new Atom(estimates.get(agentName)));
    }
}

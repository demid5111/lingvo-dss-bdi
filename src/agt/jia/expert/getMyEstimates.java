// Internal action code for project auction_ag

package jia.expert;

import java.util.HashMap;
import java.util.Map;

import jason.*;
import jason.asSemantics.*;
import jason.asSyntax.*;
import jia.constants.Estimations;
import jia.constants.Scales;

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
		estimates.put("rice_transporter", Estimations.rice_transporter);
		estimates.put("melnitza", Estimations.melnitza);
		estimates.put("miller_agent", Estimations.miller_agent);
		estimates.put("ecolog", Estimations.ecolog);
		estimates.put("gov_social_politics", Estimations.gov_social_politics);
		estimates.put("gov_politics", Estimations.gov_politics);
		estimates.put("fermers", Estimations.fermers);
    }

	/**
	 * :param args - parameters that we receive from Jason
	 * args[0] - Variable - pass estimations of the agent back to him
	 * args[1] - Variable - pass scales of the agent back to him
	 * args[2] - Atom - context of the problem
	 * args[3] - Atom - name of the agent to get his estimates from a map
	 */
	@Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
        if (!args[0].isVar()) {
            throw new JasonException("The estimations argument of 'getMyEstimates' is not a variable.");                
        }
        else if (!args[1].isVar()) {
            throw new JasonException("The scales argument of 'getMyEstimates' is not a variable.");                
        }
        else if (!args[2].isAtom()) {
            throw new JasonException("The context argument of 'getMyEstimates' is not an atom.");                
        }
        else if (!args[3].isAtom()) {
            throw new JasonException("The agent name argument of 'getMyEstimates' is not an atom.");                
        }
        
        String agentName = ((Atom)args[3]).toString();
        String estimatesValues = estimates.get(agentName);
        
        // to this moment all agents share all possible scales with others
        String scales = Scales.full_list;
        
        un.unifies(args[1], new Atom(scales));
        return un.unifies(args[0], new Atom(estimatesValues));
    }
}

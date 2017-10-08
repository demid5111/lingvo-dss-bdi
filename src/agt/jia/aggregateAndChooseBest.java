// Internal action code for project ldss_auction

package jia;

import jason.asSemantics.*;
import jason.asSyntax.*;

public class aggregateAndChooseBest extends DefaultInternalAction {

    /**
	 * Serialization UID
	 */
	private static final long serialVersionUID = 8266677815056387304L;

	@Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
        // execute the internal action
        ts.getAg().getLogger().info("executing internal action 'jia.AggregateAndChooseBest'");
        
        // everything ok, so returns true
        return true;
    }
}

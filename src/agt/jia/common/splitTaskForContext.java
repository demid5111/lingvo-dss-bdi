// Internal action code for project auction_ag

package jia.common;

import jason.JasonException;
import jason.asSemantics.*;
import jason.asSyntax.*;
import ldss_auction.Utils;

public class splitTaskForContext extends DefaultInternalAction {

    /**
	 * Serialization UID
	 */
	private static final long serialVersionUID = 5590875839043752478L;

	/**
	 * :param args - parameters that we receive from Jason
	 * arg[0] - Variable - pass context of problem back to him
	 * arg[1] - Atom - name+context
	 */
	@Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
		if (!args[0].isVar()) {
            throw new JasonException("The first argument of 'splitTaskForContext' is not a variable.");                
        }
        else if (!args[1].isString()) {
            throw new JasonException("The second argument of 'splitTaskForContext' is not a .");                
        }
		
		String fullStr = args[1].toString();
		String context = Utils.splitTaskForContext(fullStr);
        
        return un.unifies(args[0], new Atom(context));
    }
}

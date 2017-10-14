// Internal action code for project auction_ag

package jia.coordinator;

import jason.asSemantics.*;
import jason.asSyntax.*;
import jia.constants.Context;

public class getProblemContext extends DefaultInternalAction {

    /**
	 * Serialization UID
	 */
	private static final long serialVersionUID = -6602434715472806809L;

	@Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
        
        return un.unifies(args[0], new Atom(Context.allContext));
    }
}

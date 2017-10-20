+!start(Id,P)
   <- 
	makeArtifact(Id, "ldss_auction.AuctionArtifact", [], ArtId);
	.print("Auction artifact created for ",P);
	Id::focus(ArtId);  // place observable properties of this auction in a particular name space
	jia.coordinator.getProblemContext(C);
	Id::start(P, C);
	.broadcast(achieve,focus(Id));  // ask all others to focus on this new artifact
	.at("now + 5 seconds", {+!decide(Id)}).

+!decide(Id)
   <- 
	jia.coordinator.getProblemContext(C);
	jia.coordinator.getDecisionSettings(S);
 	Id::aggregateAndChooseBest(C, S);
	Id::stop.

+NS::winner(W) : W \== no_winner
   <- 
   	?NS::task(S);
   	// T - task name, S - task+context
   	jia.common.splitTaskForName(T, S);
    .print("Winner for ", T, " is ",W, "\n\n\n\n").

{ include("$jacamoJar/templates/common-cartago.asl") }
{ include("$jacamoJar/templates/common-moise.asl") }
{ include("$jacamoJar/templates/org-obedient.asl") }

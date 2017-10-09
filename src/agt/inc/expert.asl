+!focus(A) // goal sent by the auctioneer
   <- lookupArtifact(A,ToolId);
      focus(ToolId).

+task(D)[artifact_id(AId)] : running("yes")[artifact_id(AId)]
   <- 
   .my_name(Me);
   jia.getMyEstimates(E, Me);
   bid(E)[artifact_id(AId)].

+winner(W):  W \== no_winner
	<- 
	.print("Got a winner alternative: ", W).

{ include("$jacamoJar/templates/common-cartago.asl") }
{ include("$jacamoJar/templates/common-moise.asl") }
{ include("$jacamoJar/templates/org-obedient.asl") }
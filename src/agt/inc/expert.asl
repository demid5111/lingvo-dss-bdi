+!focus(A) // goal sent by the auctioneer
   <- lookupArtifact(A,ToolId);
      focus(ToolId).

+task(D)[artifact_id(AId)] : running("yes")[artifact_id(AId)]
   <- 
   .my_name(Me);
   .print("Name: ", Me);
   jia.getMyEstimates(E, Me);
   bid(E)[artifact_id(AId)].

+winner(W) : .my_name(W) <- .print("I Won!").

{ include("$jacamoJar/templates/common-cartago.asl") }
{ include("$jacamoJar/templates/common-moise.asl") }
{ include("$jacamoJar/templates/org-obedient.asl") }
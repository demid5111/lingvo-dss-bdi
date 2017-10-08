// when the central agent is sure about the winner it broadcasts the new belief
+auction(service, D)[source(A)] 
	<- 
	.print("Preparing a bid... for ", my_bid) 
	.broadcast(tell, bid(D, math.random * 100 + 10)).

// every time the new bid belief appears, this event is triggered
+bid(D, V)[source(A)]
	<- 
	.wait(1000);
	.print("Got ", D, " with value ", V, " from: ", A).

// when the central agent is sure about the winner it broadcasts the new belief
+winner(S, W)
	<- 
	print("I got the winner! ", W, " Now I need to stop").

{ include("$jacamoJar/templates/common-cartago.asl") }
{ include("$jacamoJar/templates/common-moise.asl") }
{ include("$jacamoJar/templates/org-obedient.asl") }
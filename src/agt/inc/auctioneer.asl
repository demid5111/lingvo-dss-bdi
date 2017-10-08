!start. // initial goal
  
  +!start: true
   	<- .broadcast(tell, auction(service, flight_ticket(paris,athens,"15/12/2015")));
  	.at("now + 1 seconds", {+!decide(flight_ticket(paris,athens,"15/12/2015"))}).
  
  +!decide (Service)     // receives bids and checks for new winner
     :  .findall(b(V,A),bid(Service,V)[source(A)],L)  
     <- .min(L,b(V,W));
        .print("Winner for ", Service, " is ",W," with ", V);
       .broadcast(tell, winner(Service,W)).

 
{ include("$jacamoJar/templates/common-cartago.asl") }
{ include("$jacamoJar/templates/common-moise.asl") }
{ include("$jacamoJar/templates/org-obedient.asl") }

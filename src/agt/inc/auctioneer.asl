!start. // initial goal

+!start
   <- start("flight_ticket(paris,athens,15/12/2015)");
      .at("now + 10 seconds", {+!decide}).

+!decide
   <- stop.

+winner(W) : W \== no_winner
   <- ?task(S);
      ?best_bid(V);
      .print("Winner for ", S, " is ",W," with ", V).

{ include("$jacamoJar/templates/common-cartago.asl") }
{ include("$jacamoJar/templates/common-moise.asl") }
{ include("$jacamoJar/templates/org-obedient.asl") }

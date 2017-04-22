package economySimulation;

import economySimulation.Apple;
import economySimulation.Banana;
import economySimulation.Commodity;

public class CommodityFactory {
	//this class will be used by the stock exchange to return 
	//a new commodity of a specified type denoted by letter. 
public Commodity makeCommodity(String newComodType, double buyPrice) {
			
	if (newComodType.equals("A")) {
		return new Apple(buyPrice);
	} else if (newComodType.equals("B")) {
		return new Banana(buyPrice);
	} else return null;	
}	
}

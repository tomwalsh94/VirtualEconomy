package economySimulation;

import economySimulation.Apple;
import economySimulation.Banana;
import economySimulation.Commodity;

public class CommodityFactory {
	//this class will be used by the stock exchange to return 
	//a new commodity of a specified type denoted by letter. 
public Commodity makeCommodity(String newComodType) {
			
	if (newComodType.equals("A")) {
		return new Apple(0);
	} else if (newComodType.equals("B")) {
		return new Banana(0);
	} else return null;	
}	
}

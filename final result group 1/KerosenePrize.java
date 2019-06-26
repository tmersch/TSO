public class KerosenePrize {
    //prize of kersoene per litre in US dollars, taken from https://www.globalpetrolprices.com/kerosene-prices/
    private static final double kerosenePrizeUSDPerLiter = 0.79;
    //conversion rate USD --> Euro, taken from https://www.bloomberg.com/quote/EURUSD:CUR
    private static final double conversionUSDPerEuro = 1.1215;
    //prize of kerosene per gallon in Euros
    private static final double kerosenePrizeEuroPerLiter = kerosenePrizeUSDPerLiter/conversionUSDPerEuro;
    //volumic mass of kerosene, taken from https://en.wikipedia.org/wiki/Kerosene
    //We chose 0.8 because the site says the density is 0.78 - 0.81 g/mL
    private static final double keroseneDensity = 0.8;         //g/cm^3 = g/mL = kg/L
    //prize of kerosene per kg in Euro
    private static final double kerosenePrizeEuroPerKG = kerosenePrizeEuroPerLiter/keroseneDensity;

    /** Returns the prize of the consumed fuel in Euros
      *
      * @param burntFuelMass the mass in kg of the consumed fuel
      */
    public static double getPrizeOfKeroseneInEuros (double burntFuelMass) {
        return burntFuelMass * kerosenePrizeEuroPerKG;
    }
}

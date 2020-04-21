
package group.currencyconverter;

public class ConversionModule {
    CurrencyDB currDB;
    
    // Must pass in the currency database AS A REFERENCE.  
    public ConversionModule(CurrencyDB theDB) {
        currDB = theDB;
    }
        
    public double convertCurrency(String fromCode, String toCode) {
        double fromRate = currDB.getConversionRate(fromCode);
        double toRate = currDB.getConversionRate(toCode);
        return toRate / fromRate;
    }
    
}

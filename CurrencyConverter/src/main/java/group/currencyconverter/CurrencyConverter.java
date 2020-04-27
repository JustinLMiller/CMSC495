package group.currencyconverter;


public class CurrencyConverter {
    
    public static void main(String[] args) {
        try {
            CurrencyDB currDB = new CurrencyDB();
            ConversionModule convMod = new ConversionModule(currDB);
            GUI g = new GUI(); // added line of code, 04/23/2020
            
            String toStr = "EUR";
            String fromStr = "USD";
            double testrate = convMod.convertCurrency(fromStr, toStr);
            System.out.println("Converting " + fromStr + " to " + toStr + ": " + testrate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        
        
    }
}

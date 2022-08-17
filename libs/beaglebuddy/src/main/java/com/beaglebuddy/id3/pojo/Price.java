package com.beaglebuddy.id3.pojo;

import com.beaglebuddy.id3.enums.Currency;



/**
 * <table class="logos_width">
 *    <tbody>
 *       <tr>
 *          <td                               ><img src="../../../../resources/id3v2.gif"                     width="56"  height="54"  alt="ID3 logo"        /></td>
 *          <td class="logos_horz_align_right"><img src="../../../../resources/beaglebuddy_software_logo.gif" width="340" height="110" alt="Beaglebuddy logo"/></td>
 *       </tr>
 *    </tbody>
 * </table>
 * <p>
 * An ID3v2.x price consists of a currency and an amount.
 * <table class="beaglebuddy">
 *    <caption><b>Price Fields</b></caption>
 *    <thead>
 *       <tr><th class="beaglebuddy">&nbsp;</th><th class="beaglebuddy">Field</th><th class="beaglebuddy">Description</th></tr>
 *    </thead>
 *    <tbody>
 *       <tr><td class="beaglebuddy">1. </td><td class="beaglebuddy">{@link com.beaglebuddy.id3.enums.Currency}</td><td class="beaglebuddy">currency of the price.                                 </td></tr>
 *       <tr><td class="beaglebuddy">2. </td><td class="beaglebuddy">amount                                    </td><td class="beaglebuddy">The amount uses the "." character as the decimal point.</td></tr>
 *    </tbody>
 * </table>
 * </p>
 * <p>
 * Some examples of a price are:
 * <ul>
 *    <li>USD0.99</li>
 *    <li>EUR1.00</li>
 *    <li>GBP0.65</li>
 * </ul>
 * </p>
 */
public class Price
{
   // data members
   private Currency currency;
   private String   amount;



   /**
    * default constructor using default values of USD and 0.00 for the currency and amount, respectively.
    */
   public Price()
   {
      this(Currency.USD, "0.00");
   }

   /**
    * constructor.
    * @param currency  the {@link Currency ISO-4217 currency code}.
    * @param amount    the amount of the price using "." as the decimal point.
    * @throws IllegalArgumentException  if the amount is not a valid amount.  That is, if the amount is not in the form "^\d+\.?\d*$"
    * @see #setAmount(String)
    */
   public Price(Currency currency, String amount) throws IllegalArgumentException
   {
      setCurrency(currency);
      setAmount  (amount);
   }

   /**
    * get the currency of the price.
    * @return the {@link Currency ISO-4217 currency code}.
    * @see #setCurrency(Currency)
    */
   public Currency getCurrency()
   {
      return currency;
   }

   /**
    * sets the currency of the price.
    * @param currency   the ISO-4217 currency.
    * @see #getCurrency()
    */
   public void setCurrency(Currency currency)
   {
      this.currency = currency;
   }

   /**
    * get the amount of the price.
    * @return the amount of the price.
    * @see #setAmount(String)
    */
   public String getAmount()
   {
      return amount;
   }

   /**
    * set the amount of the price.
    * The amount must coform to the regular expression: ^\d+\.?\d*$<br/>
    * This means the amount must start with a digit, followed by an optional decimal point, followed by 0 or more digits.
    * <br/>.<br/>
    * Examples of valid amounts include:
    * <ul>
    *    <li>1    </li>
    *    <li>1.   </li>
    *    <li>1.0  </li>
    *    <li>1.50 </li>
    *    <li>0.50</li>
    * </ul>
    * <br/>
    * Examples of invalid amounts include:
    * <ul>
    *    <li>+1</li>
    *    <li>-1.50</li>
    *    <li>.50</li>
    * </ul>
    * @param amount   the amount of the price using "." as the decimal point.
    * @throws IllegalArgumentException  if the amount is not a valid amount.  That is, if the amount is not in the form "^\d+\.?\d*$"
    * @see #getAmount()
    */
   public void setAmount(String amount)
   {
      if (!amount.matches("^\\d+\\.?\\d*$"))
         throw new IllegalArgumentException(amount + " is not valid amount.");

      this.amount = amount;
   }

   /**
    * get a string representation of a price.
    * @return a string representation of a price.
    */
   public String toString()
   {
      return currency.toString() + amount;
   }
}

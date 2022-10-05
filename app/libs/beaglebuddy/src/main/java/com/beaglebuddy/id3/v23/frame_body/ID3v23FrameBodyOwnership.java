package com.beaglebuddy.id3.v23.frame_body;

import java.io.InputStream;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import com.beaglebuddy.id3.enums.Currency;
import com.beaglebuddy.id3.enums.v23.Encoding;
import com.beaglebuddy.id3.enums.v23.FrameType;
import com.beaglebuddy.id3.pojo.Price;




/**
 * <table class="logos_width">
 *    <tbody>
 *       <tr>
 *          <td                               ><img src="../../../../../resources/id3v2.gif"                     width="56"  height="54"  alt="ID3 logo"        /></td>
 *          <td class="logos_horz_align_right"><img src="../../../../../resources/beaglebuddy_software_logo.gif" width="340" height="110" alt="Beaglebuddy logo"/></td>
 *       </tr>
 *    </tbody>
 * </table>
 * <p class="beaglebuddy">
 * An <i>ownership</i> frame body is associated with an ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#OWNERSHIP OWNE} {@link com.beaglebuddy.id3.v23.ID3v23Frame frame} which is used to indicate that a user is the owner of the
 * .mp3 file.  This frame is often used in conjunction with the {@link ID3v23FrameBodyTermsOfUse terms of use} and {@link ID3v23FrameBodyTextInformation file owner/licensee} frames.
 * The <i>ownership</i> frame body contains the following fields:
 * <table class="beaglebuddy">
 *    <caption><b>Ownership Frame Body Fields</b></caption>
 *    <thead>
 *       <tr><th class="beaglebuddy">&nbsp;</th><th class="beaglebuddy">Field</th><th class="beaglebuddy">Description</th></tr>
 *    </thead>
 *    <tbody>
 *       <tr><td class="beaglebuddy">1. </td><td class="beaglebuddy">{@link com.beaglebuddy.id3.enums.v23.Encoding encoding}</td><td class="beaglebuddy">character set used to {@link #setEncoding(Encoding) encode} the <i>seller</i> field.                                                                     </td></tr>
 *       <tr><td class="beaglebuddy">2. </td><td class="beaglebuddy">prices                                             </td><td class="beaglebuddy">the purchase {@link com.beaglebuddy.id3.pojo.Price price} of the .mp3 file.  The purchase price may be specified in equivalent currencies.  For
 *                                                                                                                                                   example, USD1.00 = GBP0.60 = EUR1.25, etc.  However, there may only be one price for a given currency.  That is, you may not specify USD1.00 and USD2.00.</td></tr>
 *       <tr><td class="beaglebuddy">3. </td><td class="beaglebuddy">purchaseDate                                       </td><td class="beaglebuddy">date the .mp3 file was purchased formatted as YYYYMMDD.                                                                                                  </td></tr>
 *       <tr><td class="beaglebuddy">4. </td><td class="beaglebuddy">seller                                             </td><td class="beaglebuddy">the name of the person or organization that sold the .mp3 file.                                                                                          </td></tr>
 *    </tbody>
 * </table>
 * </p>
 * <p class="beaglebuddy">
 * There may only be one "OWNE" frame in a tag.
 * </p>
 * @see com.beaglebuddy.id3.v23.ID3v23Frame
 * @see <a href="http://id3.org/id3v2.3.0"         target="_blank">ID3 tag version 2.3.0 standard</a>
 * @see <a href="http://en.wikipedia.org/wiki/ID3" target="_blank">wikipedia history of ID3 tags</a>
 */
public class ID3v23FrameBodyOwnership extends ID3v23FrameBody
{
   // class mnemonics
   private static final int PURCHASE_DATE_SIZE = 8;   // size of purchase date field

   // data members
   private Encoding    encoding;          // charset used to encode the seller.
   private List<Price> prices;            // price paid for the song
   private String      purchaseDate;      // date song was purchased - 8 character date string (YYYYMMDD)
   private String      seller;            // vendor who sold the mp3 song



   /**
    * The default constructor is called when creating a new frame.
    * The default values used are:
    * <ul>
    *    <li>ISO-8859-1 encoding</li>
    *    <li>USD 0.00 price</li>
    *    <li>today's date</li>
    *    <li>no seller</li>
    * </ul>
    */
   public ID3v23FrameBodyOwnership()
   {
      this(Encoding.ISO_8859_1, new Vector<Price>(), formateDate(null) , "");
   }

   /**
    * This constructor is called when creating a new frame.
    * @param encoding       character set used to encode the seller's name.
    * @param prices         price(s) paid for the song.
    * @param purchaseDate   date the mp3 song was purchased - 8 character date string (YYYYMMDD).
    * @param seller         vendor who sold the mp3 song.
    */
   public ID3v23FrameBodyOwnership(Encoding encoding, List<Price> prices, String purchaseDate, String seller)
   {
      super(FrameType.OWNERSHIP);

      setEncoding    (encoding);
      setPrices      (prices);
      setPurchaseDate(purchaseDate);
      setSeller      (seller);
      dirty = true;      // values have not yet been saved to the frame body's internal byte buffer
   }

   /**
    * This constructor is called when reading in an existing frame from an .mp3 file.  This is then followed by a call to the parse() method.
    * @param inputStream    input stream pointing to an ownership frame body in the .mp3 file.
    * @param frameBodySize  size (in bytes) of the frame's body.
    * @throws IOException   if there is an error while reading the frame body.
    */
   public ID3v23FrameBodyOwnership(InputStream inputStream, int frameBodySize) throws IOException
   {
      super(inputStream, FrameType.OWNERSHIP, frameBodySize);
   }

   /**
    * parses the raw bytes of the frame body and stores the parsed values in the frame's fields.
    * @throws IllegalArgumentException  if an invalid value is detected while parsing the frame body's raw bytes.
    */
   @Override
   public void parse() throws IllegalArgumentException
   {
      try
      {
         setEncoding(Encoding.valueOf(buffer[0]));
      }
      catch (IllegalArgumentException ex)
      {  // ignore the bad value and set it to ISO-8859-1 so we can continue parsing the tag
         setEncoding(Encoding.ISO_8859_1);
      }
      nullTerminatorIndex = getNextNullTerminator(1, Encoding.ISO_8859_1);
      String        pricesString = new String(buffer, 1, nullTerminatorIndex-1, Encoding.ISO_8859_1.getCharacterSet()).trim();
      String[]      pricesArray  = pricesString.split("/");
      Vector<Price> prices       = new Vector<Price>();

      for(String price : pricesArray)
         prices.add(new Price(Currency.getCurrency(price.substring(0,3)), price.substring(3)));

      setPrices(prices);
      nullTerminatorIndex++;
      purchaseDate        = new String(buffer, nullTerminatorIndex, PURCHASE_DATE_SIZE, Encoding.ISO_8859_1.getCharacterSet()).trim();
      nullTerminatorIndex+=PURCHASE_DATE_SIZE;
      seller              = new String(buffer, nullTerminatorIndex, buffer.length - nullTerminatorIndex, encoding.getCharacterSet()).trim();
      dirty               = false;    // we just read in the frame info, so the frame body's internal byte buffer is up to date
   }

   /**
    * gets the character encoding of the seller.
    * @return the character encoding of the seller.
    * @see #setEncoding(Encoding)
    */
   public Encoding getEncoding()
   {
      return encoding;
   }

   /**
    * sets the character encoding of the seller.
    * @param encoding    the character set used to encode the seller.  Only ISO 8859-1 and UTF-16 are allowed.
    * @see #getEncoding()
    */
   public void setEncoding(Encoding encoding)
   {
      if (encoding == null)
         throw new IllegalArgumentException("The encoding field in the " + frameType.getId() + " frame may not be null.");

      this.encoding = encoding;
      this.dirty    = true;
   }

   /**
    * gets the price(s) of the .mp3 song.  Mulitple prices may be specified in different currencies.
    * @return the price(s) of the song.
    * @see #setPrices(List)
    */
   public List<Price> getPrices()
   {
      return prices;
   }

   /**
    * sets the price(s) of the .mp3 song.  The purchase price may be specified in a single currecny (ex: USD 0.99) or in equivalent currencies (USD1.00 = GBP0.60 = EUR1.25, etc.).
    * However, there may only be one price for a given currency.  That is, you may not specify USD1.00 and USD2.00.
    * @param prices   price(s) paid for the .mp3 file.
    * @see #getPrices()
    */
   public void setPrices(List<Price> prices)
   {
      if (prices == null)
         throw new IllegalArgumentException("The prices field in the " + frameType.getId() + " frame may not be empty.");

      if (prices.size() == 0)
         prices.add(new Price(Currency.USD, "0.00"));

      // see if the price contains duplicate currencies
      Currency previous = null;
      for(Price price : prices)
      {
         if (price.getCurrency() == previous) {
            throw new IllegalArgumentException("Invalid price list.  It contains two different " + price.getCurrency().getCode() + " prices.");
         }
      }

      this.dirty  = true;
      this.prices = prices;
   }

   /**
    * gets the date the mp3 song was purchased.
    * @return the purchase Date.
    * @see #setPurchaseDate(String)
    */
   public String getPurchaseDate()
   {
      return purchaseDate;
   }

   /**
    * sets the date the mp3 song was purchased.
    * @param purchaseDate   the purchaseDate in the form YYYYMMDD.
    * @throws IllegalArgumentException  if the date is not valid.
    * @see #getPurchaseDate()
    */
   public void setPurchaseDate(String purchaseDate)
   {
      if (purchaseDate == null || purchaseDate.length() != 8 || !purchaseDate.matches("\\d{8}"))
         throw new IllegalArgumentException("The purchase date field in the " + frameType.getId() + " frame contains an invalid value, " + purchaseDate + ".  It must have the format YYYYMMDD.");

      // check month
      try
      {
         int month = Integer.valueOf(purchaseDate.substring(5, 6));
         if (month < 1 || month > 12)
            throw new IllegalArgumentException("The purchase date field in the " + frameType.getId() + " frame contains an invalid value, " + purchaseDate + ".  It must have the format YYYYMMDD.");
      }
      catch (NumberFormatException ex)
      {
         throw new IllegalArgumentException("The purchase date field in the " + frameType.getId() + " frame contains an invalid value, " + purchaseDate + ".  It must have the format YYYYMMDD.");
      }

      // check day
      try
      {
         int day = Integer.valueOf(purchaseDate.substring(7, 8));
         if (day < 1 || day > 31)
            throw new IllegalArgumentException("The purchase date field in the " + frameType.getId() + " frame contains an invalid value, " + purchaseDate + ".  It must have the format YYYYMMDD.");
      }
      catch (NumberFormatException ex)
      {
         throw new IllegalArgumentException("The purchase date field in the " + frameType.getId() + " frame contains an invalid value, " + purchaseDate + ".  It must have the format YYYYMMDD.");
      }
      this.purchaseDate = purchaseDate;
      this.dirty        = true;
   }

   /**
    * sets the vendor who sold the mp3 song.
    * @return the seller.
    * @see #setSeller(String)
    */
   public String getSeller()
   {
      return seller;
   }

   /**
    * sets the vendor who sold the mp3 song.
    * @param seller   the vendor who sold the mp3 song.
    * @see #getSeller()
    */
   public void setSeller(String seller)
   {
      if (seller == null || seller.length() == 0)
         throw new IllegalArgumentException("The seller field in the " + frameType.getId() + " frame may not be empty.");

      this.seller  = seller;
      this.dirty   = true;
   }

   /**
    * If the frame body's values have been modified, then resize the raw binary buffer and store the new values there.
    * When finished, the dirty flag is reset to indicate that the buffer is up to date, and the frame is now ready to be saved to the .mp3 file.
    */
   @Override
   public void setBuffer()
   {
      if (isDirty())
      {
         byte[] priceBytes        = stringToBytes(Encoding.ISO_8859_1, pricesToString(prices));
         byte[] purchaseDateBytes = stringToBytes(Encoding.ISO_8859_1, purchaseDate);
         byte[] sellerBytes       = stringToBytes(encoding           , seller      );
         int    index             = 1;

         buffer = new byte[1 + priceBytes.length + purchaseDateBytes.length + sellerBytes.length];
         buffer[0] = (byte)encoding.ordinal();
         System.arraycopy(priceBytes       , 0, buffer, index, priceBytes       .length);
         index = 1 + priceBytes.length;
         System.arraycopy(purchaseDateBytes, 0, buffer, index, purchaseDateBytes.length);
         index += purchaseDateBytes.length;
         System.arraycopy(sellerBytes      , 0, buffer, index, sellerBytes      .length);
         dirty = false;
      }
   }

   /**
    * gets a string representation of the <i>ownership</i> frame body showing all of the frame's fields and their values.
    * @return a string representation of the frame body.
    */
   @Override
   public String toString()
   {
      StringBuffer buffer = new StringBuffer();

      buffer.append("frame body: ownership\n");
      buffer.append("   bytes............: " + this.buffer.length     + " bytes\n");
      buffer.append("                      " + hex(this.buffer, 22)   + "\n");
      buffer.append("   encoding.........: " + encoding               + "\n");
      buffer.append("   price(s).........: " + pricesToString(prices) + "\n");
      buffer.append("   purchase date....: " + purchaseDate           + "\n");
      buffer.append("   seller...........: " + seller                 + "\n");

      return buffer.toString();
   }
}

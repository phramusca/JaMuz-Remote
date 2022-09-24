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
 * A <i>commercial</i> frame body is associated with an ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#COMMERCIAL COMR} {@link com.beaglebuddy.id3.v23.ID3v23Frame frame} which allows vendors to include a price offer in an .mp3 file.
 * The <i>commercial</i> frame body contains the following fields:
 * <table class="beaglebuddy">
 *    <caption><b>Commercial Frame Body Fields</b></caption>
 *    <thead>
 *       <tr><th class="beaglebuddy">&nbsp;</th><th class="beaglebuddy">Field</th><th class="beaglebuddy">Description</th></tr>
 *    </thead>
 *    <tbody>
 *       <tr><td class="beaglebuddy">1. </td><td class="beaglebuddy">{@link com.beaglebuddy.id3.enums.v23.Encoding encoding}         </td><td class="beaglebuddy">character set used to {@link #setEncoding(Encoding) encode} the <i>seller</i> and <i>description</i> fields.                                                          </td></tr>
 *       <tr><td class="beaglebuddy">2. </td><td class="beaglebuddy">prices                                                      </td><td class="beaglebuddy">the purchase {@link com.beaglebuddy.id3.pojo.Price price} of the .mp3 file.  The purchase price may be specified in equivalent currencies.  For
 *                                                                                                                                                            example, USD1.00 = GBP0.60 = EUR1.25, etc.  However, there may only be one price for a given currency.  That is, you may not specify USD1.00 and USD2.00.             </td></tr>
 *       <tr><td class="beaglebuddy">3. </td><td class="beaglebuddy">validUntil                                                  </td><td class="beaglebuddy">date in the format YYYYMMDD indicating how long the price(s) are valid for.                                                                                           </td></tr>
 *       <tr><td class="beaglebuddy">4. </td><td class="beaglebuddy">contactURL                                                  </td><td class="beaglebuddy">URL or e-mail address with which the seller can be contacted.                                                                                                         </td></tr>
 *       <tr><td class="beaglebuddy">5. </td><td class="beaglebuddy">{@link ReceivedType receivedAs}                             </td><td class="beaglebuddy">indicates how the .mp3 audio file will be {@link #setReceivedAs(ReceivedType) delivered} after is has been bought.                                                    </td></tr>
 *       <tr><td class="beaglebuddy">6. </td><td class="beaglebuddy">seller                                                      </td><td class="beaglebuddy">name of the vendor.                                                                                                                                                   </td></tr>
 *       <tr><td class="beaglebuddy">7. </td><td class="beaglebuddy">description                                                 </td><td class="beaglebuddy">description of the .mp3 file.                                                                                                                                         </td></tr>
 *       <tr><td class="beaglebuddy">8. </td><td class="beaglebuddy">pictureMimeType                                             </td><td class="beaglebuddy">an optional field that specifies the vendor's logo image mime type.  Only <i>image/png</i> and <i>image/jpeg</i> are allowed.  If omitted, <i>image/</i> will be used.</td></tr>
 *       <tr><td class="beaglebuddy">9. </td><td class="beaglebuddy">sellerLogo                                                  </td><td class="beaglebuddy">an optional field that contains the raw binary data of the the vendor's logo image.  Both this field and the previous <i>pictureMimeType</i> field may be ommitted if
 *                                                                                                                                                            the vendor does not have a logo image to attach.                                                                                                                      </td></tr>
 *    </tbody>
 * </table>
 * </p>
 * <p>
 * There may be more than one <i>commercial</i> frame in an ID3v2.3 {@link com.beaglebuddy.id3.v23.ID3v23Tag tag}, but only one with the same seller.
 * </p>
 * @see com.beaglebuddy.id3.v23.ID3v23Frame
 * @see <a href="http://id3.org/id3v2.3.0"         target="_blank">ID3 tag version 2.3.0 standard</a>
 * @see <a href="http://en.wikipedia.org/wiki/ID3" target="_blank">wikipedia history of ID3 tags</a>
 */
public class ID3v23FrameBodyCommercial extends ID3v23FrameBody
{
   /** valid ID3v2.3 commercial received as types */
   public enum ReceivedType
   {                                                                                         /** Other                                      */
      OTHER           ("Other"              , "Other"                                     ), /** Standard CD album with other songs         */
      CD              ("CD"                 , "Standard CD album with other songs"        ), /** Compressed audio on CD                     */
      COMPRESSED      ("Compressed"         , "Compressed audio on CD"                    ), /** File over the Internet                     */
      FILE            ("File"               , "File over the Internet"                    ), /** Stream over the Internet                   */
      STREAM          ("Stream"             , "Stream over the Internet"                  ), /** As note sheets                             */
      NOTE_SHEETS     ("Note Sheets"        , "As note sheets"                            ), /** As note sheets in a book with other sheets */
      BOOK_NOTE_SHEETS("Book of Note Sheets", "As note sheets in a book with other sheets"), /** Music on other media                       */
      OTHER_MEDIA     ("Other Media"        , "Music on other media"                      ), /** Non-musical merchandise                    */
      NON_MUSICAL     ("Non-Musical"        , "Non-musical merchandise"                   );

      // data members
      private String toString;
      private String description;

      /**
       * constructor
       * @param toString      a user friendly version of the ordinal name of the received type.
       * @param description   description of the received type.
       */
      ReceivedType(String toString, String description)
      {
         this.toString    = toString;
         this.description = description;
      }

      /**
       * get a description of the received type.
       * @return a description of the received type.
       */
      public String getDescription()
      {
         return description;
      }

      /**
       * get a string representation of the received type.
       * @return a string representation of the received type.
       */
      @Override
      public String toString()
      {
         return "" + ordinal() + " - " + toString;
      }

      /**
       * converts an integral value to its corresponding received type enum.
       * @return a received type enum corresponding to the integral value.
       * @param receivedType  integral value to be converted to a received type enum.
       * @throws IllegalArgumentException   if the integral value is not a valid received type.
       */
      public static ReceivedType getReceivedType(byte receivedType) throws IllegalArgumentException
      {
         for (ReceivedType r : ReceivedType.values())
            if (receivedType == r.ordinal())
               return r;
         throw new IllegalArgumentException("Invalid received type " + receivedType + ".");
      }
   }

   // class mnemonics
   private static final int          VALID_UNTIL_DATE_SIZE = 8;   // size of valid until date field

   // data members
   private Encoding     encoding;          // charset used to encode the seller and description fields
   private List<Price>  prices;            // the purchase price(s) of the .mp3 file.
   private String       validUntil;        // date until which the price is valid
   private String       contactURL;        // url for contacting the seller
   private ReceivedType receivedAs;        // how the song was delivered to the buyer.
   private String       seller;            // name of the seller
   private String       description;       // description of the song
   private String       pictureMimeType;   // mime type of logo image
   private byte[]       sellerLogo;        // raw bytes of the seller's logo image



   /**
    * The default constructor is called when creating a new frame.
    * The default values used are:
    * <ul>
    *    <li>ISO-8859-1 encoding</li>
    *    <li>USD 0.00 price</li>
    *    <li>valid until 01/01/2099</li>
    *    <li>empty contact url</li>
    *    <li>received type as <i>other</i>- see {@link #setReceivedAs}</li>
    *    <li>empty seller</li>
    *    <li>empty description</li>
    *    <li>empty seller</li>
    *    <li>empty mime type</li>
    *    <li>empty seller logo</li>
    * </ul>
    */
   public ID3v23FrameBodyCommercial()
   {
      this(Encoding.ISO_8859_1, new Vector<Price>(), "20990101", "", ReceivedType.OTHER, "", "", "", new byte[0]);
   }

   /**
    * This constructor is called when creating a new frame.
    * @param encoding          character set used to encode the seller and the description fields.
    * @param prices            price(s) paid for the song.
    * @param validUntil        date until which the price is valid in the format YYYYMMDD.
    * @param contactURL        url for contacting the seller.
    * @param receivedAs        how the song was delivered to the buyer.
    * @param seller            name of the seller.
    * @param description       description of the song.
    * @param pictureMimeType   mime type of the seller's logo image.  this field is optional and may be set to null.
    * @param sellerLogo        seller's logo image.  this field is optional and may be set to null.
    */
   public ID3v23FrameBodyCommercial(Encoding encoding, List<Price> prices, String validUntil, String contactURL, ReceivedType receivedAs, String seller, String description, String pictureMimeType, byte[] sellerLogo)
   {
      super(FrameType.COMMERCIAL);

      setEncoding       (encoding);
      setPrices         (prices);
      setValidUntil     (validUntil);
      setContactURL     (contactURL);
      setReceivedAs     (receivedAs);
      setSeller         (seller);
      setDescription    (description);
      setPictureMimeType(pictureMimeType);
      setSellerLogo     (sellerLogo);
      dirty = true;      // values have not yet been saved to the frame body's internal byte buffer
   }

   /**
    * This constructor is called when reading in an existing frame from an .mp3 file.  This is then followed by a call to the parse() method.
    * @param inputStream    input stream pointing to a commercial frame body in the .mp3 file.
    * @param frameBodySize  size (in bytes) of the frame's body.
    * @throws IOException   if there is an error while reading the frame body.
    */
   public ID3v23FrameBodyCommercial(InputStream inputStream, int frameBodySize) throws IOException
   {
      super(inputStream, FrameType.COMMERCIAL, frameBodySize);
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
      nullTerminatorIndex     = getNextNullTerminator(1, Encoding.ISO_8859_1);
      String        pricesString = new String(buffer, 1, nullTerminatorIndex-1, Encoding.ISO_8859_1.getCharacterSet()).trim();
      String[]      pricesArray  = pricesString.split("/");
      Vector<Price> prices       = new Vector<Price>();

      for(String price : pricesArray)
         prices.add(new Price(Currency.getCurrency(price.substring(0,3)), price.substring(3)));

      nullTerminatorIndex++;
      setValidUntil(new String(buffer, nullTerminatorIndex, VALID_UNTIL_DATE_SIZE, Encoding.ISO_8859_1.getCharacterSet()).trim());
      nullTerminatorIndex    += VALID_UNTIL_DATE_SIZE;
      nextNullTerminatorIndex = getNextNullTerminator(nullTerminatorIndex, Encoding.ISO_8859_1);
      contactURL              = new String(buffer, nullTerminatorIndex, nextNullTerminatorIndex - nullTerminatorIndex, Encoding.ISO_8859_1.getCharacterSet()).trim();
      nullTerminatorIndex     = nextNullTerminatorIndex + 1;
      try
      {
         setReceivedAs(ReceivedType.getReceivedType(buffer[nullTerminatorIndex]));
      }
      catch (IllegalArgumentException ex)
      {  // ignore the bad value and set it to other so we can continue parsing the tag
         setReceivedAs(ReceivedType.OTHER);
      }
      nullTerminatorIndex++;
      nextNullTerminatorIndex = getNextNullTerminator(nullTerminatorIndex, encoding);
      seller                  = new String(buffer, nullTerminatorIndex, nextNullTerminatorIndex-nullTerminatorIndex, encoding.getCharacterSet()).trim();
      nullTerminatorIndex    += nextNullTerminatorIndex + encoding.getNumBytesInNullTerminator();
      nextNullTerminatorIndex = getNextNullTerminator(nullTerminatorIndex, encoding);
      description             = new String(buffer, nullTerminatorIndex, nextNullTerminatorIndex-nullTerminatorIndex, encoding.getCharacterSet()).trim();
      nullTerminatorIndex    += nextNullTerminatorIndex + encoding.getNumBytesInNullTerminator();
      nextNullTerminatorIndex = getNextNullTerminator(nullTerminatorIndex, Encoding.ISO_8859_1);
      pictureMimeType         = new String(buffer, nullTerminatorIndex, nextNullTerminatorIndex-nullTerminatorIndex, Encoding.ISO_8859_1.getCharacterSet()).trim();
      nullTerminatorIndex    += nextNullTerminatorIndex + 1;
      sellerLogo              = new byte[buffer.length - nullTerminatorIndex];
      System.arraycopy(buffer, nullTerminatorIndex, sellerLogo, 0, sellerLogo.length);
      dirty                   = false;    // we just read in the frame info, so the frame body's internal byte buffer is up to date
   }

   /**
    * gets the character encoding of the seller and description fields.
    * @return the character encoding of the seller and description fields.
    * @see #setEncoding(Encoding)
    */
   public Encoding getEncoding()
   {
      return encoding;
   }

   /**
    * sets the character encoding of the seller and description fields.
    * @param encoding    the character set used to encode of the seller and description fields.  Only ISO 8859-1 and UTF-16 are allowed.
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
      if (prices.size() == 0)
         prices.add(new Price(Currency.USD, "0.00"));

      // see if the price contains duplicate currencies
      Currency previous = null;
      for(Price price : prices)
      {
         if (price.getCurrency() == previous)
         throw new IllegalArgumentException("The prices field in the " + frameType.getId() + " frame contains an invalid value.  It contains two different " + price.getCurrency().getCode() + " prices.");
      }

      this.dirty  = true;
      this.prices = prices;
   }

   /**
    * gets the date until which the price is valid.
    * @return the date until which the price is valid.
    * @see #setValidUntil(String)
    */
   public String getValidUntil()
   {
      return validUntil;
   }

   /**
    * sets how long the price is valid until.
    * @param validUntil   the date until which the price is valid.
    * @see #getValidUntil()
    */
   public void setValidUntil(String validUntil)
   {
      if (validUntil == null || validUntil.length() != 8 || !validUntil.matches("\\d{8}"))
         throw new IllegalArgumentException("The valid until date field in the " + frameType.getId() + " frame contains an invalid value, " + validUntil  + ".  It must have the format YYYYMMDD.");

      this.dirty      = true;
      this.validUntil = validUntil;
   }

   /**
    * gets the url at which the seller can be contacted.
    * @return the url at which the seller can be contacted.
    * @see #setContactURL(String)
    */
   public String getContactURL()
   {
      return contactURL;
   }

   /**
    * sets the contact URL.
    * @param contactURL   the url at which the seller can be contacted.
    * @see #getContactURL()
    */
   public void setContactURL(String contactURL)
   {
      if (contactURL == null || contactURL.trim().length() == 0)
         throw new IllegalArgumentException("The contact URL field in the " + frameType.getId() + " frame may not be empty.");

      this.dirty      = true;
      this.contactURL = contactURL;
   }

   /**
    * gets how the song was delivered to the buyer.
    * @return how the song was delivered to the buyer.
    * @see #setReceivedAs(ReceivedType)
    */
   public ReceivedType getReceivedAs()
   {
      return receivedAs;
   }

   /**
    * sets how the song was delivered to the buyer.  The valid methods are listed below:
    * <table class="beaglebuddy">
    *    <thead>
    *       <tr><th class="beaglebuddy">Id</th><th class="beaglebuddy">Delivery Method</th></tr>
    *    </thead>
    *    <tbody>
    *       <tr><td class="beaglebuddy">0</td><td class="beaglebuddy">other                                     </td></tr>
    *       <tr><td class="beaglebuddy">1</td><td class="beaglebuddy">standard CD album with other songs        </td></tr>
    *       <tr><td class="beaglebuddy">2</td><td class="beaglebuddy">compressed audio on CD                    </td></tr>
    *       <tr><td class="beaglebuddy">3</td><td class="beaglebuddy">file over the Internet                    </td></tr>
    *       <tr><td class="beaglebuddy">4</td><td class="beaglebuddy">stream over the Internet                  </td></tr>
    *       <tr><td class="beaglebuddy">5</td><td class="beaglebuddy">as note sheets                            </td></tr>
    *       <tr><td class="beaglebuddy">6</td><td class="beaglebuddy">as note sheets in a book with other sheets</td></tr>
    *       <tr><td class="beaglebuddy">7</td><td class="beaglebuddy">music on other media                      </td></tr>
    *       <tr><td class="beaglebuddy">8</td><td class="beaglebuddy">non-musical merchandise                   </td></tr>
    *    </tbody>
    * </table>
    * @param receivedAs    how the song was received by the buyer.
    * @see #getReceivedAs()
    */
   public void setReceivedAs(ReceivedType receivedAs)
   {
      if (receivedAs == null)
         throw new IllegalArgumentException("The received as field in the " + frameType.getId() + " frame may not be empty.");

      this.dirty      = true;
      this.receivedAs = receivedAs;
   }

   /**
    * gets the name of the seller.
    * @return the name of the seller.
    * @see #setSeller(String)
    */
   public String getSeller()
   {
      return seller;
   }

   /**
    * sets the name of the seller.
    * @param seller   the seller's name.
    * @see #getSeller()
    */
   public void setSeller(String seller)
   {
      if (seller == null || seller.trim().length() == 0)
         throw new IllegalArgumentException("The seller field in the " + frameType.getId() + " frame may not be empty.");

      this.dirty   = true;
      this.seller  = seller;
   }

   /**
    * gets the description of the song.
    * @return the description of the song.
    * @see #setDescription(String)
    */
   public String getDescription()
   {
      return description;
   }

   /**
    * sets the description of the song.
    * @param description   the description of the song.
    * @see #getDescription()
    */
   public void setDescription(String description)
   {
      this.dirty       = true;
      this.description = description == null || description.trim().length() == 0 ? "" : description;
   }

   /**
    * gets the mime type of the seller's logo.
    * @return the mime type of the seller's logo.
    * @see #setPictureMimeType(String)
    */
   public String getPictureMimeType()
   {
      return pictureMimeType;
   }

   /**
    * sets the seller's logo's mime type.
    * @param pictureMimeType   the mime type of the seller's logo.
    * @see #getPictureMimeType()
    */
   public void setPictureMimeType(String pictureMimeType)
   {
      if (pictureMimeType != null && !pictureMimeType.equals("") && !pictureMimeType.equals("image/") && !pictureMimeType.equals("image/jpg") && !pictureMimeType.equals("image/png"))
         throw new IllegalArgumentException("The picture mime type field in the " + frameType.getId() + " frame contains an invalid value, " + pictureMimeType + ".\n" +
                                            "It must be either \"\", \"image/\", \"image/jpg\", or \"image/png\".");

      this.dirty           = true;
      this.pictureMimeType = pictureMimeType == null || pictureMimeType.length() == 0 ? "image/" : pictureMimeType;
   }

   /**
    * gets the raw bytes of the seller's logo image.
    * @return the raw bytes of the seller's logo image
    * @see #setSellerLogo(byte[])
    */
   public byte[] getSellerLogo()
   {
      return sellerLogo;
   }

   /**
    * sets the raw bytes of the seller's logo image.
    * @param sellerLogo    how the song was received by the buyer.
    * @see #getSellerLogo()
    */
   public void setSellerLogo(byte[] sellerLogo)
   {
      if (sellerLogo == null)
      {
         this.pictureMimeType = "";
         this.sellerLogo      = new byte[0];
      }
      else if (sellerLogo.length == 0)
      {
         this.pictureMimeType = "";
      }
      else
      {
         this.sellerLogo = sellerLogo;
      }
      this.dirty = true;
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
         byte[] priceBytes           = stringToBytes(Encoding.ISO_8859_1, pricesToString(prices));
         byte[] validUntilBytes      = stringToBytes(Encoding.ISO_8859_1, validUntil     );
         byte[] contactURLBytes      = stringToBytes(Encoding.ISO_8859_1, contactURL     );
         byte[] sellerBytes          = stringToBytes(encoding           , seller         );
         byte[] descriptionBytes     = stringToBytes(encoding           , description    );
         byte[] pictureMimeTypeBytes = stringToBytes(Encoding.ISO_8859_1, pictureMimeType);
         int    index                = 0;

         buffer = new byte[1 + priceBytes.length + validUntilBytes.length + contactURLBytes.length + 1 + sellerBytes.length + descriptionBytes.length + pictureMimeTypeBytes.length + sellerLogo.length];

         buffer[0] = (byte)encoding.ordinal();
         System.arraycopy(priceBytes          , 0, buffer, 1, priceBytes.length);
         index = priceBytes.length;
         System.arraycopy(validUntilBytes     , 0, buffer, index, validUntilBytes.length);
         index += validUntilBytes.length;
         System.arraycopy(contactURLBytes     , 0, buffer, index, contactURLBytes.length);
         index += contactURLBytes.length;
         buffer[index] = (byte)receivedAs.ordinal();
         System.arraycopy(sellerBytes         , 0, buffer, index, sellerBytes.length);
         index += sellerBytes.length;
         System.arraycopy(descriptionBytes    , 0, buffer, index, descriptionBytes.length);
         index += descriptionBytes.length;
         System.arraycopy(pictureMimeTypeBytes, 0, buffer, index, pictureMimeTypeBytes.length);
         index += pictureMimeTypeBytes.length;
         System.arraycopy(sellerLogo          , 0, buffer, index, sellerLogo.length);
         dirty = false;
      }
   }

   /**
    * gets a string representation of the <i>commercial</i> frame body showing all of the frame's fields and their values.
    * @return a string representation of the frame body.
    */
   @Override
   public String toString()
   {
      StringBuffer buffer = new StringBuffer();

      buffer.append("frame body: commercial\n");
      buffer.append("   bytes................: " + this.buffer.length                                 + " bytes\n");
      buffer.append("                          " + hex(this.buffer, 26)                               + "\n");
      buffer.append("   encoding.............: " + encoding                                           + "\n");
      buffer.append("   price(s).............: " + pricesToString(prices)                             + "\n");
      buffer.append("   valid until..........: " + validUntil                                         + "\n");
      buffer.append("   contact url..........: " + contactURL                                         + "\n");
      buffer.append("   received as..........: " + receivedAs                                         + "\n");
      buffer.append("   seller...............: " + seller                                             + "\n");
      buffer.append("   description..........: " + description                                        + "\n");
      buffer.append("   seller logo mime type: " + pictureMimeType == null ? "none" : pictureMimeType + "\n");
      buffer.append("   seller logo..........: " + sellerLogo.length                                  + "\n");

      return buffer.toString();
   }
}

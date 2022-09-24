package com.beaglebuddy.id3.enums.v24;



/**
 * <table class="logos_width">
 *    <tbody>
 *       <tr>
 *          <td                               ><img src="../../../../../resources/id3v2.gif"                     width="56"  height="54"  alt="ID3 logo"        /></td>
 *          <td class="logos_horz_align_right"><img src="../../../../../resources/beaglebuddy_software_logo.gif" width="340" height="110" alt="Beaglebuddy logo"/></td>
 *       </tr>
 *    </tbody>
 * </table>
 * valid ID3v2.4 text size restrictions.
 */
public enum TextSizeRestriction
{                                                                            /** No restrictions on text size.             */
   NO_RESTRICTIONS("No string size restrictions."             , (byte)0x00), /** No string is longer than 1024 characters. */
   CHARACTERS_1024("No string is longer than 1024 characters.", (byte)0x08), /** No string is longer than  128 characters. */
   CHARACTERS_128 ("No string is longer than 128 characters." , (byte)0x10), /** No string is longer than   30 characters. */
   CHARACTERS_30  ("No string is longer than 30 characters."  , (byte)0x18);

   // data members
   private String description;
   private byte   mask;

   /**
    * constructor
    * @param description   description of the text size restriction.
    * @param mask          mask used to set the appropriate bits in the flag byte in the ID3v2.4 extended header.
    */
   TextSizeRestriction(String description, byte mask)
   {
      this.description = description;
      this.mask        = mask;
   }

   /**
    * get a description of the text size restriction.
    * @return a description of the text size restriction.
    */
   public String getDescription()
   {
      return description;
   }

   /**
    * get the mask used to set the appropriate bits in the flag byte in the ID3v2.4 extended header.
    * @return the mask used to set the appropriate bits in the flag byte in the ID3v2.4 extended header.
    */
   public byte getMask()
   {
      return mask;
   }

   /**
    * converts an integral value to its corresponding text size restriction enum.
    * @return a text size restriction enum corresponding to the integral value.
    * @param textSizeRestriction  integral value to be converted to a text size restriction enum.
    * @throws IllegalArgumentException   if the integral value is not a valid text size restriction.
    */
   public static TextSizeRestriction valueOf(int textSizeRestriction) throws IllegalArgumentException
   {
      for (TextSizeRestriction r : TextSizeRestriction.values())
         if (textSizeRestriction == r.ordinal())
            return r;
      throw new IllegalArgumentException("Invalid text size restriction " + textSizeRestriction + ".");
   }

   /**
    * get a string representation of the text size restriction.
    * @return a string representation of the text size restriction.
    */
   @Override
   public String toString()
   {
      return "" + ordinal() + " - " + description;
   }
}

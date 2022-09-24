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
 * valid ID3v2.4 text encoding restrictions.
 */
public enum TextEncodingRestriction
{                                                                                         /** No String encoding restrictions.                   */
   NO_RESTRICTIONS    ("No String encoding restrictions."                  , (byte)0x00), /** Strings are only encoded with ISO-8859-1 or UTF-8. */
   ISO_8859_1_OR_UTF_8("Strings are only encoded with ISO-8859-1 or UTF-8.", (byte)0x20);

   // data members
   private String description;
   private byte   mask;

   /**
    * constructor
    * @param description   description of the text encoding restriction.
    * @param mask          mask used to set the appropriate bits in the flag byte in the ID3v2.4 extended header.
    */
   TextEncodingRestriction(String description, byte mask)
   {
      this.description = description;
      this.mask        = mask;
   }

   /**
    * get a description of the text encoding restriction.
    * @return a description of the text encoding restriction.
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
    * converts an integral value to its corresponding text encoding restriction enum.
    * @return a text encoding restriction enum corresponding to the integral value.
    * @param textEncodingRestriction  integral value to be converted to a text encoding restriction enum.
    * @throws IllegalArgumentException   if the integral value is not a valid text encoding restriction.
    */
   public static TextEncodingRestriction valueOf(int textEncodingRestriction) throws IllegalArgumentException
   {
      for (TextEncodingRestriction r : TextEncodingRestriction.values())
         if (textEncodingRestriction == r.ordinal())
            return r;
      throw new IllegalArgumentException("Invalid text encoding restriction " + textEncodingRestriction + ".");
   }

   /**
    * get a string representation of the text encoding restriction.
    * @return a string representation of the text encoding restriction.
    */
   @Override
   public String toString()
   {
      return "" + ordinal() + " - " + description;
   }
}

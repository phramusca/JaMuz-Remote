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
 * valid ID3v2.4 image encoding restrictions.
 */
public enum ImageEncodingRestriction
{                                                                            /** No image encoding restrictions.           */
   NO_RESTRICTIONS("No image encoding restrictions."          , (byte)0x00), /** Images are encoded only with PNG or JPEG. */
   PNG_OR_JPEG    ("Images are encoded only with PNG or JPEG.", (byte)0x04);

   // data members
   private String description;
   private byte   mask;

   /**
    * constructor
    * @param description   description of the image encoding restriction.
    * @param mask          mask used to set the appropriate bits in the flag byte in the ID3v2.4 extended header.
    */
   ImageEncodingRestriction(String description, byte mask)
   {
      this.description = description;
      this.mask        = mask;
   }

   /**
    * get a description of the image encoding restriction.
    * @return a description of the image encoding restriction.
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
    * converts an integral value to its corresponding image encoding restriction enum.
    * @return a image encoding restriction enum corresponding to the integral value.
    * @param imageEncodingRestriction  integral value to be converted to a image encoding restriction enum.
    * @throws IllegalArgumentException   if the integral value is not a valid image encoding restriction.
    */
   public static ImageEncodingRestriction valueOf(int imageEncodingRestriction) throws IllegalArgumentException
   {
      for (ImageEncodingRestriction r : ImageEncodingRestriction.values())
         if (imageEncodingRestriction == r.ordinal())
            return r;
      throw new IllegalArgumentException("Invalid image encoding restriction " + imageEncodingRestriction + ".");
   }

   /**
    * get a string representation of the image encoding restriction.
    * @return a string representation of the image encoding restriction.
    */
   @Override
   public String toString()
   {
      return "" + ordinal() + " - " + description;
   }
}

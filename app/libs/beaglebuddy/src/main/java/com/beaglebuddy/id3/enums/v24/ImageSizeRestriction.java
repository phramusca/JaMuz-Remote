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
 * valid ID3v2.4 image size restrictions.
 */
public enum ImageSizeRestriction
{                                                                                                                   /** No restrictions on image size.                                  */
   NO_RESTRICTIONS                ("No restrictions"                                                , (byte)0x00), /** All images are 256x256 pixels or smaller.                       */
   DIMENSIONS_256_X_256_OR_SMALLER("All images are 256x256 pixels or smaller."                      , (byte)0x01), /** All images are 64x64 pixels or smaller.                         */
   DIMENSIONS_64_x_64_OR_SMALLER  ("All images are 64x64 pixels or smaller."                        , (byte)0x02), /** All images are exactly 64x64 pixels, unless required otherwise. */
   DIMENSIONS_64_X_64             ("All images are exactly 64x64 pixels, unless required otherwise.", (byte)0x03);

   // data members
   private String description;
   private byte   mask;

   /**
    * constructor
    * @param description   description of the image size restriction.
    * @param mask          mask used to set the appropriate bits in the flag byte in the ID3v2.4 extended header.
    */
   ImageSizeRestriction(String description, byte mask)
   {
      this.description = description;
      this.mask        = mask;
   }

   /**
    * get a description of the image size restriction.
    * @return a description of the image size restriction.
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
    * converts an integral value to its corresponding image size restriction enum.
    * @return a image size restriction enum corresponding to the integral value.
    * @param imageSizeRestriction  integral value to be converted to a image size restriction enum.
    * @throws IllegalArgumentException   if the integral value is not a valid image size restriction.
    */
   public static ImageSizeRestriction valueOf(int imageSizeRestriction) throws IllegalArgumentException
   {
      for (ImageSizeRestriction r : ImageSizeRestriction.values())
         if (imageSizeRestriction == r.ordinal())
            return r;
      throw new IllegalArgumentException("Invalid image size restriction " + imageSizeRestriction + ".");
   }

   /**
    * get a string representation of the image size restriction.
    * @return a string representation of the image size restriction.
    */
   @Override
   public String toString()
   {
      return "" + ordinal() + " - " + description;
   }
}

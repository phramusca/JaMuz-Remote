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
 * valid ID3v2.4 tag size restrictions.
 */
public enum TagSizeRestriction
{                                                                                                 /** No more than 128 frames and 1   MB total tag size. */
   TAG_SIZE_1_MB_AND_128_FRAMES("No more than 128 frames and 1 MB total tag size"  , (byte)0x00), /** No more than 64  frames and 128 KB total tag size. */
   TAG_SIZE_128KB_AND_64_FRAMES("No more than 64 frames and 128 KB total tag size.", (byte)0x40), /** No more than 32  frames and  40 KB total tag size. */
   TAG_SIZE_40KB_AND_32_FRAMES ("No more than 32 frames and 40 KB total tag size." , (byte)0x80), /** No more than 32  frames and   4 KB total tag size. */
   TAG_SIZE_4KB_AND_32_FRAMES  ("No more than 32 frames and 4 KB total tag size."  , (byte)0xC0);

   // data members
   private String description;
   private byte   mask;

   /**
    * constructor
    * @param description   description of the tag size restriction.
    * @param mask          mask used to set the appropriate bits in the flag byte in the ID3v2.4 extended header.
    */
   TagSizeRestriction(String description, byte mask)
   {
      this.description = description;
      this.mask        = mask;
   }

   /**
    * get a description of the tag size restriction.
    * @return a description of the tag size restriction.
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
    * converts an integral value to its corresponding tag size restriction enum.
    * @return a tag size restriction enum corresponding to the integral value.
    * @param tagSizeRestriction  integral value to be converted to a tag size restriction enum.
    * @throws IllegalArgumentException   if the integral value is not a valid tag size restriction.
    */
   public static TagSizeRestriction valueOf(int tagSizeRestriction) throws IllegalArgumentException
   {
      for (TagSizeRestriction r : TagSizeRestriction.values())
         if (tagSizeRestriction == r.ordinal())
            return r;
      throw new IllegalArgumentException("Invalid tag size restriction " + tagSizeRestriction + ".");
   }

   /**
    * get a string representation of the tag size restriction.
    * @return a string representation of the tag size restriction.
    */
   @Override
   public String toString()
   {
      return "" + ordinal() + " - " + description;
   }
}

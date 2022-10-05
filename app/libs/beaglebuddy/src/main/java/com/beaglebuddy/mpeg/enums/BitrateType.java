package com.beaglebuddy.mpeg.enums;



/**
 * <table class="logos_width">
 *    <tbody>
 *       <tr>
 *          <td                               ><img src="../../../../resources/id3v2.gif"                     width="56"  height="54"  alt="ID3 logo"        /></td>
 *          <td class="logos_horz_align_right"><img src="../../../../resources/beaglebuddy_software_logo.gif" width="340" height="110" alt="Beaglebuddy logo"/></td>
 *       </tr>
 *    </tbody>
 * </table>
 * <p class="beaglebuddy">
 * Type of bitrate used during the encoding process.  Mp3 files can be encoded using a constant bit rate or a variable bit rate.  With constant bit rate encoding,
 * each mpeg audio frame in the .mp3 file is encoded using the same bit rate.  This has the advantage that every mpeg audio frame is independent of each other, and
 * hence decoding the .mp3 is simpler, and the .mp3 file can be split at any frame and each part will still be able to be played.  Since music consists of both
 * aurally simple and complex sections, variable bit rate encoding uses more bits for the complex parts and less for the simple parts.  The advantage of this method
 * is that .mp3 files can produce better sounding audio using less data.  The disadvantage with variable bit rate encoding is that the mpeg audio frames are dependent
 * on one another and hence an .mp3 file can not be split at an arbitrary audio frame and decoding an .mp3 file is more complex.
 * </p>
 * @see <ahref="http://en.wikipedia.org/wiki/Variable_bitrate" target="_blank" >Variable bitrate</a>
 */
public enum BitrateType
{                             /** mp3 was encoded with a constant bit rate. */
   CBR("constant bit rate"),  /** mp3 was encoded with a variable bit rate. */
   VBR("variable bit rate");

   // data members
   private String description;

   /**
    * constructor.
    * @param description  the description of the bitrate type.
    */
   private BitrateType(String description)
   {
      this.description = description;
   }

   /**
    * gets the description of the bitrate type.
    * @return the description of the bitrate type.
    */
   public String getDescription()
   {
      return description;
   }
}

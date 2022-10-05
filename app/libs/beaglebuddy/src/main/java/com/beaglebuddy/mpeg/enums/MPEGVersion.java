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
 * The list of MPEG versions supported by the <a href="http://mpeg.chiariglione.org/standards/mpeg-1/audio">MPEG audio standard</a>.
 * @see com.beaglebuddy.mpeg.MPEGFrameHeader
 * @see <a href="http://en.wikipedia.org/wiki/Moving_Picture_Experts_Group" target="_blank">Moving Picture Experts Group</a>
 */
public enum MPEGVersion
{                                                   /** MPEG version 2.5 - not an official standard */
   MPEG_25      ("MPEG 2.5", ""               ),    /** reserved for future use                     */
   MPEG_RESERVED("Reserved", ""               ),    /** MPEG version 2                              */
   MPEG_2       ("MPEG 2"  , "ISO/IEC 13818-3"),    /** MPEG version 1                              */
   MPEG_1       ("MPEG 1"  , "ISO/IEC 11172-3");

   // data members
   private String description;
   private String iso;

   /**
    * constructor.
    * @param description  description of the mpeg version.
    * @param iso          ISO standard describing the MPEG version.
    */
   private MPEGVersion(String description, String iso)
   {
      this.description = description;
      this.iso         = iso;
   }

   /**
    * gets the description of the MPEG version.
    * @return the description of the MPEG version.
    */
   public String getDescription()
   {
      return description;
   }

   /**
    * gets the ISO standard describing the MPEG version.
    * @return the ISO standard describing the MPEG version.
    */
   public String getISOStandard()
   {
      return iso;
   }

   /**
    * converts an integral value to its corresponding MPEG version enum.
    * @return the MPEG version enum corresponding to the integral value.
    * @param mpegVersion  integral value to be converted to an MPEG version enum.
    * @throws IllegalArgumentException   if the value is not a valid MPEG version.
    */
   public static MPEGVersion valueOf(int mpegVersion) throws IllegalArgumentException
   {
      for (MPEGVersion v : MPEGVersion.values())
         if (mpegVersion == v.ordinal())
            return v;
      throw new IllegalArgumentException("Invalid MPEG version " + mpegVersion + ".  It must be 0 <= version <= 3.");
   }

   /**
    * gets a string representation of the MPEG version enum.
    * @return a string representation of the MPEG version enum.
    */
   public String toString()
   {
      return description;
   }
}

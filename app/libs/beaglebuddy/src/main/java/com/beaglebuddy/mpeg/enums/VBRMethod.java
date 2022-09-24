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
 * The list of bit rate encoding methods supported by the <a target="_blank" href="http://lame.sourceforge.net/">LAME encoder</a>.
 * @see com.beaglebuddy.mpeg.LAMEHeader
 * @see <a href="http://gabriel.mp3-tech.org/mp3infotag.html" target="_blank">LAME Header Format</a>
 */
public enum VBRMethod
{                                 /** Unknown encoding method                  */
   UNKNOWN   ("Unknown"       ),  /** constant bit rate encoding               */
   CBR       ("CBR"           ),  /** average  bit rate encoding               */
   ABR       ("ABR"           ),  /** variabel bit rate encoding method 1      */
   VBR_1     ("VBR 1"         ),  /** variabel bit rate encoding method 2      */
   VBR_2     ("VBR 2"         ),  /** variabel bit rate encoding method 3      */
   VBR_3     ("VBR 3"         ),  /** variabel bit rate encoding method 4      */
   VBR_4     ("VBR 4"         ),  /** unused                                   */
   UNUSED_1  ("UNUSED"        ),  /** constant bit rate 2 pass encoding method */
   CBR_2_PASS("CBR 2 Pass"    ),  /** average  bit rate 2 pass encoding method */
   ABR_2_PASS("ABR 2 Pass"    ),  /** unused                                   */
   UNUSED_2  ("UNUSED"        ),  /** unused                                   */
   UNUSED_3  ("UNUSED"        ),  /** unused                                   */
   UNUSED_4  ("UNUSED"        ),  /** unused                                   */
   UNUSED_5  ("UNUSED"        ),  /** unused                                   */
   UNUSED_6  ("UNUSED"        ),  /** reserved                                 */
   RESERVED  ("Reserved"      );

   // data members
   private String name;

   /**
    * constructor.
    * @param name  name of the LAME VBR method.
    */
   private VBRMethod(String name)
   {
      this.name = name;
   }

   /**
    * gets the name of the MPEG layer.
    * @return the name of the MPEG layer.
    */
   public String getName()
   {
      return name;
   }

   /**
    * converts an integral value to its corresponding LAME VBR method enum.
    * @return the LAME VBR method enum corresponding to the integral value.
    * @param method  integral value to be converted to an LAME VBR method enum.
    * @throws IllegalArgumentException   if the value is not a valid LAME VBR method.
    */
   public static VBRMethod valueOf(int method) throws IllegalArgumentException
   {
      for (VBRMethod m : VBRMethod.values())
         if (method == m.ordinal())
            return m;
      throw new IllegalArgumentException("Invalid LAME VBR method " + method + ".  It must be 0 <= method <= 15.");
   }

   /**
    * gets a string representation of the LAME VBR method enum.
    * @return a string representation of the LAME VBR method enum.
    */
   public String toString()
   {
      return name;
   }
}

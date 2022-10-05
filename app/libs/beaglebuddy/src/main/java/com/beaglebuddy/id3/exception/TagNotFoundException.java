package com.beaglebuddy.id3.exception;



/**
 * <table class="logos_width">
 *    <tbody>
 *       <tr>
 *          <td                               ><img src="../../../../resources/id3v2.gif"                     width="56"  height="54"  alt="ID3 logo"        /></td>
 *          <td class="logos_horz_align_right"><img src="../../../../resources/beaglebuddy_software_logo.gif" width="340" height="110" alt="Beaglebuddy logo"/></td>
 *       </tr>
 *    </tbody>
 * </table>
 * Thrown when an ID3 tag can not be found in an .mp3 file.
 */
public class TagNotFoundException extends RuntimeException
{
	private static final long serialVersionUID = 5388491489213071181L;

   /**
    * constructor.
    * @param message  detailed message explaining what version of the ID3 tag could not be found.
    */
   public TagNotFoundException(String message)
   {
      super(message);
   }
}

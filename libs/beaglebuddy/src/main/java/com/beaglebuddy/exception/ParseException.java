package com.beaglebuddy.exception;



/**
 * <table class="logos_width">
 *    <tbody>
 *       <tr>
 *          <td                               ><img src="../../../../resources/id3v2.gif"                     width="56"  height="54"  alt="ID3 logo"        /></td>
 *          <td class="logos_horz_align_right"><img src="../../../../resources/beaglebuddy_software_logo.gif" width="340" height="110" alt="Beaglebuddy logo"/></td>
 *       </tr>
 *    </tbody>
 * </table>
 * Exception which occurs while parsing an mp3 file and unexpected bytes/values are found.
 */
public class ParseException extends RuntimeException
{
   private static final long serialVersionUID = -3213501781487092248L;

   // data members
   byte[] data;

   /**
    * constructor.
    * @param message  detailed message explaining the parsing error.
    */
   public ParseException(String message)
   {
      super(message);

      data = null;
   }

   /**
    * constructor.
    * @param message  detailed message explaining the parsing error.
    * @param data     bytes which contained the unexpected value.
    */
   public ParseException(String message, byte[] data)
   {
      super(message);

      this.data = data;
   }

   /**
    * gets bytes which contained the unexpected value.
    * @return the bytes which contained the unexpected value.
    */
   public byte[] getData()
   {
      return data;
   }
}

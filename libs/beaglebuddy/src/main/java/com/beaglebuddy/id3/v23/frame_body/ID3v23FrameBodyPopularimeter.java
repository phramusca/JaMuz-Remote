package com.beaglebuddy.id3.v23.frame_body;

import java.io.InputStream;
import java.io.IOException;

import com.beaglebuddy.id3.enums.v23.Encoding;
import com.beaglebuddy.id3.enums.v23.FrameType;



/**
 * <table class="logos_width">
 *    <tbody>
 *       <tr>
 *          <td                               ><img src="../../../../../resources/id3v2.gif"                     width="56"  height="54"  alt="ID3 logo"        /></td>
 *          <td class="logos_horz_align_right"><img src="../../../../../resources/beaglebuddy_software_logo.gif" width="340" height="110" alt="Beaglebuddy logo"/></td>
 *       </tr>
 *    </tbody>
 * </table>
 * <p class="beaglebuddy">
 * A <i>popularimeter</i> frame body is associated with an ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#POPULARIMETER POPM} {@link com.beaglebuddy.id3.v23.ID3v23Frame frame} which is used to specify how much you like an .mp3 song.
 * The <i>popularimeter</i> frame body contains the following fields:
 * <table class="beaglebuddy">
 *    <caption><b>Popularimeter Frame Body Fields</b></caption>
 *    <thead>
 *       <tr><th class="beaglebuddy">&nbsp;</th><th class="beaglebuddy">Field</th><th class="beaglebuddy">Description</th></tr>
 *    </thead>
 *    <tbody>
 *       <tr><td class="beaglebuddy">1. </td><td class="beaglebuddy">userEmail</td><td class="beaglebuddy">user's e-mail address                                                                                       </td></tr>
 *       <tr><td class="beaglebuddy">2. </td><td class="beaglebuddy">rating   </td><td class="beaglebuddy">rating of the .mp3 song from 1 - 255, where 1 is the worst and 255 is best. 0 is unknown, ie, not yet rated.</td></tr>
 *       <tr><td class="beaglebuddy">3. </td><td class="beaglebuddy">counter  </td><td class="beaglebuddy">number of times the .mp3 song has been played.  this field is optional.                                     </td></tr>
 *    </tbody>
 * </table>
 * </p>
 * <p class="beaglebuddy">
 * There may be more than one <i>popularimeter</i> frame in each tag, but only one with the same e-mail address.
 * </p>
 * @see com.beaglebuddy.id3.v23.ID3v23Frame
 * @see ID3v23FrameBodyPlayCounter
 * @see <a href="http://id3.org/id3v2.3.0"         target="_blank">ID3 tag version 2.3.0 standard</a>
 * @see <a href="http://en.wikipedia.org/wiki/ID3" target="_blank">wikipedia history of ID3 tags</a>
 */
public class ID3v23FrameBodyPopularimeter extends ID3v23FrameBody
{
   // class mnemonics
                                                 /** the .mp3 song does not have a rating yet */
   public final static int UNKNOWN = 0;          /** lowest rating possible                   */
   public final static int WORST   = 1;          /** average rating                           */
   public final static int MIDDLE  = 128;        /** highest rating possible                  */
   public final static int BEST    = 255;

   // data members
   private String userEmail;     // user's e-mail
   private int    rating;        // [1-255] where 1 = worst and 255 = best.  0 is unknown, ie, not yet rated.
   private int    counter;       // number of times the song has been played (optional)



   /**
    * The default constructor is called when creating a new frame.
    * The default values used are:
    * <ul>
    *    <li>empty user e-maill address</li>
    *    <li>not yet rated</li>
    *    <li>0 times played</li>
    * </ul>
    */
   public ID3v23FrameBodyPopularimeter()
   {
      this(".", UNKNOWN, 0);
   }

   /**
    * This constructor is called when creating a new frame.
    * @param userEmail    user's e-mail.
    * @param rating       [1-255] where 1 = worst and 255 = best.  0 is unknown, ie, not yet rated.
    * @param counter      number of times the song has been played (optional).
    */
   public ID3v23FrameBodyPopularimeter(String userEmail, int rating, int counter)
   {
      super(FrameType.POPULARIMETER);

      setUserEmail(userEmail);
      setRating   (rating);
      setCounter  (counter);
      dirty = true;      // values have not yet been saved to the frame body's internal byte buffer
   }

   /**
    * This constructor is called when reading in an existing frame from an .mp3 file.  This is then followed by a call to the parse() method.
    * @param inputStream    input stream pointing to a popularimeter frame body in the .mp3 file.
    * @param frameBodySize  size (in bytes) of the frame's body.
    * @throws IOException   if there is an error while reading the frame body.
    */
   public ID3v23FrameBodyPopularimeter(InputStream inputStream, int frameBodySize) throws IOException
   {
      super(inputStream, FrameType.POPULARIMETER, frameBodySize);
   }

   /**
    * parses the raw bytes of the frame body and stores the parsed values in the frame's fields.
    * @throws IllegalArgumentException  if an invalid value is detected while parsing the frame body's raw bytes.
    */
   @Override
   public void parse() throws IllegalArgumentException
   {
      nullTerminatorIndex = getNextNullTerminator(0, Encoding.ISO_8859_1);

      setUserEmail(new String(buffer, 0, nullTerminatorIndex, Encoding.ISO_8859_1.getCharacterSet()).trim());
      nullTerminatorIndex ++;
      // note: java treats byte as a signed value, while the ID3v2.3 spec treats bytes as unsigned.
      //       this necessitates converting each byte to a larger value (integer)
      setRating(buffer[nullTerminatorIndex] & 0x0FF);
      nullTerminatorIndex ++;
      // see if the optional 4 byte counter is present
      if ((nullTerminatorIndex + 4) <= buffer.length)
      {
         counter = ((buffer[nullTerminatorIndex] & 0xFF ) << 24) + ((buffer[nullTerminatorIndex + 1] & 0xFF) << 16) + ((buffer[nullTerminatorIndex + 2] & 0xFF) << 8) + (buffer[nullTerminatorIndex + 3] & 0xFF);
         int numBytesInCounter = buffer.length - nullTerminatorIndex;  // how many bytes remaining for counter
         if (numBytesInCounter != 4)
         {
            throw new IllegalArgumentException("The size of the counter field in the " + frameType.getId() + " can not be " + buffer.length + " bytes.  It must be 4 bytes.");
         }
      }
      dirty = false;    // we just read in the frame info, so the frame body's internal byte buffer is up to date
   }

   /**
    * gets the user's e-mail address.
    * @return the user's e-mail.
    * @see #setUserEmail(String)
    */
   public String getUserEmail()
   {
      return userEmail;
   }

   /**
    * sets the user's e-mail address.
    * @param userEmail   the user's e-mail address.
    * @see #getUserEmail()
    */
   public void setUserEmail(String userEmail)
   {
      if (userEmail == null || userEmail.length() == 0)
         throw new IllegalArgumentException("The user e-mail field in the " + frameType.getId() + " frame may not be empty.");

      this.userEmail = userEmail;
      this.dirty     = true;
   }

   /**
    * gets the song's rating.
    * @return the rating of the song.
    * @see #setRating(int)
    */
   public int getRating()
   {
      return rating;
   }

   /**
    * sets the rating of the song.
    * @param rating    [1-255] where 1 = worst and 255 = best.  0 is unknown, ie, not yet rated.
    * @see #getRating()
    */
   public void setRating(int rating)
   {
      if (rating < UNKNOWN || rating > BEST)
         throw new IllegalArgumentException("The rating field in the " + frameType.getId() + " frame contains an invalid value, " + rating + ".  It must be " + UNKNOWN + " <= rating <= " + BEST + ".");

      this.rating = rating;
      this.dirty  = true;
   }

   /**
    * gets the number of times the song has been played.
    * @return the number of times the song has been played.
    * @see #setCounter(int)
    */
   public int getCounter()
   {
      return counter;
   }

   /**
    * sets the number of times the song has been played.
    * @param counter    the number of times the song has been played.
    * @see #getCounter()
    */
   public void setCounter(int counter)
   {
      if (counter < 0)
         throw new IllegalArgumentException("The counter field in the " + frameType.getId() + " frame contains an invalid value, " + counter + ".  It must be >= 0.");

      this.counter = counter;
      this.dirty   = true;
   }

   /**
    * If the frame body's values have been modified, then resize the raw binary buffer and store the new values there.
    * When finished, the dirty flag is reset to indicate that the buffer is up to date, and the frame is now ready to be saved to the .mp3 file.
    */
   @Override
   public void setBuffer()
   {
      if (isDirty())
      {
         byte[] userEmailBytes = stringToBytes(Encoding.ISO_8859_1, userEmail);
         int    index          = userEmailBytes.length;

         buffer = new byte[userEmailBytes.length + 1 + (counter == 0 ? 0 : 4)];

         System.arraycopy(userEmailBytes, 0, buffer, 0, userEmailBytes.length);
         buffer[index] = (byte)rating;
         if (counter != 0)
         {
            index++;
            System.arraycopy(intToBytes(counter), 0, buffer, index, 4);
         }
         dirty = false;
      }
   }

   /**
    * gets a string representation of the <i>popularimeter</i> frame body showing all of the frame's fields and their values.
    * @return a string representation of the frame body.
    */
   @Override
   public String toString()
   {
      StringBuffer buffer = new StringBuffer();

      buffer.append("frame body: popularimeter\n");
      buffer.append("   bytes...........: " + this.buffer.length   + " bytes\n");
      buffer.append("                     " + hex(this.buffer, 21) + "\n");
      buffer.append("   user e-mail.....: " + userEmail            + "\n");
      buffer.append("   rating..........: " + rating               + "\n");
      buffer.append("   num times played: " + counter              + "\n");

      return buffer.toString();
   }
}

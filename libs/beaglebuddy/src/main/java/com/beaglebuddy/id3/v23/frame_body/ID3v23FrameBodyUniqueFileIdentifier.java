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
 * A <i>unique file identifier</i> frame body is associated with an ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#UNIQUE_FILE_IDENTIFIER UFID} {@link com.beaglebuddy.id3.v23.ID3v23Frame frame} which is used to identify the audio file in a
 * database that may contain more information relevant to the content, such as CDDB.  This is very similar to the {@link ID3v23FrameBodyMusicCDIdentifier music CD identifier} frame.
 * The <i>ownerId</i> URL should not be used for the actual database queries. The string "http://www.id3.org/dummy/ufid.html" should be used for tests. Software that isn't told otherwise
 * may safely remove such frames. The <i>unique file identifier</i> frame body contains the following fields:
 * <table class="beaglebuddy">
 *    <caption><b>Unique File Identifier Frame Body Fields</b></caption>
 *    <thead>
 *       <tr><th class="beaglebuddy">&nbsp;</th><th class="beaglebuddy">Field</th><th class="beaglebuddy">Description</th></tr>
 *    </thead>
 *    <tbody>
 *       <tr><td class="beaglebuddy">1. </td><td class="beaglebuddy">ownerId</td><td class="beaglebuddy">URL containing an e-mail address, or a link to a location where an e-mail
 *                                                                                                       address can be found, that belongs to the organization responsible for this
 *                                                                                                       specific database implementation. Questions regarding the database should
 *                                                                                                       be sent to the e-mail address specified.                                    </td></tr>
 *       <tr><td class="beaglebuddy">2. </td><td class="beaglebuddy">data   </td><td class="beaglebuddy">the actual identifier, which may be up to 64 bytes.                         </td></tr>
 *    </tbody>
 * </table>
 * </p>
 * <p class="beaglebuddy">
 * There may be more than one <i>unique file identifier</i> frame in an ID3v2.3 {@link com.beaglebuddy.id3.v23.ID3v23Tag tag}, but only one with the same <i>owner id</i> field.
 * </p>
 * @see com.beaglebuddy.id3.v23.ID3v23Frame
 * @see <a href="http://id3.org/id3v2.3.0"         target="_blank">ID3 tag version 2.3.0 standard</a>
 * @see <a href="http://en.wikipedia.org/wiki/ID3" target="_blank">wikipedia history of ID3 tags</a>
 */
public class ID3v23FrameBodyUniqueFileIdentifier extends ID3v23FrameBody
{
   // class members
   private static int MAX_DATA_LENGTH = 64;

   // data members
   private String ownerId;   // id of the owner of the private data
   private byte[] data;      // raw binary data




   /**
    * The default constructor is called when creating a new frame.
    * The default values used are:
    * <ul>
    *    <li>empty owner id</li>
    *    <li>empty data</li>
    * </ul>
    */
   public ID3v23FrameBodyUniqueFileIdentifier()
   {
      this(" ", new byte[0]);
   }

   /**
    * This constructor is called when creating a new frame.
    * <br/><br/>
    * @param ownerId   id of the owner of the private data.
    * @param data      raw binary data
    */
   public ID3v23FrameBodyUniqueFileIdentifier(String ownerId, byte[] data)
   {
      super(FrameType.UNIQUE_FILE_IDENTIFIER);

      setOwnerId(ownerId);
      setData   (data);
      dirty = true;      // values have not yet been saved to the frame body's internal byte buffer
   }

   /**
    * This constructor is called when reading in an existing frame from an .mp3 file.  This is then followed by a call to the parse() method.
    * @param inputStream    input stream pointing to a unique file identifier frame body in the .mp3 file.
    * @param frameBodySize  size (in bytes) of the frame's body.
    * @throws IOException   if there is an error while reading the frame body.
    */
   public ID3v23FrameBodyUniqueFileIdentifier(InputStream inputStream, int frameBodySize) throws IOException
   {
      super(inputStream, FrameType.UNIQUE_FILE_IDENTIFIER, frameBodySize);
   }

   /**
    * parses the raw bytes of the frame body and stores the parsed values in the frame's fields.
    * @throws IllegalArgumentException  if an invalid value is detected while parsing the frame body's raw bytes.
    */
   @Override
   public void parse() throws IllegalArgumentException
   {
      // extract the null terminated owner id
      nullTerminatorIndex = getNextNullTerminator(0, Encoding.ISO_8859_1);
      ownerId = new String(buffer, 0, nullTerminatorIndex, Encoding.ISO_8859_1.getCharacterSet()).trim();
      nullTerminatorIndex++;
      // extract the raw binary data
      data = new byte[buffer.length - ownerId.length() - 1];
      System.arraycopy(buffer, nullTerminatorIndex, data, 0, data.length);
      dirty = false;    // we just read in the frame info, so the frame body's internal byte buffer is up to date
   }

   /**
    * gets the owner id of the frame.
    * @return the owner id of the frame.
    * @see #setOwnerId(String)
    */
   public String getOwnerId()
   {
      return ownerId;
   }

   /**
    * sets the owner id, which is a URL containing an e-mail address, or a link to a location where an e-mail address can be found, that belongs to the
    * organization responsible for the frame. Questions regarding the frame should be sent to the indicated e-mail address.
    * @param ownerId    the id of the owner of the data.
    * @see #getOwnerId()
    */
   public void setOwnerId(String ownerId)
   {
      if (ownerId == null || ownerId.length() == 0)
         throw new IllegalArgumentException("The owner id field in the " + frameType.getId() + " frame may not be empty.");

      this.dirty   = true;
      this.ownerId = ownerId;
   }

   /**
    * gets the actual .mp3 file identifier to the audio file database.
    * @return the binary data of the audio database identifier.
    * @see #setData(byte[])
    */
   public byte[] getData()
   {
      return data;
   }

   /**
    * sets the actual .mp3 file identifier to the audio file database.
    * @param data   the binary data of the audio database identifier.
    * @see #getData()
    * @throws IllegalArgumentException   if the length of the data is greater than 64.
    */
   public void setData(byte[] data) throws IllegalArgumentException
   {
/*    if (data == null)
         data = new byte[0];
*/
      if (data == null || data.length == 0)
         throw new IllegalArgumentException("The data field in the " + frameType.getId() + " frame may not be empty.");
      if (data.length > MAX_DATA_LENGTH)
         throw new IllegalArgumentException("The data field in the " + frameType.getId() + " frame contains an invalid value.  It must be < " + MAX_DATA_LENGTH + " bytes long.");

      this.data  = data;
      this.dirty = true;
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
         byte[] ownerIdBytes = stringToBytes(Encoding.ISO_8859_1, ownerId);

         buffer = new byte[ownerIdBytes.length + data.length];
         System.arraycopy(ownerIdBytes, 0, buffer, 0, ownerIdBytes.length);
         System.arraycopy(data        , 0, buffer, ownerIdBytes.length, data.length);

         dirty = false;
      }
   }

   /**
    * gets a string representation of the <i>unique field identifier</i> frame body showing all of the frame's fields and their values.
    * @return a string representation of the frame body.
    */
   @Override
   public String toString()
   {
      StringBuffer buffer = new StringBuffer();

      buffer.append("frame body: unique file identifier\n");
      buffer.append("   bytes...: " + this.buffer.length   + " bytes\n");
      buffer.append("             " + hex(this.buffer, 13) + "\n");
      buffer.append("   owner id: " + ownerId              + "\n");
      buffer.append("   data....: " + data.length          + " bytes\n");
      buffer.append("             " + hex(data, 13)        + "\n");

      return buffer.toString();
   }
}

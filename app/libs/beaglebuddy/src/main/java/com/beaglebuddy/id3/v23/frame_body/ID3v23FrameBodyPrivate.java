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
 * A <i>private</i> frame body is associated with an ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#PRIVATE PRIV} {@link com.beaglebuddy.id3.v23.ID3v23Frame frame} which contains information from a software producer that its
 * program uses and does not fit into any of the other frames. The <i>private</i> frame body contains the following fields:
 * <table class="beaglebuddy">
 *    <caption><b>Private Frame Body Fields</b></caption>
 *    <thead>
 *       <tr><th class="beaglebuddy">&nbsp;</th><th class="beaglebuddy">Field</th><th class="beaglebuddy">Description</th></tr>
 *    </thead>
 *    <tbody>
 *       <tr><td class="beaglebuddy">1. </td><td class="beaglebuddy">ownerId</td><td class="beaglebuddy">URL containing an e-mail address, or a link to a location where an e-mail address can be found, that belongs to the
 *                                                                                                       organization responsible for the frame. Questions regarding the frame should be sent to the indicated e-mail address.</td></tr>
 *       <tr><td class="beaglebuddy">2. </td><td class="beaglebuddy">data   </td><td class="beaglebuddy">raw binary data of the private content.                                                                              </td></tr>
 *    </tbody>
 * </table>
 * The tag may contain more than one <i>private</i> frame but only with different contents. It is recommended to keep the number of <i>private</i> frames as low as possible.
 * </p>
 * <p class="beaglebuddy">
 * </p>
 * @see com.beaglebuddy.id3.v23.ID3v23Frame
 * @see <a href="http://id3.org/id3v2.3.0"         target="_blank">ID3 tag version 2.3.0 standard</a>
 * @see <a href="http://en.wikipedia.org/wiki/ID3" target="_blank">wikipedia history of ID3 tags</a>
 */
public class ID3v23FrameBodyPrivate extends ID3v23FrameBody
{
   // data members
   private String ownerId;    // a URL containing an email address, or a link to a location where an email address can be found, that belongs to the organisation responsible for the frame
   private byte[] data;       // raw binary data



   /**
    * The default constructor is called when creating a new frame.
    * The default values used are:
    * <ul>
    *    <li>empty owner id</li>
    *    <li>no data</li>
    * </ul>
    */
   public ID3v23FrameBodyPrivate()
   {
      this(" ", new byte[1]);
   }

   /**
    * This constructor is called when creating a new frame.
    * @param ownerId    a URL containing an email address, or a link to a location where an email address can be found, that belongs to the organisation responsible for the frame.
    * @param data       raw binary data.
    */
   public ID3v23FrameBodyPrivate(String ownerId, byte[] data)
   {
      super(FrameType.PRIVATE);

      setOwnerId(ownerId);
      setData   (data);
      dirty = true;      // values have not yet been saved to the frame body's internal byte buffer
   }

   /**
    * This constructor is called when reading in an existing frame from an .mp3 file.  This is then followed by a call to the parse() method.
    * @param inputStream    input stream pointing to a private frame body in the .mp3 file.
    * @param frameBodySize  size (in bytes) of the frame's body.
    * @throws IOException   if there is an error while reading the frame body.
    */
   public ID3v23FrameBodyPrivate(InputStream inputStream, int frameBodySize) throws IOException
   {
      super(inputStream, FrameType.PRIVATE, frameBodySize);
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
      setOwnerId(new String(buffer, 0, nullTerminatorIndex, Encoding.ISO_8859_1.getCharacterSet()).trim());
      nullTerminatorIndex++;
      if (nullTerminatorIndex >= buffer.length)
         throw new IllegalArgumentException("The data field in the " + frameType.getId() + " frame may not be empty.");
      // extract the raw binary data
      data = new byte[buffer.length - nullTerminatorIndex];
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
    * gets the raw binary data.
    * @return the raw binary data.
    * @see #setData(byte[])
    */
   public byte[] getData()
   {
      return data;
   }

   /**
    * sets the raw binary data.
    * @param data   the binary data.
    * @see #getData()
    */
   public void setData(byte[] data)
   {
      if (data == null || data.length == 0)
         throw new IllegalArgumentException("The data field in the " + frameType.getId() + " frame may not be empty.");

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
    * gets a string representation of the <i>private</i> frame body showing all of the frame's fields and their values.
    * @return a string representation of the frame body.
    */
   @Override
   public String toString()
   {
      StringBuffer buffer = new StringBuffer();

      buffer.append("frame body: private\n");
      buffer.append("   bytes...: " + this.buffer.length   + " bytes\n");
      buffer.append("             " + hex(this.buffer, 13) + "\n");
      buffer.append("   owner id: " + ownerId              + "\n");
      buffer.append("   data....: " + data.length          + " bytes\n");
      buffer.append("             " + hex(data, 13)        + "\n");

      return buffer.toString();
   }
}

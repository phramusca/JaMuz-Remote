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
 * An <i>encryption method registration</i> frame body is associated with an ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#ENCRYPTION_METHOD_REGISTRATION ENCR} {@link com.beaglebuddy.id3.v23.ID3v23Frame frame} which is used to identify with which method a frame
 * has been encrypted.  Thus, the encryption method must be registered so that the encrypted frame(s) can be decrypyted at a later time.  The <i>encryption method registration</i> frame body contains the
 * following fields:
 * <table class="beaglebuddy">
 *    <caption><b>Encryption Method Registration Frame Body Fields</b></caption>
 *    <thead>
 *       <tr><th class="beaglebuddy">&nbsp;</th><th class="beaglebuddy">Field</th><th class="beaglebuddy">Description</th></tr>
 *    </thead>
 *    <tbody>
 *       <tr><td class="beaglebuddy">1. </td><td class="beaglebuddy">ownerId     </td><td class="beaglebuddy">URL containing an e-mail address, or a link to a location where an e-mail address can be found, that belongs to the organization responsible
 *                                                                                                            for this specific encryption method. Questions regarding the encryption method should be sent to the indicated e-mail address.              </td></tr>
 *       <tr><td class="beaglebuddy">2. </td><td class="beaglebuddy">methodSymbol</td><td class="beaglebuddy">encryption method id.  Values below 0x80 are reserved.                                                                                      </td></tr>
 *       <tr><td class="beaglebuddy">3. </td><td class="beaglebuddy">data        </td><td class="beaglebuddy">This is an optional field that, if specified, contains encryption data specific to this encryption method.                                  </td></tr>
 *    </tbody>
 * </table>
 * </p>
 * <p>
 * There may be more than one <i>encrypted method registration</i> frame in an ID3v2.3 {@link com.beaglebuddy.id3.v23.ID3v23Tag tag}, but only one with the same method
 * symbol and only one containing the same owner identifier.  The encryption method must be used somewhere in the tag.
 * </p>
 * @see com.beaglebuddy.id3.v23.ID3v23Frame
 * @see <a href="http://id3.org/id3v2.3.0"         target="_blank">ID3 tag version 2.3.0 standard</a>
 * @see <a href="http://en.wikipedia.org/wiki/ID3" target="_blank">wikipedia history of ID3 tags</a>
 */
public class ID3v23FrameBodyEncryptionMethodRegistration extends ID3v23FrameBody
{
   // data members
   private String ownerId;       // a URL containing an email address, or a link to a location where an email address can be found, that belongs to the organisation responsible for this specific encryption method.
   private byte   methodSymbol;  // id of encryption method
   private byte[] data;          // raw binary encryption data




   /**
    * The default constructor is called when creating a new frame.
    * The default values used are:
    * <ul>
    *    <li>empty owner id</li>
    *    <li>0 method symbol</li>
    *    <li>no encryption data</li>
    * </ul>
    */
   public ID3v23FrameBodyEncryptionMethodRegistration()
   {
      this("", (byte)0x00, new byte[0]);
   }

   /**
    * This constructor is called when creating a new frame.
    * @param ownerId        a URL containing an e-mail address, or a link to a location where an e-mail address can be found, that belongs to the organization responsible for this specific encryption method.
    * @param methodSymbol   id of encryption method.
    * @param data           raw binary encryption data.
    */
   public ID3v23FrameBodyEncryptionMethodRegistration(String ownerId, byte methodSymbol, byte[] data)
   {
      super(FrameType.ENCRYPTION_METHOD_REGISTRATION);

      setOwnerId     (ownerId);
      setMethodSymbol(methodSymbol);
      setData        (data);
      dirty = true;      // values have not yet been saved to the frame body's internal byte buffer
   }

   /**
    * constructor.  called when reading in an existing frame from an .mp3 file.
    * @param inputStream    input stream pointing to an encryption method registration frame body in the .mp3 file.
    * @param frameBodySize  size (in bytes) of the frame's body.
    * @throws IOException   if there is an error while reading the frame body.
    */
   public ID3v23FrameBodyEncryptionMethodRegistration(InputStream inputStream, int frameBodySize) throws IOException
   {
      super(inputStream, FrameType.ENCRYPTION_METHOD_REGISTRATION, frameBodySize);
   }

   /**
    * parses the raw bytes of the frame body and stores the parsed values in the frame's fields.
    * @throws IllegalArgumentException  if an invalid value is detected while parsing the frame body's raw bytes.
    */
   @Override
   public void parse() throws IllegalArgumentException
   {
      nullTerminatorIndex = getNextNullTerminator(0, Encoding.ISO_8859_1);
      ownerId = new String(buffer, 0, nullTerminatorIndex, Encoding.ISO_8859_1.getCharacterSet()).trim();
      nullTerminatorIndex++;
      methodSymbol = buffer[nullTerminatorIndex];
      nullTerminatorIndex++;
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
    * gets the id of the encryption method.
    * @return the id of the encryption method.
    * @see #setMethodSymbol(byte)
    */
   public byte getMethodSymbol()
   {
      return methodSymbol;
   }

   /**
    * sets the id of encryption method.
    * @param methodSymbol   the id of the encryption method.
    * @see #getMethodSymbol()
    */
   public void setMethodSymbol(byte methodSymbol)
   {
      this.dirty        = true;
      this.methodSymbol = methodSymbol;
   }

   /**
    * gets the data specific to the encryption method so that is used to encrypt/decrypt frames.
    * @return the encryption data.
    * @see #setData(byte[])
    */
   public byte[] getData()
   {
      return data;
   }

   /**
    * sets the data specific to the encryption method so that is used to encrypt/decrypt frames.
    * @param data   the binary encryption data.
    * @see #getData()
    */
   public void setData(byte[] data)
   {
      this.dirty   = true;
      this.data    = data == null ? new byte[0] : data;
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
         int    index        = 0;

         buffer = new byte[ownerIdBytes.length + 1 + data.length];

         System.arraycopy(ownerIdBytes, 0, buffer, 0, ownerIdBytes.length);
         index = ownerIdBytes.length;
         buffer[index] = methodSymbol;
         System.arraycopy(data, 0, buffer, index + 1, data.length);
         dirty = false;
      }
   }

   /**
    * gets a string representation of the <i>encryption method registration</i> frame body showing all of the frame's fields and their values.
    * @return a string representation of the frame body.
    */
   @Override
   public String toString()
   {
      StringBuffer buffer = new StringBuffer();

      buffer.append("frame body: encryption method registration\n");
      buffer.append("   bytes..........: " + this.buffer.length   + " bytes\n");
      buffer.append("                    " + hex(this.buffer, 21) + "\n");
      buffer.append("   owner id.......: " + ownerId              + "\n");
      buffer.append("   method symbol..: " + methodSymbol         + "\n");
      buffer.append("   encryption data: " + data.length          + " bytes\n");

      return buffer.toString();
   }
}

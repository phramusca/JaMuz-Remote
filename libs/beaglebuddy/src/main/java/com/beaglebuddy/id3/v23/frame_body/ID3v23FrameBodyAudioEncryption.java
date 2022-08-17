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
 * An <i>audio encryption</i> frame body is associated with an ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#AUDIO_ENCRYPTION AENC} {@link com.beaglebuddy.id3.v23.ID3v23Frame frame} which is used to indicate if any part, or all, of the actual audio
 * stream of the .mp3 file is encrypted, and if so, by whom.  The <i>audio encryption</i> frame body contains the following fields:
 * <table class="beaglebuddy">
 *    <caption><b>Audio Encryption Frame Body Fields</b></caption>
 *    <thead>
 *       <tr><th class="beaglebuddy">&nbsp;</th><th class="beaglebuddy">Field</th><th class="beaglebuddy">Description</th></tr>
 *    </thead>
 *    <tbody>
 *       <tr><td class="beaglebuddy">1.</td><td class="beaglebuddy">ownerId       </td><td class="beaglebuddy">URL containing an e-mail address, or a link to a location where an e-mail
 *                                                                                                             address can be found, that belongs to the organization responsible for this
 *                                                                                                             specific encrypted audio file. Questions regarding the encrypted audio should
 *                                                                                                             be sent to the e-mail address specified.                                   </td></tr>
 *       <tr><td class="beaglebuddy">2.</td><td class="beaglebuddy">previewStart  </td><td class="beaglebuddy">pointer to an unencrypted part of the audio.  If the entire audio part of the
 *                                                                                                             .mp3 file is encrypted, then this field should be set to 0.                </td></tr>
 *       <tr><td class="beaglebuddy">3.</td><td class="beaglebuddy">previewLength </td><td class="beaglebuddy">length of the unencrypted audio data.  If the entire audio part of the
 *                                                                                                             .mp3 file is encrypted, then this field should be set to 0.                </td></tr>
 *       <tr><td class="beaglebuddy">4.</td><td class="beaglebuddy">encryptionInfo</td><td class="beaglebuddy">data block required for decryption of the audio.  This field is optional,
 *                                                                                                             and if no data block is required for decryption, then this field can be
 *                                                                                                             left empty (a byte array of length 0).                                     </td></tr>
 *    </tbody>
 * </table>
 * </p>
 * <p class="beaglebuddy">
 * There may be more than one audio encryption frame in an ID3v2.3 {@link com.beaglebuddy.id3.v23.ID3v23Tag tag}, but only one with the same <i>ownerId</i> field.
 * </p>
 * @see com.beaglebuddy.id3.v23.ID3v23Frame
 * @see <a href="http://id3.org/id3v2.3.0"         target="_blank">ID3 tag version 2.3.0 standard</a>
 * @see <a href="http://en.wikipedia.org/wiki/ID3" target="_blank">wikipedia history of ID3 tags</a>
 */
public class ID3v23FrameBodyAudioEncryption extends ID3v23FrameBody
{
   // data members
   private String ownerId;           // an email address, or a link to a location where an email address can be found, that belongs to the organization responsible for this specific encrypted audio file.
   private int    previewStart;      // a pointer to an unencrypted part of the audio.
   private int    previewLength;     // length of unencrypted data.
   private byte[] encrcrytpionInfo;  // optional field containing data required for decryption of the audio.


   /**
    * The default constructor is called when creating a new frame.
    * The default values used are:
    * <ul>
    *    <li>empty owner id</li>
    *    <li>preview start at 0</li>
    *    <li>preview length of 0</li>
    *    <li>no encryption info</li>
    * </ul>
    */
   public ID3v23FrameBodyAudioEncryption()
   {
      this("", 0, 0, new byte[0]);
   }

   /**
    * This constructor is called when creating a new frame.
    * @param ownerId            an email address, or a link to a location where an email address can be found, that belongs to the organization responsible for this specific encrypted audio file.
    * @param previewStart       a pointer to an unencrypted part of the audio.
    * @param previewLength      length of unencrypted data.
    * @param encrcrytpionInfo   optional field containing data required for decryption of the audio.
    */
   public ID3v23FrameBodyAudioEncryption(String ownerId, int previewStart, int previewLength, byte[] encrcrytpionInfo)
   {
      super(FrameType.AUDIO_ENCRYPTION);

      setOwnerId         (ownerId);
      setPreviewStart    (previewStart);
      setPreviewLength   (previewLength);
      setEncrcrytpionInfo(encrcrytpionInfo);
      dirty = true;      // values have not yet been saved to the frame body's internal byte buffer
   }

   /**
    * This constructor is called when reading in an existing frame from an .mp3 file.  This is then followed by a call to the parse() method.
    * @param inputStream    input stream pointing to a audio encryption frame body in the .mp3 file.
    * @param frameBodySize  size (in bytes) of the frame's body.
    * @throws IOException   if there is an error while reading the frame body.
    */
   public ID3v23FrameBodyAudioEncryption(InputStream inputStream, int frameBodySize) throws IOException
   {
      super(inputStream, FrameType.AUDIO_ENCRYPTION, frameBodySize);
   }

   /**
    * parses the raw bytes of the frame body and stores the parsed values in the frame's fields.
    * @throws IllegalArgumentException  if an invalid value is detected while parsing the frame body's raw bytes.
    */
   @Override
   public void parse() throws IllegalArgumentException
   {
      nullTerminatorIndex  = getNextNullTerminator(0, Encoding.ISO_8859_1);
      ownerId              = new String(buffer, 0, nullTerminatorIndex, Encoding.ISO_8859_1.getCharacterSet()).trim();
      previewStart         = ((buffer[nullTerminatorIndex+1] & 0xFF) << 8) + (buffer[nullTerminatorIndex+2] & 0xFF);
      previewLength        = ((buffer[nullTerminatorIndex+3] & 0xFF) << 8) + (buffer[nullTerminatorIndex+4] & 0xFF);
      nullTerminatorIndex += 5;
      encrcrytpionInfo     = new byte[buffer.length - nullTerminatorIndex];
      System.arraycopy(buffer, nullTerminatorIndex, encrcrytpionInfo, 0, encrcrytpionInfo.length);
      dirty                = false;    // we just read in the frame info, so the frame body's internal byte buffer is up to date
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
    * gets a pointer to the  unencrypted part of the audio.
    * @return a pointer to an unencrypted part of the audio.
    * @see #setPreviewStart(int)
    */
   public int getPreviewStart()
   {
      return previewStart;
   }

   /**
    * sets a pointer to an unencrypted part of the audio.
    * @param previewStart   pointer to an unencrypted part of the audio.
    * @see #getPreviewStart()
    */
   public void setPreviewStart(int previewStart)
   {
      if (previewStart < 0)
         throw new IllegalArgumentException("The preview start field in the " + frameType.getId() + " frame contains an invalid value, " + previewStart + ".  It must be >= 0.");

      this.previewStart = previewStart;
      this.dirty        = true;
   }

   /**
    * gets length of unencrypted audio data.
    * @return length of unencrypted audio data.
    */
   public int getPreviewLength()
   {
      return previewLength;
   }

   /**
    * sets length of unencrypted audio data.
    * @param previewLength the length of the unencrypted audio data.
    */
   public void setPreviewLength(int previewLength)
   {
      if (previewLength < 0)
         throw new IllegalArgumentException("The preview length field in the " + frameType.getId() + " frame contains an invalid value, " + previewLength + ".  It must be >= 0.");

      this.previewLength = previewLength;
      this.dirty         = true;
   }

   /**
    * gets the data required for decryption of the audio.
    * @return data required for decryption of the audio.
    */
   public byte[] getEncrcrytpionInfo()
   {
      return encrcrytpionInfo;
   }

   /**
    * sets the data required for decryption of the audio.
    * @param encrcrytpionInfo the encrcrytpionInfo.
    */
   public void setEncrcrytpionInfo(byte[] encrcrytpionInfo)
   {
      this.encrcrytpionInfo = encrcrytpionInfo == null ? new byte[0] : encrcrytpionInfo;
      this.dirty            = true;
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

         buffer = new byte[ownerIdBytes.length + 2 + 2 + encrcrytpionInfo.length];

         System.arraycopy(ownerIdBytes, 0, buffer, 0, ownerIdBytes.length);
         index = ownerIdBytes.length;
         System.arraycopy(shortToBytes(previewStart ), 0, buffer, index, 2);
         index += 2;
         System.arraycopy(shortToBytes(previewLength), 0, buffer, index, 2);
         index += 2;
         System.arraycopy(encrcrytpionInfo, 0, buffer, index, encrcrytpionInfo.length);
         dirty = false;
      }
   }

   /**
    * gets a string representation of the <i>audio encryption</i> frame body showing all of the frame's fields and their values.
    * @return a string representation of the frame body.
    */
   @Override
   public String toString()
   {
      StringBuffer buffer = new StringBuffer();

      buffer.append("frame body: audio encryption\n");
      buffer.append("   bytes............: " + this.buffer.length        + " bytes\n");
      buffer.append("                      " + hex(this.buffer, 23)      + "\n");
      buffer.append("   owner id.........: " + ownerId                   + "\n");
      buffer.append("   preview start....: " + previewStart              + "\n");
      buffer.append("   preview length...: " + previewLength             + "\n");
      buffer.append("   encrcrytpion info: " + encrcrytpionInfo          + "bytes\n");
      buffer.append("                      " + hex(encrcrytpionInfo, 23) + "\n");
      return buffer.toString();
   }
}

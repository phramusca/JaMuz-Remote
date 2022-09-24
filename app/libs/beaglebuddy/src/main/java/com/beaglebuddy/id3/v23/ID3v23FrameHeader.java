package com.beaglebuddy.id3.v23;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

import com.beaglebuddy.id3.enums.v23.Encoding;
import com.beaglebuddy.id3.enums.v23.FrameType;
import com.beaglebuddy.id3.v23.frame_body.ID3v23FrameBodyUtility;





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
 * This class provides methods for reading and writing frame headers in ID3V2.3 frames.  An ID3v2.3 frame header is 10 bytes long and precedes the
 * {@link com.beaglebuddy.id3.v23.frame_body.ID3v23FrameBody frame body} in an ID3v2.3 {@link com.beaglebuddy.id3.v23.ID3v23Frame frame}, as shown below.
 * </p>
 * <p>
 * <a name="mp3_file_format"><img src="../../../../resources/id3v2x_frame.jpg" height="140" width="400" alt="ID3v2.3 Frame"/></a>
 * </p>
 * <p class="beaglebuddy">
 * An ID3v2.3 frame header has the following fields:
 * <table class="beaglebuddy">
 *    <caption><b>Frame Header Fields</b></caption>
 *    <thead>
 *       <tr><th class="beaglebuddy">&nbsp;</th><th class="beaglebuddy">Field</th><th class="beaglebuddy">Description</th></tr>
 *    </thead>
 *    <tbody>
 *       <tr><td class="beaglebuddy">1. </td><td class="beaglebuddy">{@link com.beaglebuddy.id3.enums.v23.FrameType frameType}</td><td class="beaglebuddy">which of the 74 ID3v2.3 defined types of frame is this the header for.                                                                           </td></tr>
 *       <tr><td class="beaglebuddy">2. </td><td class="beaglebuddy">frameBodySize                                        </td><td class="beaglebuddy">number of bytes in the associated frame body.                                                                                                        </td></tr>
 *       <tr><td class="beaglebuddy">3. </td><td class="beaglebuddy">tagAlterPreservation                                 </td><td class="beaglebuddy">an indicator to mp3 software whether the frame should be preserved or discarded if the frame is unknown and the tag is altered in any way.           </td></tr>
 *       <tr><td class="beaglebuddy">4. </td><td class="beaglebuddy">fileAlterPreservation                                </td><td class="beaglebuddy">an indicator to mp3 software whether the frame should be preserved or discarded if the frame is unknown and the audio is altered in any way.
 *                                                                                                                                                     note: this does not apply when the audio is completely replaced with other audio data.                                                               </td></tr>
 *       <tr><td class="beaglebuddy">5. </td><td class="beaglebuddy">readOnly                                             </td><td class="beaglebuddy">whether the contents of this frame are intended to be read only.                                                                                     </td></tr>
 *       <tr><td class="beaglebuddy">6. </td><td class="beaglebuddy">compressed                                           </td><td class="beaglebuddy">whether the frame has been compressed using zlib.  If it has, then the <i>uncompressedSize</i> field will contain the uncompressed size of the frame.</td></tr>
 *       <tr><td class="beaglebuddy">7. </td><td class="beaglebuddy">encrypted                                            </td><td class="beaglebuddy">whether the frame has been encrypted. If it has, then the <i>encryptionMethod</i> field will contain the method used to encrypt the frame.           </td></tr>
 *       <tr><td class="beaglebuddy">8. </td><td class="beaglebuddy">belongsToGroup                                       </td><td class="beaglebuddy">whether the frame belongs in a group with other frames.  If it does, then the <i>groupId</i> field will contain the group's id. Every frame belonging
 *                                                                                                                                                     to the same group will have the same group id.                                                                                                       </td></tr>
 *       <tr><td class="beaglebuddy">9. </td><td class="beaglebuddy">uncompressedSize                                     </td><td class="beaglebuddy">optional field set only if the <i>compressed</i>     field is true, it contains the uncompressed size of the frame.                                  </td></tr>
 *       <tr><td class="beaglebuddy">10.</td><td class="beaglebuddy">encryptionMethod                                     </td><td class="beaglebuddy">optional field set only if the <i>encrypted</i>      field is true, it contains the id of the method used to encrypt the frame.                      </td></tr>
 *       <tr><td class="beaglebuddy">11.</td><td class="beaglebuddy">groupId                                              </td><td class="beaglebuddy">optional field set only if the <i>belongsToGroup</i> field is true, it contains the id of the group to which the frame belongs.                      </td></tr>
 *       <tr><td class="beaglebuddy">12.</td><td class="beaglebuddy">dirty                                                </td><td class="beaglebuddy">whether the frame header has been modified.                                                                                                          </td></tr>
 *    </tbody>
 * </table>
 * </p>
 * @see <a href="http://www.id3.org/id3v2.3.0/"    target="_blank">ID3 tag version 2.3.0 standard</a>
 * @see <a href="http://en.wikipedia.org/wiki/ID3" target="_blank">wikipedia history of ID3 tags</a>
 */
public class ID3v23FrameHeader
{
   // class mnemonics
   private static final int  FRAME_HEADER_DEFAULT_SIZE                 = 10;
   private static final int  FRAME_HEADER_UNCOMPRESSED_SIZE            = 4;
   private static final int  FRAME_HEADER_ENCRYPTED_SIZE               = 1;
   private static final int  FRAME_HEADER_GROUP_ID_SIZE                = 1;
   private static final int  FRAME_HEADER_MAX_SIZE                     = FRAME_HEADER_DEFAULT_SIZE + FRAME_HEADER_UNCOMPRESSED_SIZE + FRAME_HEADER_ENCRYPTED_SIZE + FRAME_HEADER_GROUP_ID_SIZE;

   private static final byte FRAME_HEADER_TAG_ALTER_PRESERVATION_MASK  = (byte)0x80;
   private static final byte FRAME_HEADER_FILE_ALTER_PRESERVATION_MASK = (byte)0x40;
   private static final byte FRAME_HEADER_READ_ONLY_MASK               = (byte)0x20;
   private static final byte FRAME_HEADER_COMPRESSED_MASK              = (byte)0x80;
   private static final byte FRAME_HEADER_ENCRYPTED_MASK               = (byte)0x40;
   private static final byte FRAME_HEADER_BELONGS_TO_GROUP_MASK        = (byte)0x20;




   // data members
   private byte[]    header;                   // raw binary data of frame header.
   private FrameType frameType;                // ID3v2.3 frame type.
   private boolean   padding;                  // whether this is padding and not actually a frame header.
   private int       frameBodySize;            // size of the frame body.
   private boolean   tagAlterPreservation;     // whether the frame should be preserved or discarded if the frame is unknown and the tag is altered in any way.
   private boolean   fileAlterPreservation;    // whether the frame should be preserved or discarded if the frame is unknown and the file, excluding the tag, is altered.
   private boolean   readOnly;                 // whether the contents of this frame is intended to be read only.
   private boolean   compressed;               // whether the frame is compressed using zlib.
   private boolean   encrypted;                // whether the frame is encrypted.
   private boolean   belongsToGroup;           // whether the frame belongs in a group with other frames.
   private int       uncompressedSize;         // if compressed, indicates the frame's uncompressed size.
   private byte      encryptionMethod;         // if encrypted , indicates the method of encryption.
   private byte      groupId;                  // if belongs with other frames, indicates the frame group's id.
   private boolean   dirty;                    // whether or not the user has modified any value(s) in the frame header.
   private String    invalidFrameId;           // if the frame id read in is not a valid ID3v2.3 frame id, then this field is set so that the error can be reported later
                                               // after the entire frame has been read in (and discarded) so that we may continue processing the remaining frames.



   /**
    * This constructor is called when creating a new frame header.
    * The default values used are:
    * <ul>
    *    <li>frame body size is 0</li>
    *    <li> tag alter preservation is false</li>
    *    <li>file alter preservation is false</li>
    *    <li>read only  is false             </li>
    *    <li>compressed is false             </li>
    *    <li>encrypted  is false             </li>
    *    <li>belongs to a group is false     </li>
    *    <li>dirty is true                   </li>
    * </ul>
    * @param frameType   ID3v2.3 frame type.
    */
   public ID3v23FrameHeader(FrameType frameType)
   {
      this.header                = new byte[FRAME_HEADER_DEFAULT_SIZE];
      this.frameType             = frameType;
      this.padding               = false;
      this.frameBodySize         = 0;
      this.tagAlterPreservation  = false;
      this.fileAlterPreservation = false;
      this.readOnly              = false;
      this.compressed            = false;
      this.encrypted             = false;
      this.belongsToGroup        = false;
      this.uncompressedSize      = 0;
      this.encryptionMethod      = (byte)0x00;
      this.groupId               = (byte)0x00;
      this.dirty                 = true;   // the frame header has been created, but the values have not yet been written to the raw binary buffer
   }

   /**
    * This constructor is called when an existing frame header is being read in from an .mp3 file.  If the frame id read in is not one of the 74 frame types defined in the ID3v2.3 specification,
    * then the frame is marked as invalid, but we continue to read and parse the remainder of the header.  This allows an attempt to be made to read in the invalid frame's corresponding body,
    * at which point the entire invalid frame can be discarded and the remainder of the frames can be read in from the .mp3 file.
    * @param inputStream          input stream pointing to an ID3v2.3 frame header in the .mp3 file.
    * @throws IOException  if there is an error while reading the frame header in from the .mp3 file.
    */
   public ID3v23FrameHeader(InputStream inputStream) throws IOException
   {
      byte _byte;

      // read in the 1st byte and see if it is 0x00.
      // if it is, then we have reached the end of the frames, and have reached the padding which comes after the frames.
      if ((_byte = (byte)inputStream.read()) == 0x00)
      {
         padding = true;
      }
      else
      {
         // otherwise, this is a frame header, so read it in and parse the bytes
         byte[] data   = new byte[FRAME_HEADER_DEFAULT_SIZE - 1];
         byte[] buffer = new byte[FRAME_HEADER_MAX_SIZE];

         if (inputStream.read(data) == data.length)
         {
            // copy the bytes into a raw binary buffer
            buffer[0] = _byte;
            System.arraycopy(data, 0, buffer, 1, data.length);

            // parse the frame id
            try
            {
               String s = new String(buffer, 0, FrameType.FRAME_ID_LENGTH, Encoding.ISO_8859_1.getCharacterSet());
               frameType = FrameType.getFrameType(s);
            }
            catch (IllegalArgumentException ex)
            {
               invalidFrameId = new String(buffer, 0, FrameType.FRAME_ID_LENGTH, Encoding.ISO_8859_1.getCharacterSet());
               // continue parsing so that we can skip over this invalid frame
            }
            catch (Throwable ex)
            {
              ex.printStackTrace();
            }
            // note: java treats byte as a signed value, while the ID3v2.3 spec treats bytes as unsigned.
            //       this necessitates converting each byte to a larger value (integer) and then shifting and adding them.
            frameBodySize         = ID3v23FrameBodyUtility.bytesToInt(buffer, 4);
            tagAlterPreservation  = (buffer[8] & FRAME_HEADER_TAG_ALTER_PRESERVATION_MASK ) != 0;
            fileAlterPreservation = (buffer[8] & FRAME_HEADER_FILE_ALTER_PRESERVATION_MASK) != 0;
            readOnly              = (buffer[8] & FRAME_HEADER_READ_ONLY_MASK              ) != 0;
            compressed            = (buffer[9] & FRAME_HEADER_COMPRESSED_MASK             ) != 0;
            encrypted             = (buffer[9] & FRAME_HEADER_ENCRYPTED_MASK              ) != 0;
            belongsToGroup        = (buffer[9] & FRAME_HEADER_BELONGS_TO_GROUP_MASK       ) != 0;

            // if any additional fields are found, then calculate the size of the frame header
            int frameHeaderSize = FRAME_HEADER_DEFAULT_SIZE;

            // if the frame is compressed by zlib, get its uncompressed size
            if (compressed)
            {
               byte[] uncompressedSizeData = new byte[FRAME_HEADER_UNCOMPRESSED_SIZE];
               inputStream.read(uncompressedSizeData);
               System.arraycopy(uncompressedSizeData, 0, buffer, frameHeaderSize, uncompressedSizeData.length);
               frameHeaderSize += uncompressedSizeData.length;
               uncompressedSize = ((uncompressedSizeData[0] & 0xFF ) << 24) + ((uncompressedSizeData[1] & 0xFF) << 16) + ((uncompressedSizeData[2] & 0xFF) << 8) + (uncompressedSizeData[3] & 0xFF);
            }
            // if the frame is encrypted, get its encryption method
            if (encrypted)
            {
               encryptionMethod = (byte)inputStream.read();
               buffer[frameHeaderSize] = encryptionMethod;
               frameHeaderSize++;
            }
            // if the frame belongs to a group of frames, get its group id
            if (belongsToGroup)
            {
               groupId = (byte)inputStream.read();
               buffer[frameHeaderSize] = groupId;
               frameHeaderSize++;
            }
            // copy all the fields read into the raw binary header
            header = new byte[frameHeaderSize];
            System.arraycopy(buffer, 0, header, 0, frameHeaderSize);
         }
         else
         {
            throw new IOException("Unable to read a ID3v2.3 frame header.");
         }
      }
      dirty = false;
   }

   /**
    * copy constructor.
    * @param header    an ID3v2.3 header that was previously read in.
    */
   public ID3v23FrameHeader(ID3v23FrameHeader header)
   {
      this.header                = header.header;
      this.frameType             = header.frameType;
      this.frameBodySize         = header.frameBodySize;
      this.tagAlterPreservation  = header.tagAlterPreservation;
      this.fileAlterPreservation = header.fileAlterPreservation;
      this.readOnly              = header.readOnly;
      this.compressed            = header.compressed;
      this.encrypted             = header.encrypted;
      this.belongsToGroup        = header.belongsToGroup;
      this.uncompressedSize      = header.uncompressedSize;
      this.encryptionMethod      = header.encryptionMethod;
      this.groupId               = header.groupId;
      this.dirty                 = header.dirty;
   }

   /**
    * gets the ID3v2.3 frame type.
    * @return the frame id.
    * @see #setFrameType(FrameType)
    */
   public FrameType getFrameType()
   {
      return frameType;
   }

   /**
    * sets the frame type.
    * @param frameType   the type of ID3v2.3 frame.
    * @see #getFrameType()
    */
   public void setFrameType(FrameType frameType)
   {
      this.dirty     = true;
      this.frameType = frameType;
   }

   /**
    * gets the size of the associated frame body.
    * @return the frame body size.
    * @see #setFrameBodySize(int)
    */
   public int getFrameBodySize()
   {
      return frameBodySize;
   }

   /**
    * sets the size of the frame body.
    * @param frameBodySize the size of the frame body (in bytes).
    * @see #getFrameBodySize()
    */
   public void setFrameBodySize(int frameBodySize)
   {
      if (frameBodySize < 0)
         throw new IllegalArgumentException("Invalid ID3v2.3 frame body size, " + frameBodySize + ", in frame " + frameType.getName() + ". It must be > 0.");

      this.dirty         = true;
      this.frameBodySize = frameBodySize;
   }

   /**
    * gets whether the frame should be preserved or discarded if the frame is unknown and the tag is altered in any way.
    * @return whether the frame should be preserved or discarded if the frame is unknown and the tag is altered in any way.
    * @see #setTagAlterPreservation(boolean)
    */
   public boolean isTagAlterPreservation()
   {
      return tagAlterPreservation;
   }

   /**
    * sets whether the frame should be preserved or discarded if the frame is unknown and the tag is altered in any way.
    * @param tagAlterPreservation    the tag alteration preservation flag.
    * @see #isTagAlterPreservation()
    */
   public void setTagAlterPreservation(boolean tagAlterPreservation)
   {
      this.dirty                = true;
      this.tagAlterPreservation = tagAlterPreservation;
   }

   /**
    * gets whether the frame should be preserved or discarded if the frame is unknown and the file, excluding the tag, is altered.  This does not include when
    * the audio is completely replaced with other audio data.
    * @return the whether the frame should be preserved or discarded if the frame is unknown and the file, excluding the tag, is altered.
    * @see #setFileAlterPreservation(boolean)
    */
   public boolean isFileAlterPreservation()
   {
      return fileAlterPreservation;
   }

   /**
    * sets whether the frame should be preserved or discarded if the frame is unknown and the file, excluding the tag, is altered.  This does not apply when the
    * audio is completely replaced with other audio data.
    * @param fileAlterPreservation the file alteration preservation flag.
    * @see #isFileAlterPreservation()
    */
   public void setFileAlterPreservation(boolean fileAlterPreservation)
   {
      this.dirty                 = true;
      this.fileAlterPreservation = fileAlterPreservation;
   }

   /**
    * gets whether the contents of this frame are intended to be read only.
    * @return whether the contents of this frame are intended to be read only.
    * @see #setReadOnly(boolean)
    */
   public boolean isReadOnly()
   {
      return readOnly;
   }

   /**
    * sets whether the contents of this frame are intended to be read only.
    * @param readOnly    booleain indicating whether the contents of this frame are intended to be read only.
    * @see #isReadOnly()
    */
   public void setReadOnly(boolean readOnly)
   {
      this.dirty    = true;
      this.readOnly = readOnly;
   }

   /**
    * gets whether the frame is compressed using zlib.  If so, then the <i>uncompressedSize</i> field will contain the uncompressed size of the frame.
    * @return whether the frame is compressed using zlib.
    * @see #setCompressed(boolean)
    * @see #getUncompressedSize()
    */
   public boolean isCompressed()
   {
      return compressed;
   }

   /**
    * sets whether the frame is compressed using zlib.  If so, then the <i>uncompressedSize</i> field should be set to contain the uncompressed size of the frame.
    * Otherwise, the <i>uncompressedSize</i> field should be set to 0.
    * @param compressed   boolean indicating whether the frame is compressed using zlib.
    * @see #isCompressed()
    */
   public void setCompressed(boolean compressed)
   {
      this.dirty     = true;
      this.compressed = compressed;
   }

   /**
    * gets whether the frame is encrypted.
    * @return whether the frame is encrypted.
    * @see #setEncrypted(boolean encrypted)
    */
   public boolean isEncrypted()
   {
      return encrypted;
   }

   /**
    * sets whether the frame is encrypted.  If so, then the <i>encryptionMethod</i> field will contain the method used to encrypt the frame.
    * Otherwise, the <i>encryptionMethod</i> field should be set to 0.
    * @param encrypted   whether the frame is encrypted.
    * @see #isEncrypted()
    * @see #setEncryptionMethod(byte)
    */
   public void setEncrypted(boolean encrypted)
   {
      this.dirty     = true;
      this.encrypted = encrypted;
   }

   /**
    * gets whether the frame belongs in a group with other frames.  If it does, then the <i>groupId</i> field will contain the group's id. Every frame belonging
    * to the same group will have the same group id.
    * @return whether the frame belongs in a group with other frames.
    * @see #setBelongsToGroup(boolean)
    */
   public boolean isBelongsToGroup()
   {
      return belongsToGroup;
   }

   /**
    * sets whether the frame belongs in a group with other frames.  If it does, then the <i>groupId</i> field will contain the group's id. Every frame belonging
    * to the same group will have the same group id.
    * @param belongsToGroup   boolean indicating whether the frame belongs in a group with other frames.
    * @see #isBelongsToGroup()
    */
   public void setBelongsToGroup(boolean belongsToGroup)
   {
      this.dirty          = true;
      this.belongsToGroup = belongsToGroup;
   }

   /**
    * if the frame has been compressed using zlib as indicated by the <i>compressed</i> field, then this method returns the uncompressed size of the frame.
    * @return if <i>compressed</i> is true, then the frame's uncompressed size (in bytes) is returned.  Otherwise, 0 is returned.
    * @see #setUncompressedSize(int)
    * @see #setCompressed(boolean)
    */
   public int getUncompressedSize()
   {
      return uncompressedSize;
   }

   /**
    * sets the frame's uncompressed size as well as the <i>compressed</i> flag.  If the uncompressed size is 0, then the <i>compressed</i> flag is set to false.
    * Otherwise, it is set to true.
    * @param uncompressedSize   the frame's uncompressed size or 0 to indicate that the frame is not compressed.
    * @see #getUncompressedSize()
    * @see #setCompressed(boolean)
    */
   public void setUncompressedSize(int uncompressedSize)
   {
      if (uncompressedSize < 0)
         throw new IllegalArgumentException("Invalid uncompressed size, " + uncompressedSize + ", in ID3v2.3 frame " + frameType.getName() + ". It must be >= 0.");

      this.dirty            = true;
      this.compressed       = uncompressedSize != 0;
      this.uncompressedSize = uncompressedSize;
   }

   /**
    * if the frame has been encryped, as indicated by the <i>encrypted</i> field, then this method gets the method used to encrypt the frame.
    * @return if <i>encrypted</i> is true, then the method used to encrypt the frame is returned.  Otherwise, 0 is returned.
    * @see #setEncryptionMethod(byte)
    * @see #setEncrypted(boolean)
    */
   public byte getEncryptionMethod()
   {
      return encryptionMethod;
   }

   /**
    * sets the frame's encryption method as well as the <i>encrypted</i> flag.  If the encryption method is 0, then the <i>encrypted</i> flag is set to false.
    * Otherwise, it it set to true.
    * @param encryptionMethod   the encryption algorithm used to encrypt the frame or 0 to indicate that the frame is not encrypted.
    * @see #getEncryptionMethod()
    * @see #setEncrypted(boolean)
    */
   public void setEncryptionMethod(byte encryptionMethod)
   {
      this.dirty            = true;
      this.encrypted        = encryptionMethod != 0;
      this.encryptionMethod = encryptionMethod;
   }

   /**
    * if the frame belongs to a group, as indicated by the <i>belongsToGroup</i> field, then this method gets the id used to uniquely indentify the group.
    * @return if <i>belongsToGroup</i> is true, then the id used to uniquely indentify the group is returned.  Otherwise 0 is returned.
    * @see #setGroupId(byte)
    * @see #setBelongsToGroup(boolean)
    */
   public byte getGroupId()
   {
      return groupId;
   }

   /**
    * sets the frame's group id as well as the <i>belongsToGroup</i> flag.  If the group id is 0, then the <i>belongsToGroup</i> flag is set to false.
    * Otherwise, it it set to true.
    * @param groupId    the id that will uniquely indentify a frame group or 0 to indicate that the frame does not belong to a group of frames.
    * @see #getGroupId()
    * @see #setBelongsToGroup(boolean)
    */
   public void setGroupId(byte groupId)
   {
      this.dirty          = true;
      this.belongsToGroup = groupId != 0;
      this.groupId        = groupId;
   }

   /**
    * gets the size (in bytes) of the frame header.
    * @return the size (in bytes) of the frame header.
    * @see #getFrameBodySize()
    */
   public int getSize()
   {
      return header.length;
   }

   /**
    * gets whether the frame header has been modified sinze the last time it was saved.
    * @return whether any value(s) in the frame's header have been modified.
    * @see #setBuffer()
    */
   public boolean isDirty()
   {
      return dirty;
   }

   /**
    * although an ID3v2.3 tag header specifies the size of the tag, it does not specifiy how many frames there are, the total size of the frames, nor the size of the padding.
    * Thus, in order to detect when the frames have ended and the padding has begun, one must read in a single byte where a frame header should be and determine if the byte
    * is 0x00.  If it is, then the end of the frames have been reached and the padding has begun.  Thus, this boolean indicates whether this condition has occurred, and thus
    * there is no longer any need to continue to try and read any more frames.
    * @return whether the last frame in the ID3v2.3 tag has been read and the padding has begun.
    * @see ID3v23Frame
    */
   public boolean isPadding()
   {
      return padding;
   }

   /**
    * if the frame id read in is not a valid ID3v2.3 frame id, then this field is set so that the error can be reported later
    * after the entire frame has been read in (and discarded) so that we may continue processing the remaining frames.
    * @return the invalid ID3v2.3 frame id that was encountered while reading in the frame header from the .mp3 file.
    */
   public String getInvalidFrameId()
   {
      return invalidFrameId;
   }

   /**
    * save the frame header to the .mp3 file.
    * @param outputStream   output stream pointing to the starting location of the ID3v2.3 frame header within the .mp3 file.
    * @throws IOException   if there was an error writing the ID3v2.3 frame header to the .mp3 file.
    */
   public void save(OutputStream outputStream) throws IOException
   {
      if (dirty)
         throw new IllegalStateException("ID3v2.3 frame " + frameType.getName() + " has been modified and requires setBuffer() to be called before it can be saved.");

      outputStream.write(header);
   }

   /**
    * save the frame header to the .mp3 file.
    * @param file   random access file pointing to the starting location of the ID3v2.3 frame header within the .mp3 file.
    * @throws IOException   if there was an error writing the ID3v2.3 frame header to the .mp3 file.
    */
   public void save(RandomAccessFile file) throws IOException
   {
      if (dirty)
         throw new IllegalStateException("ID3v2.3 frame " + frameType.getName() + " has been modified and requires setBuffer() to be called before it can be saved.");

      file.write(header);
   }

   /**
    * if the frame header's values have been modified, then resize the raw binary buffer and store the new values there.
    * When finished, the dirty flag is reset to indicate that the buffer is up to date, and the frame is now ready to be saved to the .mp3 file.
    */
   public void setBuffer()
   {
      int frameHeaderSize = FRAME_HEADER_DEFAULT_SIZE + (compressed     ? FRAME_HEADER_UNCOMPRESSED_SIZE : 0) +
                                                        (encrypted      ? FRAME_HEADER_ENCRYPTED_SIZE    : 0) +
                                                        (belongsToGroup ? FRAME_HEADER_GROUP_ID_SIZE     : 0);

      byte[] frameIdBytes = ID3v23FrameBodyUtility.stringToBytes(Encoding.ISO_8859_1, frameType.getId());

      header = new byte[frameHeaderSize];
      System.arraycopy(frameIdBytes, 0, header, 0, frameIdBytes.length);
      header[4] = (byte)((frameBodySize & 0xFF000000) >> 24);
      header[5] = (byte)((frameBodySize & 0x00FF0000) >> 16);
      header[6] = (byte)((frameBodySize & 0x0000FF00) >>  8);
      header[7] = (byte)((frameBodySize & 0x000000FF)      );
      header[8] = (byte)(tagAlterPreservation  ? header[8] | FRAME_HEADER_TAG_ALTER_PRESERVATION_MASK  : header[8] & ~FRAME_HEADER_TAG_ALTER_PRESERVATION_MASK );
      header[8] = (byte)(fileAlterPreservation ? header[8] | FRAME_HEADER_FILE_ALTER_PRESERVATION_MASK : header[8] & ~FRAME_HEADER_FILE_ALTER_PRESERVATION_MASK);
      header[8] = (byte)(readOnly              ? header[8] | FRAME_HEADER_READ_ONLY_MASK               : header[8] & ~FRAME_HEADER_READ_ONLY_MASK              );
      header[9] = (byte)(compressed            ? header[9] | FRAME_HEADER_COMPRESSED_MASK              : header[9] & ~FRAME_HEADER_COMPRESSED_MASK             );
      header[9] = (byte)(encrypted             ? header[9] | FRAME_HEADER_ENCRYPTED_MASK               : header[9] & ~FRAME_HEADER_ENCRYPTED_MASK              );
      header[9] = (byte)(belongsToGroup        ? header[9] | FRAME_HEADER_BELONGS_TO_GROUP_MASK        : header[9] & ~FRAME_HEADER_BELONGS_TO_GROUP_MASK       );

      // if there are any additional fields, then write them to the raw binary header
      if (header.length != FRAME_HEADER_DEFAULT_SIZE)
      {
         int index = FRAME_HEADER_DEFAULT_SIZE;
         if (compressed)
         {
            header[10] = (byte)((uncompressedSize & 0xFF000000) >> 24);
            header[11] = (byte)((uncompressedSize & 0x00FF0000) >> 16);
            header[12] = (byte)((uncompressedSize & 0x0000FF00) >>  8);
            header[13] = (byte)((uncompressedSize & 0x000000FF)      );
            index += 4;
         }
         if (encrypted)
         {
            header[index] = encryptionMethod;
            index++;
         }
         if (belongsToGroup)
         {
            header[index] = groupId;
            index++;
         }
      }
      dirty = false;
   }

   /**
    * gets a string representation of an ID3v2.3 frame's header.
    * @return a string representation of an ID3v2.3 frame's header.
    */
   public String toString()
   {
      StringBuffer buffer = new StringBuffer();

      buffer.append("frame header\n");
      buffer.append("   bytes..................: " + ID3v23FrameBodyUtility.hex(header, 0) + "\n");
      buffer.append("   frame type.............: " + frameType             + "\n");
      buffer.append("   frame header size......: " + header.length         + "\n");
      buffer.append("   frame body size........: " + frameBodySize         + "\n");
      buffer.append("   tag  alter preservation: " + tagAlterPreservation  + "\n");
      buffer.append("   file alter preservation: " + fileAlterPreservation + "\n");
      buffer.append("   read only..............: " + readOnly              + "\n");
      buffer.append("   compression............: " + compressed            + "\n");
      buffer.append("   encryption.............: " + encrypted             + "\n");
      buffer.append("   grouping identity......: " + belongsToGroup        + "\n");
      buffer.append("   uncompressed size......: " + uncompressedSize      + "\n");
      buffer.append("   encryption method......: " + encryptionMethod      + "\n");
      buffer.append("   group Id...............: " + groupId               + "\n");

      return buffer.toString();
   }
}

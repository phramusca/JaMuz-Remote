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
 * A <i>linked information</i> frame body is associated with an ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#LINKED_INFORMATION LINK} {@link com.beaglebuddy.id3.v23.ID3v23Frame frame} which is used to link information from another ID3v2 tag
 * that might reside in another audio file or alone in a binary file.
 * The <i>linked information</i> frame body contains the following fields:
 * <table class="beaglebuddy">
 *    <caption><b>Linked Information Frame Body Fields</b></caption>
 *    <thead>
 *       <tr><th class="beaglebuddy">&nbsp;</th><th class="beaglebuddy">Field</th><th class="beaglebuddy">Description</th></tr>
 *    </thead>
 *    <tbody>
 *       <tr><td class="beaglebuddy">1. </td><td class="beaglebuddy">{@link com.beaglebuddy.id3.enums.v23.FrameType linkedFrameType}</td><td class="beaglebuddy">type of ID3v2.3 frame being linked.            </td></tr>
 *       <tr><td class="beaglebuddy">2. </td><td class="beaglebuddy">url                                                        </td><td class="beaglebuddy">reference to the file where the frame is given.</td></tr>
 *       <tr><td class="beaglebuddy">3. </td><td class="beaglebuddy">additionalIdData                                           </td><td class="beaglebuddy">this optional field is used to help locate a specific frame in another file when the frame type alone is not sufficient to uniquely identify it. For example, an ID3v2.3 tag may contain several images, such as the
 *                                                                                                                                                           {@link com.beaglebuddy.id3.enums.PictureType#FRONT_COVER CD front cover}, {@link com.beaglebuddy.id3.enums.PictureType#BACK_COVER CD back cover}, image of a {@link com.beaglebuddy.id3.enums.PictureType#DURING_PERFORMANCE live performance}, etc.
 *                                                                                                                                                           Thus, the {@link com.beaglebuddy.id3.enums.v23.FrameType#ATTACHED_PICTURE attached picture} frame type alone is not enough to uniquely identify a single, specific frame.
 *                                                                                                                                                           <p class="beaglebuddy">
 *                                                                                                                                                           The frames {@link com.beaglebuddy.id3.enums.v23.FrameType#INVOLVED_PEOPLE_LIST IPLS}, {@link com.beaglebuddy.id3.enums.v23.FrameType#MUSIC_CD_IDENTIFIER MCDI},
 *                                                                                                                                                           {@link com.beaglebuddy.id3.enums.v23.FrameType#EVENT_TIMING_CODES ETCO}, {@link com.beaglebuddy.id3.enums.v23.FrameType#MPEG_LOCATION_LOOKUP_TABLE MLLT}, {@link com.beaglebuddy.id3.enums.v23.FrameType#SYNCHRONIZED_TEMPO_CODES SYTC},
 *                                                                                                                                                           {@link com.beaglebuddy.id3.enums.v23.FrameType#RELATIVE_VOLUME_ADJUSTMENT RVAD}, {@link com.beaglebuddy.id3.enums.v23.FrameType#EQUALIZATION EQUA}, {@link com.beaglebuddy.id3.enums.v23.FrameType#REVERB RVRB},
 *                                                                                                                                                           {@link com.beaglebuddy.id3.enums.v23.FrameType#RECOMMENDED_BUFFER_SIZE RBUF}, the text information frames ({@link com.beaglebuddy.id3.enums.v23.FrameType#ALBUM_TITLE TALB}, {@link com.beaglebuddy.id3.enums.v23.FrameType#BEATS_PER_MINUTE TBPM},
 *                                                                                                                                                           {@link com.beaglebuddy.id3.enums.v23.FrameType#COMPOSER TCOM}, {@link com.beaglebuddy.id3.enums.v23.FrameType#CONTENT_TYPE TCON}, {@link com.beaglebuddy.id3.enums.v23.FrameType#COPYRIGHT_MESSAGE TCOP},
 *                                                                                                                                                           {@link com.beaglebuddy.id3.enums.v23.FrameType#DATE TDAT}, {@link com.beaglebuddy.id3.enums.v23.FrameType#PLAYLIST_DELAY TDLY}, {@link com.beaglebuddy.id3.enums.v23.FrameType#ENCODED_BY TENC},
 *                                                                                                                                                           {@link com.beaglebuddy.id3.enums.v23.FrameType#LYRICIST TEXT}, {@link com.beaglebuddy.id3.enums.v23.FrameType#FILE_TYPE TFLT}, {@link com.beaglebuddy.id3.enums.v23.FrameType#TIME TIME},
 *                                                                                                                                                           {@link com.beaglebuddy.id3.enums.v23.FrameType#CONTENT_GROUP_DESCRIPTION TIT1}, {@link com.beaglebuddy.id3.enums.v23.FrameType#SONG_TITLE TIT2}, {@link com.beaglebuddy.id3.enums.v23.FrameType#SUBTITLE_REFINEMENT TIT3},
 *                                                                                                                                                           {@link com.beaglebuddy.id3.enums.v23.FrameType#INITIAL_KEY TKEY}, {@link com.beaglebuddy.id3.enums.v23.FrameType#LANGUAGE TLAN}, {@link com.beaglebuddy.id3.enums.v23.FrameType#LENGTH TLEN},
 *                                                                                                                                                           {@link com.beaglebuddy.id3.enums.v23.FrameType#MEDIA_TYPE TMED}, {@link com.beaglebuddy.id3.enums.v23.FrameType#ORIGINAL_ALBUM_TITLE TOAL}, {@link com.beaglebuddy.id3.enums.v23.FrameType#ORIGINAL_FILENAME TOFN},
 *                                                                                                                                                           {@link com.beaglebuddy.id3.enums.v23.FrameType#ORIGINAL_LYRICIST TOLY}, {@link com.beaglebuddy.id3.enums.v23.FrameType#ORIGINAL_ARTIST TOPE}, {@link com.beaglebuddy.id3.enums.v23.FrameType#ORIGINAL_RELEASE_YEAR TORY},
 *                                                                                                                                                           {@link com.beaglebuddy.id3.enums.v23.FrameType#FILE_OWNER TOWN}, {@link com.beaglebuddy.id3.enums.v23.FrameType#LEAD_PERFORMER TPE1}, {@link com.beaglebuddy.id3.enums.v23.FrameType#BAND TPE2},
 *                                                                                                                                                           {@link com.beaglebuddy.id3.enums.v23.FrameType#CONDUCTOR TPE3}, {@link com.beaglebuddy.id3.enums.v23.FrameType#INTERPRETED_MODIFIED_BY TPE4}, {@link com.beaglebuddy.id3.enums.v23.FrameType#PART_OF_A_SET TPOS},
 *                                                                                                                                                           {@link com.beaglebuddy.id3.enums.v23.FrameType#PUBLISHER TPUB}, {@link com.beaglebuddy.id3.enums.v23.FrameType#TRACK_NUMBER TRCK}, {@link com.beaglebuddy.id3.enums.v23.FrameType#RECORDING_DATES TRDA},
 *                                                                                                                                                           {@link com.beaglebuddy.id3.enums.v23.FrameType#INTERNET_RADIO_STATION_NAME TRSN}, {@link com.beaglebuddy.id3.enums.v23.FrameType#INTERNET_RADIO_STATION_OWNER TRSO}, {@link com.beaglebuddy.id3.enums.v23.FrameType#SIZE TSIZ},
 *                                                                                                                                                           {@link com.beaglebuddy.id3.enums.v23.FrameType#INTERNATIONAL_STANDARD_RECORDING_CODE TSRC}, {@link com.beaglebuddy.id3.enums.v23.FrameType#SOFTWARE_HARDWARE_ENCODING_SETTINGS TSSE}, {@link com.beaglebuddy.id3.enums.v23.FrameType#YEAR TYER}),
 *                                                                                                                                                           and the URL link frames ({@link com.beaglebuddy.id3.enums.v23.FrameType#COMMERCIAL_INFORMATION WCOM}, {@link com.beaglebuddy.id3.enums.v23.FrameType#COPYRIGHT_LEGAL_INFORMATION WCOP}, {@link com.beaglebuddy.id3.enums.v23.FrameType#OFFICIAL_AUDIO_FILE_WEBPAGE WOAF},
 *                                                                                                                                                           {@link com.beaglebuddy.id3.enums.v23.FrameType#OFFICIAL_ARTIST_WEBPAGE WOAR}, {@link com.beaglebuddy.id3.enums.v23.FrameType#OFFICIAL_AUDIO_SOURCE_WEBPAGE WOAS}, {@link com.beaglebuddy.id3.enums.v23.FrameType#OFFICIAL_INTERNET_RADIO_STATION_HOMEPAGE WORS},
 *                                                                                                                                                           {@link com.beaglebuddy.id3.enums.v23.FrameType#PAYMENT WPAY}, {@link com.beaglebuddy.id3.enums.v23.FrameType#PUBLISHERS_OFFICIAL_WEBPAGE WPUB}) may be linked without any additional ID data.
 *                                                                                                                                                           </p>
 *                                                                                                                                                           <p class="beaglebuddy">
 *                                                                                                                                                           The {@link com.beaglebuddy.id3.enums.v23.FrameType#AUDIO_ENCRYPTION AENC}, {@link com.beaglebuddy.id3.enums.v23.FrameType#ATTACHED_PICTURE APIC}, {@link com.beaglebuddy.id3.enums.v23.FrameType#GENERAL_ENCAPSULATED_OBJECT GEOB} and
 *                                                                                                                                                           {@link com.beaglebuddy.id3.enums.v23.FrameType#USER_DEFINED_TEXT_INFORMATION TXXX} frames may be linked with the content descriptor as additional ID data.
 *                                                                                                                                                           </p>
 *                                                                                                                                                           <p class="beaglebuddy">
 *                                                                                                                                                           The {@link com.beaglebuddy.id3.enums.v23.FrameType#COMMENTS COMM}, {@link com.beaglebuddy.id3.enums.v23.FrameType#SYNCHRONIZED_LYRIC_TEXT SYLT} and {@link com.beaglebuddy.id3.enums.v23.FrameType#UNSYCHRONIZED_LYRICS USLT}
 *                                                                                                                                                           frames may be linked with the three character ISO-639-2 {@link com.beaglebuddy.id3.enums.Language language code} followed by a description as additional ID data.
 *                                                                                                                                                           </p>
 *                                                                                                                                                           </td></tr>
 *    </tbody>
 * </table>
 * </p>
 * <p class="beaglebuddy">
 * There may be more than one <i>linked information</i> frame, but only one with the same contents.
 * </p>
 * @see com.beaglebuddy.id3.v23.ID3v23Frame
 * @see <a href="http://id3.org/id3v2.3.0"         target="_blank">ID3 tag version 2.3.0 standard</a>
 * @see <a href="http://en.wikipedia.org/wiki/ID3" target="_blank">wikipedia history of ID3 tags</a>
 */
public class ID3v23FrameBodyLinkedInformation extends ID3v23FrameBody
{
   // data members
   FrameType linkedFrameType;
   String    url;
   byte[]    additionalIdData;



   /**
    * The default constructor is called when creating a new frame.
    * The default values used are:
    * <ul>
    *    <li>{@link com.beaglebuddy.id3.enums.v23.FrameType#ATTACHED_PICTURE attached picture} frame type</li>
    *    <li>empty URL</li>
    *    <li>no additional id data</li>
    * </ul>
    */
   public ID3v23FrameBodyLinkedInformation()
   {
      this(FrameType.ATTACHED_PICTURE, " ", new byte[0]);
   }

   /**
    * This constructor is called when creating a new frame.
    * <br/><br/>
    * @param linkedFrameType          the type of ID3v2.3 linked frame.
    * @param url                the url which holds a reference to the file which contains the linked frame.
    * @param additionalIdData   the extra data which together with the frame type uniquely identifies a single frame in another file.
    */
   public ID3v23FrameBodyLinkedInformation(FrameType linkedFrameType, String url, byte[] additionalIdData)
   {
      super(FrameType.LINKED_INFORMATION);

      setLinkedFrameType (linkedFrameType);
      setURL             (url);
      setAdditionalIdData(additionalIdData);
      dirty = true;      // values have not yet been saved to the frame body's internal byte buffer
   }

   /**
    * This constructor is called when reading in an existing frame from an .mp3 file.  This is then followed by a call to the parse() method.
    * @param inputStream    input stream pointing to a linked information frame body in the .mp3 file.
    * @param frameBodySize  size (in bytes) of the frame's body.
    * @throws IOException   if there is an error while reading the frame body.
    */
   public ID3v23FrameBodyLinkedInformation(InputStream inputStream, int frameBodySize) throws IOException
   {
      super(inputStream, FrameType.LINKED_INFORMATION, frameBodySize);
   }

   /**
    * parses the raw bytes of the frame body and stores the parsed values in the frame's fields.
    * @throws IllegalArgumentException  if an invalid value is detected while parsing the frame body's raw bytes.
    */
   @Override
   public void parse() throws IllegalArgumentException
   {
      setLinkedFrameType(FrameType.getFrameType(new String(buffer, 0, FrameType.FRAME_ID_LENGTH, Encoding.ISO_8859_1.getCharacterSet())));
      nullTerminatorIndex = getNextNullTerminator(FrameType.FRAME_ID_LENGTH, Encoding.ISO_8859_1);
      setURL(new String(buffer, FrameType.FRAME_ID_LENGTH, nullTerminatorIndex - FrameType.FRAME_ID_LENGTH, Encoding.ISO_8859_1.getCharacterSet()).trim());
      nullTerminatorIndex +=  Encoding.ISO_8859_1.getNumBytesInNullTerminator();
      additionalIdData    = new byte[buffer.length - nullTerminatorIndex];
      System.arraycopy(buffer, nullTerminatorIndex, additionalIdData, 0, additionalIdData.length);
      dirty = false;    // we just read in the frame info, so the frame body's internal byte buffer is up to date
   }

   /**
    * gets the type of ID3v2.3 frame that is linked.
    * @return the type of ID3v2.3 frame that is linked.
    * @see #setLinkedFrameType(FrameType)
    */
   public FrameType getLinkedFrameType()
   {
      return linkedFrameType;
   }

   /**
    * sets the type of ID3v2.3 frame that is linked.
    * @param linkedFrameType  the type of ID3v2.3 frame that is linked.
    * @see #getFrameType()
    */
   public void setLinkedFrameType(FrameType linkedFrameType)
   {
      if (linkedFrameType == null)
         throw new IllegalArgumentException("The linked frame type field in the " + frameType.getId() + " frame may not be empty.");

      this.linkedFrameType = linkedFrameType;
      this.dirty           = true;
   }

   /**
    * gets the url which holds a reference to the file which contains the linked frame.
    * @return the url which holds a reference to the file which contains the linked frame.
    * @see #setURL(String)
    */
   public String getURL()
   {
      return url;
   }

   /**
    * sets the url which holds a reference to the file which contains the linked frame.
    * @param url   the url which holds a reference to the file which contains the linked frame.
    * @see #getURL()
    */
   public void setURL(String url)
   {
      if (url == null || url.length() == 0)
         throw new IllegalArgumentException("The url field in the " + frameType.getId() + " frame may not be empty.");

      this.url   = url;
      this.dirty = true;
   }

   /**
    * gets the additional id data which is used to help locate a specific frame in another file when the frame type alone is not sufficient to uniquely identify it.
    * @return the extra data which together with the frame type uniquely identifies a single frame in another file.
    * @see #setAdditionalIdData(byte[])
    */
   public byte[] getAdditionalIdData()
   {
      return additionalIdData;
   }

   /**
    * sets the additional id data which is used to help locate a specific frame in another file when the frame type alone is not sufficient to uniquely identify it.
    * @param additionalIdData   the extra data which together with the frame type uniquely identifies a single frame in another file.
    * @see #getAdditionalIdData()
    */
   public void setAdditionalIdData(byte[] additionalIdData)
   {
      this.additionalIdData = additionalIdData == null ? new byte[0] : additionalIdData;
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
         byte[] frameIdBytes = linkedFrameType.getId().getBytes(Encoding.ISO_8859_1.getCharacterSet());
         byte[] urlBytes     = stringToBytes(Encoding.ISO_8859_1, url);
         int    index        = 0;

         buffer = new byte[frameIdBytes.length + urlBytes.length + additionalIdData.length];
         System.arraycopy(frameIdBytes    , 0, buffer, index, frameIdBytes    .length);
         index += frameIdBytes.length;
         System.arraycopy(urlBytes        , 0, buffer, index, urlBytes        .length);
         index += urlBytes.length;
         System.arraycopy(additionalIdData, 0, buffer, index, additionalIdData.length);

         dirty = false;    // data has already been saved
      }
   }

   /**
    * gets a string representation of the <i>linked information</i> frame body showing all of the frame's fields and their values.
    * @return a string representation of the frame body.
    */
   @Override
   public String toString()
   {
      StringBuffer buffer = new StringBuffer();

      buffer.append("frame body: linked information\n");
      buffer.append("   bytes...........: " + this.buffer.length        + " bytes\n");
      buffer.append("                     " + hex(this.buffer, 21)      + "\n");
      buffer.append(" frame type........: " + linkedFrameType           + "\n");
      buffer.append(" url...............: " + url                       + "\n");
      buffer.append(" additional id data: " + hex(additionalIdData, 21) + "\n");

      return buffer.toString();
   }
}

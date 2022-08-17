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
 * A <i>url link</i> frame body is associated with the ID3v2.3 {@link com.beaglebuddy.id3.v23.ID3v23Frame frames} listed below, which contain dynamic data such as webpages with touring
 * information, price information or plain ordinary news.  All <i>url link</i> frame identifiers begins with "W". Only URL link frame identifiers begins with "W".
 * The <i>url link</i> frame body contains the following fields:
 * <table class="beaglebuddy">
 *    <caption><b>URL Link Frame Body Fields</b></caption>
 *    <thead>
 *       <tr><th class="beaglebuddy">&nbsp;</th><th class="beaglebuddy">Field</th><th class="beaglebuddy">Description</th></tr>
 *    </thead>
 *    <tbody>
 *       <tr><td class="beaglebuddy">1. </td><td class="beaglebuddy">url</td><td class="beaglebuddy">url of a website whose purpose depends on the type of text frame.  see the list below.                     </td></tr>
 *    </tbody>
 * </table>
 * </p>
 * <br/><br/>
 * <p class="beaglebuddy">
 *    <table class="beaglebuddy">
 *       <caption><b>URL Link Frames</b></caption>
 *       <thead>
 *          <tr><th class="beaglebuddy_text_frame_id">Frame Id</th><th class="beaglebuddy">Description</th></tr>
 *       </thead>
 *       <tbody>
 *          <tr><td class="beaglebuddy">WCOM</td><td class="beaglebuddy">The <i>Commercial information</i> frame is a URL pointing at a webpage with information such as where the album can be bought. There may be more than one "WCOM" frame in a tag, but not with the same content. </td></tr>
 *          <tr><td class="beaglebuddy">WCOP</td><td class="beaglebuddy">The <i>Copyright/Legal information</i> frame is a URL pointing at a webpage where the terms of use and ownership of the file is described. </td></tr>
 *          <tr><td class="beaglebuddy">WOAF</td><td class="beaglebuddy">The <i>Official audio file webpage</i> frame is a URL pointing at a file specific webpage. </td></tr>
 *          <tr><td class="beaglebuddy">WOAR</td><td class="beaglebuddy">The <i>Official artist/performer webpage</i> frame is a URL pointing at the artists official webpage. There may be more than one "WOAR" frame in a tag if the audio contains more than one performer, but not with the same content. </td></tr>
 *          <tr><td class="beaglebuddy">WOAS</td><td class="beaglebuddy">The <i>Official audio source webpage</i> frame is a URL pointing at the official webpage for the source of the audio file, e.g. a movie. </td></tr>
 *          <tr><td class="beaglebuddy">WORS</td><td class="beaglebuddy">The <i>Official internet radio station homepage</i> contains a URL pointing at the homepage of the internet radio station. </td></tr>
 *          <tr><td class="beaglebuddy">WPAY</td><td class="beaglebuddy">The <i>Payment</i> frame is a URL pointing at a webpage that will handle the process of paying for this file. </td></tr>
 *          <tr><td class="beaglebuddy">WPUB</td><td class="beaglebuddy">The <i>Publishers official webpage</i> frame is a URL pointing at the official wepage for the publisher. </td></tr>
 *       </tbody>
 *    </table>
 * </p>
 * <table class="beaglebuddy">
 * There may only be one <i>url link</i> frame of its kind in a ID3v2.3 {@link com.beaglebuddy.id3.v23.ID3v23Tag tag}, except when stated otherwise in the frame description.
 * </p>
 * @see com.beaglebuddy.id3.v23.ID3v23Frame
 * @see <a href="http://id3.org/id3v2.3.0"         target="_blank">ID3 tag version 2.3.0 standard</a>
 * @see <a href="http://en.wikipedia.org/wiki/ID3" target="_blank">wikipedia history of ID3 tags</a>
 */
public class ID3v23FrameBodyURLLink extends ID3v23FrameBody
{
   // data members
   private String url;



   /**
    * This constructor is called when creating a new frame.
    * @param frameType   It must be one of the following 8 ID3v2.3 URL Link frame types:<br/><br/>
    * <table class="beaglebuddy">
    *    <caption><b>ID3v2.3 URL Link Frame Types</b></caption>
    *    <thead>
    *       <tr><th class="beaglebuddy">&nbsp;</th><th class="beaglebuddy">Frame Type</th><th class="beaglebuddy">Frame id</th><th class="beaglebuddy">Description</th></tr>
    *    </thead>
    *    <tbody>
    *       <tr><td class="beaglebuddy">1.</td><td class="beaglebuddy">COMMERCIAL_INFORMATION                  </td><td class="beaglebuddy">WCOM</td><td class="beaglebuddy">commercial information                  </td></tr>
    *       <tr><td class="beaglebuddy">2.</td><td class="beaglebuddy">COPYRIGHT_LEGAL_INFORMATION             </td><td class="beaglebuddy">WCOP</td><td class="beaglebuddy">copyright/legal information             </td></tr>
    *       <tr><td class="beaglebuddy">3.</td><td class="beaglebuddy">OFFICIAL_AUDIO_FILE_WEBPAGE             </td><td class="beaglebuddy">WOAF</td><td class="beaglebuddy">official audio file webpage             </td></tr>
    *       <tr><td class="beaglebuddy">4.</td><td class="beaglebuddy">OFFICIAL_ARTIST_WEBPAGE                 </td><td class="beaglebuddy">WOAR</td><td class="beaglebuddy">official artist/performer webpage       </td></tr>
    *       <tr><td class="beaglebuddy">5.</td><td class="beaglebuddy">OFFICIAL_AUDIO_SOURCE_WEBPAGE           </td><td class="beaglebuddy">WOAS</td><td class="beaglebuddy">official audio source webpage           </td></tr>
    *       <tr><td class="beaglebuddy">6.</td><td class="beaglebuddy">OFFICIAL_INTERNET_RADIO_STATION_HOMEPAGE</td><td class="beaglebuddy">WORS</td><td class="beaglebuddy">official internet radio station homepage</td></tr>
    *       <tr><td class="beaglebuddy">7.</td><td class="beaglebuddy">PAYMENT                                 </td><td class="beaglebuddy">WPAY</td><td class="beaglebuddy">payment                                 </td></tr>
    *       <tr><td class="beaglebuddy">8.</td><td class="beaglebuddy">PUBLISHERS_OFFICIAL_WEBPAGE             </td><td class="beaglebuddy">WPUB</td><td class="beaglebuddy">publishers official webpage             </td></tr>
    *    </tbody>
    * </table>
    */
   public ID3v23FrameBodyURLLink(FrameType frameType)
   {
      this(frameType, " ");
   }

   /**
    * This constructor is called when creating a new frame.
    * <br/><br/>
    * @param frameType   one of the 8 ID3v2.3 URL Link frame types.  See {@link #ID3v23FrameBodyURLLink(FrameType)}
    * @param url         URL.
    */
   public ID3v23FrameBodyURLLink(FrameType frameType, String url)
   {
      super(frameType);

      setURL(url);
      dirty = true;      // values have not yet been saved to the frame body's internal byte buffer
   }

   /**
    * This constructor is called when reading in an existing frame from an .mp3 file.  This is then followed by a call to the parse() method.
    * @param inputStream    input stream pointing to a URL link frame body in the .mp3 file.
    * @param frameType      the type of ID3v2.3 frame.
    * @param frameBodySize  size (in bytes) of the url link frame's body.
    * @throws IOException   if there is an error while reading the frame body.
    */
   public ID3v23FrameBodyURLLink(InputStream inputStream, FrameType frameType, int frameBodySize) throws IOException
   {
      super(inputStream, frameType,  frameBodySize);
   }

   /**
    * parses the raw bytes of the frame body and stores the parsed values in the frame's fields.
    * @throws IllegalArgumentException  if an invalid value is detected while parsing the frame body's raw bytes.
    */
   @Override
   public void parse() throws IllegalArgumentException
   {
      url   = new String(buffer, 0, buffer.length, Encoding.ISO_8859_1.getCharacterSet()).trim();
      dirty = false;    // we just read in the frame info, so the frame body's internal byte buffer is up to date
   }

   /**
    * gets the url.
    * @return the url.
    * @see #setURL(String)
    */
   public String getURL()
   {
      return url;
   }

   /**
    * sets the url.
    * @param url   the url.
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
    * If the frame body's values have been modified, then resize the raw binary buffer and store the new values there.
    * When finished, the dirty flag is reset to indicate that the buffer is up to date, and the frame is now ready to be saved to the .mp3 file.
    */
   @Override
   public void setBuffer()
   {
      if (isDirty())
      {
         byte[] urlBytes = stringToBytes(Encoding.ISO_8859_1, url);
         buffer = new byte[urlBytes.length];

         System.arraycopy(urlBytes, 0, buffer, 0, urlBytes.length);
         dirty = false;
      }
   }

   /**
    * gets a string representation of the <i>url link</i> frame body showing all of the frame's fields and their values.
    * @return a string representation of the ID3v2.3 tag's url link frame.
    */
   @Override
   public String toString()
   {
      StringBuffer buffer = new StringBuffer();

      buffer.append("frame body: URL link\n");
      buffer.append("   bytes: " + this.buffer.length   + " bytes\n");
      buffer.append("          " + hex(this.buffer, 10) + "\n");
      buffer.append("   url..: " + url + "\n");

      return buffer.toString();
   }
}

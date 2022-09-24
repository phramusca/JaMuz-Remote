package com.beaglebuddy.id3.v1;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import com.beaglebuddy.exception.ParseException;
import com.beaglebuddy.id3.enums.Genre;



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
 * This class provides methods for reading and writing the <a href="http://id3.org/ID3v1">ID3v1</a> and <a href="http://id3.org/ID3v1">ID3v1.1</a> tags.  This tag is 128
 * bytes long, and is found at the end of the .mp3 file as shown below. The format of the tag is very simple, as there are eight fixed length fields.  All of the fields
 * are ISO-8859-1 encoded strings with the exception of the <i>genre</i> and the <i>track</i> fields.  The <i>genre</i> is a number which is an index into a predefined
 * list of {@link #setGenre(byte) music genres}, or categories if you like. For example, a <i>genre</i> value of 1 indicates that the song
 * is "Classic Rock".  Finally, the <i>track</i> is stored within the last byte of the <i>comments</i> field.  When the track number is stored there, the tag is
 * considered to be version ID3v1.1.
 * </p>
 * <p>
 * <table border="0">
 *    <tbody>
 *       <tr>
 *          <td class="vert_align_top">
 *             <img src="../../../../resources/mp3_format_ID3v2.3.gif" height="550" width="330" alt="mp3 ID3v2.3 format" usemap="#id3v23_map"/>
 *          </td>
 *          <td class="vert_align_top">
 *             &nbsp;
 *          </td>
 *          <td class="vert_align_top">
 *             <table class="beaglebuddy">
 *                <caption>ID1v1 Tag Format</caption>
 *                <tr><th class="beaglebuddy">Field                                  </th><th class="beaglebuddy">Size (in bytes)</th></tr>
 *                <tr><td class="beaglebuddy">Tag Id                                 </td><td class="beaglebuddy">3 </td></tr>
 *                <tr><td class="beaglebuddy">Song Title                             </td><td class="beaglebuddy">30</td></tr>
 *                <tr><td class="beaglebuddy">Artist                                 </td><td class="beaglebuddy">30</td></tr>
 *                <tr><td class="beaglebuddy">Album                                  </td><td class="beaglebuddy">30</td></tr>
 *                <tr><td class="beaglebuddy">Year                                   </td><td class="beaglebuddy">4 </td></tr>
 *                <tr><td class="beaglebuddy">Comments                               </td><td class="beaglebuddy">30</td></tr>
 *                <tr><td class="beaglebuddy">Track                                  </td><td class="beaglebuddy">0 </td></tr>
 *                <tr><td class="beaglebuddy">{@link com.beaglebuddy.id3.enums.Genre}</td><td class="beaglebuddy">1 </td></tr>
 *             </table>
 *          </td>
 *       </tr>
 *    </tbody>
 * </table>
 * <map name="id3v23_map">
 *    <area shape="rect" coords=" 230, 145, 300, 165" href="../v23/ID3v23Tag.html"               alt="ID3v2.3 Tag"/>
 *    <area shape="rect" coords="   6,  42, 198,  75" href="../v23/ID3v23TagHeader.html"         alt="ID3v2.3 Tag Header"/>
 *    <area shape="rect" coords="   6,  76, 198, 108" href="../v23/ID3v23TagExtendedHeader.html" alt="ID3v2.3 Tag Extended Header"/>
 *    <area shape="rect" coords="   6, 109, 198, 250" href="../v23/ID3v23Frame.html"             alt="ID3v2.3 Frame""/>
 *    <area shape="rect" coords="   6, 287, 198, 374" href="../../mpeg/MPEGFrame.html"           alt="MPEG Audio Frame"/>
 *    <area shape="rect" coords="   6, 375, 198, 425" href="../../lyrics3/Lyrics3v2Tag.html"     alt="Lyrics3 Tag"/>
 *    <area shape="rect" coords="   6, 426, 198, 479" href="../../ape/APETag.html"               alt="APE Tag"/>
 *    <area shape="rect" coords="   6, 480, 198, 530" href="ID3v1Tag.html"                       alt="ID3V1 Tag"/>
 * </map>
 * </p>
 * <br/>
 * @see <a href="http://www.id3.org/ID3v1"         target="_blank">ID3 tag version 1 standard</a>
 * @see <a href="http://en.wikipedia.org/wiki/ID3" target="_blank">wikipedia history of ID3 tags</a>
 */
public class ID3v1Tag
{
   // class mnemonics
                                                                    /** character set used to encode all String fields in an ID3v1 tag */
   private static final String CHARSET_ISO_8859_1 = "ISO-8859-1";   /** fixed size (in bytes) of ID3v1 tags                            */
   public  static final int    TAG_SIZE           = 128;

   // data members
   private int     filePosition;                      // position within the .mp3 file where the ID3v1 tag occurs
   private byte[] tag;                                // raw bytes of the ID3v1 tag
   private String version;
   private String artist;
   private String title;
   private String album;
   private String comment;
   private String year;
   private byte   track;
   private byte   genre;




   /**
    * called when reading in an existing ID3v1 tag from an .mp3 file.
    * @param inputStream   input stream pointing to the ID3v1 tag in an .mp3 file.
    * @param tagOffset     position in the .mp3 file where the ID3v1 tag is found.
    * @param path          path to the .mp3 file whose ID3v1 tag we are trying to read.
    * @throws IOException      if there is an error reading in the ID3v1 tag from the .mp3 file.
    * @throws ParseException   if the .mp3 file does not contain an ID3v1 tag.
    */
   public ID3v1Tag(InputStream inputStream, int tagOffset, String path) throws IOException, ParseException
   {
      tag          = new byte[TAG_SIZE];
      filePosition = tagOffset;

      if (inputStream.read(tag) == tag.length)
         parseTag();
   }

   /**
    * constructor.
    * @param file   random access file to read in the ID3v1 tag from an .mp3 file.
    * @throws IOException      if there is an error while reading the ID3v1 tag.
    * @throws ParseException   if an ID3v1 tag can not be found or if an invalid value is detected while parsing the ID3v1 tag.
    */
   public ID3v1Tag(RandomAccessFile file) throws IOException, ParseException
   {
      // see if there is an ID3v1 tag at the end of the .mp3 file
      filePosition = (int)(file.length() - TAG_SIZE);
      tag          = new byte[TAG_SIZE];

      file.seek(filePosition);

      if (file.read(tag) != TAG_SIZE)
         throw new IOException("Unable to read ID3v1 tag.");

      parseTag();
   }

   /**
    * parse the bytes in the ID3v1 tag.
    * @throws IOException     if the 8859 character set is not supported.
    * @throws ParseException  if there is an error parsing the ID3v1 tag.
    */
   private void parseTag() throws IOException, ParseException
   {
      // see if the tag starts with "TAG"
      if ((char)tag[0] == 'T' && (char)tag[1] == 'A' && (char)tag[2] == 'G')
      {
         title   = new String(tag,  3, 30, CHARSET_ISO_8859_1).trim();
         artist  = new String(tag, 33, 30, CHARSET_ISO_8859_1).trim();
         album   = new String(tag, 63, 30, CHARSET_ISO_8859_1).trim();
         year    = new String(tag, 93,  4, CHARSET_ISO_8859_1).trim();
         comment = new String(tag, 97, 30, CHARSET_ISO_8859_1).trim();
         genre   = tag[127];

         // see if this tag is an ID3v1.1 tag
         if (tag[125] == 0)
         {
            track   = tag[126];
            version = "1.1";
         }
         else
         {
            version = "1.0";
         }
      }
      else
         throw new ParseException(" mp3 file does not contain an ID3v1 tag.");
   }

   /**
    * gets the artist who recorded the song.
    * @return the artist who recorded the song.
    * @see #setArtist(String)
    */
   public String getArtist()
   {
      return artist;
   }

   /**
    * sets the artist who recorded the song.
    * @param artist the artist who recorded the song.
    * @see #getArtist()
    */
   public void setArtist(String artist)
   {
      this.artist = artist;
   }

   /**
    * gets the title of the song.
    * @return the title of the song.
    * @see #setTitle(String)
    */
   public String getTitle()
   {
      return title;
   }

   /**
    * sets the title of the song.
    * @param title   the title of the song.
    * @see #getTitle()
    */
   public void setTitle(String title)
   {
      this.title = title;
   }

   /**
    * gets the album on which the song was released.
    * @return the album which contains the song.
    * @see #setAlbum(String)
    */
   public String getAlbum()
   {
      return album;
   }

   /**
    * sets the album on which the song was released.
    * @param album    the album which contains the song.
    * @see #getAlbum()
    */
   public void setAlbum(String album)
   {
      this.album = album;
   }

   /**
    * gets the year the song was released.
    * @return the year the song was released.
    * @see #setYear(String)
    */
   public String getYear()
   {
      return year;
   }

   /**
    * sets the year the song was released.
    * @param year    the year the song was released.
    * @see #getYear()
    */
   public void setYear(String year)
   {
      this.year = year;
   }

   /**
    * gets the description of the song.
    * @return description of the song.
    * @see #setComment(String)
    */
   public String getComment()
   {
      return comment;
   }

   /**
    * sets the description of the song.
    * @param comment   the description of the song.
    * @see #getComment()
    */
   public void setComment(String comment)
   {
      this.comment = comment;
   }

   /**
    * gets the track number of the song on the album.
    * @return the track number of the song on the album.
    * @see #setTrack(byte)
    */
   public byte getTrack()
   {
     return track;
   }

   /**
    * sets the track number of the song on the album.
    * @param track   the track number of the song on the album.
    * @see #getTrack()
    */
   public void setTrack(byte track)
   {
     this.track = track;
   }

   /**
    * gets the genre of the song.
    * @return the genre of the song.
    * @see #setGenre(byte)
    */
   public byte getGenre()
   {
     return genre;
   }

   /**
    * sets the genre of the song.
    * </p>
    * <table class="beaglebuddy">
    *    <thead>
    *       <tr><th class="beaglebuddy">Genre</th><th class="beaglebuddy">description</th></tr>
    *    </thead>
    *    <tbody>
    *       <tr><td class="beaglebuddy">0.   </td><td class="beaglebuddy">Blues                    </td></tr>
    *       <tr><td class="beaglebuddy">1.   </td><td class="beaglebuddy">Classic Rock             </td></tr>
    *       <tr><td class="beaglebuddy">2.   </td><td class="beaglebuddy">Country                  </td></tr>
    *       <tr><td class="beaglebuddy">3.   </td><td class="beaglebuddy">Dance                    </td></tr>
    *       <tr><td class="beaglebuddy">4.   </td><td class="beaglebuddy">Disco                    </td></tr>
    *       <tr><td class="beaglebuddy">5.   </td><td class="beaglebuddy">Funk                     </td></tr>
    *       <tr><td class="beaglebuddy">6.   </td><td class="beaglebuddy">Grunge                   </td></tr>
    *       <tr><td class="beaglebuddy">7.   </td><td class="beaglebuddy">Hip-Hop                  </td></tr>
    *       <tr><td class="beaglebuddy">8.   </td><td class="beaglebuddy">Jazz                     </td></tr>
    *       <tr><td class="beaglebuddy">9.   </td><td class="beaglebuddy">Metal                    </td></tr>
    *       <tr><td class="beaglebuddy">10.  </td><td class="beaglebuddy">New Age                  </td></tr>
    *       <tr><td class="beaglebuddy">11.  </td><td class="beaglebuddy">Oldies                   </td></tr>
    *       <tr><td class="beaglebuddy">12.  </td><td class="beaglebuddy">Other                    </td></tr>
    *       <tr><td class="beaglebuddy">13.  </td><td class="beaglebuddy">Pop                      </td></tr>
    *       <tr><td class="beaglebuddy">14.  </td><td class="beaglebuddy">R&B                      </td></tr>
    *       <tr><td class="beaglebuddy">15.  </td><td class="beaglebuddy">Rap                      </td></tr>
    *       <tr><td class="beaglebuddy">16.  </td><td class="beaglebuddy">Reggae                   </td></tr>
    *       <tr><td class="beaglebuddy">17.  </td><td class="beaglebuddy">Rock                     </td></tr>
    *       <tr><td class="beaglebuddy">18.  </td><td class="beaglebuddy">Techno                   </td></tr>
    *       <tr><td class="beaglebuddy">19.  </td><td class="beaglebuddy">Industrial               </td></tr>
    *       <tr><td class="beaglebuddy">20.  </td><td class="beaglebuddy">Alternative              </td></tr>
    *       <tr><td class="beaglebuddy">21.  </td><td class="beaglebuddy">Ska                      </td></tr>
    *       <tr><td class="beaglebuddy">22.  </td><td class="beaglebuddy">Death Metal              </td></tr>
    *       <tr><td class="beaglebuddy">23.  </td><td class="beaglebuddy">Pranks                   </td></tr>
    *       <tr><td class="beaglebuddy">24.  </td><td class="beaglebuddy">Soundtrack               </td></tr>
    *       <tr><td class="beaglebuddy">25.  </td><td class="beaglebuddy">Euro-Techno              </td></tr>
    *       <tr><td class="beaglebuddy">26.  </td><td class="beaglebuddy">Ambient                  </td></tr>
    *       <tr><td class="beaglebuddy">27.  </td><td class="beaglebuddy">Trip-Hop                 </td></tr>
    *       <tr><td class="beaglebuddy">28.  </td><td class="beaglebuddy">Vocal                    </td></tr>
    *       <tr><td class="beaglebuddy">29.  </td><td class="beaglebuddy">Jazz+Funk                </td></tr>
    *       <tr><td class="beaglebuddy">30.  </td><td class="beaglebuddy">Fusion                   </td></tr>
    *       <tr><td class="beaglebuddy">31.  </td><td class="beaglebuddy">Trance                   </td></tr>
    *       <tr><td class="beaglebuddy">32.  </td><td class="beaglebuddy">Classical                </td></tr>
    *       <tr><td class="beaglebuddy">33.  </td><td class="beaglebuddy">Instrumental             </td></tr>
    *       <tr><td class="beaglebuddy">34.  </td><td class="beaglebuddy">Acid                     </td></tr>
    *       <tr><td class="beaglebuddy">35.  </td><td class="beaglebuddy">House                    </td></tr>
    *       <tr><td class="beaglebuddy">36.  </td><td class="beaglebuddy">Game                     </td></tr>
    *       <tr><td class="beaglebuddy">37.  </td><td class="beaglebuddy">Sound Clip               </td></tr>
    *       <tr><td class="beaglebuddy">38.  </td><td class="beaglebuddy">Gospel                   </td></tr>
    *       <tr><td class="beaglebuddy">39.  </td><td class="beaglebuddy">Noise                    </td></tr>
    *       <tr><td class="beaglebuddy">40.  </td><td class="beaglebuddy">AlternRock               </td></tr>
    *       <tr><td class="beaglebuddy">41.  </td><td class="beaglebuddy">Bass                     </td></tr>
    *       <tr><td class="beaglebuddy">42.  </td><td class="beaglebuddy">Soul                     </td></tr>
    *       <tr><td class="beaglebuddy">43.  </td><td class="beaglebuddy">Punk                     </td></tr>
    *       <tr><td class="beaglebuddy">44.  </td><td class="beaglebuddy">Space                    </td></tr>
    *       <tr><td class="beaglebuddy">45.  </td><td class="beaglebuddy">Meditative               </td></tr>
    *       <tr><td class="beaglebuddy">46.  </td><td class="beaglebuddy">Instrumental Pop         </td></tr>
    *       <tr><td class="beaglebuddy">47.  </td><td class="beaglebuddy">Instrumental Rock        </td></tr>
    *       <tr><td class="beaglebuddy">48.  </td><td class="beaglebuddy">Ethnic                   </td></tr>
    *       <tr><td class="beaglebuddy">49.  </td><td class="beaglebuddy">Gothic                   </td></tr>
    *       <tr><td class="beaglebuddy">50.  </td><td class="beaglebuddy">Darkwave                 </td></tr>
    *       <tr><td class="beaglebuddy">51.  </td><td class="beaglebuddy">Techno-Industrial        </td></tr>
    *       <tr><td class="beaglebuddy">52.  </td><td class="beaglebuddy">Electronic               </td></tr>
    *       <tr><td class="beaglebuddy">53.  </td><td class="beaglebuddy">Pop-Folk                 </td></tr>
    *       <tr><td class="beaglebuddy">54.  </td><td class="beaglebuddy">Eurodance                </td></tr>
    *       <tr><td class="beaglebuddy">55.  </td><td class="beaglebuddy">Dream                    </td></tr>
    *       <tr><td class="beaglebuddy">56.  </td><td class="beaglebuddy">Southern Rock            </td></tr>
    *       <tr><td class="beaglebuddy">57.  </td><td class="beaglebuddy">Comedy                   </td></tr>
    *       <tr><td class="beaglebuddy">58.  </td><td class="beaglebuddy">Cult                     </td></tr>
    *       <tr><td class="beaglebuddy">59.  </td><td class="beaglebuddy">Gangsta                  </td></tr>
    *       <tr><td class="beaglebuddy">60.  </td><td class="beaglebuddy">Top 40                   </td></tr>
    *       <tr><td class="beaglebuddy">61.  </td><td class="beaglebuddy">Christian Rap            </td></tr>
    *       <tr><td class="beaglebuddy">62.  </td><td class="beaglebuddy">Pop/Funk                 </td></tr>
    *       <tr><td class="beaglebuddy">63.  </td><td class="beaglebuddy">Jungle                   </td></tr>
    *       <tr><td class="beaglebuddy">64.  </td><td class="beaglebuddy">Native American          </td></tr>
    *       <tr><td class="beaglebuddy">65.  </td><td class="beaglebuddy">Cabaret                  </td></tr>
    *       <tr><td class="beaglebuddy">66.  </td><td class="beaglebuddy">New Wave                 </td></tr>
    *       <tr><td class="beaglebuddy">67.  </td><td class="beaglebuddy">Psychadelic              </td></tr>
    *       <tr><td class="beaglebuddy">68.  </td><td class="beaglebuddy">Rave                     </td></tr>
    *       <tr><td class="beaglebuddy">69.  </td><td class="beaglebuddy">Showtunes                </td></tr>
    *       <tr><td class="beaglebuddy">70.  </td><td class="beaglebuddy">Trailer                  </td></tr>
    *       <tr><td class="beaglebuddy">71.  </td><td class="beaglebuddy">Lo-Fi                    </td></tr>
    *       <tr><td class="beaglebuddy">72.  </td><td class="beaglebuddy">Tribal                   </td></tr>
    *       <tr><td class="beaglebuddy">73.  </td><td class="beaglebuddy">Acid Punk                </td></tr>
    *       <tr><td class="beaglebuddy">74.  </td><td class="beaglebuddy">Acid Jazz                </td></tr>
    *       <tr><td class="beaglebuddy">75.  </td><td class="beaglebuddy">Polka                    </td></tr>
    *       <tr><td class="beaglebuddy">76.  </td><td class="beaglebuddy">Retro                    </td></tr>
    *       <tr><td class="beaglebuddy">77.  </td><td class="beaglebuddy">Musical                  </td></tr>
    *       <tr><td class="beaglebuddy">78.  </td><td class="beaglebuddy">Rock & Roll              </td></tr>
    *       <tr><td class="beaglebuddy">79.  </td><td class="beaglebuddy">Hard Rock                </td></tr>
    *    <tbody>
    * </table>
    * @param genre   the genre of the song.
    * @see #getGenre()
    */
   public void setGenre(byte genre)
   {
      if (genre > 80)
         throw new IllegalArgumentException("Invalid ID3v1 genre value, " + genre + ".  It must be between 0 <= genre <= 79.");

      this.genre = genre;
   }

   /**
    * gets the genre name according to the predefined list of {@link #setGenre(byte) music genres}.
    * @return the genre as a string.
    */
   public String getGenreAsString()
   {
      // note: java treats byte as a signed value, while the ID3v2.3 spec treats bytes as unsigned.
      //       this necessitates converting each byte to a larger value (integer) and then shifting and adding them.
      int index = genre & 0xFF;
      return index < Genre.REMIX.ordinal() ? Genre.valueOf((byte)index).getName() : "";
   }

   /**
    * gets the version of the ID3 tag.  It is either 1.0 or 1.1.
    * @return version of the tag.
    */
   public String getVersion()
   {
      return version;
   }

   /**
    * gets a string representation of the ID3v1 tag showing all of its fields and their values.
    * @return a string representation of the ID3v1 tag.
    */
   public String toString()
   {
      StringBuffer buffer = new StringBuffer();
      buffer.append("ID3v" + version + " tag\n");
      buffer.append("   file position: " + filePosition + "\n");
      buffer.append("   title........: " + title        + "\n");
      buffer.append("   artist.......: " + artist       + "\n");
      buffer.append("   album........: " + album        + "\n");
      buffer.append("   year.........: " + year         + "\n");
      buffer.append("   comment......: " + album        + "\n");
      buffer.append("   track........: " + track        + "\n");
      buffer.append("   genre........: " + genre        + " - " + getGenreAsString());

      return buffer.toString();
   }
}

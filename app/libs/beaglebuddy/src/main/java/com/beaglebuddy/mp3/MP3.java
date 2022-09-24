package com.beaglebuddy.mp3;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Vector;

import com.beaglebuddy.id3.enums.Genre;
import com.beaglebuddy.id3.enums.ID3TagVersion;
import com.beaglebuddy.id3.enums.Language;
import com.beaglebuddy.id3.enums.PictureType;
import com.beaglebuddy.id3.pojo.AttachedPicture;
import com.beaglebuddy.id3.pojo.SynchronizedLyric;

import com.beaglebuddy.mpeg.enums.BitrateType;
import com.beaglebuddy.mpeg.enums.ChannelMode;
import com.beaglebuddy.mpeg.enums.Layer;
import com.beaglebuddy.mpeg.enums.MPEGVersion;




/**
 * <table class="logos_width">
 *    <tbody>
 *       <tr>
 *          <td                               ><img src="../../../resources/id3v2.gif"                     width="56"  height="54"  alt="ID3 logo"        /></td>
 *          <td class="logos_horz_align_right"><img src="../../../resources/beaglebuddy_software_logo.gif" width="340" height="110" alt="Beaglebuddy logo"/></td>
 *       </tr>
 *    </tbody>
 * </table>
 * <p class="beaglebuddy">
 * This class provides high level, easy to use functions for working with .mp3 files.  In particular, it provides a simple, easy to use yet powerful interface
 * for getting and setting information about the song stored in the .mp3 file.  Internally, this information is stored in a tag (most commonly an
 * {@link com.beaglebuddy.id3.v23.ID3v23Tag ID3v2.3 tag}) embedded in the .mp3 file.  And by most commonly, I mean 99.9% of the time.  Occasionally, an .mp3
 * file will contain an {@link com.beaglebuddy.id3.v24.ID3v24Tag ID3v2.4 tag}. The format of an .mp3 file is shown below:
 * </p>
 * <p>
 * <table border="0">
 *   <tbody>
 *      <tr>
 *          <td class="beaglebuddy_pic_align_top">
 *             <img src="../../../resources/mp3_format_ID3v2.3.gif" height="550" width="330" alt="mp3 ID3v2.3 format" usemap="#id3v23_map"/>
 *          </td>
 *          <td> &nbsp; &nbsp; &nbsp; </td>
 *          <td class="beaglebuddy_pic_align_top">
 *             <img src="../../../resources/mp3_format_ID3v2.4.gif" height="580" width="330" alt="mp3 ID3v2.4 format" usemap="#id3v24_map"/>
 *          </td>
 *       </tr>
 *    </tbody>
 * </table>
 * <map name="id3v23_map">
 *    <area shape="rect" coords=" 230, 145, 300, 165" href="../id3/v23/ID3v23Tag.html"                alt="ID3v2.3 Tag"/>
 *    <area shape="rect" coords="   6,  42, 198,  75" href="../id3/v23/ID3v23TagHeader.html"          alt="ID3v2.3 Tag Header"/>
 *    <area shape="rect" coords="   6,  76, 198, 108" href="../id3/v23/ID3v23TagExtendedHeader.html"  alt="ID3v2.3 Tag Extended Header"/>
 *    <area shape="rect" coords="   6, 109, 198, 250" href="../id3/v23/ID3v23Frame.html"              alt="ID3v2.3 Frame"/>
 *    <area shape="rect" coords="   6, 251, 198, 286" href="MP3Base.html#setID3v2xPadding(int)"       alt="ID3v2.3 Padding"/>
 *    <area shape="rect" coords="   6, 287, 198, 374" href="../mpeg/MPEGFrame.html"                   alt="MPEG Audio Frame"/>
 *    <area shape="rect" coords="   6, 375, 198, 425" href="../lyrics3/Lyrics3v2Tag.html"             alt="Lyrics3 Tag"/>
 *    <area shape="rect" coords="   6, 426, 198, 479" href="../ape/APETag.html"                       alt="APE Tag"/>
 *    <area shape="rect" coords="   6, 480, 198, 530" href="../id3/v1/ID3v1Tag.html"                  alt="ID3V1 Tag"/>
 * </map>
 * <map name="id3v24_map">
 *    <area shape="rect" coords=" 230, 170, 300, 185" href="../id3/v24/ID3v24Tag.html"                alt="ID3v2.4 Tag"/>
 *    <area shape="rect" coords="   6,  42, 198,  75" href="../id3/v24/ID3v24TagHeader.html"          alt="ID3v2.4 Tag Header"/>
 *    <area shape="rect" coords="   6,  76, 198, 108" href="../id3/v24/ID3v24TagExtendedHeader.html"  alt="ID3v2.4 Tag Extended Header"/>
 *    <area shape="rect" coords="   6, 109, 198, 250" href="../id3/v24/ID3v24Frame.html"              alt="ID3v2.4 Frame""/>
 *    <area shape="rect" coords="   6, 251, 198, 286" href="MP3Base.html#setID3v2xPadding(int)"       alt="ID3v2.4 Padding"/>
 *    <area shape="rect" coords="   6, 287, 198, 321" href="../id3/v24/ID3v24TagFooter.html"          alt="ID3v2.4 Tag Footer"/>
 *    <area shape="rect" coords="   6, 322, 198, 410" href="../mpeg/MPEGFrame.html"                   alt="MPEG Audio Frame"/>
 *    <area shape="rect" coords="   6, 411, 198, 462" href="../lyrics3/Lyrics3v2Tag.html"             alt="Lyrics3 Tag"/>
 *    <area shape="rect" coords="   6, 463, 198, 515" href="../ape/APETag.html"                       alt="APE Tag"/>
 *    <area shape="rect" coords="   6, 516, 198, 564" href="../id3/v1/ID3v1Tag.html"                  alt="ID3V1 Tag"/>
 * </map>
 * <br/><br/>
 * </p>
 * <p class="beaglebuddy">
 * You don't have to know any of the details about the low level format of an {@link com.beaglebuddy.id3.v23.ID3v23Tag ID3v2.3 tag} or
 * {@link com.beaglebuddy.id3.v24.ID3v24Tag ID3v2.4 tag} in order to use this class.
 * You can simply open an .mp3 file, and then use the MP3 class's methods to get or set the information you want.  You can set things like the name of the song,
 * the track number, the name of the band, who wrote the song, the lyrics to the song, the album the song was released on, store images of the CD cover, etc. all
 * without knowing a single thing about {@link com.beaglebuddy.id3.v23.ID3v23Tag ID3v2.3 tags}.  All of the details are handled for you.  To see how easy the
 * MP3 class is to use, look at the numerous examples of <a href="http://www.beaglebuddy.com/content/pages/more_sample_code/file_list.html" target="_blank">sample code</a>
 * provided in the sample_code/src directory.  For a quick start, take a look at the sample code shown below:
 * </p>
 * <pre class="beaglebuddy">
 * <code>
 * import java.io.File;
 * import java.io.IOException;
 * import com.beaglebuddy.mp3.MP3;
 * import com.beaglebuddy.id3.enums.Genre;
 * import com.beaglebuddy.id3.enums.PictureType;
 *
 * public class MP3Example
 * {
 *    public static void main(String[] args)
 *    {
 *       try
 *       {
 *          MP3 mp3 = new MP3("c:/mp3/Wild Cat.mp3");
 *
 *          // if there was any invalid information (ie, ID3v2.x frames) in the .mp3 file,
 *          // then display the errors to the user
 *          if (mp3.hasErrors())
 *          {
 *             mp3.displayErrors(System.out);      // display the errors that were found
 *             mp3.save();                         // discard the invalid information (ID3v2.x frames) and
 *          }                                      // save only the valid frames back to the .mp3 file
 *
 *          mp3.setBand("Axel Rudi Pell");
 *          mp3.setAlbum("Wild Obsession");
 *          mp3.setTitle("Wild Cat");              // name of the song
 *          mp3.setMusicType(Genre.HARD_ROCK);     // 79 == Hard Rock
 *          mp3.setRating(220);                    // 1= worst, 255 = best
 *          mp3.setTrack(1);
 *          mp3.setYear(1989);                     // year the song was released
 *          mp3.setLyrics("Like a wild cat, breakin' out of my cage\n...");
 *          mp3.setPicture(PictureType.FRONT_COVER, new File("c:/images/axel_rudi_pell.wild_obsession.jpg"));
 *
 *
 *          if (mp3.getAudioDuration() == 0)       // if the length of the song hasn't been specified,
 *             mp3.setAudioDuration(221);          // if you know how long the song is:  221 seconds == 3 minutes and 41 seconds
 *          mp3.setAudioDuration();                // otherwise, calculate it from the mpeg audio frames
 *
 *          mp3.save();
 *       }
 *       catch (IOException ex)
 *       {
 *          System.out.println("An error occurred while reading/saving the mp3 file.");
 *       }
 *    }
 * }
 * </code>
 * </pre>
 * <p class="beaglebuddy">
 * But, if you're an ID3v2.x tag expert, and want to work directly with the tag itself, then you certainly can do that as well.  Just call the MP3 class's
 * {@link com.beaglebuddy.mp3.MP3Base#getID3v23Tag() getID3v23Tag()} or {@link com.beaglebuddy.mp3.MP3Base#getID3v24Tag() getID3v24Tag()} method in order to
 * get direct access to the {@link com.beaglebuddy.id3.v23.ID3v23Tag}/{@link com.beaglebuddy.id3.v24.ID3v24Tag} tag.
 * </p>
 * <p class="beaglebuddy">
 * One thing that you should take a look at is how the Beaglebuddy MP3 library handles errors that are encountered while reading in an existing .mp3 file.
 * Please see the {@link com.beaglebuddy.id3.v23.ID3v23Frame} or {@link com.beaglebuddy.id3.v24.ID3v24Frame} class for the details on the Beaglebuddy MP3 library's error handling.
 * </p>
 */
public class MP3 extends MP3Base
{
   /**
    * This constructor reads the ID3 tag(s), which contain the information about the song, from the .mp3 file.
    * @param mp3File   path to an .mp3 file.
    * @throws IOException  if there is a problem reading the .mp3 file.
    */
   public MP3(String mp3File) throws IOException
   {
      this(new File(mp3File));
   }

   /**
    * This constructor reads the ID3 tag(s), which contain the information about the song, from the .mp3 file.
    * @param mp3File   file pointing to an .mp3 file.
    * @throws IOException   if there is a problem reading the .mp3 file.
    */
   public MP3(File mp3File) throws IOException
   {
      super(mp3File);
   }

   /**
    * This constructor reads the ID3 tag(s), which contain the information about the song, from the .mp3 file located at the specified URL.
    * <p>
    * When you load an .mp3 file from a URL, the Beaglebuddy MP3 library treats the .mp3 file as being read only.  For that reason, you can not change any of the information
    * in the .mp3 file, nor can you save it.  You may only read the values that are currently stored in the .mp3 file.
    * </p>
    * <p>
    * Please note that the URL must be correctly encoded.  That is, it must conform to <a href="http://www.ietf.org/rfc/rfc1738.txt">RFC 1738</a>.  A more user friendly explanation can be found at
    * the <a href="http://www.w3schools.com/tags/ref_urlencode.asp">HTML URL Encoding Reference</a>.  Also note that the java class <i>java.net.URLEncoder</i> does not work properly when passed
    * an entire URL.
    * </p>
    * <p>
    * Thus, the following URL will not work in java: http://www.beaglebuddy.com/content/downloads/mp3/01.Hells Bells.mp3<br/>
    * Instead, you must specify the correctly encoded URL to the MP3 constructor as: http://www.beaglebuddy.com/content/downloads/mp3/01.Hells%20Bells.mp3
    * </p>
    * <p>
    * Another issue when accessing an .mp3 file through a URL is the use of proxy servers.
    * If you run into the problem of being able to access an .mp3 file in your web browser, such as the following:
    * <a href="http://www.beaglebuddy.com/content/downloads/mp3/01.Hells%20Bells.mp3">http://www.beaglebuddy.com/content/downloads/mp3/01.Hells%20Bells.mp3</a><br/>
    * but using the exact same URL in your java program does not work (you get a connection time out error message), then perhaps you're internet connection is
    * utilizing a proxy server.  If so, you can solve this problem by specifying the proxy server's host and port as system properties in one of two ways:
    * <ol>
    *    <li>Specifying them as Defines on the command line to the java JRE</li>
    *    <li>Specifying them with java code in your application</li>
    * </ol>
    * <br/>
    * To specify them on the command line, do something like the following: <br/>
    * <code>
    * <pre class="beaglebuddy">
    * %java_jre_home%\java -Dhttp.proxyHost=usproxy.mycompany.com -Dhttp.proxyPort=9000 -classpath beaglebuddy_mp3.jar;. com.beaglebuddy.mp3.sample_code.BasicURL
    * </pre>
    * </code>
    * To specify them in your code, add the following two lines to your code<br/>
    * <code>
    * <pre class="beaglebuddy">
    * System.setProperty("http.proxyHost", "usproxy.mycompany.com");
    * System.setProperty("http.proxyPort", "9000");
    * </pre>
    * </code>
    * </p>
    * See the sample code provided in the com.beaglebuddy.mp3.sample_code.BasicURL for an example of how to load an .mp3 from a URL.
    * @param mp3URL   URL to an .mp3 file.
    * @throws IOException  if there is a problem reading the .mp3 file.
    */
   public MP3(URL mp3URL) throws IOException
   {
      super(mp3URL);
   }

   /**
    * This constructor reads the ID3 tag(s), which contain the information about the song, from the .mp3 file pointed to by the specified input stream.
    * This constructor is usefull for reading .mp3 files from a compressed source (.zip file), from a 3rd party streaming source, etc.
    * <p>
    * When you load an .mp3 file from a generic input stream, the Beaglebuddy MP3 library treats the .mp3 file as being read only.  For that reason, you can not
    * change any of the information in the .mp3 file, nor can you save it.  You may only read the values that are currently stored in the .mp3 file.
    * </p>
    * @param inputStream   An input stream pointing to an .mp3 file.
    * @throws IOException  if there is a problem reading the contents of the .mp3 file from the input stream.
    */
   public MP3(InputStream inputStream) throws IOException
   {
      super(inputStream);
   }

   /**
    * clears all the information (ie, frames) in the ID3v2.x tag from the .mp3 file.
    * <br/><br/>
    * @throws IllegalStateException    if the mp3 song was loaded from a URL or an input stream and is therefore considered to be read only and thus may not be modified.
    */
   public void clear() throws IllegalStateException
   {
      if (mp3File == null)
        throw new IllegalStateException(getReadOnlyErrorMessage());

      if (id3v23Tag != null)
         id3v23Tag.removeFrames();
      else
         id3v24Tag.removeFrames();
   }

   /**
    * save the ID3v2.x tag to the .mp3 file.
    * Any invalid frames that were found while reading in the .mp3 file are discarded, thereby ensuring that
    * saved .mp3 file contains only valid information.
    * <br/><br/>
    * @throws IOException              if there was an error writing the ID3v2.x tag to the .mp3 file.
    * @throws IllegalStateException    if the mp3 song was loaded from a URL or an input stream and is therefore considered to be read only and thus may not be modified.
    */
   public void save() throws IOException, IllegalStateException
   {
      if (mp3File == null) {
         throw new IllegalStateException(getReadOnlyErrorMessage());
      }

      if (id3v23Tag != null)
         saveID3v23();
      else
          saveID3v24();
       readMP3File(mp3File);
   }

   /**
    * gets the codec (coder - decoder) used to encode this .mp3 file.
    * @return the codec used to encode this .mp3 file.
    */
   public String getCodec()
   {
      return mpegFrame.getMPEGFrameHeader().getMPEGVersion() + " " + mpegFrame.getMPEGFrameHeader().getLayer();
   }

   /**
    * gets the MPEG version of this .mp3 file.
    * @return the MPEG version of this .mp3 file.
    */
   public MPEGVersion getMPEGVersion()
   {
      return mpegFrame.getMPEGFrameHeader().getMPEGVersion();
   }

   /**
    * gets the MPEG layer of this .mp3 file.
    * @return the MPEG layer of this .mp3 file.
    */
   public Layer getLayer()
   {
      return mpegFrame.getMPEGFrameHeader().getLayer();
   }

   /**
    * gets the bit rate in kbps (kilobits per second) at which the mp3 file was created.  If the .mp3 file was encoded using a constant bit rate (CBR), then the bit rate
    * returned is the actual bit rate.  If the .mp3 file was encoded using a variable bit rate (VBR), then the bit rate returned is an average bit rate.
    * @return the bit rate in kbps (kilobits per second) at which the mp3 file was created.  If the .mp3 file was encoded using a variable bit rate (VBR), then the average bitrate is calculated
    * by reading through the {@link com.beaglebuddy.mpeg.MPEGFrame MPEG audio frames} in the .mp3 file.  If an error occurs during this process, then -1 is returned.
    * @see <a href="http://en.wikipedia.org/wiki/Bit_rates" target="_blank">definition of kbps</a>
    */
   public int getBitrate()
   {
      // if the .mp3 file was encoded using a variable bitrate, then read through the MPEG audio frames to calculate the average bitrate
      if (bitrate == -1 && bitrateType == BitrateType.VBR)
      {
         try {validateMPEGFrames();} catch (IOException ex) {/* no code necessary */}
      }
      return bitrate;
   }

   /**
    * gets the type of bit rate used to encode the .mp3 file, either constant or variable.
    * @return the type of bit rate used to encode the .mp3 file.
    */
   public BitrateType getBitrateType()
   {
      return bitrateType;
   }

   /**
    * gets the frequency (in herz - hz), which specifies how many times per second the audio is sampled and stored as a number in the .mp3 file.
    * CD audio is sampled at 44.1 khz, which means 44,100 samples per second.
    * @return the sampling frequency (in hz) used to convert the analog audio to a digital .mp3 file.  A value of -1 indicates an invalid value due to the MPEG version
    */
   public int getFrequency()
   {
      return mpegFrame.getMPEGFrameHeader().getFrequency();
   }

   /**
    * gets the channel mode (stereo or mono) used to encode the .mp3 file.
    * @return the channel mode (stereo or mono) used to encode the .mp3 file.
    */
   public ChannelMode getChannelMode()
   {
      return mpegFrame.getMPEGFrameHeader().getChannelMode();
   }


   /**
    * gets the name of the album on which the .mp3 song was released.
    * <br/><br/><b>Example:</b><code><pre class="beaglebuddy">
    *     // load the song "Something for Nothing" from Rush's "2112" album.
    *     MP3 mp3 = new MP3("c:/mp3/rush/2112/something for nothing.mp3");
    *     System.out.println("the song " + mp3.getTitle() + " was released on the album " + mp3.getAlbum());</pre></code>
    * @return the album on which the song was released.  If no album has been specified for this song, then null is returned.
    * @see #setAlbum(String)
    * @see #removeAlbum()
    */
   public String getAlbum()
   {
      return id3v23Tag != null ? getV23Album() : getV24Album();
   }

   /**
    * sets the name of the album on which the .mp3 song was released.
    * <br/><br/><b>Example:</b><code><pre class="beaglebuddy">
    *     // load the song "Something for Nothing" from Rush's "2112" album.
    *     MP3 mp3 = new MP3("c:/mp3/rush/2112/something for nothing.mp3");
    *     mp3.setAlbum("2112");
    *     System.out.println("the song " + mp3.getTitle() + " was released on the album " + mp3.getAlbum());</pre></code>
    * @param album  the name of the album on which the .mp3 song was released.
    * @throws IllegalStateException   if the mp3 song was loaded from a URL or an input stream and is therefore considered to be read only and thus may not be modified.
    * @see #getAlbum()
    * @see #removeAlbum()
    */
   public void setAlbum(String album) throws IllegalStateException
   {
      if (mp3File == null)
         throw new IllegalStateException(getReadOnlyErrorMessage());

      if (id3v23Tag != null)
         setV23Album(album);
      else
         setV24Album(album);
   }

   /**
    * removes the name of the album on which the .mp3 song was released.
    * @throws IllegalStateException   if the mp3 song was loaded from a URL or an input stream and is therefore considered to be read only and thus may not be modified.
    * @see #getAlbum()
    * @see #setAlbum(String)
    */
   public void removeAlbum() throws IllegalStateException
   {
      if (mp3File == null)
         throw new IllegalStateException(getReadOnlyErrorMessage());

      if (id3v23Tag != null)
         removeV23Album();
      else
         removeV24Album();
   }

   /**
    * gets the duration (in seconds) of the song in the .mp3 file.
    * <br/><br/><b>Example:</b><code><pre class="beaglebuddy">
    *     // load the song "El Laberinto del Minotauro" from Tierra Santa's "Sangre de Reyes" album.
    *     MP3 mp3     = new MP3("c:/mp3/tierra santa/sangre de reyes/el laberinto del minotauro.mp3");
    *     int minutes = mp3.getAudioDuration() / 60;
    *     int seconds = mp3.getAudioDuration() % 60;
    *     System.out.println("the song " + mp3.getTitle() + " is " + minutes + " minutes and " + seconds + " seconds long.");</pre></code>
    * @return the duration (in seconds) of the song in the .mp3 file. .  If no audio duration has been specified, then 0 is returned.
    * @see #setAudioDuration()
    * @see #setAudioDuration(int)
    * @see #removeAudioDuration()
    */
   public int getAudioDuration()
   {
      return id3v23Tag != null ? getV23AudioDuration() : getV24AudioDuration();
   }

   /**
    * sets the duration (in seconds) of the song in the .mp3 file from a calculation of the audio data.
    * <br/><br/><b>Example:</b><code><pre class="beaglebuddy">
    *     // load the song "El Laberinto del Minotauro" from Tierra Santa's "Sangre de Reyes" album.
    *     MP3 mp3 = new MP3("c:/mp3/tierra santa/sangre de reyes/el laberinto del minotauro.mp3");
    *     mp3.setAudioDuration();              // calculate the duration of the song from the audio data in the .mp3
    *     int minutes = mp3.getAudioDuration() / 60;
    *     int seconds = mp3.getAudioDuration() % 60;
    *     System.out.println("the song " + mp3.getTitle() + " is " + minutes + " minutes and " + seconds + " seconds long.");</pre></code>
    * @throws IllegalArgumentException   If the calculated duration is less than 0.
    * @throws IllegalStateException      if the mp3 song was loaded from a URL or an input stream and is therefore considered to be read only and thus may not be modified.
    * @see #getAudioDuration()
    * @see #setAudioDuration(int)
    * @see #removeAudioDuration()
    */
   public void setAudioDuration() throws IllegalArgumentException, IllegalStateException
   {
      int duration = calculateAudioDuration();

      if (duration > 0)
         setAudioDuration(duration);
   }

   /**
    * sets the duration (in seconds) of the song in the .mp3 file.
    * <br/><br/><b>Example:</b><code><pre class="beaglebuddy">
    *     // load the song "El Laberinto del Minotauro" from Tierra Santa's "Sangre de Reyes" album.
    *     MP3 mp3 = new MP3("c:/mp3/tierra santa/sangre de reyes/el laberinto del minotauro.mp3");
    *     mp3.setAudioDuration(307);           // 5 minutes and 7 seconds == 5 * 60 + 7 == 307 seconds
    *     int minutes = mp3.getAudioDuration() / 60;
    *     int seconds = mp3.getAudioDuration() % 60;
    *     System.out.println("the song " + mp3.getTitle() + " is " + minutes + " minutes and " + seconds + " seconds long.");</pre></code>
    * @param duration the duration (in seconds) of the song in the .mp3 file.
    * @throws IllegalArgumentException   If the duration is less than 0.
    * @throws IllegalStateException      if the mp3 song was loaded from a URL or an input stream and is therefore considered to be read only and thus may not be modified.
    * @see #getAudioDuration()
    * @see #removeAudioDuration()
    */
   public void setAudioDuration(int duration) throws IllegalArgumentException, IllegalStateException
   {
      if (mp3File == null)
         throw new IllegalStateException(getReadOnlyErrorMessage());

      if (id3v23Tag != null)
         setV23AudioDuration(duration);
      else
         setV24AudioDuration(duration);
   }

   /**
    * removes the duration of the song from the .mp3 file.
    * @throws IllegalStateException   if the mp3 song was loaded from a URL or an input stream and is therefore considered to be read only and thus may not be modified.
    * @see #getAudioDuration()
    * @see #setAudioDuration(int)
    */
   public void removeAudioDuration() throws IllegalStateException
   {
      if (mp3File == null)
         throw new IllegalStateException(getReadOnlyErrorMessage());

      if (id3v23Tag != null)
         removeV23AudioDuration();
      else
         removeV24AudioDuration();
   }

   /**
    * gets the size (in bytes) of the audio portion of the .mp3 file.  See the <a href="#mp3_file_format">mp3 file format</a> to view where the audio portion
    * of a song is stored in an .mp3 file.
    * <br/><br/><b>Example:</b><code><pre class="beaglebuddy">
    *     // load the song "All We Are" from Warlock's "Triumph and Agony" album.
    *     MP3 mp3 = new MP3("c:/mp3/warlock/triumph and agony/all we are.mp3");
    *     System.out.println("the size of the .mp3 file " + mp3.getPath() + " is " + mp3.getFileSize() + " bytes");
    *     System.out.println("the actual audio portion of the song " + mp3.getTitle() + " stored in the .mp3 file is " + mp3.getAudioSize() + " bytes");</pre></code>
    * @return the size (in bytes) of the audio portion of the .mp3 file. If no audio size has been specified, then 0 is returned.
    * @see #setAudioSize(int)
    * @see #removeAudioSize()
    */
   public int getAudioSize()
   {
      return id3v23Tag != null ? getV23AudioSize() : audioSize;
   }

   /**
    * sets the size (in bytes) of the audio portion of the .mp3 file.  See the <a href="#mp3_file_format">mp3 file format</a> to view where the audio portion
    * of a song is stored in an .mp3 file.
    * <br/><br/><b>Example:</b><code><pre class="beaglebuddy">
    *     // load the song "All We Are" from Warlock's "Triumph and Agony" album.
    *     MP3 mp3 = new MP3("c:/mp3/warlock/triumph and agony/all we are.mp3");
    *     mp3.setAudioSize(3202068);
    *     System.out.println("the size of the .mp3 file " + mp3.getPath() + " is " + mp3.getFileSize() + " bytes");
    *     System.out.println("the actual audio portion of the song " + mp3.getTitle() + " stored in the .mp3 file is " + mp3.getAudioSize() + " bytes");</pre></code>
    * @param size  size (in bytes) of the audion portion of the .mp3 file.
    * @throws IllegalArgumentException   If the audio size is less than or equal 0.
    * @throws IllegalStateException      if the mp3 song was loaded from a URL or an input stream and is therefore considered to be read only and thus may not be modified.
    * @see #getAudioSize()
    * @see #removeAudioSize()
    */
   public void setAudioSize(int size) throws IllegalArgumentException, IllegalStateException
   {
      if (mp3File == null)
         throw new IllegalStateException(getReadOnlyErrorMessage());

      if (id3v23Tag != null)
         setV23AudioSize(size);
      else
         audioSize = size;
   }

   /**
    * removes the size of the audio portion of the song from the .mp3 file.
    * @throws IllegalStateException   If the mp3 song was loaded from a URL and therefore is considered read only and may not be modified.
    * @see #getAudioSize()
    * @see #setAudioSize(int)
    */
   public void removeAudioSize() throws IllegalStateException
   {
      if (mp3File == null)
         throw new IllegalStateException(getReadOnlyErrorMessage());

      if (id3v23Tag != null)
         removeV23AudioDuration();
      else
         removeV24AudioDuration();
   }

   /**
    * gets the name of the band who recorded the .mp3 song.
    * On Windows machines, this is called the "Artist".
    * <br/></br><b>Note for ID3 tag experts:</b> Some mp3 software stores the name of the band in {@link com.beaglebuddy.id3.enums.v23.FrameType#BAND TPE2} frames, which is
    * what the getBand() method returns, while other mp3 software use {@link com.beaglebuddy.id3.enums.v23.FrameType#LEAD_PERFORMER TPE1} frames, which is what the
    * {@link #getLeadPerformer} method returns.  So, we recommend calling getBand() first, checking if it is null, and if so, calling getLeadPerformer().
    * <br/><br/><b>Example:</b><code><pre class="beaglebuddy">
    *     // load the song "Beads Of Ebony" from Praying Mantis's "Time Tells No Lies" album.
    *     MP3 mp3 = new MP3("c:/mp3/praying mantis/time tells no lies/beads of ebony.mp3");
    *     String band = mp3.getBand() != null ? mp3.getBand() : mp3.getLeadPerformer();
    *     System.out.println("the song " + mp3.getTitle() + " was released by " + band + " on the album " + mp3.getAlbum());</pre></code>
    * @return the band who recorded the .mp3 song. If no band has been specified, then null is returned.
    * @see #setBand(String)
    * @see #getLeadPerformer()
    * @see #removeBand()
    */
   public String getBand()
   {
      return id3v23Tag != null ? getV23Band() : getV24Band();
   }

   /**
    * sets the name of the band who recorded the .mp3 song.
    * <br/></br><b>Note for ID3 tag experts:</b> The Beaglebuddy MP3 class stores the name of the band in the {@link com.beaglebuddy.id3.enums.v23.FrameType#BAND TPE2} frame.
    * <br/><br/><b>Example:</b><code><pre class="beaglebuddy">
    *     // load the song "Beads Of Ebony" from Praying Mantis's "Time Tells No Lies" album.
    *     MP3 mp3 = new MP3("c:/mp3/praying mantis/time tells no lies/beads of ebony.mp3");
    *     mp3.setBand("Praying Mantis");
    *     System.out.println("the song " + mp3.getTitle() + " was released by " + mp3.getBand() + " on the album " + mp3.getAlbum());</pre></code>
    * @param band  the band who recorded the song.
    * @throws IllegalStateException   if the mp3 song was loaded from a URL or an input stream and is therefore considered to be read only and thus may not be modified.
    * @see #getBand()
    * @see #removeBand()
    */
   public void setBand(String band) throws IllegalStateException
   {
      if (mp3File == null)
         throw new IllegalStateException(getReadOnlyErrorMessage());

      if (id3v23Tag != null)
         setV23Band(band);
      else
         setV24Band(band);
   }

   /**
    * removes the name of the band who recorded the song from the .mp3 file.
    * @throws IllegalStateException      if the mp3 song was loaded from a URL or an input stream and is therefore considered to be read only and thus may not be modified.
    * @see #getBand()
    * @see #setBand(String)
    */
   public void removeBand() throws IllegalStateException
   {
      if (mp3File == null)
         throw new IllegalStateException(getReadOnlyErrorMessage());

      if (id3v23Tag != null)
         removeV23Band();
      else
         removeV24Band();
   }

   /**
    * gets the comments about the song.
    * <br/></br><b>Note for ID3 tag experts:</b> While the ID3v2.x standards support storing multiple comments within an .mp3 file, with one comment for each of the 504
    * languages defined by the {@link com.beaglebuddy.id3.enums.Language ISO-639-2} standard, in practice this is never done, and as such, the MP3 class only supports
    * storing one comment in an .mp3 file.
    * <br/><br/><b>Example:</b><code><pre class="beaglebuddy">
    *     // load the song "Pas Plus de Seize Ans" from Voie de Fait's "Ange ou D�mon" album.
    *     MP3 mp3 = new MP3("c:/mp3/voie de fait/ange ou d�mon/pas plus de seize ans.mp3");
    *     if (mp3.getComments() == null)
    *        System.out.println("the song " + mp3.getTitle() + " does not have any comments");
    *     else
    *        System.out.println("the song " + mp3.getTitle() + " has a comment: " + mp3.getComments());</pre></code>
    * @return the comments about the song. If no comments have been specified, then null is returned.
    * @see #setComments(String)
    * @see #removeComments()
    */
   public String getComments()
   {
      return id3v23Tag != null ? getV23Comments(Language.ENG) : getV24Comments(Language.ENG);
   }

   /**
    * stores a comment about the song in the .mp3 file.
    * <br/></br><b>Note for ID3 tag experts:</b> While the ID3v2.x standards support storing multiple comments within an .mp3 file, with one comment for each of the 504
    * languages defined by the {@link com.beaglebuddy.id3.enums.Language ISO-639-2} standard, in practice this is never done, and as such, the MP3 class only supports
    * storing one comment in an .mp3 file.
    * <br/><br/><b>Example:</b><code><pre class="beaglebuddy">
    *     // load the song "Pas Plus de Seize Ans" from Voie de Fait's "Ange ou D�mon" album.
    *     MP3 mp3 = new MP3("c:/mp3/voie de fait/ange ou d�mon/pas plus de seize ans.mp3");
    *     mp3.setComments("I like this song");            // write a comment in english
    *     mp3.setComments("Das Lied gef�llt mir");        // write a comment in german  - overwrites the previous english comment
    *     mp3.setComments("Me gusta esta Canci�n");       // write a comment in spanish - overwrites the previous german  comment
    *     System.out.println("the song " + mp3.getTitle() + " has a comment: " + mp3.getComments());</pre></code>
    * @param comments  comment about the song.
    * @throws IllegalStateException   if the mp3 song was loaded from a URL or an input stream and is therefore considered to be read only and thus may not be modified.
    * @see #getComments()
    * @see #removeComments()
    */
   public void setComments(String comments) throws IllegalStateException
   {
      if (mp3File == null)
         throw new IllegalStateException(getReadOnlyErrorMessage());

      if (id3v23Tag != null)
         setV23Comments(Language.ENG, comments);
      else
         setV24Comments(Language.ENG, comments);
   }

   /**
    * removes the comment about the song from the .mp3 file.
    * @throws IllegalStateException      if the mp3 song was loaded from a URL or an input stream and is therefore considered to be read only and thus may not be modified.
    * @see #getComments()
    * @see #setComments(String)
    */
   public void removeComments() throws IllegalStateException
   {
      if (mp3File == null)
         throw new IllegalStateException(getReadOnlyErrorMessage());

      if (id3v23Tag != null)
         removeV23Comments();
      else
         removeV24Comments();
   }

   /**
    * gets the disc number of the cd on which the song was released.  This is useful for double album cds and boxed set collections.
    * <br/><br/><b>Example:</b><code><pre class="beaglebuddy">
    *     // load the song "Phoenix Rising" from "Voices Of Rock"'s "MMVII" album.
    *     MP3 mp3 = new MP3("c:/mp3/voices of rock/mmvii/phoenix rising.mp3");
    *     mp3.getDisc();            // get the disc number
    *     System.out.println("the .mp3 song " + mp3.getTitle() + "  is on cd disc #" + mp3.getDisc() + ".");</pre></code>
    * @return the disc number of the cd on which the song was released.
    * If no disc number has been specified, then 0 is returned.
    * @see #setDisc(int)
    * @see #removeDisc()
    */
   public int getDisc()
   {
      return id3v23Tag != null ? getV23Disc() : getV24Disc();
   }

   /**
    * sets the disc number of the cd on which the song was released.  This is useful for double album cds and boxed set collections.
    * <br/><br/><b>Example:</b><code><pre class="beaglebuddy">
    *     // load the song "Phoenix Rising" from "Voices Of Rock"'s "MMVII" album.
    *     MP3 mp3 = new MP3("c:/mp3/voices of rock/mmvii/phoenix rising.mp3");
    *     mp3.setDisc(2);            // set the disc number to 2
    *     System.out.println("the .mp3 song " + mp3.getTitle() + "  is on cd disc #" + mp3.getDisc() + ".");</pre></code>
    * @param disc  the disc number of the cd on which the song was released.  It must be >= 1.
    * @throws IllegalArgumentException   if the disc number is <= 0.
    * @throws IllegalStateException      if the mp3 song was loaded from a URL or an input stream and is therefore considered to be read only and thus may not be modified.
    * @see #getDisc()
    * @see #removeDisc()
    */
   public void setDisc(int disc) throws IllegalArgumentException, IllegalStateException
   {
      if (mp3File == null)
         throw new IllegalStateException(getReadOnlyErrorMessage());

      if (id3v23Tag != null)
         setV23Disc(disc);
      else
         setV24Disc(disc);
   }

   /**
    * removes the disc number of the cd on which song was released from the .mp3 file.  If {@link MP3#getDisc()} is subsequently called, it will return 0.
    * <br/><br/><b>Example:</b><code><pre class="beaglebuddy">
    *     // load the song "Phoenix Rising" from "Voices Of Rock"'s "MMVII" album.
    *     MP3 mp3 = new MP3("c:/mp3/voices of rock/mmvii/phoenix rising.mp3");
    *     mp3.removeDisc();            // removes the disc number from the .mp3 file
    *     System.out.println("the .mp3 song " + mp3.getTitle() + "  is on cd disc #" + mp3.getDisc() + " on the cd.");</pre></code>
    * @throws IllegalStateException      if the mp3 song was loaded from a URL or an input stream and is therefore considered to be read only and thus may not be modified.
    * @see #getDisc()
    * @see #setDisc(int)
    */
   public void removeDisc() throws IllegalStateException
   {
      if (mp3File == null)
         throw new IllegalStateException(getReadOnlyErrorMessage());

      if (id3v23Tag != null)
         removeV23Disc();
      else
         removeV24Disc();
   }

   /**
    * gets the name of the lead performer who recorded the .mp3 song.
    * On Windows machines, this is called the "Contributing artist".
    * <br/><br/><b>Example:</b><code><pre class="beaglebuddy">
    *     // load the song "Last Tribe" from Magnus Karlson's "FreeFall" album.
    *     MP3 mp3 = new MP3("c:/mp3/magnus karlson/freefall/last tribe.mp3");
    *     System.out.println("the song " + mp3.getTitle() + " was released by " + mp3.getLeadPerformer() + " on the album " + mp3.getAlbum());</pre></code>
    * @return the lead performer who recorded the .mp3 song. If no lead performer has been specified, then null is returned.
    * @see #setLeadPerformer(String)
    * @see #removeLeadPerformer()
    */
   public String getLeadPerformer()
   {
      return id3v23Tag != null ? getV23LeadPerformer() : getV24LeadPerformer();
   }

   /**
    * sets the name of the lead performer who recorded the .mp3 song.
    * <br/></br><b>Note for ID3 tag experts:</b> The Beaglebuddy MP3 class stores the name of the lead performer in the {@link com.beaglebuddy.id3.enums.v23.FrameType#LEAD_PERFORMER TPE1} frame.
    * <br/><br/><b>Example:</b><code><pre class="beaglebuddy">
    *     // load the song "Last Tribe" from Magnus Karlson's "FreeFall" album.
    *     MP3 mp3 = new MP3("c:/mp3/magnus karlson/freefall/last tribe.mp3");
    *     mp3.setleadPerformer("Magnus Karlson");
    *     System.out.println("the song " + mp3.getTitle() + " was released by " + mp3.getLeadPerformer() + " on the album " + mp3.getAlbum());</pre></code>
    * @param leadPerformer  the lead performer who recorded the song.
    * @throws IllegalStateException   if the mp3 song was loaded from a URL or an input stream and is therefore considered to be read only and thus may not be modified.
    * @see #getLeadPerformer()
    * @see #removeLeadPerformer()
    */
   public void setLeadPerformer(String leadPerformer) throws IllegalStateException
   {
      if (mp3File == null)
         throw new IllegalStateException(getReadOnlyErrorMessage());

      if (id3v23Tag != null)
         setV23LeadPerformer(leadPerformer);
      else
         setV24LeadPerformer(leadPerformer);
   }

   /**
    * removes the name of the lead performer who recorded the song from the .mp3 file.
    * @throws IllegalStateException      if the mp3 song was loaded from a URL or an input stream and is therefore considered to be read only and thus may not be modified.
    * @see #getLeadPerformer()
    * @see #setLeadPerformer(String)
    */
   public void removeLeadPerformer() throws IllegalStateException
   {
      if (mp3File == null)
         throw new IllegalStateException(getReadOnlyErrorMessage());

      if (id3v23Tag != null)
         removeV23LeadPerformer();
      else
         removeV24LeadPerformer();
   }

   /**
    * gets the (english) lyrics to the song.
    * <br/><br/><b>Example:</b><code><pre class="beaglebuddy">
    *     // load the song "Lethal Heroes" from Pretty Maids' "Jump The Gun" album.
    *     MP3 mp3 = new MP3("c:/mp3/pretty maids/jump the gun/lethal heroes.mp3");
    *
    *     System.out.print("the song " + mp3.getTitle() + " was released by " + mp3.getBand() + " on the album " + mp3.getAlbum());
    *     if (mp3.getLyrics() == null)
    *        System.out.println(" does not contain any english lyrics.");
    *     else
    *        System.out.println(" contains the following english lyrics: \n" + mp3.getLyrics());</pre></code>
    * @return the (english) lyrics to the song.  If no (english) lyrics have been specified, then null is returned.
    * @see #getLyrics(Language)
    * @see #getLyricsBy()
    * @see #setLyrics(String)
    * @see #setLyrics(Language, String)
    * @see #getSynchronizedLyrics()
    * @see #removeLyrics()
    * @see #removeLyrics(Language)
    */
   public String getLyrics()
   {
      return getLyrics(Language.ENG);
   }

   /**
    * gets the lyrics to the song in the specified language.
    * <br/><br/><b>Example:</b><code><pre class="beaglebuddy">
    *     // load the song "Lethal Heroes" from Pretty Maids' "Jump The Gun" album.
    *     MP3 mp3 = new MP3("c:/mp3/pretty maids/jump the gun/lethal heroes.mp3");
    *
    *     System.out.print("the song " + mp3.getTitle() + " was released by " + mp3.getBand() + " on the album " + mp3.getAlbum());
    *     if (mp3.getLyrics(Language.DEU) == null)
    *        System.out.println(" does not contain any german lyrics.");
    *     else
    *        System.out.println(" contains the following german lyrics: \n" + mp3.getLyrics(Language.DEU));</pre></code>
    * @param language   the ISO-639-2 {@link com.beaglebuddy.id3.enums.Language language} the lyrics are written in.
    * @return the lyrics to the song in the specified language.  If no lyrics have been specified in the specified language, then null is returned.
    * @see #getLyrics()
    * @see #getLyricsBy()
    * @see #setLyrics(String)
    * @see #setLyrics(Language, String)
    * @see #getSynchronizedLyrics()
    * @see #removeLyrics()
    * @see #removeLyrics(Language)
    */
   public String getLyrics(Language language)
   {
      return id3v23Tag != null ? getV23UnsynchronizedLyrics(language) : getV24UnsynchronizedLyrics(language);
   }

   /**
    * sets the (english) lyrics to the song.
    * <br/><br/><b>Example:</b><code><pre class="beaglebuddy">
    *     // load the song "Lethal Heroes" from Pretty Maids' "Jump The Gun" album and set the "english" lyrics to the song.
    *     MP3 mp3 = new MP3("c:/mp3/pretty maids/jump the gun/lethal heroes.mp3");
    *     mp3.setLyrics("Get a little frightened sometimes\nA little cold inside\nCatching bad news on the radio\n...");</pre></code>
    * @param lyrics  the (english) lyrics to the song.
    * @throws IllegalStateException   if the mp3 song was loaded from a URL or an input stream and is therefore considered to be read only and thus may not be modified.
    * @see #getLyrics()
    * @see #getLyrics(Language)
    * @see #getLyricsBy()
    * @see #getSynchronizedLyrics()
    * @see #removeLyrics()
    * @see #removeLyrics(Language)
    * @see #setLyrics(Language,String)
    */
   public void setLyrics(String lyrics) throws IllegalStateException
   {
      setLyrics(Language.ENG, lyrics);
   }

   /**
    * sets the lyrics to the song using the specified language.
    * <br/><br/><b>Example:</b><code><pre class="beaglebuddy">
    *     // load the song "Lethal Heroes" from Pretty Maids' "Jump The Gun" album and set the "german" lyrics to the song.
    *     MP3 mp3 = new MP3("c:/mp3/pretty maids/jump the gun/lethal heroes.mp3");
    *     mp3.setLyrics(Language.DEU, "Get a little frightened sometimes\nA little cold inside\nCatching bad news on the radio\n...");</pre></code>
    * @param language   the ISO-639-2 {@link com.beaglebuddy.id3.enums.Language language} the lyrics are written in.
    * @param lyrics     the lyrics to the song in the specified language.
    * @throws IllegalStateException   if the mp3 song was loaded from a URL or an input stream and is therefore considered to be read only and thus may not be modified.
    * @see #getLyrics()
    * @see #getLyrics(Language)
    * @see #getLyricsBy()
    * @see #setLyricsBy(String)
    * @see #getSynchronizedLyrics()
    * @see #removeLyrics()
    * @see #removeLyrics(Language)
    * @see #setLyrics(String)
    */
   public void setLyrics(Language language, String lyrics) throws IllegalStateException
   {
      if (mp3File == null)
         throw new IllegalStateException(getReadOnlyErrorMessage());

      if (id3v23Tag != null)
         setV23UnsynchronizedLyrics(language, lyrics);
      else
         setV24UnsynchronizedLyrics(language, lyrics);
   }

   /**
    * removes the (english) lyrics to the song from the .mp3 file.
    * @throws IllegalStateException      if the mp3 song was loaded from a URL or an input stream and is therefore considered to be read only and thus may not be modified.
    * @see #getLyrics()
    * @see #getLyrics(Language)
    * @see #getLyricsBy()
    * @see #removeLyrics(Language)
    * @see #getSynchronizedLyrics()
    * @see #setLyrics(String)
    * @see #setLyrics(Language, String)
    */
   public void removeLyrics() throws IllegalStateException
   {
      removeLyrics(Language.ENG);
   }

   /**
    * removes the lyrics in the specified language to the song from the .mp3 file.
    * @param language   the ISO-639-2 {@link com.beaglebuddy.id3.enums.Language language} the lyrics are written in.
    * @throws IllegalStateException      if the mp3 song was loaded from a URL or an input stream and is therefore considered to be read only and thus may not be modified.
    * @see #getLyrics()
    * @see #getLyrics(Language)
    * @see #getLyricsBy()
    * @see #getSynchronizedLyrics()
    * @see #removeLyrics()
    * @see #setLyrics(String)
    * @see #setLyrics(Language, String)
    */
   public void removeLyrics(Language language) throws IllegalStateException
   {
      if (mp3File == null)
         throw new IllegalStateException(getReadOnlyErrorMessage());

      if (id3v23Tag != null)
         removeV23UnsynchronizedLyrics(language);
      else
         removeV24UnsynchronizedLyrics(language);
   }

   /**
    * gets name of the person(s) who wrote the lyrics to the song.
    * If more than one person wrote the lyrics, their names are separated by a forward slash, "/".
    * <br/><br/><b>Example:</b><code><pre class="beaglebuddy">
    *     // load the song "The Clairvoyant" from Iron Maiden's "Seventh Son of a Seventh Son" album.
    *     MP3 mp3 = new MP3("c:/mp3/iron maiden/seventh son of a seventh son/the clairvoyant.mp3");
    *     System.out.println("the lyrics to the song " + mp3.getTitle() + " were written by " + mp3.getLyricsBy());</pre></code>
    * @return the person(s) who wrote the lyrics to the song.  If no lyricist has been specified, then null is returned.
    * @see #setLyricsBy(String)
    * @see #removeLyricsBy()
    * @see #getMusicBy()
    */
   public String getLyricsBy()
   {
      return id3v23Tag != null ? getV23LyricsBy() : getV24LyricsBy();
   }

   /**
    * sets the name of the person(s) who wrote the lyrics to the song.
    * If there is more than one person, separate their names by a forward slash, "/".
    * <br/><br/><b>Example:</b><code><pre class="beaglebuddy">
    *     // load the song "The Clairvoyant" from Iron Maiden's "Seventh Son of a Seventh Son" album.
    *     MP3 mp3 = new MP3("c:/mp3/iron maiden/seventh son of a seventh son/the clairvoyant.mp3");
    *     mp3.setLyricsBy("Steve Harris/Bruce Dickenson")
    *     System.out.println("the lyrics to the song " + mp3.getTitle() + " were written by " + mp3.getLyricsBy());</pre></code>
    * @param lyricist  the person(s) who wrote the lyrics to the song.
    * @throws IllegalStateException   if the mp3 song was loaded from a URL or an input stream and is therefore considered to be read only and thus may not be modified.
    * @see #getLyricsBy()
    * @see #removeLyricsBy()
    * @see #getMusicBy()
    */
   public void setLyricsBy(String lyricist) throws IllegalStateException
   {
      if (mp3File == null)
         throw new IllegalStateException(getReadOnlyErrorMessage());

      if (id3v23Tag != null)
         setV23LyricsBy(lyricist);
      else
         setV24LyricsBy(lyricist);
   }

   /**
    * removes the name of the person(s) who wrote the lyrics to the song from the .mp3 file.
    * @throws IllegalStateException      if the mp3 song was loaded from a URL or an input stream and is therefore considered to be read only and thus may not be modified.
    * @see #getLyricsBy()
    * @see #setLyricsBy(String)
    * @see #getMusicBy()
    */
   public void removeLyricsBy() throws IllegalStateException
   {
      if (mp3File == null)
         throw new IllegalStateException(getReadOnlyErrorMessage());

      if (id3v23Tag != null)
         removeV23LyricsBy();
      else
         removeV24LyricsBy();
   }

   /**
    * gets name of the person(s) who wrote the music to the song.
    * If more than one person wrote the music, their names are separated by a forward slash, "/".
    * <br/><br/><b>Example:</b><code><pre class="beaglebuddy">
    *     // load the song "Tell Me What You Want" from Zebra's 1st album, "Zebra".
    *     MP3 mp3 = new MP3("c:/mp3/zebra/zebra/tell me what you want.mp3");
    *     System.out.println("the music to the song " + mp3.getTitle() + " was written by " + mp3.getMusicBy());</pre></code>
    * @return the person(s) who wrote the music to the song. If no composer has been specified, then null is returned.
    * @see #setMusicBy(String)
    * @see #removeMusicBy()
    * @see #getLyricsBy()
    */
   public String getMusicBy()
   {
      return id3v23Tag != null ? getV23MusicBy() :getV24MusicBy();
   }

   /**
    * sets the name of the person(s) who wrote the music to the song.
    * If there is more than one person, separate their names by a forward slash, "/".
    * <br/><br/><b>Example:</b><code><pre class="beaglebuddy">
    *     // load the song "Tell Me What You Want" from Zebra's 1st album, "Zebra".
    *     MP3 mp3 = new MP3("c:/mp3/zebra/zebra/tell me what you want.mp3");
    *     mp3.setMusicBy("Randy Jackson");
    *     System.out.println("the music to the song " + mp3.getTitle() + " was written by " + mp3.getMusicBy());
    *
    *     // load the song "Living in a World" from Axxis' album, "Kingdom of the Night".
    *     MP3 mp3 = new MP3("c:/mp3/axxis/kingdom of the night/living in a world.mp3");
    *     mp3.setMusicBy("Bernhard Wei�/Walter Pietsch");
    *     System.out.println("the music to the song " + mp3.getTitle() + " was written by " + mp3.getMusicBy());
    * @param composer  the person(s) who wrote the music to the song.
    * @throws IllegalStateException   if the mp3 song was loaded from a URL or an input stream and is therefore considered to be read only and thus may not be modified.
    * @see #getMusicBy()
    * @see #removeMusicBy()
    * @see #getLyricsBy()
    */
   public void setMusicBy(String composer) throws IllegalStateException
   {
      if (mp3File == null)
         throw new IllegalStateException(getReadOnlyErrorMessage());

      if (id3v23Tag != null)
         setV23MusicBy(composer);
      else
         setV24MusicBy(composer);
   }

   /**
    * removes the name of the person(s) who wrote the music to the song from the .mp3 file.
    * @throws IllegalStateException      if the mp3 song was loaded from a URL or an input stream and is therefore considered to be read only and thus may not be modified.
    * @see #getMusicBy()
    * @see #setMusicBy(String)
    */
   public void removeMusicBy() throws IllegalStateException
   {
      if (mp3File == null)
         throw new IllegalStateException(getReadOnlyErrorMessage());

      if (id3v23Tag != null)
         removeV23MusicBy();
      else
         removeV24MusicBy();
   }

   /**
    * gets the category of music for this song.  see {@link com.beaglebuddy.id3.enums.Genre} or {@link #setMusicType(String)} for a list of standard music categories.
    * @return the music category for the type of music.
    * If the type of music has not been specified, then null is returned.
    * @see #setMusicType(Genre)
    * @see #setMusicType(String)
    */
   public String getMusicType()
   {
      return id3v23Tag != null ? getV23MusicType() : getV24MusicType();
   }

   /**
    * sets the category of music for this song.
    * @param genre   the song's music type.  That is, the type of music the song would be described as.
    * @throws IllegalStateException   if the mp3 song was loaded from a URL or an input stream and is therefore considered to be read only and thus may not be modified.
    * @see #getMusicType()
    * @see #setMusicType(String)
    */
    public void setMusicType(Genre genre) throws IllegalStateException
    {
      if (mp3File == null)
         throw new IllegalStateException(getReadOnlyErrorMessage());

      if (id3v23Tag != null)
         setV23MusicType(genre);
      else
         setV24MusicType(genre);
    }

   /**
    * sets the category of music for this song.
    * <p>
    * References to the ID3v1, ID3v2, and WinAmp genres (which are shown in the list below) can be made by starting with an opening parenthesis "(" followed
    * by a number from the genres list and ended with a closing parenthesis ")".<br/>
    * example: mp3.setMusicType("(17)");       // indicates "Rock"<br/>
    * </p>
    * <p>
    * This may optionally be followed by a refinement.  The refinement can either be another music category from the list, or it may be text.<br/>
    * If the refinement is a music category from the list below, then the refinement should have two opening parentheses, as shown in the following example.<br/>
    * Example: mp3.setMusicType("(17)((79)");     // indicates "Rock" with a refinement of "Hard Rock"<br/>
    * </p>
    * <p>
    * Otherwise, if the refinement is text, it can appear as in the example below.<br/>
    * example: mp3.setMusicType("(4)Eurodisco");   // indicates "Disco" with a refinement of "Eurodisco"<br/>
    * </p>
    * <table class="beaglebuddy">
    *    <thead>
    *       <tr><th class="beaglebuddy">Number</th><th class="beaglebuddy">Music Type</th></tr>
    *    </thead>
    *    <tbody>
    *       <tr><th class="beaglebuddy"                     colspan="2"><i>ID3v1 Extensions</i>    </th></tr>
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
    *       <tr><th class="beaglebuddy"                       colspan="2"><i>WinAmp Extensions</i> </th></tr>
    *       <tr><td class="beaglebuddy">80.  </td><td class="beaglebuddy">Folk                     </td></tr>
    *       <tr><td class="beaglebuddy">81.  </td><td class="beaglebuddy">Folk-Rock                </td></tr>
    *       <tr><td class="beaglebuddy">82.  </td><td class="beaglebuddy">National Folk            </td></tr>
    *       <tr><td class="beaglebuddy">83.  </td><td class="beaglebuddy">Swing                    </td></tr>
    *       <tr><td class="beaglebuddy">84.  </td><td class="beaglebuddy">Fast Fusion              </td></tr>
    *       <tr><td class="beaglebuddy">85.  </td><td class="beaglebuddy">Bebob                    </td></tr>
    *       <tr><td class="beaglebuddy">86.  </td><td class="beaglebuddy">Latin                    </td></tr>
    *       <tr><td class="beaglebuddy">87.  </td><td class="beaglebuddy">Revival                  </td></tr>
    *       <tr><td class="beaglebuddy">88.  </td><td class="beaglebuddy">Celtic                   </td></tr>
    *       <tr><td class="beaglebuddy">89.  </td><td class="beaglebuddy">Bluegrass                </td></tr>
    *       <tr><td class="beaglebuddy">90.  </td><td class="beaglebuddy">Avantgarde               </td></tr>
    *       <tr><td class="beaglebuddy">91.  </td><td class="beaglebuddy">Gothic Rock              </td></tr>
    *       <tr><td class="beaglebuddy">92.  </td><td class="beaglebuddy">Progressive Rock         </td></tr>
    *       <tr><td class="beaglebuddy">93.  </td><td class="beaglebuddy">Psychedelic Rock         </td></tr>
    *       <tr><td class="beaglebuddy">94.  </td><td class="beaglebuddy">Symphonic Rock           </td></tr>
    *       <tr><td class="beaglebuddy">95.  </td><td class="beaglebuddy">Slow Rock                </td></tr>
    *       <tr><td class="beaglebuddy">96.  </td><td class="beaglebuddy">Big Band                 </td></tr>
    *       <tr><td class="beaglebuddy">97.  </td><td class="beaglebuddy">Chorus                   </td></tr>
    *       <tr><td class="beaglebuddy">98.  </td><td class="beaglebuddy">Easy Listening           </td></tr>
    *       <tr><td class="beaglebuddy">99.  </td><td class="beaglebuddy">Acoustic                 </td></tr>
    *       <tr><td class="beaglebuddy">100. </td><td class="beaglebuddy">Humour                   </td></tr>
    *       <tr><td class="beaglebuddy">101. </td><td class="beaglebuddy">Speech                   </td></tr>
    *       <tr><td class="beaglebuddy">102. </td><td class="beaglebuddy">Chanson                  </td></tr>
    *       <tr><td class="beaglebuddy">103. </td><td class="beaglebuddy">Opera                    </td></tr>
    *       <tr><td class="beaglebuddy">104. </td><td class="beaglebuddy">Chamber Music            </td></tr>
    *       <tr><td class="beaglebuddy">105. </td><td class="beaglebuddy">Sonata                   </td></tr>
    *       <tr><td class="beaglebuddy">106. </td><td class="beaglebuddy">Symphony                 </td></tr>
    *       <tr><td class="beaglebuddy">107. </td><td class="beaglebuddy">Booty Brass              </td></tr>
    *       <tr><td class="beaglebuddy">108. </td><td class="beaglebuddy">Primus                   </td></tr>
    *       <tr><td class="beaglebuddy">109. </td><td class="beaglebuddy">Porn Groove              </td></tr>
    *       <tr><td class="beaglebuddy">110. </td><td class="beaglebuddy">Satire                   </td></tr>
    *       <tr><td class="beaglebuddy">111. </td><td class="beaglebuddy">Slow Jam                 </td></tr>
    *       <tr><td class="beaglebuddy">112. </td><td class="beaglebuddy">Club                     </td></tr>
    *       <tr><td class="beaglebuddy">113. </td><td class="beaglebuddy">Tango                    </td></tr>
    *       <tr><td class="beaglebuddy">114. </td><td class="beaglebuddy">Samba                    </td></tr>
    *       <tr><td class="beaglebuddy">115. </td><td class="beaglebuddy">Folklore                 </td></tr>
    *       <tr><td class="beaglebuddy">116. </td><td class="beaglebuddy">Ballad                   </td></tr>
    *       <tr><td class="beaglebuddy">117. </td><td class="beaglebuddy">Power Ballad             </td></tr>
    *       <tr><td class="beaglebuddy">118. </td><td class="beaglebuddy">Rhytmic Soul             </td></tr>
    *       <tr><td class="beaglebuddy">119. </td><td class="beaglebuddy">Freestyle                </td></tr>
    *       <tr><td class="beaglebuddy">120. </td><td class="beaglebuddy">Duet                     </td></tr>
    *       <tr><td class="beaglebuddy">121. </td><td class="beaglebuddy">Punk Rock                </td></tr>
    *       <tr><td class="beaglebuddy">122. </td><td class="beaglebuddy">Drum Solo                </td></tr>
    *       <tr><td class="beaglebuddy">123. </td><td class="beaglebuddy">A Capela                 </td></tr>
    *       <tr><td class="beaglebuddy">124. </td><td class="beaglebuddy">Euro-House               </td></tr>
    *       <tr><td class="beaglebuddy">125. </td><td class="beaglebuddy">Dance Hall               </td></tr>
    *       <tr><td class="beaglebuddy">126. </td><td class="beaglebuddy">Goa                      </td></tr>
    *       <tr><td class="beaglebuddy">127. </td><td class="beaglebuddy">Drum & Bass              </td></tr>
    *       <tr><td class="beaglebuddy">128. </td><td class="beaglebuddy">Club-House               </td></tr>
    *       <tr><td class="beaglebuddy">129. </td><td class="beaglebuddy">Hardcore                 </td></tr>
    *       <tr><td class="beaglebuddy">130. </td><td class="beaglebuddy">Terror                   </td></tr>
    *       <tr><td class="beaglebuddy">131. </td><td class="beaglebuddy">Indie                    </td></tr>
    *       <tr><td class="beaglebuddy">132. </td><td class="beaglebuddy">BritPop                  </td></tr>
    *       <tr><td class="beaglebuddy">133. </td><td class="beaglebuddy">Negerpunk                </td></tr>
    *       <tr><td class="beaglebuddy">134. </td><td class="beaglebuddy">Polsk Punk               </td></tr>
    *       <tr><td class="beaglebuddy">135. </td><td class="beaglebuddy">Beat                     </td></tr>
    *       <tr><td class="beaglebuddy">136. </td><td class="beaglebuddy">Christian Gangsta        </td></tr>
    *       <tr><td class="beaglebuddy">137. </td><td class="beaglebuddy">Heavy Metal              </td></tr>
    *       <tr><td class="beaglebuddy">138. </td><td class="beaglebuddy">Black Metal              </td></tr>
    *       <tr><td class="beaglebuddy">139. </td><td class="beaglebuddy">Crossover                </td></tr>
    *       <tr><td class="beaglebuddy">140. </td><td class="beaglebuddy">Contemporary C           </td></tr>
    *       <tr><td class="beaglebuddy">141. </td><td class="beaglebuddy">Christian Rock           </td></tr>
    *       <tr><td class="beaglebuddy">142. </td><td class="beaglebuddy">Merengue                 </td></tr>
    *       <tr><td class="beaglebuddy">143. </td><td class="beaglebuddy">Salsa                    </td></tr>
    *       <tr><td class="beaglebuddy">144. </td><td class="beaglebuddy">Thrash Metal             </td></tr>
    *       <tr><td class="beaglebuddy">145. </td><td class="beaglebuddy">Anime                    </td></tr>
    *       <tr><td class="beaglebuddy">146. </td><td class="beaglebuddy">JPop                     </td></tr>
    *       <tr><td class="beaglebuddy">147. </td><td class="beaglebuddy">SynthPop                 </td></tr>
    *       <tr><th class="beaglebuddy"                       colspan="2"><i>ID3v2 Extensions</i>  </th></tr>
    *       <tr><td class="beaglebuddy">RX   </td><td class="beaglebuddy">Remix                    </td></tr>
    *       <tr><td class="beaglebuddy">CR   </td><td class="beaglebuddy">Cover                    </td></tr>
    *    </tbody>
    * </table>
    * @param type  the music category for the type of music.
    * @throws IllegalStateException   if the mp3 song was loaded from a URL or an input stream and is therefore considered to be read only and thus may not be modified.
    * @see #getMusicType()
    * @see #removeMusicType()
    */
   public void setMusicType(String type) throws IllegalStateException
   {
      if (mp3File == null)
         throw new IllegalStateException(getReadOnlyErrorMessage());

      if (id3v23Tag != null)
         setV23MusicType(type);
      else
         setV24MusicType(type);
   }

   /**
    * removes the type of music for the song from the .mp3 file.
    * @throws IllegalStateException   if the mp3 song was loaded from a URL or an input stream and is therefore considered to be read only and thus may not be modified.
    * @see #getMusicType()
    * @see #setMusicType(String)
    */
   public void removeMusicType() throws IllegalStateException
   {
      if (mp3File == null)
         throw new IllegalStateException(getReadOnlyErrorMessage());

      if (id3v23Tag != null)
         removeV23MusicType();
      else
         removeV24MusicType();
   }

   /**
    * gets the attached picture for the specified picture type.
    * <br/><br/><b>Example:</b><code><pre class="beaglebuddy">
    *     // load the song "Take Hold of the Flame" from Queensryche's album, "The Warning".
    *     MP3             mp3             = new MP3("c:/mp3/queensryche/the warning/take hold of the flame.mp3");
    *     AttachedPicture attachedPicture = mp3.getPicture(PictureType.FRONT_COVER);
    *
    *     if (attachedPicture == null)
    *        System.out.println("no front cover image was stored in the mp3.");
    *     else
    *        System.out.println("the front cover image stored in the mp3 is " + attachedPicture);
    *
    *     attachedPicture = mp3.getPicture(PictureType.BAND_LOGO);
    *     if (attachedPicture == null)
    *        System.out.println("no band logo image was stored in the mp3.");
    *     else
    *        System.out.println("the band logo image stored in the mp3 is " + attachedPicture);</pre></code>
    * @return the attached picture for the specified picture type.  If no picture of the specified type is found in the .mp3 file, then null is returned.
    * @param type    {@link com.beaglebuddy.id3.enums.PictureType type} of picture.
    * @see #setPicture(PictureType, String)
    * @see #setPicture(PictureType, File)
    * @see #setPicture(AttachedPicture)
    * @see #getPictures()
    * @see #removePicture(PictureType)
    * @see #removePictures()
    * @see com.beaglebuddy.id3.v23.frame_body.ID3v23FrameBodyAttachedPicture#setPictureType(PictureType)
    */
   public AttachedPicture getPicture(PictureType type)
   {
      return id3v23Tag != null ? getV23AttachedPicture(type) : getV24AttachedPicture(type);
   }

   /**
    * gets all the pictures stored in the .mp3 file's tag.
    * <br/><br/><b>Example:</b><code><pre class="beaglebuddy">
    *     // load the song "Take Hold of the Flame" from Queensryche's album, "The Warning".
    *     MP3                   mp3              = new MP3("c:/mp3/queensryche/the warning/take hold of the flame.mp3");
    *     List&lt;AttachedPicture&gt; attachedPictures = mp3.getPictures();
    *
    *    // print out each picture's type, picture's mime type, optional description of the picture, and the number of bytes in the image
    *    if (attachedPictures.size() == 0)
    *    {
    *       System.out.println("No pictures were found in the .mp3 file");
    *    }
    *    else
    *    {
    *       System.out.println("" + attachedPictures.size() + " pictures were found in the .mp3 file");
    *       for(AttachedPicture picture : attachedPictures)
    *          System.out.println(picture);
    *    }</pre></code>
    *
    * @return a list of all the pictures in the .mp3 file's tag.  If no pictures are found in the tag, then an empty list is returned.
    * @see #setPicture(PictureType, String)
    * @see #setPicture(PictureType, File)
    * @see #setPicture(AttachedPicture)
    * @see #getPicture(PictureType)
    * @see #removePicture(PictureType)
    * @see #removePictures()
    */
   public List<AttachedPicture> getPictures()
   {
      List<AttachedPicture> attachedPictures = null;

      if (id3v23Tag != null)
         attachedPictures = getV23AttachedPictures();
      else
         attachedPictures = getV24AttachedPictures();

      return attachedPictures;
   }

   /**
    * adds the picture to the song.  The mime type of the image is derived from the image file's extension (ie, .jpg -> image/jpg).
    * <br/><br/><b>Example:</b><code><pre class="beaglebuddy">
    *     // load the song "Take Hold of the Flame" from Queensryche's album, "The Warning".
    *     MP3 mp3 = new MP3("c:/mp3/queensryche/the warning/take hold of the flame.mp3");
    *
    *     mp3.setPicture(PictureType.FRONT_COVER, "c:/mp3/queensryche/the warning/take hold of the flame.jpg");</pre></code>
    * @param type            {@link com.beaglebuddy.id3.enums.PictureType type} of picture.
    * @param imageFilename   path to the image file, such as a .jpg, .gif, .png, etc.
    * @throws IOException             if the specified image file can not be read.
    * @throws IllegalStateException   if the mp3 song was loaded from a URL or an input stream and is therefore considered to be read only and thus may not be modified.
    * @see #setPicture(PictureType, File)
    * @see #setPicture(AttachedPicture)
    * @see #getPicture(PictureType)
    * @see #getPictures()
    * @see #removePicture(PictureType)
    * @see #removePictures()
    */
   public void setPicture(PictureType type, String imageFilename) throws IOException, IllegalStateException
   {
      if (mp3File == null)
         throw new IllegalStateException(getReadOnlyErrorMessage());

      setPicture(type, new File(imageFilename));
   }

   /**
    * adds the picture to the song.  The mime type of the image is derived from the image file's extension (ie, .jpg -> image/jpg).
    * <br/><br/><b>Example:</b><code><pre class="beaglebuddy">
    *     // load the song "Take Hold of the Flame" from Queensryche's album, "The Warning".
    *     MP3 mp3 = new MP3("c:/mp3/queensryche/the warning/take hold of the flame.mp3");
    *
    *     mp3.setPicture(PictureType.FRONT_COVER, new File("c:/mp3/queensryche/the warning/take hold of the flame.jpg"));</pre></code>
    * @param type           {@link com.beaglebuddy.id3.enums.PictureType type} of picture.
    * @param imageFile      file, such as a .jpg, .gif, .png, etc. containing the image to be added to the .mp3 file.
    * @throws IOException             if the specified image file can not be read.
    * @throws IllegalStateException   if the mp3 song was loaded from a URL or an input stream and is therefore considered to be read only and thus may not be modified.
    * @see #setPicture(PictureType, String)
    * @see #setPicture(AttachedPicture)
    * @see #getPicture(PictureType)
    * @see #getPictures()
    * @see #removePicture(PictureType)
    * @see #removePictures()
    */
   public void setPicture(PictureType type, File imageFile) throws IOException, IllegalStateException
   {
      if (mp3File == null)
         throw new IllegalStateException(getReadOnlyErrorMessage());

      setPicture(new AttachedPicture(type, imageFile));
   }

   /**
    * adds the picture to the song.  The mime type of the image is derived from the image file's extension (ie, .jpg -> image/jpg).
    * <br/><br/><b>Example:</b><code><pre class="beaglebuddy">
    *     // load the song "Take Hold of the Flame" from Queensryche's album, "The Warning".
    *     MP3 mp3 = new MP3("c:/mp3/queensryche/the warning/take hold of the flame.mp3");
    *
    *     mp3.setPicture(PictureType.FRONT_COVER, new URL("http://www.beaglebuddy.com/content/downloads/mp3/take%20hold%20of%20the%20flame.front%20cover.jpg"));</pre></code>
    * @param type        {@link com.beaglebuddy.id3.enums.PictureType type} of picture.
    * @param imageURL    url of an image file, such as a .jpg, .gif, .png, etc.
    * @throws IOException             if the specified image file can not be read.
    * @throws IllegalStateException   if the mp3 song was loaded from a URL or an input stream and is therefore considered to be read only and thus may not be modified.
    * @see #setPicture(PictureType, File)
    * @see #setPicture(AttachedPicture)
    * @see #getPicture(PictureType)
    * @see #getPictures()
    * @see #removePicture(PictureType)
    * @see #removePictures()
    */
   public void setPicture(PictureType type, URL imageURL) throws IOException, IllegalStateException
   {
      if (mp3File == null)
         throw new IllegalStateException(getReadOnlyErrorMessage());

      setPicture(new AttachedPicture(type, imageURL));
   }

   /**
    * adds the attached picture to the song.
    * <br/><br/><b>Example:</b><code><pre class="beaglebuddy">
    *     // load the song "Take Hold of the Flame" from Queensryche's album, "The Warning".
    *     MP3             mp3             = new MP3("c:/mp3/queensryche/the warning/take hold of the flame.mp3");
    *     AttachedPicture attachedPicture = new AttachedPicture(PictureType.FRONT_COVER, "image/jpg", "front cover of cd", new File("c:/mp3/queensryche/the warning/take hold of the flame.jpg"));
    *
    *     mp3.setPicture(attachedPicture);</pre></code>
    * @param attachedPicture   picture to add to the .mp3 file.
    * @throws IllegalStateException   if the mp3 song was loaded from a URL or an input stream and is therefore considered to be read only and thus may not be modified.
    * @see #setPicture(PictureType, String)
    * @see #setPicture(PictureType, File)
    * @see #getPicture(PictureType)
    * @see #getPictures()
    * @see #removePicture(PictureType)
    * @see #removePictures()
    */
   public void setPicture(AttachedPicture attachedPicture) throws IllegalStateException
   {
      if (mp3File == null)
         throw new IllegalStateException(getReadOnlyErrorMessage());

      if (mp3File == null)
         throw new IllegalStateException(getReadOnlyErrorMessage());

      if (id3v23Tag != null)
         setV23AttachedPicture(attachedPicture);
      else
         setV24AttachedPicture(attachedPicture);
   }

   /**
    * removes the picture of the specified type from the .mp3 file.  If no picture of the specified type is found in the .mp3 file, then no action is taken.
    * <br/><br/><b>Example:</b><code><pre class="beaglebuddy">
    *     // load the song "Take Hold of the Flame" from Queensryche's album, "The Warning".
    *     MP3 mp3 = new MP3("c:/mp3/queensryche/the warning/take hold of the flame.mp3");
    *
    *     mp3.removePicture(PictureType.FRONT_COVER);</pre></code>
    * @param type   {@link com.beaglebuddy.id3.enums.PictureType type} of picture to remove from the .mp3 file.
    * @throws IllegalStateException      if the mp3 song was loaded from a URL or an input stream and is therefore considered to be read only and thus may not be modified.
    * @see #setPicture(PictureType, String)
    * @see #setPicture(PictureType, File)
    * @see #setPicture(AttachedPicture)
    * @see #getPicture(PictureType)
    * @see #getPictures()
    * @see #removePictures()
    */
   public void removePicture(PictureType type) throws IllegalStateException
   {
      if (mp3File == null)
         throw new IllegalStateException(getReadOnlyErrorMessage());

      if (id3v23Tag != null)
         removeV23AttachedPicture(type);
      else
         removeV24AttachedPicture(type);
   }

   /**
    * removes all the picture(s) from the .mp3 file.
    * <br/><br/><b>Example:</b><code><pre class="beaglebuddy">
    *     // load the song "Take Hold of the Flame" from Queensryche's album, "The Warning".
    *     MP3 mp3 = new MP3("c:/mp3/queensryche/the warning/take hold of the flame.mp3");
    *
    *     mp3.removePictures();</pre></code>
    * @return the list of attached pictures that were removed from the ID3v2.x tag.
    *         If the ID3v2.x tag does not contain any attached pictures, then an empty list is returned.
    * @throws IllegalStateException      if the mp3 song was loaded from a URL or an input stream and is therefore considered to be read only and thus may not be modified.
    * @see #setPicture(PictureType, String)
    * @see #setPicture(PictureType, File)
    * @see #setPicture(AttachedPicture)
    * @see #getPicture(PictureType)
    * @see #getPictures()
    * @see #removePicture(PictureType)
    */
   public List<AttachedPicture> removePictures() throws IllegalStateException
   {
      if (mp3File == null)
         throw new IllegalStateException(getReadOnlyErrorMessage());

      return id3v23Tag != null ? removeV23AttachedPictures() : removeV24AttachedPictures();
   }

   /**
    * gets the publisher of the song.
    * <br/><br/><b>Example:</b><code><pre class="beaglebuddy">
    *     // load the song "Soldier Of The Line" from Magnum's "Chase The Dragon" album.
    *     MP3 mp3 = new MP3("c:/mp3/magnum/chase the dragon/soldier of the line.mp3");
    *     System.out.println("the song " + mp3.getTitle() + " was published by " + mp3.getPublisher());</pre></code>
    * @return the publisher of the song. If no publisher has been specified, then null is returned.
    * @see #setPublisher(String)
    * @see #removePublisher()
    */
   public String getPublisher()
   {
      return id3v23Tag != null ? getV23Publisher() : getV24Publisher();
   }

   /**
    * sets the publisher of the the song.
    * <br/><br/><b>Example:</b><code><pre class="beaglebuddy">
    *     // load the song "Soldier Of The Line" from Magnum's "Chase The Dragon" album.
    *     MP3 mp3 = new MP3("c:/mp3/magnum/chase the dragon/soldier of the line.mp3");
    *     mp3.setPublisher("Jet Records");
    *     System.out.println("the song " + mp3.getTitle() + " was published by " + mp3.getPublisher());</pre></code>
    * @param publisher  the publisher of the song.
    * @throws IllegalStateException   if the mp3 song was loaded from a URL or an input stream and is therefore considered to be read only and thus may not be modified.
    * @see #getPublisher()
    * @see #removePublisher()
    */
   public void setPublisher(String publisher) throws IllegalStateException
   {
      if (mp3File == null)
         throw new IllegalStateException(getReadOnlyErrorMessage());

      if (id3v23Tag != null)
         setV23Publisher(publisher);
      else
         setV24Publisher(publisher);
   }

   /**
    * removes the publisher of the the song from the .mp3 file.
    * @throws IllegalStateException      if the mp3 song was loaded from a URL or an input stream and is therefore considered to be read only and thus may not be modified.
    * @see #getPublisher()
    * @see #setPublisher(String)
    */
   public void removePublisher() throws IllegalStateException
   {
      if (mp3File == null)
         throw new IllegalStateException(getReadOnlyErrorMessage());

      if (id3v23Tag != null)
         removeV23Publisher();
      else
         removeV24Publisher();
   }

   /**
    * gets the rating (ie, how much you like it) of the song.
    * <br/><br/><b>Example:</b><code><pre class="beaglebuddy">
    *     // load the song "Magic Power" from Triumph's "Allied Forces" album.
    *     MP3 mp3 = new MP3("c:/mp3/triumph/allied forces/magic power.mp3");
    *     if (mp3.getRating() == 0)
    *        System.out.println("the song " + mp3.getTitle() + " has not yet been rated.");
    *     else
    *        System.out.println("the song " + mp3.getTitle() + " was rated a " + mp3.getRating() + " out of 255.");</pre></code>
    * @return the rating of the song.  The rating is 1-255 where 1 is worst and 255 is best. 0 is unknown, or unrated.
    * If no song rating has been specified, then 0 is returned.
    * @see #setRating(int)
    * @see #removeRating()
    */
   public int getRating()
   {
      return id3v23Tag != null ? getV23Rating() : getV24Rating();
   }

   /**
    * sets the rating of the song.
    * <br/><br/><b>Example:</b><code><pre class="beaglebuddy">
    *     // load the song "Magic Power" from Triumph's "Allied Forces" album.
    *     MP3 mp3 = new MP3("c:/mp3/triumph/allied forces/magic power.mp3");
    *     mp3.getRating(240);
    *     System.out.println("the song " + mp3.getTitle() + " was rated a " + mp3.getRating() + " out of 255.");</pre></code>
    * @param rating   the rating of the song where 1 is worst and 255 is best. 0 is unknown, ie, the song has not yet been rated.
    * @throws IllegalStateException   if the mp3 song was loaded from a URL or an input stream and is therefore considered to be read only and thus may not be modified.
    * @see #getRating()
    * @see #removeRating()
    */
   public void setRating(int rating) throws IllegalStateException
   {
      if (mp3File == null)
         throw new IllegalStateException(getReadOnlyErrorMessage());

      if (id3v23Tag != null)
         setV23Rating(rating);
      else
         setV24Rating(rating);
   }

   /**
    * removes the rating from the .mp3 file.  This sets the rating to 0, which means "unrated", ie, the song has not yet been rated.
    * <br/><br/><b>Example:</b><code><pre class="beaglebuddy">
    *     // load the song "Magic Power" from Triumph's "Allied Forces" album.
    *     MP3 mp3 = new MP3("c:/mp3/triumph/allied forces/magic power.mp3");
    *     mp3.removeRating();
    *     System.out.println("the song " + mp3.getTitle() + " was rated a " + mp3.getRating() + " out of 255.");</pre></code>
    * @throws IllegalStateException      if the mp3 song was loaded from a URL or an input stream and is therefore considered to be read only and thus may not be modified.
    * @see #setRating(int)
    * @see #getRating()
    */
   public void removeRating() throws IllegalStateException
   {
      if (mp3File == null)
         throw new IllegalStateException(getReadOnlyErrorMessage());

      if (id3v23Tag != null)
         removeV23Rating();
      else
         removeV24Rating();
   }

   /**
    * gets the (english) synchronized lyrics to the song.  Synchronized lyrics are like those seen on Kareoke machines.  The words to the song are synchonized with the music.
    * Each syllable or word (or whatever size of text is considered to be convenient) has a corresponding timestamp indicating when in the song it occurs.
    * <br/><br/><b>Example:</b><code><pre class="beaglebuddy">
    *     // load the song "Long After Midnight" from Loudness's "Soldier Of Fortune" album.
    *     MP3                     mp3    = new MP3("c:/mp3/loudness/soldier of fortune/long after midnight.mp3");
    *     List&lt;SynchronizedLyric&gt; lyrics = mp3.getSynchronizedLyrics();
    *
    *     if (lyrics == null)
    *        System.out.println("the song " + mp3.getTitle() + " does not have any english kareoke lyrics.");
    *     else
    *        System.out.println("the song " + mp3.getTitle() + " contains " + lyrics.size() + " english synchonized lyrics.");</pre></code>
    * @return the synchronized (english) lyrics to the song.  If no synchronized (english) lyrics have been specified, then null is returned.
    * @see #setSynchronizedLyrics(List)
    * @see #setSynchronizedLyrics(Language, List)
    * @see #removeSynchronizedLyrics()
    * @see #removeSynchronizedLyrics(Language)
    * @see #getLyrics()
    * @see #getLyrics(Language)
    * @see "com.beaglebuddy.mp3.sample_code.Kareoke"
    */
   public List<SynchronizedLyric> getSynchronizedLyrics()
   {
      return getSynchronizedLyrics(Language.ENG);
   }

   /**
    * gets the synchronized lyrics to the song in the specified language.  Synchronized lyrics are like those seen on Kareoke machines.  The words to the song are synchonized with the music.
    * Each syllable or word (or whatever size of text is considered to be convenient) has a corresponding timestamp indicating when in the song it occurs.
    * <br/><br/><b>Example:</b><code><pre class="beaglebuddy">
    *     // load the song "Long After Midnight" from Loudness's "Soldier Of Fortune" album.
    *     MP3                     mp3    = new MP3("c:/mp3/loudness/soldier of fortune/long after midnight.mp3");
    *     List&lt;SynchronizedLyric&gt; lyrics = mp3.getSynchronizedLyrics(Language.SPA);
    *
    *     if (lyrics == null)
    *        System.out.println("the song " + mp3.getTitle() + " does not have any spanish kareoke lyrics.");
    *     else
    *        System.out.println("the song " + mp3.getTitle() + " contains " + lyrics.size() + " spanish synchonized lyrics.");</pre></code>
    * @param language   the ISO-639-2 {@link com.beaglebuddy.id3.enums.Language language} the lyrics are written in.
    * @return the synchronized lyrics to the song in the specified language.  If no synchronized lyrics in the specified language have been specified, then null is returned.
    * @see #setSynchronizedLyrics(List)
    * @see #setSynchronizedLyrics(Language, List)
    * @see #removeSynchronizedLyrics()
    * @see #removeSynchronizedLyrics(Language)
    * @see #getLyrics()
    * @see #getLyrics(Language)
    * @see "com.beaglebuddy.mp3.sample_code.Kareoke"
    */
   public List<SynchronizedLyric> getSynchronizedLyrics(Language language)
   {
      return id3v23Tag != null ? getV23SynchronizedLyrics(language) : getV24SynchronizedLyrics(language);
   }

   /**
    * sets the (english) synchronized lyrics to the song.  Synchronized lyrics are like those seen on Kareoke machines.  The words to the song are synchonized with the music.
    * Each syllable or word (or whatever size of text is considered to be convenient) has a corresponding timestamp indicating when in the song it occurs.
    * <br/><br/><b>Example:</b><code><pre class="beaglebuddy">
    *     // load the song "Long After Midnight" from Loudness's "Soldier Of Fortune" album.
    *     MP3                 mp3    = new MP3("c:/mp3/loudness/soldier of fortune/long after midnight.mp3");
    *     SynchronizedLyric[] lyrics = {new SynchronizedLyric("I"    , 33000),
    *                                   new SynchronizedLyric("can"  , 33400),
    *                                   new SynchronizedLyric("feel" , 33825),
    *                                   new SynchronizedLyric("a"    , 34000),
    *                                   new SynchronizedLyric("touch", 34500),
    *                                   etc...
    *                                                                          };
    *
    *     mp3.setSynchronizedLyrics(lyrics);</pre></code>
    * @param synchronizedLyrics  the synchronized (english) lyrics to the song.
    * @throws IllegalStateException   if the mp3 song was loaded from a URL or an input stream and is therefore considered to be read only and thus may not be modified.
    * @see #getSynchronizedLyrics()
    * @see #getSynchronizedLyrics(Language)
    * @see #setSynchronizedLyrics(List)
    * @see #setSynchronizedLyrics(Language, List)
    * @see #removeSynchronizedLyrics()
    * @see #removeSynchronizedLyrics(Language)
    * @see #setLyrics(String)
    * @see #setLyrics(Language, String)
    * @see "com.beaglebuddy.mp3.sample_code.Kareoke"
    */
   public void setSynchronizedLyrics(SynchronizedLyric[] synchronizedLyrics) throws IllegalStateException
   {
      setSynchronizedLyrics(Language.ENG, synchronizedLyrics);
   }

   /**
    * sets the synchronized lyrics to the song in the specified language.  Synchronized lyrics are like those seen on Kareoke machines.  The words to the song are synchonized with the music.
    * Each syllable or word (or whatever size of text is considered to be convenient) has a corresponding timestamp indicating when in the song it occurs.
    * <br/><br/><b>Example:</b><code><pre class="beaglebuddy">
    *     // load the song "Long After Midnight" from Loudness's "Soldier Of Fortune" album.
    *     MP3                 mp3    = new MP3("c:/mp3/loudness/soldier of fortune/long after midnight.mp3");
    *     SynchronizedLyric[] lyrics = {new SynchronizedLyric("I"    , 33000),
    *                                   new SynchronizedLyric("can"  , 33400),
    *                                   new SynchronizedLyric("feel" , 33825),
    *                                   new SynchronizedLyric("a"    , 34000),
    *                                   new SynchronizedLyric("touch", 34500),
    *                                   etc...
    *                                                                          };
    *
    *     mp3.setSynchronizedLyrics(Language.SPA, lyrics);</pre></code>
    * @param language            the ISO-639-2 {@link com.beaglebuddy.id3.enums.Language language} the lyrics are written in.
    * @param synchronizedLyrics  the synchronized lyrics to the song in the specified language.
    * @throws IllegalStateException   if the mp3 song was loaded from a URL or an input stream and is therefore considered to be read only and thus may not be modified.
    * @see #getSynchronizedLyrics()
    * @see #getSynchronizedLyrics(Language)
    * @see #setSynchronizedLyrics(List)
    * @see #setSynchronizedLyrics(Language, List)
    * @see #removeSynchronizedLyrics()
    * @see #removeSynchronizedLyrics(Language)
    * @see #setLyrics(String)
    * @see #setLyrics(Language, String)
    * @see "com.beaglebuddy.mp3.sample_code.Kareoke"
    */
   public void setSynchronizedLyrics(Language language, SynchronizedLyric[] synchronizedLyrics) throws IllegalStateException
   {
      if (mp3File == null)
         throw new IllegalStateException(getReadOnlyErrorMessage());

      Vector<SynchronizedLyric> lyrics = new Vector<SynchronizedLyric>();

      if (synchronizedLyrics != null && synchronizedLyrics.length != 0)
      {
         for(SynchronizedLyric synchronizedLyric : synchronizedLyrics)
            lyrics.add(synchronizedLyric);
      }
      setSynchronizedLyrics(language, lyrics);
   }

   /**
    * sets the (english) synchronized lyrics to the song.  Synchronized lyrics are like those seen on Kareoke machines.  The words to the song are synchonized with the music.
    * Each syllable or word (or whatever size of text is considered to be convenient) has a corresponding timestamp indicating when in the song it occurs.
    * <br/><br/><b>Example:</b><code><pre class="beaglebuddy">
    *     // load the song "Long After Midnight" from Loudness's "Soldier Of Fortune" album.
    *     MP3                     mp3    = new MP3("c:/mp3/loudness/soldier of fortune/long after midnight.mp3");
    *     List&lt;SynchronizedLyric&gt; lyrics = new List<SynchronizedLyric>();
    *
    *     lyrics.add(new SynchronizedLyric("I"    , 33000));
    *     lyrics.add(new SynchronizedLyric("can"  , 33400));
    *     lyrics.add(new SynchronizedLyric("feel" , 33825));
    *     lyrics.add(new SynchronizedLyric("a"    , 34000));
    *     lyrics.add(new SynchronizedLyric("touch", 34500));
    *                etc...
    *
    *     mp3.setSynchronizedLyrics(lyrics);</pre></code>
    * @param synchronizedLyrics  the (english) synchronized lyrics to the song.
    * @throws IllegalStateException   if the mp3 song was loaded from a URL or an input stream and is therefore considered to be read only and thus may not be modified.
    * @see #getSynchronizedLyrics()
    * @see #getSynchronizedLyrics(Language)
    * @see #setSynchronizedLyrics(Language, List)
    * @see #removeSynchronizedLyrics()
    * @see #removeSynchronizedLyrics(Language)
    * @see #setLyrics(String)
    * @see #setLyrics(Language, String)
    * @see "com.beaglebuddy.mp3.sample_code.Kareoke"
    */
   public void setSynchronizedLyrics(List<SynchronizedLyric> synchronizedLyrics) throws IllegalStateException
   {
      setSynchronizedLyrics(Language.ENG, synchronizedLyrics);
   }

   /**
    * sets the synchronized lyrics to the song in the specified language.  Synchronized lyrics are like those seen on Kareoke machines.  The words to the song are synchonized with the music.
    * Each syllable or word (or whatever size of text is considered to be convenient) has a corresponding timestamp indicating when in the song it occurs.
    * <br/><br/><b>Example:</b><code><pre class="beaglebuddy">
    *     // load the song "Long After Midnight" from Loudness's "Soldier Of Fortune" album.
    *     MP3                     mp3    = new MP3("c:/mp3/loudness/soldier of fortune/long after midnight.mp3");
    *     List&lt;SynchronizedLyric&gt; lyrics = new List<SynchronizedLyric>();
    *
    *     lyrics.add(new SynchronizedLyric("I"    , 33000));
    *     lyrics.add(new SynchronizedLyric("can"  , 33400));
    *     lyrics.add(new SynchronizedLyric("feel" , 33825));
    *     lyrics.add(new SynchronizedLyric("a"    , 34000));
    *     lyrics.add(new SynchronizedLyric("touch", 34500));
    *                etc...
    *
    *     mp3.setSynchronizedLyrics(Language.DEU, lyrics);</pre></code>
    * @param language            the ISO-639-2 {@link com.beaglebuddy.id3.enums.Language language} the lyrics are written in.
    * @param synchronizedLyrics  the synchronized lyrics to the song in the specified language.
    * @throws IllegalStateException   if the mp3 song was loaded from a URL or an input stream and is therefore considered to be read only and thus may not be modified.
    * @see #getSynchronizedLyrics()
    * @see #getSynchronizedLyrics(Language)
    * @see #setSynchronizedLyrics(List)
    * @see #removeSynchronizedLyrics()
    * @see #removeSynchronizedLyrics(Language)
    * @see #setLyrics(String)
    * @see #setLyrics(Language, String)
    * @see "com.beaglebuddy.mp3.sample_code.Kareoke"
    */
   public void setSynchronizedLyrics(Language language, List<SynchronizedLyric> synchronizedLyrics) throws IllegalStateException
   {
      if (mp3File == null)
         throw new IllegalStateException(getReadOnlyErrorMessage());

      if (synchronizedLyrics == null || synchronizedLyrics.size() == 0)
      {
         if (id3v23Tag != null)
            removeV23SynchronizedLyrics(language);
         else
            removeV24SynchronizedLyrics(language);
      }
      else
      {
         if (id3v23Tag != null)
            setV23SynchronizedLyrics(language, synchronizedLyrics);
         else
            setV24SynchronizedLyrics(language, synchronizedLyrics);
      }
   }

   /**
    * remove the (english) synchronized lyrics from the .mp3 song.
    * <br/><br/><b>Example:</b><code><pre class="beaglebuddy">
    *     // load the song "Long After Midnight" from Loudness's "Soldier Of Fortune" album.
    *     MP3 mp3 = new MP3("c:/mp3/loudness/soldier of fortune/long after midnight.mp3");
    *     mp3.removeSynchronizedLyrics();         // remove the (english) synchronized lyrics</pre></code>
    * @throws IllegalStateException      if the mp3 song was loaded from a URL or an input stream and is therefore considered to be read only and thus may not be modified.
    * @see #setLyrics(String)
    * @see #setLyrics(Language, String)
    * @see #getSynchronizedLyrics()
    * @see #getSynchronizedLyrics(Language)
    * @see #setSynchronizedLyrics(List)
    * @see #setSynchronizedLyrics(Language, List)
    * @see #removeSynchronizedLyrics()
    * @see #removeSynchronizedLyrics(Language)
    */
   public void removeSynchronizedLyrics() throws IllegalStateException
   {
      removeSynchronizedLyrics(Language.ENG);
   }

   /**
    * remove the synchronized lyrics in the specified language from the .mp3 song.
    * <br/><br/><b>Example:</b><code><pre class="beaglebuddy">
    *     // load the song "Long After Midnight" from Loudness's "Soldier Of Fortune" album.
    *     MP3 mp3 = new MP3("c:/mp3/loudness/soldier of fortune/long after midnight.mp3");
    *     mp3.removeSynchronizedLyrics(Language.SPA);         // remove the spanish synchronized lyrics</pre></code>
    * @param language    the ISO-639-2 {@link com.beaglebuddy.id3.enums.Language language} the lyrics are written in.
    * @throws IllegalStateException      if the mp3 song was loaded from a URL or an input stream and is therefore considered to be read only and thus may not be modified.
    * @see #setLyrics(String)
    * @see #setLyrics(Language, String)
    * @see #getSynchronizedLyrics()
    * @see #getSynchronizedLyrics(Language)
    * @see #setSynchronizedLyrics(List)
    * @see #setSynchronizedLyrics(Language, List)
    * @see #removeSynchronizedLyrics()
    * @see #removeSynchronizedLyrics(Language)
    */
   public void removeSynchronizedLyrics(Language language) throws IllegalStateException
   {
      if (mp3File == null)
         throw new IllegalStateException(getReadOnlyErrorMessage());

      if (id3v23Tag != null)
         removeV23SynchronizedLyrics(language);
      else
         removeV24SynchronizedLyrics(language);
   }

   /**
    * gets the name of the song stored in the .mp3 file.
    * <br/><br/><b>Example:</b><code><pre class="beaglebuddy">
    *     // load the song "The Arena" from Fates Warning's "Perfect Symmetry" album.
    *     MP3 mp3 = new MP3("c:/mp3/fates warning/perfect symmetry/the arena.mp3");
    *     System.out.println("the .mp3 file " + mp3.getPath() + " contains the song " + mp3.getTitle());</pre></code>
    * @return the name of the song. If no song name has been specified, then null is returned.
    * @see #setTitle(String)
    * @see #setTitleFromFilename()
    * @see #removeTitle()
    */
   public String getTitle()
   {
      return id3v23Tag != null ? getV23Title() : getV24Title();
   }

   /**
    * sets the name of the song stored in the .mp3 file.
    * <br/><br/><b>Example:</b><code><pre class="beaglebuddy">
    *     // load the song "The Arena" from Fates Warning's "Perfect Symmetry" album.
    *     MP3 mp3 = new MP3("c:/mp3/fates warning/perfect symmetry/the arena.mp3");
    *     mp3.setTitle("The Arena");
    *     System.out.println("the .mp3 file " + mp3.getPath() + " contains the song " + mp3.getTitle());</pre></code>
    * @param title  the name of the song.
    * @throws IllegalStateException   if the mp3 song was loaded from a URL or an input stream and is therefore considered to be read only and thus may not be modified.
    * @see #getTitle()
    * @see #setTitleFromFilename()
    * @see #removeTitle()
    */
   public void setTitle(String title) throws IllegalStateException
   {
      if (mp3File == null)
         throw new IllegalStateException(getReadOnlyErrorMessage());

      if (id3v23Tag != null)
         setV23Title(title);
      else
         setV24Title(title);
   }

   /**
    * sets the name of the song stored in the .mp3 file from the name of the .mp3 file on disk.
    * <br/><br/><b>Example:</b><code><pre class="beaglebuddy">
    *     // load the song "The Arena" from Fates Warning's "Perfect Symmetry" album.
    *     MP3 mp3 = new MP3("c:/mp3/fates warning/perfect symmetry/the arena.mp3");
    *     mp3.setTitleFromFilename();         // set the title to "the arena"
    *     System.out.println("the .mp3 file " + mp3.getPath() + " contains the song " + mp3.getTitle());</pre></code>
    * @throws IllegalStateException   if the mp3 song was loaded from a URL or an input stream and is therefore considered to be read only and thus may not be modified.
    * @see #getTitle()
    * @see #setTitle(String)
    * @see #removeTitle()
    */
   public void setTitleFromFilename() throws IllegalStateException
   {
      if (mp3File == null)
         throw new IllegalStateException(getReadOnlyErrorMessage());

      String title = mp3File.getPath();
      title = title.substring(0, title.length() - 4);  // remove .mp3 extension
      if (title.lastIndexOf(File.separatorChar) == -1)
      {
         String os = System.getProperty("os.name");
         if (os.equals("Windows") && title.lastIndexOf(":") != -1)
            title = title.substring(title.lastIndexOf(":") + 1, title.length());
      }
      else
      {
         title = title.substring(title.lastIndexOf(File.separatorChar) + 1, title.length());
      }
      if (id3v23Tag != null)
         setV23Title(title);
      else
         setV24Title(title);
   }

   /**
    * removes the name of the song from the .mp3 file.
    * <br/><br/><b>Example:</b><code><pre class="beaglebuddy">
    *     // load the song "The Arena" from Fates Warning's "Perfect Symmetry" album.
    *     MP3 mp3 = new MP3("c:/mp3/fates warning/perfect symmetry/the arena.mp3");
    *     mp3.removeTitle();         // remove the title
    *     System.out.println("the .mp3 file " + mp3.getPath() + " contains the song " + mp3.getTitle());</pre></code>
    * @throws IllegalStateException      if the mp3 song was loaded from a URL or an input stream and is therefore considered to be read only and thus may not be modified.
    * @see #getTitle()
    * @see #setTitle(String)
    * @see #setTitleFromFilename()
    */
   public void removeTitle() throws IllegalStateException
   {
      if (mp3File == null)
         throw new IllegalStateException(getReadOnlyErrorMessage());

      if (id3v23Tag != null)
         removeV23Title();
      else
         removeV24Title();
   }

   /**
    * gets the track number of the song.
    * <br/><br/><b>Example:</b><code><pre class="beaglebuddy">
    *     // load the song "O Father" from House of Lords' "Demons Down" album.
    *     MP3 mp3 = new MP3("c:/mp3/house of lords/demons down/o father.mp3");
    *     mp3.getTrack();            // get the track number
    *     System.out.println("the .mp3 song " + mp3.getTitle() + "  is song #" + mp3.getTrack() + " on the cd.");</pre></code>
    * @return the track number of the song.
    * If no track number has been specified, then 0 is returned.
    * @see #setTrack(int)
    * @see #removeTrack()
    */
   public int getTrack()
   {
      return id3v23Tag != null ? getV23Track() : getV24Track();
   }

   /**
    * sets the track number of the song.
    * <br/><br/><b>Example:</b><code><pre class="beaglebuddy">
    *     // load the song "O Father" from House of Lords' "Demons Down" album.
    *     MP3 mp3 = new MP3("c:/mp3/house of lords/demons down/o father.mp3");
    *     mp3.setTrack(2);            // set the track number to 2
    *     System.out.println("the .mp3 song " + mp3.getTitle() + "  is song #" + mp3.getTrack() + " on the cd.");</pre></code>
    * @param track  the track number of the song.  It must be >= 1.
    * @throws IllegalArgumentException   if the track number is <= 0.
    * @throws IllegalStateException      if the mp3 song was loaded from a URL or an input stream and is therefore considered to be read only and thus may not be modified.
    * @see #getTrack()
    * @see #removeTrack()
    */
   public void setTrack(int track) throws IllegalArgumentException, IllegalStateException
   {
      if (mp3File == null)
         throw new IllegalStateException(getReadOnlyErrorMessage());

      if (id3v23Tag != null)
         setV23Track(track);
      else
         setV24Track(track);
   }

   /**
    * removes the track number of the song from the .mp3 file.  If {@link MP3#getTrack()} is subsequently called, it will return 0.
    * <br/><br/><b>Example:</b><code><pre class="beaglebuddy">
    *     // load the song "O Father" from House of Lords' "Demons Down" album.
    *     MP3 mp3 = new MP3("c:/mp3/house of lords/demons down/o father.mp3");
    *     mp3.removeTrack();            // removes the track number from the .mp3 file
    *     System.out.println("the .mp3 song " + mp3.getTitle() + "  is song #" + mp3.getTrack() + " on the cd.");</pre></code>
    * @throws IllegalStateException      if the mp3 song was loaded from a URL or an input stream and is therefore considered to be read only and thus may not be modified.
    * @see #getTrack()
    * @see #setTrack(int)
    */
   public void removeTrack() throws IllegalStateException
   {
      if (mp3File == null)
         throw new IllegalStateException(getReadOnlyErrorMessage());

      if (id3v23Tag != null)
         removeV23Track();
      else
         removeV24Track();
   }

   /**
    * gets the version of the ID3 tag stored in the .mp3 song which holds all of the information about the .mp3 file.
    * @return the version of the ID3 tag stored in the .mp3 song which holds all of the information about the .mp3 file.
    */
   public ID3TagVersion getVersion()
   {
      return id3v23Tag != null ? id3v23Tag.getVersion() : id3v24Tag.getVersion();
   }

   /**
    * gets the year the song was recorded.
    * <br/><br/><b>Example:</b><code><pre class="beaglebuddy">
    *     // load the song "Lethal Heroes" from Pretty Maids' "Jump The Gun" album.
    *     MP3 mp3 = new MP3("c:/mp3/pretty maids/jump the gun/lethal heroes.mp3");
    *     System.out.println("the song " + mp3.getTitle() + "  was recorded in " + mp3.getYear());</pre></code>
    * @return the year the song was released.
    * If no year has been specified, then 0 is returned.
    * @see #setYear(int)
    * @see #removeYear()
    */
   public int getYear()
   {
      return id3v23Tag != null ? getV23Year() : getV24Year();
   }

   /**
    * sets the year the song was recorded.
    * <br/><br/><b>Example:</b><code><pre class="beaglebuddy">
    *     // load the song "Lethal Heroes" from Pretty Maids' "Jump The Gun" album.
    *     MP3 mp3 = new MP3("c:/mp3/pretty maids/jump the gun/lethal heroes.mp3");
    *     mp3.setYear(1990);
    *     System.out.println("the song " + mp3.getTitle() + "  was recorded in " + mp3.getYear());</pre></code>
    * @param year  the year the song was recorded.  It must be >= 1.
    * @throws IllegalArgumentException   if the year is <= 0.
    * @throws IllegalStateException      if the mp3 song was loaded from a URL or an input stream and is therefore considered to be read only and thus may not be modified.
    * @see #getYear()
    * @see #removeYear()
    */
   public void setYear(int year) throws IllegalArgumentException, IllegalStateException
   {
      if (mp3File == null)
         throw new IllegalStateException(getReadOnlyErrorMessage());

      if (id3v23Tag != null)
         setV23Year(year);
      else
         setV24Year(year);
   }

   /**
    * removes the year the song was released from the .mp3 file.
    * <br/><br/><b>Example:</b><code><pre class="beaglebuddy">
    *     // load the song "Lethal Heroes" from Pretty Maids' "Jump The Gun" album.
    *     MP3 mp3 = new MP3("c:/mp3/pretty maids/jump the gun/lethal heroes.mp3");
    *     mp3.removeYear();</pre></code>
    * @throws IllegalStateException      if the mp3 song was loaded from a URL or an input stream and is therefore considered to be read only and thus may not be modified.
    * @see #getYear()
    * @see #setYear(int)
    */
   public void removeYear() throws IllegalStateException
   {
      if (mp3File == null)
         throw new IllegalStateException(getReadOnlyErrorMessage());

      if (id3v23Tag != null)
         removeV23Year();
      else
         removeV24Year();
   }

   /**
    * returns a string representation of the mp3 file.
    * This is useful in debugging, as this method shows all of the internal details of the .mp3 file, including the ID3v2.x tag, the 1st mpeg audio frame, etc.
    * <br/><br/><b>Example:</b><code><pre class="beaglebuddy">
    *     // load the song "Knife in my Heart" from Sinner's "Dangerous Charm" album.
    *     MP3 mp3 = new MP3("c:/mp3/sinner/dangerous charm/knife in my heart.mp3");
    *     System.out.println(mp3);</pre></code>
    * @return string representation of the mp3 file.
    */
   @Override
   public String toString()
   {
      return super.toString();
   }
}

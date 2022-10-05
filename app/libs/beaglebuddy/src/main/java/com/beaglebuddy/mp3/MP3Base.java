package com.beaglebuddy.mp3;

import java.io.File;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Vector;

import com.beaglebuddy.ape.APETag;
import com.beaglebuddy.exception.ParseException;
import com.beaglebuddy.id3.enums.ID3TagVersion;
import com.beaglebuddy.id3.v1.ID3v1Tag;
import com.beaglebuddy.id3.v23.ID3v23Tag;
import com.beaglebuddy.id3.v23.ID3v23TagHeader;
import com.beaglebuddy.id3.v24.ID3v24Tag;
import com.beaglebuddy.id3.v24.ID3v24TagFooter;
import com.beaglebuddy.id3.v24.ID3v24TagHeader;
import com.beaglebuddy.lyrics3.Lyrics3v1Tag;
import com.beaglebuddy.lyrics3.Lyrics3v2Tag;
import com.beaglebuddy.mpeg.MPEGFrame;
import com.beaglebuddy.mpeg.MPEGFrameHeader;
import com.beaglebuddy.mpeg.enums.BitrateType;
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
 * This base class provides some underlying methods that help make the derived MP3 class's interface much cleaner and easier to understand.
 * Thus, this class provides lower level methods that are used by the MP3 class, but would otherwise clutter the MP3 class's public interface.
 * </p>
 */
public class MP3Base extends MP3BaseID3v24
{
   // data members
                                         /** if the mp3 file is loaded from a local file, then this data member will contain the path to the .mp3 file. Otherwise, this data member will be null.                                              */
   protected File         mp3File;       /** if the mp3 file is loaded from a URL,        then this data member will contain the URL  to the .mp3 file. Otherwise, this data member will be null.                                              */
   protected URL          mp3Url;        /** size (in bytes) of the .mp3 file.                                                                                                                                                                 */
   protected int          fileSize;      /** size (in bytes) of the ID3v2.x tag.                                                                                                                                                               */
   protected int          tagSize;       /** size (in bytes) of the audio portion of the .mp3 file.  That is, the number of bytes comprising the actual sound data for the song.                                                               */
   protected int          audioSize;     /** the 1st mpeg audio frame found after the end of the ID3v2.x tag.                                                                                                                                  */
   protected MPEGFrame    mpegFrame;     /** the actual bit rate for {@link com.beaglebuddy.mpeg.enums.BitrateType CBR} encoded .mp3 files and the average bit rate for {@link com.beaglebuddy.mpeg.enums.BitrateType VBR} encoded .mp3 files. */
   protected int          bitrate;       /** whether the mp3 file was encoded with a constant bit rate ({@link com.beaglebuddy.mpeg.enums.BitrateType CBR}) or with a variable bit rate ({@link com.beaglebuddy.mpeg.enums.BitrateType VBR}).  */
   protected BitrateType  bitrateType;   /** the optional ID3v1     tag which can found at the end of the .mp3 file, after the mp3 audio data.                                                                                                 */
   protected ID3v1Tag     id3v1Tag;      /** the optional Lyrics3v1 tag which can found at the end of the .mp3 file, after the mp3 audio data and before the ID3v1 tag.                                                                        */
   protected Lyrics3v1Tag lyrics3v1Tag;  /** the optional Lyrics3v2 tag which can found at the end of the .mp3 file, after the mp3 audio data and before the ID3v1 tag.                                                                        */
   protected Lyrics3v2Tag lyrics3v2Tag;  /** the optional APEv1 tag is found at the end of the .mp3 file, while the APEv2 tag can be found at the beginning or end of the .mp3 file.                                                           */
   protected APETag       apeTag;




   /**
    * constructor.
    * @param mp3File   path to the .mp3 file from which to get information.
    * @throws IOException   if there is a problem reading the .mp3 file.
    */
   protected MP3Base(String mp3File) throws IOException
   {
      this(new File(mp3File));
   }

   /**
    * constructor.
    * @param mp3File   .mp3 file from which to get information.
    * @throws IOException   if there is a problem reading the .mp3 file.
    */
   protected MP3Base(File mp3File) throws IOException
   {
      readMP3File(mp3File);
   }

   /**
    * constructor.
    * @param mp3Url   URL of an .mp3 file from which to get information.
    * @throws IOException   if there is a problem reading the .mp3 file.
    */
   protected MP3Base(URL mp3Url) throws IOException
   {
      BufferedInputStream inputStream = null;
      try
      {
         this.mp3Url = mp3Url;
         HttpURLConnection httpConnection = (HttpURLConnection)this.mp3Url.openConnection();
         httpConnection.connect();
         inputStream = new BufferedInputStream(mp3Url.openStream());
         readID3Tag(inputStream);
         this.fileSize  = httpConnection.getContentLength();
         this.tagSize   = id3v23Tag != null ? id3v23Tag.getSize() : id3v24Tag.getSize();
         this.audioSize = fileSize - tagSize;
         httpConnection.disconnect();

         readFirstMPEGFrame(inputStream);
      }
      finally
      {
         if (inputStream != null)
            try {inputStream.close();} catch (Exception ioex) { /* nothing can be done */ }
      }
   }

   /**
    * This constructor reads the ID3 tag(s), which contain the information about the song, from the .mp3 file pointed to by the specified input stream.
    * This constructor is usefull for reading .mp3 files from a compressed source (.zip file), from a 3rd party streaming source, etc.
    * @param inputStream   An input stream pointing to an .mp3 file.
    * @throws IOException  if there is a problem reading the contents of the .mp3 file from the input stream.
    */
   protected MP3Base(InputStream inputStream) throws IOException
   {
      readID3Tag(inputStream);
      this.tagSize   = id3v23Tag != null ? id3v23Tag.getSize() : id3v24Tag.getSize();
      this.audioSize = readFirstMPEGFrame(inputStream);     // this is actually incorrect - but we have no idea what kind of input stream we have or how large it is
      this.fileSize  = tagSize + audioSize;
   }

   /**
    * reads in and parses the mp3 file.
    * @param mp3File   .mp3 file from which to get information.
    * @throws IOException   if there is a problem reading the .mp3 file.
    */
   protected void readMP3File(File mp3File) throws IOException
   {
      BufferedInputStream inputStream = null;
      try
      {
         inputStream = new BufferedInputStream(new FileInputStream(mp3File));
         this.mp3File = mp3File;
         boolean tagCreated = readID3Tag(inputStream);
         this.fileSize  = (int)mp3File.length();
         this.tagSize   = id3v23Tag != null ? id3v23Tag.getSize() : id3v24Tag.getSize();
         this.audioSize = this.fileSize - this.tagSize;
         if (tagCreated)
         {  // reset the input stream to the end of the new ID3v2.3 tag that was added to the beginning of the .mp3 file
            inputStream = new BufferedInputStream(new FileInputStream(mp3File));
            inputStream.skip(tagSize);
         }

         readFirstMPEGFrame(inputStream);
         readID3v1Tag();
         readLyrics3vTag();
         readAPETag();
      }
      finally
      {
         if (inputStream != null)
            try {inputStream.close();} catch (Exception ioex) { /* nothing can be done */ }
      }
   }

   /**
    * reads in the ID3v2.x tag from the .mp3 file.  If the .mp3 file does not have an ID3v2.x tag, then an ID3v1 tag is searched for, and if found, values from it
    * are used to create an ID3v2.3 tag.
    * @return true if a new ID3v2.x tag was created, and false if an existing ID3v2.x tag was found.
    * @param inputStream   input stream pointing to the beginning of an .mp3 file
    * @throws IOException  if there is a problem reading the .mp3 file.
    */
   private boolean readID3Tag(InputStream inputStream) throws IOException
   {
      boolean tagCreated = false;

      this.id3v23Tag = null;
      this.id3v24Tag = null;

      ID3TagVersion id3TagVersion = ID3TagVersion.readVersion(inputStream);

      switch (id3TagVersion)
      {
         case NONE:
              // the .mp3 file does not contain an ID3v2.x tag.  create one.
              this.id3v23Tag = new ID3v23Tag();
              this.tagSize   = 0;
              this.id3v23Tag.setPadding(0);
              tagCreated = true;

              // if the .mp3 file is being read from a local file (on the user's hard drive for example), see if it has an ID3v1 tag
              // since the ID3v1 tag is found at the very end of an .mp3 file, you don't want to download the entire .mp3 file just to
              // read the ID3v1 tag if .mp3 file is being read from a URL.
              if (mp3File != null)
              {
                 try
                 {
                    // skip to the ID3v1 tag at the end of the file
                    // note: we read in the bytes for the ID3v2.3 tag header, so subtract those bytes as well
                    long tagOffset = mp3File.length() - ID3TagVersion.NUM_ID_BYTES - ID3v1Tag.TAG_SIZE;
                    if (skip(inputStream, tagOffset) == tagOffset)
                    {
                       ID3v1Tag id3v1Tag = new ID3v1Tag(inputStream, (int)tagOffset, getPath());
                       if (id3v1Tag.getAlbum().length()         != 0)
                          setV23Album(id3v1Tag.getAlbum());
                       if (id3v1Tag.getArtist().length()        != 0)
                          setV23Band(id3v1Tag.getArtist());
                       if (id3v1Tag.getTitle().length()         != 0)
                          setV23Title(id3v1Tag.getTitle());
                       if (id3v1Tag.getTrack()                  != 0)
                          setV23Track(id3v1Tag.getTrack());
                       if (id3v1Tag.getGenreAsString().length() != 0)
                          setV23MusicType("(" + (id3v1Tag.getGenre() & 0xFF )+ ")");                  // java treats byte as a signed value, while the ID3v2.3 spec treats bytes as unsigned.
                       if (id3v1Tag.getYear().length()          == 4)
                          setV23Year(Integer.parseInt(id3v1Tag.getYear()));
                    }
                 }
                 catch (Exception e)
                 {
                    // nothing to do.  hey, at least we tried.
                 }
                 inputStream.close();                                                                 // close the .mp3 file input stream
                 audioSize = (int)mp3File.length();                                                   // otherwise, the save() method can't rename the temp file to the .mp3 file
                 saveID3v23();                                                                        // save the newly added ID3v2.3 tag to the .mp3 file
              }
         break;
         case ID3V2_2:
              throw new IOException("An ID3v2.2 tag was found in the mp3 file but that version is not currently supported.");
         case ID3V2_3:
              this.id3v23Tag = new ID3v23Tag(inputStream);
         break;
         case ID3V2_4:
              this.id3v24Tag = new ID3v24Tag(inputStream);
         break;
         case ID3V2_4_FOOTER:
              throw new IOException("An ID3v2.4 footer tag was found at the beginning of the mp3 file but that is not currently supported.");
      }
      return tagCreated;
   }

   /**
    * reads in the first mpeg audio frame from the .mp3 file.  The audio portion of an .mp3 file is comprised of frames (not to be confused with the frames in an
    * ID3v2.x tag).  The 1st mpeg audio frame consists of a {@link com.beaglebuddy.mpeg.MPEGFrameHeader frame header}, {@link com.beaglebuddy.mpeg.MPEGSideInformation side information},
    * followed by some optional {@link com.beaglebuddy.mpeg.enums.BitrateType VBR} headers, and then by the {@link com.beaglebuddy.mpeg.MPEGAudioSamples audio data}.
    * This method searches the audio section of the .mp3 file until it finds the first valid mpeg audio frame header.
    * @return the number of bytes read in to determine the first valid mpeg audio frame.
    * @param inputStream   input stream pointing to the beginning of the audio portion of an .mp3 file.
    * @throws IOException  if there is a problem reading the MPEG audio section of the .mp3 file.
    */
   private int readFirstMPEGFrame(InputStream inputStream) throws IOException
   {
      boolean found        = false;
      byte[]  bytes        = new byte[MPEGFrameHeader.FRAME_HEADER_SIZE];       // bytes used to read in the mpeg audio frames from the .mp3 file
      int     filePosition = tagSize;                                           // start looking for MPEG audio frames after the ID3v2.x tag
              bitrateType  = BitrateType.CBR;                                   // assume the mp3 file is CBR encoded
              bitrate      = -1;                                                // the bitrate has not yet been set

      if (inputStream.read(bytes) != bytes.length)
         throw new IOException("Unable to read the 1st mpeg audio frame from the mp3 file.");

      do
      {
         try
         {
            mpegFrame = new MPEGFrame(bytes, inputStream);
            mpegFrame.setFilePosition(filePosition);
            filePosition += mpegFrame.getMPEGFrameHeader().getFrameSize();

            // see if the .mp3 file is encoded with a variable bit rate
            if ((mpegFrame.getXingHeader() != null && mpegFrame.getXingHeader().getBitrateType() == BitrateType.VBR) || mpegFrame.getVBRIHeader() != null)
               bitrateType = BitrateType.VBR;

            // try to read in the next mpeg audio frame just to make sure we haven't accidentally stumbled upon a false synch
            MPEGFrame mpegFrame2 = new MPEGFrame(inputStream);
            mpegFrame2.setFilePosition(filePosition);
            found = true;                             // if we found two MPEG audio frames in a row, then we're definitely good

            // if the .mp3 file is encoded at a constant bit rate, then we know the exact bit rate of the whole file
            // otherwise, if the .mp3 file is encoded at a variable bit rate, then the average bit rate will be calculated when the user calls the getBitrate() method for the first time
            if (bitrateType == BitrateType.CBR)
               this.bitrate = mpegFrame.getMPEGFrameHeader().getBitrate();
         }
         catch (ParseException ex)
         {
            // shift over 1 byte, and keep reading through the .mp3 file until a valid mpeg audio frame is found
            byte[] b = new byte[1];
            if (inputStream.read(b) != 1)
               throw new IOException("Unable to read the 1st mpeg audio frame from the mp3 file.");

            bytes[0] = bytes[1];
            bytes[1] = bytes[2];
            bytes[2] = bytes[3];
            bytes[3] = b[0];
            filePosition++;
         }
      } while (!found);

      return filePosition - tagSize;
   }

   /**
    * This method reads through the audio portion of an .mp3 file and validates the {@link com.beaglebuddy.mpeg.MPEGFrame MPEG audio frames}.  It makes sure that the MPEG audio frames:<br/><br/>
    * <ul>
    *    <li>are the correct type: MPEG 1 Layer |||</li>
    *    <li>are located where they are supposed to be</li>
    *    <li>have valid {@link com.beaglebuddy.mpeg.MPEGFrameHeader MPEG frame headers}</li>
    *    <li>have a valid structure ({@link com.beaglebuddy.mpeg.MPEGFrameHeader frame header} followed by {@link com.beaglebuddy.mpeg.MPEGSideInformation side information} followed by
    *        {@link com.beaglebuddy.mpeg.MPEGAudioSamples audio data})</li>
    *    <li>use the same bit rate for every audio sample when the mp3 file is encoded with a {@link com.beaglebuddy.mpeg.enums.BitrateType constant bit rate}</li>
    * </ul>
    * <br/>It also makes sure that:<br/><br/>
    * <ul>
    *    <li>{@link com.beaglebuddy.mpeg.XingHeader Xing}, {@link com.beaglebuddy.mpeg.LAMEHeader Lame}, and {@link com.beaglebuddy.mpeg.VBRIHeader VBRI} headers occur only in the 1st
    *        {@link com.beaglebuddy.mpeg.MPEGFrame MPEG audio frame}</li>
    *    <li>{@link com.beaglebuddy.mpeg.enums.BitrateType VBR} encoded .mp3 files contain a VBR header</li>
    * </ul>
    * <br/><b>note:</b></br>
    * This method should only be called on .mp3 files that are loaded from a file system.
    * This is due to the amount of time it takes to download and read through an .mp3 file that is loaded from a URL.
    * If invoked on an .mp3 loaded from a URL, this method will simply return an empty list, indicating no errors were found.
    * @return a list of errors found in the MPEG audio section of the .mp3 file.  If no errors were found, then an empty list is returned.
    * @throws IOException  if the .mp3 file can not be read.
    * @see <a href="http://id3.org/ID3v1"                                           target="_blank">ID3v1 specification</a>
    * @see <a href="http://wiki.hydrogenaud.io/index.php?title=APEv2_specification" target="_blank">APE v2 specification</a>
    * @see <a href="http://id3.org/Lyrics3v2"                                       target="_blank">LYRICS 3.2 specification</a>
    */
   public List<String> validateMPEGFrames() throws IOException
   {
      Vector<String> errors = new Vector<String>();

      if (mp3File != null)
      {
         BufferedInputStream inputStream  = new BufferedInputStream(new FileInputStream(mp3File));
         int                 filePosition = mpegFrame.getFilePosition();
         byte[]              bytes        = new byte[MPEGFrameHeader.FRAME_HEADER_SIZE];       // bytes used to read in the mpeg audio frames from the .mp3 file
         int                 prevBitrate  = mpegFrame.getMPEGFrameHeader().getBitrate();       // used to verify that CBR encoded .mp3 files use the same bitrate for all mpeg audio frames
         boolean             cbr          = true;                                              // whether all mpeg audio frames use the same bitrate
         int                 bitrates     = 0;                                                 // used to calculate the average bit rate for VBR encoded mp3 files
         int                 frameNum     = 0;                                                 // the number of the current mpeg audio frame being validated

         try
         {
            if (inputStream.skip(filePosition) != filePosition)
               throw new IOException("Unable to skip to the 1st mpeg audio frame in the mp3 file.");
            MPEGFrame mpegFrame = null;

            for(frameNum = 1; ; ++frameNum)
            {
               try
               {
                  mpegFrame = new MPEGFrame(inputStream);
                  mpegFrame.setFilePosition(filePosition);

                  filePosition += mpegFrame.getSize();

                  // make sure the .mp3 file consists of MPEG I Layer III audio frames
                  if (mpegFrame.getMPEGFrameHeader().getMPEGVersion() != MPEGVersion.MPEG_1 || mpegFrame.getMPEGFrameHeader().getLayer() != Layer.III)
                     errors.add("MPEG audio frame " + frameNum + " at file position " + filePosition + " uses incorrect codec " + mpegFrame.getMPEGFrameHeader().getCodec() + ".");

                  // if an MPEG audio frame other than the first one contains VBR information, then flag it as an error
                  if (frameNum != 1 && (mpegFrame.getXingHeader() != null || mpegFrame.getLAMEHeader() != null || mpegFrame.getVBRIHeader() != null))
                     errors.add("MPEG audio frame " + frameNum + " at file position " + filePosition + " contains VBR header(s).");

                  // see if all the mpeg audio frames are encoded at the same bitrate
                  if (mpegFrame.getMPEGFrameHeader().getBitrate() != prevBitrate)
                     cbr = false;

                  // if the frame size is wrong, then try and re-synch to the next MPEG audio frame
                  if (mpegFrame.getMPEGFrameHeader().getFrameSize() < 0)
                     throw new ParseException("Invalid MPEG audio frame size.");

                  prevBitrate = mpegFrame.getMPEGFrameHeader().getBitrate();      // store this mpeg audio frame's bitrate so that it can be compared to the next frame's bitrate
                  bitrates   += mpegFrame.getMPEGFrameHeader().getBitrate();      // sum up all the bitrates in order to obtain an average bitrate
               }
               catch (ParseException ex)
               {
                  // did we reach the end of the .mp3 file?
                  if (ex.getData() == null)
                     break;                                // then we're done validating

                  if (ex.getMessage().startsWith("EOF"))
                  {
                     errors.add("MPEG audio frame " + frameNum + " truncated at the end (" + filePosition + ") of the .mp3 file.");
                     break;
                  }

                  // see if we are near the end of the file and there is an ID3v1 tag, APE tag, or LYRICS3 tag
                  String tag = new String(ex.getData());
                  if (tag.startsWith("TAG") || tag.equals("APET") || tag.equals("LYRI"))
                     break;

                  // MPEG frame not found at expected location - attempt to re-synch
                  errors.add("MPEG audio frame " + frameNum  + " not found at expected file location " + filePosition + ".  Attempting to re-synch.");

                  boolean found = false;
                  while (!found)
                  {
                     // shift over 1 byte, and keep reading through the .mp3 file until a valid mpeg audio frame is found
                     byte[] b            = new byte[1];
                     int    numBytesRead = inputStream.read(b);
                     filePosition++;

                     // if we reached the end of the .mp3 file, then we're done validating
                     if (numBytesRead == -1)
                        break;

                     // some problem reading the .mp3 file
                     if (numBytesRead != 1)
                        throw new IOException("Error reading MPEG audio frame " + frameNum + " at file position " + filePosition + ".");

                     // see if these bytes are a valid mpeg audio frame header
                     bytes[0] = bytes[1];
                     bytes[1] = bytes[2];
                     bytes[2] = bytes[3];
                     bytes[3] = b[0];

                     try
                     {
                        mpegFrame = new MPEGFrame(bytes, inputStream);
                        errors.add("re-synched MPEG audio frame " + frameNum + " at file location " + filePosition + ".");
                        found = true;
                        filePosition += (mpegFrame.getMPEGFrameHeader().getFrameSize() - mpegFrame.getMPEGFrameHeader().getSize());
                        prevBitrate   =  mpegFrame.getMPEGFrameHeader().getBitrate();      // store this mpeg audio frame's bitrate so that it can be compared to the next frame's bitrate
                        bitrates     +=  mpegFrame.getMPEGFrameHeader().getBitrate();      // sum up all the bitrates in order to obtain an average bitrate
                     }
                     catch (ParseException e)
                     {
                        // see if we are near the end of the file and there is an ID3v1 tag, APE tag, or LYRICS tag
                        // if there is, then we have reached the end of the mpeg audio data, so we can stop validating
                        tag = new String(ex.getData());
                        if (tag.startsWith("TAG") || tag.equals("APET") || tag.equals("LYRI"))
                           break;
                        // keep trying to re-synch
                     }
                  }
                  if (!found)
                  {
                     errors.add("Unable to re-synch MPEG audio frame " + frameNum + ".");
                     break;
                  }
               }
            }
         }
         catch (Exception ex)
         {
            ex.printStackTrace();
            errors.add(getPath() + " - MPEG audio frame at file position " + filePosition + " has an error");
         }
         finally
         {
            if (inputStream != null)
               try {inputStream.close();} catch (Exception ioex) { /* nothing can be done */ }
         }
         // if the file is CBR encoded, then make sure that all of the mpeg audio frames were encoded at the same bitrate
         if (bitrateType == BitrateType.CBR && !cbr)
         {
            errors.add("VBR encoded .mp3 file does not contain any VBR headers.");
            bitrateType = BitrateType.VBR;
            bitrate = -1;
         }

         // if the .mp3 file is encoded with a variable bit rate, then set the bit rate to the average bit rate
         if (bitrateType == BitrateType.VBR && bitrate == -1)
            this.bitrate = Math.round((float)bitrates / (float)frameNum);
      }
      return errors;
   }

   /**
    * reads the optional {@link com.beaglebuddy.id3.v1.ID3v1Tag ID3v1 tag} from the .mp3 file and, if found, sets the id3v1Tag data member.
    * This method can only be called on mp3's loaded from a file.
    * @throws IOException              if there is a problem reading the .mp3 file.
    * @throws IllegalStateException    if the mp3 song was loaded from a URL and therefore will take a considerable amount of time to reach the
    *                                  {@link com.beaglebuddy.id3.v1.ID3v1Tag ID3v1 tag} at the end of the .mp3 file.
    */
   private void readID3v1Tag() throws IOException, IllegalStateException
   {
      if (mp3File == null)
        throw new IllegalStateException(getReadOnlyErrorMessage());

      RandomAccessFile file = null;
      // see if the .mp3 file has an ID3v1 tag
      try
      {
         file     = new RandomAccessFile(mp3File, "r");
         id3v1Tag = new ID3v1Tag(file);
      }
      catch (ParseException ex)
      {
         /** nothing to do.  the mp3 file simply does not have an optional ID3v1 tag */
      }
      catch (FileNotFoundException ex)
      {
         /* this can not happen */
      }
      finally
      {
         if (file != null)
            try {file.close(); } catch (Exception ex) { /* nothing can be done */  }
      }
   }

   /**
    * reads the optional Lyrics3 tag from the .mp3 file and, if found, sets the lyrics3v1Tag/lyrics3v2Tag data member.
    * @throws IOException if there is an error reading the .mp3 file.
    * @throws IllegalStateException  if if the mp3 song was loaded from a URL and therefore will take a considerable amount of time to reach the
    *                                {@link com.beaglebuddy.lyrics3.Lyrics3v2Tag Lyrics3v2Tag tag} at the end of the .mp3 file.
    */
   private void readLyrics3vTag() throws IOException
   {
      if (mp3File == null)
        throw new IllegalStateException(getReadOnlyErrorMessage());

      RandomAccessFile file = null;
      // see if the .mp3 file has a Lyrics3v2 tag
      try
      {
         file         = new RandomAccessFile(mp3File, "r");
         lyrics3v2Tag = new Lyrics3v2Tag(file);
      }
      catch (ParseException ex)
      {
         // see if the .mp3 file has a Lyrics3v1 tag
         try
         {
            lyrics3v1Tag = new Lyrics3v1Tag(file);
         }
         catch (ParseException pe)
         {
            /** nothing to do.  the mp3 file simply does not have an optional Lyrics3v1 tag */
         }
         catch (FileNotFoundException fe)
         {
            /* this can not happen */
         }
      }
      catch (FileNotFoundException ex)
      {
         /* this can not happen */
      }
      finally
      {
         if (file != null)
            try {file.close(); } catch (IOException ex) { /* nothing to be done */ }
      }
   }

   /**
    * reads the optional APE tag from the .mp3 file and, if found, sets the apeTag data member.
    */
   private void readAPETag() throws IOException
   {
      if (mp3File == null)
        throw new IllegalStateException(getReadOnlyErrorMessage());

      RandomAccessFile file = null;
      try
      {
         // skip to the end of the file before the ID3v1 tag
         file   = new RandomAccessFile(mp3File, "r");
         apeTag = new APETag(file);
      }
      catch (ParseException ex)
      {
         /** nothing to do.  the mp3 file simply does not have an optional APE tag */
      }
      catch (FileNotFoundException ex)
      {
         /* this can not happen */
      }
      finally
      {
         if (file != null) try {file.close(); } catch (IOException ex) { /* nothing to be done */ }
      }
   }

   /**
    * determines if the mp3 file had any errors in the information stored in the frames of the ID3v2.x tag.  If any of the ID3v2.x frames were invalid, then they are
    * removed from the ID3v2.x tag.
    * @return whether any invalid ID3v2.x frames were found in the ID3v2.x tag from the .mp3 file.
    * @see #getErrors()
    * @see #displayErrors(PrintStream)
    */
   public boolean hasErrors()
   {
      return id3v23Tag != null ? hasV23Errors() : hasV24Errors();
   }

   /**
    * gets a list of any errors found in the ID3v2.x tag.
    * @return a list of errors found in the ID3v2.x tag.  If no errors were found, then an empty list is returned.
    * @see #hasErrors()
    * @see #displayErrors(PrintStream)
    */
   public List<String> getErrors()
   {
      return id3v23Tag != null ? getV23Errors() : getV24Errors();
   }

   /**
    * displays any errors found in the ID3v2.x tag from the .mp3 file.  If no errors were found, then nothing is written to the print stream.
    * @param printStream   print stream used to write out the errors.
    * @see #getErrors()
    * @see #hasErrors()
    */
   public void displayErrors(PrintStream printStream)
   {
      List<String> errors = getErrors();

      if (errors.size() != 0)
      {
         printStream.println(getPath() + " had " + errors.size() + " invalid frames");
         for(String error : errors)
            printStream.println("   " + error);
      }
   }

   /**
    * gets the read only error message to display to users when they try to modify an .mp3 file loaded from a URL.
    * @return the read only error message to display to users when they try to modify an .mp3 file loaded from a URL.
    */
   protected String getReadOnlyErrorMessage()
   {
      return "The mp3 song " + (id3v23Tag != null ? getV23Title() : getV24Title()) + " was loaded from a URL, " + getPath() + ", and is therefore read only.";
   }

   /**
    * gets the path to the .mp3 file. This method may be called regardless of whether the .mp3 file was loaded from a file system, an input stream, or from a url.
    * <br/><br/><b>Example:</b><code><pre class="beaglebuddy">
    *     // load the song "Take Hold of the Flame" from Queensryche's album, "The Warning".
    *     MP3 mp3 = new MP3("c:/mp3/queensryche/the warning/take hold of the flame.mp3");
    *     System.out.println("the mp3 file was loaded from " + mp3.getPath());
    *
    *     // load the song "Take Hold of the Flame" from Queensryche's album, "The Warning".
    *     MP3 mp3 = new MP3("http://www.beaglebuddy.com/mp3/queensryche/the warning/take hold of the flame.mp3");
    *     System.out.println("the mp3 file was loaded from " + mp3.getPath());</pre></code>
    * @return the path to the .mp3 file.
    */
   public String getPath()
   {
      String path = null;

           if (mp3File != null) path = mp3File.getPath();
      else if (mp3Url  != null) path = mp3Url.toExternalForm();
      else                      path = "input stream";

      return path;
   }

   /**
    * returns the size (in bytes) of the .mp3 file.
    * @return the size (in bytes) of the .mp3 file.
    */
   public long getFileSize()
   {
      return fileSize;
   }

   /**
    * calculates the duration of the song (in seconds) from data found in the {@link com.beaglebuddy.mpeg.MPEGFrame mpeg audio frames}.
    * @return the length (in seconds) of the song.
    */
   protected int calculateAudioDuration()
   {
      if (bitrate == -1 && bitrateType == BitrateType.VBR)
      {
         try {validateMPEGFrames();} catch (IOException ex) {/* no code necessary */}
      }
      return bitrate == 0 ? 0 : (8 * audioSize / 1000) / bitrate;
   }

   /**
    * determines whether the mp3 file is encoded at a constant bit rate ({@link com.beaglebuddy.mpeg.enums.BitrateType CBR}).
    * @return    whether the mp3 file is encoded at a constant bit rate ({@link com.beaglebuddy.mpeg.enums.BitrateType CBR}).
    */
   public boolean isConstantBitRate()
   {
      return bitrateType == BitrateType.CBR;
   }

   /**
    * renames the temporary file name to the current .mp3 filename.
    * @param tempFile   temporary .mp3 file which will be renamed to the .mp3 file.
    * @param mp3File    current .mp3 file which will be deleted.
    * @throws IOException if the current .mp3 file can not be deleted or if the temporary .mp3 file can not be renamed to the current .mp3 file.
    */
   private static void rename(File tempFile, File mp3File) throws IOException
   {
      if (!mp3File.delete())
         throw new IOException("Unable to delete the file " + mp3File.getPath());
      if (!tempFile.renameTo(mp3File))
         throw new IOException("Unable to rename the file " + tempFile.getPath() + " to " + mp3File.getPath() + ".");
   }

   /**
    * determines whether the .mp3 file has an {@link com.beaglebuddy.id3.v1.ID3v1Tag ID3v1 tag} at the end of the .mp3 file.  This method can only be called on mp3's loaded from a file.
    * @return    whether the .mp3 file has an {@link com.beaglebuddy.id3.v1.ID3v1Tag ID3v1 tag} at the end of the .mp3 file.
    */
   public boolean hasID3v1Tag()
   {
      return id3v1Tag != null;
   }

   /**
    * gets the optional ID3v1 tag.
    * @return the optional ID3v1 tag, if present, and null otherwise.
    */
   public ID3v1Tag getID3v1Tag()
   {
      return id3v1Tag;
   }

   /**
    * determines whether the .mp3 file has an {@link com.beaglebuddy.id3.v24.ID3v24Tag ID3v2.4 tag} at the end of the .mp3 file.
    * @return    whether the .mp3 file has an {@link com.beaglebuddy.id3.v24.ID3v24Tag ID3v2.4 tag} at the end of the .mp3 file.
    * @throws IOException              if there is a problem reading the .mp3 file.
    * @throws IllegalStateException    if the mp3 song was loaded from a URL and therefore will take a considerable amount of time to reach the {@link com.beaglebuddy.id3.v24.ID3v24Tag ID3v2.4 tag}
    *                                  at the end of the .mp3 file.
    */
   public boolean hasID3v24TagAtEnd() throws IOException, IllegalStateException
   {
      return getID3v24TagFooterAtEnd() != null;
   }

   /**
    * determines whether the .mp3 file has a {@link com.beaglebuddy.ape.APETag APE tag} at the beginning or end of the .mp3 file.  This method can only be called on mp3's loaded from a file.
    * @return    whether the .mp3 file has a {@link com.beaglebuddy.ape.APETag APE tag} at the beginning or end of the .mp3 file.
    */
   public boolean hasAPETag()
   {
      return apeTag != null;
   }

   /**
    * gets the optional APE tag.
    * @return the optional APE tag, if present, and null otherwise.
    */
   public APETag getAPETag()
   {
      return apeTag;
   }

   /**
    * determines whether the .mp3 file has a {@link com.beaglebuddy.lyrics3.Lyrics3v1Tag Lyrics3v1 tag} at the end of the .mp3 file.  This method can only be called on mp3's loaded from a file.
    * @return    whether the .mp3 file has a {@link com.beaglebuddy.lyrics3.Lyrics3v1Tag Lyrics3v1 tag} at the end of the .mp3 file.
    */
   public boolean hasLyrics3v1Tag()
   {
      return lyrics3v1Tag != null;
   }

   /**
    * determines whether the .mp3 file has a {@link com.beaglebuddy.lyrics3.Lyrics3v2Tag Lyrics3v2 tag} at the end of the .mp3 file.  This method can only be called on mp3's loaded from a file.
    * @return    whether the .mp3 file has a {@link com.beaglebuddy.lyrics3.Lyrics3v2Tag Lyrics3v2 tag} at the end of the .mp3 file.
    */
   public boolean hasLyrics3v2Tag()
   {
      return lyrics3v2Tag != null;
   }

   /**
    * gets the optional Lyrics3v1 tag.
    * @return the optional Lyrics3v1 tag, if present, and null otherwise.
    */
   public Lyrics3v1Tag getLyrics3v1Tag()
   {
      return lyrics3v1Tag;
   }

   /**
    * gets the optional Lyrics3v2 tag.
    * @return the optional Lyrics3v2 tag, if present, and null otherwise.
    */
   public Lyrics3v2Tag getLyrics3v2Tag()
   {
      return lyrics3v2Tag;
   }

   /**
    * gets the first {@link com.beaglebuddy.mpeg.MPEGFrame} found after the ID3v2.x tag.  MP3 files store the audio data in MPEG audio frames.  They should occur one right
    * after the other and form an unbroken stream of MPEG audio frames.  Almost all of the mp3 programs and libraries available on the internet today find the first MPEG
    * audio frame by searching the .mp3 file following the ID3v2.x tag which is located at the beginning of the .mp3 file.  While this works most of the time, there is a
    * problem with this method.  It is possible that a "false synch" is encountered.  That is, what appears to be a valid MPEG audio frame is really just some other data that
    * just happens to look like an actual MPEG audio frame.  For this reason, the Beaglebuddy mp3 library checks to make sure that the first MPEG audio frame found is directly
    * followed by the second MPEG audio frame.  If it isn't, then the Beaglebuddy mp3 library continues searching until it finds two valid MPEG audio frames in a row.  When two
    * valid MPEG audio frames are found in a row, then we are reasonably certain that the beginning of the MPEG audio stream has indeed been found and not a "false synch".
    * Since the {@link com.beaglebuddy.mpeg.enums.BitrateType bit rate type}, {@link com.beaglebuddy.mp3.MP3#getCodec() codec},
    * {@link com.beaglebuddy.mpeg.MPEGFrameHeader#getFrequency() frequency}, {@link com.beaglebuddy.mp3.MP3#getBitrate() bit rate}, etc. are all obtained from the first MPEG
    * audio frame, you might see discrepancies between the Beaglebuddy mp3 library and other programs.  The reason is because the Beaglebuddy mp3 library is more stringent
    * when locating the first MPEG audio frame than are the other .mp3 programs.
    * @return the first mpeg audio frame found after the ID3v2.x tag.
    */
   public MPEGFrame getFirstMpegFrame()
   {
      return mpegFrame;
   }

   /**
    * gets the {@link com.beaglebuddy.id3.v24.ID3v24TagFooter ID3v2.4 tag footer} that is found at the end of an .mp3 file whenever an {@link com.beaglebuddy.id3.v24.ID3v24Tag ID3v2.4 tag}
    * is stored at the end of an .mp3 file instead of the beginning.
    * @return the ID3v2.4 tag footer found at the end of an .mp3 file if present.  Otherwise null is returned.
    * @throws IOException              if there is a problem reading the .mp3 file.
    * @throws IllegalStateException    if the mp3 song was loaded from a URL and therefore will take a considerable amount of time to reach the
    *                                  {@link com.beaglebuddy.id3.v24.ID3v24TagFooter ID3v2.4 tag footer} at the end of the .mp3 file.
    */
   private ID3v24TagFooter getID3v24TagFooterAtEnd() throws IOException, IllegalStateException
   {
      if (mp3File == null)
        throw new IllegalStateException(getReadOnlyErrorMessage());

      InputStream     inputStream = null;
      ID3v24TagFooter footer      = null;

      try
      {
         inputStream = new BufferedInputStream(new FileInputStream(mp3File));

         // see if there is an ID3v2.4 footer at the end of the .mp3 file
         // To speed up the process of locating an ID3v2.4 tag when searching from the end of a file, a footer must be added to the tag.
         long tagOffset = mp3File.length() - ID3v24TagFooter.TAG_FOOTER_SIZE;
         if (skip(inputStream, tagOffset) == tagOffset)
            footer = new ID3v24TagFooter(inputStream);
      }
      catch (ParseException ex)
      {
         // a valid ID3v2.4 tag footer was not found
         footer = null;
      }
      finally
      {
         if (inputStream != null)
            try {inputStream.close(); } catch (Exception ex) { /* nothing can be done */  }
      }
      return footer;
   }

   /**
    * gets the optional {@link com.beaglebuddy.id3.v24.ID3v24Tag ID3v2.4 tag} found at the end of an .mp3 file.  This really is a stupid addition to the Id3v2.4 standard.
    * @return the {@link com.beaglebuddy.id3.v24.ID3v24Tag ID3v2.4 tag} found at the end of an .mp3 file if present.  Otherwise null is returned.
    * @throws IOException              if there is a problem reading the .mp3 file.
    * @throws IllegalStateException    if the mp3 song was loaded from a URL and therefore will take a considerable amount of time to reach the {@link com.beaglebuddy.id3.v24.ID3v24Tag ID3v2.4 tag}
    *                                  at the end of the .mp3 file.
    */
   public ID3v24Tag getID3v24TagAtEnd() throws IOException, IllegalStateException
   {
      InputStream     inputStream   = null;
      ID3v24TagFooter footer        = getID3v24TagFooterAtEnd();
      ID3v24Tag       id3v24Tag     = null;
      ID3TagVersion   id3TagVersion = null;

      try
      {
         inputStream = new BufferedInputStream(new FileInputStream(mp3File));

         // get the byte offset within the .mp3 file where the ID3v2.4 tag starts after the audio portion from the Id3v2.4 tag footer
         long tagOffset = mp3File.length() - (footer.getTagSize() + ID3v24TagHeader.TAG_HEADER_SIZE + ID3v24TagFooter.TAG_FOOTER_SIZE);
         if (skip(inputStream, tagOffset) == tagOffset)
         {
            id3TagVersion = ID3TagVersion.readVersion(inputStream);
            if (id3TagVersion == ID3TagVersion.ID3V2_4)
               id3v24Tag = new ID3v24Tag(inputStream);
         }
      }
      finally
      {
         if (inputStream != null)
            try {inputStream.close(); } catch (Exception ex) { /* nothing can be done */  }
      }
      return id3v24Tag;
   }

   /**
    * remove the {@link com.beaglebuddy.id3.v1.ID3v1Tag ID3v1 tag} at the end of the .mp3 file.
    * This method simply truncates the end of the .mp3 file to remove the ID3v1 tag, and hence does not require a call to the {@link MP3#save()} method.
    * It does not save any changes to the ID3v2.x tag.
    * <br/><br/>
    * @throws IOException              if there was an error truncating the {@link com.beaglebuddy.id3.v1.ID3v1Tag ID3v1 tag} from the end of the .mp3 file.
    * @throws IllegalStateException    if the mp3 song was loaded from a URL and is therefore considered to be read only and thus may not be modified.
    */
   public void removeID3v1Tag() throws IOException
   {
      if (mp3File == null)
        throw new IllegalStateException(getReadOnlyErrorMessage());
      if (id3v1Tag == null)
        throw new IllegalStateException("The mp3 song " + (id3v23Tag != null ? getV23Title() : getV24Title()) + " does not contain an ID3v1 tag.");

      RandomAccessFile file = null;

      try
      {
         file = new RandomAccessFile(mp3File, "rwd");
         file.setLength(fileSize - ID3v1Tag.TAG_SIZE);    // truncate the last 128 bytes from the .mp3 file

         audioSize -= ID3v1Tag.TAG_SIZE;
         fileSize   = (int)mp3File.length();
         id3v1Tag   = null;
      }
      finally
      {
         if (file != null)
            try {file.close(); } catch (Exception ex) {/* no code necessary */}
      }
   }

   /**
    * remove the {@link com.beaglebuddy.lyrics3.Lyrics3v1Tag Lyrics3v1 tag} at the end of the .mp3 file.
    * This method simply truncates the end of the .mp3 file to remove the Lyrics3v1 tag, and hence does not require a call to the {@link MP3#save()} method.
    * It does not save any changes to the ID3v2.x tag.
    * Since the Lyrics3v1 tag is found after the mpeg audio and before the {@link com.beaglebuddy.id3.v1.ID3v1Tag ID3v1 tag} at the end of the .mp3 file,
    * removing the Lyrics3v1 tag from the .mp3 file will also remove the ID3v1 tag as well.
    * <br/><br/>
    * @throws IOException              if there was an error truncating the {@link com.beaglebuddy.lyrics3.Lyrics3v1Tag Lyrics3v1 tag} from the end of the .mp3 file.
    * @throws IllegalStateException    if the mp3 song was loaded from a URL and is therefore considered to be read only and thus may not be modified, or if the mp3 file
    *                                  does not contain a Lyrics3v1 tag.
    */
   public void removeLyrics3v1Tag() throws IOException
   {
      if (mp3File == null)
        throw new IllegalStateException(getReadOnlyErrorMessage());
      if (lyrics3v1Tag == null)
        throw new IllegalStateException("The mp3 song " + (id3v23Tag != null ? getV23Title() : getV24Title()) + " does not contain a Lyrics3v1 tag.");

      RandomAccessFile file         = null;
      int              bytesRemoved = (int)(mp3File.length() - lyrics3v1Tag.getFilePosition());

      try
      {
         file = new RandomAccessFile(mp3File, "rwd");         // truncate all bytes from the end of the .mp3 file starting at the Lyrics3v1 tag
         file.setLength(lyrics3v1Tag.getFilePosition());      // this may remove the optional APE tag, if it is present, and also the ID3v1 tag

         // if the .mp3 file has an APE tag and it is found after the lyrics3v1 tag, then it too is truncated
         if (apeTag != null && apeTag.getFilePosition() > lyrics3v1Tag.getFilePosition())
            apeTag = null;

         audioSize   -= bytesRemoved;
         fileSize     = (int)mp3File.length();
         id3v1Tag     = null;
         lyrics3v1Tag = null;
      }
      finally
      {
         if (file != null)
            try {file.close(); } catch (Exception ex) {/* no code necessary */}
      }
   }

   /**
    * remove the {@link com.beaglebuddy.lyrics3.Lyrics3v2Tag Lyrics3v2 tag} at the end of the .mp3 file.
    * This method simply truncates the end of the .mp3 file to remove the Lyrics3v2 tag, and hence does not require a call to the {@link MP3#save()} method.
    * It does not save any changes to the ID3v2.x tag.
    * Since the Lyrics3v2 tag is found after the mpeg audio and before the {@link com.beaglebuddy.id3.v1.ID3v1Tag ID3v1 tag} at the end of the .mp3 file,
    * removing the Lyrics3v2 tag from the .mp3 file will also remove the ID3v1 tag as well.
    * <br/><br/>
    * @throws IOException              if there was an error truncating {@link com.beaglebuddy.lyrics3.Lyrics3v2Tag Lyrics3v2 tag} from the end of the .mp3 file.
    * @throws IllegalStateException    if the mp3 song was loaded from a URL and is therefore considered to be read only and thus may not be modified, or if the mp3 file
    *                                  does not contain a Lyrics3v2 tag.
    */
   public void removeLyrics3v2Tag() throws IOException
   {
      if (mp3File == null)
        throw new IllegalStateException(getReadOnlyErrorMessage());
      if (lyrics3v2Tag == null)
        throw new IllegalStateException("The mp3 song " + (id3v23Tag != null ? getV23Title() : getV24Title()) + " does not contain a Lyrics3v2 tag.");

      RandomAccessFile file         = null;
      int              bytesRemoved = (int)(mp3File.length() - lyrics3v2Tag.getFilePosition());

      try
      {
         file = new RandomAccessFile(mp3File, "rwd");
         file.setLength(lyrics3v2Tag.getFilePosition());      // truncate all bytes from the end of the .mp3 file starting at the Lyrics3v2 tag

         // if the .mp3 file has an APE tag and it is found after the lyrics3v2 tag, then it too is truncated
         if (apeTag != null && apeTag.getFilePosition() > lyrics3v2Tag.getFilePosition())
            apeTag = null;

         audioSize   -= bytesRemoved;
         fileSize     = (int)mp3File.length();
         id3v1Tag     = null;
         lyrics3v2Tag = null;
      }
      finally
      {
         if (file != null)
            try {file.close(); } catch (Exception ex) {/* no code necessary */}
      }
   }

   /**
    * remove the {@link com.beaglebuddy.ape.APETag APE tag} at the end of the .mp3 file.
    * This method simply truncates the end of the .mp3 file to remove the APE tag, and hence does not require a call to the {@link MP3#save()} method.
    * It does not save any changes to the ID3v2.x tag.
    * Since the APE tag is found after the mpeg audio and before the {@link com.beaglebuddy.id3.v1.ID3v1Tag ID3v1 tag}, if present, at the end of the .mp3 file,
    * removing the APE tag from the end of the .mp3 file will also remove the ID3v1 tag, if it is present, as well.
    * <br/><br/>
    * @throws IOException              if there was an error truncating {@link com.beaglebuddy.ape.APETag APE tag} from the end of the .mp3 file.
    * @throws IllegalStateException    if the mp3 song was loaded from a URL and is therefore considered to be read only and thus may not be modified, or if the mp3 file
    *                                  does not contain an APE tag.
    */
   public void removeAPETag() throws IOException
   {
      if (mp3File == null)
        throw new IllegalStateException(getReadOnlyErrorMessage());
      if (apeTag == null)
        throw new IllegalStateException("The mp3 song " + (id3v23Tag != null ? getV23Title() : getV24Title()) + " does not contain an APE tag.");

      RandomAccessFile file         = null;
      int              bytesRemoved = (int)(mp3File.length() - apeTag.getFilePosition());

      try
      {
         file = new RandomAccessFile(mp3File, "rwd");
         file.setLength(apeTag.getFilePosition());      // truncate all bytes from the end of the .mp3 file starting at the APE tag

         // if the .mp3 file has a lyrics3v1 or lyrics3v2 tag and it is found after the APE tag, then it too is truncated
         if (lyrics3v1Tag != null && lyrics3v1Tag.getFilePosition() > apeTag.getFilePosition())
            lyrics3v1Tag = null;
         if (lyrics3v2Tag != null && lyrics3v2Tag.getFilePosition() > apeTag.getFilePosition())
            lyrics3v2Tag = null;

         audioSize -= bytesRemoved;
         fileSize   = (int)mp3File.length();
         id3v1Tag   = null;
         apeTag     = null;
      }
      finally
      {
         if (file != null)
            try {file.close(); } catch (Exception ex) {/* no code necessary */}
      }
   }

   /**
    * remove the {@link com.beaglebuddy.id3.v24.ID3v24Tag ID3v2.4 tag} at the end of the .mp3 file.  If the ID3v2.4 tag is not found at the absolute very end of
    * the .mp3 file, then no action is taken and the .mp3 file is left untouched.
    * <br/><br/>
    * @throws IOException              if there was an error truncating {@link com.beaglebuddy.id3.v24.ID3v24Tag ID3v2.4 tag} from the end of the .mp3 file.
    * @throws IllegalStateException    if the mp3 song was loaded from a URL and is therefore considered to be read only and thus may not be modified.
    */
   public void removeID3v24TagAtEnd() throws IOException
   {
      if (mp3File == null)
        throw new IllegalStateException(getReadOnlyErrorMessage());

      ID3v24TagFooter footer = getID3v24TagFooterAtEnd();

      if (footer != null)
      {
         RandomAccessFile file          = null;
         int              id3v24TagSize = footer.getTagSize() + ID3v24TagHeader.TAG_HEADER_SIZE + ID3v24TagFooter.TAG_FOOTER_SIZE;

         try
         {  // truncate the Id3v2.4 tag from the .mp3 file
            file = new RandomAccessFile(mp3File, "rwd");
            file.setLength(fileSize - id3v24TagSize);

            audioSize -= id3v24TagSize;
            fileSize   = (int)mp3File.length();
         }
         finally
         {
            if (file != null)
               try {file.close(); } catch (Exception ex) {/* nothing can be done */}
         }
      }
   }

   /**
    * gets the size of the padding inside the ID3v2.x tag.
    * @return the size of the padding, in bytes, inside the ID3v2.x tag.
    * @see #setID3v2xPadding(int)
    */
   public int getID3v2xPadding()
   {
      return id3v23Tag != null ? id3v23Tag.getPadding().length : id3v24Tag.getPadding().length;
   }

   /**
    * this method sets the padding inside the ID3v2.x tag to the specified amount and saves the .mp3 file.
    * If the .mp3 file has an ID3v2.4 tag which itself contains a footer, then the padding will not be set, as padding and the footer are mutually exclusive.
    * @param newPaddingSize   the new size, in bytes, that the ID3v2.x padding should be set to.
    * @throws IOException  if an error occurs while saving the .mp3 file.
    * @see #getID3v2xPadding()
    */
   public void setID3v2xPadding(int newPaddingSize) throws IOException
   {
      if (mp3File == null)
        throw new IllegalStateException(getReadOnlyErrorMessage());

      if (id3v23Tag != null)                                                       // if the .mp3 file has an ID3v2.3 tag
      {
         int oldPaddingSize = id3v23Tag.getPadding().length;                       // get the size of the padding in the tag before any changes were made
                              id3v23Tag.setBuffer();                               // save any changes to the tag's internal byte buffer
         int newTagSize     = id3v23Tag.getSize() - oldPaddingSize;                // get the size of the tag after any changes were made - this size is without any padding

         rewriteFileID3v2x(tagSize, newTagSize, newPaddingSize);                   // write the new ID3v2.3 tag along with the new padding size
         readMP3File(mp3File);                                                     // re-read the new mp3 file which will re-set all the tag sizes and file positions in the various
                                                                                   // tags such as the 1st mpeg frame, ape tag, lyrics3 tag, etc.)
      }
      else if (id3v24Tag.getFooter() == null)                                      // if the .mp3 file has an ID3v2.4 tag which does not have a footer
      {
         int oldPaddingSize = id3v24Tag.getPadding().length;                       // get the size of the padding in the tag before any changes were made
                              id3v24Tag.setBuffer();                               // save any changes to the tag's internal byte buffer
         int newTagSize     = id3v24Tag.getSize() - oldPaddingSize;                // get the size of the tag after any changes were made - this size is without any padding

         rewriteFileID3v2x(tagSize, newTagSize, newPaddingSize);                   // write the new ID3v2.4 tag along with the new padding size
         readMP3File(mp3File);                                                     // re-read the new mp3 file which will re-set all the tag sizes and file positions in the various
      }                                                                            // tags such as the 1st mpeg frame, ape tag, lyrics3 tag, etc.)
   }

   /**
    * save the {@link com.beaglebuddy.id3.v23.ID3v23Tag ID3v2.3 tag} to the .mp3 file.
    * This is a very messy method, and you really have to understand the ID3v2.3 structure to understand this method.
    * So, if you can, by all means, avoid reading the code in this method.
    * <br/><br/>
    * @throws IOException   if there was an error writing the {@link com.beaglebuddy.id3.v23.ID3v23Tag ID3v2.3 tag} to the .mp3 file.
    */
   protected void saveID3v23() throws IOException
   {
      if (audioSize != 0)
         setV23AudioSize(audioSize);                                            // set the size (in bytes) of the audio portion of the .mp3 in a TSIZ frame
      int oldTagSize     = tagSize;                                             // get the size of the tag before any changes were made, including padding
      int oldPaddingSize = id3v23Tag.getPadding().length;                       // get the size of the padding in the tag before any changes were made
                           id3v23Tag.setBuffer();                               // save any changes to the tag's internal byte buffer
      int newTagSize     = id3v23Tag.getSize() - oldPaddingSize;                // get the size of the tag after any changes were made - this size is without any padding


      // if the new ID3v2.3 tag is smaller than the old tag, so we can just re-use the old tag's space and adjust the padding
      if (newTagSize < oldTagSize)
      {
         ID3v23TagHeader header = id3v23Tag.getHeader();                         // header in ID3v2.3 tag where the tag size - sizeof(header) is stored
         id3v23Tag.setPadding(oldTagSize - newTagSize);                          // set the new padding size
         header.setTagSize(oldTagSize - ID3v23TagHeader.TAG_HEADER_SIZE);        // new tag fits inside the old tag size, just the amount of padding has changed
         header.setBuffer();                                                     // clear the dirty flag in the header
         RandomAccessFile file = new RandomAccessFile(mp3File, "rwd");           // open the mp3 file for writing
         id3v23Tag.save(file);                                                   // write the ID3v2.3 tag to the beginning of the .mp3 file
         file.close();
      }
      // otherwise, we need to re-write the whole .mp3 file so that we have enough space to accommodate the ID3v2.3 tag's new larger size.
      else
      {
         rewriteFileID3v2x(oldTagSize, newTagSize, ID3v23Tag.DEFAULT_PADDING_SIZE);
      }
      tagSize  = id3v23Tag.getSize();
      fileSize = (int)mp3File.length();
   }

   /**
    * saves the .mp3 file by rewriting the .mp3 file to a temp file, deleting the existing .mp3 file, and then renaming the temp file back to the .mp3 file.
    * @param oldTagSize      old size of the ID3v2.x tag, including padding.
    * @param newTagSize      new size of the ID3v2.x tag, excluding padding.
    * @param newPaddingSize  new size of the ID3v2.x tag's padding.
    * throws IOException  if an error occurs while saving the .mp3 file to disk
    */
   private void rewriteFileID3v2x(int oldTagSize, int newTagSize, int newPaddingSize) throws IOException
   {
      File             tempFile     = new File(mp3File.getPath() + ".tmp");   // name of the temporary .mp3 file
      FileOutputStream tempMp3File  = new FileOutputStream(tempFile);         // output stream used to write the bytes to the temp    .mp3 file
      FileInputStream  audioFile    = new FileInputStream (mp3File);          // input  stream used to read  the audio of the current .mp3 file
      byte[]           audio        = new byte[2048];                         // buffer used to read the audio bytes from the current .mp3 file to the temp .mp3 file

      if (id3v23Tag != null)
      {
         id3v23Tag.setPadding(newPaddingSize);                                // set the padding to the new size
         newTagSize += newPaddingSize;                                        // add the new padding size to the total tag size
         ID3v23TagHeader header = id3v23Tag.getHeader();                      // header in ID3v2.3 tag where the tag size - sizeof(header) is stored
         header.setTagSize(newTagSize - ID3v23TagHeader.TAG_HEADER_SIZE);     // tag size stored in the header doesn't include the header itself
         header.setBuffer();                                                  // clear the dirty flag in the header
         id3v23Tag.save(tempMp3File);                                         // save the new ID3v2.3 tag to the beginning of the new .mp3 file
      }
      else if (id3v24Tag != null)
      {
         id3v24Tag.setPadding(newPaddingSize);                                // set the padding to the new size
         newTagSize += newPaddingSize;                                        // add the new padding size to the total tag size
         ID3v24TagHeader header = id3v24Tag.getHeader();                      // header in ID3v2.4 tag where the tag size - sizeof(header) is stored
         header.setTagSize(newTagSize - ID3v24TagHeader.TAG_HEADER_SIZE);     // tag size stored in the header doesn't include the header itself
         header.setBuffer();                                                  // clear the dirty flag in the header
         id3v24Tag.save(tempMp3File);                                         // save the new ID3v2.4 tag to the beginning of the new .mp3 file
      }

      // copy the audio portion of the old .mp3 file to the new one
      int n = 0;
      int audioSizeWritten = 0;
      skip(audioFile, oldTagSize);                                            // skip to the audio portion of the current .mp3 file

      while ((n = audioFile.read(audio)) != -1)
      {
         audioSizeWritten+=n;
         tempMp3File.write(audio, 0, n);
      }
      tempMp3File.close();
      audioFile.close();

      if (audioSizeWritten != audioSize)
          throw new IOException("Error saving the audio portion.  Expected " + audioSize + " bytes, but saved " + audioSizeWritten + " bytes.");

      rename(tempFile, mp3File);
   }

   /**
    * save the {@link com.beaglebuddy.id3.v24.ID3v24Tag ID3v2.4 tag} to the .mp3 file.
    * This is a very messy method, and you really have to understand the ID3v2.4 structure to understand this method.
    * So, if you can, by all means, avoid reading the code in this method.
    * <br/><br/>
    * @throws IOException  if there was an error writing the {@link com.beaglebuddy.id3.v24.ID3v24Tag ID3v2.4 tag} to the .mp3 file.
    */
   protected void saveID3v24() throws IOException
   {
      ID3v24TagHeader header         = id3v24Tag.getHeader();                       // header in ID3v2.4 tag where the tag size - sizeof(header) is stored
      boolean         footerPresent  = header.isFooterPresent();                    // does the ID3v2.4 tag have a footer
      int             oldTagSize     = tagSize;                                     // get the size of the tag before any changes were made
      int             oldPaddingSize = id3v24Tag.getPadding().length;               // get the size of the padding in the tag before any changes were made
                                       id3v24Tag.setBuffer();                       // save any changes to the tag's internal byte buffer
      int             newTagSize     = id3v24Tag.getSize() -                        // get the size of the tag after any changes were made - this size is without any padding
                                      (header.isFooterPresent() ? 0 : oldPaddingSize);

      // if the new tag is smaller than the old tag, and doesn't have a footer, then we can just re-use the old tag's space and adjust the padding
      if (newTagSize < oldTagSize && !footerPresent)
      {
         id3v24Tag.setPadding(oldTagSize - newTagSize);
         header.setTagSize(oldTagSize - ID3v24TagHeader.TAG_HEADER_SIZE);           // new tag size is same as the old tag size, just the amount of padding has been reduced
         header.setBuffer();                                                        // clear the dirty flag in the header
         RandomAccessFile file = new RandomAccessFile(mp3File, "rwd");              // open the mp3 file for writing
         id3v24Tag.save(file);                                                      // write the ID3v2.4 tag to the beginning of the .mp3 file
         file.close();
      }
      // otherwise, we need to re-write the whole .m3 file so that we have enough space to accommodate the tag's new larger size.
      else
      {
         rewriteFileID3v2x(oldTagSize, newTagSize, footerPresent ? 0 : ID3v24Tag.DEFAULT_PADDING_SIZE);
      }
   }

   /**
    * this is a more robust implementation of the InputStream class's skip() method.
    * note: see http://bugs.sun.com/view_bug.do?bug_id=6204246
    * @return the number of bytes actually skipped.
    * @param numBytes the number of bytes to skip.
    * @throws IOException  if an error occurs while skipping over the bytes in the input stream.
    */
   private static long skip(InputStream inputStream, long numBytes) throws IOException
   {
      long totalNumBytesSkipped = 0L;
      long numBytesRemaining    = numBytes;

      while (numBytesRemaining > 0)
      {
         long bytesSkipped = inputStream.skip(numBytesRemaining);
         if (bytesSkipped > 0)
         {
             numBytesRemaining    -= bytesSkipped;
             totalNumBytesSkipped += bytesSkipped;
         }
         else if (bytesSkipped == 0)
         {
             // should we retry? lets read one byte
             if (inputStream.read() == -1)  // EOF
                break;
             numBytesRemaining--;
             totalNumBytesSkipped++;
         }
         else
         {
            throw new IOException("skip() returned a negative value, " + bytesSkipped + ".");
         }
      }
      return totalNumBytesSkipped;
   }

   /**
    * gets a string representation of the mp3 file.
    * @return string representation of the mp3 file.
    */
   @Override
   public String toString()
   {
      StringBuffer buffer = new StringBuffer();
      buffer.append("mp3 file.....: " + getPath()        + "\n");
      buffer.append("mp3 file size: " + fileSize         + " bytes\n");
      buffer.append("audio size...: " + audioSize        + " bytes\n");
      buffer.append("codec........: " + mpegFrame.getMPEGFrameHeader().getMPEGVersion() + " " + mpegFrame.getMPEGFrameHeader().getLayer() + "\n");
      buffer.append("bit rate.....: " + bitrate                                         + " kbits/s\n");
      buffer.append("bit rate type: " + bitrateType                                     + "\n");
      buffer.append("frequency....: " + mpegFrame.getMPEGFrameHeader().getFrequency()   + " hz\n");
      buffer.append("channel mode.: " + mpegFrame.getMPEGFrameHeader().getChannelMode() + "\n");
      if (id3v23Tag != null)
         buffer.append("ID3v2.3 tag..: " + id3v23Tag + "\n");
      else
         buffer.append("ID3v2.4 tag..: " + id3v24Tag + "\n");
      buffer.append(mpegFrame);
      if (lyrics3v2Tag != null)
         buffer.append("\n" + lyrics3v2Tag);
      if (apeTag != null)
         buffer.append("\n" + apeTag);
      if (id3v1Tag != null)
         buffer.append("\n" + id3v1Tag);

      return buffer.toString();
   }
}

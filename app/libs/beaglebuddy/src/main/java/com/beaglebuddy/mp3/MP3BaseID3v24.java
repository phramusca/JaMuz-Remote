package com.beaglebuddy.mp3;

import java.util.List;
import java.util.Vector;

import com.beaglebuddy.id3.enums.Genre;
import com.beaglebuddy.id3.enums.Language;
import com.beaglebuddy.id3.enums.PictureType;
import com.beaglebuddy.id3.enums.v24.Encoding;
import com.beaglebuddy.id3.enums.v24.FrameType;
import com.beaglebuddy.id3.pojo.AttachedPicture;
import com.beaglebuddy.id3.pojo.SynchronizedLyric;
import com.beaglebuddy.id3.v24.ID3v24Frame;
import com.beaglebuddy.id3.v24.ID3v24Tag;
import com.beaglebuddy.id3.v24.frame_body.ID3v24FrameBodyAttachedPicture;
import com.beaglebuddy.id3.v24.frame_body.ID3v24FrameBodyComments;
import com.beaglebuddy.id3.v24.frame_body.ID3v24FrameBodyPopularimeter;
import com.beaglebuddy.id3.v24.frame_body.ID3v24FrameBodySynchronizedLyricsText;
import com.beaglebuddy.id3.v24.frame_body.ID3v24FrameBodyTextInformation;
import com.beaglebuddy.id3.v24.frame_body.ID3v24FrameBodyUnsynchronizedLyrics;
import com.beaglebuddy.util.Utility;




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
 * This base class provides some underlying methods for working with an ID3v2.4 tag.
 * </p>
 * @see MP3BaseID3v24
 */
public class MP3BaseID3v24 extends MP3BaseID3v23
{
   // data members
                                            /** ID3v2.4 tag which holds all the information about an .mp3 file */
   protected ID3v24Tag id3v24Tag;



   /**
    * determines if the ID3v2.4 tag had any frame errors when it was read in.
    * @return whether any invalid frames were encountered while reading in the ID3v2.4 tag from the .mp3 file.
    */
   public boolean hasV24Errors()
   {
      return id3v24Tag.getInvalidFrames().size() != 0;
   }

   /**
    * gets a list of any frame errors encountered while reading in the ID3v2.4 tag.
    * @return a list of the frame errors that occurred while reading in the ID3v2.4 tag.
    *         If the ID3v2.4 tag was valid and did not contain any errors, then an empty list is returned.
    */
   public List<String> getV24Errors()
   {
      Vector<String>    errors        = new Vector<String>();
      List<ID3v24Frame> invalidFrames = id3v24Tag.getInvalidFrames();

      for(ID3v24Frame frame : invalidFrames)
         errors.add(frame.getInvalidMessage());

      return errors;
   }

   /**
    * gets the ID3v2.4 Tag.
    * @return the ID3v2.4 Tag.
    */
   public ID3v24Tag getID3v24Tag()
   {
      return id3v24Tag;
   }

   /**
    * add an ID3v2.4 frame of the specified type to the ID3v2.4 tag.
    * @param frameType   type of ID3v2.4 frame to add to the ID3v2.4 tag.
    * @return the new ID3v2.4 frame that was added to the ID3v2.4 tag.
    */
   public ID3v24Frame addV24Frame(FrameType frameType)
   {
      return id3v24Tag.addFrame(frameType);
   }

   /**
    * finds the first ID3v2.4 frame in the ID3v2.4 tag with the specified ID3v2.4 frame id.
    * @param frameType   type of ID3v2.4 frame to search for.
    * @return the first ID3v2.4 frame with the given ID3v2.4 frame id found in the ID3v2.4 tag, or null if no frame with the specified id can be found.
    */
   public ID3v24Frame getV24Frame(FrameType frameType)
   {
      return id3v24Tag.getFrame(frameType);
   }

   /**
    * finds all the ID3v2.4 frames in the ID3v2.4 tag with the specified ID3v2.4 frame id.
    * @param frameType   type of ID3v2.4 frames to retrieve from the ID3v2.4 tag.
    * @return a list of all the ID3v2.4 frames with the given ID3v2.4 frame id that were found in the ID3v2.4 tag,
    *         or an empty collection of size 0 if no ID3v2.4 frame with the specified ID3v2.4 id can be found.
    */
   public List<ID3v24Frame> getV24Frames(FrameType frameType)
   {
      return id3v24Tag.getFrames(frameType);
   }

   /**
    * removes the first ID3v2.4 frame with the specified ID3v2.4 frame id from the ID3v2.4 tag.
    * @param frameType   type of type ID3v2.4 frame to remove from the ID3v2.4 tag.
    * @return the ID3v2.4 frame in the ID3v2.4 tag with the given ID3v2.4 frame id that was removed, or null if no ID3v2.4 frame with the specified id was found.
    */
   public ID3v24Frame removeV24Frame(FrameType frameType)
   {
      return id3v24Tag.removeFrame(frameType);
   }

   /**
    * removes all the ID3v2.4 frames with the specified ID3v2.4 frame id from the ID3v2.4 tag.
    * @param frameType   type of ID3v2.4 frame to remove from the ID3v2.4 tag.
    * @return a list of all the ID3v2.4 frames with the given ID3v2.4 frame id that were removed from the ID3v2.4 tag,
    *         or an empty collection of size 0 if no ID3v2.4 frames with the specified ID3v2.4 id could be found.
    */
   public List<ID3v24Frame> removeV24Frames(FrameType frameType)
   {
      return id3v24Tag.removeFrames(frameType);
   }

   /**
    * finds the ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#ALBUM_TITLE} text frame in the ID3v2.4 tag and retrieves the text field.
    * @return the text field found in the ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#ALBUM_TITLE} text frame.
    *         If no ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#ALBUM_TITLE} text frame is found in the ID3v2.4 tag, then null is returned.
    */
   protected String getV24Album()
   {
      return getV24Text(FrameType.ALBUM_TITLE);
   }

   /**
    * adds an ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#ALBUM_TITLE} text frame with the specified album to the ID3v2.4 tag.
    * If the ID3v2.4 tag already contains an ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#ALBUM_TITLE} text frame, then the existing frame's text is simply updated.
    * Otherwise, a new ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#ALBUM_TITLE} text frame is added to the ID3v2.4 tag.
    * @param album        the name of the album on which the .mp3 song was released.  The text is encoded using the UTF-16 character set.
    * @return the ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#ALBUM_TITLE} text frame that was added/updated.
    */
   protected ID3v24Frame setV24Album(String album)
   {
      return setV24Text(album, FrameType.ALBUM_TITLE);
   }

   /**
    * removes the ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#ALBUM_TITLE} text frame in the ID3v2.4 tag.
    * @return the ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#ALBUM_TITLE} text frame that was removed.
    *         If the ID3v2.4 tag does not contain an ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#ALBUM_TITLE} text frame, then no frame is removed and null is returned.
    */
   protected ID3v24Frame removeV24Album()
   {
      return removeV24Frame(FrameType.ALBUM_TITLE);
   }

   /**
    * finds the ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#ATTACHED_PICTURE} frame in the ID3v2.4 tag and retrieves the attached picture.
    * @param pictureType   one of the 21 allowable ID3v2.4 picture types.
    * @return the attached picture found in the ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#ATTACHED_PICTURE} frame.
    *         If no ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#ATTACHED_PICTURE} frame is found in the ID3v2.4 tag, then null is returned.
    */
   protected AttachedPicture getV24AttachedPicture(PictureType pictureType)
   {
      AttachedPicture attachedPicture = null;
      ID3v24Frame     frame           = getV24AttachedPictureFrame(pictureType);

      if (frame != null)
      {
         ID3v24FrameBodyAttachedPicture frameBody = (ID3v24FrameBodyAttachedPicture)frame.getBody();
         attachedPicture = frameBody.getAttachedPicture();
      }
      return attachedPicture;
   }

   /**
    * finds all the ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#ATTACHED_PICTURE} frames in the ID3v2.4 tag and retrieves the attached picture for each frame.
    * @return the attached pictures found in the ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#ATTACHED_PICTURE} frames.
    *         If no ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#ATTACHED_PICTURE} frames are found in the ID3v2.4 tag, then an empty list is returned.
    */
   protected List<AttachedPicture> getV24AttachedPictures()
   {
      List<ID3v24Frame>              frames    = getV24Frames(FrameType.ATTACHED_PICTURE);
      ID3v24FrameBodyAttachedPicture frameBody = null;
      Vector<AttachedPicture>        pictures  = new Vector<AttachedPicture>();

      if (frames.size() != 0)
      {
         for(ID3v24Frame frame : frames)
         {
            frameBody = (ID3v24FrameBodyAttachedPicture)frame.getBody();
            pictures.add(frameBody.getAttachedPicture());
         }
      }
      return pictures;
   }

   /**
    * finds the ID3v2.4 attached picture frame containing the specified picture type in the ID3v2.4 tag.
    * @param pictureType   one of the 21 allowable ID3v2.4 picture types.
    * @return the ID3v2.4 attached picture frame containing the specified picture type.
    * If no ID3v2.4 attached picture frame with the specified picture type was found in the ID3v2.4 tag, then null is returned.
    */
   protected ID3v24Frame getV24AttachedPictureFrame(PictureType pictureType)
   {
      ID3v24Frame       found  = null;
      List<ID3v24Frame> frames = getV24Frames(FrameType.ATTACHED_PICTURE);

      for(ID3v24Frame frame : frames)
      {
         ID3v24FrameBodyAttachedPicture frameBody = (ID3v24FrameBodyAttachedPicture)frame.getBody();
         if (frameBody.getPictureType() == pictureType)
            found = frame;
      }
      return found;
   }

   /**
    * adds an ID3v2.4 attached picture frame with the specified picture type to the ID3v2.4 tag.
    * If the ID3v2.4 tag already contains an ID3v2.4 attached picture frame with the specified picture type, then the existing frame's attached picture is simply updated
    * with the new picture.  Otherwise, a new ID3v2.4 attached picture frame is added to the ID3v2.4 tag.
    * @param attachedPicture   an attached picture containing the information about the image to be added to the ID3V2.4 tag.
    * @return the ID3v2.4 attached picture frame that was added/updated.
    */
   protected ID3v24Frame setV24AttachedPicture(AttachedPicture attachedPicture)
   {
      ID3v24Frame                    frame     = getV24AttachedPictureFrame(attachedPicture.getPictureType());
      ID3v24FrameBodyAttachedPicture frameBody = null;

      if (frame == null)
         frame = addV24Frame(FrameType.ATTACHED_PICTURE);

      frameBody = (ID3v24FrameBodyAttachedPicture)frame.getBody();
      frameBody.setEncoding   (Encoding.UTF_16);
      frameBody.setMimeType   (attachedPicture.getMimeType());
      frameBody.setPictureType(attachedPicture.getPictureType());
      frameBody.setDescription(attachedPicture.getDescription());
      frameBody.setImage      (attachedPicture.getImage());

      return frame;
   }

   /**
    * removes the ID3v2.4 attached picture frame with the specified picture type from the ID3v2.4 tag.
    * @param pictureType  one of the 21 valid ID3v2.4 picture types.
    * @return the ID3v2.4 attached picture frame with the specified picture type that was removed from the ID3v2.4 tag.
    *         If the ID3v2.4 tag does not contain an ID3v2.4 attached picture frame with the specified picture type, then null is returned.
    */
   protected ID3v24Frame removeV24AttachedPicture(PictureType pictureType)
   {
      ID3v24Frame frame = getV24AttachedPictureFrame(pictureType);
      if (frame != null)
         id3v24Tag.getFrames().remove(frame);

      return frame;
   }

   /**
    * removes all ID3v2.4 attached picture frames from the ID3v2.4 tag.
    * @return the ID3v2.4 attached picture frame with the specified picture type that was removed from the ID3v2.4 tag.
    *         If the ID3v2.4 tag does not contain an ID3v2.4 attached picture frame with the specified picture type, then an empty list is returned.
    */
   protected List<AttachedPicture> removeV24AttachedPictures()
   {
      List<ID3v24Frame>              frames    = removeV24Frames(FrameType.ATTACHED_PICTURE);
      ID3v24FrameBodyAttachedPicture frameBody = null;
      Vector<AttachedPicture>        pictures  = new Vector<AttachedPicture>();

      if (frames.size() != 0)
      {
         for(ID3v24Frame frame : frames)
         {
            frameBody = (ID3v24FrameBodyAttachedPicture)frame.getBody();
            pictures.add(frameBody.getAttachedPicture());

         }
      }
      return pictures;
   }

   /**
    * finds the ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#LENGTH} text frame in the ID3v2.4 tag and retrieves the text field.
    * The text field is then converted to an integer and returned.
    * @return the text field found in the ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#LENGTH} text frame.
    *         If no ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#LENGTH} text frame is found in the ID3v2.4 tag, or if the text field is not a valid integer,
    *         then 0 is returned.
    */
   public int getV24AudioDuration()
   {
      return getV24TextAsInteger(FrameType.LENGTH) / 1000;
   }

   /**
    * adds an ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#LENGTH} text frame with the specified duration to the ID3v2.4 tag.
    * If the ID3v2.4 tag already contains an ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#LENGTH} text frame, then the existing frame's text is simply updated.
    * Otherwise, a new ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#LENGTH} text frame is added to the ID3v2.4 tag.
    * @param duration     the duration (in seconds) of the song in the .mp3 file.
    * @return the ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#LENGTH} text frame that was added/updated.
    * @throws IllegalArgumentException   If the duration is less than 0.
    */
   protected ID3v24Frame setV24AudioDuration(int duration) throws IllegalArgumentException
   {
      if (duration < 0)
         throw new IllegalArgumentException("Invalid audio duration, " + duration + ", specified.  It must be greater than or equal to 0.");

      return setV24Text(duration * 1000, FrameType.LENGTH);
   }

   /**
    * removes the ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#LENGTH} text frame in the ID3v2.4 tag.
    * @return the ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#LENGTH} text frame that was removed.
    *         If the ID3v2.4 tag does not contain an ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#LENGTH} text frame, then no frame is removed and null is returned.
    */
   protected ID3v24Frame removeV24AudioDuration()
   {
      return removeV24Frame(FrameType.LENGTH);
   }

   /**
    * does nothing.  Since the LENGTH frame type was removed in ID3v2.4, this method is simply here to provide compatibility with its ID3v2.3 counter part.
    */
   protected void removeV24AudioSize()
   {
      // no code necessary
   }

   /**
    * finds the ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#BAND} text frame in the ID3v2.4 tag and retrieves the text field.
    * @return the text field found in the ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#BAND} text frame.
    *         If no ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#BAND} text frame is found in the ID3v2.4 tag, then null is returned.
    */
   protected String getV24Band()
   {
      return getV24Text(FrameType.BAND);
   }

   /**
    * adds an ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#BAND} text frame with the specified band to the ID3v2.4 tag.
    * If the ID3v2.4 tag already contains an ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#BAND} text frame, then the existing frame's text is simply updated.
    * Otherwise, a new ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#BAND} text frame is added to the ID3v2.4 tag.
    * @param band    the name of the band who recorded the song.  The text is encoded using the UTF-16 character set.
    * @return the ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#BAND} text frame that was added/updated.
    */
   protected ID3v24Frame setV24Band(String band)
   {
      return setV24Text(band, FrameType.BAND);
   }

   /**
    * removes the ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#BAND} text frame in the ID3v2.4 tag.
    * @return the ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#BAND} text frame that was removed.
    *         If the ID3v2.4 tag does not contain an ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#BAND} text frame, then no frame is removed and null is returned.
    */
   protected ID3v24Frame removeV24Band()
   {
      return removeV24Frame(FrameType.BAND);
   }

   /**
    * finds the song comments written in the specified language in the ID3v2.4 tag.
    * @param language   the ISO-639-2 language code of the language the song comments are written in.
    * @return the song comments written in the specified language.
    * If no song comments written in the specified language could be found in the ID3v2.4 tag, then null is returned.
    */
   protected String getV24Comments(Language language) throws IllegalArgumentException
   {
      ID3v24Frame frame = getV24CommentsFrame(language);
      return frame == null ? null : ((ID3v24FrameBodyComments)frame.getBody()).getText();
   }

   /**
    * finds the ID3v2.4 comments frame with the specified language in the ID3v2.4 tag.
    * @param language   the ISO-639-2 language code of the language the song comments are written in.
    * @return the ID3v2.4 comments frame containing with the specified language.
    * If no ID3v2.4 comments frame written in the specified language were found in the ID3v2.4 tag, then null is returned.
    */
   protected ID3v24Frame getV24CommentsFrame(Language language)
   {
      ID3v24Frame       found  = null;
      List<ID3v24Frame> frames = getV24Frames(FrameType.COMMENTS);

      for(ID3v24Frame frame : frames)
      {
         ID3v24FrameBodyComments frameBody = (ID3v24FrameBodyComments)frame.getBody();
         if (frameBody.getLanguage() == language)
            found = frame;
      }
      return found;
   }

   /**
    * adds an ID3v2.4 comments frame with the comments written in the specified language to the ID3v2.4 tag.
    * The comments are encoded as a UTF-16 string in the ID3v2.4 comments frame.
    * If the ID3v2.4 tag already contains an ID3v2.4 comments frame with the specified language, then this frame's comments are simply updated
    * with the new one.  Otherwise, a new ID3v2.4 comments frame is added to the ID3v2.4 tag.
    * @param language   the ISO-639-2 language code of the language the song comments are written in.
    * @param comments   the comments about the song.
    * @throws IllegalArgumentException   if the comments are empty or contain only whitespace.
    * @return the ID3v2.4 comments frame that was added/updated.
    */
   protected ID3v24Frame setV24Comments(Language language, String comments) throws IllegalArgumentException
   {
      if (comments == null || comments.trim().length() == 0)
         throw new IllegalArgumentException("Invalid comments.  They can not be null or empty.");

      ID3v24Frame             frame     = getV24CommentsFrame(language);
      ID3v24FrameBodyComments frameBody = null;

      if (frame == null)
         frame = addV24Frame(FrameType.COMMENTS);

      frameBody = (ID3v24FrameBodyComments)frame.getBody();
      frameBody.setEncoding(Encoding.UTF_16);
      frameBody.setLanguage(language);
      frameBody.setText    (comments);

      return frame;
   }

   /**
    * removes the english language song comments from the ID3v2.4 tag.
    * @return the ID3v2.4 comments frame that was removed from the ID3v2.4 tag.
    *         If the ID3v2.4 tag does not an english language ID3v2.4 comments frame, then null is returned.
    */
   protected ID3v24Frame removeV24Comments() throws IllegalStateException
   {
      return removeV24Comments(Language.ENG);
   }

   /**
    * removes the ID3v2.4 song comments written in the specified language from the ID3v2.4 tag.
    * @param language   the ISO-639-2 language code of the language the song comments are written in.
    * @return the ID3v2.4 comments frame that was removed from the ID3v2.4 tag.
    *         If the ID3v2.4 tag does not contain an ID3v2.4 comments frame with the specified language, then null is returned.
    */
   protected ID3v24Frame removeV24Comments(Language language) throws IllegalArgumentException
   {
      ID3v24Frame frame = getV24CommentsFrame(language);
      if (frame != null)
         id3v24Tag.getFrames().remove(frame);

      return frame;
   }

   /**
    * finds the ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#PART_OF_A_SET} text frame in the ID3v2.4 tag and retrieves the text field.
    * The text field is then converted to an integer and returned.
    * @return the text field found in the ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#PART_OF_A_SET} text frame.
    *         If no ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#PART_OF_A_SET} text frame is found in the ID3v2.4 tag, or if the text field is not a valid integer,
    *         then 0 is returned.
    */
   protected int getV24Disc()
   {
      return getV24TextAsInteger(FrameType.PART_OF_A_SET);
   }

   /**
    * adds an ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#PART_OF_A_SET} text frame with the specified size to the ID3v2.4 tag.
    * If the ID3v2.4 tag already contains an ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#PART_OF_A_SET} text frame, then the existing frame's text is simply updated.
    * Otherwise, a new ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#PART_OF_A_SET} text frame is added to the ID3v2.4 tag.
    * @param disc     the disc number of the cd on which the song was released.  It must be >= 1.
    * @return the ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#PART_OF_A_SET} text frame that was added/updated.
    * @throws IllegalArgumentException   If the disc is less than 1.
    */
   protected ID3v24Frame setV24Disc(int disc) throws IllegalArgumentException
   {
      if (disc <= 0)
         throw new IllegalArgumentException("Invalid disc number specified, " + disc + ".  It must be greater than or equal to 1.");

      return setV24Text(disc, FrameType.PART_OF_A_SET);
   }

   /**
    * removes the ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#PART_OF_A_SET} text frame in the ID3v2.4 tag.
    * @return the ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#PART_OF_A_SET} text frame that was removed.
    *         If the ID3v2.4 tag does not contain an ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#PART_OF_A_SET} text frame, then no frame is removed and null is returned.
    */
   protected ID3v24Frame removeV24Disc()
   {
      return removeV24Frame(FrameType.PART_OF_A_SET);
   }

   /*
    * finds the ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#LEAD_PERFORMER} text frame in the ID3v2.4 tag and retrieves the text field.
    * On Windows machines, this field is called "Contributing Artist".
    * @return the text field found in the ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#LEAD_PERFORMER} text frame.
    *         If no ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#LEAD_PERFORMER} text frame is found in the ID3v2.4 tag, then null is returned.
    */
   protected String getV24LeadPerformer()
   {
      return getV24Text(FrameType.LEAD_PERFORMER);
   }

   /**
    * adds an ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#LEAD_PERFORMER} text frame with the specified lead performer to the ID3v2.4 tag.
    * If the ID3v2.4 tag already contains an ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#LEAD_PERFORMER} text frame, then the existing frame's text is simply updated.
    * Otherwise, a new ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#LEAD_PERFORMER} text frame is added to the ID3v2.4 tag.
    * On Windows machines, this field is called "Contributing Artist".
    * @param leadPerformer        the name of the lead performer on which the .mp3 song was released.  The text is encoded using the UTF-16 character set.
    * @return the ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#LEAD_PERFORMER} text frame that was added/updated.
    */
   protected ID3v24Frame setV24LeadPerformer(String leadPerformer)
   {
      return setV24Text(leadPerformer, FrameType.LEAD_PERFORMER);
   }

   /**
    * removes the ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#LEAD_PERFORMER} text frame in the ID3v2.4 tag.
    * On Windows machines, this field is called "Contributing Artist".
    * @return the ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#LEAD_PERFORMER} text frame that was removed.
    *         If the ID3v2.4 tag does not contain an ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#LEAD_PERFORMER} text frame, then no frame is removed and null is returned.
    */
   protected ID3v24Frame removeV24LeadPerformer()
   {
      return removeV24Frame(FrameType.LEAD_PERFORMER);
   }

   /**
    * finds the ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#LYRICIST} text frame in the ID3v2.4 tag and retrieves the text field.
    * @return the text field found in the ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#LYRICIST} text frame.
    *         If no ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#LYRICIST} text frame is found in the ID3v2.4 tag, then null is returned.
    */
   protected String getV24LyricsBy()
   {
      return getV24Text(FrameType.LYRICIST);
   }

   /**
    * adds an ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#LYRICIST} text frame with the specified title to the ID3v2.4 tag.
    * If the ID3v2.4 tag already contains an ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#LYRICIST} text frame, then the existing frame's text is simply updated.
    * Otherwise, a new ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#LYRICIST} text frame is added to the ID3v2.4 tag.
    * @param lyricist     the person(s) who wrote the lyrics to the song.  The text is encoded using the UTF-16 character set.
    * @return the ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#LYRICIST} text frame that was added/updated.
    */
   protected ID3v24Frame setV24LyricsBy(String lyricist)
   {
      return setV24Text(lyricist, FrameType.LYRICIST);
   }

   /**
    * removes the ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#LYRICIST} text frame in the ID3v2.4 tag.
    * @return the ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#LYRICIST} text frame that was removed.
    *         If the ID3v2.4 tag does not contain an ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#LYRICIST} text frame, then no frame is removed and null is returned.
    */
   protected ID3v24Frame removeV24LyricsBy()
   {
      return removeV24Frame(FrameType.LYRICIST);
   }

   /**
    * finds the ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#COMPOSER} text frame in the ID3v2.4 tag and retrieves the text field.
    * @return the text field found in the ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#COMPOSER} text frame.
    *         If no ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#COMPOSER} text frame is found in the ID3v2.4 tag, then null is returned.
    */
   protected String getV24MusicBy()
   {
      return getV24Text(FrameType.COMPOSER);
   }

   /**
    * adds an ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#COMPOSER} text frame with the specified title to the ID3v2.4 tag.
    * If the ID3v2.4 tag already contains an ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#COMPOSER} text frame, then the existing frame's text is simply updated.
    * Otherwise, a new ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#COMPOSER} text frame is added to the ID3v2.4 tag.
    * @param composer     the person(s) who wrote the music to the song.  The text is encoded using the UTF-16 character set.
    * @return the ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#COMPOSER} text frame that was added/updated.
    */
   protected ID3v24Frame setV24MusicBy(String composer)
   {
      return setV24Text(composer, FrameType.COMPOSER);
   }

   /**
    * removes the ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#COMPOSER} text frame in the ID3v2.4 tag.
    * @return the ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#COMPOSER} text frame that was removed.
    *         If the ID3v2.4 tag does not contain an ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#COMPOSER} text frame, then no frame is removed and null is returned.
    */
   protected ID3v24Frame removeV24MusicBy()
   {
      return removeV24Frame(FrameType.COMPOSER);
   }

   /**
    * finds the ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#CONTENT_TYPE} text frame in the ID3v2.4 tag and retrieves the text field.
    * @return the text field found in the ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#CONTENT_TYPE} text frame.
    *         If no ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#CONTENT_TYPE} text frame is found in the ID3v2.4 tag, then null is returned.
    */
   protected String getV24MusicType()
   {
      return getV24Text(FrameType.CONTENT_TYPE);
   }

   /**
    * adds an ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#CONTENT_TYPE} text frame with the specified genre to the ID3v2.4 tag.
    * If the ID3v2.4 tag already contains an ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#CONTENT_TYPE} text frame, then the existing frame's text is simply updated.
    * Otherwise, a new ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#CONTENT_TYPE} text frame is added to the ID3v2.4 tag.
    * @param genre   the song's music type.  That is, the type of music the song would be described as.  The text is encoded using the UTF-16 character set.
    * @return the ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#CONTENT_TYPE} text frame that was added/updated.
    */
   protected ID3v24Frame setV24MusicType(Genre genre)
   {
      return setV24MusicType(genre.toString());
   }

   /**
    * adds an ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#CONTENT_TYPE} text frame with the specified genre to the ID3v2.4 tag.
    * If the ID3v2.4 tag already contains an ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#CONTENT_TYPE} text frame, then the existing frame's text is simply updated.
    * Otherwise, a new ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#CONTENT_TYPE} text frame is added to the ID3v2.4 tag.
    * @param genre   the song's music type.  That is, the type of music the song would be described as.  The text is encoded using the UTF-16 character set.
    * @return the ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#CONTENT_TYPE} text frame that was added/updated.
    */
   protected ID3v24Frame setV24MusicType(String genre)
   {
      return setV24Text(genre, FrameType.CONTENT_TYPE);
   }

   /**
    * removes the ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#CONTENT_TYPE} text frame in the ID3v2.4 tag.
    * @return the ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#CONTENT_TYPE} text frame that was removed.
    *         If the ID3v2.4 tag does not contain an ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#CONTENT_TYPE} text frame, then no frame is removed and null is returned.
    */
   protected ID3v24Frame removeV24MusicType()
   {
      return removeV24Frame(FrameType.CONTENT_TYPE);
   }

   /**
    * finds the ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#PUBLISHER} text frame in the ID3v2.4 tag and retrieves the text field.
    * @return the text field found in the ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#PUBLISHER} text frame.
    *         If no ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#PUBLISHER} text frame is found in the ID3v2.4 tag, then null is returned.
    */
   protected String getV24Publisher()
   {
      return getV24Text(FrameType.PUBLISHER);
   }

   /**
    * adds an ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#PUBLISHER} text frame with the specified title to the ID3v2.4 tag.
    * If the ID3v2.4 tag already contains an ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#PUBLISHER} text frame, then the existing frame's text is simply updated.
    * Otherwise, a new ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#PUBLISHER} text frame is added to the ID3v2.4 tag.
    * @param publisher     the publisher of the song.  The text is encoded using the UTF-16 character set.
    * @return the ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#PUBLISHER} text frame that was added/updated.
    */
   protected ID3v24Frame setV24Publisher(String publisher)
   {
      return setV24Text(publisher, FrameType.PUBLISHER);
   }

   /**
    * removes the ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#PUBLISHER} text frame in the ID3v2.4 tag.
    * @return the ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#PUBLISHER} text frame that was removed.
    *         If the ID3v2.4 tag does not contain an ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#PUBLISHER} text frame, then no frame is removed and null is returned.
    */
   protected ID3v24Frame removeV24Publisher()
   {
      return removeV24Frame(FrameType.PUBLISHER);
   }

   /**
    * finds the ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#POPULARIMETER} frame in the ID3v2.4 tag and retrieves the rating field.
    * @return the rating field found in the ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#POPULARIMETER} frame.
    *         If no ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#POPULARIMETER} frame is found in the ID3v2.4 tag, then 0 is returned.
    */
   protected int getV24Rating()
   {
      ID3v24Frame frame = getV24Frame(FrameType.POPULARIMETER);

      return frame == null ? 0 : ((ID3v24FrameBodyPopularimeter)frame.getBody()).getRating();
   }

   /**
    * adds an ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#POPULARIMETER} frame with the specified rating to the ID3v2.4 tag.
    * If the ID3v2.4 tag already contains an ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#POPULARIMETER} frame, then the existing frame's rating is simply updated.
    * Otherwise, a new ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#POPULARIMETER} frame is added to the ID3v2.4 tag.
    * @param rating       the rating of the song.  The rating is 1-255 where 1 is worst and 255 is best. 0 is unknown, or unrated.
    * @return the ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#POPULARIMETER} frame that was added/updated.
    * @throws IllegalArgumentException   If the rating is less than 0 or greater than 255.
    */
   protected ID3v24Frame setV24Rating(int rating)
   {
      if (rating < ID3v24FrameBodyPopularimeter.UNKNOWN || rating > ID3v24FrameBodyPopularimeter.BEST)
         throw new IllegalArgumentException("Invalid rating, " + rating + ".  It must be between " + ID3v24FrameBodyPopularimeter.UNKNOWN + " and " + ID3v24FrameBodyPopularimeter.BEST + ".");

      ID3v24FrameBodyPopularimeter frameBody = null;
      ID3v24Frame                  frame     = getV24Frame(FrameType.POPULARIMETER);

      if (frame == null)
         frame = addV24Frame(FrameType.POPULARIMETER);

      frameBody = (ID3v24FrameBodyPopularimeter)(frame.getBody());
      frameBody.setRating(rating);

      return frame;
   }

   /**
    * removes the ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#POPULARIMETER} text frame in the ID3v2.4 tag.
    * @return the ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#POPULARIMETER} text frame that was removed.
    *         If the ID3v2.4 tag does not contain an ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#POPULARIMETER} text frame, then no frame is removed and null is returned.
    */
   protected ID3v24Frame removeV24Rating()
   {
      return removeV24Frame(FrameType.POPULARIMETER);
   }

   /**
    * finds the synchronized song lyrics in the specified language in the ID3v2.4 tag.
    * @param language   the ISO-639-2 language code of the language the song lyrics are written in.
    * @return the list of the synchronized song lyrics in the specified language.
    * If no synchronized song lyrics in the specified language were found in the ID3v2.4 tag, then null is returned.
    */
   protected List<SynchronizedLyric> getV24SynchronizedLyrics(Language language)
   {
      ID3v24Frame frame  = getV24SynchronizedLyricsFrame(language);
      return frame == null ? null : ((ID3v24FrameBodySynchronizedLyricsText)frame.getBody()).getSynchronizedLyrics();
   }

   /**
    * finds the ID3v2.4 synchronized lyrics frame containing in the specified language in the ID3v2.4 tag.
    * @param language   the ISO-639-2 language code of the language the synchronized song lyrics are written in.
    * @return the ID3v2.4 synchronized lyrics frame written in the specified language.
    * If no ID3v2.4 synchronized lyrics frame written in the specified language was found in the ID3v2.4 tag, then null is returned.
    */
   protected ID3v24Frame getV24SynchronizedLyricsFrame(Language language)
   {
      ID3v24Frame       found  = null;
      List<ID3v24Frame> frames = getV24Frames(FrameType.SYNCHRONIZED_LYRIC_TEXT);

      for(ID3v24Frame frame : frames)
      {
         ID3v24FrameBodySynchronizedLyricsText frameBody = (ID3v24FrameBodySynchronizedLyricsText)frame.getBody();
         if (frameBody.getLanguage() == language)
            found = frame;
      }
      return found;
   }

   /**
    * adds an ID3v2.4 synchronized lyrics frame with the specified english song lyrics to the ID3v2.4 tag.
    * If the ID3v2.4 tag already contains an ID3v2.4 synchronized lyrics frame with the lyrics in english, then this frame's synchronized lyrics are simply updated with the new lyrics.
    * Otherwise, a new ID3v2.4 synchronized lyrics frame is added to the ID3v2.4 tag.
    * @param synchronizedLyrics   the synchronized (english) lyrics to the song.  The lyrics are encoded using the UTF-16 character set.
    * @return the ID3v2.4 synchronized lyrics frame that was added/updated.
    */
   protected ID3v24Frame setV24SynchronizedLyrics(List<SynchronizedLyric> synchronizedLyrics)
   {
      return setV24SynchronizedLyrics(Encoding.UTF_16, Language.ENG, synchronizedLyrics);
   }

   /**
    * adds an ID3v2.4 synchronized lyrics frame with the specified song lyrics written in the given language to the ID3v2.4 tag.
    * If the ID3v2.4 tag already contains an ID3v2.4 synchronized lyrics frame with the lyrics in the specified language,
    * then the existing frame's synchronized lyrics are simply updated with the new lyrics.
    * Otherwise, a new ID3v2.4 synchronized lyrics frame is added to the ID3v2.4 tag.
    * The UTF-16 character set used to encode the lyrics.
    * @param language            the ISO-639-2 language code of the language the song lyrics are written in.
    * @param synchronizedLyrics  a list of synchronized lyrics to the song.  The lyrics must be in chronological order.
    * @return the ID3v2.4 synchronized lyrics frame that was added/updated.
    * @throws IllegalArgumentException   if the synchronized lyrics are not sorted in ascending chronological order.
    */
   protected ID3v24Frame setV24SynchronizedLyrics(Language language, List<SynchronizedLyric> synchronizedLyrics) throws IllegalArgumentException
   {
      return setV24SynchronizedLyrics(Encoding.UTF_16, language, synchronizedLyrics);
   }

   /**
    * adds an ID3v2.4 synchronized lyrics frame with the specified song lyrics written in the given language to the ID3v2.4 tag.
    * If the ID3v2.4 tag already contains an ID3v2.4 synchronized lyrics frame with the lyrics in the specified language,
    * then this frame's synchronized lyrics are simply updated with the new lyrics.
    * Otherwise, a new ID3v2.4 synchronized lyrics frame is added to the ID3v2.4 tag.
    * @param encoding            the character set used to encode the lyrics.  Only ISO 8859-1 and UTF-16 are allowed.
    * @param language            the ISO-639-2 language code of the language the song lyrics are written in.
    * @param synchronizedLyrics  a list of synchronized lyrics to the song.  The lyrics must be in chronological order.
    * @return the ID3v2.4 synchronized lyrics frame that was added/updated.
    * @throws IllegalArgumentException   if the synchronized lyrics are not sorted in ascending chronological order.
    */
   protected ID3v24Frame setV24SynchronizedLyrics(Encoding encoding, Language language, List<SynchronizedLyric> synchronizedLyrics) throws IllegalArgumentException
   {
      ID3v24Frame                           frame     = getV24SynchronizedLyricsFrame(language);
      ID3v24FrameBodySynchronizedLyricsText frameBody = null;

      if (frame == null)
         frame = addV24Frame(FrameType.SYNCHRONIZED_LYRIC_TEXT);

      frameBody = (ID3v24FrameBodySynchronizedLyricsText)frame.getBody();
      frameBody.setEncoding          (encoding);
      frameBody.setLanguage          (language);
      frameBody.setSynchronizedLyrics(synchronizedLyrics);

      return frame;
   }

   /**
    * removes the ID3v2.4 synchronized lyrics frame written in the specified language from the ID3v2.4 tag.
    * @param language   the ISO-639-2 language code of the language the synchronized song lyrics are written in.
    * @return the ID3v2.4 synchronized lyrics frame written in the specified language that was removed from the ID3v2.4 tag.
    *         If the ID3v2.4 tag does not contain an ID3v2.4 synchronized lyrics frame in the specified language, then null is returned.
    */
   protected ID3v24Frame removeV24SynchronizedLyrics(Language language)
   {
      ID3v24Frame frame = getV24SynchronizedLyricsFrame(language);
      if (frame != null)
         id3v24Tag.getFrames().remove(frame);

      return frame;
   }

   /**
    * finds the ID3v2.4 text frame with the specified ID3v2.4 frame type in the ID3v2.4 tag and retrieves the text field from the ID3v2.4 frame.
    * @param frameType   one of the ID3v2.4 text frame types.
    * @return            the text field found in the ID3v2.4 text frame specified by the ID3v2.4 frame type.
    *                    If no ID3v2.4 text frame with the specified frame type was found in the ID3v2.4 tag, then null is returned.
    */
   protected String getV24Text(FrameType frameType)
   {
      ID3v24Frame frame = getV24Frame(frameType);
      return frame == null ? null : ((ID3v24FrameBodyTextInformation)(frame.getBody())).getText();
   }

   /**
    * gets the text from a text frame and converts it to an integer.
    * @return the text of a text frame as an integer.  If no number has been specified or if the number is less <= 0, then 0 is returned.
    * @param frameType   ID3v2.4 text frame type.
    */
   protected int getV24TextAsInteger(FrameType frameType)
   {
      ID3v24Frame frame = getV24Frame(frameType);
      int         n     = 0;

      if (frame != null)
      {
         ID3v24FrameBodyTextInformation frameBody = (ID3v24FrameBodyTextInformation)frame.getBody();
         try
         {
            n = Integer.parseInt(frameBody.getText());
            // if an invalid value has been specified, simply return 0.
            if (n < 0)
               n = 0;
         }
         catch (NumberFormatException ex)
         {
            if (frameType == FrameType.TRACK_NUMBER || frameType == FrameType.PART_OF_A_SET)
            {  // track numbers and disc numbers may be specified in the format d+/d+
               String track = frameBody.getText();
               int    index = track.indexOf('/');
               if (index != -1)
               {
                  try
                  {
                     n = Integer.parseInt(track.substring(0, index));
                     if (n < 0)
                        n = 0;
                  }
                  catch (NumberFormatException nfe)
                  {
                     // if an invalid value has been specified, simply return 0.
                  }
               }
            }
         }
      }
      return n;
   }

   /**
    * adds an ID3v2.4 text frame with the specified text to the ID3v2.4 tag.
    * If the ID3v2.4 tag already contains an ID3v2.4 text frame with the specified frame type, then the existing frame's text is simply updated.
    * Otherwise, a new ID3v2.4 text frame with the specified frame type is added to the ID3v2.4 tag.
    * @param text        the text to be added to the ID3v2.4 tag.  The text is encoded using the UTF-16 character set.
    * @param frameType   the type of ID3v2.4 text frame to add.
    * @return the ID3v2.4 text frame that was added/updated.
    */
   protected ID3v24Frame setV24Text(String text, FrameType frameType)
   {
      return setV24Text(Encoding.UTF_16, text, frameType);
   }

   /**
    * adds an ID3v2.4 text frame with the string value of the specified integer to the ID3v2.4 tag.
    * If the ID3v2.4 tag already contains an ID3v2.4 text frame with the specified frame type, then the existing frame's text is simply updated.
    * Otherwise, a new ID3v2.4 text frame with the specified frame type is added to the ID3v2.4 tag.
    * The specified integer is converted to a string which is then stored in the text frame in the ID3v2.4 tag.
    * @param n           the integer whose value will be converted to a string and stored in a text frame in the ID3v2.4 tag.
    *                    The text is encoded using the ISO 8859-1 character set.
    * @param frameType   the type of ID3v2.4 text frame to add.
    * @return the ID3v2.4 text frame that was added/updated.
    * @throws IllegalArgumentException   If the number n is <= 0.
    */
   public ID3v24Frame setV24Text(int n, FrameType frameType) throws IllegalArgumentException
   {
      if (n <= 0)
         throw new IllegalArgumentException("Invalid number specified, " + n + ".  It must be greater than or equal to 1.");

      return setV24Text(Encoding.ISO_8859_1, (String.valueOf(n)), frameType);
   }

   /**
    * adds an ID3v2.4 text frame with the specified text to the ID3v2.4 tag.
    * If the ID3v2.4 tag already contains an ID3v2.4 text frame with the specified frame type, then the existing frame's text is simply updated.
    * Otherwise, a new ID3v2.4 text frame with the specified frame type is added to the ID3v2.4 tag.
    * @param encoding    the character set used to encode the text.  Only ISO 8859-1 and UTF-16 are allowed.
    * @param text        the text to be added to the ID3v2.4 tag.
    * @param frameType   the type of ID3v2.4 text frame to add.
    * @return the ID3v2.4 text frame that was added/updated.
    */
   protected ID3v24Frame setV24Text(Encoding encoding, String text, FrameType frameType)
   {
      ID3v24FrameBodyTextInformation frameBody = null;
      ID3v24Frame                    frame     = getV24Frame(frameType);

      if (frame == null)
         frame = addV24Frame(frameType);

      frameBody = (ID3v24FrameBodyTextInformation)frame.getBody();
      frameBody.setEncoding(encoding);
      frameBody.setText    (text);
      frameBody.setText    (encoding == Encoding.UTF_16 ? Utility.getUTF16String(text) : text);  // make sure UTF-16 encoded strings start with a BOM (byte order mark)

      return frame;
   }

   /**
    * finds the ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#SONG_TITLE} text frame in the ID3v2.4 tag and retrieves the text field.
    * @return the text field found in the ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#SONG_TITLE} text frame.
    *         If no ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#SONG_TITLE} text frame is found in the ID3v2.4 tag, then null is returned.
    */
   protected String getV24Title()
   {
      return getV24Text(FrameType.SONG_TITLE);
   }

   /**
    * adds an ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#SONG_TITLE} text frame with the specified title to the ID3v2.4 tag.
    * If the ID3v2.4 tag already contains an ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#SONG_TITLE} text frame, then the existing frame's text is simply updated.
    * Otherwise, a new ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#SONG_TITLE} text frame is added to the ID3v2.4 tag.
    * @param title        the name of the .mp3 song.  The text is encoded using the UTF-16 character set.
    * @return the ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#SONG_TITLE} text frame that was added/updated.
    */
   protected ID3v24Frame setV24Title(String title)
   {
      return setV24Text(title, FrameType.SONG_TITLE);
   }

   /**
    * removes the ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#SONG_TITLE} text frame in the ID3v2.4 tag.
    * @return the ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#SONG_TITLE} text frame that was removed.
    *         If the ID3v2.4 tag does not contain an ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#SONG_TITLE} text frame, then no frame is removed and null is returned.
    */
   protected ID3v24Frame removeV24Title()
   {
      return removeV24Frame(FrameType.SONG_TITLE);
   }

   /**
    * finds the ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#TRACK_NUMBER} text frame in the ID3v2.4 tag and retrieves the text field.
    * The text field is then converted to an integer and returned.
    * @return the text field found in the ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#TRACK_NUMBER} text frame.
    *         If no ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#TRACK_NUMBER} text frame is found in the ID3v2.4 tag, or if the text field is not a valid integer,
    *         then 0 is returned.
    */
   protected int getV24Track()
   {
      return getV24TextAsInteger(FrameType.TRACK_NUMBER);
   }

   /**
    * adds an ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#TRACK_NUMBER} text frame with the specified size to the ID3v2.4 tag.
    * If the ID3v2.4 tag already contains an ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#TRACK_NUMBER} text frame, then the existing frame's text is simply updated.
    * Otherwise, a new ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#TRACK_NUMBER} text frame is added to the ID3v2.4 tag.
    * @param track     the track number of the song.  It must be >= 1.
    * @return the ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#TRACK_NUMBER} text frame that was added/updated.
    * @throws IllegalArgumentException   If the track is less than 1.
    */
   protected ID3v24Frame setV24Track(int track) throws IllegalArgumentException
   {
      if (track <= 0)
         throw new IllegalArgumentException("Invalid track number specified, " + track + ".  It must be greater than or equal to 1.");

      return setV24Text(track, FrameType.TRACK_NUMBER);
   }

   /**
    * removes the ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#TRACK_NUMBER} text frame in the ID3v2.4 tag.
    * @return the ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#TRACK_NUMBER} text frame that was removed.
    *         If the ID3v2.4 tag does not contain an ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#TRACK_NUMBER} text frame, then no frame is removed and null is returned.
    */
   protected ID3v24Frame removeV24Track()
   {
      return removeV24Frame(FrameType.TRACK_NUMBER);
   }

   /**
    * finds the unsynchronized song lyrics in the specified language in the ID3v2.4 tag.
    * @param language   the ISO-639-2 language code of the language the song lyrics are written in.
    * @return the list of the unsynchronized song lyrics in the specified language.
    * If no unsynchronized song lyrics in the specified language were found in the ID3v2.4 tag, then null is returned.
    */
   protected String getV24UnsynchronizedLyrics(Language language)
   {
      ID3v24Frame frame  = getV24UnsynchronizedLyricsFrame(language);
      return frame == null ? null : ((ID3v24FrameBodyUnsynchronizedLyrics)frame.getBody()).getText();
   }

   /**
    * finds the ID3v2.4 unsynchronized lyrics frame containing in the specified language in the ID3v2.4 tag.
    * @param language   the ISO-639-2 language code of the language the unsynchronized song lyrics are written in.
    * @return the ID3v2.4 unsynchronized lyrics frame written in the specified language.
    * If no ID3v2.4 unsynchronized lyrics frame written in the specified language was found in the ID3v2.4 tag, then null is returned.
    */
   protected ID3v24Frame getV24UnsynchronizedLyricsFrame(Language language)
   {
      ID3v24Frame       found  = null;
      List<ID3v24Frame> frames = getV24Frames(FrameType.UNSYCHRONIZED_LYRICS);

      for(ID3v24Frame frame : frames)
      {
         ID3v24FrameBodyUnsynchronizedLyrics frameBody = (ID3v24FrameBodyUnsynchronizedLyrics)frame.getBody();
         if (frameBody.getLanguage() == language)
            found = frame;
      }
      return found;
   }

   /**
    * adds an ID3v2.4 unsynchronized lyrics frame with the specified song lyrics written in the given language to the ID3v2.4 tag.
    * If the ID3v2.4 tag already contains an ID3v2.4 unsynchronized lyrics frame with the lyrics in the specified language,
    * then the existing frame's unsynchronized lyrics are simply updated with the new lyrics.
    * Otherwise, a new ID3v2.4 unsynchronized lyrics frame is added to the ID3v2.4 tag.
    * The lyrics are encoded with the UTF-16 character set.
    * @param language     the ISO-639-2 language code of the language the song lyrics are written in.
    * @param lyrics       the unsynchronized lyrics to the song.
    * @return the ID3v2.4 synchronized lyrics frame that was added/updated.
    */
   protected ID3v24Frame setV24UnsynchronizedLyrics(Language language, String lyrics)
   {
      ID3v24Frame                         frame     = getV24UnsynchronizedLyricsFrame(language);
      ID3v24FrameBodyUnsynchronizedLyrics frameBody = null;

      if (frame == null)
         frame = addV24Frame(FrameType.UNSYCHRONIZED_LYRICS);

      frameBody = (ID3v24FrameBodyUnsynchronizedLyrics)frame.getBody();
      frameBody.setEncoding(Encoding.UTF_16);
      frameBody.setLanguage(language);
      frameBody.setText    (lyrics);

      return frame;
   }

   /**
    * removes the ID3v2.4 unsynchronized lyrics frame written in the specified language from the ID3v2.4 tag.
    * @param language   the ISO-639-2 language code of the language the unsynchronized song lyrics are written in.
    * @return the ID3v2.4 unsynchronized lyrics frame written in the specified language that was removed from the ID3v2.4 tag.
    *         If the ID3v2.4 tag does not contain an ID3v2.4 unsynchronized lyrics frame in the specified language, then null is returned.
    */
   protected ID3v24Frame removeV24UnsynchronizedLyrics(Language language)
   {
      ID3v24Frame frame = getV24UnsynchronizedLyricsFrame(language);
      if (frame != null)
         id3v24Tag.getFrames().remove(frame);

      return frame;
   }

   /**
    * finds the ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#RECORDING_TIME} text frame in the ID3v2.4 tag and retrieves the text field.
    * The text field is then converted to an integer and returned.
    * @return the text field found in the ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#RECORDING_TIME} text frame.
    *         If no ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#RECORDING_TIME} text frame is found in the ID3v2.4 tag, or if the text field is not a valid integer,
    *         then 0 is returned.
    */
   public int getV24Year()
   {
      return getV24TextAsInteger(FrameType.RECORDING_TIME);
   }

   /**
    * adds an ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#RECORDING_TIME} text frame with the specified year to the ID3v2.4 tag.
    * If the ID3v2.4 tag already contains an ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#RECORDING_TIME} text frame, then the existing frame's text is simply updated.
    * Otherwise, a new ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#RECORDING_TIME} text frame is added to the ID3v2.4 tag.
    * @param year     the year the song was recorded.  It must be >= 1.
    * @return the ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#RECORDING_TIME} text frame that was added/updated.
    * @throws IllegalArgumentException   If the year is less than 0.
    */
   protected ID3v24Frame setV24Year(int year) throws IllegalArgumentException
   {
      if (year <= 0)
         throw new IllegalArgumentException("Invalid year, " + year + ", specified.  It must be greater than or equal to 1.");

      return setV24Text(year, FrameType.RECORDING_TIME);
   }

   /**
    * removes the ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#RECORDING_TIME} text frame in the ID3v2.4 tag.
    * @return the ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#RECORDING_TIME} text frame that was removed.
    *         If the ID3v2.4 tag does not contain an ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#RECORDING_TIME} text frame, then no frame is removed and null is returned.
    */
   protected ID3v24Frame removeV24Year()
   {
      return removeV24Frame(FrameType.RECORDING_TIME);
   }
}

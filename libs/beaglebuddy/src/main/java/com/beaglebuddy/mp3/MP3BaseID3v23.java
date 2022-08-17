package com.beaglebuddy.mp3;

import java.util.List;
import java.util.Vector;

import com.beaglebuddy.id3.enums.Genre;
import com.beaglebuddy.id3.enums.Language;
import com.beaglebuddy.id3.enums.PictureType;
import com.beaglebuddy.id3.enums.v23.Encoding;
import com.beaglebuddy.id3.enums.v23.FrameType;
import com.beaglebuddy.id3.pojo.AttachedPicture;
import com.beaglebuddy.id3.pojo.SynchronizedLyric;
import com.beaglebuddy.id3.v23.ID3v23Frame;
import com.beaglebuddy.id3.v23.ID3v23Tag;
import com.beaglebuddy.id3.v23.frame_body.ID3v23FrameBodyAttachedPicture;
import com.beaglebuddy.id3.v23.frame_body.ID3v23FrameBodyComments;
import com.beaglebuddy.id3.v23.frame_body.ID3v23FrameBodyPopularimeter;
import com.beaglebuddy.id3.v23.frame_body.ID3v23FrameBodySynchronizedLyricsText;
import com.beaglebuddy.id3.v23.frame_body.ID3v23FrameBodyTextInformation;
import com.beaglebuddy.id3.v23.frame_body.ID3v23FrameBodyUnsynchronizedLyrics;
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
 * This base class provides some underlying methods for working with an ID3v2.3 tag.
 * </p>
 * @see MP3BaseID3v24
 */
public class MP3BaseID3v23
{
   // data members
                                      /** ID3v2.3 tag which holds all the information about an .mp3 file */
   protected ID3v23Tag id3v23Tag;



   /**
    * determines if the ID3v2.3 tag had any frame errors when it was read in.
    * @return whether any invalid frames were encountered while reading in the ID3v2.3 tag from the .mp3 file.
    */
   public boolean hasV23Errors()
   {
      return id3v23Tag.getInvalidFrames().size() != 0;
   }

   /**
    * gets a list of any frame errors encountered while reading in the ID3v2.3 tag.
    * @return a list of the frame errors that occurred while reading in the ID3v2.3 tag.
    *         If the ID3v2.3 tag was valid and did not contain any errors, then an empty list is returned.
    */
   public List<String> getV23Errors()
   {
      Vector<String>    errors        = new Vector<String>();
      List<ID3v23Frame> invalidFrames = id3v23Tag.getInvalidFrames();

      for(ID3v23Frame frame : invalidFrames)
         errors.add(frame.getInvalidMessage());

      return errors;
   }

   /**
    * gets the ID3v2.3 Tag.
    * @return the ID3v2.3 Tag.
    */
   public ID3v23Tag getID3v23Tag()
   {
      return id3v23Tag;
   }

   /**
    * add an ID3v2.3 frame of the specified type to the ID3v2.3 tag.
    * @param frameType   type of ID3v2.3 frame to add to the ID3v2.3 tag.
    * @return the new ID3v2.3 frame that was added to the ID3v2.3 tag.
    */
   public ID3v23Frame addV23Frame(FrameType frameType)
   {
      return id3v23Tag.addFrame(frameType);
   }

   /**
    * finds the first ID3v2.3 frame in the ID3v2.3 tag with the specified ID3v2.3 frame id.
    * @param frameType   type of ID3v2.3 frame to search for.
    * @return the first ID3v2.3 frame with the given ID3v2.3 frame id found in the ID3v2.3 tag, or null if no frame with the specified id can be found.
    */
   public ID3v23Frame getV23Frame(FrameType frameType)
   {
      return id3v23Tag.getFrame(frameType);
   }

   /**
    * finds all the ID3v2.3 frames in the ID3v2.3 tag with the specified ID3v2.3 frame id.
    * @param frameType   type of ID3v2.3 frames to retrieve from the ID3v2.3 tag.
    * @return a list of all the ID3v2.3 frames with the given ID3v2.3 frame id that were found in the ID3v2.3 tag,
    *         or an empty collection of size 0 if no ID3v2.3 frame with the specified ID3v2.3 id can be found.
    */
   public List<ID3v23Frame> getV23Frames(FrameType frameType)
   {
      return id3v23Tag.getFrames(frameType);
   }

   /**
    * removes the first ID3v2.3 frame with the specified ID3v2.3 frame id from the ID3v2.3 tag.
    * @param frameType   type of type ID3v2.3 frame to remove from the ID3v2.3 tag.
    * @return the ID3v2.3 frame in the ID3v2.3 tag with the given ID3v2.3 frame id that was removed, or null if no ID3v2.3 frame with the specified id was found.
    */
   public ID3v23Frame removeV23Frame(FrameType frameType)
   {
      return id3v23Tag.removeFrame(frameType);
   }

   /**
    * removes all the ID3v2.3 frames with the specified ID3v2.3 frame id from the ID3v2.3 tag.
    * @param frameType   type of ID3v2.3 frame to remove from the ID3v2.3 tag.
    * @return a list of all the ID3v2.3 frames with the given ID3v2.3 frame id that were removed from the ID3v2.3 tag,
    *         or an empty collection of size 0 if no ID3v2.3 frames with the specified ID3v2.3 id could be found.
    */
   public List<ID3v23Frame> removeV23Frames(FrameType frameType)
   {
      return id3v23Tag.removeFrames(frameType);
   }

   /**
    * finds the ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#ALBUM_TITLE} text frame in the ID3v2.3 tag and retrieves the text field.
    * @return the text field found in the ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#ALBUM_TITLE} text frame.
    *         If no ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#ALBUM_TITLE} text frame is found in the ID3v2.3 tag, then null is returned.
    */
   protected String getV23Album()
   {
      return getV23Text(FrameType.ALBUM_TITLE);
   }

   /**
    * adds an ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#ALBUM_TITLE} text frame with the specified album to the ID3v2.3 tag.
    * If the ID3v2.3 tag already contains an ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#ALBUM_TITLE} text frame, then the existing frame's text is simply updated.
    * Otherwise, a new ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#ALBUM_TITLE} text frame is added to the ID3v2.3 tag.
    * @param album        the name of the album on which the .mp3 song was released.  The text is encoded using the UTF-16 character set.
    * @return the ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#ALBUM_TITLE} text frame that was added/updated.
    */
   protected ID3v23Frame setV23Album(String album)
   {
      return setV23Text(album, FrameType.ALBUM_TITLE);
   }

   /**
    * removes the ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#ALBUM_TITLE} text frame in the ID3v2.3 tag.
    * @return the ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#ALBUM_TITLE} text frame that was removed.
    *         If the ID3v2.3 tag does not contain an ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#ALBUM_TITLE} text frame, then no frame is removed and null is returned.
    */
   protected ID3v23Frame removeV23Album()
   {
      return removeV23Frame(FrameType.ALBUM_TITLE);
   }

   /**
    * finds the ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#ATTACHED_PICTURE} frame in the ID3v2.3 tag and retrieves the attached picture.
    * @param pictureType   one of the 21 allowable ID3v2.3 picture types.
    * @return the attached picture found in the ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#ATTACHED_PICTURE} frame.
    *         If no ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#ATTACHED_PICTURE} frame is found in the ID3v2.3 tag, then null is returned.
    */
   protected AttachedPicture getV23AttachedPicture(PictureType pictureType)
   {
      AttachedPicture attachedPicture = null;
      ID3v23Frame     frame           = getV23AttachedPictureFrame(pictureType);

      if (frame != null)
      {
         ID3v23FrameBodyAttachedPicture frameBody = (ID3v23FrameBodyAttachedPicture)frame.getBody();
         attachedPicture = frameBody.getAttachedPicture();
      }
      return attachedPicture;
   }

   /**
    * finds all the ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#ATTACHED_PICTURE} frames in the ID3v2.3 tag and retrieves the attached picture for each frame.
    * @return the attached pictures found in the ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#ATTACHED_PICTURE} frames.
    *         If no ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#ATTACHED_PICTURE} frames are found in the ID3v2.3 tag, then an empty list is returned.
    */
   protected List<AttachedPicture> getV23AttachedPictures()
   {
      List<ID3v23Frame>              frames    = getV23Frames(FrameType.ATTACHED_PICTURE);
      ID3v23FrameBodyAttachedPicture frameBody = null;
      Vector<AttachedPicture>        pictures  = new Vector<AttachedPicture>();

      if (frames.size() != 0)
      {
         for(ID3v23Frame frame : frames)
         {
            frameBody = (ID3v23FrameBodyAttachedPicture)frame.getBody();
            pictures.add(frameBody.getAttachedPicture());
         }
      }
      return pictures;
   }

   /**
    * finds the ID3v2.3 attached picture frame containing the specified picture type in the ID3v2.3 tag.
    * @param pictureType   one of the 21 allowable ID3v2.3 picture types.
    * @return the ID3v2.3 attached picture frame containing the specified picture type.
    * If no ID3v2.3 attached picture frame with the specified picture type was found in the ID3v2.3 tag, then null is returned.
    */
   protected ID3v23Frame getV23AttachedPictureFrame(PictureType pictureType)
   {
      ID3v23Frame       found  = null;
      List<ID3v23Frame> frames = getV23Frames(FrameType.ATTACHED_PICTURE);

      for(ID3v23Frame frame : frames)
      {
         ID3v23FrameBodyAttachedPicture frameBody = (ID3v23FrameBodyAttachedPicture)frame.getBody();
         if (frameBody.getPictureType() == pictureType)
            found = frame;
      }
      return found;
   }

   /**
    * adds an ID3v2.3 attached picture frame with the specified picture type to the ID3v2.3 tag.
    * If the ID3v2.3 tag already contains an ID3v2.3 attached picture frame with the specified picture type, then the existing frame's attached picture is simply updated
    * with the new picture.  Otherwise, a new ID3v2.3 attached picture frame is added to the ID3v2.3 tag.
    * @param attachedPicture   an attached picture containing the information about the image to be added to the ID3V2.3 tag.
    * @return the ID3v2.3 attached picture frame that was added/updated.
    */
   protected ID3v23Frame setV23AttachedPicture(AttachedPicture attachedPicture)
   {
      ID3v23Frame                    frame     = getV23AttachedPictureFrame(attachedPicture.getPictureType());
      ID3v23FrameBodyAttachedPicture frameBody = null;

      if (frame == null)
         frame = addV23Frame(FrameType.ATTACHED_PICTURE);

      frameBody = (ID3v23FrameBodyAttachedPicture)frame.getBody();
      frameBody.setEncoding   (Encoding.UTF_16);
      frameBody.setMimeType   (attachedPicture.getMimeType());
      frameBody.setPictureType(attachedPicture.getPictureType());
      frameBody.setDescription(attachedPicture.getDescription());
      frameBody.setImage      (attachedPicture.getImage());

      return frame;
   }

   /**
    * removes the ID3v2.3 attached picture frame with the specified picture type from the ID3v2.3 tag.
    * @param pictureType  one of the 21 valid ID3v2.3 picture types.
    * @return the ID3v2.3 attached picture frame with the specified picture type that was removed from the ID3v2.3 tag.
    *         If the ID3v2.3 tag does not contain an ID3v2.3 attached picture frame with the specified picture type, then null is returned.
    */
   protected ID3v23Frame removeV23AttachedPicture(PictureType pictureType)
   {
      ID3v23Frame frame = getV23AttachedPictureFrame(pictureType);
      if (frame != null)
         id3v23Tag.getFrames().remove(frame);

      return frame;
   }

   /**
    * removes all ID3v2.3 attached picture frames from the ID3v2.3 tag.
    * @return the list of ID3v2.3 attached pictures that were removed from the ID3v2.3 tag.
    *         If the ID3v2.3 tag does not contain any ID3v2.3 attached picture frames, then an empty list is returned.
    */
   protected List<AttachedPicture> removeV23AttachedPictures()
   {
      List<ID3v23Frame>              frames    = removeV23Frames(FrameType.ATTACHED_PICTURE);
      ID3v23FrameBodyAttachedPicture frameBody = null;
      Vector<AttachedPicture>        pictures  = new Vector<AttachedPicture>();

      if (frames.size() != 0)
      {
         for(ID3v23Frame frame : frames)
         {
            frameBody = (ID3v23FrameBodyAttachedPicture)frame.getBody();
            pictures.add(frameBody.getAttachedPicture());

         }
      }
      return pictures;
   }

   /**
    * finds the ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#LENGTH} text frame in the ID3v2.3 tag and retrieves the text field.
    * The text field is then converted to an integer and returned.
    * @return the text field found in the ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#LENGTH} text frame.
    *         If no ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#LENGTH} text frame is found in the ID3v2.3 tag, or if the text field is not a valid integer,
    *         then 0 is returned.
    */
   public int getV23AudioDuration()
   {
      return getV23TextAsInteger(FrameType.LENGTH) / 1000;
   }

   /**
    * adds an ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#LENGTH} text frame with the specified duration to the ID3v2.3 tag.
    * If the ID3v2.3 tag already contains an ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#LENGTH} text frame, then the existing frame's text is simply updated.
    * Otherwise, a new ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#LENGTH} text frame is added to the ID3v2.3 tag.
    * @param duration     the duration (in seconds) of the song in the .mp3 file.
    * @return the ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#LENGTH} text frame that was added/updated.
    * @throws IllegalArgumentException   If the duration is less than 0.
    */
   protected ID3v23Frame setV23AudioDuration(int duration) throws IllegalArgumentException
   {
      if (duration < 0)
         throw new IllegalArgumentException("Invalid audio duration, " + duration + ", specified.  It must be greater than or equal to 0.");

      return setV23Text(duration * 1000, FrameType.LENGTH);
   }

   /**
    * removes the ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#LENGTH} text frame in the ID3v2.3 tag.
    * @return the ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#LENGTH} text frame that was removed.
    *         If the ID3v2.3 tag does not contain an ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#LENGTH} text frame, then no frame is removed and null is returned.
    */
   protected ID3v23Frame removeV23AudioDuration()
   {
      return removeV23Frame(FrameType.LENGTH);
   }

   /**
    * finds the ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#SIZE} text frame in the ID3v2.3 tag and retrieves the text field.
    * The text field is then converted to an integer and returned.
    * @return the text field found in the ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#SIZE} text frame.
    *         If no ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#SIZE} text frame is found in the ID3v2.3 tag, or if the text field is not a valid integer,
    *         then 0 is returned.
    */
   protected int getV23AudioSize()
   {
      return getV23TextAsInteger(FrameType.SIZE);
   }

   /**
    * adds an ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#SIZE} text frame with the specified size to the ID3v2.3 tag.
    * If the ID3v2.3 tag already contains an ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#SIZE} text frame, then the existing frame's text is simply updated.
    * Otherwise, a new ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#SIZE} text frame is added to the ID3v2.3 tag.
    * @param size     the size (in bytes) of the audio portion of the .mp3 file.
    * @return the ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#SIZE} text frame that was added/updated.
    * @throws IllegalArgumentException   If the size is less than 1.
    */
   protected ID3v23Frame setV23AudioSize(int size) throws IllegalArgumentException
   {
      if (size <= 0)
         throw new IllegalArgumentException("Invalid audio size specified, " + size + ".  It must be greater than or equal to 1.");

      return setV23Text(size, FrameType.SIZE);
   }

   /**
    * removes the ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#SIZE} text frame in the ID3v2.3 tag.
    * @return the ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#SIZE} text frame that was removed.
    *         If the ID3v2.3 tag does not contain an ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#SIZE} text frame, then no frame is removed and null is returned.
    */
   protected ID3v23Frame removeV23AudioSize()
   {
      return removeV23Frame(FrameType.SIZE);
   }

   /**
    * finds the ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#BAND} text frame in the ID3v2.3 tag and retrieves the text field.
    * @return the text field found in the ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#BAND} text frame.
    *         If no ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#BAND} text frame is found in the ID3v2.3 tag, then null is returned.
    */
   protected String getV23Band()
   {
      return getV23Text(FrameType.BAND);
   }

   /**
    * adds an ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#BAND} text frame with the specified band to the ID3v2.3 tag.
    * If the ID3v2.3 tag already contains an ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#BAND} text frame, then the existing frame's text is simply updated.
    * Otherwise, a new ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#BAND} text frame is added to the ID3v2.3 tag.
    * @param band    the name of the band who recorded the song.  The text is encoded using the UTF-16 character set.
    * @return the ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#BAND} text frame that was added/updated.
    */
   protected ID3v23Frame setV23Band(String band)
   {
      return setV23Text(band, FrameType.BAND);
   }

   /**
    * removes the ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#BAND} text frame in the ID3v2.3 tag.
    * @return the ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#BAND} text frame that was removed.
    *         If the ID3v2.3 tag does not contain an ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#BAND} text frame, then no frame is removed and null is returned.
    */
   protected ID3v23Frame removeV23Band()
   {
      return removeV23Frame(FrameType.BAND);
   }

   /**
    * finds the song comments written in the specified language in the ID3v2.3 tag.
    * @param language   the ISO-639-2 language code of the language the song comments are written in.
    * @return the song comments written in the specified language.
    * If no song comments written in the specified language could be found in the ID3v2.3 tag, then null is returned.
    */
   protected String getV23Comments(Language language) throws IllegalArgumentException
   {
      ID3v23Frame frame = getV23CommentsFrame(language);
      return frame == null ? null : ((ID3v23FrameBodyComments)frame.getBody()).getText();
   }

   /**
    * finds the ID3v2.3 comments frame with the specified language in the ID3v2.3 tag.
    * @param language   the ISO-639-2 language code of the language the song comments are written in.
    * @return the ID3v2.3 comments frame containing with the specified language.
    * If no ID3v2.3 comments frame written in the specified language were found in the ID3v2.3 tag, then null is returned.
    */
   protected ID3v23Frame getV23CommentsFrame(Language language)
   {
      ID3v23Frame         found  = null;
      List<ID3v23Frame> frames = getV23Frames(FrameType.COMMENTS);

      for(ID3v23Frame frame : frames)
      {
         ID3v23FrameBodyComments frameBody = (ID3v23FrameBodyComments)frame.getBody();
         if (frameBody.getLanguage() == language)
            found = frame;
      }
      return found;
   }

   /**
    * adds an ID3v2.3 comments frame with the comments written in the specified language to the ID3v2.3 tag.
    * The comments are encoded as a UTF-16 string in the ID3v2.3 comments frame.
    * If the ID3v2.3 tag already contains an ID3v2.3 comments frame with the specified language, then this frame's comments are simply updated
    * with the new one.  Otherwise, a new ID3v2.3 comments frame is added to the ID3v2.3 tag.
    * @param language   the ISO-639-2 language code of the language the song comments are written in.
    * @param comments   the comments about the song.
    * @throws IllegalArgumentException   if the comments are empty or contain only whitespace.
    * @return the ID3v2.3 comments frame that was added/updated.
    */
   protected ID3v23Frame setV23Comments(Language language, String comments) throws IllegalArgumentException
   {
      if (comments == null || comments.trim().length() == 0)
         throw new IllegalArgumentException("Invalid comments.  They can not be null or empty.");

      ID3v23Frame             frame     = getV23CommentsFrame(language);
      ID3v23FrameBodyComments frameBody = null;

      if (frame == null)
         frame = addV23Frame(FrameType.COMMENTS);

      frameBody = (ID3v23FrameBodyComments)frame.getBody();
      frameBody.setEncoding(Encoding.UTF_16);
      frameBody.setLanguage(language);
      frameBody.setText    (comments);

      return frame;
   }

   /**
    * removes the english language song comments from the ID3v2.3 tag.
    * @return the ID3v2.3 comments frame that was removed from the ID3v2.3 tag.
    *         If the ID3v2.3 tag does not an english language ID3v2.3 comments frame, then null is returned.
    */
   protected ID3v23Frame removeV23Comments() throws IllegalStateException
   {
      return removeV23Comments(Language.ENG);
   }

   /**
    * removes the ID3v2.3 song comments written in the specified language from the ID3v2.3 tag.
    * @param language   the ISO-639-2 language code of the language the song comments are written in.
    * @return the ID3v2.3 comments frame that was removed from the ID3v2.3 tag.
    *         If the ID3v2.3 tag does not contain an ID3v2.3 comments frame with the specified language, then null is returned.
    */
   protected ID3v23Frame removeV23Comments(Language language) throws IllegalArgumentException
   {
      ID3v23Frame frame = getV23CommentsFrame(language);
      if (frame != null)
         id3v23Tag.getFrames().remove(frame);

      return frame;
   }

   /**
    * finds the ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#PART_OF_A_SET} text frame in the ID3v2.3 tag and retrieves the text field.
    * The text field is then converted to an integer and returned.
    * @return the text field found in the ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#PART_OF_A_SET} text frame.
    *         If no ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#PART_OF_A_SET} text frame is found in the ID3v2.3 tag, or if the text field is not a valid integer,
    *         then 0 is returned.
    */
   protected int getV23Disc()
   {
      return getV23TextAsInteger(FrameType.PART_OF_A_SET);
   }

   /**
    * adds an ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#PART_OF_A_SET} text frame with the specified size to the ID3v2.3 tag.
    * If the ID3v2.3 tag already contains an ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#PART_OF_A_SET} text frame, then the existing frame's text is simply updated.
    * Otherwise, a new ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#PART_OF_A_SET} text frame is added to the ID3v2.3 tag.
    * @param disc     the disc number of the cd on which the song was released.  It must be >= 1.
    * @return the ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#PART_OF_A_SET} text frame that was added/updated.
    * @throws IllegalArgumentException   If the disc number is less than 1.
    */
   protected ID3v23Frame setV23Disc(int disc) throws IllegalArgumentException
   {
      if (disc <= 0)
         throw new IllegalArgumentException("Invalid disc number specified, " + disc + ".  It must be greater than or equal to 1.");

      return setV23Text(disc, FrameType.PART_OF_A_SET);
   }

   /**
    * removes the ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#PART_OF_A_SET} text frame in the ID3v2.3 tag.
    * @return the ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#PART_OF_A_SET} text frame that was removed.
    *         If the ID3v2.3 tag does not contain an ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#PART_OF_A_SET} text frame, then no frame is removed and null is returned.
    */
   protected ID3v23Frame removeV23Disc()
   {
      return removeV23Frame(FrameType.PART_OF_A_SET);
   }

   /*
    * finds the ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#LEAD_PERFORMER} text frame in the ID3v2.3 tag and retrieves the text field.
    * On Windows machines, this field is called "Contributing Artist".
    * @return the text field found in the ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#LEAD_PERFORMER} text frame.
    *         If no ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#LEAD_PERFORMER} text frame is found in the ID3v2.3 tag, then null is returned.
    */
   protected String getV23LeadPerformer()
   {
      return getV23Text(FrameType.LEAD_PERFORMER);
   }

   /**
    * adds an ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#LEAD_PERFORMER} text frame with the specified lead performer to the ID3v2.3 tag.
    * If the ID3v2.3 tag already contains an ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#LEAD_PERFORMER} text frame, then the existing frame's text is simply updated.
    * Otherwise, a new ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#LEAD_PERFORMER} text frame is added to the ID3v2.3 tag.
    * On Windows machines, this field is called "Contributing Artist".
    * @param leadPerformer        the name of the lead performer on which the .mp3 song was released.  The text is encoded using the UTF-16 character set.
    * @return the ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#LEAD_PERFORMER} text frame that was added/updated.
    */
   protected ID3v23Frame setV23LeadPerformer(String leadPerformer)
   {
      return setV23Text(leadPerformer, FrameType.LEAD_PERFORMER);
   }

   /**
    * removes the ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#LEAD_PERFORMER} text frame in the ID3v2.3 tag.
    * On Windows machines, this field is called "Contributing Artist".
    * @return the ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#LEAD_PERFORMER} text frame that was removed.
    *         If the ID3v2.3 tag does not contain an ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#LEAD_PERFORMER} text frame, then no frame is removed and null is returned.
    */
   protected ID3v23Frame removeV23LeadPerformer()
   {
      return removeV23Frame(FrameType.LEAD_PERFORMER);
   }

   /**
    * finds the ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#LYRICIST} text frame in the ID3v2.3 tag and retrieves the text field.
    * @return the text field found in the ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#LYRICIST} text frame.
    *         If no ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#LYRICIST} text frame is found in the ID3v2.3 tag, then null is returned.
    */
   protected String getV23LyricsBy()
   {
      return getV23Text(FrameType.LYRICIST);
   }

   /**
    * adds an ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#LYRICIST} text frame with the specified title to the ID3v2.3 tag.
    * If the ID3v2.3 tag already contains an ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#LYRICIST} text frame, then the existing frame's text is simply updated.
    * Otherwise, a new ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#LYRICIST} text frame is added to the ID3v2.3 tag.
    * @param lyricist     the person(s) who wrote the lyrics to the song.  The text is encoded using the UTF-16 character set.
    * @return the ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#LYRICIST} text frame that was added/updated.
    */
   protected ID3v23Frame setV23LyricsBy(String lyricist)
   {
      return setV23Text(lyricist, FrameType.LYRICIST);
   }

   /**
    * removes the ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#LYRICIST} text frame in the ID3v2.3 tag.
    * @return the ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#LYRICIST} text frame that was removed.
    *         If the ID3v2.3 tag does not contain an ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#LYRICIST} text frame, then no frame is removed and null is returned.
    */
   protected ID3v23Frame removeV23LyricsBy()
   {
      return removeV23Frame(FrameType.LYRICIST);
   }

   /**
    * finds the ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#COMPOSER} text frame in the ID3v2.3 tag and retrieves the text field.
    * @return the text field found in the ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#COMPOSER} text frame.
    *         If no ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#COMPOSER} text frame is found in the ID3v2.3 tag, then null is returned.
    */
   protected String getV23MusicBy()
   {
      return getV23Text(FrameType.COMPOSER);
   }

   /**
    * adds an ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#COMPOSER} text frame with the specified title to the ID3v2.3 tag.
    * If the ID3v2.3 tag already contains an ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#COMPOSER} text frame, then the existing frame's text is simply updated.
    * Otherwise, a new ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#COMPOSER} text frame is added to the ID3v2.3 tag.
    * @param composer     the person(s) who wrote the music to the song.  The text is encoded using the UTF-16 character set.
    * @return the ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#COMPOSER} text frame that was added/updated.
    */
   protected ID3v23Frame setV23MusicBy(String composer)
   {
      return setV23Text(composer, FrameType.COMPOSER);
   }

   /**
    * removes the ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#COMPOSER} text frame in the ID3v2.3 tag.
    * @return the ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#COMPOSER} text frame that was removed.
    *         If the ID3v2.3 tag does not contain an ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#COMPOSER} text frame, then no frame is removed and null is returned.
    */
   protected ID3v23Frame removeV23MusicBy()
   {
      return removeV23Frame(FrameType.COMPOSER);
   }

   /**
    * finds the ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#CONTENT_TYPE} text frame in the ID3v2.3 tag and retrieves the text field.
    * @return the text field found in the ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#CONTENT_TYPE} text frame.
    *         If no ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#CONTENT_TYPE} text frame is found in the ID3v2.3 tag, then null is returned.
    */
   protected String getV23MusicType()
   {
      return getV23Text(FrameType.CONTENT_TYPE);
   }

   /**
    * adds an ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#CONTENT_TYPE} text frame with the specified genre to the ID3v2.3 tag.
    * If the ID3v2.3 tag already contains an ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#CONTENT_TYPE} text frame, then the existing frame's text is simply updated.
    * Otherwise, a new ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#CONTENT_TYPE} text frame is added to the ID3v2.3 tag.
    * @param genre   the song's music type.  That is, the type of music the song would be described as.  The text is encoded using the UTF-16 character set.
    * @return the ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#CONTENT_TYPE} text frame that was added/updated.
    */
   protected ID3v23Frame setV23MusicType(Genre genre)
   {
      return setV23MusicType(genre.toString());
   }

   /**
    * adds an ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#CONTENT_TYPE} text frame with the specified genre to the ID3v2.3 tag.
    * If the ID3v2.3 tag already contains an ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#CONTENT_TYPE} text frame, then the existing frame's text is simply updated.
    * Otherwise, a new ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#CONTENT_TYPE} text frame is added to the ID3v2.3 tag.
    * @param genre   the song's music type.  That is, the type of music the song would be described as.  The text is encoded using the UTF-16 character set.
    * @return the ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#CONTENT_TYPE} text frame that was added/updated.
    */
   protected ID3v23Frame setV23MusicType(String genre)
   {
      return setV23Text(genre, FrameType.CONTENT_TYPE);
   }

   /**
    * removes the ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#CONTENT_TYPE} text frame in the ID3v2.3 tag.
    * @return the ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#CONTENT_TYPE} text frame that was removed.
    *         If the ID3v2.3 tag does not contain an ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#CONTENT_TYPE} text frame, then no frame is removed and null is returned.
    */
   protected ID3v23Frame removeV23MusicType()
   {
      return removeV23Frame(FrameType.CONTENT_TYPE);
   }

   /**
    * finds the ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#PUBLISHER} text frame in the ID3v2.3 tag and retrieves the text field.
    * @return the text field found in the ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#PUBLISHER} text frame.
    *         If no ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#PUBLISHER} text frame is found in the ID3v2.3 tag, then null is returned.
    */
   protected String getV23Publisher()
   {
      return getV23Text(FrameType.PUBLISHER);
   }

   /**
    * adds an ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#PUBLISHER} text frame with the specified title to the ID3v2.3 tag.
    * If the ID3v2.3 tag already contains an ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#PUBLISHER} text frame, then the existing frame's text is simply updated.
    * Otherwise, a new ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#PUBLISHER} text frame is added to the ID3v2.3 tag.
    * @param publisher     the publisher of the song.  The text is encoded using the UTF-16 character set.
    * @return the ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#PUBLISHER} text frame that was added/updated.
    */
   protected ID3v23Frame setV23Publisher(String publisher)
   {
      return setV23Text(publisher, FrameType.PUBLISHER);
   }

   /**
    * removes the ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#PUBLISHER} text frame in the ID3v2.3 tag.
    * @return the ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#PUBLISHER} text frame that was removed.
    *         If the ID3v2.3 tag does not contain an ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#PUBLISHER} text frame, then no frame is removed and null is returned.
    */
   protected ID3v23Frame removeV23Publisher()
   {
      return removeV23Frame(FrameType.PUBLISHER);
   }

   /**
    * finds the ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#POPULARIMETER} frame in the ID3v2.3 tag and retrieves the rating field.
    * @return the rating field found in the ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#POPULARIMETER} frame.
    *         If no ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#POPULARIMETER} frame is found in the ID3v2.3 tag, then 0 is returned.
    */
   protected int getV23Rating()
   {
      ID3v23Frame frame = getV23Frame(FrameType.POPULARIMETER);

      return frame == null ? 0 : ((ID3v23FrameBodyPopularimeter)frame.getBody()).getRating();
   }

   /**
    * adds an ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#POPULARIMETER} frame with the specified rating to the ID3v2.3 tag.
    * If the ID3v2.3 tag already contains an ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#POPULARIMETER} frame, then the existing frame's rating is simply updated.
    * Otherwise, a new ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#POPULARIMETER} frame is added to the ID3v2.3 tag.
    * @param rating       the rating of the song.  The rating is 1-255 where 1 is worst and 255 is best. 0 is unknown, or unrated.
    * @return the ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#POPULARIMETER} frame that was added/updated.
    * @throws IllegalArgumentException   If the rating is less than 0 or greater than 255.
    */
   protected ID3v23Frame setV23Rating(int rating)
   {
      if (rating < ID3v23FrameBodyPopularimeter.UNKNOWN || rating > ID3v23FrameBodyPopularimeter.BEST)
         throw new IllegalArgumentException("Invalid rating, " + rating + ".  It must be between " + ID3v23FrameBodyPopularimeter.UNKNOWN + " and " + ID3v23FrameBodyPopularimeter.BEST + ".");

      ID3v23FrameBodyPopularimeter frameBody = null;
      ID3v23Frame                  frame     = getV23Frame(FrameType.POPULARIMETER);

      if (frame == null)
         frame = addV23Frame(FrameType.POPULARIMETER);

      frameBody = (ID3v23FrameBodyPopularimeter)(frame.getBody());
      frameBody.setRating(rating);

      return frame;
   }

   /**
    * removes the ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#POPULARIMETER} text frame in the ID3v2.3 tag.
    * @return the ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#POPULARIMETER} text frame that was removed.
    *         If the ID3v2.3 tag does not contain an ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#POPULARIMETER} text frame, then no frame is removed and null is returned.
    */
   protected ID3v23Frame removeV23Rating()
   {
      return removeV23Frame(FrameType.POPULARIMETER);
   }

   /**
    * finds the synchronized song lyrics in the specified language in the ID3v2.3 tag.
    * @param language   the ISO-639-2 language code of the language the song lyrics are written in.
    * @return the list of the synchronized song lyrics in the specified language.
    * If no synchronized song lyrics in the specified language were found in the ID3v2.3 tag, then null is returned.
    */
   protected List<SynchronizedLyric> getV23SynchronizedLyrics(Language language)
   {
      ID3v23Frame frame  = getV23SynchronizedLyricsFrame(language);
      return frame == null ? null : ((ID3v23FrameBodySynchronizedLyricsText)frame.getBody()).getSynchronizedLyrics();
   }

   /**
    * finds the ID3v2.3 synchronized lyrics frame containing in the specified language in the ID3v2.3 tag.
    * @param language   the ISO-639-2 language code of the language the synchronized song lyrics are written in.
    * @return the ID3v2.3 synchronized lyrics frame written in the specified language.
    * If no ID3v2.3 synchronized lyrics frame written in the specified language was found in the ID3v2.3 tag, then null is returned.
    */
   protected ID3v23Frame getV23SynchronizedLyricsFrame(Language language)
   {
      ID3v23Frame       found  = null;
      List<ID3v23Frame> frames = getV23Frames(FrameType.SYNCHRONIZED_LYRIC_TEXT);

      for(ID3v23Frame frame : frames)
      {
         ID3v23FrameBodySynchronizedLyricsText frameBody = (ID3v23FrameBodySynchronizedLyricsText)frame.getBody();
         if (frameBody.getLanguage() == language)
            found = frame;
      }
      return found;
   }

   /**
    * adds an ID3v2.3 synchronized lyrics frame with the specified english song lyrics to the ID3v2.3 tag.
    * If the ID3v2.3 tag already contains an ID3v2.3 synchronized lyrics frame with the lyrics in english, then this frame's synchronized lyrics are simply updated with the new lyrics.
    * Otherwise, a new ID3v2.3 synchronized lyrics frame is added to the ID3v2.3 tag.
    * @param synchronizedLyrics   the synchronized (english) lyrics to the song.  The lyrics are encoded using the UTF-16 character set.
    * @return the ID3v2.3 synchronized lyrics frame that was added/updated.
    */
   protected ID3v23Frame setV23SynchronizedLyrics(List<SynchronizedLyric> synchronizedLyrics)
   {
      return setV23SynchronizedLyrics(Encoding.UTF_16, Language.ENG, synchronizedLyrics);
   }

   /**
    * adds an ID3v2.3 synchronized lyrics frame with the specified song lyrics written in the given language to the ID3v2.3 tag.
    * If the ID3v2.3 tag already contains an ID3v2.3 synchronized lyrics frame with the lyrics in the specified language,
    * then the existing frame's synchronized lyrics are simply updated with the new lyrics.
    * Otherwise, a new ID3v2.3 synchronized lyrics frame is added to the ID3v2.3 tag.
    * The UTF-16 character set used to encode the lyrics.
    * @param language            the ISO-639-2 language code of the language the song lyrics are written in.
    * @param synchronizedLyrics  a list of synchronized lyrics to the song.  The lyrics must be in chronological order.
    * @return the ID3v2.3 synchronized lyrics frame that was added/updated.
    * @throws IllegalArgumentException   if the synchronized lyrics are not sorted in ascending chronological order.
    */
   protected ID3v23Frame setV23SynchronizedLyrics(Language language, List<SynchronizedLyric> synchronizedLyrics) throws IllegalArgumentException
   {
      return setV23SynchronizedLyrics(Encoding.UTF_16, language, synchronizedLyrics);
   }

   /**
    * adds an ID3v2.3 synchronized lyrics frame with the specified song lyrics written in the given language to the ID3v2.3 tag.
    * If the ID3v2.3 tag already contains an ID3v2.3 synchronized lyrics frame with the lyrics in the specified language,
    * then this frame's synchronized lyrics are simply updated with the new lyrics.
    * Otherwise, a new ID3v2.3 synchronized lyrics frame is added to the ID3v2.3 tag.
    * @param encoding            the character set used to encode the lyrics.  Only ISO 8859-1 and UTF-16 are allowed.
    * @param language            the ISO-639-2 language code of the language the song lyrics are written in.
    * @param synchronizedLyrics  a list of synchronized lyrics to the song.  The lyrics must be in chronological order.
    * @return the ID3v2.3 synchronized lyrics frame that was added/updated.
    * @throws IllegalArgumentException   if the synchronized lyrics are not sorted in ascending chronological order.
    */
   protected ID3v23Frame setV23SynchronizedLyrics(Encoding encoding, Language language, List<SynchronizedLyric> synchronizedLyrics) throws IllegalArgumentException
   {
      ID3v23Frame                           frame     = getV23SynchronizedLyricsFrame(language);
      ID3v23FrameBodySynchronizedLyricsText frameBody = null;

      if (frame == null)
         frame = addV23Frame(FrameType.SYNCHRONIZED_LYRIC_TEXT);

      frameBody = (ID3v23FrameBodySynchronizedLyricsText)frame.getBody();
      frameBody.setEncoding          (encoding);
      frameBody.setLanguage          (language);
      frameBody.setSynchronizedLyrics(synchronizedLyrics);

      return frame;
   }

   /**
    * removes the ID3v2.3 synchronized lyrics frame written in the specified language from the ID3v2.3 tag.
    * @param language   the ISO-639-2 language code of the language the synchronized song lyrics are written in.
    * @return the ID3v2.3 synchronized lyrics frame written in the specified language that was removed from the ID3v2.3 tag.
    *         If the ID3v2.3 tag does not contain an ID3v2.3 synchronized lyrics frame in the specified language, then null is returned.
    */
   protected ID3v23Frame removeV23SynchronizedLyrics(Language language)
   {
      ID3v23Frame frame = getV23SynchronizedLyricsFrame(language);
      if (frame != null)
         id3v23Tag.getFrames().remove(frame);

      return frame;
   }

   /**
    * finds the ID3v2.3 text frame with the specified ID3v2.3 frame type in the ID3v2.3 tag and retrieves the text field from the ID3v2.3 frame.
    * @param frameType   one of the ID3v2.3 text frame types.
    * @return            the text field found in the ID3v2.3 text frame specified by the ID3v2.3 frame type.
    *                    If no ID3v2.3 text frame with the specified frame type was found in the ID3v2.3 tag, then null is returned.
    */
   protected String getV23Text(FrameType frameType)
   {
      ID3v23Frame frame = getV23Frame(frameType);
      return frame == null ? null : ((ID3v23FrameBodyTextInformation)(frame.getBody())).getText();
   }

   /**
    * gets the text from a text frame and converts it to an integer.
    * @return the text of a text frame as an integer.  If no number has been specified or if the number is less <= 0, then 0 is returned.
    * @param frameType   ID3v2.3 text frame type.
    */
   protected int getV23TextAsInteger(FrameType frameType)
   {
      ID3v23Frame frame = getV23Frame(frameType);
      int         n     = 0;

      if (frame != null)
      {
         ID3v23FrameBodyTextInformation frameBody = (ID3v23FrameBodyTextInformation)frame.getBody();
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
    * adds an ID3v2.3 text frame with the specified text to the ID3v2.3 tag.
    * If the ID3v2.3 tag already contains an ID3v2.3 text frame with the specified frame type, then the existing frame's text is simply updated.
    * Otherwise, a new ID3v2.3 text frame with the specified frame type is added to the ID3v2.3 tag.
    * @param text        the text to be added to the ID3v2.3 tag.  The text is encoded using the UTF-16 character set.
    * @param frameType   the type of ID3v2.3 text frame to add.
    * @return the ID3v2.3 text frame that was added/updated.
    */
   protected ID3v23Frame setV23Text(String text, FrameType frameType)
   {
      return setV23Text(Encoding.UTF_16, text, frameType);
   }

   /**
    * adds an ID3v2.3 text frame with the string value of the specified integer to the ID3v2.3 tag.
    * If the ID3v2.3 tag already contains an ID3v2.3 text frame with the specified frame type, then the existing frame's text is simply updated.
    * Otherwise, a new ID3v2.3 text frame with the specified frame type is added to the ID3v2.3 tag.
    * The specified integer is converted to a string which is then stored in the text frame in the ID3v2.3 tag.
    * @param n           the integer whose value will be converted to a string and stored in a text frame in the ID3v2.3 tag.
    *                    The text is encoded using the ISO 8859-1 character set.
    * @param frameType   the type of ID3v2.3 text frame to add.
    * @return the ID3v2.3 text frame that was added/updated.
    * @throws IllegalArgumentException   If the number n is <= 0.
    */
   public ID3v23Frame setV23Text(int n, FrameType frameType) throws IllegalArgumentException
   {
      if (n <= 0)
         throw new IllegalArgumentException("Invalid number specified, " + n + ".  It must be greater than or equal to 1.");

      return setV23Text(Encoding.ISO_8859_1, (String.valueOf(n)), frameType);
   }

   /**
    * adds an ID3v2.3 text frame with the specified text to the ID3v2.3 tag.
    * If the ID3v2.3 tag already contains an ID3v2.3 text frame with the specified frame type, then the existing frame's text is simply updated.
    * Otherwise, a new ID3v2.3 text frame with the specified frame type is added to the ID3v2.3 tag.
    * @param encoding    the character set used to encode the text.  Only ISO 8859-1 and UTF-16 are allowed.
    * @param text        the text to be added to the ID3v2.3 tag.
    * @param frameType   the type of ID3v2.3 text frame to add.
    * @return the ID3v2.3 text frame that was added/updated.
    */
   protected ID3v23Frame setV23Text(Encoding encoding, String text, FrameType frameType)
   {
      ID3v23FrameBodyTextInformation frameBody = null;
      ID3v23Frame                    frame     = getV23Frame(frameType);

      if (frame == null)
         frame = addV23Frame(frameType);

      frameBody = (ID3v23FrameBodyTextInformation)frame.getBody();
      frameBody.setEncoding(encoding);
      frameBody.setText    (encoding == Encoding.UTF_16 ? Utility.getUTF16String(text) : text);  // make sure UTF-16 encoded strings start with a BOM (byte order mark)

      return frame;
   }

   /**
    * finds the ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#SONG_TITLE} text frame in the ID3v2.3 tag and retrieves the text field.
    * @return the text field found in the ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#SONG_TITLE} text frame.
    *         If no ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#SONG_TITLE} text frame is found in the ID3v2.3 tag, then null is returned.
    */
   protected String getV23Title()
   {
      return getV23Text(FrameType.SONG_TITLE);
   }

   /**
    * adds an ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#SONG_TITLE} text frame with the specified title to the ID3v2.3 tag.
    * If the ID3v2.3 tag already contains an ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#SONG_TITLE} text frame, then the existing frame's text is simply updated.
    * Otherwise, a new ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#SONG_TITLE} text frame is added to the ID3v2.3 tag.
    * @param title        the name of the .mp3 song.  The text is encoded using the UTF-16 character set.
    * @return the ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#SONG_TITLE} text frame that was added/updated.
    */
   protected ID3v23Frame setV23Title(String title)
   {
      return setV23Text(title, FrameType.SONG_TITLE);
   }

   /**
    * removes the ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#SONG_TITLE} text frame in the ID3v2.3 tag.
    * @return the ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#SONG_TITLE} text frame that was removed.
    *         If the ID3v2.3 tag does not contain an ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#SONG_TITLE} text frame, then no frame is removed and null is returned.
    */
   protected ID3v23Frame removeV23Title()
   {
      return removeV23Frame(FrameType.SONG_TITLE);
   }

   /**
    * finds the ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#TRACK_NUMBER} text frame in the ID3v2.3 tag and retrieves the text field.
    * The text field is then converted to an integer and returned.
    * @return the text field found in the ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#TRACK_NUMBER} text frame.
    *         If no ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#TRACK_NUMBER} text frame is found in the ID3v2.3 tag, or if the text field is not a valid integer,
    *         then 0 is returned.
    */
   protected int getV23Track()
   {
      return getV23TextAsInteger(FrameType.TRACK_NUMBER);
   }

   /**
    * adds an ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#TRACK_NUMBER} text frame with the specified size to the ID3v2.3 tag.
    * If the ID3v2.3 tag already contains an ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#TRACK_NUMBER} text frame, then the existing frame's text is simply updated.
    * Otherwise, a new ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#TRACK_NUMBER} text frame is added to the ID3v2.3 tag.
    * @param track     the track number of the song.  It must be >= 1.
    * @return the ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#TRACK_NUMBER} text frame that was added/updated.
    * @throws IllegalArgumentException   If the track is less than 1.
    */
   protected ID3v23Frame setV23Track(int track) throws IllegalArgumentException
   {
      if (track <= 0)
         throw new IllegalArgumentException("Invalid track number specified, " + track + ".  It must be greater than or equal to 1.");

      return setV23Text(track, FrameType.TRACK_NUMBER);
   }

   /**
    * removes the ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#TRACK_NUMBER} text frame in the ID3v2.3 tag.
    * @return the ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#TRACK_NUMBER} text frame that was removed.
    *         If the ID3v2.3 tag does not contain an ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#TRACK_NUMBER} text frame, then no frame is removed and null is returned.
    */
   protected ID3v23Frame removeV23Track()
   {
      return removeV23Frame(FrameType.TRACK_NUMBER);
   }

   /**
    * finds the unsynchronized song lyrics in the specified language in the ID3v2.3 tag.
    * @param language   the ISO-639-2 language code of the language the song lyrics are written in.
    * @return the list of the unsynchronized song lyrics in the specified language.
    * If no unsynchronized song lyrics in the specified language were found in the ID3v2.3 tag, then null is returned.
    */
   protected String getV23UnsynchronizedLyrics(Language language)
   {
      ID3v23Frame frame  = getV23UnsynchronizedLyricsFrame(language);
      return frame == null ? null : ((ID3v23FrameBodyUnsynchronizedLyrics)frame.getBody()).getText();
   }

   /**
    * finds the ID3v2.3 unsynchronized lyrics frame containing in the specified language in the ID3v2.3 tag.
    * @param language   the ISO-639-2 language code of the language the unsynchronized song lyrics are written in.
    * @return the ID3v2.3 unsynchronized lyrics frame written in the specified language.
    * If no ID3v2.3 unsynchronized lyrics frame written in the specified language was found in the ID3v2.3 tag, then null is returned.
    */
   protected ID3v23Frame getV23UnsynchronizedLyricsFrame(Language language)
   {
      ID3v23Frame       found  = null;
      List<ID3v23Frame> frames = getV23Frames(FrameType.UNSYCHRONIZED_LYRICS);

      for(ID3v23Frame frame : frames)
      {
         ID3v23FrameBodyUnsynchronizedLyrics frameBody = (ID3v23FrameBodyUnsynchronizedLyrics)frame.getBody();
         if (frameBody.getLanguage() == language)
            found = frame;
      }
      return found;
   }

   /**
    * adds an ID3v2.3 unsynchronized lyrics frame with the specified song lyrics written in the given language to the ID3v2.3 tag.
    * If the ID3v2.3 tag already contains an ID3v2.3 unsynchronized lyrics frame with the lyrics in the specified language,
    * then the existing frame's unsynchronized lyrics are simply updated with the new lyrics.
    * Otherwise, a new ID3v2.3 unsynchronized lyrics frame is added to the ID3v2.3 tag.
    * The lyrics are encoded with the UTF-16 character set.
    * @param language     the ISO-639-2 language code of the language the song lyrics are written in.
    * @param lyrics       the unsynchronized lyrics to the song.
    * @return the ID3v2.3 synchronized lyrics frame that was added/updated.
    */
   protected ID3v23Frame setV23UnsynchronizedLyrics(Language language, String lyrics)
   {
      ID3v23Frame                         frame     = getV23UnsynchronizedLyricsFrame(language);
      ID3v23FrameBodyUnsynchronizedLyrics frameBody = null;

      if (frame == null)
         frame = addV23Frame(FrameType.UNSYCHRONIZED_LYRICS);

      frameBody = (ID3v23FrameBodyUnsynchronizedLyrics)frame.getBody();
      frameBody.setEncoding(Encoding.UTF_16);
      frameBody.setLanguage(language);
      frameBody.setText    (lyrics);

      return frame;
   }

   /**
    * removes the ID3v2.3 unsynchronized lyrics frame written in the specified language from the ID3v2.3 tag.
    * @param language   the ISO-639-2 language code of the language the unsynchronized song lyrics are written in.
    * @return the ID3v2.3 unsynchronized lyrics frame written in the specified language that was removed from the ID3v2.3 tag.
    *         If the ID3v2.3 tag does not contain an ID3v2.3 unsynchronized lyrics frame in the specified language, then null is returned.
    */
   protected ID3v23Frame removeV23UnsynchronizedLyrics(Language language)
   {
      ID3v23Frame frame = getV23UnsynchronizedLyricsFrame(language);
      if (frame != null)
         id3v23Tag.getFrames().remove(frame);

      return frame;
   }

   /**
    * finds the ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#YEAR} text frame in the ID3v2.3 tag and retrieves the text field.
    * The text field is then converted to an integer and returned.
    * @return the text field found in the ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#YEAR} text frame.
    *         If no ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#YEAR} text frame is found in the ID3v2.3 tag, or if the text field is not a valid integer,
    *         then 0 is returned.
    */
   public int getV23Year()
   {
      return getV23TextAsInteger(FrameType.YEAR);
   }

   /**
    * adds an ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#YEAR} text frame with the specified year to the ID3v2.3 tag.
    * If the ID3v2.3 tag already contains an ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#YEAR} text frame, then the existing frame's text is simply updated.
    * Otherwise, a new ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#YEAR} text frame is added to the ID3v2.3 tag.
    * @param year     the year the song was recorded.  It must be >= 1.
    * @return the ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#YEAR} text frame that was added/updated.
    * @throws IllegalArgumentException   If the year is less than 0.
    */
   protected ID3v23Frame setV23Year(int year) throws IllegalArgumentException
   {
      if (year <= 0)
         throw new IllegalArgumentException("Invalid year, " + year + ", specified.  It must be greater than or equal to 1.");

      return setV23Text(year, FrameType.YEAR);
   }

   /**
    * removes the ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#YEAR} text frame in the ID3v2.3 tag.
    * @return the ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#YEAR} text frame that was removed.
    *         If the ID3v2.3 tag does not contain an ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#YEAR} text frame, then no frame is removed and null is returned.
    */
   protected ID3v23Frame removeV23Year()
   {
      return removeV23Frame(FrameType.YEAR);
   }
}

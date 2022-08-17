package com.beaglebuddy.id3.pojo;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Hashtable;
import com.beaglebuddy.id3.enums.PictureType;



/**
 * <table class="logos_width">
 *    <tbody>
 *       <tr>
 *          <td                               ><img src="../../../../resources/id3v2.gif"                     width="56"  height="54"  alt="ID3 logo"        /></td>
 *          <td class="logos_horz_align_right"><img src="../../../../resources/beaglebuddy_software_logo.gif" width="340" height="110" alt="Beaglebuddy logo"/></td>
 *       </tr>
 *    </tbody>
 * </table>
 * This class holds information about an attached picture.
 * It contains the following fields:
 * <p class="beaglebuddy">
 * <table class="beaglebuddy">
 *    <caption><b>Attached Picture Fields</b></caption>
 *    <thead>
 *       <tr><th class="beaglebuddy">&nbsp;</th><th class="beaglebuddy">Field</th><th class="beaglebuddy">Description</th></tr>
 *    </thead>
 *    <tbody>
 *       <tr><td class="beaglebuddy">1. </td><td class="beaglebuddy">{@link com.beaglebuddy.id3.enums.PictureType}                           </td><td class="beaglebuddy">type of picture the image represents.</td></tr>
 *       <tr><td class="beaglebuddy">2. </td><td class="beaglebuddy"><a href="http://www.iana.org/assignments/media-types/image">mimeType</a></td><td class="beaglebuddy">the mime type of the image.          </td></tr>
 *       <tr><td class="beaglebuddy">3. </td><td class="beaglebuddy">description                                                             </td><td class="beaglebuddy">a description of the image.          </td></tr>
 *       <tr><td class="beaglebuddy">4. </td><td class="beaglebuddy">image                                                                   </td><td class="beaglebuddy">the raw bytes of the image.          </td></tr>
 *    </tbody>
 * </table>
 * </p>
 * @see com.beaglebuddy.id3.v23.frame_body.ID3v23FrameBodyAttachedPicture
 * @see com.beaglebuddy.id3.v24.frame_body.ID3v24FrameBodyAttachedPicture
 */
public class AttachedPicture
{
   // class members
   private static Hashtable<String, String> mimeTypes;         // types of image mime types supported by the mp3 class

   // data members
   private PictureType pictureType;    // type of picture the image represents.
   private String      mimeType;       // mime type of the image (ex: image/jpg, image/gif, etc.)
   private String      description;    // description of the image
   private byte[]      image;          // raw bytes of the image file

   static
   {
      mimeTypes = new Hashtable<String, String>();
      mimeTypes.put("jpg", "image/jpg");
      mimeTypes.put("gif", "image/gif");
      mimeTypes.put("png", "image/png");
      mimeTypes.put("bmp", "image/bmp");
   }


   /**
    * constructor.
    * <br/><br/><b>Example:</b><code><pre class="beaglebuddy">
    *     import com.beaglebuddy.id3.enums.PictureType;
    *
    *     AttachedPicture attachedPicture = new AttachedPicture(PictureType.BAND_LOGO, "/images/rush/band_logo.jpg");</pre></code>
    * @param pictureType    {@link com.beaglebuddy.id3.enums.PictureType type} of picture the image represents.
    * @param imageFile      path to the image file on your hard drive.
    * @throws IOException   if the image file can not be read.
    */
   public AttachedPicture(PictureType pictureType, String imageFile) throws IOException
   {
      this(pictureType, new File(imageFile));
   }

   /**
    * constructor.
    * <br/><br/><b>Example:</b><code><pre class="beaglebuddy">
    *     import java.io.File;
    *     import com.beaglebuddy.id3.enums.PictureType;
    *
    *     AttachedPicture attachedPicture = new AttachedPicture(PictureType.FRONT_COVER, new File("/images/ac dc/back in black/front_cover.jpg"));</pre></code>
    * @param pictureType    {@link com.beaglebuddy.id3.enums.PictureType type} of picture the image represents.
    * @param imageFile      file containing the image you want to store in the .mp3 file.
    * @throws IOException   if the image file can not be read.
    */
   public AttachedPicture(PictureType pictureType, File imageFile) throws IOException
   {
      this(pictureType, getMimeType(imageFile.getPath()), pictureType.getDescription(), imageFile);
   }

   /**
    * constructor.
    * <br/><br/><b>Example:</b><code><pre class="beaglebuddy">
    *     import com.beaglebuddy.id3.enums.PictureType;
    *
    *     AttachedPicture attachedPicture = new AttachedPicture(PictureType.BACK_COVER, "image/gif", "back cover of cd", "/images/valentino/cd_back-cover.gif");</pre></code>
    * @param pictureType    {@link com.beaglebuddy.id3.enums.PictureType type} of picture the image represents.
    * @param mimeType       <a href="http://www.iana.org/assignments/media-types/image">mime type</a> of the image (ex: image/jpg, image/gif, etc.)
    * @param description    description of the image.
    * @param imageFile      path to the image file on your hard drive.
    * @throws IOException   if the image file can not be read.
    */
   public AttachedPicture(PictureType pictureType, String mimeType, String description, String imageFile) throws IOException
   {
      this(pictureType, mimeType, description, new File(imageFile));
   }

   /**
    * constructor.
    * <br/><br/><b>Example:</b><code><pre class="beaglebuddy">
    *     import java.io.File;
    *     import com.beaglebuddy.id3.enums.PictureType;
    *
    *     AttachedPicture attachedPicture = new AttachedPicture(PictureType.BAND, "image/png", "photo of the band", new File("/images/guns_n_roses.band_photo.png"));</pre></code>
    * @param pictureType    {@link com.beaglebuddy.id3.enums.PictureType type} of picture the image represents.
    * @param mimeType       <a href="http://www.iana.org/assignments/media-types/image">mime type</a> of the image (ex: image/jpg, image/gif, etc.)
    * @param description    description of the image.
    * @param imageFile      file containing the image you want to store in the .mp3 file.
    * @throws IOException   if the image file can not be read.
    */
   public AttachedPicture(PictureType pictureType, String mimeType, String description, File imageFile) throws IOException
   {
      if (!imageFile.exists() || !imageFile.isFile())
         throw new IllegalArgumentException(imageFile.getPath() + " does not refer to a valid image file.");

      byte[] image = new byte[(int)imageFile.length()];
      FileInputStream file = new FileInputStream(imageFile);
      try
      {
         if (file.read(image) != image.length)
            throw new IOException("unable to load the image file at " + imageFile.getPath());

         setPictureType(pictureType);
         setMimeType   (mimeType);
         setDescription(description);
         setImage      (image);
      }
      finally
      {
         try {file.close(); } catch (IOException ex) {/* nothing we can */}
      }
   }

   /**
    * constructor.
    * <br/><br/><b>Example:</b><code><pre class="beaglebuddy">
    *     import java.net.URL;
    *     import com.beaglebuddy.id3.enums.PictureType;
    *
    *     AttachedPicture attachedPicture = new AttachedPicture(PictureType.DURING_RECORDING, new URL("http://www.beaglebuddy.com/images/vinnie%20moore.in%20the%20studio.jpg"));</pre></code>
    * @param pictureType    {@link com.beaglebuddy.id3.enums.PictureType type} of picture the image represents.
    * @param imageURL       url encoded URL of a file containing the image you want to store in the .mp3 file.  See the {@link com.beaglebuddy.mp3.MP3#MP3(URL) MP3(java.net.URL) constructor}
    *                       for a description of the format required to specify a URL.
    * @throws IOException   if the image file can not be read.
    */
   public AttachedPicture(PictureType pictureType, URL imageURL) throws IOException
   {
      this(pictureType, getMimeType(imageURL.toExternalForm()), pictureType.getDescription(), imageURL);
   }

   /**
    * constructor.
    * <br/><br/><b>Example:</b><code><pre class="beaglebuddy">
    *     import java.net.URL;
    *     import com.beaglebuddy.id3.enums.PictureType;
    *
    *     AttachedPicture attachedPicture = new AttachedPicture(PictureType.DURING_PERFORMANCE, "image/gif", "live in japan", new URL("http://www.beaglebuddy.com/images/savatage.live%20in%20concert.jpg"));</pre></code>
    * @param pictureType    {@link com.beaglebuddy.id3.enums.PictureType type} of picture the image represents.
    * @param mimeType       <a href="http://www.iana.org/assignments/media-types/image">mime type</a> of the image (ex: image/jpg, image/gif, etc.)
    * @param description    description of the image.
    * @param imageURL       url encoded URL of a file containing the image you want to store in the .mp3 file.  See the {@link com.beaglebuddy.mp3.MP3#MP3(URL) MP3(java.net.URL) constructor}
    *                       for a description of the format required to specify a URL.
    * @throws IOException   if the image file can not be read.
    */
   public AttachedPicture(PictureType pictureType, String mimeType, String description, URL imageURL) throws IOException
   {
      URLConnection connection  = imageURL.openConnection();
      connection.connect();
      InputStream   inputStream = imageURL.openStream();
      int           imageSize   = connection.getContentLength();
      byte[]        image       = new byte[imageSize];

      try
      {
         if (inputStream.read(image) != image.length)
            throw new IOException("unable to load the image file from " + imageURL.toExternalForm());

         setPictureType(pictureType);
         setMimeType   (mimeType);
         setDescription(description);
         setImage      (image);
      }
      finally
      {
         if (inputStream != null)
            try {inputStream.close();} catch (Exception ex) { /* nothing can be done */ }
      }
   }

   /**
    * constructor.
    * @param pictureType    {@link com.beaglebuddy.id3.enums.PictureType type} of picture the image represents.
    * @param mimeType       <a href="http://www.iana.org/assignments/media-types/image">mime type</a> of the image (ex: image/jpg, image/gif, etc.)
    * @param description    description of the image.
    * @param image          raw bytes of the image file.
    */
   public AttachedPicture(PictureType pictureType, String mimeType, String description, byte[] image)
   {
      setPictureType(pictureType);
      setMimeType   (mimeType);
      setDescription(description);
      setImage      (image);
   }

   /**
    * try to deduce the image file's mime type from its extension.
    * @param imageFile   file containing the raw bytes of the image.
    */
   private static String getMimeType(String imageFile)
   {
      String extension = imageFile.substring(imageFile.lastIndexOf('.')+1);

      return mimeTypes.get(extension) == null ? "image/" : mimeTypes.get(extension);
   }

   /**
    * gets the type of picture the image represents.
    * @return type of picture the image represents.
    * @see #setPictureType(PictureType)
    */
   public PictureType getPictureType()
   {
      return pictureType;
   }

   /**
    * sets the type of picture the image represents.
    * @param pictureType   type of picture the image represents.
    * @see #getPictureType()
    */
   public void setPictureType(PictureType pictureType)
   {
      if (pictureType == null)
         throw new IllegalArgumentException("The picture type may not be empty.");

      this.pictureType = pictureType;
   }

   /**
    * gets the mime type of the image.
    * @return the <a href="http://www.iana.org/assignments/media-types/image">mime type</a> of the image.
    * @see #setMimeType(String)
    */
   public String getMimeType()
   {
      return mimeType;
   }

   /**
    * sets the mime type of the image.
    * @param mimeType   <a href="http://www.iana.org/assignments/media-types/image">mime type</a> of the image (ex: image/jpg, image/gif, etc.)
    * @see #getMimeType()
    */
   public void setMimeType(String mimeType)
   {
      if (mimeType == null || !mimeType.trim().startsWith("image"))
         throw new IllegalArgumentException("Invalid mime type, " + mimeType + ".  It must be a valid image mime type such as \"image\", \"image/jpg\", \"image/gif\", etc.");

      this.mimeType = mimeType.trim();
   }

   /**
    * gets the description of the image.
    * @return the description of the image.
    * @see #setDescription(String)
    */
   public String getDescription()
   {
      return description;
   }

   /**
    * sets the description of the image.
    * @param description   description of the image.
    * @see #getDescription()
    */
   public void setDescription(String description)
   {
      this.description = description.trim();
   }

   /**
    * gets the raw bytes of the image.
    * @return an array containing the raw bytes of the image.
    * @see #setImage(byte[])
    */
   public byte[] getImage()
   {
      return image;
   }

   /**
    * sets the raw bytes of the image.
    * @param image   array containing the raw bytes of the image.
    * @see #getImage()
    */
   public void setImage(byte[] image)
   {
      if (image == null || image.length == 0)
         throw new IllegalArgumentException("The raw bytes of the image may not be empty.");

      this.image = image;
   }

   /**
    * get a string representation of an attached picture.
    * @return a string representation of an attached picture.
    */
   @Override
   public String toString()
   {
      StringBuffer buffer = new StringBuffer ();
      buffer.append("picture type: " + pictureType  + "\n");
      buffer.append("mime type...: " + mimeType     + "\n");
      buffer.append("description.: " + description  + "\n");
      buffer.append("image.......: " + image.length + " bytes");

      return buffer.toString();
   }
}
